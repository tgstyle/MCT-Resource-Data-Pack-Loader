package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentWelcome;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Says;

import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IllegalFormatException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

public final class ContentPregen {
    public static final int VANILLA_SPAWN_REACH = 12;
    private static final long STILL_AFTER_MS = 3000L;
    private static final long STALL_MS = 60000L;
    private static final double NO_BORDER = 5.9E7D;
    private static final String HELD_MODE = "rdplPregenHeldMode";
    private static final String OVERWORLD = "minecraft:overworld";
    private static final Deque<ResourceLocation> PENDING = new ArrayDeque<>();
    private static final Map<UUID, Held> HELD = new LinkedHashMap<>();
    @Nullable private static ContentPregen running;
    private static long stillUntil;
    private static long chainBegun;
    private static int wantedRadius;
    private static boolean chaining;
    private static String progress = "";
    private static String lastSaid = "";
    private static long beats;
    private static long heldDayTime = -1L;
    @Nullable private final ServerPlayer asked;
    private final MinecraftServer server;
    private final ResourceKey<Level> dimension;
    private final int reach;
    private final int middleX;
    private final int middleZ;
    private final ContentChunkOrder order;
    private final int inFlight;
    private final long started;
    private long done;
    private long made;
    private long failed;
    private int flying;
    private long spoke;
    private long resumedFrom;
    private long etaFigured;
    private long etaSeconds;
    private long etaAt;
    private long etaMade;
    private double etaRate;
    private int loggedAt;
    private long begun;
    private long watchedDone = -1L;
    private long watchedAt;
    private boolean stopping;

    private ContentPregen(@Nullable ServerPlayer asked, MinecraftServer server, ResourceKey<Level> dimension, int middleX, int middleZ, int reach) {
        this.asked = asked;
        this.server = server;
        this.dimension = dimension;
        this.reach = reach;
        this.middleX = middleX;
        this.middleZ = middleZ;
        this.order = new ContentChunkOrder(middleX, middleZ, reach);
        this.inFlight = Math.max(1, Math.min(512, ContentControl.number(ContentControl.CHUNKS, "pregenChunksInFlight", Config.chunks.pregenChunksInFlight())));
        this.started = System.currentTimeMillis();
    }

    private static final class Held {
        private final GameType before;
        private final String warning;
        private ResourceKey<Level> dimension;
        private double x;
        private double y;
        private double z;
        private float yaw;
        private float pitch;

        private Held(ServerPlayer player, GameType before) {
            this.before = before;
            this.warning = defaulted("pregenSpectatingSays", Config.chunks.pregenSpectatingSays(), Config.PREGEN_SPECTATING, "rdpl.pregen.spectating", player);
            rebase(player);
        }

        private void rebase(ServerPlayer player) {
            this.dimension = player.level().dimension();
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();
            this.yaw = player.getYRot();
            this.pitch = player.getXRot();
        }

        private boolean strayed(ServerPlayer player) {
            double dx = player.getX() - x;
            double dy = player.getY() - y;
            double dz = player.getZ() - z;
            return dx * dx + dy * dy + dz * dz > 1.0E-4D;
        }
    }

    public static boolean busy() { return running != null; }

    public static boolean holdsStill(Level level) {
        if (level.isClientSide()) { return false; }
        ContentPregen worker = running;
        if (worker != null) { return level.dimension().equals(worker.dimension); }
        return System.currentTimeMillis() < stillUntil;
    }

    public static boolean reachesTheBorder() { return ContentControl.flag(ContentControl.CHUNKS, "pregenToBorder", Config.chunks.pregenToBorder()); }

    public static boolean picksUpAgain() { return ContentControl.flag(ContentControl.CHUNKS, "pregenResume", Config.chunks.pregenResume()); }

    public static int wantedOnNewWorld() {
        int asked = Math.max(0, ContentControl.number(ContentControl.CHUNKS, "pregenOnNewWorld", Config.chunks.pregenOnNewWorld()));
        return Math.max(asked, VANILLA_SPAWN_REACH);
    }

    private static boolean asksForLand() { return ContentControl.number(ContentControl.CHUNKS, "pregenOnNewWorld", Config.chunks.pregenOnNewWorld()) > 0 || reachesTheBorder(); }

