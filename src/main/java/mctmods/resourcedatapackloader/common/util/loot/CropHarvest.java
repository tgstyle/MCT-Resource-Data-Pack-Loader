package mctmods.resourcedatapackloader.common.util.loot;

import mctmods.resourcedatapackloader.common.util.world.BlockFlags;

import mctmods.resourcedatapackloader.common.util.world.WorldEvents;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class CropHarvest {
    private static final String AGE = "age";

    private CropHarvest() {}

    @Nullable private static PropertyInteger ageOf(IBlockState state) {
        for (IProperty<?> property : state.getPropertyKeys()) {
            if (property instanceof PropertyInteger && AGE.equals(property.getName())) { return (PropertyInteger) property; }
        }
        return null;
    }

    public static boolean ripe(IBlockState state, int age) {
        PropertyInteger property = ageOf(state);
        if (property == null) { return false; }
        return state.getValue(property) >= (age >= 0 ? age : Collections.max(property.getAllowedValues()));
    }

    @SuppressWarnings("deprecation") public static void harvest(World world, BlockPos pos, IBlockState state, @Nullable EntityPlayer player, int fortune, boolean replant) {
        List<ItemStack> drops = BlockDrops.roll(world, pos, state, fortune, player);
        PropertyInteger property = ageOf(state);
        ItemStack seed = replant && property != null ? state.getBlock().getItem(world, pos, state) : ItemStack.EMPTY;
        boolean seedTaken = seed.isEmpty();
        for (ItemStack drop : drops) {
            ItemStack kept = drop.copy();
            if (!seedTaken && ItemStack.areItemsEqual(kept, seed)) {
                kept.shrink(1);
                seedTaken = true;
            }
            if (!kept.isEmpty()) { Block.spawnAsEntity(world, pos, kept); }
        }
        WorldEvents.breakEffect(world, null, pos, state);
        if (replant && property != null) { world.setBlockState(pos, state.withProperty(property, Collections.min(property.getAllowedValues())), BlockFlags.UPDATE_ALL); }
        else { world.setBlockState(pos, Blocks.AIR.getDefaultState(), BlockFlags.UPDATE_ALL); }
    }
}
