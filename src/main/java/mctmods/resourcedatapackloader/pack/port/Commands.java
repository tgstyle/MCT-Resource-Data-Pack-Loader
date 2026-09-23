package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

final class Commands {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private static final String SELF = "@s";
    private static final Set<String> SIMPLE = new HashSet<>(Arrays.asList("say", "me", "msg", "tell", "w", "kill", "time", "seed", "list", "help", "stop", "save-all", "save-on", "save-off", "op", "deop", "ban", "ban-ip", "pardon", "pardon-ip", "whitelist", "kick", "worldborder", "setidletimeout", "publish", "debug", "reload", "trigger", "defaultgamemode", "difficulty", "gamemode", "spawnpoint", "setworldspawn", "advancement", "recipe"));
    private static final Set<String> NO_TWIN = new HashSet<>(Arrays.asList("attribute", "bossbar", "datapack", "forceload", "item", "jfr", "loot", "perf", "place", "random", "return", "ride", "schedule", "spectate", "teammsg", "tm", "fillbiome", "damage", "tick", "transfer", "rotate", "dialog", "waypoint", "test", "stopwatch", "version"));
    private static final Set<String> RULES = new HashSet<>(Arrays.asList("announceAdvancements", "commandBlockOutput", "disableElytraMovementCheck", "doDaylightCycle", "doEntityDrops", "doFireTick", "doLimitedCrafting", "doMobLoot", "doMobSpawning", "doTileDrops", "doWeatherCycle", "gameLoopFunction", "keepInventory", "logAdminCommands", "maxCommandChainLength", "maxEntityCramming", "mobGriefing", "naturalRegeneration", "randomTickSpeed", "reducedDebugInfo", "sendCommandFeedback", "showDeathMessages", "spawnRadius", "spectatorsGenerateChunks"));
    private static final Map<String, String> STRUCTURES = new LinkedHashMap<>();

    static {
        STRUCTURES.put("village", "Village");
        STRUCTURES.put("mansion", "Mansion");
        STRUCTURES.put("monument", "Monument");
        STRUCTURES.put("stronghold", "Stronghold");
        STRUCTURES.put("mineshaft", "Mineshaft");
        STRUCTURES.put("end_city", "EndCity");
        STRUCTURES.put("fortress", "Fortress");
        STRUCTURES.put("desert_pyramid", "Temple");
        STRUCTURES.put("jungle_pyramid", "Temple");
        STRUCTURES.put("swamp_hut", "Temple");
        STRUCTURES.put("igloo", "Temple");
    }

    private Commands() {}

    static final class Kept extends RuntimeException {
        Kept(String why) { super(why, null, false, false); }
    }

