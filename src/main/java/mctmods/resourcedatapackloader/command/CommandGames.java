package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.ContentDice;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.ContentTurns;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Scores;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

final class CommandGames {
    private static final String DEFAULT_ROLL = "1d20";
    private static final int MOST_SHOWN = 50;
    private static final List<String> ROLLS = List.of("coin", "die", "dice", "advantage", "disadvantage", "pick", "deck", "teamroll", "tiebreak", "last", "pass", "board");
    private static final List<String> DECK_ACTIONS = List.of("draw", "shuffle", "left");
    private static final List<String> PICKS = List.of("player", "team");
    private static final List<String> OPTIONS = List.of("store", "audience");
    private static final List<String> SAMPLE_ROLLS = List.of("1d20", "2d6", "1d6+1");
    private static final Map<String, Integer> LEVELS = new LinkedHashMap<>();
    static {
        for (String roll : ROLLS) { LEVELS.put(roll, 0); }
        LEVELS.remove("deck");
        for (String action : DECK_ACTIONS) { LEVELS.put("deck " + action, 0); }
        LEVELS.put("deck shuffle", 2);
        LEVELS.put("tiebreak", 2);
        LEVELS.remove("board");
        for (String action : CommandBoard.ACTIONS) { LEVELS.put("board " + action, 0); }
        LEVELS.put("board start", 2);
        LEVELS.put("board end", 2);
        LEVELS.put("board ai", 2);
    }

    private CommandGames() {}

    private static int level(String sub) { return ContentStructureSearch.listedLevel("gameLevels", Config.commands.gameLevels(), sub, LEVELS.getOrDefault(sub, 0)); }

    private static int lowest() {
        int lowest = CommandShared.OPERATOR;
        for (String sub : LEVELS.keySet()) { lowest = Math.min(lowest, level(sub)); }
        return lowest;
    }

    static LiteralArgumentBuilder<CommandSourceStack> game(String name) {
        return Commands.literal("game").requires(source -> source.hasPermission(lowest()))
                .then(Commands.argument("roll", StringArgumentType.greedyString()).suggests((context, suggestions) -> suggest(context.getSource(), suggestions))
                        .executes(context -> run(context.getSource(), name, StringArgumentType.getString(context, "roll"))));
    }

    private static String sub(String[] args) { return args.length > 1 && ("deck".equals(args[0]) || "board".equals(args[0])) ? args[0] + " " + args[1] : args[0]; }

    private static int refuse(CommandSourceStack source, String text) {
        source.sendFailure(Component.literal(text));
        return 0;
    }

    private static int run(CommandSourceStack source, String name, String line) {
        CommandShared.ran(source, name, "game " + line);
        String[] args = line.trim().split("\\s+");
        if (!ROLLS.contains(args[0])) { return refuse(source, ContentDice.words(source.getPlayer(), "usage")); }
        if (!source.hasPermission(level(sub(args)))) { return refuse(source, ContentDice.words(source.getPlayer(), "notallowed")); }
        if ("board".equals(args[0])) { return CommandBoard.run(source, args); }
        MinecraftServer server = source.getServer();
        if ("pass".equals(args[0])) {
            String refusal = ContentTurns.pass(server, source.getPlayer());
            return refusal == null ? 1 : refuse(source, ContentDice.words(source.getPlayer(), refusal));
        }
        List<String> plain = new ArrayList<>();
        String store = null;
        ContentDice.Audience audience = ContentDice.fallback();
        for (int at = 1; at < args.length; at++) {
            if ("store".equals(args[at]) && at + 1 < args.length) {
                store = args[++at];
                continue;
            }
            if ("audience".equals(args[at]) && at + 1 < args.length) {
                String asked = args[++at];
                if (ContentDice.RADIUS.equals(asked) && at + 1 < args.length) { asked = asked + " " + args[++at]; }
                audience = ContentDice.Audience.parse(asked);
                if (audience == null) { return refuse(source, ContentDice.words(source.getPlayer(), "badaudience", "audience", asked)); }
                continue;
            }
            plain.add(args[at]);
        }
        if ("last".equals(args[0])) { return last(source, plain); }
        if (store != null && !ContentDice.keeps(server, store)) { return refuse(source, ContentDice.words(source.getPlayer(), "noobjective", "objective", store)); }
        ContentDice.Result result = roll(server, source, args[0], plain);
        if (result == null) { return refuse(source, ContentDice.words(source.getPlayer(), "usage")); }
        if (result.refused()) { return refuse(source, ContentDice.text(source.getPlayer(), result, source.getTextName())); }
        ContentDice.announce(server, source, line.trim(), result, audience, store);
        return 1;
    }

