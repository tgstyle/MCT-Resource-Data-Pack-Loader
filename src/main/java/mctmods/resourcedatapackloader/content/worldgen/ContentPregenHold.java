package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.block.BlockFalling;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ContentPregenHold {
    private static final Map<UUID, Held> HELD = new ConcurrentHashMap<>();
    private static final String HELD_MODE = "rdplPregenHeldMode";
    private static ScheduledExecutorService flasher;
    static volatile String progress = "";

    private static boolean online(MinecraftServer server, UUID id) {
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (player.getUniqueID().equals(id)) { return true; }
        }
        return false;
    }

    private static boolean welcomeAtDefault() {
        String[] entries = ContentControl.list(ContentControl.CHUNKS, "welcomeSays", Config.chunks.welcomeSays);
        return entries.length == ContentPregenProgress.SHIPPED.welcomeSays.length && entries.length == 1 && entries[0].trim().equals(ContentPregenProgress.SHIPPED.welcomeSays[0]);
    }

    static String greetingFor(int dimension, boolean fallBack) {
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
            this.warning = ContentPregenProgress.defaulted("pregenSpectatingSays", Config.chunks.pregenSpectatingSays, ContentPregenProgress.SHIPPED.pregenSpectatingSays, "rdpl.pregen.spectating", player);
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

    public static void holdEveryone(boolean fog) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { hold(player, fog); }
    }

    private static void hold(EntityPlayerMP player, boolean fog) {
        if (HELD.containsKey(player.getUniqueID())) { return; }
        NBTTagCompound data = player.getEntityData();
        GameType before = data.hasKey(HELD_MODE) ? GameType.getByID(data.getInteger(HELD_MODE)) : player.interactionManager.getGameType();
        data.setInteger(HELD_MODE, before.getID());
        Held held = new Held(player, before);
        HELD.put(player.getUniqueID(), held);
        player.setGameType(GameType.SPECTATOR);
        RDPLNetwork.sendHold(player, true, held.warning, fog);
        if (RDPLNetwork.vanilla(player)) { flash(held); }
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
        flasher.scheduleAtFixedRate(ContentPregenHold::flashHeld, 250L, 250L, TimeUnit.MILLISECONDS);
    }

    private static void stopFlashing() {
        if (flasher == null) { return; }
        flasher.shutdown();
        flasher = null;
    }

    private static long beats;
    private static String lastSaid = "";

    private static void flashHeld() {
        ContentPregen live = ContentPregen.running;
        String said = live == null ? progress : ContentPregenProgress.sofar(live);
        if (!said.isEmpty()) { progress = said; }
        boolean titles = beats % 6L == 0L;
        boolean keepAlive = beats % 8L == 0L;
        beats++;
        boolean changed = !said.isEmpty() && !said.equals(lastSaid);
        if (changed) { lastSaid = said; }
        for (Held held : HELD.values()) {
            if (titles && RDPLNetwork.vanilla(held.connection)) { flash(held); }
            if (changed || (keepAlive && !said.isEmpty())) { held.connection.sendPacket(bar(said)); }
        }
    }

    private static SPacketChat bar(String said) { return new SPacketChat(new TextComponentString(said).setStyle(new Style().setColor(TextFormatting.YELLOW)), ChatType.GAME_INFO); }

    public static void tellBar(MinecraftServer server, String said) {
        if (server == null || said.isEmpty()) { return; }
        SPacketChat packet = bar(said);
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { player.connection.sendPacket(packet); }
    }

    private static void flash(Held held) {
        if (held.warning.isEmpty()) { return; }
        held.connection.sendPacket(new SPacketTitle(0, 15, 10));
        held.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString(held.warning).setStyle(new Style().setColor(TextFormatting.RED))));
        held.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("")));
    }

    public static void releaseEveryone(boolean welcomed) {
        if (HELD.isEmpty()) { return; }
        stopFlashing();
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) {
            HELD.clear();
            return;
        }
        int released = 0;
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            Held held = HELD.get(player.getUniqueID());
            if (held == null) { continue; }
            if (ContentIntroPlay.reading(player.getUniqueID())) { continue; }
            HELD.remove(player.getUniqueID());
            release(player, held);
            if (welcomed) { welcome(player); }
            released++;
        }
        HELD.keySet().removeIf(id -> !online(server, id));
        if (released > 0) { ContentLog.LOGGER.info("Released {} player(s) held while the land was made, back to {}", released, ContentTerrain.worldGameMode().isEmpty() ? "the mode they had" : ContentTerrain.worldGameMode()); }
    }

    public static void releaseAfterIntro(EntityPlayerMP player) {
        if (ContentPregen.busy()) { return; }
        Held held = HELD.remove(player.getUniqueID());
        if (held != null) { release(player, held); }
        welcome(player);
    }

    private static final Set<UUID> ARRIVED = new HashSet<>();

    public static boolean arrived(EntityPlayerMP player) { return ARRIVED.contains(player.getUniqueID()); }

    static void welcome(EntityPlayerMP player) {
        greet(player);
        if (!ARRIVED.add(player.getUniqueID())) { return; }
        mctmods.resourcedatapackloader.content.ContentScoring.greet(player);
        mctmods.resourcedatapackloader.content.ContentTeams.greet(player);
    }

    private static void greet(EntityPlayerMP player) {
        String greeting = welcomeAtDefault() ? Lang.tr(player, "rdpl.pregen.welcome") : greetingFor(player.dimension, true);
        if (greeting == null || greeting.isEmpty()) { return; }
        if (Says.card()) {
            Says.tell(player, greeting, TextFormatting.GREEN);
            return;
        }
        show(player, greeting, TextFormatting.GREEN);
    }

    public static void show(EntityPlayerMP player, String said, TextFormatting color) {
        if (said.isEmpty() || RDPLNetwork.sendNote(player, said)) { return; }
        player.connection.sendPacket(new SPacketTitle(10, 70, 20));
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.SUBTITLE, new TextComponentString(said).setStyle(new Style().setColor(color))));
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("")));
    }

    private static void release(EntityPlayerMP player, Held held) {
        modeBack(player, held.before);
        player.timeUntilPortal = 100;
        RDPLNetwork.sendHold(player, false, "", false);
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.CLEAR, null, -1, -1, -1));
    }

    private static void modeBack(EntityPlayerMP player, GameType before) {
        String mode = ContentTerrain.worldGameMode();
        GameType asked = mode.isEmpty() ? GameType.NOT_SET : ContentTerrain.gameModeFrom(mode);
        player.setGameType(asked == GameType.NOT_SET ? before : asked);
        player.getEntityData().removeTag(HELD_MODE);
    }

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ARRIVED.remove(event.player.getUniqueID());
        Held held = HELD.remove(event.player.getUniqueID());
        if (HELD.isEmpty()) { stopFlashing(); }
        if (held == null || !(event.player instanceof EntityPlayerMP)) { return; }
        release((EntityPlayerMP) event.player, held);
    }

    @SubscribeEvent public static void onTravelWhileMakingLand(EntityTravelToDimensionEvent event) {
        if (ContentPregen.busy()) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onEnderTeleportWhileMakingLand(EnderTeleportEvent event) {
        if (ContentPregen.busy()) { event.setCanceled(true); }
    }

    @SubscribeEvent public static void onHoldTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (ContentPregen.busy()) {
                BlockFalling.fallInstantly = true;
                ContentPregen.watch();
            }
            return;
        }
        if (HELD.isEmpty()) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        if (!ContentPregen.busy()) {
            int stranded = 0;
            for (UUID id : HELD.keySet()) { if (!ContentIntroPlay.reading(id) && online(server, id)) { stranded++; } }
            if (stranded > 0) {
                ContentLog.LOGGER.warn("{} player(s) were still held as spectators with no land being made, so they are released now; the release at the end of the run was missed", stranded);
                releaseEveryone(true);
                return;
            }
        }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            Held held = HELD.get(player.getUniqueID());
            if (held == null) { continue; }
            if (player.dimension != held.dimension) { held.rebase(player); }
            else if (held.strayed(player)) { player.connection.setPlayerLocation(held.x, held.y, held.z, held.yaw, held.pitch); }
        }
    }

    public static void tell(String said, TextFormatting color) { Says.tellAll(said, color); }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ContentPregenDimensions.startWhenEntered(event.player.dimension);
        ContentPregen worker = ContentPregen.running;
        if (worker == null) {
            if (event.player instanceof EntityPlayerMP && !ContentIntroPlay.willPlay((EntityPlayerMP) event.player)) { welcome((EntityPlayerMP) event.player); }
            return;
        }
        if (event.player instanceof EntityPlayerMP) { hold((EntityPlayerMP) event.player, true); }
        String said = ContentPregenProgress.sofar(worker);
        if (said.isEmpty()) { return; }
        if (event.player instanceof EntityPlayerMP) { Says.tell((EntityPlayerMP) event.player, said, TextFormatting.YELLOW); }
    }
}
