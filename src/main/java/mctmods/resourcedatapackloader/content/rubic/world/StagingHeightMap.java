package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IHeightMap;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

public class StagingHeightMap implements IHeightMap {
    private static final Comparator<ICube> TOP_DOWN = Comparator.comparingInt(cube -> -cube.getCoords().getY());
    private final List<ICube> stagedCubes = new ArrayList<>();
    private final int[] heightmap = new int[ICube.SIZE * ICube.SIZE];
    private final BitSet dirtyFlag = new BitSet(heightmap.length);

    public StagingHeightMap() { Arrays.fill(heightmap, Coords.NO_HEIGHT); }

    public void addStagedCube(ICube cube) {
        stagedCubes.add(cube);
        stagedCubes.sort(TOP_DOWN);
        if (cube.isEmpty()) { return; }
        int cubeTop = Coords.cubeToMaxBlock(cube.getY());
        for (int i = 0; i < heightmap.length; i++) {
            if (dirtyFlag.get(i) || heightmap[i] == Coords.NO_HEIGHT || cubeTop > heightmap[i]) { dirtyFlag.set(i); }
        }
    }

    public void removeStagedCube(ICube cube) {
        if (!stagedCubes.remove(cube) || cube.isEmpty()) { return; }
        int cubeBottom = Coords.cubeToMinBlock(cube.getY());
        int cubeTop = Coords.cubeToMaxBlock(cube.getY());
        for (int i = 0; i < heightmap.length; i++) {
            if (dirtyFlag.get(i) || (heightmap[i] >= cubeBottom && heightmap[i] <= cubeTop)) { dirtyFlag.set(i); }
        }
    }

    @Override public void onOpacityChange(int localX, int blockY, int localZ, int opacity) {
        if (opacity > 0) {
            if (blockY > getTopBlockY(localX, localZ)) { heightmap[index(localX, localZ)] = blockY; }
        }
        else if(blockY == getTopBlockY(localX, localZ)) { dirtyFlag.set(index(localX, localZ)); }
    }

    private int index(int localX, int localZ) { return (localZ << 4) | localX; }

    @Override public int getTopBlockY(int localX, int localZ) {
        int idx = index(localX, localZ);
        if (!dirtyFlag.get(idx)) { return heightmap[idx]; }
        dirtyFlag.clear(idx);
        return heightmap[idx] = stagedCubes.isEmpty() ? Coords.NO_HEIGHT : computeHeightMap(localX, localZ);
    }

    @SuppressWarnings("deprecation") private int computeHeightMap(int localX, int localZ) {
        for (ICube stagedCube : stagedCubes) {
            ExtendedBlockStorage ebs = stagedCube.getStorage();
            if (ebs == null || ebs.isEmpty()) { continue; }
            for (int i = 15; i >= 0; i--) {
                if (ebs.get(localX, i, localZ).getLightOpacity() > 0) { return Coords.localToBlock(stagedCube.getY(), i); }
            }
        }
        return Coords.NO_HEIGHT;
    }

    @Override public int getTopBlockYBelow(int localX, int localZ, int blockY) { throw new UnsupportedOperationException("Not implemented for staging heightmap"); }
}
