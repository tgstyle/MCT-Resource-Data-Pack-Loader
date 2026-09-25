package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.interfaces.IPregenMemory;
import mctmods.resourcedatapackloader.content.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.content.rubic.worldgen.WorldgenHangWatchdog;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunk;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMinecraftServerMessage;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.block.BlockFalling;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ContentPregen implements WorldWorkerManager.IWorker {
    private static final long STILL_AFTER_MS = 3000L;
    static volatile ContentPregen running;
    private static volatile long stillUntil;
    private static final Set<Long> DRESS_LATER = new LinkedHashSet<>();
    private static volatile World dressingIn;
    private static long watchedDone = -1L;
    private static long watchedAt;
    private static long chainBegun;
    private final ICommandSender asked;
    final int dimension;
    private final int reach;
    final boolean lightOnly;
    private final int middleX;
    private final int middleZ;
    private final int lowX;
    private final int lowZ;
    private final int highX;
    private final int highZ;
    final ContentChunkOrder order;
    final Deque<Long> resident = new ArrayDeque<>();
    private final Set<Long> held = new HashSet<>();
    private final int keep;
    private final int backlog;
    final int slice;
    final long started;
    volatile long done;
    long made;
    long undressed;
    long dressedLate;
    long paused;
    long dark;
    long darkAtEdge;
    long missing;
    long brightened;
    boolean rubicRun;
    private int round = -1;
    private long roundSpent;
    private long spoke;
    private long checkpointed;
    private long checkpointCost;
    long resumedFrom;
    long etaFigured;
    long etaSeconds;
    long etaAt;
    long etaMade;
    double etaRate;
    private int loggedAt;
    volatile long begun;
    private boolean over;
    private boolean stopping;

    private ContentPregen(ICommandSender asked, int dimension, int centreX, int centreZ, int radius, boolean lightOnly) {
        this.lightOnly = lightOnly;
        this.asked = asked;
        this.dimension = dimension;
        this.reach = radius;
        this.middleX = centreX;
        this.middleZ = centreZ;
        this.lowX = centreX - radius - 1;
        this.lowZ = centreZ - radius - 1;
        this.highX = centreX + radius + 1;
        this.highZ = centreZ + radius + 1;
        this.order = new ContentChunkOrder(centreX, centreZ, radius + 1);
        this.keep = Math.max(64, ContentControl.number(ContentControl.CHUNKS, "pregenKeepLoaded", Config.chunks.pregenKeepLoaded));
        this.backlog = Math.max(0, ContentControl.number(ContentControl.CHUNKS, "pregenPauseAbove", Config.chunks.pregenPauseAbove));
        this.slice = MathHelper.clamp(ContentControl.number(ContentControl.CHUNKS, "pregenMillisPerRound", Config.chunks.pregenMillisPerRound), 1, 1000);
        this.started = System.currentTimeMillis();
    }

    public static void serverStopping() {
        ContentPregen worker = running;
        if (worker != null) {
            ContentLog.LOGGER.info("The server is stopping while land is still being made in dimension {}, so the run is wound down at {} chunk(s) to be picked up on the next load", worker.dimension, worker.done);
            worker.stopping = true;
            worker.finish(DimensionManager.getWorld(worker.dimension));
            IPregenMemory memory = ContentPregenDimensions.memory();
            if (memory != null && !worker.lightOnly) { memory.rdpl$setPregenRun(ContentPregenDimensions.runRecord(worker.dimension, worker.middleX, worker.middleZ, worker.reach)); }
        }
        ContentPregenHold.releaseEveryone(false);
        ContentPregenDimensions.PENDING.clear();
        ContentPregenDimensions.chaining = false;
        chainBegun = 0L;
        ContentPregenDimensions.wantedRadius = 0;
    }

    static void watch() {
        ContentPregen worker = running;
        if (worker == null) { return; }
        long now = System.currentTimeMillis();
        if (worker.done != watchedDone) {
            watchedDone = worker.done;
            watchedAt = now;
            return;
        }
        if (now - watchedAt < 60000L) { return; }
        ContentLog.LOGGER.error("Making land in dimension {} has not moved past {} of {} chunk(s) for a minute, so it is being stopped rather than left hanging. What went wrong should be written above this line", worker.dimension, worker.done, worker.order.total());
        worker.stopping = true;
        worker.finish(DimensionManager.getWorld(worker.dimension));
    }

    public static boolean busy() { return running != null; }

    public static boolean makingLand(World world) {
        ContentPregen worker = running;
        return worker != null && !worker.lightOnly && world.provider.getDimension() == worker.dimension;
    }

    public static boolean lightingOnly() {
        ContentPregen worker = running;
        return worker != null && worker.lightOnly;
    }

    public static boolean dressLater(Chunk chunk) {
        ContentPregen worker = running;
        if (worker == null || !worker.lightOnly || chunk.getWorld().provider.getDimension() != worker.dimension) { return false; }
        DRESS_LATER.add(ChunkPos.asLong(chunk.x, chunk.z));
        return true;
    }

    private static void dressHeld(WorldServer world) {
        if (DRESS_LATER.isEmpty()) { return; }
        ChunkProviderServer provider = world.getChunkProvider();
        int dressed = 0;
        dressingIn = world;
        try {
            for (long key : DRESS_LATER) {
                Chunk chunk = provider.getLoadedChunk((int) key, (int) (key >> 32));
                if (chunk == null || chunk.isTerrainPopulated()) { continue; }
                chunk.populate(provider, provider.chunkGenerator);
                dressed++;
            }
        }
        finally { dressingIn = null; }
        int relit = 0;
        Set<Long> around = new LinkedHashSet<>();
        for (long key : DRESS_LATER) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) { around.add(ChunkPos.asLong((int) key + dx, (int) (key >> 32) + dz)); }
            }
        }
        for (long key : around) {
            Chunk chunk = provider.getLoadedChunk((int) key, (int) (key >> 32));
            if (chunk == null || chunk.isLightPopulated() || !chunk.isTerrainPopulated()) { continue; }
            chunk.checkLight();
            if (chunk.isLightPopulated()) {
                relit++;
                chunk.markDirty();
            }
        }
        ContentLog.LOGGER.info("Dressed {} of the {} chunk(s) that were loaded while the light was being seen to, the rest having been let go in the meantime, with the light held off while they were dressed and {} chunk(s) lit again afterwards in one go", dressed, DRESS_LATER.size(), relit);
        DRESS_LATER.clear();
    }

    public static boolean holds(int chunkX, int chunkZ) {
        ContentPregen worker = running;
        return worker != null && worker.held.contains(ChunkPos.asLong(chunkX, chunkZ));
    }

    public static boolean covers(World world, int chunkX, int chunkZ) {
        ContentPregen worker = running;
        return worker != null && world.provider.getDimension() == worker.dimension && chunkX >= worker.lowX && chunkX <= worker.highX && chunkZ >= worker.lowZ && chunkZ <= worker.highZ;
    }

    public static boolean quenches(World world, int chunkX, int chunkZ) {
        if (dressingIn == world) { return ContentLightArea.outside(world); }
        ContentPregen worker = running;
        if (worker == null || worker.lightOnly) { return false; }
        if (world.provider.getDimension() != worker.dimension) { return false; }
        if (chunkX < worker.lowX || chunkX > worker.highX || chunkZ < worker.lowZ || chunkZ > worker.highZ) { return false; }
        return ContentLightArea.outside(world);
    }

    public static boolean busyIn(World world) {
        ContentPregen worker = running;
        return worker != null && world.provider.getDimension() == worker.dimension;
    }

    public static long start(ICommandSender asked, int dimension, int centreX, int centreZ, int radius) {
        return start(asked, dimension, centreX, centreZ, radius, false);
    }

    public static long start(ICommandSender asked, int dimension, int centreX, int centreZ, int radius, boolean lightOnly) {
        ContentPregen worker = new ContentPregen(asked, dimension, centreX, centreZ, radius, lightOnly);
        WorldServer world = DimensionManager.getWorld(dimension);
        if (!lightOnly) {
            IPregenMemory memory = ContentPregenDimensions.memory();
            if (memory != null) { memory.rdpl$setPregenRun(ContentPregenDimensions.runRecord(dimension, centreX, centreZ, radius)); }
        }
        if (world != null && ContentPregenDimensions.picksUpAgain() && !lightOnly) {
            IPregenMemory memory = ContentPregenDimensions.memory();
            int reached = memory == null ? 0 : memory.rdpl$landMadeAt(dimension);
            if (reached > 0) {
                worker.done = worker.order.skip(reached);
                worker.resumedFrom = worker.done;
                ContentLog.LOGGER.info("Picking the making of land in dimension {} up again where it left off, {} chunk(s) in", dimension, worker.done);
            }
        }
        running = worker;
        watchedDone = -1L;
        if (chainBegun == 0L) { chainBegun = System.currentTimeMillis(); }
        if (dimension != 0) { DimensionManager.keepDimensionLoaded(dimension, true); }
        ContentPregenHold.holdEveryone(true);
        WorldWorkerManager.addWorker(worker);
        return worker.order.total();
    }

    public static boolean stop() {
        if (running == null) { return false; }
        running.stopping = true;
        return true;
    }

    public static String state() {
        ContentPregen worker = running;
        if (worker == null) { return "Nothing is being made at the moment"; }
        return ContentPregenProgress.report(worker);
    }

    @Override public boolean hasWork() { return !over; }

    @SuppressWarnings("NonAtomicOperationOnVolatileField") @Override public boolean doWork() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (begun == 0L) {
            begun = System.currentTimeMillis();
            tellScreen(server);
        }
        WorldServer world = DimensionManager.getWorld(dimension);
        if (world == null || stopping || !order.hasNext()) {
            finish(world);
            return false;
        }
        ChunkProviderServer provider = world.getChunkProvider();
        if (behind(provider)) {
            paused++;
            speak();
            return false;
        }
        int tick = server == null ? 0 : server.getTickCounter();
        if (tick != round) {
            round = tick;
            roundSpent = 0L;
        }
        if (roundSpent >= slice * 1000000L) { return false; }
        long began = System.nanoTime();
        ChunkPos next = order.next();
        done++;
        Chunk chunk;
        if (lightOnly) { chunk = already(provider, next.x, next.z, true); }
        else {
            try {
                WorldgenHangWatchdog.startWorldGen();
                chunk = provider.provideChunk(next.x, next.z);
            }
            finally { WorldgenHangWatchdog.endWorldGen(); }
        }
        boolean rubic = RubicWorldControl.rubicWorld(provider);
        if (chunk != null) {
            rubicRun = rubic;
            if (!lightOnly && rubic && RubicWorldControl.makeColumnCubes(provider, chunk)) { made++; }
            if (!rubic && !chunk.isTerrainPopulated()) {
                undressed++;
                if (!lightOnly) { made++; }
            }
            retain(provider, ChunkPos.asLong(next.x, next.z));
            if (lightOnly && !rubic && !chunk.isLightPopulated()) {
                fetchRing(provider, next.x, next.z);
                dressLate(provider, chunk, next.x, next.z);
            }
            if (!rubic) { brighten(provider, next.x, next.z); }
        }
        roundSpent += System.nanoTime() - began;
        speak();
        checkpoint(world);
        tellScreen(server);
        if ((done & 1023L) == 0L) { ContentStructures.forgetFarStarts(world, next.x, next.z); }
        if ((done & 255L) == 0L && rubic) { RubicWorldControl.rdpl$unloadOldCubes(provider); }
        if ((done & 255L) == 0L) {
            Runtime memory = Runtime.getRuntime();
            long used = (memory.totalMemory() - memory.freeMemory()) >> 20;
            if (rubic) {
                ContentLog.LOGGER.debug("Pregen telemetry: columns={}/{} loadedColumns={} loadedCubes={} pendingCubeSaves={} heap={}/{}MB", done, order.total(), provider.getLoadedChunkCount(), RubicWorldControl.loadedCubes(provider), RubicWorldControl.pendingSaves(provider), used, memory.maxMemory() >> 20);
            }
            else {
                int pending = provider.chunkLoader instanceof AnvilChunkLoader ? ((AnvilChunkLoader) provider.chunkLoader).getPendingSaveCount() : -1;
                ContentLog.LOGGER.debug("Pregen telemetry: chunks={}/{} loadedChunks={} pendingChunkSaves={} heap={}/{}MB", done, order.total(), provider.getLoadedChunkCount(), pending, used, memory.maxMemory() >> 20);
            }
        }
        ContentPregenHold.progress = ContentPregenProgress.sofar(this);
        int tenth = (int) (done * 10L / Math.max(1L, order.total()));
        if (tenth > loggedAt) {
            loggedAt = tenth;
            String said = ContentPregenProgress.sofar(this);
            if (!said.isEmpty()) { ContentLog.LOGGER.info(said); }
        }
        if (!order.hasNext()) {
            ContentPregenHold.tellBar(server, ContentPregenHold.progress);
            finish(world);
            return false;
        }
        return true;
    }

    private Chunk already(ChunkProviderServer provider, int x, int z, boolean counted) {
        Chunk loaded = provider.getLoadedChunk(x, z);
        if (loaded != null) { return loaded; }
        if (!provider.chunkLoader.isChunkGeneratedAt(x, z)) {
            if (counted) { missing++; }
            return null;
        }
        return provider.loadChunk(x, z);
    }

    private boolean behind(ChunkProviderServer provider) {
        if (backlog <= 0) { return false; }
        if (RubicWorldControl.rubicWorld(provider)) { return RubicWorldControl.pendingSaves(provider) > backlog; }
        if (!(provider.chunkLoader instanceof AnvilChunkLoader)) { return false; }
        return ((AnvilChunkLoader) provider.chunkLoader).getPendingSaveCount() > backlog;
    }

    private void retain(ChunkProviderServer provider, long key) {
        if (held.add(key)) { resident.addLast(key); }
        while (resident.size() > keep) {
            long oldest = resident.removeFirst();
            held.remove(oldest);
            release(provider, oldest);
        }
    }

    private void fetchRing(ChunkProviderServer provider, int middleX, int middleZ) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) { continue; }
                int x = middleX + dx;
                int z = middleZ + dz;
                if (held.contains(ChunkPos.asLong(x, z))) { continue; }
                if (already(provider, x, z, false) == null) { continue; }
                retain(provider, ChunkPos.asLong(x, z));
            }
        }
    }

    private void dressLate(ChunkProviderServer provider, Chunk chunk, int x, int z) {
        if (RubicWorldControl.rubicWorld(provider)) { return; }
        if (chunk.isTerrainPopulated()) { return; }
        if (provider.getLoadedChunk(x + 1, z) == null || provider.getLoadedChunk(x, z + 1) == null || provider.getLoadedChunk(x + 1, z + 1) == null) { return; }
        try {
            WorldgenHangWatchdog.startWorldGen();
            ((IChunk) chunk).rdpl$dress(provider.chunkGenerator);
        }
        finally { WorldgenHangWatchdog.endWorldGen(); }
        dressedLate++;
    }

    private void brighten(ChunkProviderServer provider, int madeX, int madeZ) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int x = madeX + dx;
                int z = madeZ + dz;
                if (!held.contains(ChunkPos.asLong(x, z)) || !ringHeld(x, z)) { continue; }
                Chunk chunk = provider.getLoadedChunk(x, z);
                if (chunk == null || chunk.isLightPopulated() || !chunk.isTerrainPopulated()) { continue; }
                chunk.checkLight();
                if (chunk.isLightPopulated()) {
                    brightened++;
                    chunk.markDirty();
                }
            }
        }
    }

    private boolean ringHeld(int x, int z) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) { continue; }
                if (!held.contains(ChunkPos.asLong(x + dx, z + dz))) { return false; }
            }
        }
        return true;
    }

    private void release(ChunkProviderServer provider, long key) {
        int x = (int) key;
        int z = (int) (key >> 32);
        if (provider.world.getPlayerChunkMap().contains(x, z)) { return; }
        Chunk chunk = provider.getLoadedChunk(x, z);
        if (chunk == null) { return; }
        if (RubicWorldControl.rubicWorld(provider)) { RubicWorldControl.unloadColumnCubes(provider, chunk); }
        else if (!chunk.isLightPopulated()) {
            if (x <= lowX || x >= highX || z <= lowZ || z >= highZ) { darkAtEdge++; }
            else { dark++; }
        }
        provider.queueUnload(chunk);
    }

    private void tellScreen(MinecraftServer server) {
        if (server == null) { return; }
        long total = order.total();
        int percent = total <= 0L ? 0 : (int) Math.min(100L, done * 100L / total);
        IMinecraftServerMessage progress = (IMinecraftServerMessage) server;
        progress.rdpl$setCurrentTask("Preparing spawn area");
        progress.rdpl$setPercentDone(percent);
        progress.rdpl$setUserMessage("menu.generatingTerrain");
    }

    private void checkpoint(WorldServer world) {
        long now = System.currentTimeMillis();
        if (checkpointed == 0L) { checkpointed = now; }
        if (now - checkpointed < Math.max(120000L, checkpointCost * 100L)) { return; }
        checkpointed = now;
        long writing = System.nanoTime();
        try {
            world.getSaveHandler().saveWorldInfo(world.getWorldInfo());
            MapStorage shared = world.getMapStorage();
            if (shared != null) { shared.saveAllData(); }
            world.getPerWorldStorage().saveAllData();
        }
        catch (Exception oops) { ContentLog.LOGGER.error("The safeguard save of the world records failed, so a crash from here would lose progress made since the last one that worked", oops); }
        long spent = (System.nanoTime() - writing) / 1000000L;
        checkpointCost = spent;
        ContentLog.LOGGER.debug("Safeguard save of the world records took {} ms at {} column(s)", spent, done);
        if (spent > 500L) { ContentLog.LOGGER.info("The safeguard save of the world records took {} ms and grows with the land already made; this is the checkpoint cost, not generation", spent); }
    }

    private void speak() {
        long now = System.currentTimeMillis();
        if (now - spoke < 10000L) { return; }
        spoke = now;
        ContentLog.LOGGER.info(ContentPregenProgress.report(this));
        if (!ContentPregenDimensions.picksUpAgain() || lightOnly) { return; }
        IPregenMemory memory = ContentPregenDimensions.memory();
        if (memory != null) { memory.rdpl$setLandMadeAt(dimension, (int) done); }
    }

    @SuppressWarnings({"ConstantConditions", "ConstantValue"}) private static void keepPristine() {
        if (!ContentControl.flag(ContentControl.CHUNKS, "pregenBackup", Config.chunks.pregenBackup)) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        WorldServer world = server.getWorld(0);
        if (world == null || ContentPristine.already(server)) { return; }
        String said = ContentPregenProgress.says("pregenBackupSays", Config.chunks.pregenBackupSays).trim();
        try { server.saveAllWorlds(true); }
        catch (RuntimeException notSaved) { ContentLog.LOGGER.warn("The worlds could not be flushed before the pristine copy, copying what is on disk", notSaved); }
        long begun = System.currentTimeMillis();
        int[] last = { -1 };
        int files = ContentPristine.take(server, along -> {
            if (said.isEmpty() || along == last[0]) { return; }
            last[0] = along;
            ContentPregenHold.tellBar(server, said + " " + along + "%");
        });
        if (files > 0) { ContentPristine.mark(server); }
        if (files > 0 && !said.isEmpty()) { ContentPregenHold.tellBar(server, said + " " + Lang.tr("rdpl.pregen.done")); }
        ContentLog.LOGGER.info("The pristine copy took {} ms", System.currentTimeMillis() - begun);
    }

    public static boolean holdsStill(net.minecraft.world.World world) {
        if (world.isRemote) { return false; }
        ContentPregen worker = running;
        if (worker != null) { return world.provider.getDimension() == worker.dimension; }
        return System.currentTimeMillis() < stillUntil;
    }

    private void finish(WorldServer world) {
        if (running != this) {
            over = true;
            return;
        }
        over = true;
        running = null;
        ContentPregenHold.progress = "";
        stillUntil = System.currentTimeMillis() + STILL_AFTER_MS;
        if (dimension != 0) { DimensionManager.keepDimensionLoaded(dimension, false); }
        boolean whole = world != null && !stopping && !order.hasNext();
        if (world != null) {
            IPregenMemory memory = ContentPregenDimensions.memory();
            if (memory != null) {
                if ((whole || (stopping && !ContentPregenDimensions.picksUpAgain())) && !lightOnly && reach > memory.rdpl$landMadeTo(dimension)) { memory.rdpl$setLandMadeTo(dimension, reach); }
                if (!lightOnly) {
                    memory.rdpl$setLandMadeAt(dimension, whole || !ContentPregenDimensions.picksUpAgain() ? 0 : (int) done);
                    memory.rdpl$setPregenRun(null);
                }
            }
        }
        if (world != null) {
            ChunkProviderServer provider = world.getChunkProvider();
            for (long key : resident) { release(provider, key); }
        }
        resident.clear();
        held.clear();
        ContentLog.LOGGER.info("Finished. " + ContentPregenProgress.report(this));
        if (lightOnly && world != null) { dressHeld(world); }
        else { DRESS_LATER.clear(); }
        if (stopping) { ContentPregenDimensions.PENDING.clear(); }
        if (whole && !lightOnly && !rubicRun) {
            ContentLog.LOGGER.info("Going back over dimension {} to light what the making of it could not reach", dimension);
            start(asked, dimension, middleX, middleZ, reach, true);
            return;
        }
        if (ContentPregenDimensions.chaining && !ContentPregenDimensions.PENDING.isEmpty()) { ContentPregenDimensions.nextDimension(ContentPregenDimensions.wantedRadius); }
        else { ContentPregenDimensions.chaining = false; }
        if (running == null) {
            BlockFalling.fallInstantly = false;
            String ending = stopping ? ContentPregenProgress.defaulted("pregenStoppedSays", Config.chunks.pregenStoppedSays, ContentPregenProgress.SHIPPED.pregenStoppedSays, "rdpl.pregen.stopped", null) : ContentPregenProgress.defaulted("pregenFinishedSays", Config.chunks.pregenFinishedSays, ContentPregenProgress.SHIPPED.pregenFinishedSays, "rdpl.pregen.done", null);
            if (!ending.isEmpty() && chainBegun != 0L) {
                long took = (System.currentTimeMillis() - chainBegun) / 1000L;
                ending += Lang.tr("rdpl.pregen.tooktime", took / 3600L, took / 60L % 60L, took % 60L);
            }
            chainBegun = 0L;
            ContentPregenHold.tell(ending, TextFormatting.GREEN);
            if (!stopping) { keepPristine(); }
            ContentPregenHold.releaseEveryone(!stopping);
        }
        if (asked != null && !(asked instanceof EntityPlayer)) { asked.sendMessage(new TextComponentString(Lang.tr("rdpl.pregen.finished", ContentPregenProgress.report(this))).setStyle(new Style().setColor(TextFormatting.GREEN))); }
    }
}
