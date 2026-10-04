package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.def.OrderDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class OrderItems {
    private static final Map<String, TagKey<Item>> KINDS = Map.of("pickaxe", ItemTags.PICKAXES, "axe", ItemTags.AXES, "shovel", ItemTags.SHOVELS, "hoe", ItemTags.HOES, "sword", ItemTags.SWORDS);
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
        return DROPS.computeIfAbsent(def, _ -> new IdentityHashMap<>()).computeIfAbsent(state, _ -> {
            for (ItemStack drop : Block.getDrops(state, server, pos, null)) {
                if (matches(def, drop)) { return true; }
            }
            return false;
        });
    }

    private static void yields(OrderDef def, Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel server) || !MINED.computeIfAbsent(def, _ -> new HashSet<>()).add(state)) { return; }
        Set<Item> items = YIELDS.computeIfAbsent(def, _ -> new HashSet<>());
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
        int efficiency = mob.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.EFFICIENCY).map(tool::getEnchantmentLevel).orElse(0);
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
        TagKey<Item> tools = KINDS.get(kind.toLowerCase(Locale.ROOT));
        return tools != null && stack.is(tools);
    }

    public static String kind(ItemStack tool) {
        for (Map.Entry<String, TagKey<Item>> kind : KINDS.entrySet()) {
            if (!tool.isEmpty() && tool.is(kind.getValue())) { return kind.getKey(); }
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

    @Nullable public static ResourceHandler<ItemResource> handler(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) { return null; }
        return level.getCapability(Capabilities.Item.BLOCK, pos, null);
    }

    public static ResourceHandler<ItemResource> carried(Mob mob) {
        EntityStorage storage = EntityStorage.of(mob);
        ResourceHandler<ItemResource> items = storage == null ? null : storage.items();
        return items != null ? items : new OffHand(mob);
    }

    public static boolean carriesAny(Mob mob) { return !ResourceHandlerUtil.isEmpty(carried(mob)); }

    public static int spilled(Mob mob, ItemStack stack) {
        ItemStack left = ItemUtil.insertItemReturnRemaining(carried(mob), stack, false, null);
        if (left.isEmpty()) { return 0; }
        if (mob.level() instanceof ServerLevel level) { mob.spawnAtLocation(level, left, 0.5F); }
        return left.getCount();
    }

    public static int count(OrderDef def, ResourceHandler<ItemResource> handler) {
        int count = 0;
        for (int slot = 0; slot < handler.size(); slot++) {
            ItemStack stack = ItemUtil.getStack(handler, slot);
            if (counts(def, stack)) { count += stack.getCount(); }
        }
        return count;
    }

    public static ItemStack takeOne(ResourceHandler<ItemResource> from, int slot) {
        ItemResource resource = from.getResource(slot);
        if (resource.isEmpty()) { return ItemStack.EMPTY; }
        try (Transaction move = Transaction.openRoot()) {
            int got = from.extract(slot, resource, 1, move);
            move.commit();
            return got <= 0 ? ItemStack.EMPTY : resource.toStack(got);
        }
    }

    public static int move(ResourceHandler<ItemResource> from, ResourceHandler<ItemResource> into, Predicate<ItemStack> which, @Nullable List<ItemStack> only, int cap) {
        int moved = 0;
        for (int slot = 0; slot < from.size() && moved < cap; slot++) {
            ItemStack held = ItemUtil.getStack(from, slot);
            if (held.isEmpty() || !which.test(held)) { continue; }
            int want = Math.min(cap - moved, only == null ? held.getCount() : room(only, held));
            if (want <= 0) { continue; }
            ItemResource resource = from.getResource(slot);
            int fits = moving(from, into, slot, resource, want, false);
            int went = fits <= 0 ? 0 : moving(from, into, slot, resource, fits, true);
            if (went <= 0) { continue; }
            moved += went;
            if (only != null) { spend(only, held, went); }
        }
        return moved;
    }

    private static int moving(ResourceHandler<ItemResource> from, ResourceHandler<ItemResource> into, int slot, ItemResource resource, int amount, boolean keep) {
        try (Transaction move = Transaction.openRoot()) {
            int taken = from.extract(slot, resource, amount, move);
            int went = taken <= 0 ? 0 : ResourceHandlerUtil.insertStacking(into, resource, taken, move);
            if (went != taken) { return 0; }
            if (keep) { move.commit(); }
            return went;
        }
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

    private static final class OffHand extends ItemStackResourceHandler {
        private final Mob mob;

        private OffHand(Mob mob) { this.mob = mob; }

        @Override @Nonnull protected ItemStack getStack() { return mob.getOffhandItem(); }

        @Override protected void setStack(@Nonnull ItemStack stack) { mob.setItemSlot(EquipmentSlot.OFFHAND, stack, true); }
    }
}
