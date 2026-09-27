package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class FormatUp {
    private static final String TYPE = "type";
    private static final String MINECRAFT = "minecraft:";
    private static final String BRICK = "minecraft:brick";
    private static final String OLD_PALETTES = "trims/color_palettes/";
    private static final String OLD_BASE = "trims/color_palettes/trim_palette";
    private static final Set<String> POT_KEYS = Set.of("minecraft:pot_decorations", "pot_decorations", "sherds");
    private static final Set<String> BED_KEYS = Set.of("minecraft:gameplay/bed_rule", "gameplay/bed_rule");

    private FormatUp() {}

    public static JsonElement walk(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray out = new JsonArray();
            for (JsonElement item : element.getAsJsonArray()) { out.add(walk(item)); }
            return out;
        }
        if (!element.isJsonObject()) { return element; }
        JsonObject out = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (POT_KEYS.contains(key) && value.isJsonArray()) { out.add(key, sides(value.getAsJsonArray())); }
            else if (BED_KEYS.contains(key) && value.isJsonObject()) { out.add(key, bed(value.getAsJsonObject())); }
            else { out.add(key, walk(value)); }
        }
        return out;
    }

    private static JsonObject sides(JsonArray items) {
        JsonObject out = new JsonObject();
        for (int i = 0; i < Math.min(items.size(), Snbt.SIDES.size()); i++) {
            JsonElement item = items.get(i);
            if (item.isJsonPrimitive() && !BRICK.equals(id(item.getAsString()))) { out.add(Snbt.SIDES.get(i), item); }
        }
        return out;
    }

    private static JsonObject bed(JsonObject rule) {
        JsonObject out = rule.deepCopy();
        JsonObject target = out.has("argument") && out.get("argument").isJsonObject() ? out.getAsJsonObject("argument") : out;
        JsonElement explodes = target.remove("explodes");
        if (explodes != null) { target.add("destroy_on_use", explodes); }
        return out;
    }

    public static JsonObject trim(JsonObject material, LineNote note) {
        JsonObject out = material.deepCopy();
        JsonElement asset = out.remove("asset_name");
        if (asset != null && asset.isJsonPrimitive()) {
            String name = asset.getAsString();
            out.addProperty("palette_id", (Snbt.TRIM_PALETTES.contains(name) ? "minecraft" : note.namespace()) + ":trim/" + name);
        }
        if (out.remove("override_armor_assets") != null) { note.accept("colors trims per armor material with override_armor_assets, which 26.3 reads from each equipment asset's trim_overrides, so those palettes are dropped"); }
        return out;
    }

    public static String assetPath(String path) {
        String image = "textures/" + OLD_BASE + ".png";
        if (path.startsWith(image)) { return "textures/palettes/trim_base.png" + path.substring(image.length()); }
        return path.startsWith("textures/" + OLD_PALETTES) ? "textures/palettes/trim/" + path.substring(("textures/" + OLD_PALETTES).length()) : path;
    }

    public static JsonElement asset(JsonElement json, String path, LineNote note) {
        if (!json.isJsonObject()) { return json; }
        JsonObject out = json.getAsJsonObject().deepCopy();
        if (path.startsWith("models/") && out.has("elements") && out.get("elements").isJsonArray()) {
            for (JsonElement element : out.getAsJsonArray("elements")) {
                if (!element.isJsonObject()) { continue; }
                JsonElement shade = element.getAsJsonObject().remove("shade");
                if (shade != null && shade.isJsonPrimitive() && !shade.getAsBoolean()) { element.getAsJsonObject().addProperty("shade_direction_override", "up"); }
            }
        }
        else if (path.startsWith("items/")) { tints(out, note); }
        else if (path.startsWith("atlases/") && out.has("sources") && out.get("sources").isJsonArray()) {
            for (JsonElement source : out.getAsJsonArray("sources")) {
                if (!source.isJsonObject() || !"paletted_permutations".equals(bare(text(source.getAsJsonObject(), TYPE)))) { continue; }
                JsonObject permuted = source.getAsJsonObject();
                if (permuted.has("palette_key")) { permuted.addProperty("palette_key", palette(text(permuted, "palette_key"), note)); }
                if (permuted.has("permutations") && permuted.get("permutations").isJsonObject()) { permuted.getAsJsonObject("permutations").entrySet().forEach(entry -> entry.setValue(new com.google.gson.JsonPrimitive(palette(entry.getValue().getAsString(), note)))); }
            }
        }
        else if (path.startsWith("post_effect/") || path.startsWith("shaders/")) { note.accept("carries shader programs, which 26.3 compiles differently, so they are served as written"); }
        return out;
    }

    private static void tints(JsonElement element, LineNote note) {
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonElement item = array.get(i);
                if (item.isJsonObject() && "map_color".equals(bare(text(item.getAsJsonObject(), TYPE)))) {
                    note.accept("tints an item with map_color, which 26.3 removed, so it uses the tint's default color");
                    JsonObject constant = new JsonObject();
                    constant.addProperty(TYPE, MINECRAFT + "constant");
                    constant.add("value", item.getAsJsonObject().get("default"));
                    array.set(i, constant);
                }
                else { tints(item, note); }
            }
        }
        else if (element.isJsonObject()) { element.getAsJsonObject().entrySet().forEach(entry -> tints(entry.getValue(), note)); }
    }

    private static String palette(String id, LineNote note) {
        int colon = id.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : id.substring(0, colon);
        String path = id.substring(colon + 1);
        if (OLD_BASE.equals(path)) { return namespace + ":trim_base"; }
        if (path.startsWith(OLD_PALETTES)) { return namespace + ":trim/" + path.substring(OLD_PALETTES.length()); }
        if (path.startsWith("palettes/")) { return namespace + ":" + path.substring("palettes/".length()); }
        note.accept("names the palette " + id + ", which 26.3 reads from textures/palettes, so it is kept as written and needs moving there");
        return id;
    }

    public static String function(String text, LineNote note) {
        return Snbt.pots(text, value -> {
            if (!value.startsWith("[")) {
                if (!value.startsWith("{")) { note.accept("names pot decorations through " + value + ", which cannot be read until it runs, so it is kept as written"); }
                return value;
            }
            List<String> items = Snbt.items(value.substring(1, value.length() - 1));
            StringBuilder out = new StringBuilder("{");
            for (int i = 0; i < Math.min(items.size(), Snbt.SIDES.size()); i++) {
                String item = id(Snbt.unquoted(items.get(i)));
                if (BRICK.equals(item)) { continue; }
                if (out.length() > 1) { out.append(','); }
                out.append(Snbt.SIDES.get(i)).append(":\"").append(item).append('"');
            }
            return out.append('}').toString();
        });
    }

    private static String id(String id) { return id.contains(":") ? id : MINECRAFT + id; }

    private static String text(JsonObject object, String key) { return object.has(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : ""; }

    private static String bare(String id) { return id.startsWith(MINECRAFT) ? id.substring(MINECRAFT.length()) : id; }
}
