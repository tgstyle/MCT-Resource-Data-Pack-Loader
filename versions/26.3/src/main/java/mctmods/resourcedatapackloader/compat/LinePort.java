package mctmods.resourcedatapackloader.compat;

import mctmods.resourcedatapackloader.util.FeatureForms;
import mctmods.resourcedatapackloader.util.FormatUp;
import mctmods.resourcedatapackloader.util.LineNote;
import mctmods.resourcedatapackloader.util.LootUp;
import mctmods.resourcedatapackloader.util.NumberUp;
import mctmods.resourcedatapackloader.util.WorldgenUp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Consumer;

public final class LinePort {
    private static final String[][] FOLDERS = {{"worldgen/configured_feature/", "worldgen/feature/"}, {"worldgen/configured_carver/", "worldgen/carver/"}, {"tags/worldgen/configured_feature/", "tags/worldgen/feature/"}, {"tags/worldgen/configured_carver/", "tags/worldgen/carver/"}};
    private static final List<String> LOOT = List.of("loot_table/", "predicate/", "item_modifier/", "loot_modifiers/", "villager_trade/");
    private static final List<String> NUMBERS = List.of("loot_table/", "loot_modifiers/", "villager_trade/", "trade_set/", "predicate/", "item_modifier/", "advancement/", "enchantment/");
    private static final String COOKING_TIME = "cookingtime";

    private LinePort() {}

    public static String path(String path) {
        for (String[] pair : FOLDERS) {
            if (path.startsWith(pair[0])) { return pair[1] + path.substring(pair[0].length()); }
        }
        return path;
    }

    public static JsonElement data(JsonElement json, String path, LineNote note) {
        JsonElement held = ported(FormatUp.walk(json), path, note);
        if (NUMBERS.stream().anyMatch(path::startsWith)) { held = NumberUp.fitted(held); }
        if (path.startsWith("trim_material/") && held.isJsonObject()) { return FormatUp.trim(held.getAsJsonObject(), note); }
        return path.startsWith("worldgen/") ? FeatureForms.up(held, note) : held;
    }

    public static JsonElement asset(JsonElement json, String path, LineNote note) { return FormatUp.asset(json, path, note); }

    public static String assetPath(String path) { return FormatUp.assetPath(path); }

    public static String function(String text, LineNote note) { return FormatUp.function(text, note); }

    private static JsonElement ported(JsonElement json, String path, Consumer<String> note) {
        if (path.startsWith("advancement/") && json.isJsonObject()) { return LootUp.advancement(json.getAsJsonObject()); }
        if (LOOT.stream().anyMatch(path::startsWith)) { return LootUp.loot(json); }
        if (path.startsWith("enchantment/") && json.isJsonObject()) { return WorldgenUp.configured(LootUp.loot(json).getAsJsonObject()); }
        if (path.startsWith("worldgen/noise_settings/") && json.isJsonObject()) { return WorldgenUp.portedNoiseSettings(json.getAsJsonObject(), note); }
        if (path.startsWith("worldgen/density_function/")) { return WorldgenUp.density(json, 0, 0, note); }
        if (!json.isJsonObject()) { return json; }
        if (path.startsWith("recipe/")) { return recipe(json.getAsJsonObject()); }
        if (path.startsWith("worldgen/biome/")) { return WorldgenUp.biome(json.getAsJsonObject(), note); }
        if (path.startsWith("worldgen/configured_carver/") || path.startsWith("worldgen/carver/")) { return WorldgenUp.carver(WorldgenUp.ported(json.getAsJsonObject(), note), note); }
        return path.startsWith("worldgen/") ? WorldgenUp.ported(json.getAsJsonObject(), note) : json;
    }

    private static JsonObject recipe(JsonObject recipe) {
        JsonElement type = recipe.get("type");
        int time = type == null || !type.isJsonPrimitive() ? 0 : switch (type.getAsString()) {
            case "minecraft:smelting", "smelting" -> 200;
            case "minecraft:blasting", "blasting", "minecraft:smoking", "smoking", "minecraft:campfire_cooking", "campfire_cooking" -> 100;
            default -> 0;
        };
        if (time == 0 || recipe.has(COOKING_TIME)) { return recipe; }
        JsonObject out = recipe.deepCopy();
        out.addProperty(COOKING_TIME, time);
        return out;
    }
}
