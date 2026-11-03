package mctmods.resourcedatapackloader.common.containers;

import mctmods.resourcedatapackloader.common.entities.orders.OrderItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

final class StorageContents {
    private static final List<EnumFacing> SIDES = Collections.unmodifiableList(Arrays.asList(null, EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST));
    private static final String CAPITAL_COUNT = "Count";
    private static final String COUNT = "count";
    private static final String ID = "id";

    private StorageContents() {}

    static Map<String, Long> census(NBTTagCompound root) {
        Map<String, Long> totals = new HashMap<>();
        eachStack(root, stack -> totals.merge(new ResourceLocation(stack.getString(ID)).toString(), (long) count(stack), Long::sum));
        return totals;
    }

    static List<ItemStack> stacksIn(NBTTagCompound root) {
        List<ItemStack> found = new ArrayList<>();
        eachStack(root, tag -> {
            NBTTagCompound copy = tag.copy();
            copy.setByte(CAPITAL_COUNT, (byte) 1);
            ItemStack stack = new ItemStack(copy);
            stack.setCount(count(tag));
            found.add(stack);
        });
        return found;
    }

    private static void eachStack(NBTTagCompound root, Consumer<NBTTagCompound> found) {
        for (String key : root.getKeySet()) { walk(root.getTag(key), found); }
    }

    private static void walk(NBTBase tag, Consumer<NBTTagCompound> found) {
        if (tag instanceof NBTTagList) {
            NBTTagList list = (NBTTagList) tag;
            for (int index = 0; index < list.tagCount(); index++) { walk(list.get(index), found); }
            return;
        }
        if (!(tag instanceof NBTTagCompound)) { return; }
        NBTTagCompound compound = (NBTTagCompound) tag;
        if (isStack(compound)) { found.accept(compound); }
        else { eachStack(compound, found); }
    }

