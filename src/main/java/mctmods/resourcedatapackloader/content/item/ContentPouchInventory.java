package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public final class ContentPouchInventory extends SimpleContainer {
    private final ItemStack pouch;
    private boolean loading;

    public ContentPouchInventory(ItemStack pouch, ContainerDef def) {
        super(def.size());
        this.pouch = pouch;
        NonNullList<ItemStack> items = NonNullList.withSize(def.size(), ItemStack.EMPTY);
        pouch.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        loading = true;
        for (int at = 0; at < items.size(); at++) { setItem(at, items.get(at)); }
        loading = false;
    }

    @Override public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) { return ContentContainerMenu.storable(stack); }

    @Override public void setChanged() {
        super.setChanged();
        if (loading) { return; }
        List<ItemStack> items = new ArrayList<>(getContainerSize());
        for (int at = 0; at < getContainerSize(); at++) { items.add(getItem(at)); }
        pouch.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }
}
