package mctmods.resourcedatapackloader.content.rubic.server;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.interfaces.ICubeIO;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderInternal;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import javax.annotation.Nonnull;

public class RubicAnvilChunkLoader extends AnvilChunkLoader {
    private ICubeIO cubeIOValue;
    private final Supplier<ICubeIO> cubeIOSource;

    public RubicAnvilChunkLoader(File chunkSaveLocationIn, DataFixer dataFixerIn, Supplier<ICubeIO> cubeIO) {
        super(chunkSaveLocationIn, dataFixerIn);
        this.cubeIOSource = cubeIO;
    }

    private ICubeIO getCubeIO() {
        if (cubeIOValue == null) { cubeIOValue = cubeIOSource.get(); }
        return cubeIOValue;
    }

    @Override @Nullable public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
        ICubeIO.PartialData<Chunk> data = ((ICubeProviderInternal.IServer) worldIn.getChunkProvider()).getCubeIO().loadColumnAsyncPart(x, z);
        ((ICubeProviderInternal.IServer) worldIn.getChunkProvider()).getCubeIO().loadColumnSyncPart(data);
        return data.getObject();
    }

    @Override @Nullable public Object[] loadChunk__Async(World worldIn, int x, int z) throws IOException {
        ICubeIO.PartialData<Chunk> data = ((ICubeProviderInternal.IServer) worldIn.getChunkProvider()).getCubeIO().loadColumnAsyncPart(x, z);
        return new Object[]{data.getObject(), data.getNbt()};
    }

    @Override public boolean isChunkGeneratedAt(int x, int z) { return this.getCubeIO().columnExists(x, z); }

    @Override @Nullable protected Chunk checkedReadChunkFromNBT(@Nonnull World worldIn, int x, int z, @Nonnull NBTTagCompound compound) {
        throw new UnsupportedOperationException();
    }

    @Override @Nullable protected Object[] checkedReadChunkFromNBT__Async(@Nonnull World worldIn, int x, int z, @Nonnull NBTTagCompound compound) {
        throw new UnsupportedOperationException();
    }

    @Override public void saveChunk(@Nonnull World worldIn, @Nonnull Chunk chunkIn) {
        getCubeIO().saveColumn(chunkIn);
        for (ICube cube : ((IColumn) chunkIn).getLoadedCubes()) { getCubeIO().saveCube((Cube) cube); }
    }

    @Override protected void addChunkToPending(@Nonnull ChunkPos pos, @Nonnull NBTTagCompound compound) { throw new UnsupportedOperationException(); }

    @Override public boolean writeNextIO() { return getCubeIO().writeNextIO(); }

    @Override public void saveExtraChunkData(@Nonnull World worldIn, @Nonnull Chunk chunkIn) {
    }

    @Override public void flush() {
        try {
            getCubeIO().flush();
        } catch (IOException e) {
            Rubic.LOGGER.catching(e);
        }
    }

    @Override public void loadEntities(@Nonnull World worldIn, @Nonnull NBTTagCompound compound, @Nonnull Chunk chunk) { throw new UnsupportedOperationException(); }

    @Override public int getPendingSaveCount() { return getCubeIO().getPendingColumnCount() + getCubeIO().getPendingCubeCount() / 16; }
}
