package mctmods.resourcedatapackloader.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public final class PlayerPersisted {
    private PlayerPersisted() {}

    public static NBTTagCompound of(Entity entity) {
        NBTTagCompound data = entity.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG, Constants.NBT.TAG_COMPOUND)) { data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound()); }
        return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    public static NBTTagCompound read(Entity entity, String key) { return entity.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag(key); }

    public static NBTTagCompound section(Entity entity, String key) {
        NBTTagCompound persisted = of(entity);
        if (!persisted.hasKey(key, Constants.NBT.TAG_COMPOUND)) { persisted.setTag(key, new NBTTagCompound()); }
        return persisted.getCompoundTag(key);
    }

    public static int tally(Entity entity, String section, String key) {
        NBTTagCompound tally = section(entity, section);
        int now = tally.getInteger(key) + 1;
        tally.setInteger(key, now);
        return now;
    }

    public static void clearTally(Entity entity, String section, String key) { section(entity, section).removeTag(key); }
}
