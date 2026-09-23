package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IChunkMap;
import mctmods.resourcedatapackloader.mixin.rdpl.common.ILevel;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Says;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentPregen {
    private static final int NEIGHBORS = 8;
    private static final long STILL_AFTER_MS = 3000L;
    private static final long SWEEP_AFTER_MS = 60000L;
    private static final long SETTLE_MS = 30000L;
    private static final long STALL_MS = 60000L;
    private static final TicketType<ChunkPos> TICKET = TicketType.create("rdpl_pregen", Comparator.comparingLong(ChunkPos::toLong));
    @Nullable static ContentPregen running;
    private static long stillUntil;
    private static long sweepUntil;
    private static long chainBegun;
    private static volatile long worldgenAt;
    @Nullable static Runnable pendingStart;
    static int startTick = -1;
    @Nullable private final CommandSourceStack asked;
    private final MinecraftServer server;
    final ResourceKey<Level> dimension;
    private final int reach;
    private final int middleX;
    private final int middleZ;
    final ContentChunkOrder order;
    final int inFlight;
    private final Deque<ChunkPos> unfollowed = new ArrayDeque<>();
    private final LongSet ticketed = new LongOpenHashSet();
    final LongSet flying = new LongOpenHashSet();
    private final LongSet existing = new LongOpenHashSet();
    final long started;
    long done;
    long made;
    long fresh;
    long failed;
    private long tileFlying = -1L;
    private long safeTile;
    private long safeWithin;
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
    long begun;
    private long watchedDone = -1L;
    private long watchedAt;
    private boolean stopping;

    private ContentPregen(@Nullable CommandSourceStack asked, MinecraftServer server, ResourceKey<Level> dimension, int middleX, int middleZ, int reach) {
        this.asked = asked;
        this.server = server;
        this.dimension = dimension;
        this.reach = reach;
        this.middleX = middleX;
        this.middleZ = middleZ;
        this.order = new ContentChunkOrder(middleX, middleZ, reach);
        this.inFlight = Math.clamp(ContentControl.number(ContentControl.CHUNKS, "pregenChunksInFlight", Config.chunks.pregenChunksInFlight()), 1, 512);
        this.started = System.currentTimeMillis();
    }

    public static boolean busy() { return running != null; }

    public static void worldgenMoved() { worldgenAt = System.currentTimeMillis(); }

    public static boolean landBeingMade() { return running != null || pendingStart != null; }

    public static boolean busyIn(Level level) {
        ContentPregen worker = running;
        return worker != null && level.dimension().equals(worker.dimension);
    }

    public static boolean holdsStill(Level level) {
        if (level.isClientSide()) { return false; }
        ContentPregen worker = running;
        if (worker != null) { return level.dimension().equals(worker.dimension); }
        return System.currentTimeMillis() < stillUntil;
    }

    static void later(MinecraftServer server, Runnable starter) {
        if (server.isDedicatedServer()) {
            starter.run();
            return;
        }
        pendingStart = starter;
        startTick = -1;
    }

    public static long start(@Nullable CommandSourceStack asked, MinecraftServer server, ResourceKey<Level> dimension, int middleX, int middleZ, int reach) {
        ContentPregen worker = new ContentPregen(asked, server, dimension, middleX, middleZ, reach);
        PregenMemory memory = PregenMemory.of(server);
        memory.setRun(ContentPregenDimensions.runRecord(dimension, middleX, middleZ, reach));
        if (ContentPregenDimensions.picksUpAgain()) {
            String id = dimension.location().toString();
            CompoundTag spot = memory.madeIn(id);
            long from;
            if (spot == null) { from = worker.order.skipMadeBefore(memory.madeAt(id)); }
            else if (spot.getInt("size") == worker.order.tileSize()) { from = worker.order.skipTo(spot.getLong("tile"), spot.getLong("within")); }
            else {
                from = 0L;
                ContentLog.LOGGER.info("The land of {} was left half made in tiles of {} chunk(s), which is not the {} chunk(s) tiles are made in now, so it is made again from the middle out", dimension.location(), spot.getInt("size"), worker.order.tileSize());
            }
            if (from > 0L) {
                worker.done = from;
                worker.resumedFrom = from;
                ContentLog.LOGGER.info("Picking the making of land in {} up again where it left off, {} chunk(s) in, tile {} of {}", dimension.location(), from, worker.order.tile(), worker.order.tiles());
            }
        }
        running = worker;
        if (chainBegun == 0L) { chainBegun = System.currentTimeMillis(); }
        ContentPregenHold.heldDayTime = server.overworld().getDayTime();
        ContentPregenHold.holdEveryone(server, true);
        return worker.order.total();
    }

    public static boolean stop() {
        if (running == null) { return false; }
        running.stopping = true;
        return true;
    }

    public static String state() {
        ContentPregen worker = running;
        return worker == null ? Lang.tr("rdpl.pregen.idle") : ContentPregenProgress.report(worker);
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (pendingStart != null && startTick >= 0 && (event.getServer().getTickCount() >= startTick || playersLoaded(event.getServer()))) {
            Runnable starter = pendingStart;
            pendingStart = null;
            startTick = -1;
            starter.run();
        }
        ContentPregen worker = running;
        if (worker != null) {
            worker.work();
            if (worker.dimension.equals(Level.OVERWORLD)) { event.getServer().overworld().setDayTime(ContentPregenHold.heldDayTime); }
            if (event.getServer().getTickCount() % 20 == 0) { ContentPregenHold.freezeSky(event.getServer()); }
        }
        if (event.getServer().getTickCount() % 20 == 0 && (worker != null || System.currentTimeMillis() < sweepUntil)) { sweepEmpty(event.getServer()); }
        ContentPregenHold.holdTick(event.getServer());
    }

    private static void sweepEmpty(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            if (level.players().isEmpty()) { ((ILevel) level).rdpl$blockEntityTickers().removeIf(TickingBlockEntity::isRemoved); }
        }
    }

    private void work() {
        if (begun == 0L) {
            begun = System.currentTimeMillis();
            pin();
        }
        ServerLevel level = server.getLevel(dimension);
        if (level == null || stopping) {
            if (level != null) { follow(level.getChunkSource()); }
            if (flying.isEmpty()) { finish(level); }
            return;
        }
        if (stalled()) {
            giveUp(level);
            return;
        }
        ServerChunkCache chunks = level.getChunkSource();
        follow(chunks);
        checkpoint(level);
        for (ServerPlayer player : level.players()) {
            if (!level.hasChunk(player.chunkPosition().x, player.chunkPosition().z)) { return; }
        }
        int burst = Math.max(1, inFlight / 4);
        int issued = 0;
        while (flying.size() < inFlight && order.hasNext() && issued < burst) {
            long tile = order.tile();
            if (tile != tileFlying) {
                if (!flying.isEmpty()) { break; }
                letGoFar(level);
                tileFlying = tile;
            }
            ChunkPos next = order.next();
            issued++;
            flying.add(next.toLong());
            if (((IChunkMap) chunks.chunkMap).rdpl$isExistingChunkFull(next)) { existing.add(next.toLong()); }
            chunks.addRegionTicket(TICKET, next, 0, next);
            ticketed.add(next.toLong());
            unfollowed.addLast(next);
        }
        speak();
        if (!order.hasNext() && flying.isEmpty()) { finish(level); }
    }

    private void follow(ServerChunkCache chunks) {
        for (int waiting = unfollowed.size(); waiting > 0; waiting--) {
            ChunkPos pos = unfollowed.removeFirst();
            ChunkHolder holder = chunks.chunkMap.getVisibleChunkIfPresent(pos.toLong());
            if (holder == null) {
                unfollowed.addLast(pos);
                continue;
            }
            holder.scheduleChunkGenerationTask(ChunkStatus.FULL, chunks.chunkMap).whenCompleteAsync((result, thrown) -> landed(pos, result != null && result.isSuccess(), thrown), server);
        }
    }

    private void settle(ServerChunkCache chunks) {
        long until = System.currentTimeMillis() + SETTLE_MS;
        server.managedBlock(() -> {
            follow(chunks);
            chunks.tick(() -> false, false);
            chunks.pollTask();
            return flying.isEmpty() || System.currentTimeMillis() >= until;
        });
        if (!flying.isEmpty()) { ContentLog.LOGGER.warn("{} chunk(s) of land had not come back {} seconds after the server began stopping, so they are left for the game to close and their tile is made again on the next load", flying.size(), SETTLE_MS / 1000L); }
    }

    private void letGoFar(ServerLevel level) {
        LongIterator held = ticketed.iterator();
        while (held.hasNext()) {
            ChunkPos pos = new ChunkPos(held.nextLong());
            if (order.nearTile(pos.x, pos.z, NEIGHBORS)) { continue; }
            level.getChunkSource().removeRegionTicket(TICKET, pos, 0, pos);
            held.remove();
        }
    }

    private void letGo(@Nullable ServerLevel level) {
        unfollowed.clear();
        flying.clear();
        existing.clear();
        if (level != null) {
            for (long key : ticketed) {
                ChunkPos pos = new ChunkPos(key);
                level.getChunkSource().removeRegionTicket(TICKET, pos, 0, pos);
            }
        }
        ticketed.clear();
    }

    private void landed(ChunkPos pos, boolean success, @Nullable Throwable thrown) {
        if (!flying.remove(pos.toLong())) { return; }
        done++;
        if (flying.isEmpty()) { pin(); }
        boolean known = existing.remove(pos.toLong());
        if (success) {
            made++;
            if (!known) { fresh++; }
        }
        else {
            failed++;
            if (thrown != null && failed <= 3) { ContentLog.LOGGER.error("The chunk at {}, {}, asked for while making land, could not be made", pos.x, pos.z, thrown); }
        }
        ContentPregenHold.progress = ContentPregenProgress.sofar(this);
        if ((done & 255L) == 0L) { telemetry(); }
        int tenth = (int) (done * 10L / Math.max(1L, order.total()));
        if (tenth > loggedAt) {
            loggedAt = tenth;
            if (!ContentPregenHold.progress.isEmpty()) { ContentLog.LOGGER.info(ContentPregenHold.progress); }
        }
    }

    private void telemetry() {
        ServerLevel level = server.getLevel(dimension);
        if (level == null) { return; }
        Runtime memory = Runtime.getRuntime();
        long used = (memory.totalMemory() - memory.freeMemory()) >> 20;
        ContentLog.LOGGER.debug("Pregen telemetry: chunks={}/{} tile={}/{} loadedChunks={} heap={}/{}MB", done, order.total(), order.tile(), order.tiles(), level.getChunkSource().getLoadedChunksCount(), used, memory.maxMemory() >> 20);
    }

    private void pin() {
        safeTile = order.tile();
        safeWithin = order.within();
    }

    private void remember() {
        if (!ContentPregenDimensions.picksUpAgain()) { return; }
        CompoundTag spot = new CompoundTag();
        spot.putInt("size", order.tileSize());
        spot.putLong("tile", safeTile);
        spot.putLong("within", safeWithin);
        PregenMemory.of(server).setMadeIn(dimension.location().toString(), spot);
    }

    private boolean stalled() {
        long now = System.currentTimeMillis();
        if (done != watchedDone) {
            watchedDone = done;
            watchedAt = now;
            return false;
        }
        return now - Math.max(watchedAt, worldgenAt) >= STALL_MS;
    }

    private void giveUp(ServerLevel level) {
        List<String> stuck = new ArrayList<>();
        LongIterator out = flying.iterator();
        while (out.hasNext() && stuck.size() < 8) {
            long key = out.nextLong();
            ChunkHolder holder = level.getChunkSource().chunkMap.getVisibleChunkIfPresent(key);
            ChunkStatus reached = holder == null ? null : holder.getLatestStatus();
            stuck.add(ChunkPos.getX(key) + ", " + ChunkPos.getZ(key) + (holder == null ? " not taken up by the game" : reached == null ? " not begun" : " stuck at " + reached));
        }
        ContentLog.LOGGER.error("Making land in {} has not moved past {} of {} chunk(s) for a minute, with no land generated anywhere in that time, so it is being stopped rather than left hanging. {} chunk(s) asked for never came back and {} were refused before this: {}", dimension.location(), done, order.total(), flying.size(), failed, stuck.isEmpty() ? "none is out, the run was waiting on a player's chunk" : String.join("; ", stuck));
        stopping = true;
        finish(level);
    }

    private void checkpoint(ServerLevel level) {
        long now = System.currentTimeMillis();
        if (checkpointed == 0L) { checkpointed = now; }
        if (now - checkpointed < Math.max(120000L, checkpointCost * 100L)) { return; }
        checkpointed = now;
        long writing = System.nanoTime();
        try {
            PregenMemory.of(server).setDirty();
            server.overworld().getDataStorage().save();
            if (level != server.overworld()) { level.getDataStorage().save(); }
        }
        catch (RuntimeException oops) { ContentLog.LOGGER.error("The safeguard save of the world records failed, so a crash from here would lose progress made since the last one that worked", oops); }
        long spent = (System.nanoTime() - writing) / 1000000L;
        checkpointCost = spent;
        ContentLog.LOGGER.debug("Safeguard save of the world records took {} ms at {} chunk(s)", spent, done);
        if (spent > 500L) { ContentLog.LOGGER.info("The safeguard save of the world records took {} ms and grows with the land already made; this is the checkpoint cost, not generation", spent); }
    }

    private void speak() {
        long now = System.currentTimeMillis();
        if (now - spoke < 10000L) { return; }
        spoke = now;
        ContentLog.LOGGER.info(ContentPregenProgress.report(this));
        remember();
    }

    private void finish(@Nullable ServerLevel level) {
        if (running != this) { return; }
        running = null;
        letGo(level);
        boolean whole = level != null && !stopping && !order.hasNext();
        if (whole) { ContentPregenHold.tellBar(server, ContentPregenHold.progress); }
        ContentPregenHold.progress = "";
        stillUntil = System.currentTimeMillis() + STILL_AFTER_MS;
        sweepUntil = System.currentTimeMillis() + SWEEP_AFTER_MS;
        String id = dimension.location().toString();
        PregenMemory memory = PregenMemory.of(server);
        if (level != null) {
            if ((whole || (stopping && !ContentPregenDimensions.picksUpAgain())) && reach > memory.madeTo(id)) { memory.setMadeTo(id, reach); }
            if (whole || !ContentPregenDimensions.picksUpAgain()) { memory.setMadeIn(id, null); }
            else { remember(); }
            memory.setRun(null);
        }
        ContentLog.LOGGER.info("Finished. {}", ContentPregenProgress.report(this));
        if (stopping) { ContentPregenDimensions.PENDING.clear(); }
        if (ContentPregenDimensions.chaining && !ContentPregenDimensions.PENDING.isEmpty()) { ContentPregenDimensions.nextDimension(server, ContentPregenDimensions.wantedRadius); }
        else { ContentPregenDimensions.chaining = false; }
        if (running == null) {
            String ending = stopping ? ContentPregenProgress.defaulted("pregenStoppedSays", Config.chunks.pregenStoppedSays(), Config.PREGEN_STOPPED, "rdpl.pregen.stopped", null) : ContentPregenProgress.defaulted("pregenFinishedSays", Config.chunks.pregenFinishedSays(), Config.PREGEN_FINISHED, "rdpl.pregen.done", null);
            if (!ending.isEmpty() && chainBegun != 0L) {
                long took = (System.currentTimeMillis() - chainBegun) / 1000L;
                ending += Lang.tr("rdpl.pregen.tooktime", took / 3600L, took / 60L % 60L, took % 60L);
            }
            chainBegun = 0L;
            Says.tellAll(server, ending, ChatFormatting.GREEN);
            if (!stopping) { keepPristine(server); }
            ContentPregenHold.releaseEveryone(server, !stopping);
        }
        if (asked != null && !asked.isPlayer()) { asked.sendSystemMessage(Component.literal(Lang.tr("rdpl.pregen.finished", ContentPregenProgress.report(this))).withStyle(ChatFormatting.GREEN)); }
    }

    private static void keepPristine(MinecraftServer server) {
        if (!ContentControl.flag(ContentControl.CHUNKS, "pregenBackup", Config.chunks.pregenBackup()) || ContentPristine.already(server)) { return; }
        String said = ContentPregenProgress.says("pregenBackupSays", Config.chunks.pregenBackupSays());
        try { server.saveAllChunks(true, true, true); }
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

    private static boolean playersLoaded(MinecraftServer server) {
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        if (players.isEmpty()) { return false; }
        for (ServerPlayer player : players) {
            if (!player.serverLevel().hasChunk(player.chunkPosition().x, player.chunkPosition().z)) { return false; }
        }
        return true;
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        ContentPregen worker = running;
        if (worker != null) {
            ContentLog.LOGGER.info("The server is stopping while land is still being made in {}, so the run is wound down at {} chunk(s) to be picked up on the next load", worker.dimension.location(), worker.done);
            worker.stopping = true;
            ServerLevel level = event.getServer().getLevel(worker.dimension);
            if (level != null) { worker.settle(level.getChunkSource()); }
            if (!worker.flying.isEmpty()) { worker.ticketed.clear(); }
            worker.flying.clear();
            worker.finish(level);
            PregenMemory.of(event.getServer()).setRun(ContentPregenDimensions.runRecord(worker.dimension, worker.middleX, worker.middleZ, worker.reach));
        }
        ContentPregenHold.releaseEveryone(event.getServer(), false);
        pendingStart = null;
        startTick = -1;
        ContentPregenDimensions.PENDING.clear();
        ContentPregenDimensions.chaining = false;
        chainBegun = 0L;
        ContentPregenDimensions.wantedRadius = 0;
        ContentPregenHold.forgetHeld();
    }
}
