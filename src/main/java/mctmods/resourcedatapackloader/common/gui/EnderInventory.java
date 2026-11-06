package mctmods.resourcedatapackloader.common.gui;

import mctmods.resourcedatapackloader.common.blocks.container.logic.TileEntityPackContainer;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.util.StackLists;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class EnderInventory implements IInventory {
    public static final int STORE = ContainerDef.MOST_ROWS * ContainerDef.MOST_COLUMNS;
    @Nullable private final EnderStore store;
    private final UUID owner;
    private final NonNullList<ItemStack> items;
    private final int rows;
    private final int columns;
    private final ITextComponent title;
    private final boolean custom;
    @Nullable private final TileEntityPackContainer at;

    public EnderInventory(World world, UUID owner, int rows, int columns, ITextComponent title, boolean custom, @Nullable TileEntityPackContainer at) {
        this.store = world.isRemote ? null : EnderStore.get(world);
        this.owner = owner;
        this.items = store == null ? NonNullList.withSize(STORE, ItemStack.EMPTY) : store.items(owner);
        this.rows = Math.max(1, rows);
        this.columns = Math.max(1, columns);
        this.title = title;
        this.custom = custom;
        this.at = at;
    }

    public int rows() { return rows; }

    public int columns() { return columns; }

    public int version() { return store == null ? 0 : store.version(owner); }

    @Override public int getSizeInventory() { return Math.min(STORE, rows * columns); }

    @Override public boolean isEmpty() { return StackLists.empty(items, getSizeInventory()); }

    @Override @Nonnull public ItemStack getStackInSlot(int slot) { return slot >= 0 && slot < getSizeInventory() ? items.get(slot) : ItemStack.EMPTY; }

    @Override @Nonnull public ItemStack decrStackSize(int slot, int amount) { return StackLists.split(this, items, slot, amount); }

    @Override @Nonnull public ItemStack removeStackFromSlot(int slot) {
        if (slot < 0 || slot >= getSizeInventory()) { return ItemStack.EMPTY; }
        ItemStack taken = ItemStackHelper.getAndRemove(items, slot);
        markDirty();
        return taken;
    }

    @Override public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        if (slot < 0 || slot >= getSizeInventory()) { return; }
        items.set(slot, stack);
        if (stack.getCount() > getInventoryStackLimit()) { stack.setCount(getInventoryStackLimit()); }
        markDirty();
    }

    @Override public int getInventoryStackLimit() { return 64; }

    @Override public void markDirty() { if (store != null) { store.changed(owner); } }

    @Override public boolean isUsableByPlayer(@Nonnull EntityPlayer player) { return at == null || at.isUsableByPlayer(player); }

    @Override public void openInventory(@Nonnull EntityPlayer player) { if (at != null) { at.openInventory(player); } }

    @Override public void closeInventory(@Nonnull EntityPlayer player) {
        markDirty();
        if (at != null) { at.closeInventory(player); }
    }

    @Override public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) { return ContainerPack.storable(stack); }

    @Override public int getField(int id) { return 0; }

    @Override public void setField(int id, int value) {}

    @Override public int getFieldCount() { return 0; }

    @Override public void clear() {
        for (int slot = 0; slot < getSizeInventory(); slot++) { items.set(slot, ItemStack.EMPTY); }
        markDirty();
    }

    @Override @Nonnull public String getName() { return title.getUnformattedText(); }

    @Override public boolean hasCustomName() { return custom; }

    @Override @Nonnull public ITextComponent getDisplayName() { return title; }
}
