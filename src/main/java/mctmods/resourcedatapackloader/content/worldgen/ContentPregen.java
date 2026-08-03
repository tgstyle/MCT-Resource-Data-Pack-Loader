package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.interfaces.PregenMemory;
import mctmods.resourcedatapackloader.mixin.AccessorChunk;
import mctmods.resourcedatapackloader.mixin.AccessorWorldProviderEnd;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.BlockFalling;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IllegalFormatException;
import java.util.Deque;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ContentPregen implements WorldWorkerManager.IWorker {
    private static ContentPregen running;
    private static final Deque<Integer> PENDING = new ArrayDeque<>();
    private static final Map<UUID, Held> HELD = new HashMap<>();
    private static long watchedDone = -1L;
    private static long watchedAt;
    private static long chainBegun;
    private static int wantedRadius;
    private static boolean chaining;
    private static final double NO_BORDER = 6.0E7D;
    private final ICommandSender asked;
    private final int dimension;
    private final int reach;
    private final boolean lightOnly;
    private final int middleX;
    private final int middleZ;
    private final int lowX;
    private final int lowZ;
    private final int highX;
    private final int highZ;
    private final ContentChunkOrder order;
    private final Deque<Long> resident = new ArrayDeque<>();
    private final Set<Long> held = new HashSet<>();
    private final int keep;
    private final int backlog;
    private final int slice;
    private final long started;
    private long done;
    private long made;
    private long undressed;
    private long dressedLate;
    private long paused;
    private long dark;
    private long darkAtEdge;
    private long missing;
    private int toldAt;
    private long brightened;
    private int round = -1;
    private long roundSpent;
    private long spoke;
    private long begun;
    private boolean over;
    private boolean stopping;

    private ContentPregen(ICommandSender asked, int dimension, int centreX, int centreZ, int radius, boolean lightOnly) {
        this.lightOnly = lightOnly;
        this.asked = asked;
        this.dimension = dimension;
        this.reach = radius;
        this.middleX = centreX;
        this.middleZ = centreZ;
        this.lowX = centreX - radius;
        this.lowZ = centreZ - radius;
        this.highX = centreX + radius;
        this.highZ = centreZ + radius;
        this.order = new ContentChunkOrder(centreX, centreZ, radius);
        this.keep = Math.max(64, ContentControl.number(ContentControl.CHUNKS, "pregenKeepLoaded", Config.chunks.pregenKeepLoaded));
        this.backlog = Math.max(0, ContentControl.number(ContentControl.CHUNKS, "pregenPauseAbove", Config.chunks.pregenPauseAbove));
        this.slice = Math.max(1, ContentControl.number(ContentControl.CHUNKS, "pregenMillisPerRound", Config.chunks.pregenMillisPerRound));
        this.started = System.currentTimeMillis();
    }

    public static int[] wantedDimensions() {
        return ContentControl.numbers(ContentControl.CHUNKS, "pregenDimensions", Config.chunks.pregenDimensions);
    }

    public static boolean makesEveryDimension() {
        return ContentControl.flag(ContentControl.CHUNKS, "pregenAllDimensions", Config.chunks.pregenAllDimensions);
    }

    private static int[] chosenDimensions() {
        if (!makesEveryDimension()) { return wantedDimensions(); }

        List<Integer> picked = new ArrayList<>();
        for (Integer dimension : DimensionManager.getStaticDimensionIDs()) {
            if (madeUpFront(dimension)) { picked.add(dimension); }
        }
        Collections.sort(picked);
        if (picked.remove(Integer.valueOf(0))) { picked.add(0, 0); }

        int[] chosen = new int[picked.size()];
        for (int at = 0; at < chosen.length; at++) { chosen[at] = picked.get(at); }
        return chosen;
    }

    public static int[] enteredDimensions() {
        return ContentControl.numbers(ContentControl.CHUNKS, "pregenDimensionsWhenEntered", Config.chunks.pregenDimensionsWhenEntered);
    }

    private static boolean madeUpFront(int dimension) {
        for (int later : enteredDimensions()) {
            if (later == dimension) { return false; }
        }
        return true;
    }

    private static void startWhenEntered(int dimension) {
        int radius = wantedOnNewWorld();
        if ((radius <= 0 && !reachesTheBorder()) || madeUpFront(dimension)) { return; }
        if (running != null && running.dimension == dimension) { return; }
        if (PENDING.contains(dimension)) { return; }

        WorldServer entered = DimensionManager.getWorld(dimension);
        if (!reachesTheBorder() && entered != null) {
            BlockPos spawn = entered.getSpawnPoint();
            if (alreadyMade(memory(), dimension, radius, entered, spawn.getX() >> 4, spawn.getZ() >> 4)) { return; }
        }

        PENDING.addLast(dimension);
        wantedRadius = radius;
        chaining = true;
        if (!busy()) { nextDimension(radius); }
    }

    @SubscribeEvent public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) { startWhenEntered(event.toDim); }

    public static boolean reachesTheBorder() {
        return ContentControl.flag(ContentControl.CHUNKS, "pregenToBorder", Config.chunks.pregenToBorder);
    }

    public static boolean picksUpAgain() {
        return ContentControl.flag(ContentControl.CHUNKS, "pregenResume", Config.chunks.pregenResume);
    }

    public static int wantedOnNewWorld() {
        return Math.max(0, ContentControl.number(ContentControl.CHUNKS, "pregenOnNewWorld", Config.chunks.pregenOnNewWorld));
    }

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote || world.provider.getDimension() != 0 || busy()) { return; }

        int radius = wantedOnNewWorld();
        if ((radius <= 0 && !reachesTheBorder()) || !PENDING.isEmpty()) { return; }

        PregenMemory memory = (PregenMemory) world.getWorldInfo();
        for (int dimension : chosenDimensions()) {
            if (reachesTheBorder() || memory.rdpl$landMadeTo(dimension) < radius) { PENDING.addLast(dimension); }
        }
        wantedRadius = radius;
        chaining = true;
        nextDimension(radius);
    }

    private static final class Held {
        private final GameType before;
        private int dimension;
        private double x;
        private double y;
        private double z;
        private float yaw;
        private float pitch;

        private Held(EntityPlayerMP player) {
            this.before = player.interactionManager.getGameType();
            rebase(player);
        }

        private void rebase(EntityPlayerMP player) {
            this.dimension = player.dimension;
            this.x = player.posX;
            this.y = player.posY;
            this.z = player.posZ;
            this.yaw = player.rotationYaw;
            this.pitch = player.rotationPitch;
        }

        private boolean strayed(EntityPlayerMP player) {
            double dx = player.posX - x;
            double dy = player.posY - y;
            double dz = player.posZ - z;
            return dx * dx + dy * dy + dz * dz > 1.0E-4D;
        }
    }

    private static void holdEveryone() {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }

        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { hold(player); }
    }

    private static void hold(EntityPlayerMP player) {
        if (HELD.containsKey(player.getUniqueID())) { return; }

        HELD.put(player.getUniqueID(), new Held(player));
        player.setGameType(GameType.SPECTATOR);
        flash(player);
    }

    private static void flash(EntityPlayerMP player) {
        String warning = says("pregenSpectatingSays", Config.chunks.pregenSpectatingSays);
        if (warning.isEmpty()) { return; }

        player.connection.sendPacket(new SPacketTitle(0, 15, 10));
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString(warning).setStyle(new Style().setColor(TextFormatting.RED))));
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("")));
    }

    private static void releaseEveryone(boolean welcomed) {
        if (HELD.isEmpty()) { return; }

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                Held held = HELD.get(player.getUniqueID());
                if (held == null) { continue; }

                release(player, held);
                if (welcomed) { welcome(player); }
            }
        }
        HELD.clear();
    }

    private static void welcome(EntityPlayerMP player) {
        String greeting = says("pregenWelcomeSays", Config.chunks.pregenWelcomeSays);
        if (greeting.isEmpty()) { return; }

        player.connection.sendPacket(new SPacketTitle(10, 70, 20));
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString(greeting).setStyle(new Style().setColor(TextFormatting.GREEN))));
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("")));
    }

    private static void release(EntityPlayerMP player, Held held) {
        String mode = ContentTerrain.worldGameMode();
        GameType asked = mode.isEmpty() ? GameType.NOT_SET : ContentTerrain.gameModeFrom(mode);
        player.setGameType(asked == GameType.NOT_SET ? held.before : asked);
        player.timeUntilPortal = 100;
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.CLEAR, null, -1, -1, -1));
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Held held = HELD.remove(event.player.getUniqueID());
        if (held == null || !(event.player instanceof EntityPlayerMP)) { return; }

        release((EntityPlayerMP) event.player, held);
    }

    @SubscribeEvent public static void onHoldTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (busy()) {
                BlockFalling.fallInstantly = true;
                watch();
            }

            return;
        }
        if (HELD.isEmpty()) { return; }

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }

        boolean blink = server.getTickCounter() % 30 == 0;
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            Held held = HELD.get(player.getUniqueID());
            if (held == null) { continue; }
            if (player.dimension != held.dimension) { held.rebase(player); }
            else if (held.strayed(player)) { player.connection.setPlayerLocation(held.x, held.y, held.z, held.yaw, held.pitch); }
            if (blink) { flash(player); }
        }
    }

    private static void watch() {
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

    public static void tell(String said, TextFormatting colour) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || said.isEmpty()) { return; }

        server.getPlayerList().sendMessage(new TextComponentString(said).setStyle(new Style().setColor(colour)));
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        startWhenEntered(event.player.dimension);
        ContentPregen worker = running;
        if (worker == null) { return; }
        if (event.player instanceof EntityPlayerMP) { hold((EntityPlayerMP) event.player); }

        String said = worker.sofar();
        if (said.isEmpty()) { return; }

        event.player.sendMessage(new TextComponentString(said).setStyle(new Style().setColor(TextFormatting.YELLOW)));
    }

    private static PregenMemory memory() {
        WorldServer overworld = DimensionManager.getWorld(0);
        return overworld == null ? null : (PregenMemory) overworld.getWorldInfo();
    }

    private static void nextDimension(int radius) {
        while (!PENDING.isEmpty()) {
            int dimension = PENDING.removeFirst();
            if (!DimensionManager.isDimensionRegistered(dimension)) {
                ContentLog.LOGGER.error("A pack asks for land to be made in dimension {}, which nothing here provides, so it is passed over", dimension);
                continue;
            }
            if (DimensionManager.getWorld(dimension) == null) { DimensionManager.initDimension(dimension); }

            WorldServer world = DimensionManager.getWorld(dimension);
            if (world == null) {
                ContentLog.LOGGER.error("Dimension {} would not open, so no land is made in it", dimension);
                continue;
            }
            int centreX;
            int centreZ;
            int reach;
            if (reachesTheBorder()) {
                WorldBorder border = world.getWorldBorder();
                if (border.getDiameter() >= NO_BORDER) {
                    ContentLog.LOGGER.error("A pack asks for the land of dimension {} to be made out to its border, but no border has been set there, so there is nothing to reach and none is made", dimension);
                    continue;
                }

                reach = (int) Math.ceil(border.getDiameter() / 2.0D / 16.0D);
                if (reach > Config.chunks.pregenBorderLimit) {
                    ContentLog.LOGGER.error("The border of dimension {} stands {} block(s) across, which is {} chunk(s) either way and past the {} allowed by pregenBorderLimit, so no land is made out to it", dimension, (long) border.getDiameter(), reach, Config.chunks.pregenBorderLimit);
                    continue;
                }
                centreX = (int) Math.floor(border.getCenterX()) >> 4;
                centreZ = (int) Math.floor(border.getCenterZ()) >> 4;
            }
            else {
                BlockPos spawn = world.getSpawnPoint();
                reach = radius;
                centreX = spawn.getX() >> 4;
                centreZ = spawn.getZ() >> 4;
            }
            if (alreadyMade(memory(), dimension, reach, world, centreX, centreZ)) { continue; }

            long total = start(null, dimension, centreX, centreZ, reach);
            ContentLog.LOGGER.info("Making {} chunk(s) of land in dimension {}, reaching {} chunk(s) either way from {}, {}, before anybody sets foot in it", total, dimension, reach, centreX, centreZ);
            return;
        }
    }

    private static boolean alreadyMade(PregenMemory memory, int dimension, int reach, WorldServer world, int centerX, int centerZ) {
        if (memory == null || memory.rdpl$landMadeTo(dimension) < reach) { return false; }

        File region = regionFolder(world);
        if (region == null) { return true; }

        int expected = 0;
        int missing = 0;
        for (int rx = (centerX - reach) >> 5; rx <= (centerX + reach) >> 5; rx++) {
            for (int rz = (centerZ - reach) >> 5; rz <= (centerZ + reach) >> 5; rz++) {
                expected++;
                if (!new File(region, "r." + rx + "." + rz + ".mca").isFile()) { missing++; }
            }
        }
        if (missing == 0) { return true; }

        ContentLog.LOGGER.info("Dimension {} was made before, but {} of the {} region file(s) its land lives in are missing from the disk, so it is being made again", dimension, missing, expected);
        memory.rdpl$setLandMadeTo(dimension, 0);
        memory.rdpl$setLandMadeAt(dimension, 0);
        if (missing == expected) { freshDragon(world); }

        return false;
    }

    private static void freshDragon(WorldServer world) {
        if (!(world.provider instanceof WorldProviderEnd) || !ContentEndDragon.wanted(world)) { return; }

        NBTTagCompound data = world.getWorldInfo().getDimensionData(world.provider.getDimension());
        data.removeTag("DragonFight");
        world.getWorldInfo().setDimensionData(world.provider.getDimension(), data);
        ((AccessorWorldProviderEnd) world.provider).rdpl$setDragonFightManager(new DragonFightManager(world, new NBTTagCompound()));
        ContentLog.LOGGER.info("The end's land is gone, so the dragon and its fight start over with it");
    }

    private static File regionFolder(WorldServer world) {
        if (!(world.getChunkProvider().chunkLoader instanceof AnvilChunkLoader)) { return null; }

        return new File(((AnvilChunkLoader) world.getChunkProvider().chunkLoader).chunkSaveLocation, "region");
    }

    public static boolean busy() { return running != null; }

    public static boolean lightingOnly() {
        ContentPregen worker = running;
        return worker != null && worker.lightOnly;
    }

    public static boolean holds(int chunkX, int chunkZ) {
        ContentPregen worker = running;
        return worker != null && worker.held.contains(ChunkPos.asLong(chunkX, chunkZ));
    }

    public static long start(ICommandSender asked, int dimension, int centreX, int centreZ, int radius) {
        return start(asked, dimension, centreX, centreZ, radius, false);
    }

    public static long start(ICommandSender asked, int dimension, int centreX, int centreZ, int radius, boolean lightOnly) {
        ContentPregen worker = new ContentPregen(asked, dimension, centreX, centreZ, radius, lightOnly);
        WorldServer world = DimensionManager.getWorld(dimension);
        if (world != null && picksUpAgain() && !lightOnly) {
            PregenMemory memory = memory();
            int reached = memory == null ? 0 : memory.rdpl$landMadeAt(dimension);
            if (reached > 0) {
                worker.done = worker.order.skip(reached);
                ContentLog.LOGGER.info("Picking the making of land in dimension {} up again where it left off, {} chunk(s) in", dimension, worker.done);
            }
        }
        running = worker;
        watchedDone = -1L;
        if (chainBegun == 0L) { chainBegun = System.currentTimeMillis(); }

        if (dimension != 0) { DimensionManager.keepDimensionLoaded(dimension, true); }

        holdEveryone();
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

        return worker.report();
    }

    @Override public boolean hasWork() { return !over; }

    @Override public boolean doWork() {
        if (begun == 0L) { begun = System.currentTimeMillis(); }

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
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
        Chunk chunk = lightOnly ? already(provider, next.x, next.z, true) : provider.provideChunk(next.x, next.z);
        if (chunk != null) {
            if (!chunk.isTerrainPopulated()) {
                undressed++;
                if (!lightOnly) { made++; }
            }

            retain(provider, ChunkPos.asLong(next.x, next.z));
            if (lightOnly && !chunk.isLightPopulated()) {
                fetchRing(provider, next.x, next.z);
                dressLate(provider, chunk, next.x, next.z);
            }

            brighten(provider, next.x, next.z);
        }
        roundSpent += System.nanoTime() - began;
        speak();
        int tenth = (int) (done * 10L / Math.max(1L, order.total()));
        if (tenth > toldAt) {
            toldAt = tenth;
            tell(sofar(), TextFormatting.YELLOW);
        }
        if (!order.hasNext()) {
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
        if (backlog <= 0 || !(provider.chunkLoader instanceof AnvilChunkLoader)) { return false; }

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
        if (chunk.isTerrainPopulated()) { return; }
        if (provider.getLoadedChunk(x + 1, z) == null || provider.getLoadedChunk(x, z + 1) == null || provider.getLoadedChunk(x + 1, z + 1) == null) { return; }

        ((AccessorChunk) chunk).rdpl$dress(provider.chunkGenerator);
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
        Chunk chunk = provider.getLoadedChunk((int) key, (int) (key >> 32));
        if (chunk == null) { return; }

        if (!chunk.isLightPopulated()) {
            int x = (int) key;
            int z = (int) (key >> 32);
            if (x <= lowX || x >= highX || z <= lowZ || z >= highZ) { darkAtEdge++; }
            else { dark++; }
        }
        provider.queueUnload(chunk);
    }

    private void speak() {
        long now = System.currentTimeMillis();
        if (now - spoke < 10000L) { return; }

        spoke = now;
        ContentLog.LOGGER.info(report());
        if (!picksUpAgain() || lightOnly) { return; }

        PregenMemory memory = memory();
        if (memory != null) { memory.rdpl$setLandMadeAt(dimension, (int) done); }
    }

    private static String says(String key, String fallback) {
        return ContentControl.text(ContentControl.CHUNKS, key, fallback).trim();
    }

    private String sofar() {
        String wording = lightOnly ? says("pregenRelightSays", Config.chunks.pregenRelightSays) : says("pregenRunningSays", Config.chunks.pregenRunningSays);
        if (wording.isEmpty()) { return ""; }

        try { return String.format(wording, done * 100L / Math.max(1L, order.total()), dimensionName()) + eta(); }
        catch (IllegalFormatException wrong) {
            ContentLog.LOGGER.error("A pack words the message about land being made as '{}', which is not something a number can be put into, so it is said as it stands", wording, wrong);
            return wording + eta();
        }
    }

    private String eta() {
        long total = order.total();
        if (begun == 0L || done <= 0L || done >= total) { return ""; }

        long left = (System.currentTimeMillis() - begun) * (total - done) / done / 1000L;
        return String.format(" - ETA %02d:%02d:%02d", left / 3600L, left / 60L % 60L, left % 60L);
    }

    private String dimensionName() {
        WorldServer world = DimensionManager.getWorld(dimension);
        return world == null ? String.valueOf(dimension) : world.provider.getDimensionType().getName();
    }

    private String report() {
        long seconds = Math.max(1L, (System.currentTimeMillis() - started) / 1000L);
        if (lightOnly) {
            return String.format("Went over %d of %d chunk(s) in dimension %d looking for ones the light never reached, lighting %d and leaving %d, with %d never made in the first place%s",
                    done, order.total(), dimension, brightened, dark + darkAtEdge, missing,
                    undressed == 0L ? "" : ", and " + undressed + " of them had never been dressed, " + dressedLate + " of which were dressed on the spot and the rest left alone to be dressed when somebody comes to them");
        }
        return String.format("Made %d of %d chunk(s) in dimension %d, %d of them new, at %d a second, holding %d and resting %d time(s) for the writing to catch up. Light reached %d of them, %d were left for later and %d could never be lit, having been asked for at the very edge of what was wanted, keeping within %d ms a round",
                done, order.total(), dimension, made, done / seconds, resident.size(), paused, brightened, dark, darkAtEdge, slice);
    }

    private void finish(WorldServer world) {
        if (running != this) {
            over = true;
            return;
        }

        over = true;
        running = null;
        if (dimension != 0) { DimensionManager.keepDimensionLoaded(dimension, false); }
        boolean whole = world != null && !stopping && !order.hasNext();
        if (world != null) {
            PregenMemory memory = memory();
            if (memory != null) {
                if (whole && !lightOnly && reach > memory.rdpl$landMadeTo(dimension)) { memory.rdpl$setLandMadeTo(dimension, reach); }
                if (!lightOnly) { memory.rdpl$setLandMadeAt(dimension, whole || !picksUpAgain() ? 0 : (int) done); }
            }
        }
        if (world != null) {
            ChunkProviderServer provider = world.getChunkProvider();
            for (long key : resident) { release(provider, key); }
        }
        resident.clear();
        held.clear();
        ContentLog.LOGGER.info("Finished. " + report());
        if (stopping) { PENDING.clear(); }
        if (whole && !lightOnly) {
            ContentLog.LOGGER.info("Going back over dimension {} to light what the making of it could not reach", dimension);
            start(asked, dimension, middleX, middleZ, reach, true);
            return;
        }

        if (chaining && !PENDING.isEmpty()) { nextDimension(wantedRadius); }
        else { chaining = false; }
        if (running == null) {
            BlockFalling.fallInstantly = false;
            String ending = stopping ? says("pregenStoppedSays", Config.chunks.pregenStoppedSays) : says("pregenFinishedSays", Config.chunks.pregenFinishedSays);
            if (!ending.isEmpty() && chainBegun != 0L) {
                long took = (System.currentTimeMillis() - chainBegun) / 1000L;
                ending += String.format(" - Total time %02d:%02d:%02d", took / 3600L, took / 60L % 60L, took % 60L);
            }
            chainBegun = 0L;
            tell(ending, TextFormatting.GREEN);
            releaseEveryone(!stopping);
        }
        if (asked != null && !(asked instanceof EntityPlayer)) { asked.sendMessage(new TextComponentString("Finished. " + report()).setStyle(new Style().setColor(TextFormatting.GREEN))); }
    }
}
