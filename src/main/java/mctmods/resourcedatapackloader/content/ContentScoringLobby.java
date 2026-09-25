package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Scores;
import mctmods.resourcedatapackloader.util.Travel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

public final class ContentScoringLobby {
    private static final Map<String, Spot> STILL = new LinkedHashMap<>();
    private static final Map<String, Long> TOLD = new LinkedHashMap<>();
    static final Map<String, Long> DUE = new LinkedHashMap<>();
    static final Map<String, String> SHOWN = new LinkedHashMap<>();
    private static final Map<UUID, Spot> ORIGINS = new LinkedHashMap<>();
    private static final List<UUID> GATHERED = new ArrayList<>();
    static final long NOTE_TICKS = 120L;
    private static final double GAP = 2.0D;
    private static final double NARROWEST = 3.0D;
    private static final int REACH = 3;

    private ContentScoringLobby() {}

    static void waitsInLobby(MinecraftServer server, ServerPlayer player) {
        ScoreDef lobby = ContentScoring.lobbyDef();
        if (lobby == null || lobby.opensLobby() == null || !lobby.opensLobbyJoins()) { return; }
        String name = player.getGameProfile().getName();
        if (ContentScoring.holding() || ContentScoring.OUT.containsKey(name) || Scores.teamOf(Scores.board(server), name) != null) { return; }
        ServerLevel level = lobbyLevel(server, lobby);
        if (level == null) { return; }
        double x = lobby.opensLobby().x() + 0.5D;
        double z = lobby.opensLobby().z() + 0.5D;
        Travel.to(player, level, x, standing(level, x, lobby.opensLobby().y(), z), z, player.getYRot(), 0.0F);
        ContentScoring.OUT.put(name, player.gameMode.getGameModeForPlayer());
        player.setGameMode(GameType.SPECTATOR);
        if (!lobby.opensJoinsSays().isEmpty()) { Says.tell(player, mctmods.resourcedatapackloader.content.card.CardIds.LOBBY_JOINS, lobby.opensJoinsSays(), ChatFormatting.GRAY); }
    }

    @Nullable private static ServerLevel lobbyLevel(MinecraftServer server, ScoreDef lobby) {
        ServerLevel level = lobby.opensLobby() == null ? null : server.getLevel(lobby.opensLobby().dimension());
        if (level == null && lobby.opensLobby() != null) { ContentLog.LOGGER.error("The lobby of {} stands in {}, which is not loaded, so nobody is gathered", lobby.name(), lobby.opensLobby().dimension().location()); }
        return level;
    }

