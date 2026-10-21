package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.Standing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import javax.annotation.Nonnull;

public final class GateStorage extends SavedData {
    private static final String NAME = "rdpl_gates";
    private static final String OLD_OPEN = "Open";
    private static final String OLD_KILLS = "Kills";
    private static final String PERSISTED = "rdplGates";
    private static final String PERSISTED_KILLS = "rdplGateKills";
    private static final String PERSISTED_TEAMS = "rdplGateTeams";
    private static final String PERSISTED_TEAM_KILLS = "rdplGateTeamKills";
    private CompoundTag open = new CompoundTag();
    private CompoundTag kills = new CompoundTag();
    private CompoundTag teams = new CompoundTag();
    private CompoundTag teamKills = new CompoundTag();

    private static GateStorage get(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(GateStorage::read, GateStorage::new, NAME); }

    private static GateStorage read(CompoundTag tag) {
        GateStorage held = new GateStorage();
        held.open = tag.getCompound(tag.contains(PERSISTED) ? PERSISTED : OLD_OPEN).copy();
        held.kills = tag.getCompound(tag.contains(PERSISTED_KILLS) ? PERSISTED_KILLS : OLD_KILLS).copy();
        held.teams = tag.getCompound(PERSISTED_TEAMS).copy();
        held.teamKills = tag.getCompound(PERSISTED_TEAM_KILLS).copy();
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag) {
        tag.put(PERSISTED, open.copy());
        tag.put(PERSISTED_KILLS, kills.copy());
        tag.put(PERSISTED_TEAMS, teams.copy());
        tag.put(PERSISTED_TEAM_KILLS, teamKills.copy());
        return tag;
    }

    public static int tally(ServerPlayer player, GateDef def) {
        if (def.global()) { return tallyGlobally(player.server, def.id()); }
        String team = tallyTeam(player, def);
        return team.isEmpty() ? tallyFor(player, def.id()) : tallyByTeam(player.server, team, def.id());
    }

    public static void clearTally(ServerPlayer player, GateDef def) {
        String team = tallyTeam(player, def);
        if (def.global()) { clearTallyGlobally(player.server, def.id()); }
        else if (team.isEmpty()) { clearTallyFor(player, def.id()); }
        else { clearTallyByTeam(player.server, team, def.id()); }
    }

    private static String tallyTeam(ServerPlayer player, GateDef def) { return def.tallyByTeam() ? Standing.teamOf(player) : ""; }

    private static int tallyByTeam(MinecraftServer server, String team, String key) {
        GateStorage data = get(server);
        CompoundTag counts = data.teamKills.getCompound(team);
        int now = counts.getInt(key) + 1;
        counts.putInt(key, now);
        keep(data.teamKills, team, counts);
        data.setDirty();
        return now;
    }

    private static void clearTallyByTeam(MinecraftServer server, String team, String key) {
        GateStorage data = get(server);
        CompoundTag counts = data.teamKills.getCompound(team);
        counts.remove(key);
        keep(data.teamKills, team, counts);
        data.setDirty();
    }

    private static void keep(CompoundTag byTeam, String team, CompoundTag entries) {
        if (entries.isEmpty()) { byTeam.remove(team); }
        else { byTeam.put(team, entries); }
    }

    public static boolean unlocked(ServerPlayer player, GateDef def) {
        if (def.global()) { return unlockedGlobally(player.server, def.id()); }
        String team = teamFor(player, def);
        return team.isEmpty() ? unlockedFor(player, def.id()) : unlockedByTeam(player.server, team, def.id());
    }

    public static void unlock(ServerPlayer player, GateDef def) {
        String team = teamFor(player, def);
        if (def.global()) { unlockGlobally(player.server, def.id()); }
        else if (team.isEmpty()) { unlockFor(player, def.id()); }
        else { unlockByTeam(player.server, team, def.id(), true); }
    }

    public static void lock(ServerPlayer player, GateDef def) {
        String team = teamFor(player, def);
        if (def.global()) { lockGlobally(player.server, def.id()); }
        else if (team.isEmpty()) { lockFor(player, def.id()); }
        else { unlockByTeam(player.server, team, def.id(), false); }
    }

    public static String teamFor(ServerPlayer player, GateDef def) { return def.teamWide() ? Standing.teamOf(player) : ""; }

    private static boolean unlockedByTeam(MinecraftServer server, String team, String key) { return get(server).teams.getCompound(team).getBoolean(key); }

    private static void unlockByTeam(MinecraftServer server, String team, String key, boolean unlocked) {
        GateStorage data = get(server);
        CompoundTag gates = data.teams.getCompound(team);
        if (unlocked) { gates.putBoolean(key, true); }
        else { gates.remove(key); }
        keep(data.teams, team, gates);
        data.setDirty();
    }

    public static boolean unlockedFor(Player player, String key) { return PlayerPersisted.read(player, PERSISTED).getBoolean(key); }

    public static void unlockFor(Player player, String key) { PlayerPersisted.section(player, PERSISTED).putBoolean(key, true); }

    public static void lockFor(Player player, String key) { PlayerPersisted.section(player, PERSISTED).remove(key); }

    public static int tallyFor(Player player, String key) { return PlayerPersisted.tally(player, PERSISTED_KILLS, key); }

    public static void clearTallyFor(Player player, String key) { PlayerPersisted.clearTally(player, PERSISTED_KILLS, key); }

    public static int tallyGlobally(MinecraftServer server, String key) {
        GateStorage data = get(server);
        int now = data.kills.getInt(key) + 1;
        data.kills.putInt(key, now);
        data.setDirty();
        return now;
    }

    public static int countGlobally(MinecraftServer server, String key) { return get(server).kills.getInt(key); }

    public static void noteFor(Player player, String key, int value) { PlayerPersisted.section(player, PERSISTED_KILLS).putInt(key, value); }

    public static int notedFor(Player player, String key) { return PlayerPersisted.read(player, PERSISTED_KILLS).getInt(key); }

    public static void clearTallyGlobally(MinecraftServer server, String key) {
        GateStorage data = get(server);
        data.kills.remove(key);
        data.setDirty();
    }

    public static boolean unlockedGlobally(MinecraftServer server, String key) { return get(server).open.getBoolean(key); }

    public static void unlockGlobally(MinecraftServer server, String key) {
        GateStorage data = get(server);
        data.open.putBoolean(key, true);
        data.setDirty();
    }

    public static void lockGlobally(MinecraftServer server, String key) {
        GateStorage data = get(server);
        data.open.remove(key);
        data.setDirty();
    }
}
