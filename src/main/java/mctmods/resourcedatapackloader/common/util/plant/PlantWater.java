package mctmods.resourcedatapackloader.common.util.plant;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class PlantWater {
    private PlantWater() { }

    public static boolean near(World world, BlockPos below, int range) {
        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
            for (int step = 1; step <= range; step++) {
                BlockPos side = below.offset(facing, step);
                IBlockState state = world.getBlockState(side);
                if (state.getMaterial() == Material.WATER) { return true; }
                if (state.getBlock().isAir(state, world, side)) { continue; }
                break;
            }
        }
        return false;
    }
}
