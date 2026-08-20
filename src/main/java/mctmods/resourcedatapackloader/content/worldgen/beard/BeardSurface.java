package mctmods.resourcedatapackloader.content.worldgen.beard;

import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunkGeneratorOverworld;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public final class BeardSurface {
    private static final Map<World, ChunkGeneratorOverworld> SAMPLERS = new WeakHashMap<>();
    private static final Set<World> UNSAMPLED = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Map<World, Map<Long, Integer>> TOPS = new WeakHashMap<>();
    private static final ThreadLocal<Scratch> SCRATCH = ThreadLocal.withInitial(Scratch::new);
    private static World lastWorld = null;
    private static Map<Long, Integer> lastTops = null;

    private BeardSurface() {}

    private static final class Scratch {
        private final double[] depth = new double[1];
        private final double[] main = new double[33];
        private final double[] lower = new double[33];
        private final double[] upper = new double[33];
        private final Biome[] window = new Biome[25];
    }

    private static long topsKey(int blockX, int blockZ) { return ((long) (blockX >> 2) << 32) | ((blockZ >> 2) & 0xFFFFFFFFL); }

    private static Map<Long, Integer> topsFor(World world) {
        if (world == lastWorld) { return lastTops; }
        lastTops = TOPS.computeIfAbsent(world, held -> new HashMap<>());
        lastWorld = world;
        return lastTops;
    }

    public static IBlockState predicted(World world, BlockPos pos) {
        int surface = surfaceAt(world, pos.getX(), pos.getZ());
        int y = pos.getY();
        if (surface < 0) { return Blocks.AIR.getDefaultState(); }
        if (y > surface) { return y <= world.getSeaLevel() ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState(); }
        Biome biome = world.getBiome(pos);
        if (y == surface) { return surface < world.getSeaLevel() ? biome.fillerBlock : biome.topBlock; }
        if (y > surface - 5) { return biome.fillerBlock; }
        return Blocks.STONE.getDefaultState();
    }

    public static int surfaceAt(World world, int blockX, int blockZ) {
        ChunkGeneratorOverworld sampled = samplerFor(world);
        if (sampled == null) { return -1; }
        Integer known = knownTop(world, blockX, blockZ);
        return known != null ? known : keepTop(world, blockX, blockZ, surface(sampled, world, blockX, blockZ));
    }
    @Nullable private static Integer knownTop(World world, int blockX, int blockZ) { return topsFor(world).get(topsKey(blockX, blockZ)); }

    private static int keepTop(World world, int blockX, int blockZ, int made) {
        topsFor(world).put(topsKey(blockX, blockZ), made);
        return made;
    }
    public static int surfaceAt(World world, ChunkGeneratorOverworld sampled, Biome[] region, int originX, int originZ, int size, int blockX, int blockZ) {
        Integer known = knownTop(world, blockX, blockZ);
        if (known != null) { return known; }
        Biome[] window = SCRATCH.get().window;
        int nx = blockX >> 2;
        int nz = blockZ >> 2;
        for (int dz = 0; dz < 5; dz++) { System.arraycopy(region, nx - 2 - originX + (nz - 2 + dz - originZ) * size, window, dz * 5, 5); }
        return keepTop(world, blockX, blockZ, surface(sampled, window, blockX, blockZ));
    }
    public static ChunkGeneratorOverworld samplerFor(World world) {
        ChunkGeneratorOverworld sampled = SAMPLERS.get(world);
        if (sampled != null) { return sampled; }
        if (UNSAMPLED.contains(world)) { return null; }
        IChunkGenerator made = world.provider.createChunkGenerator();
        if (!(made instanceof ChunkGeneratorOverworld)) {
            UNSAMPLED.add(world);
            ContentLog.LOGGER.info("The land in dimension {} is made by {}, which is not the shape this mod can read ahead, so it is asked once and not again", world.provider.getDimension(), made.getClass().getName());
            return null;
        }
        sampled = (ChunkGeneratorOverworld) made;
        SAMPLERS.put(world, sampled);
        return sampled;
    }
    private static int surface(ChunkGeneratorOverworld generator, World world, int blockX, int blockZ) {
        Biome[] biomes = world.getBiomeProvider().getBiomesForGeneration(SCRATCH.get().window, (blockX >> 2) - 2, (blockZ >> 2) - 2, 5, 5);
        return surface(generator, biomes, blockX, blockZ);
    }
    private static int surface(ChunkGeneratorOverworld generator, Biome[] biomes, int blockX, int blockZ) {
        IChunkGeneratorOverworld inside = (IChunkGeneratorOverworld) generator;
        ChunkGeneratorSettings settings = inside.rdpl$settings();
        int nx = blockX >> 2;
        int nz = blockZ >> 2;
        Scratch scratch = SCRATCH.get();
        double[] depth = inside.rdpl$depthNoise().generateNoiseOctaves(scratch.depth, nx, nz, 1, 1, settings.depthNoiseScaleX, settings.depthNoiseScaleZ, settings.depthNoiseScaleExponent);
        float coordinate = settings.coordinateScale;
        float height = settings.heightScale;
        double[] main = inside.rdpl$mainNoise().generateNoiseOctaves(scratch.main, nx, 0, nz, 1, 33, 1, coordinate / settings.mainNoiseScaleX, height / settings.mainNoiseScaleY, coordinate / settings.mainNoiseScaleZ);
        double[] lower = inside.rdpl$minLimit().generateNoiseOctaves(scratch.lower, nx, 0, nz, 1, 33, 1, coordinate, height, coordinate);
        double[] upper = inside.rdpl$maxLimit().generateNoiseOctaves(scratch.upper, nx, 0, nz, 1, 33, 1, coordinate, height, coordinate);
        float[] weights = inside.rdpl$biomeWeights();
        Biome middle = biomes[12];
        boolean amplified = inside.rdpl$terrainType() == WorldType.AMPLIFIED;
        float variation = 0.0F;
        float base = 0.0F;
        float weight = 0.0F;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                Biome beside = biomes[dx + 2 + (dz + 2) * 5];
                float depthHere = settings.biomeDepthOffSet + beside.getBaseHeight() * settings.biomeDepthWeight;
                float scaleHere = settings.biomeScaleOffset + beside.getHeightVariation() * settings.biomeScaleWeight;
                if (amplified && depthHere > 0.0F) {
                    depthHere = 1.0F + depthHere * 2.0F;
                    scaleHere = 1.0F + scaleHere * 4.0F;
                }
                float share = weights[dx + 2 + (dz + 2) * 5] / (depthHere + 2.0F);
                if (beside.getBaseHeight() > middle.getBaseHeight()) { share /= 2.0F; }
                variation += scaleHere * share;
                base += depthHere * share;
                weight += share;
            }
        }
        variation /= weight;
        base /= weight;
        variation = variation * 0.9F + 0.1F;
        base = (base * 4.0F - 1.0F) / 8.0F;
        double offset = base + wander(depth[0]) * 0.2;
        offset = offset * settings.baseSize / 8.0;
        double middleHeight = settings.baseSize + offset * 4.0;
        double over = 0.0;
        for (int cell = 32; cell >= 0; cell--) {
            double falloff = (cell - middleHeight) * settings.stretchY * 128.0 / 256.0 / variation;
            if (falloff < 0.0) { falloff *= 4.0; }
            double least = lower[cell] / settings.lowerLimitScale;
            double most = upper[cell] / settings.upperLimitScale;
            double mix = (main[cell] / 10.0 + 1.0) / 2.0;
            double density = MathHelper.clampedLerp(least, most, mix) - falloff;
            if (cell > 29) {
                double taper = (cell - 29) / 3.0F;
                density = density * (1.0 - taper) + -10.0 * taper;
            }
            if (density > 0.0) { return MathHelper.clamp((int) (cell * 8 + density / (density - over) * 8.0), 1, 254); }
            over = density;
        }
        return -1;
    }

    private static double wander(double raw) {
        double wander = raw / 8000.0;
        if (wander < 0.0) { wander = -wander * 0.3; }
        wander = wander * 3.0 - 2.0;
        if (wander < 0.0) {
            wander /= 2.0;
            if (wander < -1.0) { wander = -1.0; }
            return wander / 1.4 / 2.0;
        }
        if (wander > 1.0) { wander = 1.0; }
        return wander / 8.0;
    }
}
