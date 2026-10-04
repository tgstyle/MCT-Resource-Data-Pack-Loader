package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.def.TurnsDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.TurnClock;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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
            if (def.turns != null) { return def; }
        }
        return null;
    }

    public static boolean free(Entity who) { return !HELD.contains(who.getUniqueID()); }

    static boolean holdsNobody() { return HELD.isEmpty(); }

    static void second(MinecraftServer server, boolean open) {
        ScoreDef def = turnsDef();
        if (def == null) { return; }
        TurnsDef turns = def.turns;
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
            ContentLog.LOGGER.info("A side reached {} in {}, so the turns are over", turns.endsAtScore, def.name);
            ContentScoring.finish(def, "turns", null);
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
                if (turns.cycles > 0 && cycle >= turns.cycles) {
                    ContentLog.LOGGER.info("All {} cycle(s) of turns are played, so the turns are over", cycle);
                    ContentScoring.finish(def, "turns", null);
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
        if (TurnsDef.RANDOM.equals(turns.order)) { Collections.shuffle(ORDER, server.getWorld(0).rand); }
        else if (TurnsDef.LOWEST_FIRST.equals(turns.order)) { ORDER.sort(Comparator.comparingInt(group -> score(server, def, group))); }
        else if (TurnsDef.LAST_WINNER.equals(turns.order) && lastWinner != null && ORDER.remove(lastWinner)) { ORDER.add(0, lastWinner); }
        ContentLog.LOGGER.info("Cycle {} of turns in {} goes {}", cycle + 1, def.name, ORDER);
    }

    private static void begin(MinecraftServer server, TurnsDef turns, String group) {
        current = group;
        CLOCK.start(turns.seconds);
        ContentLog.LOGGER.info("{} takes its turn for {} second(s)", group, turns.seconds);
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { Says.chat(player, TextFormatting.GOLD, ContentDice.words(player, "turn", "group", shown(group), "seconds", turns.seconds)); }
    }

    private static void endTurn(MinecraftServer server, ScoreDef def, TurnsDef turns, Map<String, List<Entity>> groups) {
        current = null;
        CLOCK.stop();
        gap = turns.gapSeconds;
        if (gap == 0) { advance(server, def, turns, groups); }
    }

    private static void warn(ScoreDef def, List<Entity> members) {
        boolean last = CLOCK.lastWarning();
        for (Entity one : members) {
            if (!(one instanceof EntityPlayerMP)) { continue; }
            EntityPlayerMP player = (EntityPlayerMP) one;
            Says.chat(player, last ? TextFormatting.RED : TextFormatting.YELLOW, ContentDice.words(player, "turnwarn", "group", shown(current), "seconds", CLOCK.left()));
            TurnClock.ping(player, last);
        }
        ContentLog.LOGGER.info("{} has {} second(s) left of its turn in {}", current, CLOCK.left(), def.name);
    }

    private static void hold(MinecraftServer server, TurnsDef turns, Map<String, List<Entity>> groups) {
        HELD.clear();
        GameType mode = GameType.parseGameTypeWithDefault(turns.held, GameType.ADVENTURE);
        for (Map.Entry<String, List<Entity>> group : groups.entrySet()) {
            boolean waits = !group.getKey().equals(current);
            for (Entity one : group.getValue()) {
                if (!(one instanceof EntityPlayerMP)) {
                    if (waits) { HELD.add(one.getUniqueID()); }
                    continue;
                }
                EntityPlayerMP player = (EntityPlayerMP) one;
                if (!waits) { back(player); }
                else if (TurnsDef.FROZEN.equals(turns.held)) { HELD.add(player.getUniqueID()); }
                else if (!ASIDE.containsKey(player.getName())) {
                    ASIDE.put(player.getName(), player.interactionManager.getGameType());
                    player.setGameType(mode);
                }
            }
        }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (ASIDE.containsKey(player.getName()) && groupOf(turns, player.world, player) == null) { back(player); }
        }
    }

    private static void back(EntityPlayerMP player) {
        GameType was = ASIDE.remove(player.getName());
        if (was != null) { player.setGameType(was); }
    }

    private static void stop(MinecraftServer server) {
        current = null;
        CLOCK.stop();
        HELD.clear();
        playing = null;
        if (ASIDE.isEmpty()) { return; }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { back(player); }
    }

    static void over(MinecraftServer server, ScoreDef def, @Nullable String winner) {
        if (def.turns == null) { return; }
        ended = true;
        lastWinner = winner == null ? leader(server, def) : winner;
        stop(server);
    }

    static void reset(@Nullable MinecraftServer server) {
        ended = false;
        ORDER.clear();
        at = 0;
        cycle = 0;
        gap = 0;
        if (server != null) { stop(server); }
    }

    @Nullable public static String pass(MinecraftServer server, ICommandSender sender) {
        ScoreDef def = playing;
        TurnsDef turns = def == null ? null : def.turns;
        if (def == null || turns == null || current == null) { return "noturns"; }
        if (sender instanceof EntityPlayer && !current.equals(groupOf(turns, sender.getEntityWorld(), (Entity) sender))) { return "notturn"; }
        tellAll(server, "turnpass", current);
        ContentLog.LOGGER.info("{} passed the turn of {}", sender.getName(), current);
        Map<String, List<Entity>> groups = groups(server, turns);
        endTurn(server, def, turns, groups);
        if (!ended) { hold(server, turns, groups); }
        return null;
    }

    public static void byItem(EntityPlayerMP player) {
        MinecraftServer server = player.getServer();
        String refusal = server == null ? null : pass(server, player);
        if (refusal != null) { Says.chat(player, TextFormatting.RED, ContentDice.words(player, refusal)); }
    }

    static boolean refused(EntityPlayer player) {
        if (free(player)) { return false; }
        long now = player.world.getTotalWorldTime();
        Long last = TOLD.get(player.getName());
        if (last == null || now - last >= NOTE_TICKS) {
            TOLD.put(player.getName(), now);
            Says.chat(player, TextFormatting.GRAY, ContentDice.words(player, "notturn"));
        }
        return true;
    }

    private static Map<String, List<Entity>> groups(MinecraftServer server, TurnsDef turns) {
        Map<String, List<Entity>> groups = new LinkedHashMap<>();
        WorldServer overworld = server.getWorld(0);
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (ContentScoring.OUT.containsKey(player.getName()) || player.isSpectator() && !ASIDE.containsKey(player.getName())) { continue; }
            String group = groupOf(turns, overworld, player);
            if (group != null) { groups.computeIfAbsent(group, none -> new ArrayList<>()).add(player); }
        }
        for (WorldServer world : server.worlds) {
            for (Entity one : world.loadedEntityList) {
                if (!(one instanceof EntityLiving) || one.isDead || ((EntityLiving) one).getHealth() <= 0.0F) { continue; }
                String group = groupOf(turns, world, one);
                if (group != null) { groups.computeIfAbsent(group, none -> new ArrayList<>()).add(one); }
            }
        }
        return groups;
    }

    @Nullable private static String groupOf(TurnsDef turns, World world, Entity who) {
        String member = who instanceof EntityPlayer ? who.getName() : who.getCachedUniqueIdString();
        String side = ContentScoring.sideOf(world, who, member);
        if (side != null || who instanceof EntityPlayer) { return side == null ? member : side; }
        for (String tag : turns.mobTags) {
            if (who.getTags().contains(tag)) { return tag; }
        }
        ResourceLocation id = EntityList.getKey(who);
        return id != null && turns.mobTypes.contains(id.toString()) ? id.toString() : null;
    }

    private static boolean reached(MinecraftServer server, ScoreDef def, TurnsDef turns) {
        if (turns.endsAtScore <= 0) { return false; }
        for (String group : SEEN) {
            if (score(server, def, group) >= turns.endsAtScore) { return true; }
        }
        return false;
    }

    private static int score(MinecraftServer server, ScoreDef def, String group) {
        Scoreboard board = server.getWorld(0).getScoreboard();
        ScoreObjective objective = board.getObjective(def.name);
        return objective == null || !board.entityHasObjective(group, objective) ? 0 : board.getOrCreateScore(group, objective).getScorePoints();
    }

    @Nullable private static String leader(MinecraftServer server, ScoreDef def) {
        ScoreObjective objective = server.getWorld(0).getScoreboard().getObjective(def.name);
        if (objective == null) { return null; }
        List<Score> top = ContentScoringTie.top(server.getWorld(0).getScoreboard().getSortedScores(objective));
        return top.size() == 1 ? top.get(0).getPlayerName() : null;
    }

    private static String shown(String group) {
        TeamDef side = ContentTeams.named(group);
        return side == null ? group : side.displayName;
    }

    private static void tellAll(MinecraftServer server, String key, String group) {
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { Says.chat(player, TextFormatting.GOLD, ContentDice.words(player, key, "group", shown(group))); }
    }

    private static void bar(MinecraftServer server, String key, Object... pairs) {
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { player.sendStatusMessage(Says.marked(ContentDice.words(player, key, pairs), TextFormatting.YELLOW), true); }
    }
}
