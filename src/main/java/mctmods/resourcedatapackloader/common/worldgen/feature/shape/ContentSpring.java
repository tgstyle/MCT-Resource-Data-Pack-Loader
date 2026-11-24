package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.util.world.BlockFlags;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ContentSpring extends ContentPlacedShape {
    public ContentSpring(ContentPlacer placer) { super(placer); }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();
        if (!placer.replaces(world, x, y + 1, z) || !placer.replaces(world, x, y - 1, z)) { return false; }
        if (!world.isAirBlock(origin) && !placer.replaces(world, x, y, z)) { return false; }
        int rock = 0;
        int open = 0;
        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
            BlockPos beside = origin.offset(side);
            if (placer.replaces(world, beside.getX(), y, beside.getZ())) { rock++; }
            else if (world.isAirBlock(beside)) { open++; }
        }
        if (rock != 3 || open != 1) { return false; }
        IBlockState fluid = placer.choose(random);
        world.setBlockState(origin, fluid, BlockFlags.UPDATE_CLIENTS);
        world.immediateBlockTick(origin, fluid, random);
        return true;
    }
}
