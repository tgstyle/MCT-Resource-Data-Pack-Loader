package mctmods.resourcedatapackloader.common.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class ItemHolders {
    private ItemHolders() {}

    public static Holder.Reference<Item> of(Item item) { return BuiltInRegistries.ITEM.getOrThrow(BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow()); }
}
