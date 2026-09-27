package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class FormatDown {
    private static final String TYPE = "type";
    private static final String MINECRAFT = "minecraft:";
    private static final String BRICK = "minecraft:brick";
    private static final String SEQUENCE = "sequence";
    private static final String MATERIAL_RULE = "worldgen/material_rule";
    private static final String MATERIAL_CONDITION = "worldgen/material_condition";
    private static final String OLD_PALETTES = "trims/color_palettes/";
    private static final Set<String> POT_KEYS = set("minecraft:pot_decorations", "pot_decorations", "sherds");
    private static final Set<String> BED_KEYS = set("minecraft:gameplay/bed_rule", "gameplay/bed_rule");
    private static final Set<String> STRAW_KEYS = set("minecraft:gameplay/straw_bed_rule", "gameplay/straw_bed_rule");
    private static final Set<String> ENCLOSING = set("then_run", "if_true", "invert");
    private static final Set<String> NEW_COMMANDS = set("compute", "posteffect");
    private static final Set<String> FILLS = set("fill", "override");
    private static final Set<String> HANDS = set("mainhand", "offhand");
    private static final int DEPTH = 8;
    private static final String TABLE = "/assets/resourcedatapackloader/port/vanilla263.json";
    private static final String CONFIGURED = "worldgen/configured_feature";
    private static final String PLACED = "worldgen/placed_feature";
    private static final String FEATURES = "features";
    @Nullable private static JsonObject vanillaTable;

    private FormatDown() {}

    private static Set<String> set(String... values) { return new HashSet<>(Arrays.asList(values)); }

    public static JsonElement walk(JsonElement element, LineNote note) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : element.getAsJsonArray()) { out.add(walk(item, note)); }
            return out;
        }
        if (!element.isJsonObject()) { return element; }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (POT_KEYS.contains(key) && value.isJsonObject()) { out.add(key, sides(value.getAsJsonObject(), note)); }
            else if (BED_KEYS.contains(key) && value.isJsonObject()) { out.add(key, bed(value.getAsJsonObject(), note)); }
            else if (STRAW_KEYS.contains(key)) { note.accept("sets the straw bed rule, which 26.2 does not have, so it is dropped"); }
            else { out.add(key, walk(value, note)); }
        }
        return out;
    }

    private static JsonArray sides(JsonObject decorations, LineNote note) {
        JsonArray out = new JsonArray();
        if (decorations.size() == 0) { return out; }
        for (String side : Snbt.SIDES) {
            JsonElement item = decorations.get(side);
            if (item == null) { out.add(BRICK); }
            else if (item.isJsonObject()) {
                note.accept("decorates a pot with a counted or component item, which 26.2 reads as a plain item, so the count and components are dropped");
                out.add(item.getAsJsonObject().get("id"));
            }
            else { out.add(item); }
        }
        return out;
    }

    private static JsonObject bed(JsonObject rule, LineNote note) {
        JsonObject out = JsonTree.copy(rule);
        JsonObject target = out.has("argument") && out.get("argument").isJsonObject() ? out.getAsJsonObject("argument") : out;
        JsonElement destroyed = target.remove("destroy_on_use");
        if (destroyed != null) { target.add("explodes", destroyed); }
        if (target.remove("destroy_on_leave") != null) { note.accept("destroys a bed when its sleeper leaves, which 26.2 cannot, so that is dropped"); }
        return out;
    }

    public static JsonObject trim(JsonObject material, LineNote note) {
        JsonObject out = JsonTree.copy(material);
        JsonElement palette = out.remove("palette_id");
        if (palette == null || !palette.isJsonPrimitive()) { return out; }
        String id = palette.getAsString();
        String path = id.substring(id.indexOf(':') + 1);
        String name = path.startsWith("trim/") ? path.substring("trim/".length()) : path.substring(path.lastIndexOf('/') + 1);
        out.addProperty("asset_name", name);
        if (!Snbt.TRIM_PALETTES.contains(name)) { note.accept("colors trims with the palette " + id + ", which 26.2 reads through the armor_trims atlas, so that atlas must list " + name + " for the trims to show"); }
        return out;
    }

    public static String assetPath(String path) {
        String base = "textures/palettes/trim_base.png";
        if (path.startsWith(base)) { return "textures/" + OLD_PALETTES + "trim_palette.png" + path.substring(base.length()); }
        return path.startsWith("textures/palettes/trim/") ? "textures/" + OLD_PALETTES + path.substring("textures/palettes/trim/".length()) : path;
    }

    public static JsonElement asset(JsonElement json, String path, LineNote note) {
        if (!json.isJsonObject()) { return json; }
        JsonObject out = JsonTree.copy(json.getAsJsonObject());
        if (path.startsWith("models/") && out.has("elements") && out.get("elements").isJsonArray()) {
            for (JsonElement element : out.getAsJsonArray("elements")) {
                if (!element.isJsonObject()) { continue; }
                JsonElement shade = element.getAsJsonObject().remove("shade_direction_override");
                if (shade == null) { continue; }
                if (shade.isJsonPrimitive() && "up".equals(shade.getAsString())) { element.getAsJsonObject().addProperty("shade", false); }
                else { note.accept("shades a model element as if it faced " + shade + ", which 26.2 cannot, so it is shaded normally"); }
            }
        }
        else if (path.startsWith("equipment/") && out.remove("trim_overrides") != null) { note.accept("colors trims per equipment with trim_overrides, which 26.2 reads from the trim material's override_armor_assets, so those palettes are dropped"); }
        else if (path.startsWith("atlases/") && out.has("sources") && out.get("sources").isJsonArray()) {
            for (JsonElement source : out.getAsJsonArray("sources")) {
                if (!source.isJsonObject() || !"paletted_permutations".equals(bare(text(source.getAsJsonObject(), TYPE)))) { continue; }
                JsonObject permuted = source.getAsJsonObject();
                if (permuted.has("palette_key")) { permuted.addProperty("palette_key", palette(text(permuted, "palette_key"))); }
                if (permuted.has("permutations") && permuted.get("permutations").isJsonObject()) { permuted.getAsJsonObject("permutations").entrySet().forEach(entry -> entry.setValue(new JsonPrimitive(palette(entry.getValue().getAsString())))); }
            }
        }
        else if (path.startsWith("post_effect/") || path.startsWith("shaders/")) { note.accept("carries shader programs, which 26.2 compiles differently, so they are served as written"); }
        return out;
    }

    private static String palette(String id) {
        int colon = id.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : id.substring(0, colon);
        String path = id.substring(colon + 1);
        if ("trim_base".equals(path)) { return namespace + ":" + OLD_PALETTES + "trim_palette"; }
        return namespace + ":" + (path.startsWith("trim/") ? OLD_PALETTES + path.substring("trim/".length()) : "palettes/" + path);
    }

    public static String function(String text, LineNote note) {
        String[] lines = Snbt.pots(text, value -> value.startsWith("{") ? listed(value, note) : value).split("\n", -1);
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            boolean macro = trimmed.startsWith("$");
            List<String> words = words(macro ? trimmed.substring(1) : trimmed);
            int command = command(words);
            if (command < 0 || NEW_COMMANDS.contains(words.get(command)) || "item".equals(words.get(command)) && command + 1 < words.size() && FILLS.contains(words.get(command + 1))) {
                note.accept("runs " + (command < 0 ? "execute if slots" : words.get(command)) + ", which 26.2 does not have, so that line is dropped");
                continue;
            }
            int hand = command + 2;
            if ("swing".equals(words.get(command)) && hand + 1 < words.size() && HANDS.contains(words.get(hand))) {
                note.accept("swings with an animation, which 26.2 cannot, so it swings plainly");
                String kept = (macro ? "$" : "") + String.join(" ", words.subList(0, hand + 1));
                out.add(line.substring(0, line.indexOf(trimmed)) + kept);
            }
            else { out.add(line); }
        }
        return String.join("\n", out);
    }

    private static int command(List<String> words) {
        if (!"execute".equals(words.get(0))) { return 0; }
        for (int i = 1; i < words.size(); i++) {
            if (("if".equals(words.get(i)) || "unless".equals(words.get(i))) && i + 1 < words.size() && "slots".equals(words.get(i + 1))) { return -1; }
            if ("run".equals(words.get(i))) { return i + 1 < words.size() ? i + 1 : 0; }
        }
        return 0;
    }

    private static List<String> words(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder word = new StringBuilder();
        int depth = 0;
        char quote = 0;
        for (int i = 0; i < line.length(); i++) {
            char held = line.charAt(i);
            if (quote != 0) {
                if (held == '\\' && i + 1 < line.length()) { word.append(held).append(line.charAt(++i)); }
                else {
                    if (held == quote) { quote = 0; }
                    word.append(held);
                }
                continue;
            }
            if (held == ' ' && depth == 0) {
                if (word.length() > 0) { out.add(word.toString()); }
                word.setLength(0);
                continue;
            }
            if (held == '"' || held == '\'') { quote = held; }
            else if (held == '[' || held == '{') { depth++; }
            else if (held == ']' || held == '}') { depth = Math.max(0, depth - 1); }
            word.append(held);
        }
        if (word.length() > 0) { out.add(word.toString()); }
        if (out.isEmpty()) { out.add(""); }
        return out;
    }

    private static String listed(String value, LineNote note) {
        List<String> sides = new ArrayList<>(Arrays.asList(BRICK, BRICK, BRICK, BRICK));
        for (String item : Snbt.items(value.substring(1, value.length() - 1))) {
            int colon = item.indexOf(':');
            if (colon < 0) { continue; }
            int side = Snbt.SIDES.indexOf(Snbt.unquoted(item.substring(0, colon)));
            if (side < 0) { continue; }
            String held = item.substring(colon + 1).trim();
            if (held.startsWith("{")) {
                note.accept("decorates a pot with a counted or component item, which 26.2 reads as a plain item, so the count and components are dropped");
                String inner = held.substring(1, held.length() - 1);
                held = "";
                for (String field : Snbt.items(inner)) {
                    int split = field.indexOf(':');
                    if (split > 0 && "id".equals(Snbt.unquoted(field.substring(0, split)))) { held = field.substring(split + 1).trim(); }
                }
            }
            if (!held.isEmpty()) { sides.set(side, Snbt.unquoted(held)); }
        }
        StringBuilder out = new StringBuilder("[");
        for (String side : sides) { out.append(out.length() > 1 ? "," : "").append('"').append(side).append('"'); }
        return out.append(']').toString();
    }

    public static JsonElement inlined(JsonElement element, LineNote note) {
        JsonElement out = inline(element, note, 0);
        return out == null ? element : out;
    }

    @Nullable private static JsonElement inline(JsonElement element, LineNote note, int depth) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : element.getAsJsonArray()) {
                JsonElement held = inline(item, note, depth);
                if (held != null) { out.add(held); }
            }
            return out;
        }
        if (!element.isJsonObject()) { return element; }
        JsonObject in = element.getAsJsonObject();
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : in.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            String folder = folder(key, in);
            String features = features(key, in);
            if (features != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) { value = feature(features, value.getAsString(), note); }
            else if (FEATURES.equals(key) && value.isJsonArray() && value.getAsJsonArray().size() > 0 && value.getAsJsonArray().get(0).isJsonArray()) { value = steps(value.getAsJsonArray(), note); }
            else if (FEATURES.equals(key) && value.isJsonArray() && "simple_random_selector".equals(bare(text(in, TYPE)))) {
                JsonArray listed = new JsonArray();
                for (JsonElement item : value.getAsJsonArray()) { listed.add(item.isJsonPrimitive() && item.getAsJsonPrimitive().isString() ? feature(PLACED, item.getAsString(), note) : item); }
                value = listed;
            }
            else if (folder != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                JsonElement carried = resolved(folder, value.getAsString(), note, depth);
                if (carried == null && "material_rule".equals(key)) {
                    out.add(key, value);
                    continue;
                }
                if (carried == null) {
                    note.accept("names the " + folder.substring(folder.indexOf('/') + 1) + " " + value.getAsString() + ", which the pack does not carry and 26.2 has no registry for, so " + (ENCLOSING.contains(key) ? "the rule holding it is" : "it is") + " dropped");
                    if (ENCLOSING.contains(key)) { return null; }
                    continue;
                }
                value = carried;
            }
            else if (SEQUENCE.equals(key) && value.isJsonArray() && SEQUENCE.equals(bare(text(in, TYPE)))) {
                JsonArray steps = new JsonArray();
                for (JsonElement step : value.getAsJsonArray()) {
                    JsonElement held = step.isJsonPrimitive() && step.getAsJsonPrimitive().isString() ? resolved(MATERIAL_RULE, step.getAsString(), note, depth) : step;
                    if (held == null) { note.accept("names the material_rule " + step.getAsString() + ", which the pack does not carry and 26.2 has no registry for, so it is dropped"); }
                    else { steps.add(held); }
                }
                value = steps;
            }
            JsonElement held = inline(value, note, depth);
            if (held == null && ENCLOSING.contains(key)) { return null; }
            if (held != null) { out.add(key, held); }
        }
        return out;
    }

    @Nullable private static JsonElement resolved(String folder, String id, LineNote note, int depth) {
        JsonElement carried = depth < DEPTH ? note.carried(folder, id) : null;
        if (carried == null) { carried = vanilla(folder, id); }
        return carried == null ? null : inline(carried, note, depth + 1);
    }

    private static JsonElement feature(String folder, String id, LineNote note) {
        JsonElement vanilla = vanilla(folder, id);
        if (vanilla == null || note.carried(folder, id) != null) { return new JsonPrimitive(id); }
        if (!table().getAsJsonArray("lacking").contains(new JsonPrimitive(named(id)))) { return vanilla; }
        note.accept("names the 26.3 feature " + id + ", which is built from blocks 26.2 does not have, so it places nothing");
        JsonObject none = new JsonObject();
        none.addProperty(TYPE, MINECRAFT + "no_op");
        if (CONFIGURED.equals(folder)) { return none; }
        JsonObject placed = new JsonObject();
        placed.add("feature", none);
        placed.add("placement", new JsonArray());
        return placed;
    }

    private static JsonArray steps(JsonArray steps, LineNote note) {
        JsonArray out = new JsonArray();
        for (JsonElement step : steps) {
            if (!step.isJsonArray()) {
                out.add(step);
                continue;
            }
            JsonArray kept = new JsonArray();
            for (JsonElement item : step.getAsJsonArray()) {
                boolean string = item.isJsonPrimitive() && item.getAsJsonPrimitive().isString();
                if (string && vanilla(PLACED, item.getAsString()) != null && note.carried(PLACED, item.getAsString()) == null) { note.accept("places the 26.3 feature " + item.getAsString() + ", which 26.2 has no counterpart for, so the biome leaves it out"); }
                else { kept.add(item); }
            }
            out.add(kept);
        }
        return out;
    }

    @Nullable private static JsonElement vanilla(String folder, String id) {
        JsonElement held = table().has(folder) ? table().getAsJsonObject(folder).get(named(id)) : null;
        return held == null ? null : JsonTree.copy(held);
    }

    @Nullable private static String features(String key, JsonObject owner) {
        switch (key) {
            case "feature": return owner.has("placement") ? CONFIGURED : owner.has("chance") || owner.has("element_type") ? PLACED : null;
            case "default":
            case "feature_true":
            case "feature_false": return PLACED;
            default: return null;
        }
    }

    private static String named(String id) { return id.contains(":") ? id : MINECRAFT + id; }

    private static synchronized JsonObject table() {
        if (vanillaTable != null) { return vanillaTable; }
        vanillaTable = new JsonObject();
        vanillaTable.add("lacking", new JsonArray());
        try (InputStream stream = FormatDown.class.getResourceAsStream(TABLE)) {
            if (stream == null) { ContentLog.LOGGER.error("The 26.3 registry table {} is missing from the jar, so 26.3 references are not inlined", TABLE); }
            else { vanillaTable = new JsonParser().parse(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject(); }
        }
        catch (IOException | RuntimeException failed) { ContentLog.LOGGER.error("Could not read the 26.3 registry table {}", TABLE, failed); }
        return vanillaTable;
    }

    @Nullable private static String folder(String key, JsonObject owner) {
        switch (key) {
            case "then_run":
            case "material_rule": return MATERIAL_RULE;
            case "if_true":
            case "invert": return MATERIAL_CONDITION;
            case "fallback": return null;
            default: return WorldgenDown.providing(key, owner) ? "worldgen/block_state_provider" : null;
        }
    }

    private static String text(JsonObject object, String key) { return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : ""; }

    private static String bare(String id) { return id.startsWith(MINECRAFT) ? id.substring(MINECRAFT.length()) : id; }
}
