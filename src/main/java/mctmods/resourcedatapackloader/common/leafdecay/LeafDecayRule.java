package mctmods.resourcedatapackloader.common.leafdecay;

import mctmods.resourcedatapackloader.common.blocks.BlockGroup;
import mctmods.resourcedatapackloader.common.cards.CardPlace;

import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

final class LeafDecayRule {
    private final BlockGroup blocks;
    private final List<TagKey<Block>> tags;
    private final boolean all;
    private final BlockGroup exceptBlocks;
    private final List<TagKey<Block>> exceptTags;
    private final List<String> dimensions;
    private final int minTicks;
    private final int maxTicks;

    LeafDecayRule(BlockGroup blocks, List<TagKey<Block>> tags, boolean all, BlockGroup exceptBlocks, List<TagKey<Block>> exceptTags, List<String> dimensions, int minTicks, int maxTicks) {
        this.blocks = blocks;
        this.tags = tags;
        this.all = all;
        this.exceptBlocks = exceptBlocks;
        this.exceptTags = exceptTags;
        this.dimensions = dimensions;
        this.minTicks = minTicks;
        this.maxTicks = maxTicks;
    }

    private static boolean listed(BlockGroup group, List<TagKey<Block>> tags, BlockState state) {
        if (group.contains(state)) { return true; }
        for (TagKey<Block> tag : tags) {
            if (state.is(tag)) { return true; }
        }
        return false;
    }

    boolean covers(BlockState state, Level level) {
        if (!dimensions.isEmpty() && !CardPlace.inAny(dimensions, level)) { return false; }
        return (all || listed(blocks, tags, state)) && !listed(exceptBlocks, exceptTags, state);
    }

    int delay(RandomSource random) { return minTicks + random.nextInt(maxTicks - minTicks + 1); }
}
