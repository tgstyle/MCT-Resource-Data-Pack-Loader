package mctmods.resourcedatapackloader.content.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import javax.annotation.Nonnull;

public class ContainerPouch extends Container {
    public interface Source { ItemStack held(EntityPlayer player); }
    private final PouchInventory held;
    private final Source source;
    private final ItemStack opened;
    private final int rows;
    private final int columns;
    private int worn = -1;

    public ContainerPouch(InventoryPlayer player, PouchInventory held, EnumHand hand, int rows, int columns) {
        this(player, held, holder -> holder.getHeldItem(hand), rows, columns);
    }

    public int worn() { return worn; }

    public ContainerPouch worn(int slot) {
        worn = slot;
        return this;
    }

    public ContainerPouch(InventoryPlayer player, PouchInventory held, Source source, int rows, int columns) {
        this.held = held;
        this.source = source;
        this.opened = held.pouch();
        this.rows = Math.max(1, rows);
        this.columns = Math.max(1, columns);
        int wide = ContainerPack.width(this.columns);
        int tall = ContainerPack.height(this.rows);
        int left = (wide - this.columns * ContainerPack.SLOT) / 2 + 1;
        for (int row = 0; row < this.rows; row++) {
            for (int column = 0; column < this.columns; column++) {
                addSlotToContainer(new Slot(held, column + row * this.columns, left + column * ContainerPack.SLOT, ContainerPack.HEADER + 1 + row * ContainerPack.SLOT) {
                    @Override public boolean isItemValid(@Nonnull ItemStack stack) { return !ContainerPack.nests(stack); }
                });
            }
        }
        int playerLeft = (wide - 9 * ContainerPack.SLOT) / 2 + 1;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlotToContainer(guard(player, column + row * 9 + 9, playerLeft + column * ContainerPack.SLOT, tall - 83 + row * ContainerPack.SLOT));
            }
        }
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            addSlotToContainer(guard(player, hotbar, playerLeft + hotbar * ContainerPack.SLOT, tall - 25));
        }
    }

    private Slot guard(InventoryPlayer player, int index, int x, int y) {
        return new Slot(player, index, x, y) {
            @Override public boolean canTakeStack(@Nonnull EntityPlayer taking) { return getStack() != opened; }
            @Override public boolean isItemValid(@Nonnull ItemStack stack) { return getStack() != opened; }
        };
    }

    @Override public boolean canInteractWith(@Nonnull EntityPlayer player) { return source.held(player) == opened; }

    @Override public void onContainerClosed(@Nonnull EntityPlayer player) {
        super.onContainerClosed(player);
        held.closeInventory(player);
    }

    @Override @Nonnull public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int index) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) { return moved; }
        ItemStack stack = slot.getStack();
        if (stack == opened) { return moved; }
        moved = stack.copy();
        int mine = rows * columns;
        if (index < mine) {
            if (!mergeItemStack(stack, mine, inventorySlots.size(), true)) { return ItemStack.EMPTY; }
        }
        else if (ContainerPack.nests(stack) || !mergeItemStack(stack, 0, mine, false)) { return ItemStack.EMPTY; }
        if (stack.isEmpty()) { slot.putStack(ItemStack.EMPTY); }
        else { slot.onSlotChanged(); }
        return moved;
    }
}
