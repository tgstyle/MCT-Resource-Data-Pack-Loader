package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.gui.ContentContainerMenu;
import mctmods.resourcedatapackloader.common.pack.definitions.containers.ContainerDef;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ContentEnderInventory implements WorldlyContainer {
    public static final int STORE = ContainerDef.MOST_ROWS * ContainerDef.MOST_COLUMNS;
    public static final ContentEnderInventory NONE = new ContentEnderInventory();
    @Nullable private final ContentEnderStore store;
    @Nullable private final UUID owner;
    private final NonNullList<ItemStack> items;
    private final int size;
    @Nullable private final Container at;

    private ContentEnderInventory() {
        this.store = null;
        this.owner = null;
        this.items = NonNullList.create();
        this.size = 0;
        this.at = null;
    }

    public ContentEnderInventory(Level level, @Nullable UUID owner, ContainerDef def, @Nullable Container at) {
        this.store = ContentEnderStore.get(Objects.requireNonNull(level.getServer()));
        this.owner = owner;
        this.items = store.items(owner);
        this.size = Math.min(STORE, def.size());
        this.at = at;
    }

    public static int[] every(int size) { return IntStream.range(0, size).toArray(); }

    public int version() { return store == null || owner == null ? 0 : store.version(owner); }

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

    @Override public void setChanged() { if (store != null && owner != null) { store.changed(owner); } }

    @Override @Nonnull public int[] getSlotsForFace(@Nonnull Direction side) { return every(size); }

    @Override public boolean canPlaceItemThroughFace(int slot, @Nonnull ItemStack stack, @Nullable Direction side) { return canPlaceItem(slot, stack); }

    @Override public boolean canTakeItemThroughFace(int slot, @Nonnull ItemStack stack, @Nonnull Direction side) { return true; }

    @Override public boolean stillValid(@Nonnull Player player) { return at == null || at.stillValid(player); }

    @Override public void startOpen(@Nonnull ContainerUser user) { if (at != null) { at.startOpen(user); } }

    @Override public void stopOpen(@Nonnull ContainerUser user) {
        setChanged();
        if (at != null) { at.stopOpen(user); }
    }

    @Override public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) { return ContentContainerMenu.storable(stack); }

    @Override public void clearContent() {
        for (int slot = 0; slot < size; slot++) { items.set(slot, ItemStack.EMPTY); }
        setChanged();
    }
}
