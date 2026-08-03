package mctmods.resourcedatapackloader.content.gate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import javax.annotation.Nonnull;

public final class GateStorage extends WorldSavedData {
    private static final String NAME = "rdpl_gates";
    private static final String TAG = "rdplGates";
    private static final String KILLS = "rdplGateKills";
    private NBTTagCompound open = new NBTTagCompound();
    private NBTTagCompound kills = new NBTTagCompound();

    public GateStorage(String name) { super(name); }

    public static boolean unlockedFor(EntityPlayer player, String key) { return persisted(player).getCompoundTag(TAG).getBoolean(key); }

    public static void unlockFor(EntityPlayer player, String key) {
        NBTTagCompound persisted = persisted(player);
        NBTTagCompound gates = persisted.getCompoundTag(TAG);
        gates.setBoolean(key, true);
        persisted.setTag(TAG, gates);
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
    }

    public static void lockFor(EntityPlayer player, String key) {
        NBTTagCompound persisted = persisted(player);
        NBTTagCompound gates = persisted.getCompoundTag(TAG);
        gates.removeTag(key);
        persisted.setTag(TAG, gates);
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
    }

    public static int tallyFor(EntityPlayer player, String key) {
        NBTTagCompound persisted = persisted(player);
        NBTTagCompound tally = persisted.getCompoundTag(KILLS);
        int now = tally.getInteger(key) + 1;
        tally.setInteger(key, now);
        persisted.setTag(KILLS, tally);
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
        return now;
    }

    public static int tallyGlobally(World world, String key) {
        GateStorage data = get(world);
        if (data == null) { return 0; }

        int now = data.kills.getInteger(key) + 1;
        data.kills.setInteger(key, now);
        data.markDirty();
        return now;
    }

    public static void clearTallyFor(EntityPlayer player, String key) {
        NBTTagCompound persisted = persisted(player);
        NBTTagCompound tally = persisted.getCompoundTag(KILLS);
        tally.removeTag(key);
        persisted.setTag(KILLS, tally);
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
    }

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

    private static NBTTagCompound persisted(EntityPlayer player) { return player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG); }

    private static GateStorage get(World world) {
        World overworld = DimensionManager.getWorld(0);
        MapStorage storage = overworld == null ? world.getMapStorage() : overworld.getMapStorage();
        if (storage == null) { return null; }

        GateStorage data = (GateStorage) storage.getOrLoadData(GateStorage.class, NAME);
        if (data == null) {
            data = new GateStorage(NAME);
            storage.setData(NAME, data);
        }
        return data;
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) {
        open = compound.getCompoundTag(TAG);
        kills = compound.getCompoundTag(KILLS);
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setTag(TAG, open);
        compound.setTag(KILLS, kills);
        return compound;
    }
}
