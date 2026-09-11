package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
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

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentScoring {
    private static final Map<String, ScoreDef> BY_NAME = new LinkedHashMap<>();
    private static final Set<String> FINISHED = new LinkedHashSet<>();
    private static final Map<String, Integer> KILLS = new LinkedHashMap<>();
    private static final Map<String, Integer> DEATHS = new LinkedHashMap<>();
    private static final Map<String, Integer> OWN = new LinkedHashMap<>();
    private static final int OPENS_IN = 5;
    private static final long A_MINUTE = 60000L;
    private static final String PLAYER = "minecraft:player";
    private static int starting;
    @Nullable private static ScoreDef opening;
    private static long opened;
    private static int beat;
    private static int waiting;
    @Nullable private static ScoreDef resetting;

    private ContentScoring() {}

    public static boolean load() {
        BY_NAME.clear();
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
        return !BY_NAME.isEmpty();
    }

    public static Map<String, ScoreDef> all() { return BY_NAME; }

    public static boolean any() { return !BY_NAME.isEmpty(); }

    @Nullable public static ScoreDef named(String name) { return BY_NAME.get(name); }

    public static boolean roundRunning() {
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
        if (def.ends() && FINISHED.contains(def.name())) { return; }
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

    public static void onServerTick(ServerTickEvent.Post event) {
        if (BY_NAME.isEmpty() || opened == 0L) { return; }
        if (++beat % 20 != 0) { return; }
        MinecraftServer server = event.getServer();
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
        long running = System.currentTimeMillis() - opened;
        for (ScoreDef def : BY_NAME.values()) {
            if (def.endsAfterMinutes() <= 0 || FINISHED.contains(def.name())) { continue; }
            if (running >= def.endsAfterMinutes() * A_MINUTE) { finish(server, def, "time"); }
        }
    }

    private static void watch(MinecraftServer server, ScoreDef def, int standing) {
        if (def.endsAtScore() <= 0 || standing < def.endsAtScore() || FINISHED.contains(def.name())) { return; }
        finish(server, def, "score");
    }

    private static void finish(MinecraftServer server, ScoreDef def, String why) {
        if (!FINISHED.add(def.name())) { return; }
        List<String> lines = standings(server, def);
        ContentLog.LOGGER.info("The {} round is over on {}: {}", def.displayName(), why, lines);
        ContentLog.LOGGER.info("Kills this round by the killer's kind: {}; deaths by kind: {}; kills of their own side: {}", KILLS, DEATHS, OWN);
        award(server, def);
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

    private static void award(MinecraftServer server, ScoreDef def) {
        if (def.awardsTo().isEmpty()) { return; }
        Scoreboard board = Scores.board(server);
        Objective round = Scores.objective(board, def.name());
        Objective match = Scores.objective(board, def.awardsTo());
        if (round == null || match == null) {
            ContentLog.LOGGER.error("The round {} awards to {}, which is not an objective this pack keeps, so no round win is recorded", def.name(), def.awardsTo());
            return;
        }
        Scores.Row best = null;
        boolean tied = false;
        for (Scores.Row one : Scores.rows(board, round)) {
            if (best == null || one.value() > best.value()) {
                best = one;
                tied = false;
            }
            else if (one.value() == best.value()) { tied = true; }
        }
        if (best == null || tied) {
            ContentLog.LOGGER.info("The round ended level, so no round win is recorded");
            return;
        }
        int held = Scores.score(board, best.owner(), match) + 1;
        Scores.set(board, best.owner(), match, held);
        ContentLog.LOGGER.info("{} took the round, and now holds {} in {}", best.owner(), held, def.awardsTo());
        ScoreDef whole = BY_NAME.get(def.awardsTo());
        if (whole == null || FINISHED.contains(whole.name())) { return; }
        int played = 0;
        for (Scores.Row one : Scores.rows(board, match)) { played += one.value(); }
        if (whole.endsAfterRounds() > 0 && played >= whole.endsAfterRounds()) { finish(server, whole, "rounds"); }
        else { watch(server, whole, held); }
    }

    public static void starting(MinecraftServer server) {
        starting = OPENS_IN;
        count(server);
    }

    private static void count(MinecraftServer server) {
        if (opening == null || opening.startsSays().isEmpty()) { return; }
        ContentPregen.tellBar(server, opening.startsSays().replace("{seconds}", Integer.toString(starting)));
    }

    private static void open(MinecraftServer server) {
        opening = null;
        if (ContentControl.flag(ContentControl.CHUNKS, "resetClearsEntities", Config.chunks.resetClearsEntities())) { ContentReset.sweep(server); }
        roundOver(server);
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
        opened = System.currentTimeMillis();
        KILLS.clear();
        DEATHS.clear();
        OWN.clear();
        ContentTeams.seatWaiting(server);
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
        if (opened == 0L) { opened = System.currentTimeMillis(); }
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
