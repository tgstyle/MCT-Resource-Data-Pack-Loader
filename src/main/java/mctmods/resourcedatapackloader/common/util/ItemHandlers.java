package mctmods.resourcedatapackloader.common.util;

import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public final class ItemHandlers {
    private ItemHandlers() {}

    public static boolean any(IItemHandler handler, Predicate<ItemStack> test) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            if (test.test(handler.getStackInSlot(slot))) { return true; }
        }
        return false;
    }

    public static long sum(IItemHandler handler, ToLongFunction<ItemStack> value) {
        long total = 0L;
        for (int slot = 0; slot < handler.getSlots(); slot++) { total += value.applyAsLong(handler.getStackInSlot(slot)); }
        return total;
    }
}
