package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.def.TurnsDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Scores;
import mctmods.resourcedatapackloader.util.TurnClock;

import net.minecraft.ChatFormatting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public final class ContentTurns {
    private static final long NOTE_TICKS = 60L;
    private static final List<String> SEEN = new ArrayList<>();
    private static final List<String> ORDER = new ArrayList<>();
    private static final Set<UUID> HELD = new HashSet<>();
    private static final Map<String, GameType> ASIDE = new LinkedHashMap<>();
    private static final Map<String, Long> TOLD = new LinkedHashMap<>();
    private static final TurnClock CLOCK = new TurnClock();
    @Nullable private static ScoreDef playing;
    @Nullable private static String current;
    @Nullable private static String lastWinner;
    private static boolean ended;
    private static int at;
    private static int cycle;
    private static int gap;

    private ContentTurns() {}

    @Nullable private static ScoreDef turnsDef() {
        for (ScoreDef def : ContentScoring.all().values()) {
            if (def.turns() != null) { return def; }
        }
        return null;
    }

    public static boolean free(Entity who) { return !HELD.contains(who.getUUID()); }

    static boolean holdsNobody() { return HELD.isEmpty(); }

    static void second(MinecraftServer server, boolean open) {
        ScoreDef def = turnsDef();
        if (def == null) { return; }
        TurnsDef turns = def.turns();
        if (ended || !open || turns == null) {
            stop(server);
            return;
        }
        playing = def;
        Map<String, List<Entity>> groups = groups(server, turns);
        for (String group : groups.keySet()) {
            if (!SEEN.contains(group)) { SEEN.add(group); }
        }
        if (reached(server, def, turns)) {
            ContentLog.LOGGER.info("A side reached {} in {}, so the turns are over", turns.endsAtScore(), def.name());
            ContentScoring.finish(server, def, "turns", null);
            return;
        }
        if (gap > 0) {
            if (--gap == 0) { advance(server, def, turns, groups); }
            else { bar(server, "turngap", "seconds", gap); }
        }
        else if (current != null && !groups.containsKey(current)) {
            ContentLog.LOGGER.info("{} has nobody left in play, so its turn ends", current);
            endTurn(server, def, turns, groups);
        }
        else if (current == null) { advance(server, def, turns, groups); }
        else if (CLOCK.runsOut()) {
            tellAll(server, "turnout", current);
            ContentLog.LOGGER.info("{} ran out of time", current);
            endTurn(server, def, turns, groups);
        }
        else {
            if (CLOCK.warns()) { warn(def, groups.get(current)); }
            bar(server, "turnclock", "group", shown(current), "seconds", CLOCK.left());
        }
        if (!ended) { hold(server, turns, groups); }
    }

    private static void advance(MinecraftServer server, ScoreDef def, TurnsDef turns, Map<String, List<Entity>> groups) {
        if (groups.isEmpty()) { return; }
        while (true) {
            if (at >= ORDER.size()) {
                if (!ORDER.isEmpty()) { cycle++; }
                if (turns.cycles() > 0 && cycle >= turns.cycles()) {
                    ContentLog.LOGGER.info("All {} cycle(s) of turns are played, so the turns are over", cycle);
                    ContentScoring.finish(server, def, "turns", null);
                    return;
                }
                order(server, def, turns, groups);
                at = 0;
            }
            String next = ORDER.get(at++);
            if (groups.containsKey(next)) {
                begin(server, turns, next);
                return;
            }
            ContentLog.LOGGER.info("{} has nobody left in play, so its turn is skipped", next);
        }
    }

    private static void order(MinecraftServer server, ScoreDef def, TurnsDef turns, Map<String, List<Entity>> groups) {
        ORDER.clear();
        for (String group : SEEN) {
            if (groups.containsKey(group)) { ORDER.add(group); }
        }
        if (TurnsDef.RANDOM.equals(turns.order())) { Collections.shuffle(ORDER, new java.util.Random(server.overworld().getRandom().nextLong())); }
        else if (TurnsDef.LOWEST_FIRST.equals(turns.order())) { ORDER.sort(Comparator.comparingInt(group -> score(server, def, group))); }
        else if (TurnsDef.LAST_WINNER.equals(turns.order()) && lastWinner != null && ORDER.remove(lastWinner)) { ORDER.add(0, lastWinner); }
        ContentLog.LOGGER.info("Cycle {} of turns in {} goes {}", cycle + 1, def.name(), ORDER);
    }

    private static void begin(MinecraftServer server, TurnsDef turns, String group) {
        current = group;
        CLOCK.start(turns.seconds());
        ContentLog.LOGGER.info("{} takes its turn for {} second(s)", group, turns.seconds());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { Says.line(player, ChatFormatting.GOLD, ContentDice.words(player, "turn", "group", shown(group), "seconds", turns.seconds())); }
    }

    private static void endTurn(MinecraftServer server, ScoreDef def, TurnsDef turns, Map<String, List<Entity>> groups) {
        current = null;
        CLOCK.stop();
        gap = turns.gapSeconds();
        if (gap == 0) { advance(server, def, turns, groups); }
    }

    private static void warn(ScoreDef def, List<Entity> members) {
        boolean last = CLOCK.lastWarning();
        for (Entity one : members) {
            if (!(one instanceof ServerPlayer player)) { continue; }
            Says.line(player, last ? ChatFormatting.RED : ChatFormatting.YELLOW, ContentDice.words(player, "turnwarn", "group", shown(current), "seconds", CLOCK.left()));
            TurnClock.ping(player, last);
        }
        ContentLog.LOGGER.info("{} has {} second(s) left of its turn in {}", current, CLOCK.left(), def.name());
    }

    private static void hold(MinecraftServer server, TurnsDef turns, Map<String, List<Entity>> groups) {
        HELD.clear();
        GameType mode = GameType.byName(turns.held());
        for (Map.Entry<String, List<Entity>> group : groups.entrySet()) {
            boolean waits = !group.getKey().equals(current);
            for (Entity one : group.getValue()) {
                if (!(one instanceof ServerPlayer player)) {
                    if (waits) { HELD.add(one.getUUID()); }
                    continue;
                }
                String name = player.getGameProfile().getName();
                if (!waits) { back(player); }
                else if (TurnsDef.FROZEN.equals(turns.held())) { HELD.add(player.getUUID()); }
                else if (!ASIDE.containsKey(name)) {
                    ASIDE.put(name, player.gameMode.getGameModeForPlayer());
                    player.setGameMode(mode);
                }
            }
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (ASIDE.containsKey(player.getGameProfile().getName()) && groupOf(turns, player.serverLevel(), player) == null) { back(player); }
        }
    }

    private static void back(ServerPlayer player) {
        GameType was = ASIDE.remove(player.getGameProfile().getName());
        if (was != null) { player.setGameMode(was); }
    }

    private static void stop(MinecraftServer server) {
        current = null;
        CLOCK.stop();
        HELD.clear();
        playing = null;
        if (ASIDE.isEmpty()) { return; }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { back(player); }
    }

    static void over(MinecraftServer server, ScoreDef def, @Nullable String winner) {
        if (def.turns() == null) { return; }
        ended = true;
        lastWinner = winner == null ? leader(server, def) : winner;
        stop(server);
    }

    static void reset(MinecraftServer server) {
        ended = false;
        ORDER.clear();
        at = 0;
        cycle = 0;
        gap = 0;
        stop(server);
    }

    @Nullable public static String pass(MinecraftServer server, @Nullable ServerPlayer player) {
        ScoreDef def = playing;
        TurnsDef turns = def == null ? null : def.turns();
        if (def == null || turns == null || current == null) { return "noturns"; }
        if (player != null && !current.equals(groupOf(turns, player.serverLevel(), player))) { return "notturn"; }
        tellAll(server, "turnpass", current);
        ContentLog.LOGGER.info("{} passed the turn of {}", player == null ? "The server" : player.getGameProfile().getName(), current);
        Map<String, List<Entity>> groups = groups(server, turns);
        endTurn(server, def, turns, groups);
        if (!ended) { hold(server, turns, groups); }
        return null;
    }

    public static void byItem(ServerPlayer player) {
        String refusal = pass(player.server, player);
        if (refusal != null) { Says.line(player, ChatFormatting.RED, ContentDice.words(player, refusal)); }
    }

    static boolean refused(ServerPlayer player) {
        if (free(player)) { return false; }
        long now = player.serverLevel().getGameTime();
        Long last = TOLD.get(player.getGameProfile().getName());
        if (last == null || now - last >= NOTE_TICKS) {
            TOLD.put(player.getGameProfile().getName(), now);
            Says.line(player, ChatFormatting.GRAY, ContentDice.words(player, "notturn"));
        }
        return true;
    }

    private static Map<String, List<Entity>> groups(MinecraftServer server, TurnsDef turns) {
        Map<String, List<Entity>> groups = new LinkedHashMap<>();
        ServerLevel overworld = server.overworld();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            String name = player.getGameProfile().getName();
            if (ContentScoring.OUT.containsKey(name) || player.isSpectator() && !ASIDE.containsKey(name)) { continue; }
            String group = groupOf(turns, overworld, player);
            if (group != null) { groups.computeIfAbsent(group, none -> new ArrayList<>()).add(player); }
        }
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity one : level.getAllEntities()) {
                if (!(one instanceof Mob mob) || !mob.isAlive() || mob.getHealth() <= 0.0F) { continue; }
                String group = groupOf(turns, level, mob);
                if (group != null) { groups.computeIfAbsent(group, none -> new ArrayList<>()).add(mob); }
            }
        }
        return groups;
    }

    @Nullable private static String groupOf(TurnsDef turns, ServerLevel level, Entity who) {
        String side = ContentScoring.sideOf(level, who);
        if (who instanceof ServerPlayer player) { return side == null ? player.getGameProfile().getName() : side; }
        if (side != null) { return side; }
        for (String tag : turns.mobTags()) {
            if (who.getTags().contains(tag)) { return tag; }
        }
        String id = EntityType.getKey(who.getType()).toString();
        return turns.mobTypes().contains(id) ? id : null;
    }

    private static boolean reached(MinecraftServer server, ScoreDef def, TurnsDef turns) {
        if (turns.endsAtScore() <= 0) { return false; }
        for (String group : SEEN) {
            if (score(server, def, group) >= turns.endsAtScore()) { return true; }
        }
        return false;
    }

    private static int score(MinecraftServer server, ScoreDef def, String group) {
        Scoreboard board = Scores.board(server);
        Objective objective = Scores.objective(board, def.name());
        return objective == null || !Scores.has(board, group, objective) ? 0 : Scores.score(board, group, objective);
    }

    @Nullable private static String leader(MinecraftServer server, ScoreDef def) {
        Scoreboard board = Scores.board(server);
        Objective objective = Scores.objective(board, def.name());
        if (objective == null) { return null; }
        List<Scores.Row> top = ContentScoringTie.top(Scores.rows(board, objective));
        return top.size() == 1 ? top.get(0).owner() : null;
    }

    static String shown(String group) {
        TeamDef side = ContentTeams.named(group);
        return side == null ? group : side.displayName();
    }

    private static void tellAll(MinecraftServer server, String key, String group) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { Says.line(player, ChatFormatting.GOLD, ContentDice.words(player, key, "group", shown(group))); }
    }

    private static void bar(MinecraftServer server, String key, Object... pairs) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { player.displayClientMessage(Says.marked(ContentDice.words(player, key, pairs), ChatFormatting.YELLOW), true); }
    }
}
