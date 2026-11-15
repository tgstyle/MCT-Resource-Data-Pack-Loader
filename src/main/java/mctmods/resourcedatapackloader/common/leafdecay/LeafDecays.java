package mctmods.resourcedatapackloader.common.leafdecay;

import mctmods.resourcedatapackloader.common.util.plant.DynamicTrees;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class LeafDecays {
    private LeafDecays() {}

    static boolean qualifies(BlockState state) {
        Block block = state.getBlock();
        if (!(block instanceof LeavesBlock)) { return false; }
        if (!state.hasProperty(LeavesBlock.DISTANCE) || !state.hasProperty(LeavesBlock.PERSISTENT)) { return false; }
        if (state.getValue(LeavesBlock.DISTANCE) != LeavesBlock.DECAY_DISTANCE || state.getValue(LeavesBlock.PERSISTENT) || !state.isRandomlyTicking()) { return false; }
        return !DynamicTrees.isLeaves(block);
    }
}
