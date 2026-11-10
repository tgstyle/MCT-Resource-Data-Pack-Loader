package mctmods.resourcedatapackloader.common.leafdecay;

import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class LeafDecays {
    private static final String DYNAMIC_LEAVES = "com.dtteam.dynamictrees.block.leaves.DynamicLeavesBlock";
    private static volatile boolean resolved;
    @Nullable private static volatile Class<?> dynamicLeaves;

    private LeafDecays() {}

    @Nullable private static Class<?> dynamicLeaves() {
        if (!resolved) {
            try { dynamicLeaves = Class.forName(DYNAMIC_LEAVES, false, LeafDecays.class.getClassLoader()); }
            catch (ClassNotFoundException | LinkageError absent) { dynamicLeaves = null; }
            resolved = true;
        }
        return dynamicLeaves;
    }

    static boolean qualifies(BlockState state) {
        Block block = state.getBlock();
        if (!(block instanceof LeavesBlock)) { return false; }
        Class<?> dynamic = dynamicLeaves();
        if (dynamic != null && dynamic.isInstance(block)) { return false; }
        return state.hasProperty(LeavesBlock.DISTANCE) && state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.DISTANCE) == LeavesBlock.DECAY_DISTANCE && !state.getValue(LeavesBlock.PERSISTENT) && state.isRandomlyTicking();
    }
}
