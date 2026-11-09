package mctmods.resourcedatapackloader.common.blocks.compress;

import java.util.function.ToIntFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CompressedFuel {
    private static final int FACTOR = 10;

    private CompressedFuel() {}

    public static boolean layer(Item item) {
        CompressedSet set = owner(item);
        return set != null && set.layerOf(item) >= set.low();
    }

    public static int burnTime(ItemStack stack, ToIntFunction<ItemStack> base) {
        CompressedSet set = owner(stack.getItem());
        if (set == null) { return -1; }
        int layer = set.layerOf(stack.getItem());
        if (layer < set.low()) { return -1; }
        ItemStack source = set.source();
        if (source.isEmpty()) { return 0; }
        int burn = base.applyAsInt(source);
        if (burn <= 0) { return 0; }
        long total = burn;
        for (int step = 0; step < layer - set.low() + 1 && total < Integer.MAX_VALUE; step++) { total *= FACTOR; }
        return (int) Math.min(total, Integer.MAX_VALUE);
    }

    private static CompressedSet owner(Item item) { return ContentCompressables.owner(BuiltInRegistries.ITEM.getKey(item)); }
}