    private static List<ResourceLocation> ids(String key, List<String> fallback) {
        List<ResourceLocation> out = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.CHUNKS, key, fallback)) {
            ResourceLocation id = ResourceLocation.tryParse(ContentFormats.dimensionId(entry));
            if (id == null) { ContentLog.LOGGER.error("{} names '{}', which is not a dimension id, ignoring it", key, entry); }
            else { out.add(id); }
        }
        return out;
    }

    private static List<ResourceLocation> chosenDimensions(MinecraftServer server) {
        if (!ContentControl.flag(ContentControl.CHUNKS, "pregenAllDimensions", Config.chunks.pregenAllDimensions())) { return ids("pregenDimensions", Config.chunks.pregenDimensions()); }
        List<ResourceLocation> picked = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            if (madeUpFront(level.dimension().location())) { picked.add(level.dimension().location()); }
        }
        picked.sort(null);
        ResourceLocation overworld = ResourceLocation.parse(OVERWORLD);
        if (picked.remove(overworld)) { picked.add(0, overworld); }
        return picked;
    }

    private static boolean madeUpFront(ResourceLocation dimension) { return !ids("pregenDimensionsWhenEntered", Config.chunks.pregenDimensionsWhenEntered()).contains(dimension); }

    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        if (ContentControl.off(ContentControl.CHUNKS) || busy()) { return; }
        int spawnChunks = ContentControl.number(ContentControl.CHUNKS, "spawnChunkRadius", Config.chunks.spawnChunkRadius());
        if (spawnChunks >= 0) { ContentLog.LOGGER.info("spawnChunkRadius is set to {}, which this line cannot apply: the game has no such setting before 1.20.5, so the spawn chunks stay as they are", spawnChunks); }
        int radius = wantedOnNewWorld();
        PregenMemory memory = PregenMemory.of(server);
        CompoundTag run = memory.run();
        if (run != null) {
            ResourceLocation dimension = ResourceLocation.parse(run.getString("dimension"));
            if (!picksUpAgain()) {
                memory.setRun(null);
                memory.setMadeAt(dimension.toString(), 0);
                if (run.getInt("reach") > memory.madeTo(dimension.toString())) { memory.setMadeTo(dimension.toString(), run.getInt("reach")); }
                ContentLog.LOGGER.info("Land was still being made in {} when the last session ended, and picking up again is off, so the world stands as far as it was made", dimension);
                return;
            }
            if (PENDING.isEmpty() && asksForLand()) {
                for (ResourceLocation held : chosenDimensions(server)) {
                    if (!held.equals(dimension) && (reachesTheBorder() || memory.madeTo(held.toString()) < radius)) { PENDING.addLast(held); }
                }
                wantedRadius = radius;
                chaining = !PENDING.isEmpty();
            }
            ContentLog.LOGGER.info("Land was still being made in {} when the last session ended, so it is picked up again", dimension);
            start(null, server, ResourceKey.create(Registries.DIMENSION, dimension), run.getInt("middleX"), run.getInt("middleZ"), run.getInt("reach"));
            return;
        }
        if (!asksForLand() || !PENDING.isEmpty()) { return; }
        for (ResourceLocation dimension : chosenDimensions(server)) {
            if (reachesTheBorder() || memory.madeTo(dimension.toString()) < radius) { PENDING.addLast(dimension); }
        }
        wantedRadius = radius;
        chaining = true;
        nextDimension(server, radius);
    }

    private static void startWhenEntered(MinecraftServer server, ResourceKey<Level> entered) {
        if (ContentControl.off(ContentControl.CHUNKS)) { return; }
        int radius = wantedOnNewWorld();
        ResourceLocation id = entered.location();
        if (!asksForLand() || madeUpFront(id)) { return; }
        if (running != null && running.dimension.equals(entered)) { return; }
        if (PENDING.contains(id)) { return; }
        ServerLevel level = server.getLevel(entered);
        if (!reachesTheBorder() && level != null) {
            BlockPos spawn = level.getSharedSpawnPos();
            if (alreadyMade(PregenMemory.of(server), level, radius, spawn.getX() >> 4, spawn.getZ() >> 4)) { return; }
        }
        PENDING.addLast(id);
        wantedRadius = radius;
        chaining = true;
        if (!busy()) { nextDimension(server, radius); }
    }

    private static void nextDimension(MinecraftServer server, int radius) {
        while (!PENDING.isEmpty()) {
            ResourceLocation id = PENDING.removeFirst();
            ServerLevel level = server.getLevel(ResourceKey.create(Registries.DIMENSION, id));
            if (level == null) {
                ContentLog.LOGGER.error("A pack asks for land to be made in {}, which nothing here provides, so it is passed over", id);
                continue;
            }
            int middleX;
            int middleZ;
            int reach;
            if (reachesTheBorder()) {
                WorldBorder border = level.getWorldBorder();
                if (border.getSize() >= NO_BORDER) {
                    ContentLog.LOGGER.error("A pack asks for the land of {} to be made out to its border, but no border has been set there, so there is nothing to reach and none is made", id);
                    continue;
                }
                reach = (int) Math.ceil(border.getSize() / 2.0D / 16.0D);
                int limit = Config.chunks.pregenBorderLimit();
                if (reach > limit) {
                    ContentLog.LOGGER.error("The border of {} stands {} block(s) across, which is {} chunk(s) either way and past the {} allowed by pregenBorderLimit, so no land is made out to it", id, (long) border.getSize(), reach, limit);
                    continue;
                }
                middleX = (int) Math.floor(border.getCenterX()) >> 4;
                middleZ = (int) Math.floor(border.getCenterZ()) >> 4;
            }
            else {
                BlockPos spawn = level.getSharedSpawnPos();
                reach = radius;
                middleX = spawn.getX() >> 4;
                middleZ = spawn.getZ() >> 4;
            }
            if (alreadyMade(PregenMemory.of(server), level, reach, middleX, middleZ)) { continue; }
            long total = start(null, server, level.dimension(), middleX, middleZ, reach);
            ContentLog.LOGGER.info("Making {} chunk(s) of land in {}, reaching {} chunk(s) either way from {}, {}, before anybody sets foot in it", total, id, reach, middleX, middleZ);
            return;
        }
    }

    private static boolean alreadyMade(PregenMemory memory, ServerLevel level, int reach, int middleX, int middleZ) {
        String dimension = level.dimension().location().toString();
        if (memory.madeTo(dimension) < reach) { return false; }
        File region = DimensionType.getStorageFolder(level.dimension(), level.getServer().getWorldPath(LevelResource.ROOT)).resolve("region").toFile();
        if (!region.isDirectory()) { return true; }
        int expected = 0;
        int missing = 0;
        for (int rx = (middleX - reach) >> 5; rx <= (middleX + reach) >> 5; rx++) {
            for (int rz = (middleZ - reach) >> 5; rz <= (middleZ + reach) >> 5; rz++) {
                expected++;
                if (!new File(region, "r." + rx + "." + rz + ".mca").isFile()) { missing++; }
            }
        }
        if (missing == 0) { return true; }
        ContentLog.LOGGER.info("{} was made before, but {} of the {} region file(s) its land lives in are missing from the disk, so it is being made again", dimension, missing, expected);
        memory.setMadeTo(dimension, 0);
        memory.setMadeAt(dimension, 0);
        return false;
    }

    private static CompoundTag runRecord(ResourceKey<Level> dimension, int middleX, int middleZ, int reach) {
        CompoundTag run = new CompoundTag();
        run.putString("dimension", dimension.location().toString());
        run.putInt("middleX", middleX);
        run.putInt("middleZ", middleZ);
        run.putInt("reach", reach);
        return run;
    }

    public static long start(@Nullable ServerPlayer asked, MinecraftServer server, ResourceKey<Level> dimension, int middleX, int middleZ, int reach) {
        ContentPregen worker = new ContentPregen(asked, server, dimension, middleX, middleZ, reach);
        PregenMemory memory = PregenMemory.of(server);
        memory.setRun(runRecord(dimension, middleX, middleZ, reach));
        if (picksUpAgain()) {
            int reached = memory.madeAt(dimension.location().toString());
            if (reached > 0) {
                worker.done = worker.order.skip(reached);
                worker.resumedFrom = worker.done;
                ContentLog.LOGGER.info("Picking the making of land in {} up again where it left off, {} chunk(s) in", dimension.location(), worker.done);
            }
        }
        running = worker;
        if (chainBegun == 0L) { chainBegun = System.currentTimeMillis(); }
        heldDayTime = server.overworld().getDayTime();
        holdEveryone(server);
        return worker.order.total();
    }

    public static boolean stop() {
        if (running == null) { return false; }
        running.stopping = true;
        return true;
    }

    public static String state() {
        ContentPregen worker = running;
        return worker == null ? Lang.tr("rdpl.pregen.idle") : worker.report();
    }

    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        ContentPregen worker = running;
        if (worker != null) {
            worker.work();
            event.getServer().overworld().setDayTime(heldDayTime);
        }
        holdTick(event.getServer());
    }

    private void work() {
        if (begun == 0L) { begun = System.currentTimeMillis(); }
        ServerLevel level = server.getLevel(dimension);
        if (level == null || stopping) {
            if (flying == 0) { finish(level); }
            return;
        }
        watch();
        ServerChunkCache chunks = level.getChunkSource();
        while (flying < inFlight && order.hasNext()) {
            ChunkPos next = order.next();
            flying++;
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> future = chunks.getChunkFuture(next.x, next.z, ChunkStatus.FULL, true);
            future.whenCompleteAsync((result, thrown) -> landed(result != null && result.left().isPresent(), thrown), server);
        }
        speak();
        if (!order.hasNext() && flying == 0) { finish(level); }
    }

    private void landed(boolean success, @Nullable Throwable thrown) {
        flying--;
        done++;
        if (success) { made++; }
        else {
            failed++;
            if (thrown != null && failed <= 3) { ContentLog.LOGGER.error("A chunk asked for while making land could not be made", thrown); }
        }
        progress = sofar();
        int tenth = (int) (done * 10L / Math.max(1L, order.total()));
        if (tenth > loggedAt) {
            loggedAt = tenth;
            if (!progress.isEmpty()) { ContentLog.LOGGER.info(progress); }
        }
    }

    private void watch() {
        long now = System.currentTimeMillis();
        if (done != watchedDone) {
            watchedDone = done;
            watchedAt = now;
            return;
        }
        if (now - watchedAt < STALL_MS) { return; }
        ContentLog.LOGGER.error("Making land in {} has not moved past {} of {} chunk(s) for a minute, so it is being stopped rather than left hanging. What went wrong should be written above this line", dimension.location(), done, order.total());
        stopping = true;
        flying = 0;
    }

    private void speak() {
        long now = System.currentTimeMillis();
        if (now - spoke < 10000L) { return; }
        spoke = now;
        ContentLog.LOGGER.info(report());
        if (!picksUpAgain()) { return; }
        PregenMemory.of(server).setMadeAt(dimension.location().toString(), (int) Math.min(Integer.MAX_VALUE, done - flying));
    }

    private void finish(@Nullable ServerLevel level) {
        if (running != this) { return; }
        running = null;
        progress = "";
        stillUntil = System.currentTimeMillis() + STILL_AFTER_MS;
        boolean whole = level != null && !stopping && !order.hasNext();
        String id = dimension.location().toString();
        PregenMemory memory = PregenMemory.of(server);
        if (level != null) {
            if ((whole || (stopping && !picksUpAgain())) && reach > memory.madeTo(id)) { memory.setMadeTo(id, reach); }
            memory.setMadeAt(id, whole || !picksUpAgain() ? 0 : (int) Math.min(Integer.MAX_VALUE, done - flying));
            memory.setRun(null);
        }
        ContentLog.LOGGER.info("Finished. {}", report());
        if (stopping) { PENDING.clear(); }
        if (chaining && !PENDING.isEmpty()) { nextDimension(server, wantedRadius); }
        else { chaining = false; }
        if (running == null) {
            String ending = stopping ? defaulted("pregenStoppedSays", Config.chunks.pregenStoppedSays(), Config.PREGEN_STOPPED, "rdpl.pregen.stopped", null) : defaulted("pregenFinishedSays", Config.chunks.pregenFinishedSays(), Config.PREGEN_FINISHED, "rdpl.pregen.done", null);
            if (!ending.isEmpty() && chainBegun != 0L) {
                long took = (System.currentTimeMillis() - chainBegun) / 1000L;
                ending += Lang.tr("rdpl.pregen.tooktime", took / 3600L, took / 60L % 60L, took % 60L);
            }
            chainBegun = 0L;
            Says.tellAll(server, ending, ChatFormatting.GREEN);
            releaseEveryone(server, !stopping);
        }
        if (asked != null) { asked.sendSystemMessage(Component.literal(Lang.tr(asked, "rdpl.pregen.finished", report())).withStyle(ChatFormatting.GREEN)); }
    }

    private static String says(String key, String fallback) { return ContentControl.text(ContentControl.CHUNKS, key, fallback).trim(); }

    private static String defaulted(String key, String fallback, String shipped, String langKey, @Nullable ServerPlayer player) {
        String said = says(key, fallback);
        if (!said.equals(shipped.trim())) { return said; }
        return player == null ? Lang.tr(langKey) : Lang.tr(player, langKey);
    }

    private String sofar() {
        String wording = defaulted("pregenRunningSays", Config.chunks.pregenRunningSays(), Config.PREGEN_RUNNING, "rdpl.pregen.running", null);
        if (wording.isEmpty()) { return ""; }
        long stepped = Math.min(100L, done * 100L / Math.max(1L, order.total()));
        try { return String.format(wording, order.hasNext() || flying > 0 ? stepped : 100L, dimension.location()) + eta(); }
        catch (IllegalFormatException wrong) {
            ContentLog.LOGGER.error("A pack words the message about land being made as '{}', which is not something a number can be put into, so it is said as it stands", wording, wrong);
            return wording + eta();
        }
    }

    private String eta() {
        long total = order.total();
        if (begun == 0L || done <= resumedFrom || done >= total) { return ""; }
        long now = System.currentTimeMillis();
        if (etaFigured == 0L || now - etaFigured >= 5000L) {
            if (etaAt != 0L && now > etaAt && done > etaMade) {
                double lately = (done - etaMade) * 1000.0D / (now - etaAt);
                etaRate = etaRate == 0.0D ? lately : etaRate * 0.9D + lately * 0.1D;
            }
            etaAt = now;
            etaMade = done;
            etaFigured = now;
            if (etaRate > 0.0D) { etaSeconds = (long) ((total - done) / etaRate); }
        }
        if (etaRate <= 0.0D) { return ""; }
        long left = Math.max(0L, etaSeconds - (now - etaFigured) / 1000L);
        return Lang.tr("rdpl.pregen.eta", left / 3600L, left / 60L % 60L, left % 60L);
    }

    private String report() {
        long seconds = Math.max(1L, (System.currentTimeMillis() - started) / 1000L);
        long rate = (done - resumedFrom) / seconds;
        return String.format("Made %d of %d chunk(s) in %s, %d of them new or loaded, %d refused, at %d a second with %d asked for at once", done, order.total(), dimension.location(), made, failed, rate, inFlight);
    }

    private static void holdEveryone(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { hold(player); }
    }

    private static void hold(ServerPlayer player) {
        if (HELD.containsKey(player.getUUID())) { return; }
        CompoundTag data = player.getPersistentData();
        GameType before = data.contains(HELD_MODE) ? GameType.byId(data.getInt(HELD_MODE)) : player.gameMode.getGameModeForPlayer();
        data.putInt(HELD_MODE, before.getId());
        Held held = new Held(player, before);
        HELD.put(player.getUUID(), held);
        player.setGameMode(GameType.SPECTATOR);
        RDPLNetwork.sendHold(player, true);
        flash(player, held);
        beats = 0L;
        lastSaid = "";
    }

    private static void holdTick(MinecraftServer server) {
        if (HELD.isEmpty()) { return; }
        if (!busy()) {
            int stranded = 0;
            for (UUID id : HELD.keySet()) {
                if (!ContentIntroPlay.reading(id) && server.getPlayerList().getPlayer(id) != null) { stranded++; }
            }
            if (stranded > 0) {
                ContentLog.LOGGER.warn("{} player(s) were still held as spectators with no land being made, so they are released now; the release at the end of the run was missed", stranded);
                releaseEveryone(server, true);
                return;
            }
        }
        ContentPregen live = running;
        String said = live == null ? progress : live.sofar();
        if (!said.isEmpty()) { progress = said; }
        boolean titles = beats % 30L == 0L;
        boolean keepAlive = beats % 40L == 0L;
        beats++;
        boolean changed = !said.isEmpty() && !said.equals(lastSaid);
        if (changed) { lastSaid = said; }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Held held = HELD.get(player.getUUID());
            if (held == null) { continue; }
            if (!player.level().dimension().equals(held.dimension)) { held.rebase(player); }
            else if (held.strayed(player)) { player.connection.teleport(held.x, held.y, held.z, held.yaw, held.pitch); }
            if (titles) { flash(player, held); }
            if (changed || (keepAlive && !said.isEmpty())) { player.displayClientMessage(Component.literal(said).withStyle(ChatFormatting.YELLOW), true); }
        }
    }

    private static void flash(ServerPlayer player, Held held) {
        if (held.warning.isEmpty()) { return; }
        player.connection.send(new ClientboundSetTitlesAnimationPacket(0, 15, 10));
        player.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(held.warning).withStyle(ChatFormatting.RED)));
        player.connection.send(new ClientboundSetTitleTextPacket(Component.empty()));
    }

    private static void releaseEveryone(MinecraftServer server, boolean welcomed) {
        if (HELD.isEmpty()) { return; }
        int released = 0;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Held held = HELD.get(player.getUUID());
            if (held == null || ContentIntroPlay.reading(player.getUUID())) { continue; }
            HELD.remove(player.getUUID());
            release(player, held);
            if (welcomed) { ContentWelcome.welcome(player); }
            released++;
        }
        HELD.keySet().removeIf(id -> server.getPlayerList().getPlayer(id) == null);
        if (released > 0) { ContentLog.LOGGER.info("Released {} player(s) held while the land was made, back to {}", released, ContentTerrain.worldGameMode().isEmpty() ? "the mode they had" : ContentTerrain.worldGameMode()); }
    }

    public static void releaseAfterIntro(ServerPlayer player) {
        if (busy()) { return; }
        Held held = HELD.remove(player.getUUID());
        if (held == null) { return; }
        release(player, held);
        ContentWelcome.welcome(player);
    }

    private static void release(ServerPlayer player, Held held) {
        String mode = ContentTerrain.worldGameMode().trim();
        GameType asked = mode.isEmpty() ? null : GameType.byName(mode.toLowerCase(java.util.Locale.ROOT), null);
        player.setGameMode(asked == null ? held.before : asked);
        player.getPersistentData().remove(HELD_MODE);
        player.setPortalCooldown();
        RDPLNetwork.sendHold(player, false);
        player.connection.send(new ClientboundClearTitlesPacket(true));
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        startWhenEntered(player.server, player.level().dimension());
        ContentPregen worker = running;
        if (worker == null) { return; }
        hold(player);
        String said = worker.sofar();
        if (!said.isEmpty()) { Says.tell(player, said, ChatFormatting.YELLOW); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        Held held = HELD.remove(player.getUUID());
        if (held != null) { release(player, held); }
    }

    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { startWhenEntered(player.server, event.getTo()); }
    }

    public static void onTravel(EntityTravelToDimensionEvent event) {
        if (busy()) { event.setCanceled(true); }
    }

    public static void onTeleport(EntityTeleportEvent event) {
        if (busy() && (event instanceof EntityTeleportEvent.EnderPearl || event instanceof EntityTeleportEvent.EnderEntity || event instanceof EntityTeleportEvent.ChorusFruit)) { event.setCanceled(true); }
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        ContentPregen worker = running;
        if (worker != null) {
            ContentLog.LOGGER.info("The server is stopping while land is still being made in {}, so the run is wound down at {} chunk(s) to be picked up on the next load", worker.dimension.location(), worker.done);
            worker.stopping = true;
            worker.flying = 0;
            worker.finish(event.getServer().getLevel(worker.dimension));
            PregenMemory.of(event.getServer()).setRun(runRecord(worker.dimension, worker.middleX, worker.middleZ, worker.reach));
        }
        releaseEveryone(event.getServer(), false);
        PENDING.clear();
        chaining = false;
        chainBegun = 0L;
        wantedRadius = 0;
        HELD.clear();
    }

    public static boolean welcomesLater(ServerPlayer player) { return busy() || HELD.containsKey(player.getUUID()); }
}
