package mctmods.resourcedatapackloader.content.gui;

import mctmods.resourcedatapackloader.content.tile.TileEntityPackContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import javax.annotation.Nonnull;

public class ContainerPack extends Container {
    public static final int SLOT = 18;
    public static final int HEADER = 17;
    public static final int VANILLA_WIDE = 176;
    private final IInventory held;
    private final int rows;
    private final int columns;

    public ContainerPack(InventoryPlayer player, TileEntityPackContainer tile) {
        this.held = tile;
        this.rows = Math.max(1, tile.rows());
        this.columns = Math.max(1, tile.columns());
        tile.openInventory(player.player);
        int wide = width(columns);
        int tall = height(rows);
        int left = (wide - columns * SLOT) / 2 + 1;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                addSlotToContainer(new Slot(held, column + row * columns, left + column * SLOT, HEADER + 1 + row * SLOT) {
                    @Override public boolean isItemValid(@Nonnull ItemStack stack) { return !nests(stack); }
                });
            }
        }
        int playerLeft = (wide - 9 * SLOT) / 2 + 1;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlotToContainer(new Slot(player, column + row * 9 + 9, playerLeft + column * SLOT, tall - 83 + row * SLOT));
            }
        }
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            addSlotToContainer(new Slot(player, hotbar, playerLeft + hotbar * SLOT, tall - 25));
        }
    }

    public static boolean nests(net.minecraft.item.ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        net.minecraft.item.Item item = stack.getItem();
        if (item instanceof mctmods.resourcedatapackloader.content.item.ContentItemContainer) { return true; }
        return item instanceof net.minecraft.item.ItemBlock
                && ((net.minecraft.item.ItemBlock) item).getBlock() instanceof mctmods.resourcedatapackloader.content.block.ContentBlockContainer;
    }

    public static int width(int columns) { return Math.max(VANILLA_WIDE, columns * SLOT + 14); }

    public static int height(int rows) { return 114 + rows * SLOT; }

    public int rows() { return rows; }

    public int columns() { return columns; }

    @Override public boolean canInteractWith(@Nonnull EntityPlayer player) { return held.isUsableByPlayer(player); }

    @Override public void onContainerClosed(@Nonnull EntityPlayer player) {
        super.onContainerClosed(player);
        held.closeInventory(player);
    }

    @Override @Nonnull public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int index) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) { return moved; }
        ItemStack stack = slot.getStack();
        moved = stack.copy();
        int mine = rows * columns;
        if (index < mine) {
            if (!mergeItemStack(stack, mine, inventorySlots.size(), true)) { return ItemStack.EMPTY; }
        }
        else if (!mergeItemStack(stack, 0, mine, false)) { return ItemStack.EMPTY; }
        if (stack.isEmpty()) { slot.putStack(ItemStack.EMPTY); }
        else { slot.onSlotChanged(); }
        return moved;
    }
}
