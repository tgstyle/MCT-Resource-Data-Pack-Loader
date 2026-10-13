package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import javax.annotation.Nullable;

final class ContentOptionOres {
    private static final int MAX_SIZE = 64;
    private static final int MAX_COUNT = 256;
    private static final String DUNGEONS = "dungeonChance";
    private static final String LAVA_LAKES = "lavaLakeChance";
    private static final int LAVA_BASE = 80;
    private static final Set<String> KEPT = Set.of("ore_gold_extra");
    private static final List<Ore> ORES = List.of(
            new Ore("coal", 17, 20, 0, 128, false, List.of("ore_coal_upper", "ore_coal_lower")),
            new Ore("iron", 9, 20, 0, 64, false, List.of("ore_iron_middle", "ore_iron_upper", "ore_iron_small")),
            new Ore("gold", 9, 2, 0, 32, false, List.of("ore_gold", "ore_gold_lower", "ore_gold_extra")),
            new Ore("redstone", 8, 8, 0, 16, false, List.of("ore_redstone", "ore_redstone_lower")),
            new Ore("diamond", 8, 1, 0, 16, false, List.of("ore_diamond", "ore_diamond_medium", "ore_diamond_large", "ore_diamond_buried")),
            new Ore("lapis", 7, 1, 16, 16, true, List.of("ore_lapis", "ore_lapis_buried")),
            new Ore("dirt", 33, 10, 0, 256, false, List.of("ore_dirt")),
            new Ore("gravel", 33, 8, 0, 256, false, List.of("ore_gravel")),
            new Ore("granite", 33, 10, 0, 80, false, List.of("ore_granite_upper", "ore_granite_lower")),
            new Ore("diorite", 33, 10, 0, 80, false, List.of("ore_diorite_upper", "ore_diorite_lower")),
            new Ore("andesite", 33, 10, 0, 80, false, List.of("ore_andesite_upper", "ore_andesite_lower")));

    private ContentOptionOres() {}

    private record Ore(String key, int size, int count, int low, int high, boolean centered, List<String> placed) {
        String sizeKey() { return key + "Size"; }

        String countKey() { return key + "Count"; }

        String lowKey() { return key + (centered ? "CenterHeight" : "MinHeight"); }

        String highKey() { return key + (centered ? "Spread" : "MaxHeight"); }
    }

    static boolean ignores(String key) {
        if (DUNGEONS.equals(key) || LAVA_LAKES.equals(key)) { return false; }
        for (Ore ore : ORES) {
            if (ore.sizeKey().equals(key) || ore.countKey().equals(key) || ore.lowKey().equals(key) || ore.highKey().equals(key)) { return false; }
        }
        return true;
    }

    static void generate(@Nullable JsonObject options) {
        Map<String, JsonObject> made = new LinkedHashMap<>();
        Set<String> dropped = new LinkedHashSet<>();
        if (options != null) {
            for (Ore ore : ORES) { ore(ore, options, made, dropped); }
            dungeons(options, made, dropped);
            lavaLakes(options, made);
        }
        ContentOptionFeatures.publish(built(made), dropped);
    }

