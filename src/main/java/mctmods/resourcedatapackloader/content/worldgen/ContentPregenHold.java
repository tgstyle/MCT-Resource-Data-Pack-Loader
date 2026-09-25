package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentServer;
import mctmods.resourcedatapackloader.content.ContentWelcome;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;

public final class ContentPregenHold {
    private static final String HELD_MODE = "rdplPregenHeldMode";
    private static final Map<UUID, Held> HELD = new ConcurrentHashMap<>();
    @Nullable private static ScheduledExecutorService flasher;
    static volatile String progress = "";
    private static volatile String lastSaid = "";
    private static final AtomicLong BEATS = new AtomicLong();
    static long heldDayTime = -1L;

    private ContentPregenHold() {}

    private static final class Held {
        private final ServerPlayer player;
        private final GameType before;
        private final String warning;
        private ResourceKey<Level> dimension;
        private double x;
        private double y;
        private double z;
        private float yaw;
        private float pitch;

        private Held(ServerPlayer player, GameType before) {
            this.player = player;
            this.before = before;
            this.warning = ContentPregenProgress.defaulted("pregenSpectatingSays", Config.chunks.pregenSpectatingSays(), Config.PREGEN_SPECTATING, "rdpl.pregen.spectating", player);
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

    public static void tellBar(MinecraftServer server, String said) {
        if (said.isEmpty()) { return; }
        ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(Component.literal(said).withStyle(ChatFormatting.YELLOW));
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { player.connection.send(packet); }
    }

    static void freezeSky(MinecraftServer server) {
        ClientboundSetTimePacket still = new ClientboundSetTimePacket(server.overworld().getGameTime(), heldDayTime, false);
        for (Held held : HELD.values()) { held.player.connection.send(still); }
    }

    public static void anchored(ServerPlayer player) {
        Held held = HELD.get(player.getUUID());
        if (held != null) { held.rebase(player); }
    }

    public static void holdEveryone(MinecraftServer server, boolean fog) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { hold(player, fog); }
    }

    private static void hold(ServerPlayer player, boolean fog) {
        if (HELD.containsKey(player.getUUID())) { return; }
        CompoundTag data = player.getPersistentData();
        GameType before = data.contains(HELD_MODE) ? GameType.byId(data.getInt(HELD_MODE)) : player.gameMode.getGameModeForPlayer();
        data.putInt(HELD_MODE, before.getId());
        Held held = new Held(player, before);
        HELD.put(player.getUUID(), held);
        player.setGameMode(GameType.SPECTATOR);
        RDPLNetwork.sendHold(player, true, held.warning, fog);
        if (!RDPLNetwork.reaches(player)) { flash(player, held); }
        player.connection.send(new ClientboundSetTimePacket(player.serverLevel().getGameTime(), heldDayTime, false));
        startFlashing();
    }

    private static synchronized void startFlashing() {
        if (flasher != null) { return; }
        BEATS.set(0L);
        lastSaid = "";
        flasher = Executors.newSingleThreadScheduledExecutor(run -> {
            Thread beat = new Thread(run, "RDPL pregen spectator titles");
            beat.setDaemon(true);
            return beat;
        });
        flasher.scheduleAtFixedRate(ContentPregenHold::flashHeld, 250L, 250L, TimeUnit.MILLISECONDS);
    }

    private static synchronized void stopFlashing() {
        if (flasher == null) { return; }
        flasher.shutdown();
        flasher = null;
    }

    private static void flashHeld() {
        ContentPregen live = ContentPregen.running;
        String said = live == null ? progress : ContentPregenProgress.sofar(live);
        if (!said.isEmpty()) { progress = said; }
        long beat = BEATS.getAndIncrement();
        boolean titles = beat % 6L == 0L;
        boolean keepAlive = beat % 8L == 0L;
        boolean changed = !said.isEmpty() && !said.equals(lastSaid);
        if (changed) { lastSaid = said; }
        for (Held held : HELD.values()) {
            if (titles && !RDPLNetwork.reaches(held.player)) { flash(held.player, held); }
            if (changed || (keepAlive && !said.isEmpty())) { held.player.displayClientMessage(Component.literal(said).withStyle(ChatFormatting.YELLOW), true); }
        }
    }

