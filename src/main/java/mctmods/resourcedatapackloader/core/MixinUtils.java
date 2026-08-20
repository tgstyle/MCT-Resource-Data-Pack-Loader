package mctmods.resourcedatapackloader.core;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class MixinUtils {
    public static boolean canTickPosition(World world, BlockPos pos) { return canTickPosition(world, pos, null); }

    public static boolean canTickPosition(World world, BlockPos pos, @Nullable Predicate<Cube> canTickCube) {
        if (!world.isValid(pos)) { return true; }
        if (!world.isBlockLoaded(pos)) { return false; }
        if (canTickCube == null) { return true; }
        return canTickCube.test(((IRubicWorldInternal) world).rdpl$getCubeFromBlockCoords(pos));
    }
}
