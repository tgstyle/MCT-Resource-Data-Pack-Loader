package mctmods.resourcedatapackloader.content.menu;

import mctmods.resourcedatapackloader.content.ContentContainers;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.FilterDef;
import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.entity.EntityStorage;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EntityStorageMenu extends AbstractContainerMenu {
    private static final int FLUID = 0;
    private static final int FLUID_LOW = 1;
    private static final int ENERGY_LOW = 3;
    private static final int SHOWN = 5;
    private static final int HALF = 16;
    private static final int LOW = 0xFFFF;
    private static final int TOP = 18;
    private static final double REACH = 64.0D;
    private final StorageDef def;
    private final ContainerData shown = new SimpleContainerData(SHOWN);
    private final List<Identifier> fluids = new ArrayList<>(new TreeSet<>(BuiltInRegistries.FLUID.keySet()));
    private final ItemStacksResourceHandler items;
    @Nullable private final Entity entity;
    @Nullable private final EntityStorage storage;

    public EntityStorageMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) { this(id, inventory, inventory.player.level().getEntity(extra.readVarInt()), read(extra), false); }

    public EntityStorageMenu(int id, Inventory inventory, Entity entity, StorageDef def) { this(id, inventory, entity, def, true); }

    private EntityStorageMenu(int id, Inventory inventory, @Nullable Entity entity, StorageDef def, boolean serves) {
        super(ContentContainers.storageMenu(), id);
        this.def = def;
        this.entity = entity;
        EntityStorage held = EntityStorage.of(entity);
        ItemStacksResourceHandler own = held == null ? null : held.items();
        this.storage = serves ? held : null;
        this.items = own != null && own.size() == def.slots() ? own : new ItemStacksResourceHandler(def.slots());
        ContainerDef shape = shape(def);
        int wide = ContentContainerMenu.width(shape);
        int tall = ContentContainerMenu.height(shape);
        int left = (wide - def.columns() * ContentContainerMenu.SLOT) / 2 + 1;
        for (int slot = 0; slot < def.slots(); slot++) {
            addSlot(new ResourceHandlerSlot(items, items::set, slot, left + slot % def.columns() * ContentContainerMenu.SLOT, TOP + slot / def.columns() * ContentContainerMenu.SLOT));
        }
        int playerLeft = (wide - 9 * ContentContainerMenu.SLOT) / 2 + 1;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, playerLeft + column * ContentContainerMenu.SLOT, tall - 83 + row * ContentContainerMenu.SLOT));
            }
        }
        for (int hotbar = 0; hotbar < 9; hotbar++) { addSlot(new Slot(inventory, hotbar, playerLeft + hotbar * ContentContainerMenu.SLOT, tall - 25)); }
        addDataSlots(shown);
        read();
    }

    public static void write(RegistryFriendlyByteBuf extra, Entity entity, StorageDef def) {
        extra.writeVarInt(entity.getId());
        extra.writeVarInt(def.rows());
        extra.writeVarInt(def.columns());
        extra.writeVarInt(def.fluidCapacity());
        extra.writeVarInt(def.energyCapacity());
    }

    private static StorageDef read(RegistryFriendlyByteBuf extra) {
        int rows = Mth.clamp(extra.readVarInt(), 0, ContainerDef.MOST_ROWS);
        int columns = Mth.clamp(extra.readVarInt(), 1, ContainerDef.MOST_COLUMNS);
        return new StorageDef(rows, columns, FilterDef.NONE, Math.max(0, extra.readVarInt()), FilterDef.NONE, Math.max(0, extra.readVarInt()), 0, false, false);
    }

    public static ContainerDef shape(StorageDef def) { return ContentContainers.FALLBACK.sized(def.bands(), def.columns()); }

    public StorageDef def() { return def; }

    public Fluid fluid() {
        int index = shown.get(FLUID) & LOW;
        return index == 0 || index > fluids.size() ? Fluids.EMPTY : BuiltInRegistries.FLUID.getValue(fluids.get(index - 1));
    }

    public int fluidAmount() { return whole(FLUID_LOW); }

    public int energy() { return whole(ENERGY_LOW); }

    private int whole(int low) { return shown.get(low) & LOW | shown.get(low + 1) << HALF; }

    private void split(int low, int value) {
        shown.set(low, value & LOW);
        shown.set(low + 1, value >>> HALF);
    }

    private void read() {
        if (storage == null) { return; }
        int amount = storage.fluidAmount();
        shown.set(FLUID, amount <= 0 ? 0 : fluids.indexOf(BuiltInRegistries.FLUID.getKey(storage.fluid())) + 1);
        split(FLUID_LOW, amount);
        split(ENERGY_LOW, storage.energyStored());
    }

    @Override public void broadcastChanges() {
        read();
        super.broadcastChanges();
    }

    @Override public boolean stillValid(@Nonnull Player player) { return entity == null || entity.isAlive() && player.distanceToSqr(entity) <= REACH; }

    @Override @Nonnull public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) { return ItemStack.EMPTY; }
        ItemStack stack = slot.getItem();
        if (index < def.slots()) { moveItemStackTo(stack, def.slots(), slots.size(), true); }
        else { stack.shrink(ResourceHandlerUtil.insertStacking(items, ItemResource.of(stack), stack.getCount(), null)); }
        if (stack.isEmpty()) { slot.set(ItemStack.EMPTY); }
        else { slot.setChanged(); }
        return ItemStack.EMPTY;
    }
}
