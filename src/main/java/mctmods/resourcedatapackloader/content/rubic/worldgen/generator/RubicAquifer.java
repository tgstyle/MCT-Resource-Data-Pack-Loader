package mctmods.resourcedatapackloader.content.rubic.worldgen.generator;

import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveRegions;
import mctmods.resourcedatapackloader.util.MathUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Objects;

public final class RubicAquifer {
    private static final int SEA_LEVEL = 63;
    private static final int DRY = -1000000;
    private static final int CACHE_LIMIT = 1 << 16;
    private static final int[][] SURFACE_OFFSETS_IN_CHUNKS = {
            {0, 0}, {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}
    };
    private final World world;
    private final int lavaLevel;
    private final int offsetBlocks;
    private final IBlockState barrierBlock;
    private final DeepGeneration.Field barrier;
    private final DeepGeneration.Field floodedness;
    private final DeepGeneration.Field spread;
    private final DeepGeneration.Field lavaPocket;
    private final long seed;
    private final DeepGeneration carver;
    private final Long2ObjectOpenHashMap<int[]> locations = new Long2ObjectOpenHashMap<>();
    private final Long2LongOpenHashMap statuses = new Long2LongOpenHashMap();
    private final Long2IntOpenHashMap surfaces = new Long2IntOpenHashMap();

    public RubicAquifer(World world, long seed, int lavaLevel, int offsetBlocks, IBlockState barrierBlock, DeepGeneration carver) {
        this.world = world;
        this.seed = seed;
        this.lavaLevel = lavaLevel;
        this.offsetBlocks = offsetBlocks;
        this.barrierBlock = barrierBlock;
        this.carver = carver;
        this.barrier = new DeepGeneration.Field(seed, 16, 8, 16, 1, 1.0D);
        this.floodedness = new DeepGeneration.Field(seed, 17, 128, 191, 1, 1.3D);
        this.spread = new DeepGeneration.Field(seed, 18, 32, 45, 1, 1.0D);
        this.lavaPocket = new DeepGeneration.Field(seed, 19, 2, 2, 1, 1.5D);
    }

    public IBlockState substance(int x, int y, int z, double density) {
        if (y > surfaceEstimate(x, z) + 8 + 12) { return at(pack(SEA_LEVEL, false), y); }
        int xAnchor = gridX(x - 5);
        int yAnchor = gridY(y + 1);
        int zAnchor = gridZ(z - 5);
        long distanceSqr1 = Long.MAX_VALUE;
        long distanceSqr2 = Long.MAX_VALUE;
        long distanceSqr3 = Long.MAX_VALUE;
        long cell1 = 0;
        long cell2 = 0;
        long cell3 = 0;
        int[] location1 = null;
        int[] location2 = null;
        int[] location3 = null;
        for (int x1 = 0; x1 <= 1; x1++) {
            for (int y1 = -1; y1 <= 1; y1++) {
                for (int z1 = 0; z1 <= 1; z1++) {
                    long cell = cellKey(xAnchor + x1, yAnchor + y1, zAnchor + z1);
                    int[] location = location(cell, xAnchor + x1, yAnchor + y1, zAnchor + z1);
                    long dx = location[0] - x;
                    long dy = location[1] - y;
                    long dz = location[2] - z;
                    long distance = dx * dx + dy * dy + dz * dz;
                    if (distanceSqr1 >= distance) {
                        cell3 = cell2;
                        cell2 = cell1;
                        cell1 = cell;
                        location3 = location2;
                        location2 = location1;
                        location1 = location;
                        distanceSqr3 = distanceSqr2;
                        distanceSqr2 = distanceSqr1;
                        distanceSqr1 = distance;
                    } else if (distanceSqr2 >= distance) {
                        cell3 = cell2;
                        cell2 = cell;
                        location3 = location2;
                        location2 = location;
                        distanceSqr3 = distanceSqr2;
                        distanceSqr2 = distance;
                    } else if (distanceSqr3 >= distance) {
                        cell3 = cell;
                        location3 = location;
                        distanceSqr3 = distance;
                    }
                }
            }
        }
        long status1 = status(cell1, location1);
        double similarity12 = similarity(distanceSqr1, distanceSqr2);
        IBlockState state = at(status1, y);
        if (similarity12 <= 0.0D) { return settled(state, x, y, z); }
        if (state.getBlock() == Blocks.WATER && y - 1 < lavaLevel) { return barrierBlock; }
        double[] noiseHolder = { Double.NaN };
        long status2 = status(cell2, location2);
        if (density + similarity12 * pressure(x, y, z, status1, status2, noiseHolder) > 0.0D) { return barrierBlock; }
        long status3 = status(cell3, location3);
        double similarity13 = similarity(distanceSqr1, distanceSqr3);
        if (similarity13 > 0.0D && density + similarity12 * similarity13 * pressure(x, y, z, status1, status3, noiseHolder) > 0.0D) { return barrierBlock; }
        double similarity23 = similarity(distanceSqr2, distanceSqr3);
        if (similarity23 > 0.0D && density + similarity12 * similarity23 * pressure(x, y, z, status2, status3, noiseHolder) > 0.0D) { return barrierBlock; }
        return settled(state, x, y, z);
    }

