package mctmods.resourcedatapackloader.content.rubic.world.interfaces;

import mctmods.resourcedatapackloader.util.CubePos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import java.io.Flushable;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public interface IRubicStorage extends Flushable, AutoCloseable {
    boolean columnExists(ChunkPos pos) throws IOException;

    NBTTagCompound readColumn(ChunkPos pos) throws IOException;

    NBTTagCompound readCube(CubePos pos) throws IOException;

    void writeBatch(NBTBatch batch) throws IOException;

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
