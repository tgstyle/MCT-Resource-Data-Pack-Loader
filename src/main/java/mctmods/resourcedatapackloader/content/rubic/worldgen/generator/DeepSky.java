package mctmods.resourcedatapackloader.content.rubic.worldgen.generator;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.def.SkyStone;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.worldgen.CubePrimer;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiome;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes3D;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveRegions;
import mctmods.resourcedatapackloader.util.AddressTools;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMesa;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;

final class DeepSky {
    private static final int SKY_ISLAND_SHAPE = 0;
    static final int SKY_CAVES = 1;
    private static final String[] SKY_SHAPES = {"islands", "caves"};
    private static final int SKY_FILLER = 4;
    private static final int SKY_HEADROOM = 8;
    private static final int SKY_TAPER = 24;
    private static final int SKY_LOOKUP = 8;
    private static final double SKY_SLOPE_LIMIT = 3.0D;
    private final DeepGeneration deep;
    private final World world;
    private final int offsetBlocks;
    final IBlockState skyStone;
    final int skyShape;
    private final double skyIslands;
    private final double skyThickness;
    private final double skyMaskIslands;
    private final boolean skyRegions;
    private final boolean skyBands;
    private final int skyFloorGen;
    private final int skyCeilGen;
    private final int skyTaper;
    private final int skyLowest;
    private final int skyHighest;

    DeepSky(DeepGeneration deep, World world, int offsetBlocks) {
        this.deep = deep;
        this.world = world;
        this.offsetBlocks = offsetBlocks;
        this.skyStone = DeepGeneration.parseState(deep.scoped("skyStone", Config.worldgen.skyStone), "skyStone");
        this.skyShape = DeepGeneration.choice(deep.scoped("skyShape", Config.worldgen.skyShape), "skyShape", SKY_SHAPES, SKY_ISLAND_SHAPE);
        this.skyIslands = parseIslands(ContentControl.decimal(ContentControl.TERRAIN, "skyIslands", Config.worldgen.skyIslands));
        this.skyThickness = parseThickness(ContentControl.decimal(ContentControl.TERRAIN, "skyThickness", Config.worldgen.skyThickness));
        int[] band = parseHeights(ContentControl.numbers(ContentControl.TERRAIN, "skyHeights", Config.worldgen.skyHeights));
        this.skyLowest = band[0];
        this.skyHighest = band[1];
        this.skyMaskIslands = ContentCaveRegions.lowestSkyIslands(ContentBiomes.lowestSkyIslands(skyIslands));
        this.skyRegions = ContentCaveRegions.anyShapesSky();
        this.skyBands = ContentBiomes3D.anyShapesSky();
        this.skyFloorGen = Math.max(skyLowest, ((IRubicWorld) world).rdpl$getMaxGenerationHeight());
        this.skyCeilGen = Math.min(skyHighest, ((IMinMaxHeight) world).rdpl$getMaxHeight() - 2
                - (skyShape == SKY_CAVES ? 0 : SKY_HEADROOM) - offsetBlocks);
        this.skyTaper = MathHelper.clamp((skyCeilGen - skyFloorGen + 1) / 4, 4, SKY_TAPER);
    }

