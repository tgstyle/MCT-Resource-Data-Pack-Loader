package mctmods.resourcedatapackloader.common.util.world;

import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.IMinMaxHeight;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class Coords {
    public static final int NO_HEIGHT = Integer.MIN_VALUE + 32;
    public static final int BIOMES_PER_CUBE = 8 * 8;

    public static void writePos(NBTTagCompound tag, int x, int y, int z) {
        tag.setInteger("x", x);
        tag.setInteger("y", y);
        tag.setInteger("z", z);
    }

    public static void writePos(NBTTagCompound tag, BlockPos pos) { writePos(tag, pos.getX(), pos.getY(), pos.getZ()); }

    public static BlockPos readPos(NBTTagCompound tag) { return new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")); }

    public static int chunkToRegion(int chunk) { return chunk >> 5; }

    public static int regionToMinChunk(int region) { return region << 5; }

    public static int chebyshev(int dx, int dz) { return Math.max(Math.abs(dx), Math.abs(dz)); }

    public static boolean forEachChunkWithin(int chunkX, int chunkZ, int radius, ChunkVisitor visitor) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (!visitor.visit(chunkX + dx, chunkZ + dz)) { return false; }
            }
        }
        return true;
    }

    public static boolean quarter(Rotation turned) { return turned == Rotation.CLOCKWISE_90 || turned == Rotation.COUNTERCLOCKWISE_90; }

    public static int[] toSource(int lx, int lz, int wide, int deep, Rotation turned) {
        if (turned == Rotation.CLOCKWISE_90) { return new int[] {lz, deep - 1 - lx}; }
        if (turned == Rotation.COUNTERCLOCKWISE_90) { return new int[] {wide - 1 - lz, lx}; }
        if (turned == Rotation.CLOCKWISE_180) { return new int[] {wide - 1 - lx, deep - 1 - lz}; }
        return new int[] {lx, lz};
    }

    public static int[] toLocal(int sx, int sz, int wide, int deep, Rotation turned) {
        if (turned == Rotation.CLOCKWISE_90) { return new int[] {deep - 1 - sz, sx}; }
        if (turned == Rotation.COUNTERCLOCKWISE_90) { return new int[] {sz, wide - 1 - sx}; }
        if (turned == Rotation.CLOCKWISE_180) { return new int[] {wide - 1 - sx, deep - 1 - sz}; }
        return new int[] {sx, sz};
    }

    public interface ChunkVisitor {
        boolean visit(int x, int z);
    }

    public static int blockToLocal(int val) { return val & 0xf; }

    public static int blockToCube(int val) { return val >> 4; }

    public static boolean cubeAligned(int block) { return (block & 15) == 0; }

    public static int blockToLocalBiome3d(int val) { return (val & 15) >> 2; }

    public static int localToBlock(int cubeVal, int localVal) { return cubeToMinBlock(cubeVal) + localVal; }

    public static int cubeToMinBlock(int val) { return val << 4; }

    public static int topCubeY(IMinMaxHeight world) { return blockToCube(world.rdpl$getMaxHeight()) - 1; }

    public static int cubeToMaxBlock(int val) { return cubeToMinBlock(val) + 15; }

    public static int getCubeXForEntity(Entity entity) { return blockToCube(MathHelper.floor(entity.posX)); }

    public static int getCubeZForEntity(Entity entity) { return blockToCube(MathHelper.floor(entity.posZ)); }

    public static int getCubeYForEntity(Entity entity) { return blockToCube(MathHelper.floor(entity.posY)); }

    public static int blockToCube(double blockPos) { return blockToCube(MathHelper.floor(blockPos)); }

    @SuppressWarnings("deprecation") public static int topOpaqueLocalY(ExtendedBlockStorage ebs, int localX, int localZ) {
        for (int y = ICube.SIZE - 1; y >= 0; y--) {
            if (ebs.get(localX, y, localZ).getLightOpacity() > 0) { return y; }
        }
        return -1;
    }

    public static int getMinCubePopulationPos(int coord) { return localToBlock(blockToCube(coord - ICube.SIZE / 2), ICube.SIZE / 2); }

    public static long awaySq(long x, long z, long fromX, long fromZ) {
        long awayX = x - fromX;
        long awayZ = z - fromZ;
        return awayX * awayX + awayZ * awayZ;
    }

    public static long chunkCentreAway(int chunkX, int chunkZ, long fromX, long fromZ) { return awaySq(chunkX * 16L + 8, chunkZ * 16L + 8, fromX, fromZ); }

    @Nullable public static BlockPos nearestPin(List<long[]> pinned, long fromX, long fromZ, int beside, Predicate<long[]> skip) {
        BlockPos best = null;
        long bestAway = Long.MAX_VALUE;
        for (long[] pin : pinned) {
            if (skip.test(pin)) { continue; }
            long away = awaySq(pin[0], pin[1], fromX, fromZ);
            if (away >= bestAway) { continue; }
            bestAway = away;
            best = new BlockPos((int) pin[0] + beside, 64, (int) pin[1] + beside);
        }
        return best;
    }
}
