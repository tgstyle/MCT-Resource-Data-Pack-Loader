package mctmods.resourcedatapackloader.content.rubic.worldgen.generator;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IMinMaxHeight;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.content.rubic.worldgen.CubePrimer;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DeepGeneration {
    private static final int BLEND_TOP = 8;
    private static final double CAVE_THRESHOLD = 0.0D;
    private final World world;
    private final long seed;
    private final int offsetBlocks;
    private final int genFloor;
    private final IBlockState deepStone;
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
    private final RubicAquifer aquifer;

    public DeepGeneration(World world, int offsetBlocks, IBlockState extensionBottom) {
        this.world = world;
        this.seed = world.getSeed();
        this.offsetBlocks = offsetBlocks;
        this.genFloor = ((IMinMaxHeight) world).rdpl$getMinHeight() - offsetBlocks;
        this.extensionBottom = extensionBottom;
        this.deepStone = parseState(ContentControl.text(ContentControl.TERRAIN, "deepStone", Config.worldgen.deepStone), "deepStone");
        this.caveScope = parseScope(ContentControl.text(ContentControl.TERRAIN, "noiseCaves", Config.worldgen.noiseCaves));
        int salt = 100;
        for (String spec : ContentControl.list(ContentControl.TERRAIN, "oreVeins", Config.worldgen.oreVeins)) {
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
        this.aquifer = new RubicAquifer(world, seed, genFloor + 10, offsetBlocks, barrierState(), this);
    }

    boolean carvedAt(int x, int genY, int z) { return density(x, genY, z, 0.0D) <= CAVE_THRESHOLD; }

    public static boolean reworksBand(World world) {
        if (!(world instanceof IRubicWorld) || !((IRubicWorld) world).rdpl$isRubicWorld()) { return false; }
        String scope = ContentControl.text(ContentControl.TERRAIN, "noiseCaves", Config.worldgen.noiseCaves);
        return scope != null && "world".equalsIgnoreCase(scope.trim());
    }

    public boolean wantsDeep() { return deepStone != null || caveScope > 0 || !veins.isEmpty(); }

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
    }

    public void dressBandPrimer(ChunkPrimer primer, int cubeX, int cubeZ) {
        if (!dressesBand()) { return; }
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

    private double[][][] sampleLattice(int worldX, int genY, int worldZ) {
        int ySteps = Cube.SIZE / 8 + 1;
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