    private static boolean isStack(NBTTagCompound tag) {
        if (!tag.hasKey(ID, Constants.NBT.TAG_STRING) || count(tag) <= 0) { return false; }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.getString(ID)));
        return item != null && item != Items.AIR;
    }

    private static int count(NBTTagCompound tag) { return tag.hasKey(CAPITAL_COUNT, Constants.NBT.TAG_ANY_NUMERIC) ? tag.getInteger(CAPITAL_COUNT) : tag.getInteger(COUNT); }

    static Map<String, Long> totals(List<ItemStack> stacks) {
        Map<String, Long> totals = new HashMap<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) { totals.merge(String.valueOf(stack.getItem().getRegistryName()), (long) stack.getCount(), Long::sum); }
        }
        return totals;
    }

    static List<ItemStack> shown(IItemHandler handler) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int slot = 0; slot < handler.getSlots(); slot++) { stacks.add(handler.getStackInSlot(slot).copy()); }
        return stacks;
    }

    static boolean linked(TileEntity tile) {
        IItemHandler own = own(tile);
        IItemHandler whole = onSide(tile, null);
        return own != null && whole != null && slotsOf(whole) > own.getSlots() && paired(tile);
    }

    private static boolean paired(TileEntity tile) {
        if (!(tile instanceof TileEntityChest)) { return true; }
        TileEntityChest chest = (TileEntityChest) tile;
        chest.checkForAdjacentChests();
        return chest.adjacentChestXNeg != null || chest.adjacentChestXPos != null || chest.adjacentChestZNeg != null || chest.adjacentChestZPos != null;
    }

    static Source find(TileEntity tile, NBTTagCompound before, boolean linked, boolean strict) {
        IItemHandler own = own(tile);
        List<IItemHandler> tried = linked ? Collections.singletonList(own) : candidates(tile, own);
        Map<String, Long> tagged = census(before);
        boolean elsewhere = false;
        for (IItemHandler handler : tried) {
            if (!strict) { return new Source(handler, ""); }
            Map<String, Long> totals;
            try { totals = totals(shown(handler)); }
            catch (RuntimeException ex) { continue; }
            if (totals.equals(tagged)) { return new Source(handler, ""); }
            elsewhere |= exceeds(totals, tagged);
        }
        if (tried.isEmpty()) { return new Source(null, "it has no inventory to reach"); }
        return new Source(null, elsewhere ? "contents live elsewhere" : "holds items it does not show");
    }

    private static boolean exceeds(Map<String, Long> shown, Map<String, Long> tagged) {
        for (Map.Entry<String, Long> entry : shown.entrySet()) {
            if (entry.getValue() > tagged.getOrDefault(entry.getKey(), 0L)) { return true; }
        }
        return false;
    }

    private static List<IItemHandler> candidates(TileEntity tile, @Nullable IItemHandler own) {
        List<IItemHandler> found = new ArrayList<>();
        addCandidate(found, onSide(tile, null), own);
        addCandidate(found, own, own);
        for (EnumFacing side : EnumFacing.VALUES) { addCandidate(found, onSide(tile, side), own); }
        return found;
    }

    private static void addCandidate(List<IItemHandler> found, @Nullable IItemHandler handler, @Nullable IItemHandler own) {
        if (handler == null) { return; }
        int slots = slotsOf(handler);
        if (slots >= 0 && (own == null || slots <= own.getSlots())) { found.add(handler); }
    }

    private static int slotsOf(IItemHandler handler) {
        try { return handler.getSlots(); }
        catch (RuntimeException ex) { return -1; }
    }

    @Nullable private static IItemHandler own(TileEntity tile) { return tile instanceof IInventory && ((IInventory) tile).getSizeInventory() > 0 ? new InvWrapper((IInventory) tile) : null; }

    @Nullable private static IItemHandler onSide(TileEntity tile, @Nullable EnumFacing side) {
        try {
            if (side == null) { return OrderItems.handler(tile.getWorld(), tile.getPos()); }
            return tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side) ? tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side) : null;
        }
        catch (RuntimeException ex) { return null; }
    }

    @Nullable static IItemHandler forProbe(TileEntity probe) {
        IItemHandler own = own(probe);
        if (own != null) { return own; }
        return probe.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) ? probe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) : null;
    }

    @Nullable static IItemHandler forFill(TileEntity placed) {
        IItemHandler own = own(placed);
        IItemHandler whole = onSide(placed, null);
        return own != null && (whole == null || slotsOf(whole) != own.getSlots()) ? own : whole;
    }

    static boolean withholds(IItemHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack held = handler.getStackInSlot(slot);
            if (held.isEmpty()) { continue; }
            int asked = Math.min(held.getCount(), held.getMaxStackSize());
            ItemStack taken = handler.extractItem(slot, asked, true);
            if (taken.getCount() != asked || !ItemHandlerHelper.canItemStacksStack(taken, held)) { return true; }
        }
        return false;
    }

    static boolean drain(IItemHandler handler, List<ItemStack> held) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            for (ItemStack stack = handler.getStackInSlot(slot); !stack.isEmpty(); stack = handler.getStackInSlot(slot)) {
                int was = stack.getCount();
                ItemStack taken = handler.extractItem(slot, was, false);
                if (taken.isEmpty()) { return false; }
                held.add(taken.copy());
                ItemStack left = handler.getStackInSlot(slot);
                if (!left.isEmpty() && left.getCount() >= was) { return false; }
            }
        }
        return true;
    }

    static boolean holdsFluid(TileEntity tile) {
        for (EnumFacing side : SIDES) {
            try {
                if (!tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) { continue; }
                IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
                if (handler == null) { continue; }
                for (IFluidTankProperties tank : handler.getTankProperties()) {
                    FluidStack held = tank.getContents();
                    if (held != null && held.amount > 0) { return true; }
                }
            }
            catch (RuntimeException ex) { return true; }
        }
        return false;
    }

    static boolean savesFluid(NBTTagCompound root) {
        for (String key : root.getKeySet()) {
            NBTBase tag = root.getTag(key);
            if (!(tag instanceof NBTTagCompound)) { continue; }
            NBTTagCompound compound = (NBTTagCompound) tag;
            if (compound.hasKey("FluidName", Constants.NBT.TAG_STRING) && compound.getInteger("Amount") > 0 || savesFluid(compound)) { return true; }
        }
        return false;
    }

    static boolean shows(Slot slot, TileEntity tile) {
        if (slot.inventory == tile) { return true; }
        if (tile instanceof IInventory && slot.inventory instanceof InventoryLargeChest && ((InventoryLargeChest) slot.inventory).isPartOfLargeChest((IInventory) tile)) { return true; }
        if (!(slot instanceof SlotItemHandler)) { return false; }
        IItemHandler open = ((SlotItemHandler) slot).getItemHandler();
        for (EnumFacing side : SIDES) {
            if (open.equals(onSide(tile, side))) { return true; }
        }
        return false;
    }

    @Nullable static List<Placed> plan(IItemHandler into, List<ItemStack> shown) {
        boolean[] used = new boolean[into.getSlots()];
        List<Placed> placed = new ArrayList<>();
        List<ItemStack> waiting = new ArrayList<>();
        for (int slot = 0; slot < shown.size(); slot++) {
            if (shown.get(slot).isEmpty()) { continue; }
            ItemStack left = planInto(into, used, slot, shown.get(slot), placed);
            if (!left.isEmpty()) { waiting.add(left); }
        }
        for (ItemStack stack : waiting) {
            ItemStack left = stack;
            for (int slot = 0; slot < used.length && !left.isEmpty(); slot++) { left = planInto(into, used, slot, left, placed); }
            if (!left.isEmpty()) { return null; }
        }
        return placed;
    }

    private static ItemStack planInto(IItemHandler into, boolean[] used, int slot, ItemStack stack, List<Placed> placed) {
        if (slot >= used.length || used[slot]) { return stack; }
        ItemStack left = into.insertItem(slot, stack.copy(), true);
        int taken = stack.getCount() - left.getCount();
        if (taken <= 0) { return stack; }
        used[slot] = true;
        ItemStack part = stack.copy();
        part.setCount(taken);
        placed.add(new Placed(slot, part));
        return left;
    }

    static void fill(IItemHandler into, @Nullable List<Placed> plan, List<ItemStack> held) {
        List<ItemStack> pool = new ArrayList<>();
        for (ItemStack stack : held) { pool.add(stack.copy()); }
        if (plan != null) {
            for (Placed placed : plan) { insertPlanned(into, placed, pool); }
        }
        for (ItemStack stack : pool) {
            if (stack.isEmpty()) { continue; }
            if (!ItemHandlerHelper.insertItemStacked(into, stack, false).isEmpty()) { throw new IllegalStateException("the target took less than it promised"); }
        }
    }

    private static void insertPlanned(IItemHandler into, Placed placed, List<ItemStack> pool) {
        for (ItemStack stack : pool) {
            if (stack.isEmpty() || !ItemHandlerHelper.canItemStacksStack(stack, placed.stack)) { continue; }
            ItemStack part = stack.splitStack(Math.min(stack.getCount(), placed.stack.getCount()));
            stack.grow(into.insertItem(placed.slot, part, false).getCount());
            return;
        }
    }

    static final class Source {
        @Nullable final IItemHandler handler;
        final String why;

        private Source(@Nullable IItemHandler handler, String why) {
            this.handler = handler;
            this.why = why;
        }
    }

    static final class Placed {
        private final int slot;
        private final ItemStack stack;

        private Placed(int slot, ItemStack stack) {
            this.slot = slot;
            this.stack = stack;
        }
    }
}
