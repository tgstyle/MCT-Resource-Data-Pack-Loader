package mctmods.resourcedatapackloader.common.gates;

import mctmods.resourcedatapackloader.common.pack.definitions.portals.GateDef;
import mctmods.resourcedatapackloader.common.util.entity.PlayerPersisted;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.common.util.world.SavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public final class GateStorage extends WorldSavedData {
    private static final String NAME = "rdpl_gates";
    private static final String TAG = "rdplGates";
    private static final String KILLS = "rdplGateKills";
    private static final String TEAMS = "rdplGateTeams";
    private static final String TEAM_KILLS = "rdplGateTeamKills";
    private NBTTagCompound open = new NBTTagCompound();
    private NBTTagCompound kills = new NBTTagCompound();
    private NBTTagCompound teams = new NBTTagCompound();
    private NBTTagCompound teamKills = new NBTTagCompound();

    public GateStorage(String name) { super(name); }

    public static int tally(EntityPlayer player, GateDef def) {
        String team = scopeTeam(player, def, def.tallyByTeam());
        if (team == null) { return tallyGlobally(player.world, def.getKey()); }
        return team.isEmpty() ? tallyFor(player, def.getKey()) : tallyByTeam(player.world, team, def.getKey());
    }

    public static void clearTally(EntityPlayer player, GateDef def) {
        String team = scopeTeam(player, def, def.tallyByTeam());
        if (team == null) { clearTallyGlobally(player.world, def.getKey()); }
        else if (team.isEmpty()) { clearTallyFor(player, def.getKey()); }
        else { clearTallyByTeam(player.world, team, def.getKey()); }
    }

    @Nullable private static String scopeTeam(EntityPlayer player, GateDef def, boolean byTeam) {
        if (def.global()) { return null; }
        return byTeam ? Standing.teamOf(player) : "";
    }

    private static int tallyByTeam(World world, String team, String key) {
        GateStorage data = get(world);
        if (data == null) { return 0; }
        NBTTagCompound counts = data.teamKills.getCompoundTag(team);
        int now = PlayerPersisted.bump(counts, key);
        keep(data.teamKills, team, counts);
        data.markDirty();
        return now;
    }

    private static void clearTallyByTeam(World world, String team, String key) {
        GateStorage data = get(world);
        if (data == null) { return; }
        NBTTagCompound counts = data.teamKills.getCompoundTag(team);
        counts.removeTag(key);
        keep(data.teamKills, team, counts);
        data.markDirty();
    }

    private static void keep(NBTTagCompound byTeam, String team, NBTTagCompound entries) {
        if (entries.isEmpty()) { byTeam.removeTag(team); }
        else { byTeam.setTag(team, entries); }
    }

    public static boolean unlocked(EntityPlayer player, GateDef def) {
        String team = scopeTeam(player, def, def.teamWide());
        if (team == null) { return unlockedGlobally(player.world, def.getKey()); }
        return team.isEmpty() ? unlockedFor(player, def.getKey()) : unlockedByTeam(player.world, team, def.getKey());
    }

    public static void unlock(EntityPlayer player, GateDef def) {
        String team = scopeTeam(player, def, def.teamWide());
        if (team == null) { unlockGlobally(player.world, def.getKey()); }
        else if (team.isEmpty()) { unlockFor(player, def.getKey()); }
        else { unlockByTeam(player.world, team, def.getKey(), true); }
    }

    public static void lock(EntityPlayer player, GateDef def) {
        String team = scopeTeam(player, def, def.teamWide());
        if (team == null) { lockGlobally(player.world, def.getKey()); }
        else if (team.isEmpty()) { lockFor(player, def.getKey()); }
        else { unlockByTeam(player.world, team, def.getKey(), false); }
    }

    public static String teamFor(EntityPlayer player, GateDef def) { return def.teamWide() ? Standing.teamOf(player) : ""; }

    private static boolean unlockedByTeam(World world, String team, String key) {
        GateStorage data = get(world);
        return data != null && data.teams.getCompoundTag(team).getBoolean(key);
    }

    private static void unlockByTeam(World world, String team, String key, boolean unlocked) {
        GateStorage data = get(world);
        if (data == null) { return; }
        NBTTagCompound gates = data.teams.getCompoundTag(team);
        setFlag(gates, key, unlocked);
        keep(data.teams, team, gates);
        data.markDirty();
    }

    public static boolean unlockedFor(EntityPlayer player, String key) { return PlayerPersisted.read(player, TAG).getBoolean(key); }

    public static void unlockFor(EntityPlayer player, String key) { setFlag(PlayerPersisted.section(player, TAG), key, true); }

    public static void lockFor(EntityPlayer player, String key) { setFlag(PlayerPersisted.section(player, TAG), key, false); }

    private static void setFlag(NBTTagCompound tag, String key, boolean on) {
        if (on) { tag.setBoolean(key, true); }
        else { tag.removeTag(key); }
    }

    public static int tallyFor(EntityPlayer player, String key) { return PlayerPersisted.tally(player, KILLS, key); }

    public static int tallyGlobally(World world, String key) {
        GateStorage data = get(world);
        if (data == null) { return 0; }
        int now = PlayerPersisted.bump(data.kills, key);
        data.markDirty();
        return now;
    }

    public static int countGlobally(World world, String key) {
        GateStorage data = get(world);
        return data == null ? 0 : data.kills.getInteger(key);
    }

    public static void noteFor(EntityPlayer player, String key, int value) { PlayerPersisted.section(player, KILLS).setInteger(key, value); }

    public static int notedFor(EntityPlayer player, String key) { return PlayerPersisted.read(player, KILLS).getInteger(key); }

    public static void clearTallyFor(EntityPlayer player, String key) { PlayerPersisted.clearTally(player, KILLS, key); }

    public static void clearTallyGlobally(World world, String key) {
        GateStorage data = get(world);
        if (data == null) { return; }
        data.kills.removeTag(key);
        data.markDirty();
    }

    public static boolean unlockedGlobally(World world, String key) {
        GateStorage data = get(world);
        return data != null && data.open.getBoolean(key);
    }

    public static void unlockGlobally(World world, String key) {
        GateStorage data = get(world);
        if (data == null) { return; }
        setFlag(data.open, key, true);
        data.markDirty();
    }

    public static void lockGlobally(World world, String key) {
        GateStorage data = get(world);
        if (data == null) { return; }
        setFlag(data.open, key, false);
        data.markDirty();
    }

    private static GateStorage get(World world) {
        return SavedData.of(world.getMapStorage(), GateStorage.class, NAME, GateStorage::new);
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        open = compound.getCompoundTag(TAG);
        kills = compound.getCompoundTag(KILLS);
        teams = compound.getCompoundTag(TEAMS);
        teamKills = compound.getCompoundTag(TEAM_KILLS);
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setTag(TAG, open);
        compound.setTag(KILLS, kills);
        compound.setTag(TEAMS, teams);
        compound.setTag(TEAM_KILLS, teamKills);
        return compound;
    }
}
