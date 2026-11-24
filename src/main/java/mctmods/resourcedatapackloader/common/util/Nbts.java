package mctmods.resourcedatapackloader.common.util;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public final class Nbts {
    private Nbts() {}

    @Nullable public static UUID uuid(String text) {
        try { return UUID.fromString(text); }
        catch (IllegalArgumentException bad) { return null; }
    }

    public static CompoundTag child(CompoundTag parent, String key) {
        if (parent.getCompound(key).isEmpty()) { parent.put(key, new CompoundTag()); }
        return parent.getCompoundOrEmpty(key);
    }
}
