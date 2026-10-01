package mctmods.resourcedatapackloader.content.gui;

import mctmods.resourcedatapackloader.content.def.StorageDef;
import mctmods.resourcedatapackloader.content.entity.EntityStorage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerEntityStorage extends Container {
    private static final int FLUID = 0;
    private static final int FLUID_LOW = 1;
    private static final int FLUID_HIGH = 2;
    private static final int ENERGY_LOW = 3;
    private static final int ENERGY_HIGH = 4;
    private static final int HALF = 16;
    private static final int LOW = 0xFFFF;
    private static final double REACH = 64.0D;
    public final StorageDef def;
    private final Entity entity;
    private final int[] shown = new int[ENERGY_HIGH + 1];
    private final int[] sent = new int[ENERGY_HIGH + 1];
    private final List<String> fluids = new ArrayList<>(new TreeSet<>(FluidRegistry.getRegisteredFluids().keySet()));
    @Nullable private final EntityStorage storage;
    @Nullable private final IItemHandler items;

    public ContainerEntityStorage(InventoryPlayer player, Entity entity, StorageDef def) {
        this.entity = entity;
        this.def = def;
        this.storage = EntityStorage.of(entity);
        this.items = storage == null ? null : storage.items();
        int wide = ContainerPack.width(def.columns);
        int tall = ContainerPack.height(bands());
        int left = (wide - def.columns * ContainerPack.SLOT) / 2 + 1;
        for (int slot = 0; items != null && slot < def.slots(); slot++) {
            addSlotToContainer(new SlotItemHandler(items, slot, left + slot % def.columns * ContainerPack.SLOT, ContainerPack.HEADER + 1 + slot / def.columns * ContainerPack.SLOT));
        }
        int playerLeft = (wide - 9 * ContainerPack.SLOT) / 2 + 1;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlotToContainer(new Slot(player, column + row * 9 + 9, playerLeft + column * ContainerPack.SLOT, tall - 83 + row * ContainerPack.SLOT));
            }
        }
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            addSlotToContainer(new Slot(player, hotbar, playerLeft + hotbar * ContainerPack.SLOT, tall - 25));
        }
    }

    public int bands() { return def.rows + def.gauges(); }

    @Nullable public String fluid() { return shown[FLUID] <= 0 || shown[FLUID] > fluids.size() ? null : fluids.get(shown[FLUID] - 1); }

    public int fluidAmount() { return whole(FLUID_LOW); }

    public int energy() { return whole(ENERGY_LOW); }

    private int whole(int low) { return shown[low] & LOW | shown[low + 1] << HALF; }

    private void read() {
        IFluidHandler tank = storage == null ? null : storage.tank();
        IFluidTankProperties[] tanks = tank == null ? new IFluidTankProperties[0] : tank.getTankProperties();
        FluidStack held = tanks.length == 0 ? null : tanks[0].getContents();
        IEnergyStorage energy = storage == null ? null : storage.energy();
        int stored = energy == null ? 0 : energy.getEnergyStored();
        shown[FLUID] = held == null ? 0 : fluids.indexOf(held.getFluid().getName()) + 1;
        shown[FLUID_LOW] = held == null ? 0 : held.amount & LOW;
        shown[FLUID_HIGH] = held == null ? 0 : held.amount >>> HALF;
        shown[ENERGY_LOW] = stored & LOW;
        shown[ENERGY_HIGH] = stored >>> HALF;
    }

    @Override public void addListener(@Nonnull IContainerListener listener) {
        super.addListener(listener);
        read();
        for (int id = 0; id < shown.length; id++) { listener.sendWindowProperty(this, id, shown[id]); }
    }

    @Override public void detectAndSendChanges() {
        super.detectAndSendChanges();
        read();
        for (int id = 0; id < shown.length; id++) {
            if (sent[id] == shown[id]) { continue; }
            sent[id] = shown[id];
            for (IContainerListener listener : listeners) { listener.sendWindowProperty(this, id, shown[id]); }
        }
    }

    @Override @SideOnly(Side.CLIENT) public void updateProgressBar(int id, int data) {
        if (id >= 0 && id < shown.length) { shown[id] = data & LOW; }
    }

    @Override public boolean canInteractWith(@Nonnull EntityPlayer player) { return entity.isEntityAlive() && player.getDistanceSq(entity) <= REACH; }

    @Override @Nonnull public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int index) {
        Slot slot = inventorySlots.get(index);
        if (items == null || slot == null || !slot.getHasStack()) { return ItemStack.EMPTY; }
        ItemStack stack = slot.getStack();
        if (index < def.slots()) { mergeItemStack(stack, def.slots(), inventorySlots.size(), true); }
        else { stack.setCount(ItemHandlerHelper.insertItemStacked(items, stack.copy(), false).getCount()); }
        if (stack.isEmpty()) { slot.putStack(ItemStack.EMPTY); }
        else { slot.onSlotChanged(); }
        return ItemStack.EMPTY;
    }
}
