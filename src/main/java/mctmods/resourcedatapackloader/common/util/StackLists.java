package mctmods.resourcedatapackloader.common.util;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;

public final class StackLists {
    private StackLists() {}

    public static boolean empty(List<ItemStack> items, int size) {
        for (int slot = 0; slot < size; slot++) {
            if (!items.get(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Nonnull public static ItemStack split(IInventory inventory, List<ItemStack> items, int slot, int amount) {
        ItemStack taken = slot < 0 || slot >= inventory.getSizeInventory() ? ItemStack.EMPTY : ItemStackHelper.getAndSplit(items, slot, amount);
        if (!taken.isEmpty()) {
            inventory.markDirty();
        }
        return taken;
    }
}
