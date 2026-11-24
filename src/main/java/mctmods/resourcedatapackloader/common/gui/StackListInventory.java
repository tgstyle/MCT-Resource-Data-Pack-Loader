package mctmods.resourcedatapackloader.common.gui;

import mctmods.resourcedatapackloader.common.util.StackLists;

import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

abstract class StackListInventory implements IInventory {
    protected final NonNullList<ItemStack> items;

    protected StackListInventory(NonNullList<ItemStack> items) { this.items = items; }

    private boolean inRange(int slot) { return slot >= 0 && slot < getSizeInventory(); }

    @Override public boolean isEmpty() { return StackLists.empty(items, getSizeInventory()); }

    @Override @Nonnull public ItemStack getStackInSlot(int slot) { return inRange(slot) ? items.get(slot) : ItemStack.EMPTY; }

    @Override @Nonnull public ItemStack decrStackSize(int slot, int amount) { return StackLists.split(this, items, slot, amount); }

    @Override @Nonnull public ItemStack removeStackFromSlot(int slot) {
        if (!inRange(slot)) { return ItemStack.EMPTY; }
        ItemStack taken = ItemStackHelper.getAndRemove(items, slot);
        markDirty();
        return taken;
    }

    @Override public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        if (!inRange(slot)) { return; }
        items.set(slot, stack);
        if (stack.getCount() > getInventoryStackLimit()) { stack.setCount(getInventoryStackLimit()); }
        markDirty();
    }

    @Override public int getInventoryStackLimit() { return 64; }

    @Override public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) { return ContainerPack.storable(stack); }

    @Override public int getField(int id) { return 0; }

    @Override public void setField(int id, int value) {}

    @Override public int getFieldCount() { return 0; }

    @Override public void clear() {
        for (int slot = 0; slot < getSizeInventory(); slot++) { items.set(slot, ItemStack.EMPTY); }
        markDirty();
    }
}
