package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.DiceDef;
import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Scores;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class ContentDice {
    public static final String SILENT = "silent";
    public static final String SELF = "self";
    public static final String TEAM = "team";
    public static final String ALL = "all";
    public static final String RADIUS = "radius";
    public static final List<String> AUDIENCES = List.of(SELF, TEAM, ALL, RADIUS, SILENT);
    private static final int KEPT = 50;
    private static final int MOST_DICE = 100;
    private static final int MOST_SIDES = 1000;
    private static final Pattern ROLL = Pattern.compile("(\\d*)d(\\d+)([+-]\\d+)?");
    private static final List<Integer> SIDE_WORDS = List.of(2, 4, 6, 8, 10, 12, 20, 100);
    private static final Map<String, Map<String, Integer>> DICE = new LinkedHashMap<>();
    private static final Map<String, List<String>> DECKS = new LinkedHashMap<>();
    private static final Set<String> FIXED = new HashSet<>();
    private static final Map<String, String> SAYS = new LinkedHashMap<>();
    private static final Deque<String> RECENT = new ArrayDeque<>();
    private static Audience heard = new Audience(ALL, 0);
    @Nullable private static Identifier heardFrom;

    private ContentDice() {}

    public static void load() {
        DICE.clear();
        DECKS.clear();
        FIXED.clear();
        SAYS.clear();
        heard = new Audience(ALL, 0);
        heardFrom = null;
        Json.eachFile(PackManager.DICE, "dice file", (key, contents) -> {
            DiceDef def = ContentParserGames.diceFile(key, contents);
            if (def != null) { take(key, def); }
        });
        if (!DICE.isEmpty() || !DECKS.isEmpty()) { Summary.info("dice", "Rolling " + DICE.size() + " pack die(s) " + DICE.keySet() + " and " + DECKS.size() + " deck(s) " + DECKS.keySet()); }
    }

    private static void take(Identifier key, DiceDef def) {
        if (!def.audience().isEmpty()) {
            Audience asked = Audience.parse(def.audience());
            if (asked == null) { ContentLog.LOGGER.error("Dice file {} sets audience to '{}', which is not self, team, all, radius <blocks> or silent, so it is left as it was", key, def.audience()); }
            else if (heardFrom == null) {
                heard = asked;
                heardFrom = key;
            }
            else { ContentLog.LOGGER.error("Dice file {} sets audience too, and {} already set it, so the first one is kept", key, heardFrom); }
        }
        for (Map.Entry<String, Map<String, Integer>> die : def.dice().entrySet()) {
            if (taken(die.getKey())) { ContentLog.LOGGER.error("Dice file {} names the die '{}', which another pack already named, so the later one is left out", key, die.getKey()); }
            else { DICE.put(die.getKey(), die.getValue()); }
        }
        for (Map.Entry<String, List<String>> deck : def.decks().entrySet()) {
            if (taken(deck.getKey())) { ContentLog.LOGGER.error("Dice file {} names the deck '{}', which another pack already named, so the later one is left out", key, deck.getKey()); }
            else {
                DECKS.put(deck.getKey(), deck.getValue());
                if (def.fixed().contains(deck.getKey())) { FIXED.add(deck.getKey()); }
            }
        }
        for (Map.Entry<String, String> said : def.says().entrySet()) { SAYS.putIfAbsent(said.getKey(), said.getValue()); }
    }

    private static boolean taken(String name) { return DICE.containsKey(name) || DECKS.containsKey(name) || "coin".equals(name); }

    public static List<String> dieNames() { return new ArrayList<>(DICE.keySet()); }

    public static List<String> deckNames() { return new ArrayList<>(DECKS.keySet()); }

    public static Audience fallback() { return heard; }

    public static List<String> recent(int count) { return new ArrayList<>(RECENT).subList(0, Math.min(count, RECENT.size())); }

    public static RandomSource random(MinecraftServer server) { return server.overworld().getRandom(); }

    public static Result coin(RandomSource random) {
        boolean heads = random.nextBoolean();
        return new Result(heads ? 1 : 0).say("coin", "result", new Term(heads ? "heads" : "tails"));
    }

    public static Result die(RandomSource random, int sides) {
        if (sides < 2 || sides > MOST_SIDES) { return Result.refused("badsides", "sides", sides); }
        int rolled = 1 + random.nextInt(sides);
        return new Result(rolled).say("die", "dice", new Spell(1, sides, 0), "sides", sides, "result", rolled);
    }

    public static Result packDie(RandomSource random, String name) {
        Map<String, Integer> faces = DICE.get(name);
        if (faces == null) { return Result.refused("nodie", "name", name); }
        int total = 0;
        for (int weight : faces.values()) { total += weight; }
        int landed = random.nextInt(total);
        int at = 0;
        for (Map.Entry<String, Integer> face : faces.entrySet()) {
            at++;
            landed -= face.getValue();
            if (landed < 0) { return new Result(at).say("packdie", "die", name, "result", face.getKey()); }
        }
        return Result.refused("nodie", "name", name);
    }

    @Nullable private static int[] parse(String roll) {
        Matcher matched = ROLL.matcher(roll);
        if (!matched.matches()) { return null; }
        try {
            int count = matched.group(1).isEmpty() ? 1 : Integer.parseInt(matched.group(1));
            int sides = Integer.parseInt(matched.group(2));
            int shift = matched.group(3) == null ? 0 : Integer.parseInt(matched.group(3));
            if (count < 1 || count > MOST_DICE || sides < 2 || sides > MOST_SIDES) { return null; }
            return new int[] {count, sides, shift};
        }
        catch (NumberFormatException tooLong) { return null; }
    }

    private static List<Integer> throwOf(RandomSource random, int[] spec) {
        List<Integer> rolls = new ArrayList<>();
        for (int one = 0; one < spec[0]; one++) { rolls.add(1 + random.nextInt(spec[1])); }
        return rolls;
    }

    private static int sum(List<Integer> rolls, int[] spec) {
        int total = spec[2];
        for (int rolled : rolls) { total += rolled; }
        return total;
    }

    public static Result dice(RandomSource random, String roll) {
        int[] spec = parse(roll);
        if (spec == null) { return Result.refused("badroll", "roll", roll); }
        List<Integer> rolls = throwOf(random, spec);
        int total = sum(rolls, spec);
        Spell spelled = new Spell(spec[0], spec[1], spec[2]);
        if (spec[0] == 1) { return new Result(total).say("die", "dice", spelled, "sides", spec[1], "result", total); }
        return new Result(total).say("dice", "dice", spelled, "rolls", rolls, "result", total);
    }

    public static Result edge(RandomSource random, String roll, boolean higher) {
        int[] spec = parse(roll);
        if (spec == null) { return Result.refused("badroll", "roll", roll); }
        int first = sum(throwOf(random, spec), spec);
        int second = sum(throwOf(random, spec), spec);
        int kept = higher ? Math.max(first, second) : Math.min(first, second);
        return new Result(kept).say(higher ? "advantage" : "disadvantage", "dice", new Spell(spec[0], spec[1], spec[2]), "first", first, "second", second, "result", kept);
    }

    public static Result named(MinecraftServer server, String rolls) {
        RandomSource random = random(server);
        if ("coin".equals(rolls)) { return coin(random); }
        if (DICE.containsKey(rolls)) { return packDie(random, rolls); }
        if (DECKS.containsKey(rolls)) { return draw(server, rolls); }
        return dice(random, rolls);
    }

    public static Result draw(MinecraftServer server, String deck) {
        List<String> cards = DECKS.get(deck);
        if (cards == null) { return Result.refused("nodeck", "deck", deck); }
        int card = ContentDicePile.draw(server, deck, cards.size(), random(server));
        boolean again = card < 0 && !FIXED.contains(deck);
        if (again) {
            ContentDicePile.shuffle(server, deck);
            card = ContentDicePile.draw(server, deck, cards.size(), random(server));
        }
        if (card < 0) { return Result.refused("empty", "deck", deck); }
        return new Result(card + 1).say(again ? "reshuffled" : "draw", "deck", deck, "result", cards.get(card), "left", ContentDicePile.left(server, deck, cards.size()));
    }

    public static Result shuffle(MinecraftServer server, String deck) {
        List<String> cards = DECKS.get(deck);
        if (cards == null) { return Result.refused("nodeck", "deck", deck); }
        ContentDicePile.shuffle(server, deck);
        return new Result(cards.size()).say("shuffle", "deck", deck, "left", cards.size());
    }

    public static Result left(MinecraftServer server, String deck) {
        List<String> cards = DECKS.get(deck);
        if (cards == null) { return Result.refused("nodeck", "deck", deck); }
        int left = ContentDicePile.left(server, deck, cards.size());
        return new Result(left).say("left", "deck", deck, "left", left);
    }

    public static Result pickPlayer(MinecraftServer server) {
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        if (players.isEmpty()) { return Result.refused("nobody"); }
        int at = random(server).nextInt(players.size());
        return new Result(at + 1).say("pickplayer", "result", players.get(at).getGameProfile().name());
    }

    public static Result pickTeam(MinecraftServer server, @Nullable String team) {
        Scoreboard board = Scores.board(server);
        List<String> names = new ArrayList<>();
        if (team == null) { names.addAll(board.getTeamNames()); }
        else if (Scores.team(board, team) == null) { return Result.refused("noteam", "team", team); }
        else {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (team.equals(ContentTeams.standingOf(player))) { names.add(player.getGameProfile().name()); }
            }
        }
        if (names.isEmpty()) { return Result.refused("nobody"); }
        int at = random(server).nextInt(names.size());
        return new Result(at + 1).say(team == null ? "pickteam" : "pickmember", "team", team, "result", names.get(at));
    }

    public static Result teamroll(MinecraftServer server, CommandSourceStack source, String roll) {
        int[] spec = parse(roll);
        if (spec == null) { return Result.refused("badroll", "roll", roll); }
        List<String> members = new ArrayList<>();
        for (ServerPlayer mate : teammates(server, source)) { members.add(mate.getGameProfile().name()); }
        if (members.isEmpty()) { members.add(source.getTextName()); }
        RandomSource random = random(server);
        Result result = new Result(Integer.MIN_VALUE);
        List<String> leading = new ArrayList<>();
        for (String member : members) {
            int rolled = sum(throwOf(random, spec), spec);
            result.say("teamroll", "member", member, "dice", new Spell(spec[0], spec[1], spec[2]), "result", rolled);
            if (rolled > result.value) {
                result.value = rolled;
                leading.clear();
            }
            if (rolled == result.value) { leading.add(member); }
        }
        return result.say("teamrollwin", "result", leading.get(random.nextInt(leading.size())), "score", result.value);
    }

    public static Result tiebreak(MinecraftServer server, @Nullable String objective) {
        Scoreboard board = Scores.board(server);
        String name = objective == null ? tieObjective() : objective;
        Objective held = name == null ? null : Scores.objective(board, name);
        if (held == null) { return Result.refused("noobjective", "objective", name == null ? "-" : name); }
        List<Scores.Row> top = ContentScoringTie.top(Scores.rows(board, held));
        if (top.size() < 2) { return Result.refused("notie", "objective", name); }
        Scores.Row drawn = top.get(random(server).nextInt(top.size()));
        return new Result(drawn.value()).say("tiebreak", "objective", name, "sides", String.join(", ", ContentScoringTie.names(top)), "result", drawn.owner());
    }

    @Nullable private static String tieObjective() {
        for (ScoreDef def : ContentScoring.all().values()) {
            if (def.tiebreak()) { return def.name(); }
        }
        return ContentScoring.any() ? ContentScoring.all().keySet().iterator().next() : null;
    }

    private static List<ServerPlayer> teammates(MinecraftServer server, CommandSourceStack source) {
        List<ServerPlayer> mates = new ArrayList<>();
        ServerPlayer sender = source.getPlayer();
        String side = sender == null ? null : ContentTeams.standingOf(sender);
        if (side == null) { return mates; }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (side.equals(ContentTeams.standingOf(player))) { mates.add(player); }
        }
        return mates;
    }

    public static boolean keeps(MinecraftServer server, String objective) { return Scores.objective(Scores.board(server), objective) != null; }

    public static void announce(MinecraftServer server, CommandSourceStack source, String command, Result result, Audience audience, @Nullable String store) {
        String who = source.getTextName();
        String english = text(null, result, who);
        ContentLog.LOGGER.info("{} ran /rdplserver game {} and got {}", who, command, english);
        RECENT.addFirst(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " " + english);
        while (RECENT.size() > KEPT) { RECENT.removeLast(); }
        if (store != null) {
            Scoreboard board = Scores.board(server);
            Objective objective = Scores.objective(board, store);
            if (objective != null) { Scores.set(board, who, objective, result.value); }
        }
        if (SILENT.equals(audience.mode)) { return; }
        for (ServerPlayer hearer : hearers(server, source, audience)) {
            for (Line line : result.lines) { Says.line(hearer, ChatFormatting.YELLOW, said(hearer, line, who)); }
        }
        if (source.getPlayer() != null) { return; }
        for (Line line : result.lines) { source.sendSystemMessage(Component.literal(said(null, line, who)).withStyle(ChatFormatting.YELLOW)); }
    }

    public static void byItem(ServerPlayer player, String rolls) {
        MinecraftServer server = player.level().getServer();
        Result result = named(server, rolls);
        if (result.refused) { Says.line(player, ChatFormatting.RED, text(player, result, player.getGameProfile().name())); }
        else { announce(server, player.createCommandSourceStack(), "item " + rolls, result, heard, null); }
    }

    private static List<ServerPlayer> hearers(MinecraftServer server, CommandSourceStack source, Audience audience) {
        List<ServerPlayer> hearers = new ArrayList<>();
        if (ALL.equals(audience.mode)) { return new ArrayList<>(server.getPlayerList().getPlayers()); }
        if (TEAM.equals(audience.mode)) { hearers.addAll(teammates(server, source)); }
        if (RADIUS.equals(audience.mode)) {
            Vec3 at = source.getPosition();
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.level() == source.getLevel() && player.distanceToSqr(at) <= (double) audience.radius * audience.radius) { hearers.add(player); }
            }
        }
        if (hearers.isEmpty() && source.getPlayer() != null) { hearers.add(source.getPlayer()); }
        return hearers;
    }

    public static String text(@Nullable ServerPlayer viewer, Result result, String who) {
        List<String> said = new ArrayList<>();
        for (Line line : result.lines) { said.add(said(viewer, line, who)); }
        return String.join(" / ", said);
    }

    private static String said(@Nullable ServerPlayer viewer, Line line, String who) { return words(viewer, line.key, line.pairs).replace("{player}", who); }

    public static String words(@Nullable ServerPlayer viewer, String key, Object... pairs) {
        String text = SAYS.containsKey(key) ? SAYS.get(key) : viewer == null ? Lang.tr("rdpl.game." + key) : Lang.tr(viewer, "rdpl.game." + key);
        for (int at = 0; at + 1 < pairs.length; at += 2) {
            Object value = pairs[at + 1];
            text = text.replace("{" + pairs[at] + "}", value instanceof Term(String term) ? words(viewer, term) : value instanceof Spell spell ? spelled(viewer, spell) : String.valueOf(value));
        }
        return text;
    }

    private static String spelled(@Nullable ServerPlayer viewer, Spell spell) {
        String sided = SIDE_WORDS.contains(spell.sides) ? words(viewer, "sides." + spell.sides) : words(viewer, "sides.n", "n", spell.sides);
        String digits = String.valueOf(spell.sides);
        String one = digits.startsWith("8") || "11".equals(digits) || "18".equals(digits) ? "spell.an" : "spell.one";
        String dice = spell.count == 1 ? words(viewer, one, "sides", sided) : words(viewer, "spell.many", "count", spell.count, "sides", sided);
        return spell.shift == 0 ? dice : words(viewer, spell.shift > 0 ? "spell.plus" : "spell.minus", "dice", dice, "n", Math.abs(spell.shift));
    }

    public record Audience(String mode, int radius) {
        @Nullable public static Audience parse(String text) {
            String[] parts = text.trim().split("\\s+");
            if (RADIUS.equals(parts[0])) {
                if (parts.length != 2) { return null; }
                try { return new Audience(RADIUS, Math.max(1, Integer.parseInt(parts[1]))); }
                catch (NumberFormatException notNumber) { return null; }
            }
            return parts.length == 1 && AUDIENCES.contains(parts[0]) ? new Audience(parts[0], 0) : null;
        }
    }

    private record Term(String key) {}

    private record Spell(int count, int sides, int shift) {}

    private record Line(String key, Object[] pairs) {}

    public static final class Result {
        private final List<Line> lines = new ArrayList<>();
        private int value;
        private boolean refused;

        Result(int value) { this.value = value; }

        static Result refused(String key, Object... pairs) {
            Result result = new Result(0).say(key, pairs);
            result.refused = true;
            return result;
        }

        Result say(String key, Object... pairs) {
            lines.add(new Line(key, pairs));
            return this;
        }

        public boolean refused() { return refused; }
    }
}
