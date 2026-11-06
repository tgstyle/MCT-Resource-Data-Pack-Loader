package mctmods.resourcedatapackloader.common.util.worldgen;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public final class LineTags {
    public static final TagKey<Block> ORES = Tags.BlockItems.ORES.block();
    public static final TagKey<Block> COBBLESTONES = Tags.BlockItems.COBBLESTONES.block();
    public static final TagKey<Block> END_STONES = Tags.BlockItems.END_STONES.block();
    public static final TagKey<Block> SANDSTONE_BLOCKS = Tags.BlockItems.SANDSTONE_BLOCKS.block();
    public static final TagKey<Block> OBSIDIANS = Tags.BlockItems.OBSIDIANS.block();

    private LineTags() {}
}