    void fillSkyCube(CubePrimer primer, int cubeX, int cubeY, int cubeZ, Random rand, boolean topBedrock, boolean bottomBedrock) {
        int worldTop = ((IMinMaxHeight) world).rdpl$getMaxHeight() - 1 - (skyShape == SKY_CAVES ? 0 : SKY_HEADROOM);
        int worldBase = Coords.cubeToMinBlock(cubeY);
        boolean tracing = ContentLog.LOGGER.debugEnabled();
        if (worldBase >= worldTop) {
            if (tracing) { ContentLog.LOGGER.debug("Sky cube {},{},{} left empty: its floor y {} is at or above the sky ceiling y {}", cubeX, cubeY, cubeZ, worldBase, worldTop); }
            return;
        }
        int genBase = worldBase - offsetBlocks;
        if (genBase + Cube.SIZE - 1 < skyLowest || genBase > skyHighest) {
            if (tracing) { ContentLog.LOGGER.debug("Sky cube {},{},{} left empty: gen y {}..{} falls outside skyHeights {}..{}", cubeX, cubeY, cubeZ, genBase, genBase + Cube.SIZE - 1, skyLowest, skyHighest); }
            return;
        }
        int steps = (Cube.SIZE + SKY_LOOKUP) / 8 + 1;
        double[][][] regions = null;
        if (skyShape != SKY_CAVES) {
            regions = DeepGeneration.lattice(cubeX << 4, genBase, cubeZ << 4, steps, deep.cheeseWide::sample);
            double[] ends = span(regions);
            if (ends[1] <= skyMaskIslands) {
                if (tracing) { ContentLog.LOGGER.debug("Sky cube {},{},{} left empty: mask {} to {} never clears {}, corners {} and {}", cubeX, cubeY, cubeZ, fixed(ends[0]), fixed(ends[1]), fixed(skyMaskIslands), fixed(regions[0][0][0]), fixed(regions[4][0][4])); }
                return;
            }
        }
        double[][][] lattice = deep.sampleLattice(cubeX << 4, genBase, cubeZ << 4, steps);
        int vanillaY = cubeY - Coords.blockToCube(offsetBlocks);
        Biome[] column = world.getBiomeProvider().getBiomes(null, cubeX << 4, cubeZ << 4, Cube.SIZE, Cube.SIZE);
        int placed = 0;
        for (int z = 0; z < Cube.SIZE; z++) {
            for (int x = 0; x < Cube.SIZE; x++) {
                Biome biome = column[AddressTools.getLocalAddress(x, z)];
                CaveRegionDef region = skyRegions ? ContentCaveRegions.regionAt(world, (cubeX << 4) + x, worldBase + 8, (cubeZ << 4) + z) : null;
                BiomeDef band = skyBands ? ContentBiomes3D.shapesSkyAt(biome, worldBase + 8) : null;
                Biome surface = skySurfaceBiome(biome, region, band);
                IBlockState stone = skyStoneFor(biome, region, band);
                IBlockState top = grounded(skySurfaceFor(surface.topBlock, band, true), stone);
                IBlockState filler = grounded(skySurfaceFor(surface.fillerBlock, band, false), stone);
                double islands = skyIslandsFor(biome, region, band);
                double thickness = skyThicknessFor(biome, region, band);
                BiomeMesa mesa = surface instanceof BiomeMesa ? bandedMesa((BiomeMesa) surface) : null;
                int base = skyDepthAt((cubeX << 4) + x, (cubeZ << 4) + z);
                int depth = base;
                int under = solidAbove(regions, lattice, x, z, worldBase, genBase, worldTop, islands, thickness, base);
                for (int y = Cube.SIZE - 1; y >= 0; y--) {
                    if (skyOpen(regions, lattice, x, y, z, worldBase, genBase, worldTop, islands, thickness)) {
                        under = 0;
                        continue;
                    }
                    if (under == 0) { depth = stretched(base, lattice, x, y, z, (steps - 1) * 8 - 1); }
                    IBlockState state = mesa != null
                            ? mesaState(mesa, top, stone, under, depth, (cubeX << 4) + x, worldBase + y, (cubeZ << 4) + z, genBase + y)
                            : skyState(top, filler, stone, under, depth, (cubeX << 4) + x, genBase + y, (cubeZ << 4) + z);
                    primer.setBlockState(x, y, z, WorldGenUtils.getRandomBedrockReplacement(world, rand, state,
                            Coords.localToBlock(vanillaY, y), 5, topBedrock, bottomBedrock));
                    under++;
                    placed++;
                }
            }
        }
        if (tracing) {
            double[] ends = regions == null ? new double[] {Double.NaN, Double.NaN} : span(regions);
            ContentLog.LOGGER.debug("Sky cube {},{},{} laid {} of 4096, mask {} to {}, corners {} and {}, islands {} thickness {}",
                    cubeX, cubeY, cubeZ, placed, fixed(ends[0]), fixed(ends[1]),
                    fixed(regions == null ? Double.NaN : regions[0][0][0]), fixed(regions == null ? Double.NaN : regions[4][0][4]),
                    fixed(skyIslands), fixed(skyThickness));
        }
    }

    private static String fixed(double value) { return Double.isNaN(value) ? "n/a" : String.format("%.4f", value); }