    private IBlockState settled(IBlockState state, int x, int y, int z) {
        if (state.getBlock() != Blocks.WATER && state.getBlock() != Blocks.LAVA) { return state; }
        if (state.getBlock() == Blocks.WATER && y - 1 < lavaLevel) { return barrierBlock; }
        if (leaks(state, x, y - 1, z) || leaks(state, x - 1, y, z) || leaks(state, x + 1, y, z)
                || leaks(state, x, y, z - 1) || leaks(state, x, y, z + 1)) { return barrierBlock; }
        return state;
    }

    private boolean leaks(IBlockState fluid, int x, int y, int z) {
        if (y < lavaLevel) { return false; }
        if (at(nearestStatus(x, y, z), y).getBlock() == fluid.getBlock()) { return false; }
        return carver.carvedAt(x, y, z);
    }

    private long nearestStatus(int x, int y, int z) {
        int xAnchor = gridX(x - 5);
        int yAnchor = gridY(y + 1);
        int zAnchor = gridZ(z - 5);
        long bestDistance = Long.MAX_VALUE;
        long bestCell = 0;
        int[] bestLocation = null;
        for (int x1 = 0; x1 <= 1; x1++) {
            for (int y1 = -1; y1 <= 1; y1++) {
                for (int z1 = 0; z1 <= 1; z1++) {
                    long cell = cellKey(xAnchor + x1, yAnchor + y1, zAnchor + z1);
                    int[] location = location(cell, xAnchor + x1, yAnchor + y1, zAnchor + z1);
                    long dx = location[0] - x;
                    long dy = location[1] - y;
                    long dz = location[2] - z;
                    long distance = dx * dx + dy * dy + dz * dz;
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestCell = cell;
                        bestLocation = location;
                    }
                }
            }
        }
        return status(bestCell, bestLocation);
    }

    private static double similarity(long distanceSqr1, long distanceSqr2) { return 1.0D - (distanceSqr2 - distanceSqr1) / 25.0D; }

    private double pressure(int x, int y, int z, long status1, long status2, double[] noiseHolder) {
        IBlockState type1 = at(status1, y);
        IBlockState type2 = at(status2, y);
        boolean lava1 = type1.getBlock() == Blocks.LAVA;
        boolean lava2 = type2.getBlock() == Blocks.LAVA;
        boolean water1 = type1.getBlock() == Blocks.WATER;
        boolean water2 = type2.getBlock() == Blocks.WATER;
        if ((lava1 && water2) || (water1 && lava2)) { return 2.0D; }
        int level1 = level(status1);
        int level2 = level(status2);
        int fluidYDiff = Math.abs(level1 - level2);
        if (fluidYDiff == 0) { return 0.0D; }
        double averageFluidY = 0.5D * (level1 + level2);
        double aboveAverage = y + 0.5D - averageFluidY;
        double edge = fluidYDiff / 2.0D - Math.abs(aboveAverage);
        double gradient;
        if (aboveAverage > 0.0D) {
            gradient = edge > 0.0D ? edge / 1.5D : edge / 2.5D;
        } else {
            double fromBottom = 3.0D + edge;
            gradient = fromBottom > 0.0D ? fromBottom / 3.0D : fromBottom / 10.0D;
        }
        double noise = 0.0D;
        if (gradient >= -2.0D && gradient <= 2.0D) {
            if (Double.isNaN(noiseHolder[0])) { noiseHolder[0] = barrier.sample(x, y, z); }
            noise = noiseHolder[0];
        }
        return 2.0D * (noise + gradient);
    }

    private int[] location(long cell, int gridX, int gridY, int gridZ) {
        int[] held = locations.get(cell);
        if (held != null) { return held; }
        long hash = mix(gridX, gridY, gridZ);
        int[] made = {
                (gridX << 4) + (int) Math.floorMod(hash, 10),
                gridY * 12 + (int) Math.floorMod(hash >>> 20, 9),
                (gridZ << 4) + (int) Math.floorMod(hash >>> 40, 10)
        };
        if (locations.size() > CACHE_LIMIT) { locations.clear(); }
        locations.put(cell, made);
        return made;
    }

    private long status(long cell, int[] location) {
        if (statuses.containsKey(cell)) { return statuses.get(cell); }
        long made = computeFluid(location[0], location[1], location[2]);
        if (statuses.size() > CACHE_LIMIT) { statuses.clear(); }
        statuses.put(cell, made);
        return made;
    }

    private long computeFluid(int x, int y, int z) {
        long global = y < lavaLevel ? pack(lavaLevel, true) : pack(SEA_LEVEL, false);
        CaveRegionDef region = ContentCaveRegions.regionAt(world, x, y + offsetBlocks, z);
        if (region != null && region.hasWater()) {
            int level = Math.min(SEA_LEVEL - 1, Math.max(region.waterLevel - offsetBlocks, lavaLevel + 2));
            return pack(level, false);
        }
        int lowestSurface = Integer.MAX_VALUE;
        int topOfCell = y + 12;
        int bottomOfCell = y - 12;
        boolean centerUnderGlobalFluid = false;
        for (int[] offset : SURFACE_OFFSETS_IN_CHUNKS) {
            int sampleX = x + (offset[0] << 4);
            int sampleZ = z + (offset[1] << 4);
            int surface = surfaceEstimate(sampleX, sampleZ);
            int adjusted = surface + 8;
            boolean center = offset[0] == 0 && offset[1] == 0;
            if (center && bottomOfCell > adjusted) { return global; }
            boolean pokesAboveSurface = topOfCell > adjusted;
            if (pokesAboveSurface || center) {
                if (adjusted < SEA_LEVEL && adjusted >= lavaLevel) {
                    if (center) { centerUnderGlobalFluid = true; }
                    if (pokesAboveSurface) { return pack(SEA_LEVEL, false); }
                }
            }
            lowestSurface = Math.min(lowestSurface, surface);
        }
        double floodednessFactor = centerUnderGlobalFluid ? 1.0D - clamp01((lowestSurface + 8 - y) / 64.0D) : 0.0D;
        double flood = floodedness.sample(x, y, z);
        double fullyThreshold = -0.3D + (1.0D - floodednessFactor) * 1.1D;
        double partialThreshold = -0.8D + (1.0D - floodednessFactor) * 1.2D;
        int level;
        if (flood > fullyThreshold) {
            level = level(global);
        } else if (flood > partialThreshold) {
            int cellX = Math.floorDiv(x, 16);
            int cellY = Math.floorDiv(y, 40);
            int cellZ = Math.floorDiv(z, 16);
            int middle = cellY * 40 + 20;
            int quantized = (int) Math.floor(spread.sample(cellX, cellY, cellZ) * 10.0D / 3.0D) * 3;
            level = Math.min(lowestSurface, middle + quantized);
        } else {
            level = DRY;
        }
        boolean lava = false;
        if (level != DRY && level <= -10 && !isLava(global)) {
            lava = Math.abs(lavaPocket.sample(Math.floorDiv(x, 64), Math.floorDiv(y, 40), Math.floorDiv(z, 64))) > 0.55D;
        }
        return pack(level, lava);
    }

    private int surfaceEstimate(int x, int z) {
        long key = ((long) (x >> 4) << 32) ^ ((z >> 4) & 0xFFFFFFFFL);
        if (surfaces.containsKey(key)) { return surfaces.get(key); }
        Biome biome = world.getBiomeProvider().getBiome(new BlockPos((x & ~15) + 8, 0, (z & ~15) + 8), Biomes.PLAINS);
        int estimate = SEA_LEVEL + Math.round(biome.getBaseHeight() * 17.0F + Math.max(0.0F, biome.getHeightVariation()) * 8.0F);
        if (surfaces.size() > CACHE_LIMIT) { surfaces.clear(); }
        surfaces.put(key, estimate);
        return estimate;
    }

    private static double clamp01(double value) { return value < 0.0D ? 0.0D : Math.min(value, 1.0D); }

    private IBlockState at(long status, int y) {
        if (y >= level(status)) { return Objects.requireNonNull(Blocks.AIR).getDefaultState(); }
        return isLava(status) ? Objects.requireNonNull(Blocks.LAVA).getDefaultState() : Objects.requireNonNull(Blocks.WATER).getDefaultState();
    }

    private static long pack(int level, boolean lava) { return ((long) level << 1) | (lava ? 1L : 0L); }

    private static int level(long status) { return (int) (status >> 1); }

    private static boolean isLava(long status) { return (status & 1L) != 0; }

    private static long cellKey(int x, int y, int z) { return ((x & 0x1FFFFFL) << 42) | ((y & 0x1FFFFFL) << 21) | (z & 0x1FFFFFL); }

    private static int gridX(int block) { return block >> 4; }

    private static int gridY(int block) { return Math.floorDiv(block, 12); }

    private static int gridZ(int block) { return block >> 4; }

    private long mix(int x, int y, int z) { return MathUtil.scramble(seed ^ 0x56E27EB562EDDF5DL, x, y, z); }
}
