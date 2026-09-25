package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.util.PlayerPersisted;
import mctmods.resourcedatapackloader.util.world.SavedData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CardStorage extends WorldSavedData {
    private static final String NAME = "rdpl_cards";
    private static final String TAG = "rdplCards";
    private static final String KILLS = "rdplCardKills";
    private NBTTagCompound fired = new NBTTagCompound();

    public CardStorage(String name) { super(name); }

    static boolean firedFor(EntityPlayer player, String key) { return PlayerPersisted.read(player, TAG).hasKey(key); }

    static long lastFor(EntityPlayer player, String key) { return PlayerPersisted.read(player, TAG).getLong(key); }

    static void stampFor(EntityPlayer player, String key, long now) { PlayerPersisted.section(player, TAG).setLong(key, now); }

    static int tallyFor(EntityPlayer player, String key) { return PlayerPersisted.tally(player, KILLS, key); }

    static void clearTallyFor(EntityPlayer player, String key) { PlayerPersisted.clearTally(player, KILLS, key); }

    static boolean firedInWorld(World world, String key) {
        CardStorage data = get(world);
        return data != null && data.fired.hasKey(key);
    }

    static void stampWorld(World world, String key, long now) {
        CardStorage data = get(world);
        if (data == null) { return; }
        data.fired.setLong(key, now);
        data.markDirty();
    }

    @Nullable private static CardStorage get(World world) {
        World overworld = DimensionManager.getWorld(0);
        MapStorage storage = overworld == null ? world.getMapStorage() : overworld.getMapStorage();
        if (storage == null) { return null; }
        return SavedData.get(storage, CardStorage.class, NAME, CardStorage::new);
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound compound) { fired = compound.getCompoundTag(TAG); }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setTag(TAG, fired);
        return compound;
    }
}
