package mctmods.resourcedatapackloader.content.rubic.server.chunkio.async;

import mctmods.resourcedatapackloader.content.rubic.server.chunkio.interfaces.ICubeIO;
import mctmods.resourcedatapackloader.content.rubic.worldgen.interfaces.ICubeGenerator;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import java.util.function.Consumer;
import javax.annotation.Nullable;

final class PendingColumn extends PendingLoad<Chunk> {
    private final ICubeIO io;
    private final World world;
    private final int columnX;
    private final int columnZ;
    private final ICubeGenerator generator;
    private final Consumer<Chunk> tellProviderLoading;
    @Nullable private ICubeIO.PartialData<Chunk> data;

    PendingColumn(ICubeIO io, World world, int columnX, int columnZ, ICubeGenerator generator, Consumer<Chunk> tellProviderLoading) {
        this.io = io;
        this.world = world;
        this.columnX = columnX;
        this.columnZ = columnZ;
        this.generator = generator;
        this.tellProviderLoading = tellProviderLoading;
    }

    @Override void readOffThread() throws Exception { data = io.loadColumnAsyncPart(columnX, columnZ); }

    @Override void applyOnServerThread() {
        ICubeIO.PartialData<Chunk> held = data;
        if (held == null) { throw new IllegalStateException("Nothing was read for " + describe()); }
        if (held.getObject() == null) { return; }
        io.loadColumnSyncPart(held);
        Chunk column = held.getObject();
        try {
            tellProviderLoading.accept(column);
            MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(column, held.getNbt()));
        } finally {
            tellProviderLoading.accept(null);
        }
        column.setLastSaveTime(world.getTotalWorldTime());
        generator.recreateStructures(column);
    }

    @Nullable @Override Chunk loaded() { return data == null ? null : data.getObject(); }

    @Override String describe() { return "column " + columnX + ", " + columnZ; }
}
