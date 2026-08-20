package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class Coords {
    public static final int NO_HEIGHT = Integer.MIN_VALUE + 32;
    public static final int BIOMES_PER_CUBE = 8 * 8;

    public static int blockToLocal(int val) { return val & 0xf; }

    public static int blockToCube(int val) { return val >> 4; }

    public static int blockToLocalBiome3d(int val) { return (val & 15) >> 2; }

    public static int localToBlock(int cubeVal, int localVal) { return cubeToMinBlock(cubeVal) + localVal; }

    public static int cubeToMinBlock(int val) { return val << 4; }

    public static int cubeToMaxBlock(int val) { return cubeToMinBlock(val) + 15; }

    public static int getCubeXForEntity(Entity entity) { return blockToCube(MathHelper.floor(entity.posX)); }

    public static int getCubeZForEntity(Entity entity) { return blockToCube(MathHelper.floor(entity.posZ)); }

    public static int getCubeYForEntity(Entity entity) { return blockToCube(MathHelper.floor(entity.posY)); }

    public static int blockToCube(double blockPos) { return blockToCube(MathHelper.floor(blockPos)); }

    public static int getMinCubePopulationPos(int coord) { return localToBlock(blockToCube(coord - ICube.SIZE / 2), ICube.SIZE / 2); }
}
