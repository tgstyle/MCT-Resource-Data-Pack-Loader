package mctmods.resourcedatapackloader.common.rubic.world.interfaces;

import mctmods.resourcedatapackloader.common.util.world.CubePos;

import javax.annotation.Nullable;

public interface ICubeProvider {
    @Nullable ICube getLoadedCube(int cubeX, int cubeY, int cubeZ);

    @Nullable default ICube getLoadedCube(CubePos coords) { return getLoadedCube(coords.getX(), coords.getY(), coords.getZ()); }

    ICube getCube(int cubeX, int cubeY, int cubeZ);

    default ICube getCube(CubePos coords) { return getCube(coords.getX(), coords.getY(), coords.getZ()); }
}
