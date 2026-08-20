package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.world.chunk.Chunk;
import javax.annotation.Nullable;

public interface ICubeProvider {
    @Nullable ICube getLoadedCube(int cubeX, int cubeY, int cubeZ);

    @Nullable default ICube getLoadedCube(CubePos coords) { return getLoadedCube(coords.getX(), coords.getY(), coords.getZ()); }

    ICube getCube(int cubeX, int cubeY, int cubeZ);

    default ICube getCube(CubePos coords) { return getCube(coords.getX(), coords.getY(), coords.getZ()); }

    @Nullable Chunk getLoadedColumn(int x, int z);

    Chunk provideColumn(int x, int z);
}
