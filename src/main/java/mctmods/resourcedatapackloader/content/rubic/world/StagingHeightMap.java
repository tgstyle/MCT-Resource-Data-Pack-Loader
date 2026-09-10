package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IHeightMap;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class StagingHeightMap implements IHeightMap {
    private static final Comparator<ICube> TOP_DOWN = Comparator.comparingInt(cube -> -cube.getCoords().getY());
    private final List<ICube> stagedCubes = new CopyOnWriteArrayList<>();
    private static final int COLUMNS = ICube.SIZE * ICube.SIZE;
    private static final int CLEAN = 0;
    private static final int DIRTY = 1;
    private final AtomicIntegerArray heightmap = new AtomicIntegerArray(COLUMNS);
    private final AtomicIntegerArray dirtyFlag = new AtomicIntegerArray(COLUMNS);

    public StagingHeightMap() {
        for (int i = 0; i < COLUMNS; i++) { heightmap.set(i, Coords.NO_HEIGHT); }
    }

    public void addStagedCube(ICube cube) {
        stagedCubes.add(cube);
        stagedCubes.sort(TOP_DOWN);
        if (cube.isEmpty()) { return; }
        int cubeTop = Coords.cubeToMaxBlock(cube.getY());
        for (int i = 0; i < COLUMNS; i++) {
            int held = heightmap.get(i);
            if (dirtyFlag.get(i) == DIRTY || held == Coords.NO_HEIGHT || cubeTop > held) { dirtyFlag.set(i, DIRTY); }
        }
    }

    public void removeStagedCube(ICube cube) {
        if (!stagedCubes.remove(cube) || cube.isEmpty()) { return; }
        int cubeBottom = Coords.cubeToMinBlock(cube.getY());
        int cubeTop = Coords.cubeToMaxBlock(cube.getY());
        for (int i = 0; i < COLUMNS; i++) {
            int held = heightmap.get(i);
            if (dirtyFlag.get(i) == DIRTY || (held >= cubeBottom && held <= cubeTop)) { dirtyFlag.set(i, DIRTY); }
        }
    }

    @Override public void onOpacityChange(int localX, int blockY, int localZ, int opacity) {
        if (opacity > 0) {
            if (blockY > getTopBlockY(localX, localZ)) { heightmap.set(index(localX, localZ), blockY); }
        }
        else if (blockY == getTopBlockY(localX, localZ)) { dirtyFlag.set(index(localX, localZ), DIRTY); }
    }

    private int index(int localX, int localZ) { return (localZ << 4) | localX; }

    @Override public int getTopBlockY(int localX, int localZ) {
        int idx = index(localX, localZ);
        if (dirtyFlag.get(idx) == CLEAN) { return heightmap.get(idx); }
        dirtyFlag.set(idx, CLEAN);
        int found = stagedCubes.isEmpty() ? Coords.NO_HEIGHT : computeHeightMap(localX, localZ);
        heightmap.set(idx, found);
        return found;
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