    static String function(String contents, String from, Ported pack) {
        String[] lines = contents.split("\r?\n", -1);
        StringBuilder out = new StringBuilder();
        int changed = 0;
        int kept = 0;
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) { out.append('\n'); }
            String line = lines[i].trim();
            if (line.isEmpty() || line.startsWith("#")) {
                out.append(lines[i]);
                continue;
            }
            try {
                String converted = command(line.startsWith("/") ? line.substring(1) : line, pack);
                out.append(converted);
                if (!converted.equals(line)) { changed++; }
            }
            catch (Kept why) {
                kept++;
                out.append("# ").append(line);
                pack.note("'" + from + "' line " + (i + 1) + " is turned into a comment, since " + why.getMessage() + ": " + line);
            }
        }
        if (pack.overworldShift() != 0 && changed > 0) { pack.note("'" + from + "': the pack's overworld is a modern flat world, whose floor sits at the bottom of the world, so every absolute y in its functions moves by " + pack.overworldShift() + " to the 1.12.2 flat floor at y 0; a function that runs in another dimension needs its heights checked by hand"); }
        if (changed > 0 || kept > 0) { pack.note("'" + from + "' is a modern function: " + changed + " command(s) rewritten for 1.12.2, " + kept + " turned into comments"); }
        return out.toString();
    }

    private static String command(String line, Ported pack) {
        if (line.startsWith("$")) { throw new Kept("a macro line has no twin on 1.12.2"); }
        List<String> args = tokens(line);
        String head = args.get(0);
        if (NO_TWIN.contains(head)) { throw new Kept("the command " + head + " has no twin on 1.12.2"); }
        switch (head) {
            case "give": return give(args, pack);
            case "clear": return clear(args, pack);
            case "setblock": return setblock(args, pack);
            case "fill": return fill(args, pack);
            case "clone": return clone(args, pack);
            case "execute": return execute(args, pack);
            case "tp":
            case "teleport": return teleport(args, pack);
            case "effect": return effect(args, pack);
            case "enchant": return "enchant " + CommandSelectors.target(at(args, 1), pack) + " " + Ids.enchantment(at(args, 2)) + (args.size() > 3 ? " " + args.get(3) : "");
            case "playsound": return "playsound " + Ids.sound(at(args, 1)) + " " + at(args, 2) + " " + CommandSelectors.target(at(args, 3), pack) + positionTail(args, pack);
            case "stopsound": return "stopsound " + CommandSelectors.target(at(args, 1), pack) + (args.size() > 2 ? " " + args.get(2) : "") + (args.size() > 3 ? " " + Ids.sound(args.get(3)) : "");
            case "particle": return particle(args, pack);
            case "scoreboard": return CommandScoreboard.scoreboard(args, pack);
            case "tag": return CommandScoreboard.tag(args, pack);
            case "team": return CommandScoreboard.team(args, pack);
            case "data": return data(args, pack);
            case "tellraw": return "tellraw " + CommandSelectors.target(at(args, 1), pack) + " " + text(join(args, 2), pack);
            case "title": return title(args, pack);
            case "gamerule":
                if (!RULES.contains(at(args, 1))) { throw new Kept("1.12.2 has no game rule named " + args.get(1)); }
                return join(args, 0);
            case "weather": return "weather " + at(args, 1) + (args.size() > 2 ? " " + seconds(args.get(2)) : "");
            case "summon": return summon(args, pack);
            case "xp":
            case "experience": return experience(args, pack);
            case "locate": return locate(args);
            case "spreadplayers": return spreadplayers(args, pack);
            case "kill": return args.size() > 1 ? "kill " + CommandSelectors.target(args.get(1), pack) : "kill";
            case "function":
                if (at(args, 1).startsWith("#") || args.size() > 2) { throw new Kept("calling a function tag or passing macro arguments has no twin on 1.12.2"); }
                return "function " + Ids.namespaced(args.get(1));
            default:
                if (!SIMPLE.contains(head)) { throw new Kept("the command " + head + " is not one the port knows"); }
                return passed(args, pack);
        }
    }

    private static List<String> tokens(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;
        boolean quoted = false;
        char quote = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quoted) {
                current.append(c);
                if (c == '\\' && i + 1 < line.length()) { current.append(line.charAt(++i)); }
                else if (c == quote) { quoted = false; }
                continue;
            }
            if (c == '"' || c == '\'') {
                quoted = true;
                quote = c;
            }
            if (c == '[' || c == '{') { depth++; }
            if (c == ']' || c == '}') { depth--; }
            if (c == ' ' && depth <= 0) {
                if (current.length() > 0) { out.add(current.toString()); }
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        if (current.length() > 0) { out.add(current.toString()); }
        return out;
    }

    static String at(List<String> args, int index) {
        if (args.size() <= index) { throw new Kept("it has fewer arguments than " + args.get(0) + " needs"); }
        return args.get(index);
    }

    static String join(List<String> args, int from) { return String.join(" ", args.subList(Math.min(from, args.size()), args.size())); }

    private static String passed(List<String> args, Ported pack) {
        List<String> out = new ArrayList<>(args);
        for (int i = 1; i < out.size(); i++) { out.set(i, CommandSelectors.target(out.get(i), pack)); }
        return String.join(" ", out);
    }

    static String height(String token, Ported pack) {
        if (token.startsWith("~") || token.startsWith("^")) {
            if (token.startsWith("^")) { throw new Kept("local ^ coordinates have no twin on 1.12.2"); }
            return token;
        }
        return Convert.shiftY(token, pack.overworldShift());
    }

    private static String position(List<String> args, int from, Ported pack) {
        at(args, from + 2);
        if (args.get(from).startsWith("^") || args.get(from + 2).startsWith("^")) { throw new Kept("local ^ coordinates have no twin on 1.12.2"); }
        return args.get(from) + " " + height(args.get(from + 1), pack) + " " + args.get(from + 2);
    }

    private static String positionTail(List<String> args, Ported pack) {
        if (args.size() <= 4) { return ""; }
        return " " + position(args, 4, pack) + (args.size() > 7 ? " " + join(args, 7) : "");
    }

    private static String seconds(String time) {
        String trimmed = time.trim();
        try {
            char unit = trimmed.isEmpty() ? 't' : trimmed.charAt(trimmed.length() - 1);
            double amount = Double.parseDouble(Character.isDigit(unit) ? trimmed : trimmed.substring(0, trimmed.length() - 1));
            if (unit == 's') { return String.valueOf(Math.round(amount)); }
            if (unit == 'd') { return String.valueOf(Math.round(amount * 1200)); }
            return String.valueOf(Math.max(1, Math.round(amount / 20)));
        }
        catch (NumberFormatException broken) { throw new Kept("'" + time + "' is not a time"); }
    }

    private static String give(List<String> args, Ported pack) {
        CommandStacks.Stack stack = CommandStacks.item(at(args, 2), pack);
        String count = args.size() > 3 ? args.get(3) : "1";
        return "give " + CommandSelectors.target(args.get(1), pack) + " " + stack.id + " " + count + " " + stack.meta + (stack.nbt == null ? "" : " " + stack.nbt);
    }

    private static String clear(List<String> args, Ported pack) {
        if (args.size() < 2) { return "clear"; }
        StringBuilder out = new StringBuilder("clear ").append(CommandSelectors.target(args.get(1), pack));
        if (args.size() < 3) { return out.toString(); }
        CommandStacks.Stack stack = CommandStacks.item(args.get(2), pack);
        String most = args.size() > 3 ? args.get(3) : "-1";
        out.append(' ').append(stack.id).append(' ').append(stack.meta > 0 || Ids.variedItem(stack.id) ? stack.meta : -1).append(' ').append(most);
        if (stack.nbt != null) { out.append(' ').append(stack.nbt); }
        return out.toString();
    }

    private static String setblock(List<String> args, Ported pack) {
        CommandStacks.Stack state = CommandStacks.block(at(args, 4), pack);
        String mode = args.size() > 5 ? args.get(5) : "replace";
        if (!"replace".equals(mode) && !"destroy".equals(mode) && !"keep".equals(mode)) { throw new Kept("the setblock mode " + mode + " has no twin on 1.12.2"); }
        return "setblock " + position(args, 1, pack) + " " + state.id + " " + state.meta + " " + mode + (state.nbt == null ? "" : " " + state.nbt);
    }

    private static String fill(List<String> args, Ported pack) {
        CommandStacks.Stack state = CommandStacks.block(at(args, 7), pack);
        String out = "fill " + position(args, 1, pack) + " " + position(args, 4, pack) + " " + state.id + " " + state.meta;
        if (args.size() <= 8) { return out + (state.nbt == null ? "" : " replace " + state.nbt); }
        String mode = args.get(8);
        if ("replace".equals(mode) && args.size() > 9) {
            CommandStacks.Stack filter = CommandStacks.block(args.get(9), pack);
            return out + " replace " + filter.id + " " + filter.meta;
        }
        return out + " " + mode + (state.nbt == null ? "" : " " + state.nbt);
    }

    private static String clone(List<String> args, Ported pack) {
        String out = "clone " + position(args, 1, pack) + " " + position(args, 4, pack) + " " + position(args, 7, pack);
        if (args.size() <= 10) { return out; }
        if ("filtered".equals(args.get(10))) {
            CommandStacks.Stack filter = CommandStacks.block(at(args, 11), pack);
            return out + " filtered " + (args.size() > 12 ? args.get(12) : "normal") + " " + filter.id + " " + filter.meta;
        }
        return out + " " + join(args, 10);
    }

    private static String teleport(List<String> args, Ported pack) {
        if (args.size() == 2) { return "tp " + CommandSelectors.target(args.get(1), pack); }
        if (args.size() == 3) { return "tp " + CommandSelectors.target(args.get(1), pack) + " " + CommandSelectors.target(args.get(2), pack); }
        if (args.size() == 4) { return "tp " + position(args, 1, pack); }
        if (args.size() >= 5) {
            String rest = args.size() > 5 ? " " + join(args, 5) : "";
            if (rest.contains("facing")) { throw new Kept("teleporting to face something has no twin on 1.12.2"); }
            return "tp " + CommandSelectors.target(args.get(1), pack) + " " + position(args, 2, pack) + rest;
        }
        throw new Kept("tp takes a target and a place");
    }

    private static String effect(List<String> args, Ported pack) {
        String action = at(args, 1);
        if ("clear".equals(action)) {
            String who = args.size() > 2 ? CommandSelectors.target(args.get(2), pack) : SELF;
            return args.size() > 3 ? "effect " + who + " " + Ids.namespaced(args.get(3)) + " 0" : "effect " + who + " clear";
        }
        if (!"give".equals(action)) { throw new Kept("effect takes give or clear"); }
        StringBuilder out = new StringBuilder("effect ").append(CommandSelectors.target(at(args, 2), pack)).append(' ').append(Ids.namespaced(at(args, 3)));
        for (int i = 4; i < args.size(); i++) { out.append(' ').append("infinite".equals(args.get(i)) ? "1000000" : args.get(i)); }
        return out.toString();
    }

    private static String particle(List<String> args, Ported pack) {
        String legacy = Ids.particle(at(args, 1));
        if (legacy == null || "reddust".equals(legacy) || "iconcrack".equals(legacy) || "blockcrack".equals(legacy) || "fallingdust".equals(legacy)) { throw new Kept("the particle " + args.get(1) + " has no twin the port can write on 1.12.2"); }
        if (args.size() < 5) { return "particle " + legacy + " ~ ~ ~ 0 0 0 0"; }
        StringBuilder out = new StringBuilder("particle ").append(legacy).append(' ').append(position(args, 2, pack));
        out.append(' ').append(args.size() > 7 ? String.join(" ", args.subList(5, 8)) : "0 0 0");
        out.append(' ').append(args.size() > 8 ? args.get(8) : "0");
        if (args.size() > 9) { out.append(' ').append(args.get(9)); }
        if (args.size() > 10) { out.append(' ').append(args.get(10)); }
        if (args.size() > 11) { out.append(' ').append(CommandSelectors.target(args.get(11), pack)); }
        return out.toString();
    }


    static String plainText(String json) {
        try {
            JsonElement parsed = lenient(json);
            if (parsed.isJsonPrimitive()) { return parsed.getAsString(); }
            if (parsed.isJsonObject() && parsed.getAsJsonObject().has("text")) { return parsed.getAsJsonObject().get("text").getAsString(); }
            return json;
        }
        catch (JsonParseException notJson) { return json; }
    }

    private static JsonElement lenient(String json) {
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        return new JsonParser().parse(reader);
    }


    private static String data(List<String> args, Ported pack) {
        if (!"merge".equals(at(args, 1))) { throw new Kept("data " + args.get(1) + " has no twin on 1.12.2"); }
        if ("block".equals(at(args, 2))) { return "blockdata " + position(args, 3, pack) + " " + at(args, 6); }
        if ("entity".equals(args.get(2))) { return "entitydata " + CommandSelectors.target(at(args, 3), pack) + " " + at(args, 4); }
        throw new Kept("data merge storage has no twin on 1.12.2");
    }

    private static String title(List<String> args, Ported pack) {
        String who = CommandSelectors.target(at(args, 1), pack);
        String action = at(args, 2);
        if ("title".equals(action) || "subtitle".equals(action) || "actionbar".equals(action)) { return "title " + who + " " + action + " " + text(join(args, 3), pack); }
        return "title " + who + " " + join(args, 2);
    }

    private static String text(String json, Ported pack) {
        JsonElement parsed;
        try { parsed = lenient(json); }
        catch (JsonParseException broken) { throw new Kept("its text " + json + " is not JSON"); }
        textWalk(parsed, pack);
        return GSON.toJson(parsed);
    }

    private static void textWalk(JsonElement element, Ported pack) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { textWalk(inner, pack); }
            return;
        }
        if (!element.isJsonObject()) { return; }
        JsonObject held = element.getAsJsonObject();
        if (held.has("selector") && held.get("selector").isJsonPrimitive()) { held.addProperty("selector", CommandSelectors.target(held.get("selector").getAsString(), pack)); }
        if (held.has("score") && held.get("score").isJsonObject() && held.getAsJsonObject("score").has("name")) { held.getAsJsonObject("score").addProperty("name", CommandSelectors.target(held.getAsJsonObject("score").get("name").getAsString(), pack)); }
        for (String key : new String[] {"clickEvent", "click_event"}) {
            if (!held.has(key) || !held.get(key).isJsonObject()) { continue; }
            JsonObject click = held.remove(key).getAsJsonObject();
            String action = click.has("action") ? click.get("action").getAsString() : "";
            String value = click.has("value") ? click.get("value").getAsString() : click.has("command") ? click.get("command").getAsString() : "";
            JsonObject out = new JsonObject();
            out.addProperty("action", action);
            out.addProperty("value", ("run_command".equals(action) || "suggest_command".equals(action)) && value.startsWith("/") && value.length() > 1 ? "/" + command(value.substring(1), pack) : value);
            held.add("clickEvent", out);
        }
        for (Map.Entry<String, JsonElement> entry : held.entrySet()) {
            if (!"clickEvent".equals(entry.getKey())) { textWalk(entry.getValue(), pack); }
        }
    }

    private static String summon(List<String> args, Ported pack) {
        String entity = Ids.vanilla(at(args, 1)) ? Ids.entity(args.get(1)) : args.get(1);
        StringBuilder out = new StringBuilder("summon ").append(entity);
        if (args.size() > 4) { out.append(' ').append(position(args, 2, pack)); }
        if (args.size() > 5) {
            out.append(' ').append(join(args, 5));
            pack.note("A summon of " + args.get(1) + " carries entity nbt, which is kept as written; check its item and block ids for 1.12.2");
        }
        return out.toString();
    }

    private static String experience(List<String> args, Ported pack) {
        String action = at(args, 1);
        if (!"add".equals(action)) { throw new Kept("xp " + action + " has no twin on 1.12.2"); }
        String amount = at(args, 3);
        boolean levels = args.size() > 4 && "levels".equals(args.get(4));
        if (amount.startsWith("-") && !levels) { throw new Kept("taking points away has no twin on 1.12.2"); }
        return "xp " + amount + (levels ? "L" : "") + " " + CommandSelectors.target(args.get(2), pack);
    }

    private static String locate(List<String> args) {
        String named = "structure".equals(at(args, 1)) ? at(args, 2) : args.get(1);
        String bare = named.startsWith("#") ? named.substring(1) : named;
        bare = bare.substring(bare.indexOf(':') + 1).toLowerCase(Locale.ROOT);
        for (Map.Entry<String, String> structure : STRUCTURES.entrySet()) {
            if (bare.equals(structure.getKey()) || bare.startsWith(structure.getKey() + "_")) { return "locate " + structure.getValue(); }
        }
        throw new Kept("the structure " + named + " has no twin on 1.12.2");
    }

    private static String spreadplayers(List<String> args, Ported pack) {
        if (args.contains("under")) { throw new Kept("spreadplayers under a height has no twin on 1.12.2"); }
        List<String> out = new ArrayList<>(args);
        for (int i = 6; i < out.size(); i++) { out.set(i, CommandSelectors.target(out.get(i), pack)); }
        return String.join(" ", out);
    }

    private static String execute(List<String> args, Ported pack) {
        String who = SELF;
        String x = "~";
        String y = "~";
        String z = "~";
        String detect = "";
        boolean moved = false;
        int i = 1;
        while (i < args.size()) {
            String step = args.get(i);
            switch (step) {
                case "as":
                case "at":
                    if (!SELF.equals(who) && !SELF.equals(at(args, i + 1))) { throw new Kept("several as and at steps have no twin on 1.12.2"); }
                    if (!SELF.equals(args.get(i + 1))) { who = CommandSelectors.target(args.get(i + 1), pack); }
                    i += 2;
                    break;
                case "positioned":
                    if (moved || !detect.isEmpty()) { throw new Kept("several position steps have no twin on 1.12.2"); }
                    at(args, i + 3);
                    if ("as".equals(args.get(i + 1))) { throw new Kept("positioned as has no twin on 1.12.2"); }
                    x = args.get(i + 1);
                    y = height(args.get(i + 2), pack);
                    z = args.get(i + 3);
                    moved = true;
                    i += 4;
                    break;
                case "if":
                case "unless": {
                    if ("unless".equals(step)) { throw new Kept("execute unless has no twin on 1.12.2"); }
                    String kind = at(args, i + 1);
                    if ("block".equals(kind) && detect.isEmpty()) {
                        CommandStacks.Stack state = CommandStacks.block(at(args, i + 5), pack);
                        detect = " detect " + position(args, i + 2, pack) + " " + state.id + " " + state.meta;
                        i += 6;
                        break;
                    }
                    if ("entity".equals(kind) && i + 3 == args.size()) { return "testfor " + CommandSelectors.target(args.get(i + 2), pack); }
                    if ("score".equals(kind) && args.size() > i + 5 && "matches".equals(args.get(i + 4))) {
                        Matcher m = CommandSelectors.RANGE.matcher(args.get(i + 5));
                        String least = m.matches() ? (m.group(1).isEmpty() ? "*" : m.group(1)) : args.get(i + 5);
                        String most = m.matches() ? (m.group(2).isEmpty() ? "*" : m.group(2)) : args.get(i + 5);
                        if (i + 6 == args.size()) { return "scoreboard players test " + CommandSelectors.target(args.get(i + 2), pack) + " " + args.get(i + 3) + " " + least + " " + most; }
                    }
                    throw new Kept("the execute condition " + kind + " has no twin the port can write on 1.12.2");
                }
                case "run": {
                    String inner = command(join(args, i + 1), pack);
                    if (SELF.equals(who) && !moved && detect.isEmpty()) { return inner; }
                    return "execute " + who + " " + x + " " + y + " " + z + detect + " " + inner;
                }
                default: throw new Kept("the execute step " + step + " has no twin on 1.12.2");
            }
        }
        throw new Kept("execute has nothing to run");
    }
}
