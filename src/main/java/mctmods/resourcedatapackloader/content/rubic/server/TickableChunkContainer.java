package mctmods.resourcedatapackloader.content.rubic.server;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.util.XYZMap;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.chunk.Chunk;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class TickableChunkContainer {
    private final ObjectArrayList<ICube> cubes = ObjectArrayList.wrap(new ICube[64*1024]);
    XYZMap<ICube> forcedCubes;
    private final Set<Chunk> columns = Collections.newSetFromMap(new IdentityHashMap<>());

    void clear() {
        this.cubes.clear();
        this.columns.clear();
    }

    void addCube(ICube cube) { cubes.add(cube); }

    public void addColumn(Chunk column) { columns.add(column); }

    public Iterable<ICube> forcedCubes() { return forcedCubes; }

    public ICube[] playerTickableCubes() { return cubes.elements(); }

    public Iterable<Chunk> columns() { return columns; }
}