    private static Map<String, Holder<PlacedFeature>> built(Map<String, JsonObject> made) {
        Map<String, Holder<PlacedFeature>> built = new LinkedHashMap<>();
        if (made.isEmpty()) { return built; }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, VanillaRegistries.createLookup());
        for (Map.Entry<String, JsonObject> placement : made.entrySet()) {
            Holder<PlacedFeature> holder = ContentOptionFeatures.build(placement.getKey(), placement.getValue(), ops);
            if (holder != null) { built.put(placement.getKey(), holder); }
        }
        return built;
    }

    private static void dungeons(JsonObject options, Map<String, JsonObject> made, Set<String> dropped) {
        OptionalInt dungeons = ContentTerrain.optionNumber(options, DUNGEONS);
        if (dungeons.isEmpty()) { return; }
        int tries = Math.max(0, Math.min(MAX_COUNT, dungeons.getAsInt()));
        int deep = Math.round(tries * 0.4F);
        retuneCount("monster_room", tries, made, dropped);
        retuneCount("monster_room_deep", deep, made, dropped);
        ContentLog.LOGGER.info("generatorOptions dungeonChance {} sets the dungeon attempts per chunk to {}, with the deep dungeons at {}", dungeons.getAsInt(), tries, deep);
    }

    private static void lavaLakes(JsonObject options, Map<String, JsonObject> made) {
        OptionalInt lakes = ContentTerrain.optionNumber(options, LAVA_LAKES);
        if (lakes.isEmpty()) { return; }
        if (lakes.getAsInt() < 1) {
            ContentLog.LOGGER.error("generatorOptions lavaLakeChance is {}, which must be at least 1, so the game's own lava lakes stay", lakes.getAsInt());
            return;
        }
        double scale = lakes.getAsInt() / (double) LAVA_BASE;
        retuneRarity("lake_lava_underground", scale, made);
        retuneRarity("lake_lava_surface", scale * scale, made);
        ContentLog.LOGGER.info("generatorOptions lavaLakeChance {} scales the underground lava lakes by {} and the surface ones by {} of the game's own rarity", lakes.getAsInt(), scale, scale * scale);
    }

    private static void ore(Ore ore, JsonObject options, Map<String, JsonObject> made, Set<String> dropped) {
        OptionalInt size = ContentTerrain.optionNumber(options, ore.sizeKey());
        OptionalInt count = ContentTerrain.optionNumber(options, ore.countKey());
        OptionalInt low = ContentTerrain.optionNumber(options, ore.lowKey());
        OptionalInt high = ContentTerrain.optionNumber(options, ore.highKey());
        boolean replaced = count.isPresent() || low.isPresent() || high.isPresent();
        if (size.isEmpty() && !replaced) { return; }
        int veins = Math.max(0, Math.min(MAX_COUNT, count.orElse(ore.count())));
        int from = low.orElse(ore.low());
        int to = high.orElse(ore.high());
        for (int index = 0; index < ore.placed().size(); index++) {
            String path = ore.placed().get(index);
            boolean first = index == 0;
            if (replaced && !first && !KEPT.contains(path)) {
                dropped.add(path);
                continue;
            }
            JsonObject placed = placed(path);
            if (placed == null) { continue; }
            OptionalInt wanted = replaced && first ? OptionalInt.of(size.orElse(ore.size())) : size;
            boolean resized = wanted.isPresent() && resize(path, placed, Math.max(0, Math.min(MAX_SIZE, wanted.getAsInt())));
            if (replaced && first) { placed.add("placement", placement(ore.centered(), veins, from, to)); }
            if (resized || (replaced && first)) { made.put(path, placed); }
        }
        if (replaced) { ContentLog.LOGGER.info("generatorOptions places {} as {} vein(s) a chunk {}, and the game's other {} placements are turned off", ore.key(), veins, ore.centered() ? "around " + from + " spread " + to : "from " + from + " to " + to, ore.key()); }
        if (size.isPresent()) { ContentLog.LOGGER.info("generatorOptions sizes the {} veins at {}", ore.key(), size.getAsInt()); }
    }

    private static boolean resize(String path, JsonObject placed, int size) {
        JsonObject configured = ContentOptionFeatures.inline(path, placed);
        if (configured == null) { return false; }
        JsonObject config = configured.has("config") && configured.get("config").isJsonObject() ? configured.getAsJsonObject("config") : configured;
        if (!config.has("size")) { return false; }
        config.addProperty("size", size);
        return true;
    }

    private static JsonArray placement(boolean centered, int veins, int from, int to) {
        int low = from;
        int high = to;
        if (!centered && high < low) {
            low = to;
            high = from;
        }
        if (!centered && high == low) {
            if (low < 255) { high++; }
            else { low--; }
        }
        JsonObject height = Json.obj("type", centered ? "minecraft:trapezoid" : "minecraft:uniform", "min_inclusive", Json.obj("absolute", centered ? from - to : low),
                "max_inclusive", Json.obj("absolute", centered ? Math.max(from - to, from + to - 2) : high - 1));
        return Json.arr(Json.obj("type", "minecraft:count", "count", veins), Json.obj("type", "minecraft:in_square"), Json.obj("type", "minecraft:height_range", "height", height), Json.obj("type", "minecraft:biome"));
    }

    private static void retuneCount(String path, int tries, Map<String, JsonObject> made, Set<String> dropped) {
        if (tries == 0) {
            dropped.add(path);
            return;
        }
        JsonObject placed = placed(path);
        JsonObject count = placed == null ? null : modifier(placed, "minecraft:count");
        if (count == null) { return; }
        count.addProperty("count", tries);
        made.put(path, placed);
    }

    private static void retuneRarity(String path, double scale, Map<String, JsonObject> made) {
        JsonObject placed = placed(path);
        JsonObject rarity = placed == null ? null : modifier(placed, "minecraft:rarity_filter");
        if (rarity == null) { return; }
        rarity.addProperty("chance", Math.max(1, Math.round(Json.getInt(rarity, "chance", 1) * scale)));
        made.put(path, placed);
    }

    @Nullable private static JsonObject modifier(JsonObject placed, String type) {
        if (!placed.has("placement") || !placed.get("placement").isJsonArray()) { return null; }
        for (JsonElement modifier : placed.getAsJsonArray("placement")) {
            if (modifier.isJsonObject() && type.equals(Json.getString(modifier.getAsJsonObject(), "type", ""))) { return modifier.getAsJsonObject(); }
        }
        return null;
    }

    @Nullable private static JsonObject placed(String path) {
        ResourceLocation file = WorldgenFiles.id(ContentFormats.PLACED_FEATURES, ResourceLocation.withDefaultNamespace(path));
        return GameData.has(file) ? GameData.json(file) : null;
    }
}
