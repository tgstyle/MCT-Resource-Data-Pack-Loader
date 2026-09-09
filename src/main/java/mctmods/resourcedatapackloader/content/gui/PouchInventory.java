package mctmods.resourcedatapackloader.content.gui;

import mctmods.resourcedatapackloader.content.def.ContainerDef;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import javax.annotation.Nonnull;

public class PouchInventory implements IInventory {
    public static final String HELD = "RdplHeld";
    private final ItemStack pouch;
    private final NonNullList<ItemStack> items;
    private final String named;

    public PouchInventory(ItemStack pouch, ContainerDef def, String named) {
        this.pouch = pouch;
        this.named = named;
        this.items = NonNullList.withSize(def.size(), ItemStack.EMPTY);
        NBTTagCompound tag = pouch.getTagCompound();
        if (tag != null && tag.hasKey(HELD)) { ItemStackHelper.loadAllItems(tag.getCompoundTag(HELD), items); }
    }

    public void save() {
        NBTTagCompound tag = pouch.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            pouch.setTagCompound(tag);
        }
        NBTTagCompound held = new NBTTagCompound();
        ItemStackHelper.saveAllItems(held, items, true);
        tag.setTag(HELD, held);
    }

    public ItemStack pouch() { return pouch; }

    @Override public int getSizeInventory() { return items.size(); }

    @Override public boolean isEmpty() {
        for (ItemStack held : items) {
            if (!held.isEmpty()) { return false; }
        }
        return true;
    }

    @Override @Nonnull public ItemStack getStackInSlot(int slot) { return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY; }

    @Override @Nonnull public ItemStack decrStackSize(int slot, int amount) {
        ItemStack taken = ItemStackHelper.getAndSplit(items, slot, amount);
        if (!taken.isEmpty()) { markDirty(); }
        return taken;
    }

    @Override @Nonnull public ItemStack removeStackFromSlot(int slot) { return ItemStackHelper.getAndRemove(items, slot); }

    @Override public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        if (slot < 0 || slot >= items.size()) { return; }
        items.set(slot, stack);
        if (stack.getCount() > getInventoryStackLimit()) { stack.setCount(getInventoryStackLimit()); }
        markDirty();
    }

    @Override public int getInventoryStackLimit() { return 64; }

    @Override public void markDirty() { save(); }

    @Override public boolean isUsableByPlayer(@Nonnull EntityPlayer player) { return true; }

    @Override public void openInventory(@Nonnull EntityPlayer player) {}

    @Override public void closeInventory(@Nonnull EntityPlayer player) { save(); }

    @Override public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) { return !ContainerPack.nests(stack); }

    @Override public int getField(int id) { return 0; }

    @Override public void setField(int id, int value) {}

    @Override public int getFieldCount() { return 0; }

    @Override public void clear() { items.clear(); }

    @Override @Nonnull public String getName() { return named; }

    @Override public boolean hasCustomName() { return pouch.hasDisplayName(); }

    @Override @Nonnull public ITextComponent getDisplayName() {
        return pouch.hasDisplayName() ? new net.minecraft.util.text.TextComponentString(pouch.getDisplayName()) : new TextComponentTranslation(named);
    }
}
