package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CityBergs {
    private static final int REACH = 32;
    private static final int TOUCH = 1;
    private static final int MELT_Y = 63;
    static final int TEMPERATURE_SEA = 63;
    private final WorldGenLevel level;
    private final LineCompat.IBergNoise noise;
    private final BiomeManager biomes;
    private final int sea;
    private final int minX;
    private final int minZ;
    private final int side;
    private final byte[] berg;
    private final BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();

    private CityBergs(WorldGenLevel level, BoundingBox window) {
        this.level = level;
        ServerChunkCache source = level.getLevel().getChunkSource();
        RandomState random = source.randomState();
        this.noise = LineCompat.bergNoise(random);
        LineCompat.INoiseBiomes born = LineCompat.noiseBiomes(source.getGenerator().getBiomeSource(), random);
        Map<Long, Holder<Biome>> seen = new HashMap<>();
        this.biomes = LineCompat.withBiomes(level.getBiomeManager(), (x, y, z) -> seen.computeIfAbsent(BlockPos.asLong(x, y, z), _ -> born.at(x, y, z)));
        this.sea = source.getGenerator().getSeaLevel();
        this.minX = window.minX();
        this.minZ = window.minZ();
        this.side = window.getXSpan();
        this.berg = new byte[side * side];
    }

    public static void cleared(WorldGenLevel level, ChunkPos chunk, StructureManager manager) {
        BoundingBox scan = new BoundingBox(chunk.getMinBlockX(), level.getMinY(), chunk.getMinBlockZ(), chunk.getMaxBlockX(), level.getMaxY(), chunk.getMaxBlockZ());
        BoundingBox window = scan.inflatedBy(REACH + TOUCH);
        List<BoundingBox> pieces = ContentCityTrees.footprints(manager, chunk, null, window);
        if (pieces.isEmpty()) { return; }
        CityBergs bergs = new CityBergs(level, window);
        if (!bergs.any(scan)) { return; }
        int cleared = bergs.clear(scan, bergs.reach(pieces));
        if (cleared > 0) { ContentLog.LOGGER.debug("Cleared {} block(s) of berg ice a city reaches in chunk {}, {}", cleared, chunk.x(), chunk.z()); }
    }

    private int index(int x, int z) { return (x - minX) * side + (z - minZ); }

    private boolean inside(int x, int z) { return x >= minX && z >= minZ && x < minX + side && z < minZ + side; }

    private boolean any(BoundingBox scan) {
        for (int x = scan.minX(); x <= scan.maxX(); x++) {
            for (int z = scan.minZ(); z <= scan.maxZ(); z++) {
                if (berg(x, z)) { return true; }
            }
        }
        return false;
    }

    private boolean berg(int x, int z) {
        int i = index(x, z);
        if (berg[i] == 0) { berg[i] = rises(x, z) ? (byte) 1 : (byte) 2; }
        return berg[i] == 1;
    }

    private boolean rises(int x, int z) {
        double rise = noise.rise(x, z);
        if (rise <= 1.8) { return false; }
        Holder<Biome> biome = biomes.getBiome(probe.set(x, sea, z));
        if (!biome.is(Biomes.FROZEN_OCEAN) && !biome.is(Biomes.DEEP_FROZEN_OCEAN)) { return false; }
        double top = Math.min(rise * rise * 1.2, Math.ceil(noise.roof(x, z) * 40.0) + 14.0);
        if (biome.value().shouldMeltFrozenOceanIcebergSlightly(probe.set(x, MELT_Y, z), TEMPERATURE_SEA)) { top -= 2.0; }
        return top > 2.0;
    }

    private int[] reach(List<BoundingBox> pieces) {
        int[] far = new int[side * side];
        Arrays.fill(far, -1);
        int[] queue = new int[side * side];
        int head = 0;
        int tail = 0;
        for (BoundingBox piece : pieces) {
            for (int x = Math.max(piece.minX() - TOUCH, minX); x <= Math.min(piece.maxX() + TOUCH, minX + side - 1); x++) {
                for (int z = Math.max(piece.minZ() - TOUCH, minZ); z <= Math.min(piece.maxZ() + TOUCH, minZ + side - 1); z++) {
                    int i = index(x, z);
                    if (far[i] < 0 && berg(x, z)) {
                        far[i] = 0;
                        queue[tail++] = i;
                    }
                }
            }
        }
        int[][] steps = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (head < tail) {
            int i = queue[head++];
            if (far[i] >= REACH) { continue; }
            int x = minX + i / side;
            int z = minZ + i % side;
            for (int[] step : steps) {
                int nx = x + step[0];
                int nz = z + step[1];
                if (!inside(nx, nz)) { continue; }
                int j = index(nx, nz);
                if (far[j] < 0 && berg(nx, nz)) {
                    far[j] = far[i] + 1;
                    queue[tail++] = j;
                }
            }
        }
        return far;
    }

    private int clear(BoundingBox scan, int[] far) {
        int cleared = 0;
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int x = scan.minX(); x <= scan.maxX(); x++) {
            for (int z = scan.minZ(); z <= scan.maxZ(); z++) {
                if (far[index(x, z)] < 0) { continue; }
                int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                for (int y = sea; y < top; y++) {
                    BlockState state = level.getBlockState(at.set(x, y, z));
                    if (state.is(Blocks.PACKED_ICE) || state.is(Blocks.SNOW_BLOCK)) {
                        level.setBlock(at, air, 2);
                        cleared++;
                    }
                }
            }
        }
        return cleared;
    }
}
