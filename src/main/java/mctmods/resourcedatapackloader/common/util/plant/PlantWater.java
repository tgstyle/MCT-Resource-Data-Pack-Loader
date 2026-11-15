package mctmods.resourcedatapackloader.common.util.plant;

import mctmods.resourcedatapackloader.common.blocks.fluid.ContentLiquidBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public final class PlantWater {
    private PlantWater() { }

    public static boolean near(LevelReader level, BlockPos below, int range) {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            for (int step = 1; step <= range; step++) {
                BlockPos side = below.relative(facing, step);
                BlockState held = level.getBlockState(side);
                if (liquid(level, side, held, FluidTags.WATER, FluidDef::waterMaterial)) { return true; }
                if (held.isAir()) { continue; }
                break;
            }
        }
        return false;
    }

    public static boolean liquid(LevelReader level, BlockPos pos, BlockState held, TagKey<Fluid> tag, Predicate<FluidDef> material) {
        if (level.getFluidState(pos).is(tag)) { return true; }
        return held.getBlock() instanceof ContentLiquidBlock liquid && material.test(liquid.getDef());
    }
}
