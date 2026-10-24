package mctmods.resourcedatapackloader.content.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import javax.annotation.Nullable;

public final class PortwayBinding {
    private static final String KEY = "rdplPortway";

    private PortwayBinding() {}

    @Nullable public static PortwayStop read(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? null : data.copyTag().getCompound(KEY).map(PortwayStop::read).orElse(null);
    }

    public static void write(ItemStack stack, PortwayStop stop) { CustomData.update(DataComponents.CUSTOM_DATA, stack, held -> held.put(KEY, stop.write())); }

    public static void clear(ItemStack stack) { CustomData.update(DataComponents.CUSTOM_DATA, stack, held -> held.remove(KEY)); }
}
