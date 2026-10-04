package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.def.OrderDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.EntityHandsInvWrapper;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class OrderItems {
    private static final List<String> KINDS = List.of("pickaxe", "axe", "shovel", "hoe", "sword");
    private static final List<TagKey<Block>> TIERS = List.of(BlockTags.INCORRECT_FOR_WOODEN_TOOL, BlockTags.INCORRECT_FOR_STONE_TOOL, BlockTags.INCORRECT_FOR_IRON_TOOL, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, BlockTags.INCORRECT_FOR_NETHERITE_TOOL);
    private static final Map<OrderDef, Map<BlockState, Boolean>> DROPS = new IdentityHashMap<>();
    private static final Map<OrderDef, Set<BlockState>> MINED = new IdentityHashMap<>();
    private static final Map<OrderDef, Set<Item>> YIELDS = new IdentityHashMap<>();

    private OrderItems() {}

    static void forget() {
        DROPS.clear();
        MINED.clear();
        YIELDS.clear();
    }

    public static boolean matches(OrderDef def, ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        return def.blocks() == null || stack.is(def.blocks());
    }

    public static boolean counts(OrderDef def, ItemStack stack) {
        if (matches(def, stack)) { return true; }
        Set<Item> items = YIELDS.get(def);
        return def.job() == OrderDef.Job.MINE && items != null && items.contains(stack.getItem());
    }

    public static boolean wanted(OrderDef def, Level level, BlockPos pos, BlockState state) {
        if (state.isAir() || state.getBlock() instanceof LiquidBlock || state.getDestroySpeed(level, pos) < 0.0F) { return false; }
        if (def.job() == OrderDef.Job.FARM) {
            if (!(state.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(state)) { return false; }
            return def.blocks() == null || dropped(def, level, pos, state);
        }
        boolean own = matches(def, new ItemStack(state.getBlock()));
        if (!own && !dropped(def, level, pos, state)) { return false; }
        if (own && def.job() == OrderDef.Job.MINE) { yields(def, level, pos, state); }
        for (Direction side : Direction.values()) {
            BlockPos next = pos.relative(side);
            if (!level.getBlockState(next).isRedstoneConductor(level, next)) { return true; }
        }
        return false;
    }

    private static boolean dropped(OrderDef def, Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel server)) { return false; }
        return DROPS.computeIfAbsent(def, held -> new IdentityHashMap<>()).computeIfAbsent(state, held -> {
            for (ItemStack drop : Block.getDrops(state, server, pos, null)) {
                if (matches(def, drop)) { return true; }
            }
            return false;
        });
    }

    private static void yields(OrderDef def, Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel server) || !MINED.computeIfAbsent(def, held -> new HashSet<>()).add(state)) { return; }
        Set<Item> items = YIELDS.computeIfAbsent(def, held -> new HashSet<>());
        for (ItemStack drop : Block.getDrops(state, server, pos, null)) {
            if (!drop.isEmpty()) { items.add(drop.getItem()); }
        }
    }

    public static boolean harvestable(Mob mob, BlockState state) {
        ItemStack tool = mob.getMainHandItem();
        if (ContentHardness.digBarred(mob, state, tool)) { return false; }
        return !state.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(state);
    }

    public static int digTicks(Mob mob, BlockPos pos, BlockState state, float speed) {
        ItemStack tool = mob.getMainHandItem();
        float dig = tool.isEmpty() ? 1.0F : tool.getDestroySpeed(state);
        int efficiency = mob.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.EFFICIENCY).map(tool::getEnchantmentLevel).orElse(0);
        if (dig > 1.0F && efficiency > 0) { dig += efficiency * efficiency + 1; }
        float hardness = state.getDestroySpeed(mob.level(), pos);
        float ticks = hardness * 30.0F / dig * ContentHardness.miningAt(state, mob, pos.getX(), pos.getY(), pos.getZ()) / Math.max(0.01F, speed);
        return Math.max(1, Mth.ceil(ticks));
    }

    public static int tier(ItemStack tool) {
        Tool rules = tool.get(DataComponents.TOOL);
        if (rules == null) { return -1; }
        for (Tool.Rule rule : rules.rules()) {
            if (rule.correctForDrops().orElse(true)) { continue; }
            int level = rule.blocks().unwrapKey().map(TIERS::indexOf).orElse(-1);
            if (level >= 0) { return level; }
        }
        return 0;
    }

    public static boolean isKind(ItemStack stack, String kind) {
        if (stack.isEmpty() || kind.isEmpty()) { return false; }
        return stack.canPerformAction(ItemAbility.get(kind.toLowerCase(Locale.ROOT) + "_dig"));
    }

    public static String kind(ItemStack tool) {
        for (String kind : KINDS) {
            if (isKind(tool, kind)) { return kind; }
        }
        return "";
    }

    public static void replant(Level level, BlockPos pos, BlockState was, List<ItemStack> drops) {
        if (!(was.getBlock() instanceof CropBlock) || !level.isEmptyBlock(pos)) { return; }
        for (ItemStack drop : drops) {
            if (!(drop.getItem() instanceof BlockItem seed) || seed.getBlock() != was.getBlock()) { continue; }
            drop.shrink(1);
            level.setBlock(pos, was.getBlock().defaultBlockState(), Block.UPDATE_ALL);
            return;
        }
    }

    @Nullable public static IItemHandler handler(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) { return null; }
        return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
    }

    public static IItemHandler carried(Mob mob) {
        EntityStorage storage = EntityStorage.of(mob);
        IItemHandler items = storage == null ? null : storage.items();
        return items != null ? items : new RangedWrapper(new EntityHandsInvWrapper(mob), 1, 2);
    }

    public static boolean carriesAny(Mob mob) {
        IItemHandler carried = carried(mob);
        for (int slot = 0; slot < carried.getSlots(); slot++) {
            if (!carried.getStackInSlot(slot).isEmpty()) { return true; }
        }
        return false;
    }

    public static int spilled(Mob mob, ItemStack stack) {
        ItemStack left = ItemHandlerHelper.insertItemStacked(carried(mob), stack, false);
        if (left.isEmpty()) { return 0; }
        mob.spawnAtLocation(left, 0.5F);
        return left.getCount();
    }

    public static int count(OrderDef def, IItemHandler handler) {
        int count = 0;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (counts(def, stack)) { count += stack.getCount(); }
        }
        return count;
    }

    public static int move(IItemHandler from, IItemHandler into, Predicate<ItemStack> which, @Nullable List<ItemStack> only, int cap, Mob spill) {
        int moved = 0;
        for (int slot = 0; slot < from.getSlots() && moved < cap; slot++) {
            ItemStack held = from.getStackInSlot(slot).copy();
            if (held.isEmpty() || !which.test(held)) { continue; }
            int want = Math.min(cap - moved, only == null ? held.getCount() : room(only, held));
            if (want <= 0) { continue; }
            ItemStack trial = from.extractItem(slot, want, true);
            int fits = trial.getCount() - ItemHandlerHelper.insertItemStacked(into, trial, true).getCount();
            if (fits <= 0) { continue; }
            ItemStack taken = from.extractItem(slot, fits, false);
            ItemStack left = ItemHandlerHelper.insertItemStacked(into, taken, false);
            int went = taken.getCount() - left.getCount();
            if (!left.isEmpty()) { left = from.insertItem(slot, left, false); }
            if (!left.isEmpty()) { left = ItemHandlerHelper.insertItemStacked(from, left, false); }
            if (!left.isEmpty()) { spill.spawnAtLocation(left, 0.5F); }
            moved += went;
            if (only != null) { spend(only, held, went); }
        }
        return moved;
    }

    public static boolean same(ItemStack one, ItemStack other) { return ItemStack.isSameItemSameComponents(one, other); }

    private static int room(List<ItemStack> wants, ItemStack held) {
        for (ItemStack want : wants) {
            if (same(want, held)) { return want.getCount(); }
        }
        return 0;
    }

    private static void spend(List<ItemStack> wants, ItemStack held, int moved) {
        for (ItemStack want : wants) {
            if (!same(want, held)) { continue; }
            want.shrink(moved);
            return;
        }
    }
}
