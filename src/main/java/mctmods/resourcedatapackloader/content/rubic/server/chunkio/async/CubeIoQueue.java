package mctmods.resourcedatapackloader.content.rubic.server.chunkio.async;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.rubic.server.CubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.interfaces.ICubeIO;
import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICubeProviderServer;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorldInternal;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.content.rubic.worldgen.interfaces.ICubeGenerator;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = Rubic.MODID) public final class CubeIoQueue {
    private static final int LEAST_READERS = 1;
    private static final int PLAYERS_PER_READER = 50;
    private static final Map<CubeAt, PendingCube> cubeReads = new ConcurrentHashMap<>(20000, 0.8f, 1);
    private static final Map<ColumnAt, PendingColumn> columnReads = new ConcurrentHashMap<>();
    private static final Map<ColumnAt, AtomicInteger> cubeReadsPerColumn = new ConcurrentHashMap<>();
    private static final ConcurrentLinkedQueue<PendingLoad<?>> readyToHandOver = new ConcurrentLinkedQueue<>();
    private static final ThreadPoolExecutor cubeReaders = readers("Cube");
    private static final ThreadPoolExecutor columnReaders = readers("Column");

    private CubeIoQueue() {}

    private static ThreadPoolExecutor readers(String what) {
        AtomicInteger counted = new AtomicInteger();
        return new ThreadPoolExecutor(LEAST_READERS, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), runnable -> {
            Thread thread = new Thread(runnable, what + " I/O Thread #" + counted.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        });
    }

    @Nullable public static Cube syncCubeLoad(World world, ICubeIO io, CubeProviderServer cache, int cubeX, int cubeY, int cubeZ) {
        Chunk column = cache.getColumn(cubeX, cubeZ, ICubeProviderServer.Requirement.LIGHT);
        CubeAt at = new CubeAt(world, cubeX, cubeY, cubeZ);
        PendingCube queued = cubeReads.remove(at);
        if (queued != null) { forget(at.column()); }
        PendingCube read = queued == null || queued.hasBeenHandedOver() ? new PendingCube(io, cubeX, cubeY, cubeZ) : queued;
        read.tellColumn(column);
        return finish(read);
    }

    @Nullable public static Chunk syncColumnLoad(World world, ICubeIO io, int columnX, int columnZ, Consumer<Chunk> tellProviderLoading) {
        PendingColumn queued = columnReads.remove(new ColumnAt(world, columnX, columnZ));
        return finish(queued == null || queued.hasBeenHandedOver() ? new PendingColumn(io, world, columnX, columnZ, generator(world), tellProviderLoading) : queued);
    }

    @Nullable private static <T> T finish(PendingLoad<T> read) {
        if (read.claimRead()) { read.readNow(); }
        else { read.awaitRead(); }
        read.handOver();
        return read.loaded();
    }

    private static ICubeGenerator generator(World world) { return ((IRubicWorldInternal.IServer) world).rdpl$getCubeCache().getCubeGenerator(); }

    public static void queueCubeLoad(World world, ICubeIO io, CubeProviderServer cache, int cubeX, int cubeY, int cubeZ, Consumer<Cube> whenLoaded) {
        CubeAt at = new CubeAt(world, cubeX, cubeY, cubeZ);
        PendingCube waiting = cubeReads.get(at);
        if (waiting != null && !waiting.hasBeenHandedOver()) {
            waiting.waitFor(whenLoaded);
            return;
        }
        PendingCube read = new PendingCube(io, cubeX, cubeY, cubeZ);
        read.waitFor(whenLoaded);
        cubeReads.put(at, read);
        remember(at.column());
        Chunk column = cache.getLoadedColumn(cubeX, cubeZ);
        if (column != null) { start(read, column); }
        else { cache.asyncGetColumn(cubeX, cubeZ, ICubeProviderServer.Requirement.LIGHT, found -> start(read, found)); }
    }

    public static void queueColumnLoad(World world, ICubeIO io, int columnX, int columnZ, Consumer<Chunk> whenLoaded, Consumer<Chunk> tellProviderLoading) {
        ColumnAt at = new ColumnAt(world, columnX, columnZ);
        PendingColumn waiting = columnReads.get(at);
        if (waiting != null && !waiting.hasBeenHandedOver()) {
            waiting.waitFor(whenLoaded);
            return;
        }
        PendingColumn read = new PendingColumn(io, world, columnX, columnZ, generator(world), tellProviderLoading);
        read.waitFor(whenLoaded);
        columnReads.put(at, read);
        columnReaders.execute(() -> readThenQueue(read));
    }

    public static void dropQueuedCubeLoad(World world, int cubeX, int cubeY, int cubeZ, Consumer<Cube> whenLoaded) {
        CubeAt at = new CubeAt(world, cubeX, cubeY, cubeZ);
        PendingCube read = cubeReads.get(at);
        if (read == null) {
            ContentLog.LOGGER.warn("Nothing was waiting to read cube {}, {}, {} in {}, so nothing was dropped", cubeX, cubeY, cubeZ, world);
            return;
        }
        read.stopWaiting(whenLoaded);
        if (read.nobodyWaiting() && cubeReads.remove(at, read)) { forget(at.column()); }
    }

    public static void dropQueuedColumnLoad(World world, int columnX, int columnZ, Consumer<Chunk> whenLoaded) {
        ColumnAt at = new ColumnAt(world, columnX, columnZ);
        PendingColumn read = columnReads.get(at);
        if (read == null) {
            ContentLog.LOGGER.warn("Nothing was waiting to read column {}, {} in {}, so nothing was dropped", columnX, columnZ, world);
            return;
        }
        read.stopWaiting(whenLoaded);
        if (read.nobodyWaiting()) { columnReads.remove(at, read); }
    }

    public static boolean holdsReads(World world, int columnX, int columnZ) { return cubeReadsPerColumn.containsKey(new ColumnAt(world, columnX, columnZ)); }

    public static void tick() {
        for (PendingLoad<?> ready = readyToHandOver.poll(); ready != null; ready = readyToHandOver.poll()) { ready.handOver(); }
        for (Iterator<Map.Entry<CubeAt, PendingCube>> it = cubeReads.entrySet().iterator(); it.hasNext();) {
            Map.Entry<CubeAt, PendingCube> entry = it.next();
            if (!entry.getValue().hasBeenHandedOver()) { continue; }
            it.remove();
            forget(entry.getKey().column());
        }
        columnReads.values().removeIf(PendingLoad::hasBeenHandedOver);
    }

    public static void dropReads(World world) {
        abandon(cubeReads, at -> at.world == world);
        abandon(columnReads, at -> at.world == world);
        cubeReadsPerColumn.keySet().removeIf(at -> at.world == world);
        readyToHandOver.removeIf(PendingLoad::hasBeenHandedOver);
    }

    private static <K> void abandon(Map<K, ? extends PendingLoad<?>> reads, Predicate<K> inWorld) {
        reads.entrySet().removeIf(entry -> {
            if (!inWorld.test(entry.getKey())) { return false; }
            entry.getValue().abandon();
            return true;
        });
    }

    private static void start(PendingCube read, @Nullable Chunk column) {
        if (!read.tellColumn(column)) { return; }
        cubeReaders.execute(() -> readThenQueue(read));
    }

    private static void readThenQueue(PendingLoad<?> read) {
        if (!read.claimRead()) { return; }
        read.readNow();
        readyToHandOver.add(read);
    }

    private static void remember(ColumnAt column) { cubeReadsPerColumn.computeIfAbsent(column, at -> new AtomicInteger()).incrementAndGet(); }

    private static void forget(ColumnAt column) { cubeReadsPerColumn.computeIfPresent(column, (at, held) -> held.decrementAndGet() <= 0 ? null : held); }

    @SubscribeEvent public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) { sizeReaders(event.player.getServer()); }

    @SubscribeEvent public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) { sizeReaders(event.player.getServer()); }

    @SubscribeEvent public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) { tick(); }
    }

    private static void sizeReaders(@Nullable MinecraftServer server) {
        if (server == null) { return; }
        int wanted = Math.max(LEAST_READERS, server.getCurrentPlayerCount() / PLAYERS_PER_READER);
        cubeReaders.setCorePoolSize(wanted);
        columnReaders.setCorePoolSize(wanted);
    }

    private static final class CubeAt {
        private final World world;
        private final int x;
        private final int y;
        private final int z;

        CubeAt(World world, int x, int y, int z) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        ColumnAt column() { return new ColumnAt(world, x, z); }

        @Override public int hashCode() { return (((x * 31 + y) * 31 + z) * 31) ^ System.identityHashCode(world); }

        @Override public boolean equals(@Nullable Object other) {
            if (other == this) { return true; }
            if (!(other instanceof CubeAt)) { return false; }
            CubeAt at = (CubeAt) other;
            return x == at.x && y == at.y && z == at.z && world == at.world;
        }

        @Override public String toString() { return "cube " + x + ", " + y + ", " + z + " in " + world; }
    }

    private static final class ColumnAt {
        private final World world;
        private final int x;
        private final int z;

        ColumnAt(World world, int x, int z) {
            this.world = world;
            this.x = x;
            this.z = z;
        }

        @Override public int hashCode() { return (x * 31 + z) * 31 ^ System.identityHashCode(world); }

        @Override public boolean equals(@Nullable Object other) {
            if (other == this) { return true; }
            if (!(other instanceof ColumnAt)) { return false; }
            ColumnAt at = (ColumnAt) other;
            return x == at.x && z == at.z && world == at.world;
        }

        @Override public String toString() { return "column " + x + ", " + z + " in " + world; }
    }
}
