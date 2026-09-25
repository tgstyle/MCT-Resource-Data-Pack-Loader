package mctmods.resourcedatapackloader.content.menu;

import mctmods.resourcedatapackloader.content.ContentContainers;
import mctmods.resourcedatapackloader.content.block.ContentContainerBlock;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.item.ContentContainerItem;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentContainerMenu extends AbstractContainerMenu {
    public static final int SLOT = 18;
    private final Container held;
    private final ContainerDef def;
    private final ItemStack open;
    @Nullable private final Supplier<ItemStack> source;
    private final int worn;

    public ContentContainerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
        this(id, inventory, read(extra), extra.readVarInt());
    }

    private ContentContainerMenu(int id, Inventory inventory, ContainerDef def, int worn) {
        this(id, inventory, new SimpleContainer(def.size()), def, ItemStack.EMPTY, null, worn);
    }

    public ContentContainerMenu(int id, Inventory inventory, Container held, ContainerDef def) {
        this(id, inventory, held, def, ItemStack.EMPTY, null, -1);
    }

    public ContentContainerMenu(int id, Inventory inventory, Container held, ContainerDef def, ItemStack open, @Nullable Supplier<ItemStack> source, int worn) {
        super(ContentContainers.menu(), id);
        this.open = open;
        this.held = held;
        this.def = def;
        this.source = source;
        this.worn = worn;
        held.startOpen(inventory.player);
        int left = left(def);
        int top = 18;
        for (int row = 0; row < def.rows(); row++) {
            for (int column = 0; column < def.columns(); column++) {
                addSlot(new Slot(held, row * def.columns() + column, left + column * SLOT, top + row * SLOT) {
                    @Override public boolean mayPlace(@Nonnull ItemStack stack) { return storable(stack); }
                });
            }
        }
        int playerLeft = (width(def) - 9 * SLOT) / 2 + 1;
        int playerTop = height(def) - 83;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(locking(inventory, column + row * 9 + 9, playerLeft + column * SLOT, playerTop + row * SLOT));
            }
        }
        for (int column = 0; column < 9; column++) { addSlot(locking(inventory, column, playerLeft + column * SLOT, height(def) - 25)); }
    }

    public static void write(RegistryFriendlyByteBuf extra, ContainerDef def) { write(extra, def, -1); }

    public static void write(RegistryFriendlyByteBuf extra, ContainerDef def, int worn) {
        extra.writeVarInt(def.rows());
        extra.writeVarInt(def.columns());
        extra.writeBoolean(def.guiTexture() != null);
        if (def.guiTexture() != null) {
            extra.writeResourceLocation(def.guiTexture());
            extra.writeVarInt(def.guiWidth());
            extra.writeVarInt(def.guiHeight());
        }
        extra.writeVarInt(worn);
    }

    private static ContainerDef read(RegistryFriendlyByteBuf extra) {
        int rows = Mth.clamp(extra.readVarInt(), 1, ContainerDef.MOST_ROWS);
        int columns = Mth.clamp(extra.readVarInt(), 1, ContainerDef.MOST_COLUMNS);
        if (!extra.readBoolean()) { return new ContainerDef(rows, columns, "", false, null, null, 0, 0, ""); }
        return new ContainerDef(rows, columns, "", false, null, extra.readResourceLocation(), extra.readVarInt(), extra.readVarInt(), "");
    }

    public static boolean storable(ItemStack stack) {
        if (stack.isEmpty()) { return true; }
        if (stack.getItem() instanceof ContentContainerItem) { return false; }
        return !(stack.getItem() instanceof BlockItem placed && placed.getBlock() instanceof ContentContainerBlock);
    }

    private Slot locking(Inventory inventory, int index, int x, int y) {
        return new Slot(inventory, index, x, y) {
            @Override public boolean mayPickup(@Nonnull Player player) { return open.isEmpty() || getItem() != open; }

            @Override public boolean mayPlace(@Nonnull ItemStack stack) { return open.isEmpty() || getItem() != open; }
        };
    }

    public static int width(ContainerDef def) { return Math.max(176, def.columns() * SLOT + 14); }

    public static int height(ContainerDef def) { return 114 + def.rows() * SLOT; }

    private static int left(ContainerDef def) { return (width(def) - def.columns() * SLOT) / 2 + 1; }

    public ContainerDef def() { return def; }

    public int worn() { return worn; }

    @Override public boolean stillValid(@Nonnull Player player) { return source == null ? held.stillValid(player) : source.get() == open; }

    @Override @Nonnull public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        int size = held.getContainerSize();
        Slot slot = slots.get(index);
        if (!slot.hasItem()) { return ItemStack.EMPTY; }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (!slot.mayPickup(player)) { return ItemStack.EMPTY; }
        if (index < size) {
            if (!moveItemStackTo(stack, size, slots.size(), true)) { return ItemStack.EMPTY; }
        }
        else if (!moveItemStackTo(stack, 0, size, false)) { return ItemStack.EMPTY; }
        if (stack.isEmpty()) { slot.set(ItemStack.EMPTY); }
        else { slot.setChanged(); }
        return copy;
    }

    @Override public void removed(@Nonnull Player player) {
        super.removed(player);
        held.stopOpen(player);
    }
}
