package mctmods.resourcedatapackloader.common.leafdecay;

import mctmods.resourcedatapackloader.common.blocks.BlockGroup;
import mctmods.resourcedatapackloader.common.cards.CardPlace;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

final class LeafDecayRule {
    private final BlockGroup blocks;
    private final List<BlockMatch> tags;
    private final boolean all;
    private final BlockGroup exceptBlocks;
    private final List<BlockMatch> exceptTags;
    private final List<String> dimensions;
    private final int minTicks;
    private final int maxTicks;

    LeafDecayRule(BlockGroup blocks, List<BlockMatch> tags, boolean all, BlockGroup exceptBlocks, List<BlockMatch> exceptTags, List<String> dimensions, int minTicks, int maxTicks) {
        this.blocks = blocks;
        this.tags = tags;
        this.all = all;
        this.exceptBlocks = exceptBlocks;
        this.exceptTags = exceptTags;
        this.dimensions = dimensions;
        this.minTicks = minTicks;
        this.maxTicks = maxTicks;
    }

    private static boolean listed(BlockGroup group, List<BlockMatch> tags, IBlockState state, World world, BlockPos pos) {
        if (group.contains(state) || group.contains(state.cycleProperty(BlockLeaves.CHECK_DECAY))) { return true; }
        for (BlockMatch tag : tags) {
            if (tag.matches(state.getBlock(), world, pos)) { return true; }
        }
        return false;
    }

    boolean covers(IBlockState state, World world, BlockPos pos) {
        if (!dimensions.isEmpty() && !CardPlace.inAny(dimensions, world.provider.getDimension())) { return false; }
        return (all || listed(blocks, tags, state, world, pos)) && !listed(exceptBlocks, exceptTags, state, world, pos);
    }

    int delay(Random random) { return minTicks + random.nextInt(maxTicks - minTicks + 1); }
}
