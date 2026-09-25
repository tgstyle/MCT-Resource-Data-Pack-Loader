package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.card.CardFire;
import mctmods.resourcedatapackloader.content.card.CardIds;
import mctmods.resourcedatapackloader.content.card.CardLook;
import mctmods.resourcedatapackloader.content.card.CardRules;
import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregenHold;
import mctmods.resourcedatapackloader.content.worldgen.ContentReset;
import mctmods.resourcedatapackloader.network.MessageCard;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Scores;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
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
    static final Map<String, GameType> OUT = new LinkedHashMap<>();
    private static final Set<String> IN_PLAY = new LinkedHashSet<>();
    private static final int OPENS_IN = 5;
    private static final int SETTLE_TICKS = 3;
    private static final int MINUTE_TICKS = 20 * 60;
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

    public static void load() {
        BY_NAME.clear();
        ContentRoundReset.clear();
        Json.eachFile(PackManager.SCORING, "score file", (key, contents) -> {
            ScoreDef def = ContentParserGames.scoreFile(key, contents);
            if (def == null) { return; }
            if (BY_NAME.containsKey(def.name())) {
                ContentLog.LOGGER.error("Score file {} names the objective '{}', which another pack already named, so the later one is left out", key, def.name());
                return;
            }
            BY_NAME.put(def.name(), def);
        });
        if (!BY_NAME.isEmpty()) { Summary.info("scoring", "Keeping " + BY_NAME.size() + " score(s): " + BY_NAME.keySet()); }
        lobbied = lobbyDef() != null;
    }

    public static Map<String, ScoreDef> all() { return BY_NAME; }

    public static boolean any() { return !BY_NAME.isEmpty(); }

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

    @Nullable static String sideOf(ServerLevel level, Entity who) {
        PlayerTeam team = Scores.teamOf(Scores.board(level.getServer()), memberOf(who));
        if (team != null) { return team.getName(); }
        if (who instanceof Player) { return null; }
        List<TeamDef> claiming = ContentTeams.claiming(kindOf(who));
        return claiming.isEmpty() ? null : claiming.get(0).name();
    }

    private static void bump(Scoreboard board, Objective objective, String row, int points) { Scores.set(board, row, objective, Scores.score(board, row, objective) + points); }

    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) { return; }
        tick(event.getServer());
    }

    private static void tick(MinecraftServer server) {
        if (BY_NAME.isEmpty() || !live) { return; }
        ContentScoringLobby.keepStill(server);
        if (!closed && starting == 0 && waiting == 0 && !ContentPregen.busy()) { ticks++; }
        if (++beat % 20 != 0) { return; }
        settling(server);
        ContentRoundReset.second(server);
        if (closed && lobbied) {
            ContentScoringLobby.gather(server);
            ContentScoringLobby.lobbyNotes(server);
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
            if (!resetting.intermissionSays().isEmpty()) { ContentPregenHold.tellBar(server, resetting.intermissionSays().replace("{seconds}", Integer.toString(waiting))); }
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
            lines.add(0, (side == null ? winner : side.displayName()) + " stood last");
        }
        else if (LAST_STANDING.equals(why)) { lines.add(0, "No side was left standing"); }
        else if (RESET.equals(why)) { lines.add(0, "The round was reset"); }
        ContentLog.LOGGER.info("The {} round is over on {}: {}", def.displayName(), why, lines);
        ContentLog.LOGGER.info("Kills this round by the killer's kind: {}; deaths by kind: {}; kills of their own side: {}", KILLS, DEATHS, OWN);
        if (RESET.equals(why)) { ContentLog.LOGGER.info("A reset round is awarded to nobody"); }
        else { award(server, def, winner); }
        if (def.endsResets()) {
            resetting = def;
            waiting = Math.max(1, def.endsIntermission());
            ContentLog.LOGGER.info("The map is reset in {} second(s), so the scores can be read first", def.endsIntermission());
        }
        ItemStack icon = def.resultsIcon().isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(ResourceLocation.fromNamespaceAndPath("resourcedatapackloader", "results"), def.resultsIcon(), 1);
        if (!CardRules.unset(CardIds.SCORING_RESULTS)) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) { CardFire.builtin(CardIds.SCORING_RESULTS, player, CardLook.card(def.resultsTitle(), lines, icon, def.resultsImage(), def.resultsBackground(), def.resultsTicks())); }
            return;
        }
        if (!def.resultsCard()) {
            Says.tellAll(server, def.displayName() + " is over", ChatFormatting.GOLD);
            for (String line : lines) { Says.tellAll(server, line, ChatFormatting.YELLOW); }
            return;
        }
        MessageCard card = new MessageCard(def.resultsTitle(), lines, icon, def.resultsImage(), def.resultsBackground(), 0xFFFFFF, def.resultsTicks(), false, Says.panel(), Says.font());
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
            ContentScoringLobby.waitingSaid(server);
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

    @Nullable static ScoreDef lobbyDef() {
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
        String winner = left.isEmpty() ? null : left.get(0);
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
        if (!said.isEmpty()) { Says.tell(player, CardIds.SCORING_OUT, said, ChatFormatting.GRAY); }
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
            ContentScoringLobby.waitsInLobby(server, player);
        }
        SETTLING.keySet().retainAll(online);
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

    public static void onStill(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player || event.getEntity().level().isClientSide() || !holding()) { return; }
        event.setCanceled(true);
    }

    public static void onHeldHurt(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player && !event.getEntity().level().isClientSide() && holding()) { event.setCanceled(true); }
    }

    public static void onHeldHit(AttackEntityEvent event) {
        if (ContentScoringLobby.refused(event.getEntity())) { event.setCanceled(true); }
    }

    public static void onHeldDig(PlayerInteractEvent.LeftClickBlock event) {
        if (ContentScoringLobby.refused(event.getEntity())) { event.setCanceled(true); }
    }

    public static void onHeldUse(PlayerInteractEvent.RightClickBlock event) {
        if (ContentScoringLobby.refused(event.getEntity())) { event.setCanceled(true); }
    }

    public static void onHeldItem(PlayerInteractEvent.RightClickItem event) {
        if (ContentScoringLobby.refused(event.getEntity())) { event.setCanceled(true); }
    }

    public static void onHeldTouch(PlayerInteractEvent.EntityInteract event) {
        if (ContentScoringLobby.refused(event.getEntity())) { event.setCanceled(true); }
    }

    public static void onHeldBreak(BlockEvent.BreakEvent event) {
        if (ContentScoringLobby.refused(event.getPlayer())) { event.setCanceled(true); }
    }

    public static void onHeldPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player && ContentScoringLobby.refused(player)) { event.setCanceled(true); }
    }

    public static void onHeldToss(ItemTossEvent event) {
        if (!ContentScoringLobby.refused(event.getPlayer())) { return; }
        event.setCanceled(true);
        event.getPlayer().getInventory().add(event.getEntity().getItem());
        event.getPlayer().inventoryMenu.broadcastChanges();
    }

    public static void greet(ServerPlayer player) {
        if (closed && lobbied) { ContentScoringLobby.DUE.put(player.getGameProfile().getName(), player.serverLevel().getGameTime() + ContentScoringLobby.NOTE_TICKS); }
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
        ContentPregenHold.tellBar(server, opening.startsSays().replace("{seconds}", Integer.toString(starting)));
    }

    private static void open(MinecraftServer server) {
        opening = null;
        ContentScoringLobby.sendBack(server);
        if (ContentControl.flag(ContentControl.CHUNKS, "resetClearsEntities", Config.chunks.resetClearsEntities())) { ContentReset.sweep(server); }
        roundOver(server);
        ContentTeams.placeAtSpawns(server);
        ContentTeams.standInsNow(server);
        ContentScoringLobby.DUE.clear();
        ContentScoringLobby.SHOWN.clear();
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
        ContentTeamsPicks.draw(server);
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
}
