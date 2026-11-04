package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.common.ownership.claims.ClaimIndex;
import mctmods.resourcedatapackloader.common.util.world.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public final class OwnerTable extends WorldSavedData {
    private static final String NAME = "rdpl_owners";
    private static final String FTB_MIRROR_PREFIX = "ftbteams-";
    private final Map<Integer, OwnerRecord> records = new LinkedHashMap<>();
    private final Map<String, Integer> liveByKey = new HashMap<>();
    private NBTTagCompound sweep = new NBTTagCompound();
    private int next = 1;
    private int epoch;

    public OwnerTable(String name) { super(name); }

    @Nullable public static OwnerTable get(@Nullable MinecraftServer server) {
        if (server == null) { return null; }
        MapStorage storage = server.getWorld(0).getMapStorage();
        return storage == null ? null : SavedData.get(storage, OwnerTable.class, NAME, OwnerTable::new);
    }

    public static int handleForTeam(@Nullable MinecraftServer server, String name) {
        OwnerTable table = get(server);
        if (table == null || name.isEmpty() || name.startsWith(FTB_MIRROR_PREFIX)) { return 0; }
        return table.liveHandle(OwnerRecord.KIND_TEAM, OwnerRecord.SOURCE_SCOREBOARD, name, name);
    }

    public static int handleForSource(@Nullable MinecraftServer server, String source, String key, String label) {
        OwnerTable table = get(server);
        return table == null || key.isEmpty() ? 0 : table.liveHandle(OwnerRecord.KIND_TEAM, source, key, label);
    }

    public static int handleForPlayer(@Nullable MinecraftServer server, UUID uuid, String name) {
        OwnerTable table = get(server);
        return table == null ? 0 : table.liveHandle(OwnerRecord.KIND_PLAYER, OwnerRecord.SOURCE_SCOREBOARD, uuid.toString(), name);
    }

    public static int handleOf(EntityPlayer player, OwnershipSettings.Who who) {
        if (who == OwnershipSettings.Who.PLAYER) { return handleForPlayer(player.getServer(), player.getUniqueID(), player.getName()); }
        int team = ScoreboardOwnerSource.current().teamOf(player);
        if (team != 0 || who == OwnershipSettings.Who.TEAM) { return team; }
        return handleForPlayer(player.getServer(), player.getUniqueID(), player.getName());
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
                markDirty();
            }
            return found;
        }
        OwnerRecord record = new OwnerRecord(next++, kind, source, key, label);
        records.put(record.h, record);
        liveByKey.put(record.lookupKey(), record.h);
        markDirty();
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
        markDirty();
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
        markDirty();
    }

    public void recount(Map<Integer, Integer> counts) {
        for (OwnerRecord record : records.values()) { record.held = counts.getOrDefault(record.h, 0); }
        markDirty();
    }

    public void claimAdded(int handle) {
        OwnerRecord record = records.get(handle);
        if (record == null) { return; }
        record.heldClaims++;
        markDirty();
    }

    public void claimRemoved(int handle) {
        OwnerRecord record = records.get(handle);
        if (record == null) { return; }
        record.heldClaims = Math.max(0, record.heldClaims - 1);
        markDirty();
    }

    public void claimsMoved(int from, int to, int count) {
        OwnerRecord source = records.get(from);
        OwnerRecord target = records.get(to);
        if (count <= 0 || source == null || target == null) { return; }
        source.heldClaims = Math.max(0, source.heldClaims - count);
        target.heldClaims += count;
        markDirty();
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
        if (changed) { markDirty(); }
    }

    public NBTTagCompound sweepCursor() { return sweep.copy(); }

    public void setSweepCursor(NBTTagCompound cursor) {
        sweep = cursor.copy();
        markDirty();
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        records.clear();
        liveByKey.clear();
        next = Math.max(1, compound.getInteger("next"));
        epoch = compound.getInteger("epoch");
        sweep = compound.getCompoundTag("sweep");
        NBTTagList tagged = compound.getTagList("records", Constants.NBT.TAG_COMPOUND);
        for (int index = 0; index < tagged.tagCount(); index++) {
            OwnerRecord record = OwnerRecord.read(tagged.getCompoundTagAt(index));
            records.put(record.h, record);
            if (record.isLive()) { liveByKey.put(record.lookupKey(), record.h); }
            if (record.h >= next) { next = record.h + 1; }
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList tagged = new NBTTagList();
        for (OwnerRecord record : records.values()) { tagged.appendTag(record.write()); }
        compound.setInteger("next", next);
        compound.setInteger("epoch", epoch);
        compound.setTag("records", tagged);
        compound.setTag("sweep", sweep.copy());
        return compound;
    }
}
