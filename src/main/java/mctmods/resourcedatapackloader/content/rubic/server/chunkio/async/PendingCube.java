package mctmods.resourcedatapackloader.content.rubic.server.chunkio.async;

import mctmods.resourcedatapackloader.content.rubic.server.chunkio.interfaces.ICubeIO;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;

import net.minecraft.world.chunk.Chunk;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

final class PendingCube extends PendingLoad<Cube> {
    private final ICubeIO io;
    private final int cubeX;
    private final int cubeY;
    private final int cubeZ;
    private final AtomicBoolean columnKnown = new AtomicBoolean();
    @Nullable private volatile Chunk column;
    @Nullable private ICubeIO.PartialData<ICube> data;

    PendingCube(ICubeIO io, int cubeX, int cubeY, int cubeZ) {
        this.io = io;
        this.cubeX = cubeX;
        this.cubeY = cubeY;
        this.cubeZ = cubeZ;
    }

    boolean tellColumn(@Nullable Chunk found) {
        if (!columnKnown.compareAndSet(false, true)) { return false; }
        column = found;
        return true;
    }

    @Override void readOffThread() throws Exception {
        Chunk within = column;
        if (within == null || within.isEmpty()) { data = new ICubeIO.PartialData<>(null, null); }
        else { data = io.loadCubeAsyncPart(within, cubeY); }
    }

    @Override void applyOnServerThread() {
        ICubeIO.PartialData<ICube> held = data;
        if (held == null) { throw new IllegalStateException("Nothing was read for " + describe()); }
        if (held.getObject() == null) { return; }
        io.loadCubeSyncPart(held);
    }

    @Nullable @Override Cube loaded() { return data == null ? null : (Cube) data.getObject(); }

    @Override String describe() { return "cube " + cubeX + ", " + cubeY + ", " + cubeZ; }
}
