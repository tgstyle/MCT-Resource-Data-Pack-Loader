package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import java.io.Flushable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;

public interface IRubicStorage extends Flushable, AutoCloseable {
    boolean columnExists(ChunkPos pos) throws IOException;

    NBTTagCompound readColumn(ChunkPos pos) throws IOException;

    NBTTagCompound readCube(CubePos pos) throws IOException;

    void writeColumn(ChunkPos pos, NBTTagCompound nbt) throws IOException;

    void writeCube(CubePos pos, NBTTagCompound nbt) throws IOException;

    default void writeBatch(NBTBatch batch) throws IOException {
        try {
            batch.columns.entrySet().parallelStream().forEach(entry -> {
                try {
                    this.writeColumn(entry.getKey(), entry.getValue());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
            batch.cubes.entrySet().parallelStream().forEach(entry -> {
                try {
                    this.writeCube(entry.getKey(), entry.getValue());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    @Override void flush() throws IOException;

    @Override void close() throws IOException;

    class NBTBatch {
        public final Map<ChunkPos, NBTTagCompound> columns;
        public final Map<CubePos, NBTTagCompound> cubes;

        public NBTBatch(Map<ChunkPos, NBTTagCompound> columns, Map<CubePos, NBTTagCompound> cubes) {
            this.columns = Objects.requireNonNull(columns, "columns");
            this.cubes = Objects.requireNonNull(cubes, "cubes");
        }
    }
}
