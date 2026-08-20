package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.content.rubic.server.chunkio.interfaces.ICubeIO;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.util.CubePos;

import javax.annotation.Nullable;

public interface ICubeProviderInternal extends ICubeProvider {
    @Override @Nullable Cube getLoadedCube(int cubeX, int cubeY, int cubeZ);

    @Override @Nullable default Cube getLoadedCube(CubePos coords) { return getLoadedCube(coords.getX(), coords.getY(), coords.getZ()); }

    @Override Cube getCube(int cubeX, int cubeY, int cubeZ);

    @Override default Cube getCube(CubePos coords) { return getCube(coords.getX(), coords.getY(), coords.getZ()); }

    interface Server extends ICubeProviderInternal { ICubeIO getCubeIO(); }
}
