package mctmods.resourcedatapackloader.common.rubic.world.interfaces;

import mctmods.resourcedatapackloader.common.util.world.CubePos;

import java.io.Flushable;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

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
