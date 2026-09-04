package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import static mctmods.resourcedatapackloader.util.Coords.blockToCube;
import static mctmods.resourcedatapackloader.util.Coords.getCubeXForEntity;
import static mctmods.resourcedatapackloader.util.Coords.getCubeYForEntity;
import static mctmods.resourcedatapackloader.util.Coords.getCubeZForEntity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import javax.annotation.Nullable;

public class CubePos {
    private static final int Y_BITS = 20;
    private static final int X_BITS = 22;
    private static final int Z_BITS = 22;
    private static final int Z_BIT_OFFSET = 0;
    private static final int X_BIT_OFFSET = Z_BIT_OFFSET + Z_BITS;
    private static final int Y_BIT_OFFSET = X_BIT_OFFSET + X_BITS;
    private final int cubeX;
    private final int cubeY;
    private final int cubeZ;

    public CubePos(int cubeX, int cubeY, int cubeZ) {
        this.cubeX = cubeX;
        this.cubeY = cubeY;
        this.cubeZ = cubeZ;
    }


    public int getX() { return this.cubeX; }

    public int getY() { return this.cubeY; }

    public int getZ() { return this.cubeZ; }

    @Override public String toString() { return String.format("CubePos(%d, %d, %d)", cubeX, cubeY, cubeZ); }

    @Override public boolean equals(@Nullable Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj instanceof CubePos) {
            CubePos otherCoords = (CubePos) obj;
            return otherCoords.cubeX == cubeX && otherCoords.cubeY == cubeY && otherCoords.cubeZ == cubeZ;
        }
        return false;
    }

    @Override public int hashCode() {
        return Long.hashCode(Bits.packSignedToLong(cubeX, Y_BITS, Y_BIT_OFFSET)
                | Bits.packSignedToLong(cubeY, X_BITS, X_BIT_OFFSET)
                | Bits.packSignedToLong(cubeZ, Z_BITS, Z_BIT_OFFSET));
    }

    public int getXCenter() { return cubeX * ICube.SIZE + ICube.SIZE / 2; }

    public int getYCenter() { return cubeY * ICube.SIZE + ICube.SIZE / 2; }

    public int getZCenter() { return cubeZ * ICube.SIZE + ICube.SIZE / 2; }

    public int getMinBlockX() { return Coords.cubeToMinBlock(cubeX); }

    public int getMinBlockY() { return Coords.cubeToMinBlock(cubeY); }

    public int getMinBlockZ() { return Coords.cubeToMinBlock(cubeZ); }

    public int getMaxBlockX() { return Coords.cubeToMaxBlock(this.cubeX); }

    public int getMaxBlockY() { return Coords.cubeToMaxBlock(this.cubeY); }

    public int getMaxBlockZ() { return Coords.cubeToMaxBlock(this.cubeZ); }

    public CubePos add(int dx, int dy, int dz) { return new CubePos(getX() + dx, getY() + dy, getZ() + dz); }

    public ChunkPos chunkPos() { return new ChunkPos(getX(), getZ()); }

    public static CubePos fromBlockCoords(int blockX, int blockY, int blockZ) { return new CubePos(blockToCube(blockX), blockToCube(blockY), blockToCube(blockZ)); }

    public static CubePos fromEntityCoords(double blockX, double blockY, double blockZ) { return new CubePos(blockToCube(blockX), blockToCube(blockY), blockToCube(blockZ)); }

    public static CubePos fromEntity(Entity entity) { return new CubePos(getCubeXForEntity(entity), getCubeYForEntity(entity), getCubeZForEntity(entity)); }

    public static CubePos fromBlockCoords(BlockPos pos) { return CubePos.fromBlockCoords(pos.getX(), pos.getY(), pos.getZ()); }

    public boolean containsBlock(BlockPos pos) { return this.cubeX == blockToCube(pos.getX()) && this.cubeY == blockToCube(pos.getY()) && this.cubeZ == blockToCube(pos.getZ()); }
}
