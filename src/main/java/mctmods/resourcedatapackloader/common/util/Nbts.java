package mctmods.resourcedatapackloader.common.util;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class Nbts {
    private Nbts() {}

    @Nullable public static UUID uuid(String text) {
        try { return UUID.fromString(text); }
        catch (IllegalArgumentException bad) { return null; }
    }

    public static CompoundTag child(CompoundTag parent, String key) {
        if (!parent.contains(key, Tag.TAG_COMPOUND)) { parent.put(key, new CompoundTag()); }
        return parent.getCompound(key);
    }
}
