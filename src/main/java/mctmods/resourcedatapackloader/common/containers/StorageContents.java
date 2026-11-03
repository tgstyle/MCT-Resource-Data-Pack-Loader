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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

final class StorageContents {
    private static final List<Direction> SIDES = Collections.unmodifiableList(Arrays.asList(null, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));
    private static final String CAPITAL_COUNT = "Count";
    private static final String COUNT = "count";
    private static final String ID = "id";
    private static final String AMOUNT = "amount";

    private StorageContents() {}

    static Map<String, Long> census(CompoundTag root) {
        Map<String, Long> totals = new HashMap<>();
        eachStack(root, stack -> totals.merge(String.valueOf(idOf(stack)), (long) count(stack), Long::sum));
        return totals;
    }

    static List<ItemStack> stacksIn(CompoundTag root, HolderLookup.Provider registries) {
        List<ItemStack> found = new ArrayList<>();
        eachStack(root, tag -> {
            CompoundTag copy = tag.copy();
            copy.putInt(COUNT, 1);
            ItemStack.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), copy).result().ifPresent(stack -> {
                stack.setCount(count(tag));
                found.add(stack);
            });
        });
        return found;
    }

    private static void eachStack(CompoundTag root, Consumer<CompoundTag> found) {
        for (String key : root.keySet()) { walk(root.get(key), found); }
    }

    private static void walk(@Nullable Tag tag, Consumer<CompoundTag> found) {
        if (tag instanceof ListTag list) {
            for (Tag inside : list) { walk(inside, found); }
            return;
        }
        if (!(tag instanceof CompoundTag compound)) { return; }
        if (isStack(compound)) { found.accept(compound); }
        else { eachStack(compound, found); }
    }

    private static boolean isStack(CompoundTag tag) {
        if (count(tag) <= 0) { return false; }
        Item item = Registered.find(BuiltInRegistries.ITEM, idOf(tag));
        return item != null && item != Items.AIR;
    }

    @Nullable private static Identifier idOf(CompoundTag tag) { return Identifier.tryParse(tag.getStringOr(ID, "")); }

    private static int count(CompoundTag tag) { return tag.getIntOr(CAPITAL_COUNT, tag.getIntOr(COUNT, 0)); }

    static Map<String, Long> totals(List<ItemStack> stacks) {
        Map<String, Long> totals = new HashMap<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) { totals.merge(String.valueOf(BuiltInRegistries.ITEM.getKey(stack.getItem())), (long) stack.getCount(), Long::sum); }
        }
        return totals;
    }

    static List<ItemStack> shown(ResourceHandler<ItemResource> handler) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int slot = 0; slot < handler.size(); slot++) { stacks.add(ItemUtil.getStack(handler, slot)); }
        return stacks;
    }

    static boolean linked(Level level, BlockEntity tile) {
        ResourceHandler<ItemResource> own = own(tile);
        ResourceHandler<ItemResource> whole = onSide(level, tile, null);
        return own != null && whole != null && slotsOf(whole) > own.size() && paired(tile.getBlockState());
    }

    private static boolean paired(BlockState state) { return !state.hasProperty(BlockStateProperties.CHEST_TYPE) || state.getValue(BlockStateProperties.CHEST_TYPE) != ChestType.SINGLE; }

    static Source find(Level level, BlockEntity tile, CompoundTag before, boolean linked, boolean strict) {
        ResourceHandler<ItemResource> own = own(tile);
        List<ResourceHandler<ItemResource>> tried = linked ? Collections.singletonList(own) : candidates(level, tile, own);
        Map<String, Long> tagged = census(before);
        boolean elsewhere = false;
        for (ResourceHandler<ItemResource> handler : tried) {
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

    private static List<ResourceHandler<ItemResource>> candidates(Level level, BlockEntity tile, @Nullable ResourceHandler<ItemResource> own) {
        List<ResourceHandler<ItemResource>> found = new ArrayList<>();
        addCandidate(found, onSide(level, tile, null), own);
        addCandidate(found, own, own);
        for (Direction side : Direction.values()) { addCandidate(found, onSide(level, tile, side), own); }
        return found;
    }

    private static void addCandidate(List<ResourceHandler<ItemResource>> found, @Nullable ResourceHandler<ItemResource> handler, @Nullable ResourceHandler<ItemResource> own) {
        if (handler == null) { return; }
        int slots = slotsOf(handler);
        if (slots >= 0 && (own == null || slots <= own.size())) { found.add(handler); }
    }

    private static int slotsOf(ResourceHandler<ItemResource> handler) {
        try { return handler.size(); }
        catch (RuntimeException ex) { return -1; }
    }

    @Nullable private static ResourceHandler<ItemResource> own(BlockEntity tile) { return tile instanceof Container held && held.getContainerSize() > 0 ? VanillaContainerWrapper.of(held) : null; }

    @Nullable private static ResourceHandler<ItemResource> onSide(Level level, BlockEntity tile, @Nullable Direction side) {
        try {
            if (side == null) { return OrderItems.handler(level, tile.getBlockPos()); }
            return level.getCapability(Capabilities.Item.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, side);
        }
        catch (RuntimeException ex) { return null; }
    }

    @Nullable static ResourceHandler<ItemResource> forProbe(Level level, BlockEntity probe) {
        ResourceHandler<ItemResource> own = own(probe);
        if (own != null) { return own; }
        BlockPos pos = probe.getBlockPos();
        return level.getCapability(Capabilities.Item.BLOCK, pos, probe.getBlockState(), probe, null);
    }

    @Nullable static ResourceHandler<ItemResource> forFill(Level level, BlockEntity placed) {
        ResourceHandler<ItemResource> own = own(placed);
        ResourceHandler<ItemResource> whole = onSide(level, placed, null);
        return own != null && (whole == null || slotsOf(whole) != own.size()) ? own : whole;
    }

    static boolean withholds(ResourceHandler<ItemResource> handler) {
        for (int slot = 0; slot < handler.size(); slot++) {
            ItemResource held = handler.getResource(slot);
            if (held.isEmpty()) { continue; }
            int asked = Math.min(handler.getAmountAsInt(slot), held.getMaxStackSize());
            try (Transaction look = Transaction.openRoot()) {
                if (handler.extract(slot, held, asked, look) != asked) { return true; }
            }
        }
        return false;
    }

    static boolean drain(ResourceHandler<ItemResource> handler, List<ItemStack> held) {
        for (int slot = 0; slot < handler.size(); slot++) {
            for (ItemResource resource = handler.getResource(slot); !resource.isEmpty(); resource = handler.getResource(slot)) {
                int was = handler.getAmountAsInt(slot);
                int taken = take(handler, slot, resource, was);
                if (taken <= 0) { return false; }
                held.add(resource.toStack(taken));
                if (!handler.getResource(slot).isEmpty() && handler.getAmountAsInt(slot) >= was) { return false; }
            }
        }
        return true;
    }

    private static int take(ResourceHandler<ItemResource> handler, int slot, ItemResource resource, int amount) {
        try (Transaction move = Transaction.openRoot()) {
            int taken = handler.extract(slot, resource, amount, move);
            move.commit();
            return taken;
        }
    }

    static ItemStack insertStacked(ResourceHandler<ItemResource> into, ItemStack stack) {
        if (stack.isEmpty()) { return ItemStack.EMPTY; }
        int went = ResourceHandlerUtil.insertStacking(into, ItemResource.of(stack), stack.getCount(), null);
        return went >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - went);
    }

    static boolean holdsFluid(Level level, BlockEntity tile) {
        for (Direction side : SIDES) {
            try {
                ResourceHandler<FluidResource> handler = level.getCapability(Capabilities.Fluid.BLOCK, tile.getBlockPos(), tile.getBlockState(), tile, side);
                if (handler != null && !ResourceHandlerUtil.isEmpty(handler)) { return true; }
            }
            catch (RuntimeException ex) { return true; }
        }
        return false;
    }

    static boolean savesFluid(CompoundTag root) {
        for (String key : root.keySet()) {
            if (!(root.get(key) instanceof CompoundTag compound)) { continue; }
            if (isFluid(compound) || savesFluid(compound)) { return true; }
        }
        return false;
    }

    private static boolean isFluid(CompoundTag tag) {
        if (tag.getIntOr(AMOUNT, 0) <= 0) { return false; }
        Fluid fluid = Registered.find(BuiltInRegistries.FLUID, idOf(tag));
        return fluid != null && fluid != Fluids.EMPTY;
    }

    static boolean shows(Level level, Slot slot, BlockEntity tile) {
        if (slot.container == tile) { return true; }
        if (tile instanceof Container held && slot.container instanceof CompoundContainer large && large.contains(held)) { return true; }
        if (!(slot instanceof ResourceHandlerSlot handled)) { return false; }
        ResourceHandler<ItemResource> open = handled.getResourceHandler();
        for (Direction side : SIDES) {
            if (open.equals(onSide(level, tile, side))) { return true; }
        }
        return false;
    }

    @Nullable static List<Placed> plan(ResourceHandler<ItemResource> into, List<ItemStack> shown) {
        boolean[] used = new boolean[into.size()];
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

    private static ItemStack planInto(ResourceHandler<ItemResource> into, boolean[] used, int slot, ItemStack stack, List<Placed> placed) {
        if (slot >= used.length || used[slot]) { return stack; }
        ItemStack left = ItemUtil.insertItemReturnRemaining(into, slot, stack, true, null);
        int taken = stack.getCount() - left.getCount();
        if (taken <= 0) { return stack; }
        used[slot] = true;
        placed.add(new Placed(slot, stack.copyWithCount(taken)));
        return left;
    }

    static void fill(ResourceHandler<ItemResource> into, @Nullable List<Placed> plan, List<ItemStack> held) {
        List<ItemStack> pool = new ArrayList<>();
        for (ItemStack stack : held) { pool.add(stack.copy()); }
        if (plan != null) {
            for (Placed placed : plan) { insertPlanned(into, placed, pool); }
        }
        for (ItemStack stack : pool) {
            if (!insertStacked(into, stack).isEmpty()) { throw new IllegalStateException("the target took less than it promised"); }
        }
    }

    private static void insertPlanned(ResourceHandler<ItemResource> into, Placed placed, List<ItemStack> pool) {
        for (ItemStack stack : pool) {
            if (stack.isEmpty() || !ItemStack.isSameItemSameComponents(stack, placed.stack)) { continue; }
            ItemStack part = stack.split(Math.min(stack.getCount(), placed.stack.getCount()));
            stack.grow(ItemUtil.insertItemReturnRemaining(into, placed.slot, part, false, null).getCount());
            return;
        }
    }

    record Source(@Nullable ResourceHandler<ItemResource> handler, String why) {}

    record Placed(int slot, ItemStack stack) {}
}