    static void keepStill(MinecraftServer server) {
        if (!ContentScoring.holding()) {
            STILL.clear();
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.isSpectator()) { continue; }
            String name = player.getGameProfile().getName();
            Spot at = STILL.get(name);
            if (at == null || !at.dimension().equals(player.level().dimension())) {
                STILL.put(name, new Spot(player.level().dimension(), player.getX(), player.getY(), player.getZ(), player.getYRot()));
                continue;
            }
            if (player.distanceToSqr(at.x(), at.y(), at.z()) > 1.0E-4D) { player.connection.teleport(at.x(), at.y(), at.z(), player.getYRot(), player.getXRot()); }
        }
    }

    static boolean refused(Player player) {
        if (!(player instanceof ServerPlayer held) || !ContentScoring.holding()) { return false; }
        ScoreDef lobby = ContentScoring.lobbyDef();
        long now = held.serverLevel().getGameTime();
        Long last = TOLD.get(held.getGameProfile().getName());
        if (lobby != null && (last == null || now - last >= NOTE_TICKS)) { note(held.server, held, lobby); }
        return true;
    }

    static void gather(MinecraftServer server) {
        ScoreDef lobby = ContentScoring.lobbyDef();
        if (lobby == null || lobby.opensLobby() == null) { return; }
        ServerLevel level = lobbyLevel(server, lobby);
        if (level == null) { return; }
        List<Entity> waiting = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!player.isSpectator()) { waiting.add(player); }
        }
        for (ServerLevel each : server.getAllLevels()) {
            for (Entity one : each.getAllEntities()) {
                if (one instanceof Player || !one.isAlive() || !(one instanceof Mob)) { continue; }
                if (ContentScoring.sideOf(each, one) != null) { waiting.add(one); }
            }
        }
        waiting.sort((a, b) -> a instanceof Player != b instanceof Player ? (a instanceof Player ? -1 : 1) : a.getStringUUID().compareTo(b.getStringUUID()));
        List<UUID> ids = new ArrayList<>();
        for (Entity one : waiting) { ids.add(one.getUUID()); }
        if (ids.equals(GATHERED)) { return; }
        GATHERED.clear();
        GATHERED.addAll(ids);
        double around = 0.0D;
        for (Entity one : waiting) { around += one.getBbWidth() + GAP; }
        double radius = Math.max(NARROWEST, around / (2.0D * Math.PI));
        ScoreDef.Place place = lobby.opensLobby();
        double cx = place.x() + 0.5D;
        double cz = place.z() + 0.5D;
        double along = 0.0D;
        for (Entity one : waiting) {
            double share = one.getBbWidth() + GAP;
            double angle = (along + share / 2.0D) / around * 2.0D * Math.PI;
            along += share;
            double x = cx + radius * Math.cos(angle);
            double z = cz + radius * Math.sin(angle);
            double y = standing(level, x, place.y(), z);
            float yaw = (float) (Math.toDegrees(Math.atan2(cz - z, cx - x)) - 90.0D);
            if (!(one instanceof ServerPlayer)) { ORIGINS.putIfAbsent(one.getUUID(), new Spot(one.level().dimension(), one.getX(), one.getY(), one.getZ(), one.getYRot())); }
            Travel.to(one, level, x, y, z, yaw, 0.0F);
            if (one instanceof ServerPlayer player) { STILL.put(player.getGameProfile().getName(), new Spot(level.dimension(), x, y, z, yaw)); }
        }
        ContentLog.LOGGER.info("{} stand around the lobby at {}, {}, {} in {}, {} block(s) out", waiting.size(), place.x(), place.y(), place.z(), place.dimension().location(), Math.round(radius * 10.0D) / 10.0D);
    }

    private static double standing(Level level, double x, int y, double z) {
        int bx = Mth.floor(x);
        int bz = Mth.floor(z);
        for (int step = 0; step <= REACH * 2; step++) {
            int at = y + (step % 2 == 0 ? step / 2 : -(step + 1) / 2);
            BlockPos feet = new BlockPos(bx, at, bz);
            if (solid(level, feet.below()) && !solid(level, feet) && !solid(level, feet.above())) { return at; }
        }
        return y;
    }

    private static boolean solid(Level level, BlockPos pos) { return !level.getBlockState(pos).getCollisionShape(level, pos).isEmpty(); }

    static void sendBack(MinecraftServer server) {
        for (Map.Entry<UUID, Spot> one : ORIGINS.entrySet()) {
            Spot at = one.getValue();
            ServerLevel level = server.getLevel(at.dimension());
            Entity held = null;
            for (ServerLevel each : server.getAllLevels()) {
                held = each.getEntity(one.getKey());
                if (held != null) { break; }
            }
            if (held == null || !held.isAlive() || level == null) { continue; }
            Travel.to(held, level, at.x(), at.y(), at.z(), at.yaw(), 0.0F);
        }
        ORIGINS.clear();
        GATHERED.clear();
    }

    static void lobbyNotes(MinecraftServer server) {
        ScoreDef lobby = ContentScoring.lobbyDef();
        if (lobby == null) { return; }
        long now = server.overworld().getGameTime();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!ContentWelcome.arrived(player)) { continue; }
            String name = player.getGameProfile().getName();
            Long due = DUE.get(name);
            String shown = SHOWN.get(name);
            boolean changed = shown != null && !shown.equals(noteFor(server, player, lobby));
            if (due != null && now >= due || changed) { note(server, player, lobby); }
        }
    }

    private static String noteFor(MinecraftServer server, ServerPlayer player, ScoreDef lobby) { return ContentTeams.leadsNoSide(player) ? waitingLine(server, lobby) : lobby.opensLeaderSays(); }

    private static void note(MinecraftServer server, ServerPlayer player, ScoreDef lobby) {
        String said = noteFor(server, player, lobby);
        String name = player.getGameProfile().getName();
        DUE.remove(name);
        SHOWN.put(name, said);
        TOLD.put(name, server.overworld().getGameTime());
        if (mctmods.resourcedatapackloader.content.card.CardRules.unset(mctmods.resourcedatapackloader.content.card.CardIds.LOBBY_NOTE)) { ContentWelcome.show(player, said, ChatFormatting.GOLD); }
        else { mctmods.resourcedatapackloader.content.card.CardFire.builtin(mctmods.resourcedatapackloader.content.card.CardIds.LOBBY_NOTE, player, mctmods.resourcedatapackloader.content.card.CardLook.says(said, ChatFormatting.GOLD, mctmods.resourcedatapackloader.content.card.CardLook.CENTER)); }
    }

    private static String waitingLine(MinecraftServer server, ScoreDef lobby) {
        List<String> leaders = ContentTeams.leaders(server.overworld());
        return lobby.opensSays().replace("{leader}", leaders.isEmpty() ? "a leader" : String.join(", ", leaders));
    }

    static void waitingSaid(MinecraftServer server) {
        long now = server.overworld().getGameTime();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { DUE.put(player.getGameProfile().getName(), now); }
    }

    private record Spot(ResourceKey<Level> dimension, double x, double y, double z, float yaw) {}
}
