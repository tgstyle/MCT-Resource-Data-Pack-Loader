package mctmods.resourcedatapackloader.content.gui;

import mctmods.resourcedatapackloader.content.tile.TileEntityPackContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class ContainerPack extends Container {
    public static final int SLOT = 18;
    public static final int HEADER = 17;
    public static final int VANILLA_WIDE = 176;
    private final IInventory held;
    private final int rows;
    private final int columns;

    public interface IPlayerSlot { Slot create(InventoryPlayer player, int index, int x, int y); }

    public interface IMerge { boolean into(ItemStack stack, int from, int to, boolean reverse); }

    public ContainerPack(InventoryPlayer player, TileEntityPackContainer tile) {
        this(player, tile, tile.rows(), tile.columns());
        tile.openInventory(player.player);
    }

    protected ContainerPack(InventoryPlayer player, IInventory held, int rows, int columns) {
        this.held = held;
        this.rows = Math.max(1, rows);
        this.columns = Math.max(1, columns);
        int wide = width(this.columns);
        int left = (wide - this.columns * SLOT) / 2 + 1;
        for (int row = 0; row < this.rows; row++) {
            for (int column = 0; column < this.columns; column++) {
                addSlotToContainer(new Slot(held, column + row * this.columns, left + column * SLOT, HEADER + 1 + row * SLOT) {
                    @Override public boolean isItemValid(@Nonnull ItemStack stack) { return storable(stack); }
                });
            }
        }
        for (Slot slot : playerSlots(player, wide, height(this.rows), this::playerSlot)) { addSlotToContainer(slot); }
    }

    protected Slot playerSlot(InventoryPlayer player, int index, int x, int y) { return new Slot(player, index, x, y); }

    public static List<Slot> playerSlots(InventoryPlayer player, int wide, int tall, IPlayerSlot factory) {
        List<Slot> slots = new ArrayList<>();
        int left = (wide - 9 * SLOT) / 2 + 1;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) { slots.add(factory.create(player, column + row * 9 + 9, left + column * SLOT, tall - 83 + row * SLOT)); }
        }
        for (int hotbar = 0; hotbar < 9; hotbar++) { slots.add(factory.create(player, hotbar, left + hotbar * SLOT, tall - 25)); }
        return slots;
    }

    public static boolean storable(net.minecraft.item.ItemStack stack) {
        if (stack.isEmpty()) { return true; }
        net.minecraft.item.Item item = stack.getItem();
        if (item instanceof mctmods.resourcedatapackloader.content.item.ContentItemContainer) { return false; }
        return !(item instanceof net.minecraft.item.ItemBlock)
                || !(((net.minecraft.item.ItemBlock) item).getBlock() instanceof mctmods.resourcedatapackloader.content.block.ContentBlockContainer);
    }

    public static int width(int columns) { return Math.max(VANILLA_WIDE, columns * SLOT + 14); }

    public static int height(int rows) { return 114 + rows * SLOT; }

    @Override public boolean canInteractWith(@Nonnull EntityPlayer player) { return held.isUsableByPlayer(player); }

    @Override public void onContainerClosed(@Nonnull EntityPlayer player) {
        super.onContainerClosed(player);
        held.closeInventory(player);
    }

    @Override @Nonnull public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int index) {
        int mine = rows * columns;
        return transfer(this, index, mine, this::mergeItemStack, stack -> storable(stack) && mergeItemStack(stack, 0, mine, false));
    }

    @Nonnull public static ItemStack transfer(Container container, int index, int mine, IMerge merge, Predicate<ItemStack> store) {
        Slot slot = container.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) { return ItemStack.EMPTY; }
        ItemStack stack = slot.getStack();
        ItemStack moved = stack.copy();
        boolean done = index < mine ? merge.into(stack, mine, container.inventorySlots.size(), true) : store.test(stack);
        if (!done) { return ItemStack.EMPTY; }
        if (stack.isEmpty()) { slot.putStack(ItemStack.EMPTY); }
        else { slot.onSlotChanged(); }
        return moved;
    }
}