    @Nullable private static ContentDice.Result roll(MinecraftServer server, CommandSourceStack source, String kind, List<String> plain) {
        RandomSource random = ContentDice.random(server);
        String first = plain.isEmpty() ? null : plain.getFirst();
        return switch (kind) {
            case "coin" -> ContentDice.coin(random);
            case "die" -> first == null ? null : first.matches("\\d{1,9}") ? ContentDice.die(random, Integer.parseInt(first)) : ContentDice.packDie(random, first);
            case "dice" -> first == null ? null : ContentDice.dice(random, first);
            case "advantage" -> ContentDice.edge(random, first == null ? DEFAULT_ROLL : first, true);
            case "disadvantage" -> ContentDice.edge(random, first == null ? DEFAULT_ROLL : first, false);
            case "pick" -> pick(server, plain);
            case "deck" -> deck(server, plain);
            case "teamroll" -> ContentDice.teamroll(server, source, first == null ? DEFAULT_ROLL : first);
            default -> ContentDice.tiebreak(server, first);
        };
    }

    @Nullable private static ContentDice.Result pick(MinecraftServer server, List<String> plain) {
        if (!plain.isEmpty() && "player".equals(plain.get(0))) { return ContentDice.pickPlayer(server); }
        if (!plain.isEmpty() && "team".equals(plain.get(0))) { return ContentDice.pickTeam(server, plain.size() > 1 ? plain.get(1) : null); }
        return null;
    }

    @Nullable private static ContentDice.Result deck(MinecraftServer server, List<String> plain) {
        if (plain.size() != 2) { return null; }
        return switch (plain.get(0)) {
            case "draw" -> ContentDice.draw(server, plain.get(1));
            case "shuffle" -> ContentDice.shuffle(server, plain.get(1));
            case "left" -> ContentDice.left(server, plain.get(1));
            default -> null;
        };
    }

    private static int last(CommandSourceStack source, List<String> plain) {
        int count = 10;
        if (!plain.isEmpty()) {
            try { count = Math.clamp(Integer.parseInt(plain.getFirst()), 1, MOST_SHOWN); }
            catch (NumberFormatException notNumber) { return refuse(source, ContentDice.words(source.getPlayer(), "usage")); }
        }
        List<String> recent = ContentDice.recent(count);
        if (recent.isEmpty()) { CommandShared.send(source, ChatFormatting.GRAY, Component.literal(ContentDice.words(source.getPlayer(), "lastnone"))); }
        for (String said : recent) { CommandShared.send(source, ChatFormatting.GRAY, Component.literal(said)); }
        return 1;
    }

    private static CompletableFuture<Suggestions> suggest(CommandSourceStack source, SuggestionsBuilder builder) {
        String typed = builder.getRemaining();
        int cut = typed.lastIndexOf(' ') + 1;
        List<String> words = new ArrayList<>(Arrays.asList(typed.substring(0, cut).trim().split("\\s+")));
        words.removeIf(String::isEmpty);
        return SharedSuggestionProvider.suggest(offered(source, words), builder.createOffset(builder.getStart() + cut));
    }

    private static List<String> offered(CommandSourceStack source, List<String> words) {
        if (words.isEmpty()) {
            List<String> open = new ArrayList<>();
            for (String roll : ROLLS) {
                if (source.hasPermission(lowestOf(roll))) { open.add(roll); }
            }
            return open;
        }
        if ("board".equals(words.getFirst())) { return CommandBoard.offered(source.getServer(), words); }
        String before = words.getLast();
        if ("store".equals(before)) { return new ArrayList<>(Scores.board(source.getServer()).getObjectiveNames()); }
        if ("audience".equals(before)) { return ContentDice.AUDIENCES; }
        if (words.size() == 1) {
            switch (words.getFirst()) {
                case "die" -> {
                    List<String> dice = new ArrayList<>(List.of("6", "20"));
                    dice.addAll(ContentDice.dieNames());
                    return dice;
                }
                case "dice", "advantage", "disadvantage", "teamroll" -> { return SAMPLE_ROLLS; }
                case "pick" -> { return PICKS; }
                case "deck" -> { return DECK_ACTIONS; }
                case "tiebreak" -> { return new ArrayList<>(ContentScoring.all().keySet()); }
                default -> { }
            }
        }
        if (words.size() == 2 && "deck".equals(words.get(0))) { return ContentDice.deckNames(); }
        if (words.size() == 2 && "pick".equals(words.get(0)) && "team".equals(words.get(1))) { return new ArrayList<>(Scores.board(source.getServer()).getTeamNames()); }
        return OPTIONS;
    }

    private static int lowestOf(String roll) {
        if (!"deck".equals(roll) && !"board".equals(roll)) { return level(roll); }
        int lowest = CommandShared.OPERATOR;
        for (String action : "deck".equals(roll) ? DECK_ACTIONS : CommandBoard.ACTIONS) { lowest = Math.min(lowest, level(roll + " " + action)); }
        return lowest;
    }
}
