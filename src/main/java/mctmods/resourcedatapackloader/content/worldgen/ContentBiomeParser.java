package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.content.def.BiomeSpawnDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentBiomeParser {
    public static final ResourceLocation PLAINS = ResourceLocation.fromNamespaceAndPath("minecraft", "plains");
    private static final Gson GSON = new Gson();
    private static final List<String> RATE_KEYS = List.of("surfaceDay", "surfaceNight", "undergroundDay", "undergroundNight");
    private static final Set<String> RUBIC_KEYS = Set.of("id", "baseHeight", "heightVariation", "skyStone", "skyIslands", "skyThickness");
    private static final Set<String> CATEGORIES = Set.of("monster", "creature", "ambient", "water_creature", "water_ambient", "underground_water_creature", "axolotls", "misc");

    private ContentBiomeParser() {}

    @Nullable public static BiomeDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Biome file {} is empty, ignoring it", key);
            return null;
        }
        for (String rubic : RUBIC_KEYS) {
            if (json.has(rubic)) { ContentLog.LOGGER.info("Biome {} sets '{}', which this line does not read: terrain height is the noise settings' and the sky keys were the rubic world's", key, rubic); }
        }
        JsonObject placement = GsonHelper.getAsJsonObject(json, "placement", new JsonObject());
        if (placement.has("villageSpawn")) { ContentLog.LOGGER.info("Biome {} sets placement.villageSpawn, which this line does not read: villagers come with the village structure itself", key); }
        JsonObject rates = GsonHelper.getAsJsonObject(json, "spawnRates", new JsonObject());
        for (String rate : rates.keySet()) {
            if (!RATE_KEYS.contains(rate)) { ContentLog.LOGGER.error("Biome {} sets the spawn rate '{}', which is not one of {}, so it does nothing. These are how often hostile mobs spawn, not creature types", key, rate, RATE_KEYS); }
        }
        ResourceLocation base = ContentParser.location(GsonHelper.getAsString(json, "baseBiome", ""));
        Map<String, Integer> decoration = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, "decoration", new JsonObject()).entrySet()) {
            if (entry.getValue().isJsonPrimitive()) { decoration.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue().getAsInt()); }
        }
        return new BiomeDef(key, GsonHelper.getAsString(json, "name", key.getPath()).trim(),
                GsonHelper.getAsFloat(json, "temperature", 0.5F),
                GsonHelper.getAsFloat(json, "rainfall", 0.5F),
                GsonHelper.getAsBoolean(json, "rain", true),
                GsonHelper.getAsBoolean(json, "snow", false),
                color(json, "waterColor", key), color(json, "grassColor", key), color(json, "foliageColor", key),
                base == null ? PLAINS : base,
                GsonHelper.getAsString(json, "topBlock", "").trim(),
                GsonHelper.getAsString(json, "fillerBlock", "").trim(),
                GsonHelper.getAsString(json, "stoneBlock", "").trim(),
                Json.strings(json, "types"),
                GsonHelper.getAsString(placement, "climate", "").trim().toLowerCase(Locale.ROOT),
                Math.max(0, GsonHelper.getAsInt(placement, "weight", 10)),
                GsonHelper.getAsBoolean(placement, "playerSpawn", false),
                GsonHelper.getAsBoolean(placement, "villages", false),
                GsonHelper.getAsString(json, "villageType", "").trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsBoolean(placement, "strongholds", false),
                Map.copyOf(decoration),
                spawnChance(key, GsonHelper.getAsFloat(json, "spawnChance", 0.1F)),
                rate(rates, "surfaceDay"), rate(rates, "surfaceNight"), rate(rates, "undergroundDay"), rate(rates, "undergroundNight"),
                GsonHelper.getAsBoolean(json, "keepDefaultSpawns", false),
                spawns(key, json),
                json.has("minHeight") || json.has("maxHeight"),
                GsonHelper.getAsInt(json, "minHeight", Integer.MIN_VALUE),
                GsonHelper.getAsInt(json, "maxHeight", Integer.MAX_VALUE),
                Json.strings(json, "replaces"), Json.strings(json, "requires"));
    }

    public static List<BiomeSpawnDef> spawns(ResourceLocation key, JsonObject json) {
        if (!json.has("spawns")) { return List.of(); }
        List<BiomeSpawnDef> spawns = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "spawns")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A spawn entry in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            ResourceLocation entity = ContentParser.location(GsonHelper.getAsString(entry, "entity", ""));
            if (entity == null) {
                ContentLog.LOGGER.error("A spawn entry in {} names no entity, skipping it", key);
                continue;
            }
            String category = category(GsonHelper.getAsString(entry, "type", "creature"));
            if (category == null) {
                ContentLog.LOGGER.error("A spawn entry in {} has creature type '{}', which is not one of monster, creature, ambient or water, skipping it", key, GsonHelper.getAsString(entry, "type", ""));
                continue;
            }
            int min = Math.max(1, GsonHelper.getAsInt(entry, "min", 1));
            spawns.add(new BiomeSpawnDef(entity, category, Math.max(1, GsonHelper.getAsInt(entry, "weight", 10)), min, Math.max(min, GsonHelper.getAsInt(entry, "max", min))));
        }
        return List.copyOf(spawns);
    }

    @Nullable private static String category(String written) {
        String wanted = written.trim().toLowerCase(Locale.ROOT);
        if ("water".equals(wanted)) { return "water_creature"; }
        return CATEGORIES.contains(wanted) ? wanted : null;
    }

    private static float spawnChance(ResourceLocation key, float wanted) {
        if (wanted < 0.99F) { return Math.max(0.0F, wanted); }
        ContentLog.LOGGER.error("Biome {} asks for a spawnChance of {}. The game keeps starting another herd for as long as that roll succeeds, so at 1 it never stops and the world fills until it runs out of room. Using 0.99 instead", key, wanted);
        return 0.99F;
    }

    private static float rate(JsonObject rates, String name) { return rates.has(name) ? Math.max(0.0F, GsonHelper.getAsFloat(rates, name)) : BiomeDef.NO_RATE; }

    private static int color(JsonObject json, String name, ResourceLocation key) {
        if (!json.has(name)) { return BiomeDef.NO_COLOR; }
        return ContentParser.color(GsonHelper.getAsString(json, name, ""), key);
    }
}
