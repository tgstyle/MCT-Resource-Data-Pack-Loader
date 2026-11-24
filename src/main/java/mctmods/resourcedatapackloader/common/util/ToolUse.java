package mctmods.resourcedatapackloader.common.util;

import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

public final class ToolUse {
    private ToolUse() {}

    public static boolean isShovel(ItemStack held) { return held.getItem() instanceof ItemSpade || held.getItem().getToolClasses(held).contains("shovel"); }

    public static boolean isHoe(ItemStack held) { return held.getItem() instanceof ItemHoe || held.getItem().getToolClasses(held).contains("hoe"); }
}
