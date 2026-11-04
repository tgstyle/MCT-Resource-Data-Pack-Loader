package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

public final class OwnerTable extends SavedData {
    private static final String NAME = "rdpl_owners";
    private static final String FTB_MIRROR_PREFIX = "ftbteams-";
    private final Map<Integer, OwnerRecord> records = new LinkedHashMap<>();
    private final Map<String, Integer> liveByKey = new HashMap<>();
    private CompoundTag sweep = new CompoundTag();
    private int next = 1;
    private int epoch;

    public static OwnerTable get(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(OwnerTable::read, OwnerTable::new, NAME); }

    public static int handleForTeam(MinecraftServer server, String name) {
        if (name.isEmpty() || name.startsWith(FTB_MIRROR_PREFIX)) { return 0; }
        return get(server).liveHandle(OwnerRecord.KIND_TEAM, OwnerRecord.SOURCE_SCOREBOARD, name, name);
    }

    public static int handleForSource(MinecraftServer server, String source, String key, String label) { return key.isEmpty() ? 0 : get(server).liveHandle(OwnerRecord.KIND_TEAM, source, key, label); }

    public static int handleForPlayer(MinecraftServer server, UUID uuid, String name) { return get(server).liveHandle(OwnerRecord.KIND_PLAYER, OwnerRecord.SOURCE_SCOREBOARD, uuid.toString(), name); }

    public static int handleOf(ServerPlayer player, OwnershipSettings.Who who) {
        String name = player.getGameProfile().getName();
        if (who == OwnershipSettings.Who.PLAYER) { return handleForPlayer(player.server, player.getUUID(), name); }
        int team = ScoreboardOwnerSource.current().teamOf(player);
        if (team != 0 || who == OwnershipSettings.Who.TEAM) { return team; }
        return handleForPlayer(player.server, player.getUUID(), name);
    }

    public boolean isTeam(int handle) {
        OwnerRecord record = records.get(resolve(handle));
        return record != null && record.kind.equals(OwnerRecord.KIND_TEAM);
    }

    public String keyOf(int handle) {
        OwnerRecord record = records.get(resolve(handle));
        return record == null ? "" : record.key;
    }

    @Nullable private Integer live(String kind, String source, String key) { return liveByKey.get(OwnerRecord.lookupKey(kind, source, key)); }

    public int liveTeamHandle(String source, String key) {
        Integer found = live(OwnerRecord.KIND_TEAM, source, key);
        return found == null ? 0 : found;
    }

    public String sourceOf(int handle) {
        OwnerRecord record = records.get(handle);
        return record == null ? "" : record.source;
    }

    private int liveHandle(String kind, String source, String key, String label) {
        Integer found = live(kind, source, key);
        if (found != null) {
            OwnerRecord record = records.get(found);
            if (!record.label.equals(label)) {
                record.label = label;
                setDirty();
            }
            return found;
        }
        OwnerRecord record = new OwnerRecord(next++, kind, source, key, label);
        records.put(record.h, record);
        liveByKey.put(record.lookupKey(), record.h);
        setDirty();
        return record.h;
    }

    public int resolve(int handle) {
        Set<Integer> seen = new HashSet<>();
        int current = handle;
        while (current != 0 && seen.add(current)) {
            OwnerRecord record = records.get(current);
            if (record == null) { return 0; }
            if (record.moved == 0) { return record.isLive() ? current : 0; }
            current = record.moved;
        }
        return 0;
    }

    public boolean isLive(int handle) { return resolve(handle) != 0; }

    public String labelOf(int handle) {
        OwnerRecord record = records.get(resolve(handle));
        return record == null ? "" : record.label;
    }

    public void moveToTeam(int playerHandle, int teamHandle) {
        OwnerRecord player = records.get(playerHandle);
        OwnerRecord team = records.get(teamHandle);
        if (player == null || team == null || player.moved != 0) { return; }
        if (!player.kind.equals(OwnerRecord.KIND_PLAYER) || !team.kind.equals(OwnerRecord.KIND_TEAM) || !team.isLive()) { return; }
        player.moved = teamHandle;
        retire(player);
        ClaimIndex.moveAll(playerHandle, teamHandle);
    }

