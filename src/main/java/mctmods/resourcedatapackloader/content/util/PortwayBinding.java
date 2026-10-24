package mctmods.resourcedatapackloader.content.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import javax.annotation.Nullable;

public final class PortwayBinding {
    private static final String TAG = "rdplPortway";

    private PortwayBinding() {}

    @Nullable public static PortwayStop read(ItemStack stack) {
        NBTTagCompound tag = stack.getSubCompound(TAG);
        return tag == null || tag.getString("block").isEmpty() ? null : PortwayStop.read(tag);
    }

    public static void write(ItemStack stack, PortwayStop stop) { stack.setTagInfo(TAG, stop.write()); }

    public static void clear(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) { tag.removeTag(TAG); }
    }
}
