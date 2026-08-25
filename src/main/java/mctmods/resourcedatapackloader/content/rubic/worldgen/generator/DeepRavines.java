package mctmods.resourcedatapackloader.content.rubic.worldgen.generator;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.worldgen.CubePrimer;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public final class DeepRavines {
    private static final int RANGE = 8;
    private static final int RANGE_Y = 2;
    private static final int RARITY = 100;
    private static final int CARVE_RARITY = 4;
    private static final double VERT_SIZE = 3.0D;
    private static final double SIZE_ADD = 1.5D;
    private static final double MIN_SIZE_FACTOR = 0.75D;
    private static final double MAX_SIZE_FACTOR = 1.0D;
    private static final double STRETCH_Y = 6.0D;
    private static final float FLATTEN = 0.7F;
    private static final float CHANGE_FACTOR = 0.05F;
    private static final float PREV_HORIZ_WEIGHT = 0.5F;
    private static final float PREV_VERT_WEIGHT = 0.8F;
    private static final float MAX_HORIZ_CHANGE = 4.0F;
    private static final float MAX_VERT_CHANGE = 2.0F;
    private final DeepGeneration deep;
    private final long seed;
    private final int highestCube;
    private final float[] widths = new float[256];

    DeepRavines(DeepGeneration deep, long seed, int highestCube) {
        this.deep = deep;
        this.seed = seed;
        this.highestCube = highestCube;
    }

    void carve(CubePrimer primer, int cubeX, int cubeY, int cubeZ) {
        if (cubeY > highestCube + RANGE_Y) { return; }
        Random random = new Random(seed);
        long mulX = random.nextLong();
        long mulY = random.nextLong();
        long mulZ = random.nextLong();
        for (int originX = cubeX - RANGE; originX <= cubeX + RANGE; originX++) {
            long hashX = originX * mulX ^ seed;
            for (int originY = cubeY - RANGE_Y; originY <= cubeY + RANGE_Y; originY++) {
                long hashY = originY * mulY ^ hashX;
                for (int originZ = cubeZ - RANGE; originZ <= cubeZ + RANGE; originZ++) {
                    random.setSeed(originZ * mulZ ^ hashY);
                    start(random, primer, cubeX, cubeY, cubeZ, originX, originY, originZ);
                }
            }
        }
    }

    private void start(Random random, CubePrimer primer, int cubeX, int cubeY, int cubeZ, int originX, int originY, int originZ) {
        if (random.nextInt(RARITY) != 0 || originY > highestCube) { return; }
        double startX = Coords.localToBlock(originX, random.nextInt(Cube.SIZE));
        double startY = Coords.localToBlock(originY, random.nextInt(Cube.SIZE));
        double startZ = Coords.localToBlock(originZ, random.nextInt(Cube.SIZE));
        float horizAngle = random.nextFloat() * (float) Math.PI * 2.0F;
        float vertAngle = (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
        float size = (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
        walk(primer, random.nextLong(), cubeX, cubeY, cubeZ, startX, startY, startZ, size, horizAngle, vertAngle);
    }

    private void walk(CubePrimer primer, long walkSeed, int cubeX, int cubeY, int cubeZ,
            double x, double y, double z, float size, float horizAngle, float vertAngle) {
        Random random = new Random(walkSeed);
        float horizChange = 0.0F;
        float vertChange = 0.0F;
        int reach = Coords.cubeToMinBlock(RANGE - 1);
        int steps = reach - random.nextInt(reach / 4);
        widths(random);
        for (int walked = 0; walked < steps; walked++) {
            double horizSize = SIZE_ADD + MathHelper.sin(walked / (float) steps * (float) Math.PI) * size;
            double vertSize = horizSize * VERT_SIZE;
            horizSize *= random.nextFloat() * (MAX_SIZE_FACTOR - MIN_SIZE_FACTOR) + MIN_SIZE_FACTOR;
            vertSize *= random.nextFloat() * (MAX_SIZE_FACTOR - MIN_SIZE_FACTOR) + MIN_SIZE_FACTOR;
            float flat = MathHelper.cos(vertAngle);
            x += MathHelper.cos(horizAngle) * flat;
            y += MathHelper.sin(vertAngle);
            z += MathHelper.sin(horizAngle) * flat;
            vertAngle *= FLATTEN;
            vertAngle += vertChange * CHANGE_FACTOR;
            horizAngle += horizChange * CHANGE_FACTOR;
            vertChange *= PREV_VERT_WEIGHT;
            horizChange *= PREV_HORIZ_WEIGHT;
            vertChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * MAX_VERT_CHANGE;
            horizChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * MAX_HORIZ_CHANGE;
            if (random.nextInt(CARVE_RARITY) == 0) { continue; }
            double awayX = x - (Coords.cubeToMinBlock(cubeX) + (double) Cube.SIZE / 2);
            double awayZ = z - (Coords.cubeToMinBlock(cubeZ) + (double) Cube.SIZE / 2);
            double left = steps - walked;
            double span = size + SIZE_ADD + Cube.SIZE;
            if (awayX * awayX + awayZ * awayZ - left * left > span * span) { return; }
            cut(primer, cubeX, cubeY, cubeZ, x, y, z, horizSize, vertSize);
        }
    }

    private void cut(CubePrimer primer, int cubeX, int cubeY, int cubeZ,
            double x, double y, double z, double horizSize, double vertSize) {
        double midX = Coords.cubeToMinBlock(cubeX) + (double) Cube.SIZE / 2;
        double midY = Coords.cubeToMinBlock(cubeY) + (double) Cube.SIZE / 2;
        double midZ = Coords.cubeToMinBlock(cubeZ) + (double) Cube.SIZE / 2;
        if (x < midX - Cube.SIZE - horizSize * 2.0D || x > midX + Cube.SIZE + horizSize * 2.0D) { return; }
        if (y < midY - Cube.SIZE - vertSize * 2.0D || y > midY + Cube.SIZE + vertSize * 2.0D) { return; }
        if (z < midZ - Cube.SIZE - horizSize * 2.0D || z > midZ + Cube.SIZE + horizSize * 2.0D) { return; }
        int minX = bound(MathHelper.floor(x - horizSize) - Coords.cubeToMinBlock(cubeX) - 1);
        int maxX = bound(MathHelper.floor(x + horizSize) - Coords.cubeToMinBlock(cubeX) + 1);
        int minY = bound(MathHelper.floor(y - vertSize) - Coords.cubeToMinBlock(cubeY) - 1);
        int maxY = bound(MathHelper.floor(y + vertSize) - Coords.cubeToMinBlock(cubeY) + 1);
        int minZ = bound(MathHelper.floor(z - horizSize) - Coords.cubeToMinBlock(cubeZ) - 1);
        int maxZ = bound(MathHelper.floor(z + horizSize) - Coords.cubeToMinBlock(cubeZ) + 1);
        if (minX >= maxX || minY >= maxY || minZ >= maxZ) { return; }
        if (wet(primer, minX, minY, minZ, maxX, maxY, maxZ)) { return; }
        for (int localX = minX; localX < maxX; localX++) {
            double awayX = away(cubeX, localX, x, horizSize);
            for (int localZ = minZ; localZ < maxZ; localZ++) {
                double awayZ = away(cubeZ, localZ, z, horizSize);
                if (awayX * awayX + awayZ * awayZ >= 1.0D) { continue; }
                for (int localY = minY; localY < maxY; localY++) {
                    double awayY = away(cubeY, localY, y, vertSize);
                    double narrowing = widths[(localY + cubeY * Cube.SIZE) & 0xFF];
                    if ((awayX * awayX + awayZ * awayZ) * narrowing + awayY * awayY / STRETCH_Y >= 1.0D) { continue; }
                    if (!replaceable(primer.getBlockState(localX, localY, localZ))) { continue; }
                    primer.setBlockState(localX, localY, localZ, deep.ravineFill(Coords.localToBlock(cubeX, localX),
                            Coords.localToBlock(cubeY, localY), Coords.localToBlock(cubeZ, localZ)));
                }
            }
        }
    }

    private boolean wet(CubePrimer primer, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                if (water(primer.getBlockState(x, y, minZ)) || water(primer.getBlockState(x, y, maxZ - 1))) { return true; }
            }
        }
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                if (water(primer.getBlockState(x, minY, z)) || water(primer.getBlockState(x, maxY - 1, z))) { return true; }
            }
        }
        for (int y = minY; y < maxY; y++) {
            for (int z = minZ; z < maxZ; z++) {
                if (water(primer.getBlockState(minX, y, z)) || water(primer.getBlockState(maxX - 1, y, z))) { return true; }
            }
        }
        return false;
    }

    private void widths(Random random) {
        float value = 1.0F;
        for (int index = 0; index < widths.length; index++) {
            if (index == 0 || random.nextInt(3) == 0) { value = 1.0F + random.nextFloat() * random.nextFloat(); }
            widths[index] = value * value;
        }
    }

    private static double away(int cube, int local, double target, double size) {
        return (Coords.localToBlock(cube, local) + 0.5D - target) / size;
    }

    private static boolean water(IBlockState state) { return state.getMaterial() == Material.WATER; }

    private static boolean replaceable(IBlockState state) {
        if (state.getBlock() == Blocks.BEDROCK) { return false; }
        Material material = state.getMaterial();
        return material == Material.ROCK || material == Material.GROUND || material == Material.GRASS || material == Material.SAND;
    }

    private static int bound(int value) { return MathHelper.clamp(value, 0, Cube.SIZE); }
}
