package mctmods.resourcedatapackloader.content.rubic.visibility;

import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.util.math.ChunkPos;
import java.util.Set;
import java.util.function.Consumer;

public class CuboidalCubeSelector extends CubeSelector {
    @Override public void forAllVisibleFrom(CubePos cubePos, int horizontalViewDistance, int verticalViewDistance, Consumer<CubePos> consumer) {
        int cubeX = cubePos.getX();
        int cubeY = cubePos.getY();
        int cubeZ = cubePos.getZ();
        for (int x = cubeX - horizontalViewDistance; x <= cubeX + horizontalViewDistance; x++) {
            for (int y = cubeY - verticalViewDistance; y <= cubeY + verticalViewDistance; y++) {
                for (int z = cubeZ - horizontalViewDistance; z <= cubeZ + horizontalViewDistance; z++) { consumer.accept(new CubePos(x, y, z)); }
            }
        }
    }

    @Override public void findChanged(CubePos oldPos, CubePos newPos,
                                      int horizontalViewDistance, int verticalViewDistance,
                                      Set<CubePos> cubesToRemove, Set<CubePos> cubesToLoad,
                                      Set<ChunkPos> columnsToRemove, Set<ChunkPos> columnsToLoad) {
        int oldX = oldPos.getX();
        int oldY = oldPos.getY();
        int oldZ = oldPos.getZ();
        int newX = newPos.getX();
        int newY = newPos.getY();
        int newZ = newPos.getZ();
        int dx = newX - oldX;
        int dy = newY - oldY;
        int dz = newZ - oldZ;
        for (int currentX = newX - horizontalViewDistance; currentX <= newX + horizontalViewDistance; ++currentX) {
            for (int currentZ = newZ - horizontalViewDistance; currentZ <= newZ + horizontalViewDistance; ++currentZ) {
                if (this.isPointOutsideCubeVolume(oldX, 0, oldZ, currentX, 0, currentZ, horizontalViewDistance, verticalViewDistance)) {
                    columnsToLoad.add(new ChunkPos(currentX, currentZ));
                }
                if (this.isPointOutsideCubeVolume(newX, 0, newZ, currentX - dx, 0, currentZ - dz, horizontalViewDistance, verticalViewDistance)) {
                    columnsToRemove.add(new ChunkPos(currentX - dx, currentZ - dz));
                }
                for (int currentY = newY - verticalViewDistance; currentY <= newY + verticalViewDistance; ++currentY) {
                    if (this.isPointOutsideCubeVolume(oldX, oldY, oldZ, currentX, currentY, currentZ,
                            horizontalViewDistance, verticalViewDistance)) { cubesToLoad.add(new CubePos(currentX, currentY, currentZ)); }
                    if (this.isPointOutsideCubeVolume(newX, newY, newZ, currentX - dx, currentY - dy, currentZ - dz,
                            horizontalViewDistance, verticalViewDistance)) { cubesToRemove.add(new CubePos(currentX - dx, currentY - dy, currentZ - dz)); }
                }
            }
        }
        assert cubesToLoad.stream().noneMatch(cubesToRemove::contains) : "cubesToRemove contains element from cubesToLoad!";
        assert columnsToLoad.stream().noneMatch(columnsToRemove::contains) : "columnsToRemove contains element from columnsToLoad!";
    }

    @Override public void findAllUnloadedOnViewDistanceDecrease(CubePos playerPos,
                                                                int oldHorizontalViewDistance, int newHorizontalViewDistance,
                                                                int oldVerticalViewDistance, int newVerticalViewDistance,
                                                                Set<CubePos> cubesToUnload, Set<ChunkPos> columnsToUnload) {
        int playerCubeX = playerPos.getX();
        int playerCubeY = playerPos.getY();
        int playerCubeZ = playerPos.getZ();
        for (int cubeX = playerCubeX - oldHorizontalViewDistance; cubeX <= playerCubeX + oldHorizontalViewDistance; cubeX++) {
            for (int cubeZ = playerCubeZ - oldHorizontalViewDistance; cubeZ <= playerCubeZ + oldHorizontalViewDistance; cubeZ++) {
                if (isPointOutsideCubeVolume(playerCubeX, 0, playerCubeZ, cubeX, 0, cubeZ, newHorizontalViewDistance, newVerticalViewDistance)) {
                    columnsToUnload.add(new ChunkPos(cubeX, cubeZ));
                }
                for (int cubeY = playerCubeY - oldVerticalViewDistance; cubeY <= playerCubeY + oldVerticalViewDistance; cubeY++) {
                    if (isPointOutsideCubeVolume(playerCubeX, playerCubeY, playerCubeZ, cubeX, cubeY, cubeZ, newHorizontalViewDistance,
                            newVerticalViewDistance)) { cubesToUnload.add(new CubePos(cubeX, cubeY, cubeZ)); }
                }
            }
        }
    }

    private boolean isPointOutsideCubeVolume(int cubeX, int cubeY, int cubeZ, int pointX, int pointY, int pointZ, int horizontal, int vertical) {
        int dx = cubeX - pointX;
        int dy = cubeY - pointY;
        int dz = cubeZ - pointZ;
        return dx < -horizontal || dx > horizontal
                || dy < -vertical || dy > vertical
                || dz < -horizontal || dz > horizontal;
    }
}