    private Biome skySurfaceBiome(Biome column, @Nullable CaveRegionDef region, @Nullable BiomeDef band) {
        if (region != null && region.hasBiome()) {
            Biome named = ContentBiomes3D.named(region.biome);
            if (named != null) { return named; }
        }
        if (band != null) {
            Biome made = ContentBiomes3D.registered(band);
            if (made != null) { return made; }
        }
        return column;
    }

    private IBlockState skyStoneFor(Biome biome, @Nullable CaveRegionDef region, @Nullable BiomeDef band) {
        BiomeDef def = defOf(biome);
        IBlockState named = named(region == null ? null : region.sky);
        if (named == null) { named = named(band == null ? null : band.sky); }
        if (named == null) { named = named(def == null ? null : def.sky); }
        return named != null ? named : skyStone;
    }

    @Nullable private static IBlockState named(@Nullable SkyStone sky) { return sky == null || sky.stone.isEmpty() ? null : sky.state(); }

    private static double cascade(Biome biome, @Nullable CaveRegionDef region, @Nullable BiomeDef band, ToDoubleFunction<CaveRegionDef> regionValue, ToDoubleFunction<BiomeDef> defValue, double fallback) {
        if (region != null && !Double.isNaN(regionValue.applyAsDouble(region))) { return regionValue.applyAsDouble(region); }
        if (band != null && !Double.isNaN(defValue.applyAsDouble(band))) { return defValue.applyAsDouble(band); }
        BiomeDef def = defOf(biome);
        if (def != null && !Double.isNaN(defValue.applyAsDouble(def))) { return defValue.applyAsDouble(def); }
        return fallback;
    }

    private double skyIslandsFor(Biome biome, @Nullable CaveRegionDef region, @Nullable BiomeDef band) { return cascade(biome, region, band, r -> r.sky.islands, b -> b.sky.islands, skyIslands); }

    private double skyThicknessFor(Biome biome, @Nullable CaveRegionDef region, @Nullable BiomeDef band) { return cascade(biome, region, band, r -> r.sky.thickness, b -> b.sky.thickness, skyThickness); }

    @Nullable private static BiomeDef defOf(Biome biome) { return biome instanceof ContentBiome ? ((ContentBiome) biome).getDef() : null; }

    @Nullable private BiomeMesa bandedMesa(BiomeMesa biome) {
        if (biome.clayBands == null || biome.worldSeed != world.getSeed()) { biome.generateBands(world.getSeed()); }
        return biome.clayBands == null ? null : biome;
    }

    private IBlockState mesaState(BiomeMesa mesa, IBlockState top, IBlockState stone, int under, int depth, int worldX, int worldY, int worldZ, int genY) {
        if (under == 0) { return mesa.hasForest ? top : Blocks.HARDENED_CLAY.getDefaultState(); }
        if (under <= depth) { return mesa.getBand(worldX, worldY, worldZ); }
        return deep.veinState(worldX, genY, worldZ, stone);
    }

    private double gradient(double[][][] lattice, int x, int y, int z, int ySpan, int axis) {
        int lowX = axis == 0 ? Math.max(x - 1, 0) : x;
        int highX = axis == 0 ? Math.min(x + 1, Cube.SIZE - 1) : x;
        int lowY = axis == 1 ? Math.max(y - 1, 0) : y;
        int highY = axis == 1 ? Math.min(y + 1, ySpan) : y;
        int lowZ = axis == 2 ? Math.max(z - 1, 0) : z;
        int highZ = axis == 2 ? Math.min(z + 1, Cube.SIZE - 1) : z;
        return deep.trilerp(lattice, highX, highY, highZ) - deep.trilerp(lattice, lowX, lowY, lowZ);
    }

    private int stretched(int depth, double[][][] lattice, int x, int y, int z, int ySpan) {
        double slopeX = gradient(lattice, x, y, z, ySpan, 0);
        double slopeY = gradient(lattice, x, y, z, ySpan, 1);
        double slopeZ = gradient(lattice, x, y, z, ySpan, 2);
        double vertical = Math.abs(slopeY);
        if (vertical < 1.0E-6D) { return (int) Math.round(depth * SKY_SLOPE_LIMIT); }
        double length = Math.sqrt(slopeX * slopeX + slopeY * slopeY + slopeZ * slopeZ);
        return (int) Math.round(depth * MathHelper.clamp(length / vertical, 1.0D, SKY_SLOPE_LIMIT));
    }

