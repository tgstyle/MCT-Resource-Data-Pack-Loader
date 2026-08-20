package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public final class ContentCascade {
    private ContentCascade() {}

    public static boolean loaded(World world, BlockPos pos) { return world.isBlockLoaded(pos); }

    public static IBlockState stateOrUnloaded(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) { return Blocks.BEDROCK.getDefaultState(); }
        return world.getBlockState(pos);
    }

    public static boolean loaded(World world, BlockPos pos, int reach) {
        int lowX = (pos.getX() - reach) >> 4;
        int highX = (pos.getX() + reach) >> 4;
        int lowZ = (pos.getZ() - reach) >> 4;
        int highZ = (pos.getZ() + reach) >> 4;
        for (int x = lowX; x <= highX; x++) {
            for (int z = lowZ; z <= highZ; z++) {
                if (!world.isBlockLoaded(new BlockPos(x << 4, 64, z << 4))) { return false; }
            }
        }
        return true;
    }

    public static void report(ChunkPos parent, ChunkPos provided) {
        if (!ContentLog.LOGGER.debugEnabled()) { return; }
        StackTraceElement culprit = culprit();
        if (culprit != null) { ContentLog.LOGGER.warn("Chunk {} provided while populating {} by {}.{}:{}", provided, parent, culprit.getClassName(), culprit.getMethodName(), culprit.getLineNumber()); }
        else { ContentLog.LOGGER.warn("Chunk {} provided while populating {}, no mod frame on the stack", provided, parent); }
        ContentLog.LOGGER.debug("Full stack for chunk {} provided while populating {}", provided, parent, new Throwable("cascade trace"));
    }

    private static StackTraceElement culprit() {
        for (StackTraceElement frame : new Throwable().getStackTrace()) {
            String name = frame.getClassName();
            if (name.startsWith("net.minecraft") || name.startsWith("mctmods.") || name.startsWith("java.") || name.startsWith("sun.") || name.startsWith("org.spongepowered.")) { continue; }
            if (frame.getMethodName().contains("$rdpl$")) { continue; }
            return frame;
        }
        return null;
    }
}
