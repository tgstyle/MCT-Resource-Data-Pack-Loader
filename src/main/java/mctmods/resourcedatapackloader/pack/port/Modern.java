package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

final class Modern {
    private static final String GAMERULE = "gamerule ";

    private Modern() {}

    static boolean active() { return Port.Line.running() == Port.Line.V26; }

    static void data(JsonObject json, Port.Kind kind, String path, String file, boolean stacks, Consumer<String> note) {
        switch (kind) {
            case RECIPE -> ModernRecipes.recipe(json, file, stacks, note);
            case LOOT -> ModernLoot.loot(json, file, stacks, note);
            case ADVANCEMENT -> ModernLoot.advancement(json, file, stacks, note);
            default -> definition(json, path, file, stacks, note);
        }
    }

    private static void definition(JsonObject json, String path, String file, boolean stacks, Consumer<String> note) {
        if (path.startsWith("dimension_type/")) { ModernWorld.dimensionType(json, file, note); }
        else if (path.startsWith("worldgen/biome/")) { ModernWorld.biome(json, file, note); }
        else if (path.startsWith("worldgen/configured_feature/")) { ModernWorld.feature(json, file, note); }
        else if (path.startsWith("trim_material/") || path.startsWith("trim_pattern/")) { ModernWorld.trim(json, path.startsWith("trim_material/")); }
        else if (path.startsWith("wolf_variant/")) { ModernWorld.wolf(json, file, note); }
        else if (path.startsWith("enchantment/") || path.startsWith("painting_variant/")) {
            ModernLoot.loot(json, file, stacks, note);
            return;
        }
        ModernFixes.replace(json, ModernFixes.names(json).getAsJsonObject());
    }

    static String ported(Port.Kind kind, String contents, String path, Consumer<String> note) {
        if (kind == Port.Kind.FUNCTION) { return path.endsWith(".mcfunction") ? function(contents, path, note) : contents; }
        if (kind != Port.Kind.RECIPE && kind != Port.Kind.LOOT && kind != Port.Kind.ADVANCEMENT && kind != Port.Kind.MODEL) { return contents; }
        JsonElement parsed = JsonParser.parseString(contents);
        if (!parsed.isJsonObject()) { return contents; }
        JsonObject json = parsed.getAsJsonObject();
        if (kind == Port.Kind.MODEL) { ModernAssets.model(json); }
        else { data(json, kind, path, path, false, note); }
        return Ported.GSON.toJson(json);
    }

    static String function(String contents, String file, Consumer<String> note) {
        String[] lines = contents.split("\n", -1);
        boolean changed = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();
            String out = trimmed.startsWith(GAMERULE) ? gamerule(trimmed, file, note) : trimmed;
            boolean macro = out.startsWith("$");
            List<String> args = CrossCommands.split(macro ? out.substring(1) : out, ' ');
            command(args, 0);
            out = (macro ? "$" : "") + String.join(" ", args);
            if (out.equals(trimmed)) { continue; }
            int start = line.indexOf(trimmed);
            lines[i] = line.substring(0, start) + out + line.substring(start + trimmed.length());
            changed = true;
        }
        return changed ? String.join("\n", lines) : contents;
    }

    private static void command(List<String> args, int from) {
        if (from >= args.size()) { return; }
        switch (args.get(from)) {
            case "give", "clear", "attribute" -> named(args, from + 2);
            case "setblock" -> named(args, from + 4);
            case "fill" -> {
                named(args, from + 7);
                if (from + 9 < args.size() && "replace".equals(args.get(from + 8))) { named(args, from + 9); }
            }
            case "item" -> named(args, after(args, from, "with", 1));
            case "clone" -> named(args, after(args, from, "filtered", 1));
            case "loot" -> {
                named(args, after(args, from, "mine", 4));
                named(args, after(args, from, "fish", 5));
            }
            case "execute" -> execute(args, from + 1);
            default -> { }
        }
    }

    private static void execute(List<String> args, int from) {
        for (int i = from; i < args.size(); i++) {
            String word = args.get(i);
            if ("run".equals(word)) {
                command(args, i + 1);
                return;
            }
            if ((!"if".equals(word) && !"unless".equals(word)) || i + 2 >= args.size()) { continue; }
            if ("block".equals(args.get(i + 1))) { named(args, i + 5); }
            else if ("items".equals(args.get(i + 1))) { named(args, i + ("entity".equals(args.get(i + 2)) ? 5 : 7)); }
        }
    }

    private static int after(List<String> args, int from, String word, int offset) {
        for (int i = from + 1; i < args.size(); i++) {
            if (word.equals(args.get(i))) { return i + offset; }
        }
        return -1;
    }

    private static void named(List<String> args, int index) {
        if (index < 0 || index >= args.size() || ModernFixes.notIdentifier(args.get(index))) { return; }
        args.set(index, ModernFixes.name(args.get(index)));
    }

    private static String gamerule(String line, String file, Consumer<String> note) {
        String[] args = line.split(" +");
        if (args.length < 2) { return line; }
        Map.Entry<String, String> rule = Commands.modernRule(args[1], args.length > 2 ? args[2] : null);
        if (rule == null) {
            note.accept("'" + file + "' sets the game rule " + args[1] + ", which this version does not have, so that line is kept as written");
            return line;
        }
        return "gamerule " + rule.getKey() + (args.length > 2 ? " " + rule.getValue() : "");
    }
}
