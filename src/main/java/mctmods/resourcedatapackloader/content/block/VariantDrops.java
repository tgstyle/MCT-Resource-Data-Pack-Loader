package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.DropDef;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class VariantDrops {
    private static final Random FALLBACK = new Random();

    private VariantDrops() {}

    static int experience(BlockDef def, Random rand) { return Math.max(0, (def.expDropMax <= def.expDropMin ? AmountDef.of(def.expDropMin) : new AmountDef(def.expDropMin, def.expDropMax)).pick(rand)); }

    static void roll(Block block, NonNullList<ItemStack> drops, IBlockAccess world, IBlockState state, BlockVariant value, int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : FALLBACK;
        items(drops, value, rand, block.quantityDropped(state, fortune, rand), fortune);
    }

    static void items(NonNullList<ItemStack> drops, BlockVariant value, Random rand, int count, int fortune) {
        List<DropDef> pool = new ArrayList<>();
        for (DropDef drop : value.drops) {
            if (!drop.isEntity() && drop.weight > 0) { pool.add(drop); }
        }
        for (int i = 0; i < count; i++) {
            for (DropDef drop : value.drops) {
                if (drop.isEntity() || drop.weight > 0) { continue; }
                give(drops, drop, rand, fortune);
            }
            DropDef chosen = pool.isEmpty() ? null : DropDef.pick(pool, rand);
            if (chosen != null) { give(drops, chosen, rand, fortune); }
        }
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

    static void spawnEntities(World world, BlockPos pos, BlockVariant value) {
        List<DropDef> pool = new ArrayList<>();
        for (DropDef drop : value.drops) {
            if (drop.isEntity() && drop.weight > 0) { pool.add(drop); }
        }
        for (DropDef drop : value.drops) {
            if (!drop.isEntity() || drop.weight > 0) { continue; }
            release(world, pos, drop);
        }
        DropDef chosen = pool.isEmpty() ? null : DropDef.pick(pool, world.rand);
        if (chosen != null) { release(world, pos, chosen); }
    }

    private static void release(World world, BlockPos pos, DropDef drop) {
        ResourceLocation entity = drop.entity;
        if (entity == null || 1 + world.rand.nextInt(100) > drop.chance) { return; }
        int amount = drop.amount.pick(world.rand);
        for (int i = 0; i < amount; i++) {
            Entity spawned = EntityList.createEntityByIDFromName(entity, world);
            if (spawned == null) { return; }
            spawned.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);
            if (spawned instanceof EntityLiving) { ((EntityLiving) spawned).onInitialSpawn(world.getDifficultyForLocation(pos), null); }
            world.spawnEntity(spawned);
        }
    }
}
