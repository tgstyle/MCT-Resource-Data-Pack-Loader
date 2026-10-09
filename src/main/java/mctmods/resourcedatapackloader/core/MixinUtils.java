package mctmods.resourcedatapackloader.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MixinUtils {
    public static boolean canTickPosition(World world, BlockPos pos) { return !world.isValid(pos) || world.isBlockLoaded(pos); }
}
