package mctmods.resourcedatapackloader.common.containers;

import mctmods.resourcedatapackloader.common.entities.orders.OrderItems;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ForgeRegistries;

final class StorageContents {
    private static final List<Direction> SIDES = Collections.unmodifiableList(Arrays.asList(null, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));
    private static final String CAPITAL_COUNT = "Count";
    private static final String COUNT = "count";
    private static final String ID = "id";

    private StorageContents() {}

    static Map<String, Long> census(CompoundTag root) {
        Map<String, Long> totals = new HashMap<>();
        eachStack(root, stack -> totals.merge(String.valueOf(ResourceLocation.tryParse(stack.getString(ID))), (long) count(stack), Long::sum));
        return totals;
    }

    static List<ItemStack> stacksIn(CompoundTag root) {
        List<ItemStack> found = new ArrayList<>();
        eachStack(root, tag -> {
            CompoundTag copy = tag.copy();
            copy.putByte(CAPITAL_COUNT, (byte) 1);
            ItemStack stack = ItemStack.of(copy);
            stack.setCount(count(tag));
            found.add(stack);
        });
        return found;
    }

    private static void eachStack(CompoundTag root, Consumer<CompoundTag> found) {
        for (String key : root.getAllKeys()) { walk(root.get(key), found); }
    }

    private static void walk(@Nullable Tag tag, Consumer<CompoundTag> found) {
        if (tag instanceof ListTag list) {
            for (Tag entry : list) { walk(entry, found); }
            return;
        }
        if (!(tag instanceof CompoundTag compound)) { return; }
        if (isStack(compound)) { found.accept(compound); }
        else { eachStack(compound, found); }
    }

    private static boolean isStack(CompoundTag tag) {
        if (!tag.contains(ID, Tag.TAG_STRING) || count(tag) <= 0) { return false; }
        Item item = Registered.find(ForgeRegistries.ITEMS, ResourceLocation.tryParse(tag.getString(ID)));
        return item != null && item != Items.AIR;
    }

    private static int count(CompoundTag tag) { return tag.contains(CAPITAL_COUNT, Tag.TAG_ANY_NUMERIC) ? tag.getInt(CAPITAL_COUNT) : tag.getInt(COUNT); }

    static Map<String, Long> totals(List<ItemStack> stacks) {
        Map<String, Long> totals = new HashMap<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) { totals.merge(String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem())), (long) stack.getCount(), Long::sum); }
        }
        return totals;
    }

    static List<ItemStack> shown(IItemHandler handler) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int slot = 0; slot < handler.getSlots(); slot++) { stacks.add(handler.getStackInSlot(slot).copy()); }
        return stacks;
    }

    static boolean linked(BlockEntity tile) {
        IItemHandler own = own(tile);
        IItemHandler whole = onSide(tile, null);
        return own != null && whole != null && slotsOf(whole) > own.getSlots();
    }

    static Source find(BlockEntity tile, CompoundTag before, boolean linked, boolean strict) {
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

    private static List<IItemHandler> candidates(BlockEntity tile, @Nullable IItemHandler own) {
        List<IItemHandler> found = new ArrayList<>();
        addCandidate(found, onSide(tile, null), own);
        addCandidate(found, own, own);
        for (Direction side : Direction.values()) { addCandidate(found, onSide(tile, side), own); }
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

    @Nullable private static IItemHandler own(BlockEntity tile) { return tile instanceof Container container && container.getContainerSize() > 0 ? new InvWrapper(container) : null; }

    @Nullable private static IItemHandler onSide(BlockEntity tile, @Nullable Direction side) {
        try {
            if (side == null && tile.getLevel() != null) { return OrderItems.handler(tile.getLevel(), tile.getBlockPos()); }
            return tile.getCapability(ForgeCapabilities.ITEM_HANDLER, side).resolve().orElse(null);
        }
        catch (RuntimeException ex) { return null; }
    }

    @Nullable static IItemHandler forProbe(BlockEntity probe) {
        IItemHandler own = own(probe);
        return own != null ? own : probe.getCapability(ForgeCapabilities.ITEM_HANDLER, null).resolve().orElse(null);
    }

    @Nullable static IItemHandler forFill(BlockEntity placed) {
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

    static boolean holdsFluid(BlockEntity tile) {
        for (Direction side : SIDES) {
            try {
                IFluidHandler handler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, side).resolve().orElse(null);
                if (handler == null) { continue; }
                for (int tank = 0; tank < handler.getTanks(); tank++) {
                    if (!handler.getFluidInTank(tank).isEmpty()) { return true; }
                }
            }
            catch (RuntimeException ex) { return true; }
        }
        return false;
    }

    static boolean savesFluid(CompoundTag root) {
        for (String key : root.getAllKeys()) {
            if (!(root.get(key) instanceof CompoundTag compound)) { continue; }
            if (compound.contains("FluidName", Tag.TAG_STRING) && compound.getInt("Amount") > 0 || savesFluid(compound)) { return true; }
        }
        return false;
    }

    static boolean shows(Slot slot, BlockEntity tile) {
        if (slot.container == tile) { return true; }
        if (tile instanceof Container container && slot.container instanceof CompoundContainer large && large.contains(container)) { return true; }
        if (!(slot instanceof SlotItemHandler held)) { return false; }
        IItemHandler open = held.getItemHandler();
        for (Direction side : SIDES) {
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
            if (stack.isEmpty() || !ItemHandlerHelper.canItemStacksStack(stack, placed.stack())) { continue; }
            ItemStack part = stack.split(Math.min(stack.getCount(), placed.stack().getCount()));
            stack.grow(into.insertItem(placed.slot(), part, false).getCount());
            return;
        }
    }

    record Source(@Nullable IItemHandler handler, String why) {}

    record Placed(int slot, ItemStack stack) {}
}
