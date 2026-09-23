package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public final class ContentSpring implements IContentShape {
    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();
        if (!placer.replaces(x, y + 1, z) || !placer.replaces(x, y - 1, z)) { return false; }
        if (!placer.level().isEmptyBlock(origin) && !placer.replaces(x, y, z)) { return false; }
        int rock = 0;
        int open = 0;
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos beside = origin.relative(side);
            if (placer.replaces(beside.getX(), y, beside.getZ())) { rock++; }
            else if (placer.level().isEmptyBlock(beside)) { open++; }
        }
        if (rock != 3 || open != 1) { return false; }
        BlockState fluid = placer.palette().choose(random);
        if (!placer.placeExactly(fluid, x, y, z)) { return false; }
        FluidState flowing = fluid.getFluidState();
        if (!flowing.isEmpty()) { placer.level().scheduleTick(origin, flowing.getType(), 0); }
        return true;
    }
}
