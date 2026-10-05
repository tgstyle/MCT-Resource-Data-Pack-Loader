package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentBiomeSearch {
    private static final int RANGE = 6400;
    private static final int STEP = 32;
    private static final int VERTICAL = 64;

    private ContentBiomeSearch() {}

    @Nullable public static BlockPos find(ServerLevel level, ServerPlayer player, ResourceKey<Biome> target, String named, BlockPos from, boolean next) {
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        if (generator.getBiomeSource().possibleBiomes().stream().noneMatch(held -> held.is(target))) { return null; }
        List<BlockPos> been = new ArrayList<>();
        if (next) {
            been.addAll(ContentStructureSearch.been(player, named));
            been.add(from);
        }
        int[] heights = Mth.outFromOrigin(from.getY(), level.getMinBuildHeight() + 1, level.getMaxBuildHeight(), VERTICAL).toArray();
        for (BlockPos.MutableBlockPos column : BlockPos.spiralAround(BlockPos.ZERO, RANGE / STEP, Direction.EAST, Direction.SOUTH)) {
            int x = from.getX() + column.getX() * STEP;
            int z = from.getZ() + column.getZ() * STEP;
            if (!somewhere(level, target, x, z, heights)) { continue; }
            int surface = generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, level, level.getChunkSource().randomState());
            BlockPos site = new BlockPos(x, surface, z);
            if (ContentStructureSearch.beenNear(been, site)) { continue; }
            if (biomeAt(level, x, surface, z, target)) { return site; }
        }
        return null;
    }

    private static boolean somewhere(ServerLevel level, ResourceKey<Biome> target, int x, int z, int[] heights) {
        for (int y : heights) {
            if (biomeAt(level, x, y, z, target)) { return true; }
        }
        return false;
    }

    private static boolean biomeAt(ServerLevel level, int x, int y, int z, ResourceKey<Biome> target) { return level.getUncachedNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(y), QuartPos.fromBlock(z)).is(target); }
}
