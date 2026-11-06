package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.gui.ContentContainerMenu;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;

import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public final class ContentPouchInventory extends SimpleContainer {
    public static final String HELD = "RdplHeld";
    private final ItemStack pouch;
    private boolean loading;

    public ContentPouchInventory(ItemStack pouch, ContainerDef def) {
        super(def.size());
        this.pouch = pouch;
        NonNullList<ItemStack> items = NonNullList.withSize(def.size(), ItemStack.EMPTY);
        load(pouch, items);
        loading = true;
        for (int at = 0; at < items.size(); at++) { setItem(at, items.get(at)); }
        loading = false;
    }

    public static void load(ItemStack stack, NonNullList<ItemStack> items) {
        CompoundTag held = stack.getTagElement(HELD);
        if (held != null) { ContainerHelper.loadAllItems(held, items); }
    }

    public static void store(ItemStack stack, NonNullList<ItemStack> items) {
        CompoundTag held = new CompoundTag();
        ContainerHelper.saveAllItems(held, items, true);
        stack.getOrCreateTag().put(HELD, held);
    }

    @Override public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) { return ContentContainerMenu.storable(stack); }

    @Override public void setChanged() {
        super.setChanged();
        if (loading) { return; }
        NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        for (int at = 0; at < getContainerSize(); at++) { items.set(at, getItem(at)); }
        store(pouch, items);
    }
}
