package mctmods.resourcedatapackloader.util.world;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class GroundLevel {
    private GroundLevel() {}

    public static BlockPos inWindow(World world, BlockPos pos) {
        if (!((IRubicWorld) world).rdpl$isRubicWorld()) { return world.getTopSolidOrLiquidBlock(pos); }
        return ((IRubicWorldInternal) world).rdpl$groundInWindow(pos);
    }
}
