package mctmods.resourcedatapackloader.command;

import mctmods.resourcedatapackloader.content.ContentDice;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.util.Config;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

final class GameCommand {
    private static final String NAME = "rdplserver";
    private static final String DEFAULT_ROLL = "1d20";
    private static final int MOST_SHOWN = 50;
    private static final List<String> ROLLS = Arrays.asList("coin", "die", "dice", "advantage", "disadvantage", "pick", "deck", "teamroll", "tiebreak", "last");
    private static final List<String> DECK_ACTIONS = Arrays.asList("draw", "shuffle", "left");
    private static final List<String> PICKS = Arrays.asList("player", "team");
    private static final List<String> OPTIONS = Arrays.asList("store", "audience");
    private static final List<String> SAMPLE_ROLLS = Arrays.asList("1d20", "2d6", "1d6+1");
    private static final Map<String, Integer> LEVELS = new LinkedHashMap<>();
    static {
        for (String roll : ROLLS) { LEVELS.put(roll, 0); }
        LEVELS.remove("deck");
        for (String action : DECK_ACTIONS) { LEVELS.put("deck " + action, 0); }
        LEVELS.put("deck shuffle", 2);
        LEVELS.put("tiebreak", 2);
    }

    private GameCommand() {}

    private static int level(String sub) { return ServerCommands.listedLevel("gameLevels", Config.commands.gameLevels, sub, LEVELS.getOrDefault(sub, 0)); }

    static int lowest() {
        int lowest = ServerCommands.OPERATOR;
        for (String sub : LEVELS.keySet()) { lowest = Math.min(lowest, level(sub)); }
        return lowest;
    }

    private static String sub(String[] args) { return args.length > 2 && "deck".equals(args[1]) ? "deck " + args[2] : args[1]; }

    @Nullable private static EntityPlayer viewer(ICommandSender sender) { return sender instanceof EntityPlayer ? (EntityPlayer) sender : null; }

    private static CommandException refused(ICommandSender sender, String key, Object... pairs) { return new CommandException(ContentDice.words(viewer(sender), key, pairs)); }

