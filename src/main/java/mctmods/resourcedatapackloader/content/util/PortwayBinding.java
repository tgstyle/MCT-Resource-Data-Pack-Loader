package mctmods.resourcedatapackloader.content.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;

public final class PortwayBinding {
    private static final String KEY = "rdplPortway";

    private PortwayBinding() {}

    @Nullable public static PortwayStop read(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(KEY);
        return tag == null ? null : PortwayStop.read(tag);
    }

    public static void write(ItemStack stack, PortwayStop stop) { stack.addTagElement(KEY, stop.write()); }

    public static void clear(ItemStack stack) { stack.removeTagKey(KEY); }
}
