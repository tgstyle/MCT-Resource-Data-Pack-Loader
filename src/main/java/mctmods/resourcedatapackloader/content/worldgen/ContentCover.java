package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.interfaces.IContentChunkShape;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.Tags;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class ContentCover implements IContentChunkShape {
    private static final List<TagKey<Block>> ROCK = List.of(BlockTags.BASE_STONE_OVERWORLD, BlockTags.BASE_STONE_NETHER, BlockTags.STONE_ORE_REPLACEABLES, BlockTags.DEEPSLATE_ORE_REPLACEABLES, BlockTags.TERRACOTTA, Tags.Blocks.ORES, Tags.Blocks.COBBLESTONES, Tags.Blocks.END_STONES, Tags.Blocks.SANDSTONE_BLOCKS, Tags.Blocks.OBSIDIANS);
    @Nullable private final BlockState floor;
    private final float floorChance;
    @Nullable private final BlockState ceiling;
    private final float ceilingChance;
    private final Set<Block> replace;
    private final int minHeight;
    private final int maxHeight;

    public ContentCover(@Nullable BlockState floor, float floorChance, @Nullable BlockState ceiling, float ceilingChance, Set<Block> replace, int minHeight, int maxHeight) {
        this.floor = floor;
        this.floorChance = floorChance;
        this.ceiling = ceiling;
        this.ceilingChance = ceilingChance;
        this.replace = replace;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) { return false; }

    @Override public void generateChunk(ContentPlacer placer, ChunkPos chunk, Predicate<BlockPos> valid) {
        WorldGenLevel level = placer.level();
        RandomSource random = ContentSpread.regionRandom(level, chunk);
        int lowest = Math.max(placer.floorY(), minHeight);
        int highest = Math.min(placer.ceilingY() - 2, maxHeight);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = chunk.getMinBlockX(); x <= chunk.getMaxBlockX(); x++) {
            for (int z = chunk.getMinBlockZ(); z <= chunk.getMaxBlockZ(); z++) {
                int covered = Math.min(highest, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1);
                for (int y = lowest; y <= covered; y++) {
                    at.set(x, y, z);
                    if (!level.isEmptyBlock(at)) { continue; }
                    if (!valid.test(at)) { continue; }
                    if (floor != null && replaceable(level, at.set(x, y - 1, z)) && random.nextFloat() < floorChance) { placer.placeExactly(floor, x, y - 1, z); }
                    if (ceiling != null && replaceable(level, at.set(x, y + 1, z)) && random.nextFloat() < ceilingChance) { placer.placeExactly(ceiling, x, y + 1, z); }
                }
            }
        }
    }

    private boolean replaceable(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!replace.isEmpty()) { return replace.contains(state.getBlock()); }
        for (TagKey<Block> rock : ROCK) {
            if (state.is(rock)) { return true; }
        }
        return false;
    }
}
