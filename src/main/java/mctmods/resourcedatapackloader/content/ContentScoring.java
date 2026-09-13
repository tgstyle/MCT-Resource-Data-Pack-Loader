package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentReset;
import mctmods.resourcedatapackloader.network.MessageCard;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Scores;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.Travel;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentScoring {
    private static final Map<String, ScoreDef> BY_NAME = new LinkedHashMap<>();
    private static final Set<String> FINISHED = new LinkedHashSet<>();
    private static final Map<String, Integer> KILLS = new LinkedHashMap<>();
    private static final Map<String, Integer> DEATHS = new LinkedHashMap<>();
    private static final Map<String, Integer> OWN = new LinkedHashMap<>();
    private static final Map<UUID, Integer> SETTLING = new LinkedHashMap<>();
    private static final Map<String, GameType> OUT = new LinkedHashMap<>();
    private static final Set<String> IN_PLAY = new LinkedHashSet<>();
    private static final Map<String, Spot> STILL = new LinkedHashMap<>();
    private static final Map<String, Long> TOLD = new LinkedHashMap<>();
    private static final Map<String, Long> DUE = new LinkedHashMap<>();
    private static final Map<String, String> SHOWN = new LinkedHashMap<>();
    private static final Map<UUID, Spot> ORIGINS = new LinkedHashMap<>();
    private static final List<UUID> GATHERED = new ArrayList<>();
    private static final int OPENS_IN = 5;
    private static final int SETTLE_TICKS = 3;
    private static final int MINUTE_TICKS = 20 * 60;
    private static final long NOTE_TICKS = 120L;
    private static final double GAP = 2.0D;
    private static final double NARROWEST = 3.0D;
    private static final int REACH = 3;
    private static final String PLAYER = "minecraft:player";
    private static final String RESET = "reset";
    private static final String LAST_STANDING = "last standing";
    private static int starting;
    @Nullable private static ScoreDef opening;
    private static boolean live;
    private static int ticks;
    private static int beat;
    private static int waiting;
    @Nullable private static ScoreDef resetting;
    private static boolean closed;
    private static boolean lobbied;

    private ContentScoring() {}

    public static boolean load() {
        BY_NAME.clear();
        ContentRoundReset.clear();
        PackManager.get().forEach(PackManager.SCORING, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(namespace, path);
            ScoreDef def = ContentParser.scoreFile(key, contents);
            if (def == null) { return; }
            if (BY_NAME.containsKey(def.name())) {
                ContentLog.LOGGER.error("Score file {} names the objective '{}', which another pack already named, so the later one is left out", key, def.name());
                return;
            }
            BY_NAME.put(def.name(), def);
        });
        if (!BY_NAME.isEmpty()) { Summary.info("scoring", "Keeping " + BY_NAME.size() + " score(s): " + BY_NAME.keySet()); }
        lobbied = lobbyDef() != null;
        return !BY_NAME.isEmpty();
    }

    public static Map<String, ScoreDef> all() { return BY_NAME; }

    public static boolean any() { return !BY_NAME.isEmpty(); }

    @Nullable public static ScoreDef named(String name) { return BY_NAME.get(name); }

    public static boolean roundRunning() {
        if (closed) { return false; }
        for (ScoreDef def : BY_NAME.values()) {
            if (def.ends() && def.endsLocksTeams() && !FINISHED.contains(def.name())) { return true; }
        }
        return false;
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) { keep(level); }
    }

    public static void onDeath(LivingDeathEvent event) {
        if (BY_NAME.isEmpty()) { return; }
        Entity died = event.getEntity();
        if (!(died.level() instanceof ServerLevel level)) { return; }
        String killed = kindOf(died);
        Entity killer = event.getSource().getEntity();
        String killerSide = killer == null ? null : sideOf(level, killer);
        String diedSide = sideOf(level, died);
        boolean own = killerSide != null && killerSide.equals(diedSide);
        boolean scored = false;
        for (ScoreDef def : BY_NAME.values()) {
            if (!def.fed()) { continue; }
            Integer worth = def.killPoints().get(killed);
            if (worth != null && killer != null) {
                scored = true;
                int points = own ? def.ownKillPoints() : worth;
                if (points != 0) { credit(level, def, killer, points); }
            }
            if (def.deathPoints() != 0) { credit(level, def, died, def.deathPoints()); }
        }
        if (!scored) { return; }
        String kind = kindOf(killer);
        KILLS.merge(kind, 1, Integer::sum);
        DEATHS.merge(killed, 1, Integer::sum);
        if (own) { OWN.merge(kind, 1, Integer::sum); }
    }

    private static String kindOf(Entity entity) { return entity instanceof Player ? PLAYER : EntityType.getKey(entity.getType()).toString(); }

    private static String memberOf(Entity entity) { return entity instanceof Player player ? player.getGameProfile().getName() : entity.getStringUUID(); }

    private static void credit(ServerLevel level, ScoreDef def, Entity who, int points) {
        if (closed || def.ends() && FINISHED.contains(def.name())) { return; }
        Scoreboard board = Scores.board(level.getServer());
        Objective objective = Scores.objective(board, def.name());
        if (objective == null) { return; }
        if (def.individuals()) { bump(board, objective, memberOf(who), points); }
        if (!def.teamTotals()) { return; }
        String side = sideOf(level, who);
        if (side == null) { return; }
        bump(board, objective, side, points);
        watch(level.getServer(), def, Scores.score(board, side, objective));
    }

    @Nullable private static String sideOf(ServerLevel level, Entity who) {
        PlayerTeam team = Scores.teamOf(Scores.board(level.getServer()), memberOf(who));
        if (team != null) { return team.getName(); }
        if (who instanceof Player) { return null; }
        List<TeamDef> claiming = ContentTeams.claiming(kindOf(who));
        return claiming.isEmpty() ? null : claiming.getFirst().name();
    }

    private static void bump(Scoreboard board, Objective objective, String row, int points) { Scores.set(board, row, objective, Scores.score(board, row, objective) + points); }

    public static void onServerTick(ServerTickEvent.Post event) { tick(event.getServer()); }

    private static void tick(MinecraftServer server) {
        if (BY_NAME.isEmpty() || !live) { return; }
        keepStill(server);
        if (!closed && starting == 0 && waiting == 0 && !ContentPregen.busy()) { ticks++; }
        if (++beat % 20 != 0) { return; }
        settling(server);
        ContentRoundReset.second(server);
        if (closed && lobbied) {
            gather(server);
            lobbyNotes(server);
        }
        if (starting > 0) {
            if (--starting > 0) { count(server); }
            else { open(server); }
            return;
        }
        if (waiting > 0 && resetting != null) {
            if (--waiting == 0) {
                opening = resetting;
                resetting = null;
                ContentReset.run(server);
                return;
            }
            if (!resetting.intermissionSays().isEmpty()) { ContentPregen.tellBar(server, resetting.intermissionSays().replace("{seconds}", Integer.toString(waiting))); }
        }
        if (closed) { return; }
        for (ScoreDef def : BY_NAME.values()) {
            if (def.endsAfterMinutes() <= 0 || FINISHED.contains(def.name())) { continue; }
            if (ticks >= def.endsAfterMinutes() * MINUTE_TICKS) { finish(server, def, "time", null); }
        }
        if (starting == 0) { standing(server); }
    }

    private static void watch(MinecraftServer server, ScoreDef def, int standing) {
        if (def.endsAtScore() <= 0 || standing < def.endsAtScore() || FINISHED.contains(def.name())) { return; }
        finish(server, def, "score", null);
    }

    private static void finish(MinecraftServer server, ScoreDef def, String why, @Nullable String winner) {
        if (!FINISHED.add(def.name())) { return; }
        List<String> lines = standings(server, def);
        if (def.endsLastStanding()) { backIn(server); }
        if (winner != null) {
            TeamDef side = ContentTeams.named(winner);
            lines.addFirst((side == null ? winner : side.displayName()) + " stood last");
        }
        else if (LAST_STANDING.equals(why)) { lines.addFirst("No side was left standing"); }
        else if (RESET.equals(why)) { lines.addFirst("The round was reset"); }
        ContentLog.LOGGER.info("The {} round is over on {}: {}", def.displayName(), why, lines);
        ContentLog.LOGGER.info("Kills this round by the killer's kind: {}; deaths by kind: {}; kills of their own side: {}", KILLS, DEATHS, OWN);
        if (RESET.equals(why)) { ContentLog.LOGGER.info("A reset round is awarded to nobody"); }
        else { award(server, def, winner); }
        if (def.endsResets()) {
            resetting = def;
            waiting = Math.max(1, def.endsIntermission());
            ContentLog.LOGGER.info("The map is reset in {} second(s), so the scores can be read first", def.endsIntermission());
        }
        if (!def.resultsCard()) {
            Says.tellAll(server, def.displayName() + " is over", ChatFormatting.GOLD);
            for (String line : lines) { Says.tellAll(server, line, ChatFormatting.YELLOW); }
            return;
        }
        ItemStack icon = def.resultsIcon().isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(ResourceLocation.fromNamespaceAndPath("resourcedatapackloader", "results"), def.resultsIcon(), 1);
        MessageCard card = new MessageCard(def.resultsTitle(), lines, icon, def.resultsImage(), def.resultsBackground(), 0xFFFFFF, def.resultsTicks());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (RDPLNetwork.reaches(player)) {
                RDPLNetwork.sendCard(player, card);
                continue;
            }
            Says.tell(player, def.resultsTitle(), ChatFormatting.GOLD);
            for (String line : lines) { Says.tell(player, line, ChatFormatting.YELLOW); }
        }
    }

    private static void award(MinecraftServer server, ScoreDef def, @Nullable String winner) {
        if (def.awardsTo().isEmpty()) { return; }
        Scoreboard board = Scores.board(server);
        Objective round = Scores.objective(board, def.name());
        Objective match = Scores.objective(board, def.awardsTo());
        if (round == null || match == null) {
            ContentLog.LOGGER.error("The round {} awards to {}, which is not an objective this pack keeps, so no round win is recorded", def.name(), def.awardsTo());
            return;
        }
        String best = winner;
        if (best == null) {
            Scores.Row top = null;
            boolean tied = false;
            for (Scores.Row one : Scores.rows(board, round)) {
                if (top == null || one.value() > top.value()) {
                    top = one;
                    tied = false;
                }
                else if (one.value() == top.value()) { tied = true; }
            }
            best = top == null || tied ? null : top.owner();
        }
        if (best == null) {
            ContentLog.LOGGER.info("The round ended level, so no round win is recorded");
            return;
        }
        int held = Scores.score(board, best, match) + 1;
        Scores.set(board, best, match, held);
        ContentLog.LOGGER.info("{} took the round, and now holds {} in {}", best, held, def.awardsTo());
        ScoreDef whole = BY_NAME.get(def.awardsTo());
        if (whole == null || FINISHED.contains(whole.name())) { return; }
        int played = 0;
        for (Scores.Row one : Scores.rows(board, match)) { played += one.value(); }
        if (whole.endsAfterRounds() > 0 && played >= whole.endsAfterRounds()) { finish(server, whole, "rounds", null); }
        else { watch(server, whole, held); }
    }

    public static void starting(MinecraftServer server) {
        ScoreDef lobby = lobbyDef();
        if (lobby != null) {
            closed = true;
            opening = lobby;
            waitingSaid(server);
            return;
        }
        starting = OPENS_IN;
        count(server);
    }

    @Nullable public static ScoreDef resetOffer() {
        for (ScoreDef def : BY_NAME.values()) {
            if (def.reset().offered()) { return def; }
        }
        return null;
    }

    public static boolean noRoundToReset() { return closed || starting > 0 || resetting != null; }

    public static void resetRound(MinecraftServer server, ScoreDef offered) {
        for (ScoreDef def : new ArrayList<>(BY_NAME.values())) {
            if (!def.carries() && (def.ends() || def == offered)) { finish(server, def, RESET, null); }
        }
        resetting = offered;
        waiting = Math.max(1, offered.endsIntermission());
        ContentLog.LOGGER.info("The round was reset, so the map is reset in {} second(s)", waiting);
    }

    @Nullable private static ScoreDef lobbyDef() {
        for (ScoreDef def : BY_NAME.values()) {
            if (ScoreDef.LEADER.equals(def.opensBy())) { return def; }
        }
        return null;
    }

    public static boolean eliminating() {
        if (closed || starting > 0) { return false; }
        for (ScoreDef def : BY_NAME.values()) {
            if (def.endsLastStanding() && !FINISHED.contains(def.name())) { return true; }
        }
        return false;
    }

    private static void standing(MinecraftServer server) {
        if (!eliminating()) { return; }
        Map<String, Integer> sides = new LinkedHashMap<>();
        ServerLevel overworld = server.overworld();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (OUT.containsKey(player.getGameProfile().getName()) || player.isSpectator()) { continue; }
            String side = sideOf(overworld, player);
            if (side != null) { sides.merge(side, 1, Integer::sum); }
        }
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity one : level.getAllEntities()) {
                if (one instanceof Player || !one.isAlive() || !(one instanceof LivingEntity living) || living.getHealth() <= 0.0F) { continue; }
                String side = sideOf(level, one);
                if (side != null) { sides.merge(side, 1, Integer::sum); }
            }
        }
        if (IN_PLAY.isEmpty()) {
            IN_PLAY.addAll(sides.keySet());
            if (IN_PLAY.size() < 2) { ContentLog.LOGGER.info("Only {} side(s) are in play, so the round cannot end on the last side standing", IN_PLAY.size()); }
            else { ContentLog.LOGGER.info("The sides in play this round are {}", IN_PLAY); }
            return;
        }
        if (IN_PLAY.size() < 2) { return; }
        List<String> left = new ArrayList<>();
        for (String side : IN_PLAY) {
            if (sides.containsKey(side)) { left.add(side); }
        }
        if (left.size() > 1) { return; }
        String winner = left.isEmpty() ? null : left.getFirst();
        for (ScoreDef def : new ArrayList<>(BY_NAME.values())) {
            if (def.endsLastStanding() && !FINISHED.contains(def.name())) { finish(server, def, LAST_STANDING, winner); }
        }
    }

    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.isCanceled() || !eliminating()) { return; }
        String name = player.getGameProfile().getName();
        if (sideOf(player.serverLevel(), player) == null || OUT.containsKey(name)) { return; }
        OUT.put(name, player.gameMode.getGameModeForPlayer());
        ContentLog.LOGGER.info("{} is out of the round", name);
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !OUT.containsKey(player.getGameProfile().getName())) { return; }
        if (!eliminating()) {
            player.setGameMode(OUT.remove(player.getGameProfile().getName()));
            return;
        }
        player.setGameMode(GameType.SPECTATOR);
        String said = outSays();
        if (!said.isEmpty()) { Says.tell(player, said, ChatFormatting.GRAY); }
    }

    public static void onBack(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) { return; }
        String name = player.getGameProfile().getName();
        if (!eliminating() && OUT.containsKey(name)) { player.setGameMode(OUT.remove(name)); }
        SETTLING.put(player.getUUID(), SETTLE_TICKS);
    }

    private static void settling(MinecraftServer server) {
        if (SETTLING.isEmpty()) { return; }
        Set<UUID> online = new LinkedHashSet<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            online.add(player.getUUID());
            Integer left = SETTLING.get(player.getUUID());
            if (left == null) { continue; }
            if (left > 1) {
                SETTLING.put(player.getUUID(), left - 1);
                continue;
            }
            SETTLING.remove(player.getUUID());
            ContentReset.arrived(player);
            waitsInLobby(server, player);
        }
        SETTLING.keySet().retainAll(online);
    }

    private static void waitsInLobby(MinecraftServer server, ServerPlayer player) {
        ScoreDef lobby = lobbyDef();
        if (lobby == null || lobby.opensLobby() == null || !lobby.opensLobbyJoins()) { return; }
        String name = player.getGameProfile().getName();
        if (holding() || OUT.containsKey(name) || Scores.teamOf(Scores.board(server), name) != null) { return; }
        ServerLevel level = lobbyLevel(server, lobby);
        if (level == null) { return; }
        double x = lobby.opensLobby().x() + 0.5D;
        double z = lobby.opensLobby().z() + 0.5D;
        Travel.to(player, level, x, standing(level, x, lobby.opensLobby().y(), z), z, player.getYRot(), 0.0F);
        OUT.put(name, player.gameMode.getGameModeForPlayer());
        player.setGameMode(GameType.SPECTATOR);
        if (!lobby.opensJoinsSays().isEmpty()) { Says.tell(player, lobby.opensJoinsSays(), ChatFormatting.GRAY); }
    }

    @Nullable private static ServerLevel lobbyLevel(MinecraftServer server, ScoreDef lobby) {
        ServerLevel level = lobby.opensLobby() == null ? null : server.getLevel(lobby.opensLobby().dimension());
        if (level == null && lobby.opensLobby() != null) { ContentLog.LOGGER.error("The lobby of {} stands in {}, which is not loaded, so nobody is gathered", lobby.name(), lobby.opensLobby().dimension().location()); }
        return level;
    }

    private static void backIn(MinecraftServer server) {
        for (Map.Entry<String, GameType> one : new LinkedHashMap<>(OUT).entrySet()) {
            ServerPlayer player = server.getPlayerList().getPlayerByName(one.getKey());
            if (player == null || !player.isAlive()) { continue; }
            player.setGameMode(one.getValue());
            OUT.remove(one.getKey());
        }
    }

    private static String outSays() {
        for (ScoreDef def : BY_NAME.values()) {
            if (def.endsLastStanding()) { return def.endsOutSays(); }
        }
        return BY_NAME.isEmpty() ? "" : BY_NAME.values().iterator().next().endsOutSays();
    }

    public static boolean holding() { return lobbied && (closed || starting > 0); }

    public static boolean standInWaits() { return lobbied ? !holding() : eliminating(); }

    private static void keepStill(MinecraftServer server) {
        if (!holding()) {
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

    private static boolean refused(Player player) {
        if (!(player instanceof ServerPlayer held) || !holding()) { return false; }
        ScoreDef lobby = lobbyDef();
        long now = held.serverLevel().getGameTime();
        Long last = TOLD.get(held.getGameProfile().getName());
        if (lobby != null && (last == null || now - last >= NOTE_TICKS)) { note(held.server, held, lobby); }
        return true;
    }

    private static void gather(MinecraftServer server) {
        ScoreDef lobby = lobbyDef();
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
                if (sideOf(each, one) != null) { waiting.add(one); }
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

    private static void sendBack(MinecraftServer server) {
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

    private static void lobbyNotes(MinecraftServer server) {
        ScoreDef lobby = lobbyDef();
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
        ContentWelcome.show(player, said, ChatFormatting.GOLD);
    }

    public static void onStill(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LivingEntity) || event.getEntity() instanceof Player || event.getEntity().level().isClientSide() || !holding()) { return; }
        event.setCanceled(true);
    }

    public static void onHeldHurt(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player && !event.getEntity().level().isClientSide() && holding()) { event.setCanceled(true); }
    }

    public static void onHeldHit(AttackEntityEvent event) {
        if (refused(event.getEntity())) { event.setCanceled(true); }
    }

    public static void onHeldDig(PlayerInteractEvent.LeftClickBlock event) {
        if (refused(event.getEntity())) { event.setCanceled(true); }
    }

    public static void onHeldUse(PlayerInteractEvent.RightClickBlock event) {
        if (refused(event.getEntity())) { event.setCanceled(true); }
    }

    public static void onHeldItem(PlayerInteractEvent.RightClickItem event) {
        if (refused(event.getEntity())) { event.setCanceled(true); }
    }

    public static void onHeldTouch(PlayerInteractEvent.EntityInteract event) {
        if (refused(event.getEntity())) { event.setCanceled(true); }
    }

    public static void onHeldBreak(BlockEvent.BreakEvent event) {
        if (refused(event.getPlayer())) { event.setCanceled(true); }
    }

    public static void onHeldPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player && refused(player)) { event.setCanceled(true); }
    }

    public static void onHeldToss(ItemTossEvent event) {
        if (!refused(event.getPlayer())) { return; }
        event.setCanceled(true);
        event.getPlayer().getInventory().add(event.getEntity().getItem());
        event.getPlayer().inventoryMenu.broadcastChanges();
    }

    private static String waitingLine(MinecraftServer server, ScoreDef lobby) {
        List<String> leaders = ContentTeams.leaders(server.overworld());
        return lobby.opensSays().replace("{leader}", leaders.isEmpty() ? "a leader" : String.join(", ", leaders));
    }

    private static void waitingSaid(MinecraftServer server) {
        long now = server.overworld().getGameTime();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { DUE.put(player.getGameProfile().getName(), now); }
    }

    public static void greet(ServerPlayer player) {
        if (closed && lobbied) { DUE.put(player.getGameProfile().getName(), player.serverLevel().getGameTime() + NOTE_TICKS); }
    }

    public static String start(MinecraftServer server, @Nullable ServerPlayer who, String name, boolean operator) {
        if (lobbyDef() == null) { return "This pack's rounds open on their own"; }
        if (!closed) { return "The round is already running"; }
        if (!operator && (who == null || ContentTeams.leadsNoSide(who))) { return "Only a side's leader starts the round"; }
        List<String> reading = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (ContentIntroPlay.reading(player.getUUID())) { reading.add(player.getGameProfile().getName()); }
        }
        if (!reading.isEmpty()) { return "Not yet: still reading the intro: " + String.join(", ", reading); }
        closed = false;
        opening = lobbyDef();
        starting = OPENS_IN;
        count(server);
        ContentLog.LOGGER.info("{} started the round", name);
        return "The round starts";
    }

    private static void count(MinecraftServer server) {
        if (opening == null || opening.startsSays().isEmpty()) { return; }
        ContentPregen.tellBar(server, opening.startsSays().replace("{seconds}", Integer.toString(starting)));
    }

    private static void open(MinecraftServer server) {
        opening = null;
        sendBack(server);
        if (ContentControl.flag(ContentControl.CHUNKS, "resetClearsEntities", Config.chunks.resetClearsEntities())) { ContentReset.sweep(server); }
        roundOver(server);
        ContentTeams.placeAtSpawns(server);
        ContentTeams.standInsNow(server);
        DUE.clear();
        SHOWN.clear();
    }

    public static void roundOver(MinecraftServer server) {
        Scoreboard board = Scores.board(server);
        for (ScoreDef def : BY_NAME.values()) {
            if (!def.carries()) {
                FINISHED.remove(def.name());
                continue;
            }
            if (!FINISHED.remove(def.name())) { continue; }
            Objective held = Scores.objective(board, def.name());
            if (held == null) { continue; }
            for (Scores.Row one : Scores.rows(board, held)) { Scores.reset(board, one.owner(), held); }
            ContentLog.LOGGER.info("The {} match is over, so its standing is cleared for the next one", def.displayName());
        }
        ticks = 0;
        IN_PLAY.clear();
        KILLS.clear();
        DEATHS.clear();
        OWN.clear();
        ContentTeams.seatWaiting(server);
        ContentTeams.draw(server);
    }

    private static List<String> standings(MinecraftServer server, ScoreDef def) {
        List<String> lines = new ArrayList<>();
        Objective objective = Scores.objective(Scores.board(server), def.name());
        if (objective == null) { return lines; }
        for (Scores.Row row : Scores.rows(Scores.board(server), objective)) { lines.add(row.owner() + " " + row.value()); }
        return lines;
    }

    public static void keep(ServerLevel level) {
        if (BY_NAME.isEmpty()) { return; }
        if (!live) {
            live = true;
            if (lobbyDef() != null) { closed = true; }
        }
        Scoreboard board = Scores.board(level.getServer());
        for (ScoreDef def : BY_NAME.values()) {
            Objective held = Scores.objective(board, def.name());
            if (held == null) { held = Scores.addObjective(board, def.name(), def.criterion(), Component.literal(def.displayName()), def.render() == null ? def.criterion().getDefaultRenderType() : def.render()); }
            else if (held.getCriteria() != def.criterion()) { ContentLog.LOGGER.error("The objective {} is already on this world's scoreboard scoring on something else, so the pack's criterion is left alone", def.name()); }
            held.setDisplayName(Component.literal(def.displayName()));
            if (def.render() != null) { held.setRenderType(def.render()); }
            if (def.shown()) { Scores.display(board, def.slot(), held); }
        }
    }

    private record Spot(ResourceKey<Level> dimension, double x, double y, double z, float yaw) {}
}
