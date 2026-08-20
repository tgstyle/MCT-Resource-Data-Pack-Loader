package mctmods.resourcedatapackloader.content.rubic.server.chunkio;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.async.CubeIoQueue;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.interfaces.ICubeIO;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicStorage;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.util.CubePos;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraftforge.fml.common.FMLCommonHandler;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static com.google.common.base.Preconditions.*;

public class AsyncBatchingCubeIO implements ICubeIO {
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected final World world;
    protected final IRubicStorage storage;
    protected final Map<ChunkPos, NBTTagCompound> pendingColumns = new ConcurrentHashMap<>();
    protected final Map<CubePos, NBTTagCompound> pendingCubes = new ConcurrentHashMap<>();
    protected volatile boolean open = true;

    public AsyncBatchingCubeIO(World world, IRubicStorage storage) throws IOException {
        this.world = Objects.requireNonNull(world, "world");
        this.storage = Objects.requireNonNull(storage, "storage");
    }

    protected void ensureOpen() { checkState(this.open, "already closed?!?"); }

    public IRubicStorage getStorage() { return this.storage; }

    @Override public boolean columnExists(int columnX, int columnZ) {
        ChunkPos pos = new ChunkPos(columnX, columnZ);
        this.lock.readLock().lock();
        try {
            this.ensureOpen();
            return this.pendingColumns.containsKey(pos) || this.storage.columnExists(pos);
        } catch (IOException e) {
            Rubic.LOGGER.catching(e);
            return false;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override public PartialData<Chunk> loadColumnNbt(int chunkX, int chunkZ) throws IOException {
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        this.lock.readLock().lock();
        try {
            this.ensureOpen();
            NBTTagCompound nbt = this.pendingColumns.get(pos);
            if (nbt == null) { nbt = this.storage.readColumn(pos); }
            if (nbt != null) { nbt = FMLCommonHandler.instance().getDataFixer().process(FixTypes.CHUNK, nbt); }
            return new PartialData<>(null, nbt);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override public PartialData<ICube> loadCubeNbt(Chunk column, int cubeY) throws IOException {
        CubePos pos = new CubePos(column.x, cubeY, column.z);
        this.lock.readLock().lock();
        try {
            this.ensureOpen();
            NBTTagCompound nbt = this.pendingCubes.get(pos);
            if (nbt == null) { nbt = this.storage.readCube(pos); }
            return new PartialData<>(null, nbt);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override public void saveColumn(Chunk column) {
        this.lock.readLock().lock();
        try {
            this.ensureOpen();
            this.pendingColumns.put(column.getPos(), IONbtWriter.write(column));
            column.setModified(false);
            ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override public void saveCube(Cube cube) {
        ((IRubicWorldInternal) world).rdpl$getLightingManager().processUpdates();
        this.lock.readLock().lock();
        try {
            this.ensureOpen();
            this.pendingCubes.put(cube.getCoords(), IONbtWriter.write(cube));
            cube.markSaved();
            ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override public int getPendingColumnCount() {
        this.lock.readLock().lock();
        try {
            this.ensureOpen();
            return this.pendingColumns.size();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override public int getPendingCubeCount() {
        this.lock.readLock().lock();
        try {
            this.ensureOpen();
            return this.pendingCubes.size();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override public void flush() throws IOException {
        this.lock.writeLock().lock();
        try {
            this.ensureOpen();
            this.drainQueueBlocking();
            this.storage.flush();
        } catch (InterruptedException e) {
            Rubic.LOGGER.catching(e);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override public void close() throws IOException {
        CubeIoQueue.shutdownNowBlocking();
        this.lock.writeLock().lock();
        try {
            this.ensureOpen();
            this.drainQueueBlocking();
            this.storage.close();
            this.open = false;
        } catch (InterruptedException e) {
            Rubic.LOGGER.catching(e);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected void drainQueueBlocking() throws InterruptedException {
        do {
            ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
            ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
        } while (!this.pendingColumns.isEmpty() || !this.pendingCubes.isEmpty());
    }

    @Override public boolean writeNextIO() {
        try {
            Map<ChunkPos, NBTTagCompound> columnsSnapshot = new Object2ObjectOpenHashMap<>(this.pendingColumns.size());
            columnsSnapshot.putAll(this.pendingColumns);
            Map<CubePos, NBTTagCompound> cubesSnapshot = new Object2ObjectOpenHashMap<>(this.pendingCubes.size());
            cubesSnapshot.putAll(this.pendingCubes);
            this.storage.writeBatch(new IRubicStorage.NBTBatch(Collections.unmodifiableMap(columnsSnapshot), Collections.unmodifiableMap(cubesSnapshot)));
            columnsSnapshot.forEach(this.pendingColumns::remove);
            cubesSnapshot.forEach(this.pendingCubes::remove);
        } catch (IOException e) {
            Rubic.LOGGER.error("Failed to write pending chunk data batch", e);
        }
        return !this.pendingColumns.isEmpty() || !this.pendingCubes.isEmpty();
    }

    @Override public void loadColumnAsyncPart(PartialData<Chunk> info, int chunkX, int chunkZ) {
        if (info.getNbt() == null) { return; }
        Chunk chunk = IONbtReader.readColumn(this.world, chunkX, chunkZ, info.getNbt());
        info.setObject(chunk);
    }

    @Override public void loadColumnSyncPart(PartialData<Chunk> info) {
    }

    @Override public void loadCubeAsyncPart(PartialData<ICube> info, Chunk column, int cubeY) {
        if (info.getNbt() == null) { return; }
        Cube cube = IONbtReader.readCubeAsyncPart(column, column.x, cubeY, column.z, info.getNbt());
        info.setObject(cube);
    }

    @Override public void loadCubeSyncPart(PartialData<ICube> info) { IONbtReader.readCubeSyncPart((Cube) info.getObject(), this.world, info.getNbt()); }
}
