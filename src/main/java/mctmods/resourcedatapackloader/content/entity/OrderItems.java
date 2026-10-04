package mctmods.resourcedatapackloader.content.entity;

import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.def.OrderDef;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.oredict.OreDictionary;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class OrderItems {
    private static final Map<OrderDef, Set<Integer>> ORES = new IdentityHashMap<>();

    private OrderItems() {}

    static void forget() { ORES.clear(); }

    public static boolean matches(OrderDef def, ItemStack stack) {
        if (stack.isEmpty()) { return false; }
        if (def.blocks.isEmpty()) { return true; }
        Set<Integer> ores = ORES.computeIfAbsent(def, OrderItems::ores);
        for (int id : OreDictionary.getOreIDs(stack)) {
            if (ores.contains(id)) { return true; }
        }
        return false;
    }

    private static Set<Integer> ores(OrderDef def) {
        Set<Integer> ids = new HashSet<>();
        for (String name : OreDictionary.getOreNames()) {
            if (name.equalsIgnoreCase(def.blocks)) { ids.add(OreDictionary.getOreID(name)); }
        }
        return ids;
    }

    public static boolean wanted(OrderDef def, World world, BlockPos pos, IBlockState state) {
        Block block = state.getBlock();
        if (block.isAir(state, world, pos) || state.getMaterial().isLiquid() || state.getBlockHardness(world, pos) < 0.0F) { return false; }
        if (def.job == OrderDef.Job.FARM) {
            if (!(block instanceof BlockCrops) || !((BlockCrops) block).isMaxAge(state)) { return false; }
            return def.blocks.isEmpty() || matches(def, dropped(world, state));
        }
        if (!matches(def, new ItemStack(block, 1, block.damageDropped(state))) && !matches(def, dropped(world, state))) { return false; }
        for (EnumFacing side : EnumFacing.values()) {
            if (!world.getBlockState(pos.offset(side)).isNormalCube()) { return true; }
        }
        return false;
    }

    private static ItemStack dropped(World world, IBlockState state) {
        Item item = state.getBlock().getItemDropped(state, world.rand, 0);
        return new ItemStack(item, 1, state.getBlock().damageDropped(state));
    }

    public static boolean harvestable(EntityLiving mob, BlockPos pos, IBlockState state) {
        ItemStack tool = mob.getHeldItemMainhand();
        if (ContentHardness.digBarred(mob, state, tool)) { return false; }
        return state.getMaterial().isToolNotRequired() || ForgeHooks.canToolHarvestBlock(mob.world, pos, tool);
    }

    public static int digTicks(EntityLiving mob, BlockPos pos, IBlockState state, float speed) {
        ItemStack tool = mob.getHeldItemMainhand();
        float dig = tool.isEmpty() ? 1.0F : tool.getDestroySpeed(state);
        int efficiency = level(Enchantments.EFFICIENCY, tool);
        if (dig > 1.0F && efficiency > 0) { dig += efficiency * efficiency + 1; }
        float hardness = state.getBlockHardness(mob.world, pos);
        float ticks = hardness * 30.0F / dig * ContentHardness.miningAt(state, mob, pos.getX(), pos.getY(), pos.getZ()) / Math.max(0.01F, speed);
        return Math.max(1, MathHelper.ceil(ticks));
    }

    public static int fortune(EntityLiving mob) { return level(Enchantments.FORTUNE, mob.getHeldItemMainhand()); }

    private static int level(@Nullable Enchantment enchantment, ItemStack tool) { return enchantment == null ? 0 : EnchantmentHelper.getEnchantmentLevel(enchantment, tool); }

    public static int tier(ItemStack tool) {
        int tier = -1;
        if (tool.isEmpty()) { return tier; }
        for (String kind : tool.getItem().getToolClasses(tool)) { tier = Math.max(tier, tool.getItem().getHarvestLevel(tool, kind, null, null)); }
        return tier;
    }

    public static boolean isKind(ItemStack stack, String kind) {
        if (stack.isEmpty() || kind.isEmpty()) { return false; }
        for (String held : stack.getItem().getToolClasses(stack)) {
            if (held.equalsIgnoreCase(kind)) { return true; }
        }
        return false;
    }

    public static void replant(World world, BlockPos pos, IBlockState was, List<ItemStack> drops) {
        if (!(was.getBlock() instanceof BlockCrops) || !world.isAirBlock(pos)) { return; }
        for (ItemStack drop : drops) {
            if (!(drop.getItem() instanceof IPlantable) || ((IPlantable) drop.getItem()).getPlant(world, pos).getBlock() != was.getBlock()) { continue; }
            drop.shrink(1);
            world.setBlockState(pos, was.getBlock().getDefaultState());
            return;
        }
    }

    @Nullable public static IItemHandler handler(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) { return null; }
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null || !tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) { return null; }
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    public static IItemHandler carried(EntityLiving mob) {
        EntityStorage storage = EntityStorage.of(mob);
        IItemHandler items = storage == null ? null : storage.items();
        return items != null ? items : new RangedWrapper(new EntityHandsInvWrapper(mob), 1, 2);
    }

    public static boolean carriesAny(EntityLiving mob) {
        IItemHandler carried = carried(mob);
        for (int slot = 0; slot < carried.getSlots(); slot++) {
            if (!carried.getStackInSlot(slot).isEmpty()) { return true; }
        }
        return false;
    }

    public static int spilled(EntityLiving mob, ItemStack stack) {
        ItemStack left = ItemHandlerHelper.insertItemStacked(carried(mob), stack, false);
        if (left.isEmpty()) { return 0; }
        mob.entityDropItem(left, 0.5F);
        return left.getCount();
    }

    public static int count(OrderDef def, IItemHandler handler) {
        int count = 0;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (matches(def, stack)) { count += stack.getCount(); }
        }
        return count;
    }

    public static int move(IItemHandler from, IItemHandler into, Predicate<ItemStack> which, @Nullable List<ItemStack> only, int cap, EntityLiving spill) {
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
            if (!left.isEmpty()) { spill.entityDropItem(left, 0.5F); }
            moved += went;
            if (only != null) { spend(only, held, went); }
        }
        return moved;
    }

    private static int room(List<ItemStack> wants, ItemStack held) {
        for (ItemStack want : wants) {
            if (ItemHandlerHelper.canItemStacksStack(want, held)) { return want.getCount(); }
        }
        return 0;
    }

    private static void spend(List<ItemStack> wants, ItemStack held, int moved) {
        for (ItemStack want : wants) {
            if (!ItemHandlerHelper.canItemStacksStack(want, held)) { continue; }
            want.shrink(moved);
            return;
        }
    }
}
