package mctmods.resourcedatapackloader.compat;

import mctmods.resourcedatapackloader.util.FeatureForms;
import mctmods.resourcedatapackloader.util.FormatDown;
import mctmods.resourcedatapackloader.util.LineNote;
import mctmods.resourcedatapackloader.util.LootDown;
import mctmods.resourcedatapackloader.util.NumberDown;
import mctmods.resourcedatapackloader.util.WorldgenDown;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Consumer;

public final class LinePort {
    private static final String[][] FOLDERS = {{"worldgen/feature/", "worldgen/configured_feature/"}, {"worldgen/carver/", "worldgen/configured_carver/"}, {"tags/worldgen/feature/", "tags/worldgen/configured_feature/"}, {"tags/worldgen/carver/", "tags/worldgen/configured_carver/"}, {"recipe/brewing/", "brewing/"}};
    private static final List<String> LOOT = List.of("loot_table/", "loot_modifiers/", "villager_trade/");
    private static final List<String> NUMBERS = List.of("loot_table/", "loot_modifiers/", "villager_trade/", "trade_set/", "predicate/", "item_modifier/", "advancement/", "enchantment/");
    private static final String POTION_CONTENTS = "potion_contents";
    private static final String ITEM = "item";

    private LinePort() {}

    public static String path(String path) {
        for (String[] pair : FOLDERS) {
            if (path.startsWith(pair[0])) { return pair[1] + path.substring(pair[0].length()); }
        }
        return path;
    }

    public static JsonElement data(JsonElement json, String path, LineNote note) {
        JsonElement held = FormatDown.walk(json, note);
        if (path.startsWith("worldgen/")) { held = FormatDown.inlined(FeatureForms.down(held, note), note); }
        if (NUMBERS.stream().anyMatch(path::startsWith)) { held = NumberDown.fitted(held, note); }
        if (path.startsWith("trim_material/") && held.isJsonObject()) { return FormatDown.trim(held.getAsJsonObject(), note); }
        return ported(held, path, note);
    }

    public static JsonElement asset(JsonElement json, String path, LineNote note) { return FormatDown.asset(json, path, note); }

    public static String assetPath(String path) { return FormatDown.assetPath(path); }

    public static String function(String text, LineNote note) { return FormatDown.function(text, note); }

    private static JsonElement ported(JsonElement json, String path, Consumer<String> note) {
        if (path.startsWith("advancement/") && json.isJsonObject()) { return LootDown.advancement(json.getAsJsonObject(), note); }
        if (LOOT.stream().anyMatch(path::startsWith)) { return LootDown.loot(json, note); }
        if (path.startsWith("predicate/")) { return LootDown.predicate(json, note); }
        if (path.startsWith("item_modifier/")) { return LootDown.modifier(json, note); }
        if (path.startsWith("enchantment/")) { return WorldgenDown.states(LootDown.loot(json, note), note); }
        if (path.startsWith("brewing/") && json.isJsonObject() && "minecraft:brewing".equals(text(json.getAsJsonObject(), "type"))) { return brewing(json.getAsJsonObject(), note); }
        if (!json.isJsonObject()) { return json; }
        if (path.startsWith("worldgen/configured_carver/") || path.startsWith("worldgen/carver/")) { return WorldgenDown.configured(WorldgenDown.carver(json.getAsJsonObject(), note), note); }
        if (path.startsWith("worldgen/configured_feature/") || path.startsWith("worldgen/feature/")) { return WorldgenDown.configured(json.getAsJsonObject(), note); }
        if (path.startsWith("worldgen/noise_settings/")) { return WorldgenDown.noiseSettings(json.getAsJsonObject(), note); }
        if (path.startsWith("worldgen/density_function/")) { return WorldgenDown.density(json, note); }
        if (path.startsWith("worldgen/biome/")) { return WorldgenDown.biome(json.getAsJsonObject(), note); }
        return path.startsWith("worldgen/") ? WorldgenDown.ported(json.getAsJsonObject(), note) : json;
    }

    private static JsonObject brewing(JsonObject recipe, Consumer<String> note) {
        JsonObject input = object(recipe, "input");
        JsonObject output = object(recipe, "output");
        String reagent = text(object(recipe, "reagent"), ITEM);
        String from = text(object(input, POTION_CONTENTS), "potions");
        JsonObject components = object(output, "components");
        String to = text(object(components, "minecraft:" + POTION_CONTENTS), "potion");
        JsonObject entry = new JsonObject();
        entry.addProperty("ingredient", reagent);
        if (!from.isEmpty() && !to.isEmpty()) {
            entry.addProperty("from", from);
            entry.addProperty("to", to);
            if (!"minecraft:potion".equals(text(input, ITEM))) { note.accept("brews " + text(input, ITEM) + " only, and 26.2 brews a potion mix in every potion container"); }
        }
        else {
            entry.addProperty("input", text(input, ITEM));
            entry.addProperty("output", text(output, "id"));
            if (!components.isEmpty()) { note.accept("sets components on its brewed item, which a 26.2 brewing recipe cannot, so they are dropped"); }
        }
        JsonArray brewing = new JsonArray();
        if (reagent.isEmpty() || reagent.startsWith("#") || entry.has("input") && (text(input, ITEM).isEmpty() || text(input, ITEM).startsWith("#"))) { note.accept("brews from a tag or a list of items, which a 26.2 brewing recipe cannot name, so it is dropped"); }
        else { brewing.add(entry); }
        JsonObject out = new JsonObject();
        out.add("brewing", brewing);
        return out;
    }

    private static JsonObject object(JsonObject parent, String key) { return parent.has(key) && parent.get(key).isJsonObject() ? parent.getAsJsonObject(key) : new JsonObject(); }

    private static String text(JsonObject parent, String key) { return parent.has(key) && parent.get(key).isJsonPrimitive() ? parent.get(key).getAsString() : ""; }
}
