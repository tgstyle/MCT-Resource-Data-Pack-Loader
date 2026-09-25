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

    public static CompoundTag read(Entity entity, String key) { return of(entity, key).getCompound(key); }

    public static CompoundTag section(Entity entity, String key) {
        CompoundTag persisted = of(entity, key);
        if (!persisted.contains(key, Tag.TAG_COMPOUND)) { persisted.put(key, new CompoundTag()); }
        return persisted.getCompound(key);
    }

    public static int tally(Entity entity, String section, String key) {
        CompoundTag tally = section(entity, section);
        int now = tally.getInt(key) + 1;
        tally.putInt(key, now);
        return now;
    }

    public static void clearTally(Entity entity, String section, String key) { section(entity, section).remove(key); }
}
