package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.Standing;
import mctmods.resourcedatapackloader.util.world.SavedData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import javax.annotation.Nonnull;

public final class GateStorage extends WorldSavedData {
    private static final String NAME = "rdpl_gates";
    private static final String TAG = "rdplGates";
    private static final String KILLS = "rdplGateKills";
    private static final String TEAMS = "rdplGateTeams";
    private NBTTagCompound open = new NBTTagCompound();
    private NBTTagCompound kills = new NBTTagCompound();
    private NBTTagCompound teams = new NBTTagCompound();

    public GateStorage(String name) { super(name); }

    public static int tally(EntityPlayer player, GateDef def) { return def.global() ? tallyGlobally(player.world, def.getKey()) : tallyFor(player, def.getKey()); }

    public static void clearTally(EntityPlayer player, GateDef def) {
        if (def.global()) { clearTallyGlobally(player.world, def.getKey()); }
        else { clearTallyFor(player, def.getKey()); }
    }

    public static boolean unlocked(EntityPlayer player, GateDef def) {
        if (def.global()) { return unlockedGlobally(player.world, def.getKey()); }
        String team = teamFor(player, def);
        return team.isEmpty() ? unlockedFor(player, def.getKey()) : unlockedByTeam(player.world, team, def.getKey());
    }

    public static void unlock(EntityPlayer player, GateDef def) {
        String team = teamFor(player, def);
        if (def.global()) { unlockGlobally(player.world, def.getKey()); }
        else if (team.isEmpty()) { unlockFor(player, def.getKey()); }
        else { unlockByTeam(player.world, team, def.getKey(), true); }
    }

    public static void lock(EntityPlayer player, GateDef def) {
        String team = teamFor(player, def);
        if (def.global()) { lockGlobally(player.world, def.getKey()); }
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
        if (unlocked) { gates.setBoolean(key, true); }
        else { gates.removeTag(key); }
        if (gates.isEmpty()) { data.teams.removeTag(team); }
        else { data.teams.setTag(team, gates); }
        data.markDirty();
    }

    public static boolean unlockedFor(EntityPlayer player, String key) { return PlayerPersisted.read(player, TAG).getBoolean(key); }

    public static void unlockFor(EntityPlayer player, String key) { PlayerPersisted.section(player, TAG).setBoolean(key, true); }

    public static void lockFor(EntityPlayer player, String key) { PlayerPersisted.section(player, TAG).removeTag(key); }

    public static int tallyFor(EntityPlayer player, String key) { return PlayerPersisted.tally(player, KILLS, key); }

    public static int tallyGlobally(World world, String key) {
        GateStorage data = get(world);
        if (data == null) { return 0; }
        int now = data.kills.getInteger(key) + 1;
        data.kills.setInteger(key, now);
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
        data.open.setBoolean(key, true);
        data.markDirty();
    }

    public static void lockGlobally(World world, String key) {
        GateStorage data = get(world);
        if (data == null) { return; }
        data.open.removeTag(key);
        data.markDirty();
    }

    private static GateStorage get(World world) {
        MapStorage storage = world.getMapStorage();
        if (storage == null) { return null; }
        return SavedData.get(storage, GateStorage.class, NAME, GateStorage::new);
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        open = compound.getCompoundTag(TAG);
        kills = compound.getCompoundTag(KILLS);
        teams = compound.getCompoundTag(TEAMS);
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setTag(TAG, open);
        compound.setTag(KILLS, kills);
        compound.setTag(TEAMS, teams);
        return compound;
    }
}
