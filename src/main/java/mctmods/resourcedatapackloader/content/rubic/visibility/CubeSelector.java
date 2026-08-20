package mctmods.resourcedatapackloader.content.rubic.visibility;

import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.util.math.ChunkPos;
import java.util.Set;
import java.util.function.Consumer;

public abstract class CubeSelector {
    public abstract void forAllVisibleFrom(CubePos cubePos, int horizontalViewDistance, int verticalViewDistance, Consumer<CubePos> consumer);

    public abstract void findChanged(CubePos oldAddress, CubePos newAddress, int horizontalViewDistance, int verticalViewDistance,
            Set<CubePos> cubesToRemove, Set<CubePos> cubesToLoad, Set<ChunkPos> columnsToRemove, Set<ChunkPos> columnsToLoad);

    public abstract void findAllUnloadedOnViewDistanceDecrease(CubePos playerAddress, int oldHorizontalViewDistance, int newHorizontalViewDistance,
            int oldVerticalViewDistance, int newVerticalViewDistance, Set<CubePos> cubesToUnload, Set<ChunkPos> columnsToUnload);
}
