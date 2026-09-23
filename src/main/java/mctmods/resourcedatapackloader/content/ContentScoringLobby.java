package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.world.Travel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class ContentScoringLobby {
    private static final Map<String, double[]> STILL = new LinkedHashMap<>();
    private static final Map<String, Long> TOLD = new LinkedHashMap<>();
    static final long NOTE_TICKS = 120L;
    static final Map<String, Long> DUE = new LinkedHashMap<>();
    static final Map<String, String> SHOWN = new LinkedHashMap<>();
    private static final Map<java.util.UUID, double[]> ORIGINS = new LinkedHashMap<>();
    private static final List<java.util.UUID> GATHERED = new ArrayList<>();
    private static final double GAP = 2.0D;
    private static final double NARROWEST = 3.0D;
    private static final int REACH = 3;

    private ContentScoringLobby() {}

    static void waitsInLobby(EntityPlayerMP player) {
        ScoreDef lobby = ContentScoring.lobbyDef();
        if (lobby == null || lobby.opensLobby == null || !lobby.opensLobbyJoins) { return; }
        if (ContentScoring.holding() || ContentScoring.OUT.containsKey(player.getName())) { return; }
        if (player.world.getScoreboard().getPlayersTeam(player.getName()) != null) { return; }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        WorldServer world = server.getWorld(lobby.opensLobby[0]);
        double x = lobby.opensLobby[1] + 0.5D;
        double z = lobby.opensLobby[3] + 0.5D;
        Travel.to(player, lobby.opensLobby[0], x, standing(world, x, lobby.opensLobby[2], z), z, player.rotationYaw, 0.0F);
        ContentScoring.OUT.put(player.getName(), player.interactionManager.getGameType());
        player.setGameType(GameType.SPECTATOR);
        if (!lobby.opensJoinsSays.isEmpty()) { Says.tell(player, lobby.opensJoinsSays, TextFormatting.GRAY); }
    }

    static void keepStill(@Nullable MinecraftServer server) {
        if (server == null || !ContentScoring.holding()) {
            STILL.clear();
            return;
        }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (player.isSpectator()) { continue; }
            double[] at = STILL.get(player.getName());
            if (at == null || at[0] != player.dimension) {
                STILL.put(player.getName(), new double[] { player.dimension, player.posX, player.posY, player.posZ });
                continue;
            }
            double dx = player.posX - at[1];
            double dy = player.posY - at[2];
            double dz = player.posZ - at[3];
            if (dx * dx + dy * dy + dz * dz > 1.0E-4D) { player.connection.setPlayerLocation(at[1], at[2], at[3], player.rotationYaw, player.rotationPitch); }
        }
    }

    static boolean refused(EntityPlayer player) {
        if (player.world.isRemote || !ContentScoring.holding()) { return false; }
        ScoreDef lobby = ContentScoring.lobbyDef();
        MinecraftServer server = player.getServer();
        long now = player.world.getTotalWorldTime();
        Long last = TOLD.get(player.getName());
        if (lobby != null && server != null && player instanceof EntityPlayerMP && (last == null || now - last >= NOTE_TICKS)) { note(server, (EntityPlayerMP) player, lobby); }
        return true;
    }

    static void gather(@Nullable MinecraftServer server) {
        ScoreDef lobby = ContentScoring.lobbyDef();
        if (server == null || lobby == null || lobby.opensLobby == null) { return; }
        int dimension = lobby.opensLobby[0];
        WorldServer world = server.getWorld(dimension);
        List<Entity> waiting = new ArrayList<>();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (!player.isSpectator()) { waiting.add(player); }
        }
        for (WorldServer each : server.worlds) {
            for (Entity one : each.loadedEntityList) {
                if (one instanceof EntityPlayer || one.isDead || !(one instanceof net.minecraft.entity.EntityLiving)) { continue; }
                if (ContentScoring.sideOf(each, one, one.getCachedUniqueIdString()) != null) { waiting.add(one); }
            }
        }
        waiting.sort((a, b) -> a instanceof EntityPlayer != b instanceof EntityPlayer ? (a instanceof EntityPlayer ? -1 : 1) : a.getCachedUniqueIdString().compareTo(b.getCachedUniqueIdString()));
        List<java.util.UUID> ids = new ArrayList<>();
        for (Entity one : waiting) { ids.add(one.getUniqueID()); }
        if (ids.equals(GATHERED)) { return; }
        GATHERED.clear();
        GATHERED.addAll(ids);
        double around = 0.0D;
        for (Entity one : waiting) { around += one.width + GAP; }
        double radius = Math.max(NARROWEST, around / (2.0D * Math.PI));
        double cx = lobby.opensLobby[1] + 0.5D;
        double cz = lobby.opensLobby[3] + 0.5D;
        double along = 0.0D;
        for (Entity one : waiting) {
            double share = one.width + GAP;
            double angle = (along + share / 2.0D) / around * 2.0D * Math.PI;
            along += share;
            double x = cx + radius * Math.cos(angle);
            double z = cz + radius * Math.sin(angle);
            double y = standing(world, x, lobby.opensLobby[2], z);
            float yaw = (float) (Math.toDegrees(Math.atan2(cz - z, cx - x)) - 90.0D);
            if (!(one instanceof EntityPlayerMP)) { ORIGINS.putIfAbsent(one.getUniqueID(), new double[] { one.dimension, one.posX, one.posY, one.posZ, one.rotationYaw }); }
            Travel.to(one, dimension, x, y, z, yaw, 0.0F);
            if (one instanceof EntityPlayerMP) { STILL.put(one.getName(), new double[] { dimension, x, y, z }); }
        }
        ContentLog.LOGGER.info("{} stand around the lobby at {}, {}, {} in dimension {}, {} block(s) out", waiting.size(), lobby.opensLobby[1], lobby.opensLobby[2], lobby.opensLobby[3], dimension, Math.round(radius * 10.0D) / 10.0D);
    }

    private static double standing(World world, double x, int y, double z) {
        int bx = net.minecraft.util.math.MathHelper.floor(x);
        int bz = net.minecraft.util.math.MathHelper.floor(z);
        for (int step = 0; step <= REACH * 2; step++) {
            int at = y + (step % 2 == 0 ? step / 2 : -(step + 1) / 2);
            net.minecraft.util.math.BlockPos feet = new net.minecraft.util.math.BlockPos(bx, at, bz);
            if (world.getBlockState(feet.down()).getMaterial().blocksMovement() && !world.getBlockState(feet).getMaterial().blocksMovement() && !world.getBlockState(feet.up()).getMaterial().blocksMovement()) { return at; }
        }
        return y;
    }

    static void sendBack(MinecraftServer server) {
        for (Map.Entry<java.util.UUID, double[]> one : ORIGINS.entrySet()) {
            Entity held = server.getEntityFromUuid(one.getKey());
            if (held == null || held.isDead) { continue; }
            double[] at = one.getValue();
            Travel.to(held, (int) at[0], at[1], at[2], at[3], (float) at[4], 0.0F);
        }
        ORIGINS.clear();
        GATHERED.clear();
    }

    static void lobbyNotes(@Nullable MinecraftServer server) {
        ScoreDef lobby = ContentScoring.lobbyDef();
        if (server == null || lobby == null) { return; }
        long now = server.getWorld(0).getTotalWorldTime();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (!mctmods.resourcedatapackloader.content.worldgen.ContentPregenHold.arrived(player)) { continue; }
            Long due = DUE.get(player.getName());
            String shown = SHOWN.get(player.getName());
            boolean changed = shown != null && !shown.equals(noteFor(server, player, lobby));
            if ((due != null && now >= due) || changed) { note(server, player, lobby); }
        }
    }

    private static String noteFor(MinecraftServer server, EntityPlayerMP player, ScoreDef lobby) {
        return mctmods.resourcedatapackloader.content.ContentTeams.leadsNoSide(player) ? waitingLine(server, lobby) : lobby.opensLeaderSays;
    }

    private static void note(MinecraftServer server, EntityPlayerMP player, ScoreDef lobby) {
        String said = noteFor(server, player, lobby);
        DUE.remove(player.getName());
        SHOWN.put(player.getName(), said);
        TOLD.put(player.getName(), server.getWorld(0).getTotalWorldTime());
        mctmods.resourcedatapackloader.content.worldgen.ContentPregenHold.show(player, said, TextFormatting.GOLD);
    }

    private static String waitingLine(MinecraftServer server, ScoreDef lobby) {
        List<String> leaders = mctmods.resourcedatapackloader.content.ContentTeams.leaders(server.getWorld(0));
        return lobby.opensSays.replace("{leader}", leaders.isEmpty() ? "a leader" : String.join(", ", leaders));
    }

    static void waitingSaid(MinecraftServer server) {
        long now = server.getWorld(0).getTotalWorldTime();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { DUE.put(player.getName(), now); }
    }
}
