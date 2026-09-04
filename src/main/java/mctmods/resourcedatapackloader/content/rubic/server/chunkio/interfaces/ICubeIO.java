package mctmods.resourcedatapackloader.content.rubic.server.chunkio.interfaces;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;

import java.io.Flushable;
import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.IThreadedFileIO;

public interface ICubeIO extends Flushable, AutoCloseable, IThreadedFileIO {
	@Override void flush() throws IOException;

	@Override void close() throws IOException;

	default PartialData<Chunk> loadColumnAsyncPart(int chunkX, int chunkZ) throws IOException {
		PartialData<Chunk> data = loadColumnNbt(chunkX, chunkZ);
		loadColumnAsyncPart(data, chunkX, chunkZ);
		return data;
	}

	PartialData<Chunk> loadColumnNbt(int chunkX, int chunkZ) throws IOException;

	void loadColumnAsyncPart(PartialData<Chunk> info, int chunkX, int chunkZ);

	void loadColumnSyncPart(PartialData<Chunk> info);

	default PartialData<ICube> loadCubeAsyncPart(Chunk column, int cubeY) throws IOException {
		PartialData<ICube> data = loadCubeNbt(column, cubeY);
		loadCubeAsyncPart(data, column, cubeY);
		return data;
	}

	PartialData<ICube> loadCubeNbt(Chunk column, int cubeY) throws IOException;

	void loadCubeAsyncPart(PartialData<ICube> info, Chunk column, int cubeY);

	void loadCubeSyncPart(PartialData<ICube> info);

	void saveColumn(Chunk column);

	void saveCube(Cube cube);

	boolean columnExists(int columnX, int columnZ);

	int getPendingColumnCount();

	int getPendingCubeCount();

	class PartialData<T> {
		NBTTagCompound nbt;
		T object;

		public PartialData(T object, NBTTagCompound nbt) {
			this.object = object;
			this.nbt = nbt;
		}

		public T getObject() { return object; }

		public void setObject(T obj) { this.object = obj; }

		public NBTTagCompound getNbt() { return nbt; }

	}
}