    private IBlockState skyState(IBlockState top, IBlockState filler, IBlockState stone, int under, int depth, int worldX, int genY, int worldZ) {
        if (under == 0) { return top; }
        if (under <= depth) { return filler; }
        return deep.veinState(worldX, genY, worldZ, stone);
    }

    private IBlockState grounded(IBlockState state, IBlockState stone) {
        if (!(state.getBlock() instanceof BlockFalling)) { return state; }
        IBlockState firm = sandstoneFor(state);
        return firm != null ? firm : stone;
    }

    @Nullable private static IBlockState sandstoneFor(IBlockState filler) {
        if (filler.getBlock() != Blocks.SAND) { return null; }
        Block made = filler.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ? Blocks.RED_SANDSTONE : Blocks.SANDSTONE;
        return Objects.requireNonNull(made).getDefaultState();
    }

    private int skyDepthAt(int worldX, int worldZ) {
        double found = (deep.surfaceDepth.sample(worldX, 0.0D, worldZ) + 1.0D) * 0.5D;
        return MathHelper.clamp(1 + (int) (found * SKY_FILLER), 1, SKY_FILLER);
    }

    private IBlockState skySurfaceFor(IBlockState fallback, @Nullable BiomeDef band, boolean top) {
        if (band == null || (top ? band.topBlock : band.fillerBlock).isEmpty()) { return fallback; }
        Biome made = ContentBiomes3D.registered(band);
        if (made == null) { return fallback; }
        return top ? made.topBlock : made.fillerBlock;
    }

    private int solidAbove(double[][][] regions, double[][][] lattice, int x, int z, int worldBase, int genBase, int worldTop, double islands, double thickness, int depth) {
        int found = 0;
        for (int y = Cube.SIZE; y < Cube.SIZE + SKY_LOOKUP && found <= depth; y++) {
            if (skyOpen(regions, lattice, x, y, z, worldBase, genBase, worldTop, islands, thickness)) { break; }
            found++;
        }
        return found;
    }

    private boolean skyOpen(double[][][] regions, double[][][] lattice, int x, int y, int z, int worldBase, int genBase, int worldTop, double islands, double thickness) {
        if (worldBase + y >= worldTop) { return true; }
        if (genBase + y < skyLowest || genBase + y > skyHighest) { return true; }
        if (regions == null) { return deep.trilerp(lattice, x, y, z) <= DeepGeneration.CAVE_THRESHOLD; }
        double strength = (deep.trilerp(regions, x, y, z) - islands) / (1.0D - islands) * skyFade(genBase + y);
        if (strength <= 0.0D) { return true; }
        return deep.trilerp(lattice, x, y, z) > Math.min(strength, 1.0D) * thickness;
    }

    private double skyFade(int genY) {
        int distance = Math.min(genY - skyFloorGen, skyCeilGen - genY);
        if (distance >= skyTaper) { return 1.0D; }
        if (distance <= 0) { return 0.0D; }
        return distance / (double) skyTaper;
    }

    private double[] span(double[][][] regions) {
        double[] ends = {Double.MAX_VALUE, -1.0D};
        for (double[][] plane : regions) {
            for (int y = 0; y < Cube.SIZE / 8 + 1; y++) {
                for (double value : plane[y]) {
                    ends[0] = Math.min(ends[0], value);
                    ends[1] = Math.max(ends[1], value);
                }
            }
        }
        return ends;
    }

    private static double parseIslands(float value) {
        if (value >= -1.0F && value <= 1.0F) { return value; }
        ContentLog.LOGGER.error("skyIslands is {}, which is outside -1 to 1, so the islands keep their usual reach", value);
        return 0.5D;
    }

    private static double parseThickness(float value) {
        if (value >= 0.0F) { return value; }
        ContentLog.LOGGER.error("skyThickness is {}, which is below zero, so the islands keep their usual thickness", value);
        return 2.0D;
    }

    private static int[] parseHeights(int[] values) {
        int[] whole = {Integer.MIN_VALUE, Integer.MAX_VALUE};
        if (values.length == 0) { return whole; }
        if (values.length != 2 || values[0] >= values[1]) {
            ContentLog.LOGGER.error("skyHeights is {}, which is not a lowest and a highest with the lowest below it, so the islands fill the whole world above the window", Arrays.toString(values));
            return whole;
        }
        return values;
    }
}