    static void run(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2 || !ROLLS.contains(args[1])) { throw refused(sender, "usage"); }
        if (!sender.canUseCommand(level(sub(args)), NAME)) { throw refused(sender, "notallowed"); }
        List<String> plain = new ArrayList<>();
        String store = null;
        ContentDice.Audience audience = ContentDice.fallback();
        for (int at = 2; at < args.length; at++) {
            if ("store".equals(args[at]) && at + 1 < args.length) {
                store = args[++at];
                continue;
            }
            if ("audience".equals(args[at]) && at + 1 < args.length) {
                String asked = args[++at];
                if (ContentDice.RADIUS.equals(asked) && at + 1 < args.length) { asked = asked + " " + args[++at]; }
                audience = ContentDice.Audience.parse(asked);
                if (audience == null) { throw refused(sender, "badaudience", "audience", asked); }
                continue;
            }
            plain.add(args[at]);
        }
        if ("last".equals(args[1])) {
            last(sender, plain);
            return;
        }
        if (store != null && !ContentDice.keeps(server, store)) { throw refused(sender, "noobjective", "objective", store); }
        ContentDice.Result result = roll(server, sender, args[1], plain);
        if (result == null) { throw refused(sender, "usage"); }
        if (result.refused()) { throw new CommandException(ContentDice.text(viewer(sender), result, sender.getName())); }
        ContentDice.announce(server, sender, String.join(" ", Arrays.copyOfRange(args, 1, args.length)), result, audience, store);
    }

    @Nullable private static ContentDice.Result roll(MinecraftServer server, ICommandSender sender, String kind, List<String> plain) {
        Random random = ContentDice.random(server);
        String first = plain.isEmpty() ? null : plain.get(0);
        switch (kind) {
            case "coin": return ContentDice.coin(random);
            case "die": return first == null ? null : first.matches("\\d{1,9}") ? ContentDice.die(random, Integer.parseInt(first)) : ContentDice.packDie(random, first);
            case "dice": return first == null ? null : ContentDice.dice(random, first);
            case "advantage": return ContentDice.edge(random, first == null ? DEFAULT_ROLL : first, true);
            case "disadvantage": return ContentDice.edge(random, first == null ? DEFAULT_ROLL : first, false);
            case "pick": return pick(server, plain);
            case "deck": return deck(server, plain);
            case "teamroll": return ContentDice.teamroll(server, sender, first == null ? DEFAULT_ROLL : first);
            default: return ContentDice.tiebreak(server, first);
        }
    }

    @Nullable private static ContentDice.Result pick(MinecraftServer server, List<String> plain) {
        if (!plain.isEmpty() && "player".equals(plain.get(0))) { return ContentDice.pickPlayer(server); }
        if (!plain.isEmpty() && "team".equals(plain.get(0))) { return ContentDice.pickTeam(server, plain.size() > 1 ? plain.get(1) : null); }
        return null;
    }

    @Nullable private static ContentDice.Result deck(MinecraftServer server, List<String> plain) {
        if (plain.size() != 2) { return null; }
        switch (plain.get(0)) {
            case "draw": return ContentDice.draw(server.getWorld(0), plain.get(1));
            case "shuffle": return ContentDice.shuffle(server.getWorld(0), plain.get(1));
            case "left": return ContentDice.left(server.getWorld(0), plain.get(1));
            default: return null;
        }
    }

    private static void last(ICommandSender sender, List<String> plain) throws CommandException {
        int count = 10;
        if (!plain.isEmpty()) {
            try { count = Math.max(1, Math.min(MOST_SHOWN, Integer.parseInt(plain.get(0)))); }
            catch (NumberFormatException notNumber) { throw refused(sender, "usage"); }
        }
        List<String> recent = ContentDice.recent(count);
        if (recent.isEmpty()) { CommandShared.send(sender, TextFormatting.GRAY, ContentDice.words(viewer(sender), "lastnone")); }
        for (String line : recent) { CommandShared.send(sender, TextFormatting.GRAY, line); }
    }

    static List<String> complete(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> open = new ArrayList<>();
            for (String roll : ROLLS) {
                if (sender.canUseCommand(lowestOf(roll), NAME)) { open.add(roll); }
            }
            return CommandBase.getListOfStringsMatchingLastWord(args, open);
        }
        String before = args[args.length - 2];
        if ("store".equals(before)) {
            List<String> names = new ArrayList<>();
            for (ScoreObjective objective : server.getWorld(0).getScoreboard().getScoreObjectives()) { names.add(objective.getName()); }
            return CommandBase.getListOfStringsMatchingLastWord(args, names);
        }
        if ("audience".equals(before)) { return CommandBase.getListOfStringsMatchingLastWord(args, ContentDice.AUDIENCES); }
        if (args.length == 3) {
            switch (args[1]) {
                case "die":
                    List<String> dice = new ArrayList<>(Arrays.asList("6", "20"));
                    dice.addAll(ContentDice.dieNames());
                    return CommandBase.getListOfStringsMatchingLastWord(args, dice);
                case "dice": case "advantage": case "disadvantage": case "teamroll": return CommandBase.getListOfStringsMatchingLastWord(args, SAMPLE_ROLLS);
                case "pick": return CommandBase.getListOfStringsMatchingLastWord(args, PICKS);
                case "deck": return CommandBase.getListOfStringsMatchingLastWord(args, DECK_ACTIONS);
                case "tiebreak": return CommandBase.getListOfStringsMatchingLastWord(args, new ArrayList<>(ContentScoring.all().keySet()));
                default: break;
            }
        }
        if (args.length == 4 && "deck".equals(args[1])) { return CommandBase.getListOfStringsMatchingLastWord(args, ContentDice.deckNames()); }
        if (args.length == 4 && "pick".equals(args[1]) && "team".equals(args[2])) { return CommandBase.getListOfStringsMatchingLastWord(args, new ArrayList<>(server.getWorld(0).getScoreboard().getTeamNames())); }
        return CommandBase.getListOfStringsMatchingLastWord(args, OPTIONS);
    }

    private static int lowestOf(String roll) {
        if (!"deck".equals(roll)) { return level(roll); }
        int lowest = ServerCommands.OPERATOR;
        for (String action : DECK_ACTIONS) { lowest = Math.min(lowest, level("deck " + action)); }
        return lowest;
    }
}
