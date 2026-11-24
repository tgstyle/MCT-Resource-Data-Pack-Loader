package mctmods.resourcedatapackloader.common.gui;

import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class PouchInventory extends StackListInventory {
    public static final String HELD = "RdplHeld";
    private final ItemStack pouch;
    private final String named;

    public PouchInventory(ItemStack pouch, ContainerDef def, String named) {
        super(NonNullList.withSize(def.size(), ItemStack.EMPTY));
        this.pouch = pouch;
        this.named = named;
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

    @Override @Nonnull public ItemStack removeStackFromSlot(int slot) { return ItemStackHelper.getAndRemove(items, slot); }

    @Override public void clear() { items.clear(); }

    @Override public void markDirty() { save(); }

    @Override public boolean isUsableByPlayer(@Nonnull EntityPlayer player) { return true; }

    @Override public void openInventory(@Nonnull EntityPlayer player) {}

    @Override public void closeInventory(@Nonnull EntityPlayer player) { save(); }

    @Override @Nonnull public String getName() { return named; }

    @Override public boolean hasCustomName() { return pouch.hasDisplayName(); }

    public static ITextComponent title(ItemStack pouch, String named) { return pouch.hasDisplayName() ? new TextComponentString(pouch.getDisplayName()) : new TextComponentTranslation(named); }

    @Override @Nonnull public ITextComponent getDisplayName() { return title(pouch, named); }
}
