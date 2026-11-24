package mctmods.resourcedatapackloader.common.blocks;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockVariant;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.DropDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.util.entity.Spawns;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class VariantDrops {
    private static final Random FALLBACK = new Random();

    private VariantDrops() {}

    public static int experience(BlockDef def, Random rand) { return Math.max(0, (def.expDropMax <= def.expDropMin ? AmountDef.of(def.expDropMin) : new AmountDef(def.expDropMin, def.expDropMax)).pick(rand)); }

    public static void drops(Block block, NonNullList<ItemStack> drops, IBlockAccess world, IBlockState state, BlockVariant value, int fortune, Runnable plain) {
        if (value.drops.isEmpty()) { plain.run(); }
        else { roll(block, drops, world, state, value, fortune); }
    }

    public static void roll(Block block, NonNullList<ItemStack> drops, IBlockAccess world, IBlockState state, BlockVariant value, int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : FALLBACK;
        items(drops, value, rand, block.quantityDropped(state, fortune, rand), fortune);
    }

    static void items(NonNullList<ItemStack> drops, BlockVariant value, Random rand, int count, int fortune) {
        for (int i = 0; i < count; i++) {
            each(value, false, rand, drop -> give(drops, drop, rand, fortune));
        }
    }

    private static void each(BlockVariant value, boolean entity, Random rand, Consumer<DropDef> action) {
        List<DropDef> pool = new ArrayList<>();
        for (DropDef drop : value.drops) {
            if (drop.isEntity() == entity && drop.weight > 0) { pool.add(drop); }
        }
        for (DropDef drop : value.drops) {
            if (drop.isEntity() != entity || drop.weight > 0) { continue; }
            action.accept(drop);
        }
        DropDef chosen = pool.isEmpty() ? null : DropDef.pick(pool, rand);
        if (chosen != null) { action.accept(chosen); }
    }

    private static void give(NonNullList<ItemStack> drops, DropDef drop, Random rand, int fortune) {
        if (drop.getResolved() == null) { return; }
        int roll = 1 + rand.nextInt(100);
        int amount = drop.amount.pick(rand);
        if (amount <= 0) { return; }
        ItemStack stack = new ItemStack(drop.getResolved(), amount, drop.meta);
        if (stack.isEmpty()) { return; }
        if (roll <= drop.chance) { drops.add(stack.copy()); }
        if (roll <= drop.chanceFor(fortune)) { drops.add(stack.copy()); }
    }

    public static void spawnEntities(World world, BlockPos pos, BlockVariant value) {
        each(value, true, world.rand, drop -> release(world, pos, drop));
    }

    private static void release(World world, BlockPos pos, DropDef drop) {
        ResourceLocation entity = drop.entity;
        if (entity == null || 1 + world.rand.nextInt(100) > drop.chance) { return; }
        int amount = drop.amount.pick(world.rand);
        for (int i = 0; i < amount; i++) {
            Entity spawned = Spawns.placed(world, entity, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F, true);
            if (spawned == null) { return; }
            world.spawnEntity(spawned);
        }
    }
}
