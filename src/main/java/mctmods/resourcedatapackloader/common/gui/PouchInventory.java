package mctmods.resourcedatapackloader.common.gui;

import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;
import mctmods.resourcedatapackloader.common.util.StackLists;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class PouchInventory implements IInventory {
    public static final String HELD = "RdplHeld";
    private final ItemStack pouch;
    private final NonNullList<ItemStack> items;
    private final String named;

    public PouchInventory(ItemStack pouch, ContainerDef def, String named) {
        this.pouch = pouch;
        this.named = named;
        this.items = NonNullList.withSize(def.size(), ItemStack.EMPTY);
        load(pouch, items);
    }

    public static void load(ItemStack stack, NonNullList<ItemStack> items) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(HELD)) { ItemStackHelper.loadAllItems(tag.getCompoundTag(HELD), items); }
    }

    public static void store(ItemStack stack, NonNullList<ItemStack> items) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        NBTTagCompound held = new NBTTagCompound();
        ItemStackHelper.saveAllItems(held, items, true);
        tag.setTag(HELD, held);
    }

    public void save() { store(pouch, items); }

    public ItemStack pouch() { return pouch; }

    @Override public int getSizeInventory() { return items.size(); }

    @Override public boolean isEmpty() { return StackLists.empty(items, items.size()); }

    @Override @Nonnull public ItemStack getStackInSlot(int slot) { return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY; }

    @Override @Nonnull public ItemStack decrStackSize(int slot, int amount) { return StackLists.split(this, items, slot, amount); }

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

    @Override public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) { return ContainerPack.storable(stack); }

    @Override public int getField(int id) { return 0; }

    @Override public void setField(int id, int value) {}

    @Override public int getFieldCount() { return 0; }

    @Override public void clear() { items.clear(); }

    @Override @Nonnull public String getName() { return named; }

    @Override public boolean hasCustomName() { return pouch.hasDisplayName(); }

    public static ITextComponent title(ItemStack pouch, String named) { return pouch.hasDisplayName() ? new TextComponentString(pouch.getDisplayName()) : new TextComponentTranslation(named); }

    @Override @Nonnull public ITextComponent getDisplayName() { return title(pouch, named); }
}
