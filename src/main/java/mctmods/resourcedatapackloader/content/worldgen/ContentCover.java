package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.interfaces.IContentChunkShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class ContentCover implements IContentChunkShape {
    private final ResourceLocation biome;
    @Nullable private final BlockState floor;
    private final float floorChance;
    @Nullable private final BlockState ceiling;
    private final float ceilingChance;
    private final Set<Block> replace;
    private final int minHeight;
    private final int maxHeight;

    public ContentCover(ResourceLocation biome, @Nullable BlockState floor, float floorChance, @Nullable BlockState ceiling, float ceilingChance, Set<Block> replace, int minHeight, int maxHeight) {
        this.biome = biome;
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
                for (int y = lowest; y <= highest; y++) {
                    at.set(x, y, z);
                    if (!level.isEmptyBlock(at)) { continue; }
                    Holder<Biome> here = level.getBiome(at);
                    if (!here.is(biome)) { continue; }
                    if (floor != null && replaceable(level, at.set(x, y - 1, z)) && random.nextFloat() < floorChance) { placer.placeExactly(floor, x, y - 1, z); }
                    if (ceiling != null && replaceable(level, at.set(x, y + 1, z)) && random.nextFloat() < ceilingChance) { placer.placeExactly(ceiling, x, y + 1, z); }
                }
            }
        }
    }

    private boolean replaceable(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!replace.isEmpty()) { return replace.contains(state.getBlock()); }
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.BASE_STONE_NETHER) || state.is(BlockTags.DIRT) || state.is(BlockTags.STONE_ORE_REPLACEABLES) || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
    }
}
