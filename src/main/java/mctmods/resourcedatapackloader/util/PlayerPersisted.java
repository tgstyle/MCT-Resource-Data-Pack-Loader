package mctmods.resourcedatapackloader.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class PlayerPersisted {
    private PlayerPersisted() {}

    public static CompoundTag of(Entity entity, String key) {
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(Player.PERSISTED_NBT_TAG, Tag.TAG_COMPOUND)) { data.put(Player.PERSISTED_NBT_TAG, new CompoundTag()); }
        CompoundTag persisted = data.getCompound(Player.PERSISTED_NBT_TAG);
        Tag loose = data.get(key);
        if (loose == null) { return persisted; }
        if (!persisted.contains(key)) { persisted.put(key, loose); }
        data.remove(key);
        return persisted;
    }
}
