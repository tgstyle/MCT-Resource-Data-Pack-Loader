package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.gui.ContentContainerMenu;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ContentEnderInventory implements Container {
    public static final int STORE = ContainerDef.MOST_ROWS * ContainerDef.MOST_COLUMNS;
    private final ContentEnderStore store;
    private final UUID owner;
    private final NonNullList<ItemStack> items;
    private final int size;
    @Nullable private final Container at;

    public ContentEnderInventory(Level level, UUID owner, ContainerDef def, @Nullable Container at) {
        this.store = ContentEnderStore.get(Objects.requireNonNull(level.getServer()));
        this.owner = owner;
        this.items = store.items(owner);
        this.size = Math.min(STORE, def.size());
        this.at = at;
    }

    public int version() { return store.version(owner); }

    @Override public int getContainerSize() { return size; }

    @Override public boolean isEmpty() {
        for (int slot = 0; slot < size; slot++) {
            if (!items.get(slot).isEmpty()) { return false; }
        }
        return true;
    }

    @Override @Nonnull public ItemStack getItem(int slot) { return slot >= 0 && slot < size ? items.get(slot) : ItemStack.EMPTY; }

    @Override @Nonnull public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= size) { return ItemStack.EMPTY; }
        ItemStack taken = ContainerHelper.removeItem(items, slot, amount);
        if (!taken.isEmpty()) { setChanged(); }
        return taken;
    }

    @Override @Nonnull public ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= size) { return ItemStack.EMPTY; }
        ItemStack taken = ContainerHelper.takeItem(items, slot);
        setChanged();
        return taken;
    }

    @Override public void setItem(int slot, @Nonnull ItemStack stack) {
        if (slot < 0 || slot >= size) { return; }
        items.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) { stack.setCount(getMaxStackSize()); }
        setChanged();
    }

    @Override public void setChanged() { store.changed(owner); }

    @Override public boolean stillValid(@Nonnull Player player) { return at == null || at.stillValid(player); }

    @Override public void startOpen(@Nonnull Player player) { if (at != null) { at.startOpen(player); } }

    @Override public void stopOpen(@Nonnull Player player) {
        setChanged();
        if (at != null) { at.stopOpen(player); }
    }

    @Override public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) { return ContentContainerMenu.storable(stack); }

    @Override public void clearContent() {
        for (int slot = 0; slot < size; slot++) { items.set(slot, ItemStack.EMPTY); }
        setChanged();
    }
}
