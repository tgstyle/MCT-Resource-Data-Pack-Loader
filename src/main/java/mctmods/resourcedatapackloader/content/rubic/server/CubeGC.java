package mctmods.resourcedatapackloader.content.rubic.server;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.chunk.Chunk;
import java.util.Collection;
import java.util.Iterator;

public class CubeGC {
    private final CubeProviderServer cubeCache;
    private int tick = 0;

    public CubeGC(CubeProviderServer cubeCache) { this.cubeCache = cubeCache; }

    public void tick() {
        cubeCache.world.profiler.startSection("cubeGc");
        tick++;
        if (tick > ContentControl.number(ContentControl.CHUNKS, "cubeGCInterval", 200)) {
            tick = 0;
            chunkGc();
        }
        if (ContentLog.LOGGER.debugEnabled()) { verifyColumnConsistency(); }
        cubeCache.world.profiler.endSection();
    }

    private void verifyColumnConsistency() {
        Iterator<Cube> cubeIt = cubeCache.cubesIterator();
        while (cubeIt.hasNext()) {
            Cube cube = cubeIt.next();
            IColumn cubeCol = cube.getColumn();
            Chunk storedCol = cubeCache.getLoadedColumn(cube.getX(), cube.getZ());
            if (storedCol == null) { throw new RuntimeException("Cube with no stored column!"); }
            if (storedCol != cubeCol) { throw new RuntimeException("CubeColumn and StoredColumn are different!"); }
        }
        Iterator<Chunk> columnIt = cubeCache.columnsIterator();
        int totalCubes = 0;
        while (columnIt.hasNext()) {
            Chunk storedCol = columnIt.next();
            @SuppressWarnings("unchecked") Collection<Cube> storedColumnCubes = (Collection<Cube>) ((IColumn) storedCol).getLoadedCubes();
            for (Cube c : storedColumnCubes) {
                if (cubeCache.getLoadedCube(c.getCoords()) != c) { throw new RuntimeException("Cube in column not the same as stored cube!"); }
            }
            totalCubes += storedColumnCubes.size();
        }
        if (totalCubes != cubeCache.getLoadedCubeCount()) {
            throw new RuntimeException("Counted " + totalCubes + " cubes in columns, but there are total of " + cubeCache.getLoadedCubeCount() + " cubes!");
        }
    }

    public void chunkGc() {
        Iterator<Cube> cubeIt = cubeCache.cubesIterator();
        while (cubeIt.hasNext()) {
            if (cubeCache.tryUnloadCube(cubeIt.next())) { cubeIt.remove(); }
        }
        Iterator<Chunk> columnIt = cubeCache.columnsIterator();
        while (columnIt.hasNext()) {
            if (cubeCache.tryUnloadColumn(columnIt.next())) { columnIt.remove(); }
        }
    }
}
