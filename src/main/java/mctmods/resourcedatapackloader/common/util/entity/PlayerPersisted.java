package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.common.util.Nbts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public final class PlayerPersisted {
    private PlayerPersisted() {}

    public static NBTTagCompound of(Entity entity) { return Nbts.child(entity.getEntityData(), EntityPlayer.PERSISTED_NBT_TAG); }

    public static NBTTagCompound read(Entity entity, String key) { return entity.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag(key); }

    public static NBTTagCompound section(Entity entity, String key) { return Nbts.child(of(entity), key); }

    public static int tally(Entity entity, String section, String key) {
        return bump(section(entity, section), key);
    }

    public static int bump(NBTTagCompound tag, String key) {
        int now = tag.getInteger(key) + 1;
        tag.setInteger(key, now);
        return now;
    }

    public static void clearTally(Entity entity, String section, String key) { section(entity, section).removeTag(key); }
}