    static void holdTick(MinecraftServer server) {
        if (HELD.isEmpty()) { return; }
        if (!ContentPregen.busy()) {
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
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Held held = HELD.get(player.getUUID());
            if (held == null) { continue; }
            if (!player.level().dimension().equals(held.dimension)) { held.rebase(player); }
            else if (held.strayed(player)) { player.connection.teleport(held.x, held.y, held.z, held.yaw, held.pitch); }
        }
    }

    private static void flash(ServerPlayer player, Held held) {
        if (held.warning.isEmpty()) { return; }
        Says.title(player, 0, 15, 10, "", held.warning, ChatFormatting.RED);
    }

    public static void releaseEveryone(MinecraftServer server, boolean welcomed) {
        if (HELD.isEmpty()) { return; }
        stopFlashing();
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
        if (released > 0) { ContentLog.LOGGER.info("Released {} player(s) held while the land was made, back to {}", released, ContentServer.worldGameMode().isEmpty() ? "the mode they had" : ContentServer.worldGameMode()); }
    }

    public static boolean releaseAfterIntro(ServerPlayer player) {
        if (ContentPregen.landBeingMade()) { return false; }
        Held held = HELD.remove(player.getUUID());
        if (held == null) { return false; }
        release(player, held);
        ContentWelcome.welcome(player);
        return true;
    }

    private static void release(ServerPlayer player, Held held) {
        String mode = ContentServer.worldGameMode().trim().toLowerCase(java.util.Locale.ROOT);
        GameType asked = ContentTerrain.HARDCORE.equals(mode) ? GameType.SURVIVAL : GameType.byName(mode, null);
        player.setGameMode(asked == null ? held.before : asked);
        player.getPersistentData().remove(HELD_MODE);
        player.setPortalCooldown(100);
        ServerLevel level = player.serverLevel();
        player.connection.send(new ClientboundSetTimePacket(level.getGameTime(), level.getDayTime(), level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
        RDPLNetwork.sendHold(player, false, "", false);
        player.connection.send(new ClientboundClearTitlesPacket(true));
    }

    static void forgetHeld() {
        HELD.clear();
        stopFlashing();
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        ContentPregenDimensions.startWhenEntered(player.server, player.level().dimension());
        if (ContentPregen.pendingStart != null) {
            if (ContentPregen.startTick < 0) { ContentPregen.startTick = player.server.getTickCount() + 60; }
            heldDayTime = player.server.overworld().getDayTime();
            hold(player, true);
            return;
        }
        ContentPregen worker = ContentPregen.running;
        if (worker == null) { return; }
        hold(player, true);
        String said = ContentPregenProgress.sofar(worker);
        if (!said.isEmpty()) { Says.tell(player, mctmods.resourcedatapackloader.content.card.CardIds.PREGEN_RUNNING, said, ChatFormatting.YELLOW); }
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        Held held = HELD.remove(player.getUUID());
        if (HELD.isEmpty()) { stopFlashing(); }
        if (held != null) { release(player, held); }
    }

    public static void onTravel(EntityTravelToDimensionEvent event) {
        if (ContentPregen.busy()) { event.setCanceled(true); }
    }

    public static void onTeleport(EntityTeleportEvent event) {
        if (ContentPregen.busy() && (event instanceof EntityTeleportEvent.EnderPearl || event instanceof EntityTeleportEvent.EnderEntity || event instanceof EntityTeleportEvent.ChorusFruit)) { event.setCanceled(true); }
    }

    public static boolean welcomesLater(ServerPlayer player) { return ContentPregen.busy() || HELD.containsKey(player.getUUID()); }

    public static void fallInstantly(ServerLevel level, BlockPos pos, BlockState state) {
        int floor = level.getMinBuildHeight();
        if (!FallingBlock.isFree(level.getBlockState(pos.below())) || pos.getY() < floor) { return; }
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        BlockPos below = pos.below();
        while (FallingBlock.isFree(level.getBlockState(below)) && below.getY() > floor) { below = below.below(); }
        if (below.getY() > floor) { level.setBlockAndUpdate(below.above(), state); }
    }
}
