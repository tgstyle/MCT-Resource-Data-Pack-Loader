package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.def.ContainerDef;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class ContentPouchInventory extends SimpleContainer {
    public static final String HELD = "RdplHeld";
    private final ItemStack pouch;
    private boolean loading;

    public ContentPouchInventory(ItemStack pouch, ContainerDef def) {
        super(def.size());
        this.pouch = pouch;
        CompoundTag tag = pouch.getTag();
        if (tag == null || !tag.contains(HELD)) { return; }
        NonNullList<ItemStack> items = NonNullList.withSize(def.size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag.getCompound(HELD), items);
        loading = true;
        for (int at = 0; at < items.size(); at++) { setItem(at, items.get(at)); }
        loading = false;
    }

    @Override public void setChanged() {
        super.setChanged();
        if (loading) { return; }
        NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        for (int at = 0; at < getContainerSize(); at++) { items.set(at, getItem(at)); }
        CompoundTag held = new CompoundTag();
        ContainerHelper.saveAllItems(held, items, true);
        pouch.getOrCreateTag().put(HELD, held);
    }
}
