package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.common.util.Nbts;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class PlayerPersisted {
    private PlayerPersisted() {}

    public static CompoundTag of(Entity entity, String key) {
        CompoundTag data = entity.getPersistentData();
        CompoundTag persisted = Nbts.child(data, Player.PERSISTED_NBT_TAG);
        Tag loose = data.get(key);
        if (loose == null) { return persisted; }
        if (!persisted.contains(key)) { persisted.put(key, loose); }
        data.remove(key);
        return persisted;
    }

    public static CompoundTag read(Entity entity, String key) { return of(entity, key).getCompoundOrEmpty(key); }

    public static CompoundTag section(Entity entity, String key) { return Nbts.child(of(entity, key), key); }

    public static int tally(Entity entity, String section, String key) { return bump(section(entity, section), key); }

    public static int bump(CompoundTag tag, String key) {
        int now = tag.getIntOr(key, 0) + 1;
        tag.putInt(key, now);
        return now;
    }

    public static void clearTally(Entity entity, String section, String key) { section(entity, section).remove(key); }
}
