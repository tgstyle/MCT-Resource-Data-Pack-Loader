package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

final class CommandScoreboard {
    private static final String BELOW_NAME = "below_name";
    private static final Map<String, String> TEAM_OPTIONS = Map.of("color", "color", "friendlyfire", "friendlyFire", "seefriendlyinvisibles", "seeFriendlyInvisibles", "nametagvisibility", "nametagVisibility", "deathmessagevisibility", "deathMessageVisibility", "collisionrule", "collisionRule");

    private CommandScoreboard() {}

    static String scoreboard(String[] args, Ported pack) {
        Commands.need(args, 3);
        String action = args[2].toLowerCase(Locale.ROOT);
        return switch (args[1].toLowerCase(Locale.ROOT)) {
            case "objectives" -> objectives(args, action);
            case "players" -> players(args, action, pack);
            case "teams" -> teams(args, action, pack);
            default -> throw new Commands.Kept("scoreboard has no group named " + args[1]);
        };
    }

    private static String objectives(String[] args, String action) {
        return switch (action) {
            case "list" -> "scoreboard objectives list";
            case "add" -> "scoreboard objectives add " + Commands.at(args, 3) + " " + criterion(Commands.at(args, 4)) + (args.length > 5 ? " " + Commands.GSON.toJson(new JsonPrimitive(Commands.rest(args, 5))) : "");
            case "remove" -> "scoreboard objectives remove " + Commands.at(args, 3);
            case "setdisplay" -> "scoreboard objectives setdisplay " + ("belowname".equalsIgnoreCase(Commands.at(args, 3)) ? BELOW_NAME : args[3]) + (args.length > 4 ? " " + args[4] : "");
            default -> throw new Commands.Kept("scoreboard objectives has no action named " + args[2]);
        };
    }

    private static String criterion(String given) {
        if (given.startsWith("achievement.")) { throw new Commands.Kept("achievements became advancements, which keep no score"); }
        if (!given.startsWith("stat.")) { return given; }
        String found = Ids.criterion(given);
        if (found.equals(given)) { throw new Commands.Kept("the statistic " + given + " has no twin on this version"); }
        return found;
    }

    private static String players(String[] args, String action, Ported pack) {
        return switch (action) {
            case "list" -> "scoreboard players list" + (args.length > 3 ? " " + CommandSelectors.target(args[3], pack) : "");
            case "set", "add", "remove" -> {
                Commands.need(args, 6);
                String who = CommandSelectors.target(args[3], pack);
                String score = args[4] + " " + Commands.number(args[5]);
                if (args.length > 6) { yield "execute as " + who + " if entity " + Commands.SELF + "[nbt=" + CommandStacks.nbt(Commands.rest(args, 6)) + "] run scoreboard players " + action + " " + Commands.SELF + " " + score; }
                yield "scoreboard players " + action + " " + who + " " + score;
            }
            case "reset" -> "scoreboard players reset " + CommandSelectors.target(Commands.at(args, 3), pack) + (args.length > 4 ? " " + args[4] : "");
            case "enable" -> "scoreboard players enable " + CommandSelectors.target(Commands.at(args, 3), pack) + " " + Commands.at(args, 4);
            case "test" -> "execute if score " + CommandSelectors.target(Commands.at(args, 3), pack) + " " + Commands.at(args, 4) + " matches " + tested(Commands.at(args, 5), Commands.arg(args, 6));
            case "operation" -> "scoreboard players operation " + CommandSelectors.target(Commands.at(args, 3), pack) + " " + Commands.at(args, 4) + " " + Commands.at(args, 5) + " " + CommandSelectors.target(Commands.at(args, 6), pack) + " " + Commands.at(args, 7);
            case "tag" -> {
                Commands.need(args, 5);
                String who = CommandSelectors.target(args[3], pack);
                if ("list".equals(args[4])) { yield "tag " + who + " list"; }
                Commands.need(args, 6);
                if (args.length > 6) { who = CommandSelectors.narrowed(who, "nbt=" + CommandStacks.nbt(Commands.rest(args, 6))); }
                yield "tag " + who + " " + args[4] + " " + args[5];
            }
            default -> throw new Commands.Kept("scoreboard players has no action named " + args[2]);
        };
    }

    private static String tested(String least, @Nullable String most) {
        String low = "*".equals(least) ? null : String.valueOf(Commands.number(least));
        String high = most == null || "*".equals(most) ? null : String.valueOf(Commands.number(most));
        if (low == null && high == null) { return Integer.MIN_VALUE + ".."; }
        return CommandSelectors.bounds(low, high);
    }

    private static String teams(String[] args, String action, Ported pack) {
        return switch (action) {
            case "list" -> "team list" + (args.length > 3 ? " " + args[3] : "");
            case "add" -> "team add " + Commands.at(args, 3) + (args.length > 4 ? " " + Commands.GSON.toJson(new JsonPrimitive(Commands.rest(args, 4))) : "");
            case "remove", "empty" -> "team " + action + " " + Commands.at(args, 3);
            case "join" -> {
                Commands.need(args, 4);
                if (args.length == 4) { yield "team join " + args[3]; }
                List<String> lines = new ArrayList<>();
                for (int i = 4; i < args.length; i++) { lines.add("team join " + args[3] + " " + CommandSelectors.target(args[i], pack)); }
                yield String.join("\n", lines);
            }
            case "leave" -> {
                if (args.length == 3) { yield "team leave " + Commands.SELF; }
                List<String> lines = new ArrayList<>();
                for (int i = 3; i < args.length; i++) { lines.add("team leave " + CommandSelectors.target(args[i], pack)); }
                yield String.join("\n", lines);
            }
            case "option" -> "team modify " + Commands.at(args, 3) + " " + Commands.named(TEAM_OPTIONS, Commands.at(args, 4), "team option") + " " + Commands.at(args, 5);
            default -> throw new Commands.Kept("scoreboard teams has no action named " + args[2]);
        };
    }
}
