package mctmods.resourcedatapackloader.common.util;

import javax.annotation.Nullable;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public final class Nbts {
    public static final String STACK_TAG = "tag";

    private Nbts() {}

    @Nullable public static UUID uuid(String text) {
        try { return UUID.fromString(text); }
        catch (IllegalArgumentException bad) { return null; }
    }

    public static NBTTagCompound child(NBTTagCompound parent, String key) {
        if (!parent.hasKey(key, Constants.NBT.TAG_COMPOUND)) { parent.setTag(key, new NBTTagCompound()); }
        return parent.getCompoundTag(key);
    }
}
