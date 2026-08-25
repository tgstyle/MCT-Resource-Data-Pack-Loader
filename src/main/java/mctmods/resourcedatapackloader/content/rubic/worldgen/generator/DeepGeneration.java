package mctmods.resourcedatapackloader.content.rubic.worldgen.generator;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.worldgen.CubePrimer;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBiomeMesa;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiome;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes3D;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveRegions;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Coords;
import mctmods.resourcedatapackloader.util.Settings;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMesa;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;

public class DeepGeneration {
    private static final int BLEND_TOP = 8;
    private static final int SKY_ISLAND_SHAPE = 0;
    private static final int SKY_CAVES = 1;
    private static final int SKY_FILLER = 4;
    private static final int SKY_HEADROOM = 8;
    private static final int SKY_TAPER = 24;
    private static final int SKY_LOOKUP = 8;
    private static final double SKY_SLOPE_LIMIT = 3.0D;
    private static final double CAVE_THRESHOLD = 0.0D;
    private final World world;
    private final long seed;
    private final int offsetBlocks;
    private final int genFloor;
    private final IBlockState deepStone;
    private final IBlockState skyStone;
    private final int skyShape;
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
    private final IBlockState extensionBottom;
    private final int caveScope;
    private final List<Vein> veins = new ArrayList<>();
    private final Field cheese;
    private final Field cheeseWide;
    private final Field layer;
    private final Field spagElev;
    private final Field spag2d;
    private final Field entrA;
    private final Field entrB;
    private final Field mouth;
    private final Field noodleToggle;
    private final Field noodleA;
    private final Field noodleB;
    private final Field pillar;
    private final Field pillarRare;
    private final Field pillarThick;
    private final Field surfaceDepth;
    private final boolean voidDimension;
    private final int dimension;
    @Nullable private final DeepRavines ravines;
    private final RubicAquifer aquifer;

    public DeepGeneration(World world, int offsetBlocks, IBlockState extensionBottom) {
        this.world = world;
        this.seed = world.getSeed();
        this.offsetBlocks = offsetBlocks;
        this.genFloor = ((IMinMaxHeight) world).rdpl$getMinHeight() - offsetBlocks;
        this.voidDimension = world.provider.getDimensionType() == DimensionType.THE_END;
        this.dimension = world.provider.getDimension();
        this.extensionBottom = extensionBottom;
        this.deepStone = parseState(scoped("deepStone", Config.worldgen.deepStone), "deepStone");
        this.skyStone = parseState(scoped("skyStone", Config.worldgen.skyStone), "skyStone");
        this.skyShape = parseShape(scoped("skyShape", Config.worldgen.skyShape));
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
        this.skyTaper = Math.max(4, Math.min(SKY_TAPER, (skyCeilGen - skyFloorGen + 1) / 4));
        this.caveScope = parseScope(scoped("noiseCaves", Config.worldgen.noiseCaves));
        int salt = 100;
        for (String spec : Settings.forDimension(ContentControl.lines(ContentControl.TERRAIN, "oreVeins", Config.worldgen.oreVeins), dimension, !voidDimension, "oreVeins")) {
            Vein vein = Vein.parse(spec, seed, salt);
            if (vein != null) { veins.add(vein); }
            salt += 10;
        }
        this.cheese = new Field(seed, 1, 128, 192, 4, 2.5D);
        this.cheeseWide = new Field(seed, 1, 128, 192, 1, 1.0D);
        this.layer = new Field(seed, 2, 256, 32, 1, 1.5D);
        this.spagElev = new Field(seed, 3, 1024, 1024, 1, 1.5D);
        this.spag2d = new Field(seed, 4, 128, 128, 1, 1.5D);
        this.entrA = new Field(seed, 5, 128, 128, 1, 1.5D);
        this.entrB = new Field(seed, 6, 128, 128, 1, 1.5D);
        this.mouth = new Field(seed, 7, 170, 256, 2, 0.9D);
        this.noodleToggle = new Field(seed, 8, 256, 256, 1, 1.5D);
        this.noodleA = new Field(seed, 9, 48, 48, 1, 1.5D);
        this.noodleB = new Field(seed, 10, 48, 48, 1, 1.5D);
        this.pillar = new Field(seed, 11, 5, 427, 1, 1.5D);
        this.pillarRare = new Field(seed, 12, 128, 128, 1, 1.5D);
        this.pillarThick = new Field(seed, 13, 128, 128, 1, 1.5D);
        this.surfaceDepth = new Field(seed, 14, 16, 16, 1, 1.0D);
        this.ravines = Boolean.parseBoolean(scoped("deepRavines", String.valueOf(Config.worldgen.deepRavines)))
                ? new DeepRavines(this, seed, Coords.blockToCube(offsetBlocks) - 1) : null;
        this.aquifer = new RubicAquifer(world, seed, genFloor + 10, offsetBlocks, barrierState(), this);
    }

