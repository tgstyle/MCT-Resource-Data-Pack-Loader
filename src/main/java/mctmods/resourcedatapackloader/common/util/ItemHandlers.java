package mctmods.resourcedatapackloader.common.util;

import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

public final class ItemHandlers {
    private ItemHandlers() {}

    public static boolean any(ResourceHandler<ItemResource> handler, Predicate<ItemStack> test) {
        for (int slot = 0; slot < handler.size(); slot++) {
            if (test.test(ItemUtil.getStack(handler, slot))) { return true; }
        }
        return false;
    }

    public static long sum(ResourceHandler<ItemResource> handler, ToLongFunction<ItemStack> value) {
        long total = 0L;
        for (int slot = 0; slot < handler.size(); slot++) { total += value.applyAsLong(ItemUtil.getStack(handler, slot)); }
        return total;
    }
}
