package mctmods.resourcedatapackloader.common.rubic.world.interfaces;

import mctmods.resourcedatapackloader.core.util.address.IXZAddressable;

import java.util.Collection;
import javax.annotation.Nullable;

public interface IColumn extends IXZAddressable {
    @Deprecated int getHeightValue(int localX, int localZ);

    int getHeightValue(int localX, int blockY, int localZ);

    boolean shouldTick();

    IHeightMap getOpacityIndex();

    Collection<? extends ICube> getLoadedCubes();

    Iterable<? extends ICube> getLoadedCubes(int startY, int endY);

    @Nullable ICube getLoadedCube(int cubeY);

    ICube getCube(int cubeY);

    void addCube(ICube cube);

    @Nullable ICube removeCube(int cubeY);

    boolean hasLoadedCubes();

    void preCacheCube(ICube cube);
}
