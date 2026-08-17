package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.interfaces.IPregenMemory;
import mctmods.resourcedatapackloader.mixin.AccessorChunk;
import mctmods.resourcedatapackloader.mixin.AccessorMinecraftServerMessage;
import mctmods.resourcedatapackloader.mixin.AccessorWorldProviderEnd;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.block.BlockFalling;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.ChatType;
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
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ContentPregen implements WorldWorkerManager.IWorker {
    private static final long STILL_AFTER_MS = 3000L;
    private static volatile ContentPregen running;
    private static volatile long stillUntil;
    private static final Deque<Integer> PENDING = new ArrayDeque<>();
    private static final Map<UUID, Held> HELD = new ConcurrentHashMap<>();
    public static final int VANILLA_SPAWN_REACH = 12;
    private static final String HELD_MODE = "rdplPregenHeldMode";
    private static ScheduledExecutorService flasher;
    private static volatile String progress = "";
    private static long watchedDone = -1L;
    private static long watchedAt;
    private static long chainBegun;
    private static int wantedRadius;
    private static boolean chaining;
    private static final Config.Chunks SHIPPED = new Config.Chunks();
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
    private volatile long done;
    private long made;
    private long undressed;
    private long dressedLate;
    private long paused;
    private long dark;
    private long darkAtEdge;
    private long missing;
    private long brightened;
    private int round = -1;
    private long roundSpent;
    private long spoke;
    private int loggedAt;
    private volatile long begun;
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

    @SubscribeEvent public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        startWhenEntered(event.toDim);
        if (running != null || !(event.player instanceof EntityPlayerMP)) { return; }
        if (greetingFor(event.toDim, false) != null) { welcome((EntityPlayerMP) event.player); }
    }

    private static boolean welcomeAtDefault() {
        String[] entries = ContentControl.list(ContentControl.CHUNKS, "welcomeSays", Config.chunks.welcomeSays);
        return entries.length == SHIPPED.welcomeSays.length && entries.length == 1 && entries[0].trim().equals(SHIPPED.welcomeSays[0]);
    }

    private static String greetingFor(int dimension, boolean fallBack) {
        String everywhere = null;
        for (String entry : ContentControl.list(ContentControl.CHUNKS, "welcomeSays", Config.chunks.welcomeSays)) {
            String[] parts = entry.split("=", 2);
            Integer named = null;
            if (parts.length == 2) {
                try { named = Integer.parseInt(parts[0].trim()); }
                catch (NumberFormatException ignored) {}
            }
            if (named == null) {
                if (everywhere == null) { everywhere = entry.trim(); }
            }
            else if (named == dimension) { return parts[1].trim(); }
        }
        return fallBack ? everywhere : null;
    }

    public static boolean reachesTheBorder() {
        return ContentControl.flag(ContentControl.CHUNKS, "pregenToBorder", Config.chunks.pregenToBorder);
    }

    public static boolean picksUpAgain() {
        return ContentControl.flag(ContentControl.CHUNKS, "pregenResume", Config.chunks.pregenResume);
    }

    public static int wantedOnNewWorld() {
        int asked = Math.max(0, ContentControl.number(ContentControl.CHUNKS, "pregenOnNewWorld", Config.chunks.pregenOnNewWorld));
        return Math.max(asked, VANILLA_SPAWN_REACH);
    }

    public static void serverStopping() {
        ContentPregen worker = running;
        if (worker != null) {
            ContentLog.LOGGER.info("The server is stopping while land is still being made in dimension {}, so the run is wound down at {} chunk(s) to be picked up on the next load", worker.dimension, worker.done);
            worker.stopping = true;
            worker.finish(DimensionManager.getWorld(worker.dimension));
            IPregenMemory memory = memory();
            if (memory != null && !worker.lightOnly) { memory.rdpl$setPregenRun(runRecord(worker.dimension, worker.middleX, worker.middleZ, worker.reach)); }
        }
        releaseEveryone(false);
        PENDING.clear();
        chaining = false;
        chainBegun = 0L;
        wantedRadius = 0;
    }

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote || world.provider.getDimension() != 0 || busy()) { return; }

        int radius = wantedOnNewWorld();
        IPregenMemory memory = (IPregenMemory) world.getWorldInfo();
        NBTTagCompound run = memory.rdpl$pregenRun();
        if (!run.isEmpty()) {
            int dimension = run.getInteger("dimension");
            if (PENDING.isEmpty() && (radius > 0 || reachesTheBorder())) {
                for (int held : chosenDimensions()) {
                    if (held != dimension && (reachesTheBorder() || memory.rdpl$landMadeTo(held) < radius)) { PENDING.addLast(held); }
                }
                wantedRadius = radius;
                chaining = !PENDING.isEmpty();
            }
            ContentLog.LOGGER.info("Land was still being made in dimension {} when the last session ended, so it is picked up again", dimension);
            start(null, dimension, run.getInteger("middleX"), run.getInteger("middleZ"), run.getInteger("reach"));
            return;
        }
        if ((radius <= 0 && !reachesTheBorder()) || !PENDING.isEmpty()) { return; }

        for (int dimension : chosenDimensions()) {
            if (reachesTheBorder() || memory.rdpl$landMadeTo(dimension) < radius) { PENDING.addLast(dimension); }
        }
        wantedRadius = radius;
        chaining = true;
        nextDimension(radius);
    }

    private static final class Held {
        private final GameType before;
        private final NetHandlerPlayServer connection;
        private final String warning;
        private int dimension;
        private double x;
        private double y;
        private double z;
        private float yaw;
        private float pitch;

        private Held(EntityPlayerMP player, GameType before) {
            this.before = before;
            this.connection = player.connection;
            this.warning = defaulted("pregenSpectatingSays", Config.chunks.pregenSpectatingSays, SHIPPED.pregenSpectatingSays, "rdpl.pregen.spectating", player);
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

        NBTTagCompound data = player.getEntityData();
        GameType before = data.hasKey(HELD_MODE) ? GameType.getByID(data.getInteger(HELD_MODE)) : player.interactionManager.getGameType();
        data.setInteger(HELD_MODE, before.getID());
        Held held = new Held(player, before);
        HELD.put(player.getUniqueID(), held);
        player.setGameType(GameType.SPECTATOR);
        flash(held);
        startFlashing();
    }

    private static void startFlashing() {
        if (flasher != null) { return; }

        beats = 0L;
        lastSaid = "";
        flasher = Executors.newSingleThreadScheduledExecutor(run -> {
            Thread beat = new Thread(run, "RDPL pregen spectator titles");
            beat.setDaemon(true);
            return beat;
        });
        flasher.scheduleAtFixedRate(ContentPregen::flashHeld, 250L, 250L, TimeUnit.MILLISECONDS);
    }

    private static void stopFlashing() {
        if (flasher == null) { return; }

        flasher.shutdown();
        flasher = null;
    }

    private static long beats;
    private static String lastSaid = "";

    private static void flashHeld() {
        ContentPregen live = running;
        String said = live == null ? progress : live.sofar();
        if (!said.isEmpty()) { progress = said; }
        boolean titles = beats % 6L == 0L;
        boolean keepAlive = beats % 8L == 0L;
        beats++;
        boolean changed = !said.isEmpty() && !said.equals(lastSaid);
        if (changed) { lastSaid = said; }
        for (Held held : HELD.values()) {
            if (titles) { flash(held); }
            if (changed || (keepAlive && !said.isEmpty())) { held.connection.sendPacket(bar(said)); }
        }
    }

    private static SPacketChat bar(String said) { return new SPacketChat(new TextComponentString(said).setStyle(new Style().setColor(TextFormatting.YELLOW)), ChatType.GAME_INFO); }

    private static void tellBar(MinecraftServer server, String said) {
        if (server == null || said.isEmpty()) { return; }

        SPacketChat packet = bar(said);
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { player.connection.sendPacket(packet); }
    }

    private static void flash(Held held) {
        if (held.warning.isEmpty()) { return; }

        held.connection.sendPacket(new SPacketTitle(0, 70, 0));
        held.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString(held.warning).setStyle(new Style().setColor(TextFormatting.RED))));
        held.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("")));
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
        stopFlashing();
    }

    private static void welcome(EntityPlayerMP player) {
        String greeting = welcomeAtDefault() ? Lang.tr(player, "rdpl.pregen.welcome") : greetingFor(player.dimension, true);
        if (greeting == null || greeting.isEmpty()) { return; }

        player.connection.sendPacket(new SPacketTitle(10, 70, 20));
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString(greeting).setStyle(new Style().setColor(TextFormatting.GREEN))));
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("")));
    }

    private static void release(EntityPlayerMP player, Held held) {
        modeBack(player, held.before);
        player.timeUntilPortal = 100;
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.CLEAR, null, -1, -1, -1));
    }

    private static void modeBack(EntityPlayerMP player, GameType before) {
        String mode = ContentTerrain.worldGameMode();
        GameType asked = mode.isEmpty() ? GameType.NOT_SET : ContentTerrain.gameModeFrom(mode);
        player.setGameType(asked == GameType.NOT_SET ? before : asked);
        player.getEntityData().removeTag(HELD_MODE);
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Held held = HELD.remove(event.player.getUniqueID());
        if (HELD.isEmpty()) { stopFlashing(); }
        if (held == null || !(event.player instanceof EntityPlayerMP)) { return; }

        release((EntityPlayerMP) event.player, held);
    }

    @SubscribeEvent public static void onTravelWhileMakingLand(EntityTravelToDimensionEvent event) {
        if (busy()) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onEnderTeleportWhileMakingLand(EnderTeleportEvent event) {
        if (busy()) { event.setCanceled(true); }
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

        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            Held held = HELD.get(player.getUniqueID());
            if (held == null) { continue; }
            if (player.dimension != held.dimension) { held.rebase(player); }
            else if (held.strayed(player)) { player.connection.setPlayerLocation(held.x, held.y, held.z, held.yaw, held.pitch); }
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
        if (worker == null) {
            if (event.player instanceof EntityPlayerMP) { welcome((EntityPlayerMP) event.player); }

            return;
        }
        if (event.player instanceof EntityPlayerMP) { hold((EntityPlayerMP) event.player); }

        String said = worker.sofar();
        if (said.isEmpty()) { return; }

        event.player.sendMessage(new TextComponentString(said).setStyle(new Style().setColor(TextFormatting.YELLOW)));
    }

    private static NBTTagCompound runRecord(int dimension, int middleX, int middleZ, int reach) {
        NBTTagCompound run = new NBTTagCompound();
        run.setInteger("dimension", dimension);
        run.setInteger("middleX", middleX);
        run.setInteger("middleZ", middleZ);
        run.setInteger("reach", reach);
        return run;
    }

    private static IPregenMemory memory() {
        WorldServer overworld = DimensionManager.getWorld(0);
        return overworld == null ? null : (IPregenMemory) overworld.getWorldInfo();
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

    private static boolean alreadyMade(IPregenMemory memory, int dimension, int reach, WorldServer world, int centerX, int centerZ) {
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

    public static boolean covers(World world, int chunkX, int chunkZ) {
        ContentPregen worker = running;
        return worker != null && world.provider.getDimension() == worker.dimension && chunkX >= worker.lowX && chunkX <= worker.highX && chunkZ >= worker.lowZ && chunkZ <= worker.highZ;
    }

    public static boolean quenches(World world, int chunkX, int chunkZ) {
        ContentPregen worker = running;
        if (worker == null || worker.lightOnly) { return false; }
        if (world.provider.getDimension() != worker.dimension) { return false; }
        if (chunkX < worker.lowX || chunkX > worker.highX || chunkZ < worker.lowZ || chunkZ > worker.highZ) { return false; }

        return !ContentLightArea.inside(world);
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
            IPregenMemory memory = memory();
            if (memory != null) { memory.rdpl$setPregenRun(runRecord(dimension, centreX, centreZ, radius)); }
        }
        if (world != null && picksUpAgain() && !lightOnly) {
            IPregenMemory memory = memory();
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
        if ((done & 63L) == 0L) { tellScreen(server); }
        progress = sofar();
        int tenth = (int) (done * 10L / Math.max(1L, order.total()));
        if (tenth > loggedAt) {
            loggedAt = tenth;
            String said = sofar();
            if (!said.isEmpty()) { ContentLog.LOGGER.info(said); }
        }
        if (!order.hasNext()) {
            tellBar(server, progress);
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

    private void tellScreen(MinecraftServer server) {
        if (server == null) { return; }

        ((AccessorMinecraftServerMessage) server).rdpl$setUserMessage("Building terrain - " + done + " / " + order.total());
    }

    private void speak() {
        long now = System.currentTimeMillis();
        if (now - spoke < 10000L) { return; }

        spoke = now;
        ContentLog.LOGGER.info(report());
        if (!picksUpAgain() || lightOnly) { return; }

        IPregenMemory memory = memory();
        if (memory != null) { memory.rdpl$setLandMadeAt(dimension, (int) done); }
    }

    private static String says(String key, String fallback) {
        return ContentControl.text(ContentControl.CHUNKS, key, fallback).trim();
    }

    private static String defaulted(String key, String fallback, String shipped, String langKey, EntityPlayerMP player) {
        String said = says(key, fallback);
        if (!said.equals(shipped.trim())) { return said; }

        return player == null ? Lang.tr(langKey) : Lang.tr(player, langKey);
    }

    private String sofar() {
        String wording = lightOnly ? defaulted("pregenRelightSays", Config.chunks.pregenRelightSays, SHIPPED.pregenRelightSays, "rdpl.pregen.relight", null) : defaulted("pregenRunningSays", Config.chunks.pregenRunningSays, SHIPPED.pregenRunningSays, "rdpl.pregen.running", null);
        if (wording.isEmpty()) { return ""; }

        long stepped = Math.min(100L, done * 100L / Math.max(1L, order.total()));
        try { return String.format(wording, order.hasNext() ? stepped : 100L, dimensionName()) + eta(); }
        catch (IllegalFormatException wrong) {
            ContentLog.LOGGER.error("A pack words the message about land being made as '{}', which is not something a number can be put into, so it is said as it stands", wording, wrong);
            return wording + eta();
        }
    }

    private String eta() {
        long total = order.total();
        if (begun == 0L || done <= 0L || done >= total) { return ""; }

        long left = (System.currentTimeMillis() - begun) * (total - done) / done / 1000L;
        return Lang.tr("rdpl.pregen.eta", left / 3600L, left / 60L % 60L, left % 60L);
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
        progress = "";
        stillUntil = System.currentTimeMillis() + STILL_AFTER_MS;
        if (dimension != 0) { DimensionManager.keepDimensionLoaded(dimension, false); }
        boolean whole = world != null && !stopping && !order.hasNext();
        if (world != null) {
            IPregenMemory memory = memory();
            if (memory != null) {
                if (whole && !lightOnly && reach > memory.rdpl$landMadeTo(dimension)) { memory.rdpl$setLandMadeTo(dimension, reach); }
                if (!lightOnly) {
                    memory.rdpl$setLandMadeAt(dimension, whole || !picksUpAgain() ? 0 : (int) done);
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
            String ending = stopping ? defaulted("pregenStoppedSays", Config.chunks.pregenStoppedSays, SHIPPED.pregenStoppedSays, "rdpl.pregen.stopped", null) : defaulted("pregenFinishedSays", Config.chunks.pregenFinishedSays, SHIPPED.pregenFinishedSays, "rdpl.pregen.done", null);
            if (!ending.isEmpty() && chainBegun != 0L) {
                long took = (System.currentTimeMillis() - chainBegun) / 1000L;
                ending += Lang.tr("rdpl.pregen.tooktime", took / 3600L, took / 60L % 60L, took % 60L);
            }
            chainBegun = 0L;
            tell(ending, TextFormatting.GREEN);
            releaseEveryone(!stopping);
        }
        if (asked != null && !(asked instanceof EntityPlayer)) { asked.sendMessage(new TextComponentString(Lang.tr("rdpl.pregen.finished", report())).setStyle(new Style().setColor(TextFormatting.GREEN))); }
    }
}