    private String scoped(String key, String fallback) {
        List<String> found = Settings.forDimension(ContentControl.lines(ContentControl.TERRAIN, key, new String[] {fallback}), dimension, !voidDimension, key);
        return found.isEmpty() ? "" : found.get(found.size() - 1);
    }

    boolean carvedAt(int x, int genY, int z) { return density(x, genY, z, 0.0D) <= CAVE_THRESHOLD; }

    public static boolean reworksBand(World world) {
        if (!(world instanceof IRubicWorld) || !((IRubicWorld) world).rdpl$isRubicWorld()) { return false; }
        List<String> found = Settings.forDimension(ContentControl.lines(ContentControl.TERRAIN, "noiseCaves", new String[] {Config.worldgen.noiseCaves}),
                world.provider.getDimension(), world.provider.getDimensionType() != DimensionType.THE_END, "noiseCaves");
        return !found.isEmpty() && "world".equalsIgnoreCase(found.get(found.size() - 1));
    }

    public boolean wantsDeep() { return deepStone != null || caveScope > 0 || !veins.isEmpty() || ravines != null; }

    public boolean wantsSky() { return skyStone != null; }

    public void fillSkyCube(CubePrimer primer, int cubeX, int cubeY, int cubeZ, Random rand, boolean topBedrock, boolean bottomBedrock) {
        int worldTop = ((IMinMaxHeight) world).rdpl$getMaxHeight() - 1 - (skyShape == SKY_CAVES ? 0 : SKY_HEADROOM);
        int worldBase = Coords.cubeToMinBlock(cubeY);
        if (worldBase >= worldTop) { return; }
        int genBase = worldBase - offsetBlocks;
        if (genBase + Cube.SIZE - 1 < skyLowest || genBase > skyHighest) { return; }
        int steps = (Cube.SIZE + SKY_LOOKUP) / 8 + 1;
        double[][][] regions = null;
        if (skyShape != SKY_CAVES) {
            regions = sampleRegionLattice(cubeX << 4, genBase, cubeZ << 4, steps);
            if (strongest(regions) <= skyMaskIslands) { return; }
        }
        double[][][] lattice = sampleLattice(cubeX << 4, genBase, cubeZ << 4, steps);
        int vanillaY = cubeY - Coords.blockToCube(offsetBlocks);
        Biome[] column = world.getBiomeProvider().getBiomes(null, cubeX << 4, cubeZ << 4, Cube.SIZE, Cube.SIZE);
        for (int z = 0; z < Cube.SIZE; z++) {
            for (int x = 0; x < Cube.SIZE; x++) {
                Biome biome = column[(z << 4) | x];
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
                }
            }
        }
    }

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
        if (region != null && !region.skyStone.isEmpty()) {
            IBlockState named = region.skyState();
            if (named != null) { return named; }
        }
        if (band != null && !band.skyStone.isEmpty()) {
            IBlockState named = band.skyState();
            if (named != null) { return named; }
        }
        BiomeDef def = defOf(biome);
        if (def != null && !def.skyStone.isEmpty()) {
            IBlockState named = def.skyState();
            if (named != null) { return named; }
        }
        return skyStone;
    }

    private double skyIslandsFor(Biome biome, @Nullable CaveRegionDef region, @Nullable BiomeDef band) {
        if (region != null && !Float.isNaN(region.skyIslands)) { return region.skyIslands; }
        if (band != null && !Float.isNaN(band.skyIslands)) { return band.skyIslands; }
        BiomeDef def = defOf(biome);
        if (def != null && !Float.isNaN(def.skyIslands)) { return def.skyIslands; }
        return skyIslands;
    }

    private double skyThicknessFor(Biome biome, @Nullable CaveRegionDef region, @Nullable BiomeDef band) {
        if (region != null && !Float.isNaN(region.skyThickness)) { return region.skyThickness; }
        if (band != null && !Float.isNaN(band.skyThickness)) { return band.skyThickness; }
        BiomeDef def = defOf(biome);
        if (def != null && !Float.isNaN(def.skyThickness)) { return def.skyThickness; }
        return skyThickness;
    }

    @Nullable private static BiomeDef defOf(Biome biome) { return biome instanceof ContentBiome ? ((ContentBiome) biome).getDef() : null; }

    @Nullable private BiomeMesa bandedMesa(BiomeMesa biome) {
        IBiomeMesa access = (IBiomeMesa) biome;
        if (access.rdpl$clayBands() == null || access.rdpl$worldSeed() != world.getSeed()) { access.rdpl$generateBands(world.getSeed()); }
        return access.rdpl$clayBands() == null ? null : biome;
    }

    private IBlockState mesaState(BiomeMesa mesa, IBlockState top, IBlockState stone, int under, int depth, int worldX, int worldY, int worldZ, int genY) {
        IBiomeMesa access = (IBiomeMesa) mesa;
        if (under == 0) { return access.rdpl$hasForest() ? top : Objects.requireNonNull(Blocks.HARDENED_CLAY).getDefaultState(); }
        if (under <= depth) { return access.rdpl$getBand(worldX, worldY, worldZ); }
        return veinState(worldX, genY, worldZ, stone);
    }

    private double gradient(double[][][] lattice, int x, int y, int z, int ySpan, int axis) {
        int lowX = axis == 0 ? Math.max(x - 1, 0) : x;
        int highX = axis == 0 ? Math.min(x + 1, Cube.SIZE - 1) : x;
        int lowY = axis == 1 ? Math.max(y - 1, 0) : y;
        int highY = axis == 1 ? Math.min(y + 1, ySpan) : y;
        int lowZ = axis == 2 ? Math.max(z - 1, 0) : z;
        int highZ = axis == 2 ? Math.min(z + 1, Cube.SIZE - 1) : z;
        return trilerp(lattice, highX, highY, highZ) - trilerp(lattice, lowX, lowY, lowZ);
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
        return veinState(worldX, genY, worldZ, stone);
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
        double found = (surfaceDepth.sample(worldX, 0.0D, worldZ) + 1.0D) * 0.5D;
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
        if (regions == null) { return trilerp(lattice, x, y, z) <= CAVE_THRESHOLD; }
        double strength = (trilerp(regions, x, y, z) - islands) / (1.0D - islands) * skyFade(genBase + y);
        if (strength <= 0.0D) { return true; }
        return trilerp(lattice, x, y, z) > Math.min(strength, 1.0D) * thickness;
    }

    private double skyFade(int genY) {
        int distance = Math.min(genY - skyFloorGen, skyCeilGen - genY);
        if (distance >= skyTaper) { return 1.0D; }
        if (distance <= 0) { return 0.0D; }
        return distance / (double) skyTaper;
    }

    private double strongest(double[][][] regions) {
        double best = -1.0D;
        for (double[][] plane : regions) {
            for (int y = 0; y < Cube.SIZE / 8 + 1; y++) {
                for (double value : plane[y]) { best = Math.max(best, value); }
            }
        }
        return best;
    }

    private double[][][] sampleRegionLattice(int worldX, int genY, int worldZ, int ySteps) {
        double[][][] lattice = new double[5][ySteps][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < ySteps; y++) {
                for (int z = 0; z < 5; z++) { lattice[x][y][z] = cheeseWide.sample(worldX + (x << 2), genY + (y << 3), worldZ + (z << 2)); }
            }
        }
        return lattice;
    }

    public void populateDeepCube(int cubeX, int cubeY, int cubeZ) {
        if (caveScope == 0) { return; }
        Random random = new Random(seed ^ (cubeX * 341873128712L + cubeZ * 132897987541L + cubeY * 1052717L));
        int x = Coords.cubeToMinBlock(cubeX) + random.nextInt(16) + 8;
        int y = Coords.cubeToMinBlock(cubeY) + 1 + random.nextInt(15);
        int z = Coords.cubeToMinBlock(cubeZ) + random.nextInt(16) + 8;
        if (y < ((IMinMaxHeight) world).rdpl$getMinHeight() + 6) { return; }
        new WorldGenDungeons().generate(world, random, new BlockPos(x, y, z));
    }

    private boolean dressesBand() { return deepStone != null || caveScope == 2 || !veins.isEmpty(); }

    private boolean sealsSeam() { return caveScope == 1 && genFloor < 0; }

    public void fillDeepCube(CubePrimer primer, int cubeX, int cubeY, int cubeZ, Random rand, boolean topBedrock, boolean bottomBedrock) {
        int vanillaY = cubeY - Coords.blockToCube(offsetBlocks);
        double[][][] lattice = caveScope > 0 ? sampleLattice(cubeX << 4, Coords.cubeToMinBlock(cubeY) - offsetBlocks, cubeZ << 4) : null;
        for (int y = 0; y < Cube.SIZE; y++) {
            for (int z = 0; z < Cube.SIZE; z++) {
                for (int x = 0; x < Cube.SIZE; x++) {
                    int worldX = (cubeX << 4) + x;
                    int worldZ = (cubeZ << 4) + z;
                    int genY = Coords.cubeToMinBlock(cubeY) + y - offsetBlocks;
                    double carved = lattice != null ? trilerp(lattice, x, y, z) : 1.0D;
                    if (carved <= CAVE_THRESHOLD) {
                        IBlockState opened = fluidOrAir(worldX, genY, worldZ, carved);
                        opened = WorldGenUtils.getRandomBedrockReplacement(world, rand, opened, Coords.localToBlock(vanillaY, y), 5, topBedrock, bottomBedrock);
                        primer.setBlockState(x, y, z, opened);
                        continue;
                    }
                    IBlockState state = deepStone != null ? deepStone : extensionBottom;
                    state = veinState(worldX, genY, worldZ, state);
                    int blockY = Coords.localToBlock(vanillaY, y);
                    state = WorldGenUtils.getRandomBedrockReplacement(world, rand, state, blockY, 5, topBedrock, bottomBedrock);
                    primer.setBlockState(x, y, z, state);
                }
            }
        }
        if (ravines != null) { ravines.carve(primer, cubeX, cubeY, cubeZ); }
    }

    public void dressBandPrimer(ChunkPrimer primer, int cubeX, int cubeZ) {
        if (!dressesBand()) {
            if (sealsSeam()) { sealSeamFluids(primer, cubeX, cubeZ); }
            return;
        }
        int[] tops = caveScope == 2 ? columnTops(primer) : null;
        double[][][] lattice = tops != null ? sampleBandLattice(cubeX << 4, cubeZ << 4, tops) : null;
        for (int y = 0; y < 256; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    IBlockState state = primer.getBlockState(x, y, z);
                    int worldX = (cubeX << 4) + x;
                    int worldZ = (cubeZ << 4) + z;
                    if (lattice != null && state.getMaterial() == Material.LAVA) {
                        double lavaCarved = trilerp(lattice, x, y, z);
                        primer.setBlockState(x, y, z, lavaCarved <= CAVE_THRESHOLD ? fluidOrAir(worldX, y, worldZ, lavaCarved) : Blocks.AIR.getDefaultState());
                        continue;
                    }
                    if (!carvable(state)) { continue; }
                    double carved = lattice != null ? trilerp(lattice, x, y, z) : 1.0D;
                    if (carved <= CAVE_THRESHOLD) {
                        boolean carve = tops[(z << 4) | x] - y >= 16 || mouthTerm(worldX, y, worldZ) <= 0.0D;
                        if (carve && y < 255 && primer.getBlockState(x, y + 1, z).getMaterial() == Material.WATER) { carve = false; }
                        if (carve) {
                            primer.setBlockState(x, y, z, fluidOrAir(worldX, y, worldZ, carved));
                            continue;
                        }
                    }
                    if (state != Blocks.STONE.getDefaultState() && state != extensionBottom) { continue; }
                    if (deepStone != null && y < BLEND_TOP) {
                        float chance = y == 0 ? 1.0F : (BLEND_TOP - y) / (float) BLEND_TOP;
                        if (hash01(worldX, y, worldZ, 21) < chance) { state = deepStone; }
                    }
                    state = veinState(worldX, y, worldZ, state);
                    primer.setBlockState(x, y, z, state);
                }
            }
        }
        if (tops != null) { restoreEmptiedColumns(primer, tops); }
        if (tops != null) { sealBandFluids(primer); }
        if (sealsSeam()) { sealSeamFluids(primer, cubeX, cubeZ); }
    }

    private void sealSeamFluids(ChunkPrimer primer, int cubeX, int cubeZ) {
        double[][][] lattice = sampleLattice(cubeX << 4, -Cube.SIZE, cubeZ << 4);
        for (int z = 0; z < Cube.SIZE; z++) {
            for (int x = 0; x < Cube.SIZE; x++) {
                Material material = primer.getBlockState(x, 0, z).getMaterial();
                if (material != Material.WATER && material != Material.LAVA) { continue; }
                double carved = trilerp(lattice, x, Cube.SIZE - 1, z);
                if (carved > CAVE_THRESHOLD) { continue; }
                int worldX = (cubeX << 4) + x;
                int worldZ = (cubeZ << 4) + z;
                if (fluidOrAir(worldX, -1, worldZ, carved).getMaterial() != Material.AIR) { continue; }
                primer.setBlockState(x, 0, z, barrierState());
            }
        }
    }

    private void sealBandFluids(ChunkPrimer primer) {
        for (int y = 0; y < 256; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    Material material = primer.getBlockState(x, y, z).getMaterial();
                    if (material != Material.WATER && material != Material.LAVA) { continue; }
                    boolean leak = y > 0 && primer.getBlockState(x, y - 1, z).getMaterial() == Material.AIR;
                    if (!leak && x > 0) { leak = primer.getBlockState(x - 1, y, z).getMaterial() == Material.AIR; }
                    if (!leak && x < 15) { leak = primer.getBlockState(x + 1, y, z).getMaterial() == Material.AIR; }
                    if (!leak && z > 0) { leak = primer.getBlockState(x, y, z - 1).getMaterial() == Material.AIR; }
                    if (!leak && z < 15) { leak = primer.getBlockState(x, y, z + 1).getMaterial() == Material.AIR; }
                    if (leak) { primer.setBlockState(x, y, z, barrierState()); }
                }
            }
        }
    }

    private void restoreEmptiedColumns(ChunkPrimer primer, int[] tops) {
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int top = tops[(z << 4) | x];
                if (top <= 0) { continue; }
                boolean solid = false;
                for (int y = top; y >= 0; y--) {
                    if (primer.getBlockState(x, y, z).getMaterial() != Material.AIR) {
                        solid = true;
                        break;
                    }
                }
                if (!solid) { primer.setBlockState(x, top, z, deepStone != null && top < BLEND_TOP ? deepStone : Blocks.STONE.getDefaultState()); }
            }
        }
    }

    private int[] columnTops(ChunkPrimer primer) {
        int[] tops = new int[256];
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int top = 0;
                for (int y = 255; y >= 0; y--) {
                    if (primer.getBlockState(x, y, z).getMaterial() != Material.AIR) { top = y; break; }
                }
                tops[(z << 4) | x] = top;
            }
        }
        return tops;
    }

    private boolean carvable(IBlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.STONE || block == Blocks.DIRT || block == Blocks.GRAVEL || block == Blocks.GRASS || block == Blocks.SANDSTONE) { return true; }
        if (block == extensionBottom.getBlock()) { return true; }
        return deepStone != null && block == deepStone.getBlock();
    }

    private double[][][] sampleLattice(int worldX, int genY, int worldZ) { return sampleLattice(worldX, genY, worldZ, Cube.SIZE / 8 + 1); }

    private double[][][] sampleLattice(int worldX, int genY, int worldZ, int ySteps) {
        double[][][] lattice = new double[5][ySteps][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < ySteps; y++) {
                for (int z = 0; z < 5; z++) { lattice[x][y][z] = density(worldX + (x << 2), genY + (y << 3), worldZ + (z << 2), 0.0D); }
            }
        }
        return lattice;
    }

    private double[][][] sampleBandLattice(int worldX, int worldZ, int[] tops) {
        double[][][] lattice = new double[5][33][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 33; y++) {
                for (int z = 0; z < 5; z++) {
                    int top = tops[(Math.min(z << 2, 15) << 4) | Math.min(x << 2, 15)];
                    double fade = MathHelper.clamp(0.5D - 0.01D * (top - (y << 3)), 0.0D, 0.5D);
                    lattice[x][y][z] = density(worldX + (x << 2), y << 3, worldZ + (z << 2), fade);
                }
            }
        }
        return lattice;
    }

    private double trilerp(double[][][] lattice, int x, int y, int z) {
        int x0 = x >> 2;
        int y0 = y >> 3;
        int z0 = z >> 2;
        double fx = (x & 3) / 4.0D;
        double fy = (y & 7) / 8.0D;
        double fz = (z & 3) / 4.0D;
        double c00 = lerp(fx, lattice[x0][y0][z0], lattice[x0 + 1][y0][z0]);
        double c01 = lerp(fx, lattice[x0][y0][z0 + 1], lattice[x0 + 1][y0][z0 + 1]);
        double c10 = lerp(fx, lattice[x0][y0 + 1][z0], lattice[x0 + 1][y0 + 1][z0]);
        double c11 = lerp(fx, lattice[x0][y0 + 1][z0 + 1], lattice[x0 + 1][y0 + 1][z0 + 1]);
        return lerp(fy, lerp(fz, c00, c01), lerp(fz, c10, c11));
    }

    private double density(int x, int y, int z, double cheeseFade) {
        int aboveFloor = y - genFloor;
        double layered = layer.sample(x, y, z);
        double result = 4.0D * layered * layered + MathHelper.clamp(0.27D + cheese.sample(x, y, z), -1.0D, 1.0D) + cheeseFade;
        if (aboveFloor < 24) {
            double regionalDepth = -cheeseWide.sample(x, y, z) - 0.3D;
            if (regionalDepth > 0.0D) { result += (24 - aboveFloor) / 24.0D * regionalDepth * 12.0D; }
            result += (24 - aboveFloor) * 0.01D;
        }
        double elevation = spagElev.sample(x, 0, z) * 8.0D;
        double slab = Math.abs(elevation - 0.125D * y) - 0.95D;
        double spaghetti = Math.max(Math.abs(spag2d.sample(x, y, z)) - 0.1D, slab * slab * slab);
        result = Math.min(result, spaghetti);
        double tubes = Math.max(Math.abs(entrA.sample(x, y, z)), Math.abs(entrB.sample(x, y, z))) - 0.075D;
        result = Math.min(result, Math.min(tubes, mouthTerm(x, y, z)));
        double column = (2.0D * pillar.sample(x, y, z) + pillarRare.sample(x, y, z) - 1.0D);
        double thick = (pillarThick.sample(x, y, z) + 1.0D) * 0.55D;
        column *= thick * thick * thick;
        if (column >= 0.03D) { result = Math.max(result, column); }
        if (y >= genFloor + 4 && noodleToggle.sample(x, y, z) > 0.0D) {
            double noodle = 1.5D * Math.max(Math.abs(noodleA.sample(x, y, z)), Math.abs(noodleB.sample(x, y, z))) - 0.075D;
            result = Math.min(result, noodle);
        }
        if (aboveFloor < 8) { result += (8 - aboveFloor) * 0.02D; }
        return result;
    }

    private double mouthTerm(int x, int y, int z) { return mouth.sample(x, y, z) + 0.37D + 0.3D * MathHelper.clamp((30.0D - y) / 40.0D, 0.0D, 1.0D); }

    IBlockState ravineFill(int worldX, int worldY, int worldZ) { return fluidOrAir(worldX, worldY - offsetBlocks, worldZ, CAVE_THRESHOLD); }

    private IBlockState fluidOrAir(int x, int genY, int z, double density) {
        int lavaLevel = genFloor + 10;
        if (genY < lavaLevel) { return Blocks.LAVA.getDefaultState(); }
        return aquifer.substance(x, genY, z, density);
    }

    private IBlockState barrierState() { return deepStone != null ? deepStone : extensionBottom; }

    private IBlockState veinState(int x, int genY, int z, IBlockState base) {
        for (Vein vein : veins) {
            if (genY < vein.minY || genY > vein.maxY) { continue; }
            double veininess = vein.veininess.sample(x, genY, z);
            double edge = MathHelper.clamp(Math.min(genY - vein.minY, vein.maxY - genY) / 20.0D * 0.2D - 0.2D, -0.2D, 0.0D);
            if (Math.abs(veininess) + edge < 0.4D) { continue; }
            if (hash01(x, genY, z, vein.salt) > 0.7F) { continue; }
            if (Math.max(Math.abs(vein.branchA.sample(x, genY, z)), Math.abs(vein.branchB.sample(x, genY, z))) >= 0.08D) { continue; }
            float richness = (float) MathHelper.clamp((Math.abs(veininess) - 0.4D) * 5.0D * 0.2D + 0.1D, 0.1D, 0.3D);
            if (hash01(x, genY, z, vein.salt + 1) < richness) {
                return hash01(x, genY, z, vein.salt + 2) < 0.02F && vein.extra != null ? vein.extra : vein.ore;
            }
            return vein.filler;
        }
        return base;
    }

    private float hash01(int x, int y, int z, int salt) {
        long h = seed ^ (salt * 0x9E3779B97F4A7C15L);
        h ^= x * 0x2545F4914F6CDD1DL;
        h ^= (long) y * 0x6C62272E07BB0142L;
        h ^= (long) z * 0xCBF29CE484222325L;
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        return (h >>> 40) / (float) (1 << 24);
    }

    private static double lerp(double t, double a, double b) { return a + t * (b - a); }


    public static IBlockState parseState(String name, String key) {
        if (name == null || name.trim().isEmpty()) { return null; }
        String trimmed = name.trim();
        int meta = 0;
        int at = trimmed.indexOf('@');
        if (at >= 0) {
            try {
                meta = Integer.parseInt(trimmed.substring(at + 1));
            } catch (NumberFormatException bad) {
                Rubic.LOGGER.error("The {} block {} has a meta that is not a number, so it is not used", key, name);
                return null;
            }
            trimmed = trimmed.substring(0, at);
        }
        Block block = Block.getBlockFromName(trimmed);
        if (block == null) {
            Rubic.LOGGER.error("The {} block {} is not a known block, so it is not used", key, name);
            return null;
        }
        return meta == 0 ? block.getDefaultState() : Block.getStateById(Block.getIdFromBlock(block) + (meta << 12));
    }

    private static int parseShape(String value) {
        if (value == null || value.trim().isEmpty() || value.trim().equalsIgnoreCase("islands")) { return SKY_ISLAND_SHAPE; }
        if (value.trim().equalsIgnoreCase("caves")) { return SKY_CAVES; }
        Rubic.LOGGER.error("skyShape is {}, which is not islands or caves, so the sky is made of islands", value);
        return SKY_ISLAND_SHAPE;
    }

    private static double parseIslands(float value) {
        if (value >= -1.0F && value <= 1.0F) { return value; }
        Rubic.LOGGER.error("skyIslands is {}, which is outside -1 to 1, so the islands keep their usual reach", value);
        return 0.2D;
    }

    private static double parseThickness(float value) {
        if (value >= 0.0F) { return value; }
        Rubic.LOGGER.error("skyThickness is {}, which is below zero, so the islands keep their usual thickness", value);
        return 2.0D;
    }

    private static int[] parseHeights(int[] values) {
        int[] whole = {Integer.MIN_VALUE, Integer.MAX_VALUE};
        if (values.length == 0) { return whole; }
        if (values.length != 2 || values[0] >= values[1]) {
            Rubic.LOGGER.error("skyHeights is {}, which is not a lowest and a highest with the lowest below it, so the islands fill the whole world above the window", Arrays.toString(values));
            return whole;
        }
        return values;
    }

    private static int parseScope(String value) {
        if (value == null || value.trim().isEmpty() || value.trim().equalsIgnoreCase("off")) { return 0; }
        if (value.trim().equalsIgnoreCase("deep")) { return 1; }
        if (value.trim().equalsIgnoreCase("world")) { return 2; }
        Rubic.LOGGER.error("noiseCaves is {}, which is not off, deep or world, so the caves stay off", value);
        return 0;
    }

    private static class Vein {
        final IBlockState ore;
        final IBlockState extra;
        final IBlockState filler;
        final int minY;
        final int maxY;
        final int salt;
        final Field veininess;
        final Field branchA;
        final Field branchB;

        private Vein(IBlockState ore, IBlockState extra, IBlockState filler, int minY, int maxY, long seed, int salt) {
            this.ore = ore;
            this.extra = extra;
            this.filler = filler;
            this.minY = minY;
            this.maxY = maxY;
            this.salt = salt;
            this.veininess = new Field(seed, salt + 3, 171, 171, 1, 1.5D);
            this.branchA = new Field(seed, salt + 4, 32, 32, 1, 1.5D);
            this.branchB = new Field(seed, salt + 5, 32, 32, 1, 1.5D);
        }

        static Vein parse(String spec, long seed, int salt) {
            String[] parts = spec.split(",");
            if (parts.length != 5) {
                Rubic.LOGGER.error("An oreVeins entry needs ore,extra,filler,lowest,highest but {} has {} part(s), so it is skipped", spec, parts.length);
                return null;
            }
            IBlockState ore = parseState(parts[0], "oreVeins ore");
            IBlockState extra = parseState(parts[1], "oreVeins extra");
            IBlockState filler = parseState(parts[2], "oreVeins filler");
            if (ore == null || filler == null) { return null; }
            try {
                return new Vein(ore, extra, filler, Integer.parseInt(parts[3].trim()), Integer.parseInt(parts[4].trim()), seed, salt);
            } catch (NumberFormatException bad) {
                Rubic.LOGGER.error("An oreVeins entry has heights {} and {}, which are not numbers, so it is skipped", parts[3], parts[4]);
                return null;
            }
        }
    }

    static class Field {
        private final int[] perm = new int[512];
        private final double freqXZ;
        private final double freqY;
        private final int octaves;
        private final double scale;

        Field(long seed, int salt, double waveXZ, double waveY, int octaves, double scale) {
            this.freqXZ = 1.0D / waveXZ;
            this.freqY = 1.0D / waveY;
            this.octaves = octaves;
            this.scale = scale;
            Random random = new Random(seed ^ (salt * 0x9E3779B97F4A7C15L));
            int[] source = new int[256];
            for (int index = 0; index < 256; index++) { source[index] = index; }
            for (int index = 255; index > 0; index--) {
                int swap = random.nextInt(index + 1);
                int held = source[index];
                source[index] = source[swap];
                source[swap] = held;
            }
            for (int index = 0; index < 512; index++) { perm[index] = source[index & 255]; }
        }

        double sample(double x, double y, double z) {
            double total = 0.0D;
            double amplitude = 1.0D;
            double reach = 0.0D;
            double fx = freqXZ;
            double fy = freqY;
            for (int octave = 0; octave < octaves; octave++) {
                total += noise(x * fx, y * fy, z * fx) * amplitude;
                reach += amplitude;
                amplitude *= 0.5D;
                fx *= 2.0D;
                fy *= 2.0D;
            }
            return MathHelper.clamp(total * scale / reach, -1.0D, 1.0D);
        }

        private double noise(double x, double y, double z) {
            int cellX = (int) Math.floor(x) & 255;
            int cellY = (int) Math.floor(y) & 255;
            int cellZ = (int) Math.floor(z) & 255;
            double dx = x - Math.floor(x);
            double dy = y - Math.floor(y);
            double dz = z - Math.floor(z);
            double u = fade(dx);
            double v = fade(dy);
            double w = fade(dz);
            int a = perm[cellX] + cellY;
            int aa = perm[a] + cellZ;
            int ab = perm[a + 1] + cellZ;
            int b = perm[cellX + 1] + cellY;
            int ba = perm[b] + cellZ;
            int bb = perm[b + 1] + cellZ;
            return lerp(w, lerp(v, lerp(u, grad(perm[aa], dx, dy, dz), grad(perm[ba], dx - 1, dy, dz)),
                            lerp(u, grad(perm[ab], dx, dy - 1, dz), grad(perm[bb], dx - 1, dy - 1, dz))),
                    lerp(v, lerp(u, grad(perm[aa + 1], dx, dy, dz - 1), grad(perm[ba + 1], dx - 1, dy, dz - 1)),
                            lerp(u, grad(perm[ab + 1], dx, dy - 1, dz - 1), grad(perm[bb + 1], dx - 1, dy - 1, dz - 1))));
        }

        private static double fade(double t) { return t * t * t * (t * (t * 6.0D - 15.0D) + 10.0D); }

        private static double grad(int hash, double x, double y, double z) {
            int h = hash & 15;
            double u = h < 8 ? x : y;
            double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }
}
