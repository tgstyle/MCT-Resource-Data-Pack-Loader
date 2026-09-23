package mctmods.resourcedatapackloader.pack.port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class CommandScoreboard {
    private static final Map<String, String> TEAM_OPTIONS = new HashMap<>();

    static {
        TEAM_OPTIONS.put("color", "color");
        TEAM_OPTIONS.put("friendlyFire", "friendlyfire");
        TEAM_OPTIONS.put("seeFriendlyInvisibles", "seeFriendlyInvisibles");
        TEAM_OPTIONS.put("nametagVisibility", "nametagVisibility");
        TEAM_OPTIONS.put("deathMessageVisibility", "deathMessageVisibility");
        TEAM_OPTIONS.put("collisionRule", "collisionRule");
    }

    private CommandScoreboard() {}

    static String scoreboard(List<String> args, Ported pack) {
        String group = Commands.at(args, 1);
        String action = Commands.at(args, 2);
        if ("objectives".equals(group)) {
            switch (action) {
                case "add": {
                    String criterion = Commands.at(args, 4);
                    if (criterion.contains(":")) { throw new Commands.Kept("the statistic criterion " + criterion + " has no twin the port can write on 1.12.2"); }
                    String display = args.size() > 5 ? Commands.plainText(Commands.join(args, 5)) : "";
                    return "scoreboard objectives add " + Commands.at(args, 3) + " " + criterion + (display.isEmpty() ? "" : " " + display);
                }
                case "setdisplay": return "scoreboard objectives setdisplay " + ("below_name".equals(Commands.at(args, 3)) ? "belowName" : args.get(3)) + (args.size() > 4 ? " " + args.get(4) : "");
                case "modify": throw new Commands.Kept("scoreboard objectives modify has no twin on 1.12.2");
                default: return Commands.join(args, 0);
            }
        }
        if (!"players".equals(group)) { throw new Commands.Kept("scoreboard has no group named " + group); }
        switch (action) {
            case "get": throw new Commands.Kept("scoreboard players get has no twin on 1.12.2");
            case "display": throw new Commands.Kept("scoreboard players display has no twin on 1.12.2");
            case "operation": return "scoreboard players operation " + CommandSelectors.target(Commands.at(args, 3), pack) + " " + Commands.at(args, 4) + " " + Commands.at(args, 5) + " " + CommandSelectors.target(Commands.at(args, 6), pack) + " " + Commands.at(args, 7);
            default: {
                List<String> out = new ArrayList<>(args);
                if (out.size() > 3) { out.set(3, CommandSelectors.target(out.get(3), pack)); }
                return String.join(" ", out);
            }
        }
    }

    static String tag(List<String> args, Ported pack) {
        String who = CommandSelectors.target(Commands.at(args, 1), pack);
        String action = Commands.at(args, 2);
        if ("list".equals(action)) { return "scoreboard players tag " + who + " list"; }
        return "scoreboard players tag " + who + " " + action + " " + Commands.at(args, 3);
    }

    static String team(List<String> args, Ported pack) {
        String action = Commands.at(args, 1);
        switch (action) {
            case "add": return "scoreboard teams add " + Commands.at(args, 2) + (args.size() > 3 ? " " + Commands.plainText(Commands.join(args, 3)) : "");
            case "remove":
            case "empty": return "scoreboard teams " + action + " " + Commands.at(args, 2);
            case "list": return "scoreboard teams list" + (args.size() > 2 ? " " + args.get(2) : "");
            case "join": return "scoreboard teams join " + Commands.at(args, 2) + (args.size() > 3 ? " " + CommandSelectors.target(args.get(3), pack) : "");
            case "leave": return "scoreboard teams leave " + CommandSelectors.target(Commands.at(args, 2), pack);
            case "modify": {
                String option = TEAM_OPTIONS.get(Commands.at(args, 3));
                if (option == null) { throw new Commands.Kept("the team option " + args.get(3) + " has no twin on 1.12.2"); }
                return "scoreboard teams option " + Commands.at(args, 2) + " " + option + " " + Commands.at(args, 4);
            }
            default: throw new Commands.Kept("team has no action named " + action);
        }
    }
}