    public void markDissolved(int handle) {
        OwnerRecord record = records.get(handle);
        if (record == null || !record.isLive()) { return; }
        retire(record);
        epoch++;
    }

    private void retire(OwnerRecord record) {
        record.state = OwnerRecord.STATE_DISSOLVED;
        liveByKey.remove(record.lookupKey());
        setDirty();
    }

    public int epoch() { return epoch; }

    public int playerHandle(UUID uuid) {
        Integer found = live(OwnerRecord.KIND_PLAYER, OwnerRecord.SOURCE_SCOREBOARD, uuid.toString());
        return found == null ? 0 : found;
    }

    Map<Integer, OwnerRecord> liveTeams() {
        Map<Integer, OwnerRecord> teams = new LinkedHashMap<>();
        for (OwnerRecord record : records.values()) {
            if (record.isLive() && record.kind.equals(OwnerRecord.KIND_TEAM)) { teams.put(record.h, record); }
        }
        return teams;
    }

    public void held(int handle, int delta) {
        OwnerRecord record = records.get(handle);
        if (record == null || delta == 0) { return; }
        record.held = Math.max(0, record.held + delta);
        setDirty();
    }

    public void recount(Map<Integer, Integer> counts) {
        for (OwnerRecord record : records.values()) { record.held = counts.getOrDefault(record.h, 0); }
        setDirty();
    }

    public void claimAdded(int handle) {
        OwnerRecord record = records.get(handle);
        if (record == null) { return; }
        record.heldClaims++;
        setDirty();
    }

    public void claimRemoved(int handle) {
        OwnerRecord record = records.get(handle);
        if (record == null) { return; }
        record.heldClaims = Math.max(0, record.heldClaims - 1);
        setDirty();
    }

    public void claimsMoved(int from, int to, int count) {
        OwnerRecord source = records.get(from);
        OwnerRecord target = records.get(to);
        if (count <= 0 || source == null || target == null) { return; }
        source.heldClaims = Math.max(0, source.heldClaims - count);
        target.heldClaims += count;
        setDirty();
    }

    public void purge() {
        Set<Integer> linked = new HashSet<>();
        for (OwnerRecord record : records.values()) {
            if (record.moved != 0) { linked.add(record.moved); }
        }
        boolean changed = false;
        for (OwnerRecord record : new ArrayList<>(records.values())) {
            if (record.isLive() || record.held != 0 || record.heldClaims != 0 || linked.contains(record.h)) { continue; }
            records.remove(record.h);
            changed = true;
        }
        if (changed) { setDirty(); }
    }

    public CompoundTag sweepCursor() { return sweep.copy(); }

    public void setSweepCursor(CompoundTag cursor) {
        sweep = cursor.copy();
        setDirty();
    }

    private static OwnerTable read(CompoundTag compound) {
        OwnerTable table = new OwnerTable();
        table.next = Math.max(1, compound.getInt("next"));
        table.epoch = compound.getInt("epoch");
        table.sweep = compound.getCompound("sweep");
        ListTag tagged = compound.getList("records", Tag.TAG_COMPOUND);
        for (int index = 0; index < tagged.size(); index++) {
            OwnerRecord record = OwnerRecord.read(tagged.getCompound(index));
            table.records.put(record.h, record);
            if (record.isLive()) { table.liveByKey.put(record.lookupKey(), record.h); }
            if (record.h >= table.next) { table.next = record.h + 1; }
        }
        return table;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag compound) {
        ListTag tagged = new ListTag();
        for (OwnerRecord record : records.values()) { tagged.add(record.write()); }
        compound.putInt("next", next);
        compound.putInt("epoch", epoch);
        compound.put("records", tagged);
        compound.put("sweep", sweep.copy());
        return compound;
    }
}
