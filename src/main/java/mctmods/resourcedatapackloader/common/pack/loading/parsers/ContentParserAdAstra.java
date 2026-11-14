package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.generated.ContentFormats;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.AdAstraDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.integration.adastra.ContentAdAstra;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class ContentParserAdAstra {
    private static final List<String> KINDS = List.of(AdAstraDef.PLANET, AdAstraDef.MOON, AdAstraDef.STATION, AdAstraDef.ASTEROIDS);
    private static final double COMFY = 22.0D;
    private static final double HOT_SPAN = 49.0D;
    private static final double COLD_SPAN = 73.0D;
    private static final float OVERWORLD_SOLAR = 16.0F;
    private static final String ORBIT_BIOME = ContentAdAstra.MOD_ID + ":orbit";
    private static final List<String> NO_STATION_EQUIVALENT = List.of("showName", "checklist");

    private ContentParserAdAstra() {}

    @Nullable public static AdAstraDef adastra(ResourceLocation key, JsonObject dimension) {
        if (!dimension.has("adastra")) { return null; }
        if (!ContentAdAstra.active()) {
            ContentLog.LOGGER.info("Dimension {} has an adastra block, but Ad Astra is not loaded, so the block is skipped", key);
            return null;
        }
        JsonObject json = Json.getJsonObject(dimension, "adastra");
        String owner = "Dimension " + key + " adastra";
        String kind = Json.choice(json, "kind", AdAstraDef.PLANET, owner, KINDS);
        if (AdAstraDef.ASTEROIDS.equals(kind)) {
            ContentLog.LOGGER.error("Dimension {} is of kind asteroids, which Ad Astra has no equivalent for, so the adastra block is skipped", key);
            return null;
        }
        if (AdAstraDef.STATION.equals(kind)) { return station(key, json); }
        boolean moon = AdAstraDef.MOON.equals(kind);
        String parent = Json.getString(json, "parent", moon ? "" : AdAstraDef.SOL).trim();
        if (parent.isEmpty()) {
            ContentLog.LOGGER.error("Dimension {} is a moon with no parent, so the adastra block is skipped", key);
            return null;
        }
        int tier = Json.getInt(json, "tier", 1);
        JsonObject air = Json.getJsonObject(json, "atmosphere", new JsonObject());
        List<ResourceLocation> launch = new ArrayList<>();
        for (String named : Json.strings(json, "launchFrom")) {
            ResourceLocation site = ResourceLocation.tryParse(ContentFormats.dimensionId(named.trim()));
            if (site == null) { ContentLog.LOGGER.error("Dimension {} names launchFrom '{}', which is not a dimension id, ignoring it", key, named); }
            else { launch.add(site); }
        }
        return new AdAstraDef(kind, parent, Json.getBoolean(json, "reachable", true), Json.getInt(json, "minTier", tier), Json.getBoolean(air, "breathable", false),
                celsius(Json.getFloat(air, "temperature", 0.0F)), Json.getBoolean(air, "corrosive", false), Math.round(Math.max(0.0F, Json.getFloat(json, "solarEnergy", 1.0F)) * OVERWORLD_SOLAR),
                height(key, json, "exitHeight"), height(key, json, "landingHeight"), Math.max(0.0F, Json.getFloat(json, "fuelMultiplier", 1.0F)), List.copyOf(launch), moon ? null : system(key, parent), ContentParserCelestial.mapBody(key, json, kind), Map.of(), AdAstraDef.STATION_STRUCTURE);
    }

    public static JsonObject stationed(ResourceLocation key, JsonObject dimension) {
        if (!dimension.has("adastra") || !ContentAdAstra.active() || !AdAstraDef.STATION.equals(Settings.lower(Json.getString(Json.getJsonObject(dimension, "adastra", new JsonObject()), "kind", "").trim()))) { return dimension; }
        if (dimension.has("terrain") || dimension.has("biomes")) { ContentLog.LOGGER.error("Dimension {} is an Ad Astra station, whose world is always an empty orbit, so its terrain and biomes are ignored", key); }
        JsonObject made = dimension.deepCopy();
        JsonObject orbit = orbitWorld();
        made.add("terrain", orbit.get("terrain"));
        made.add("biomes", orbit.get("biomes"));
        return made;
    }

    public static JsonObject orbitWorld() {
        JsonObject terrain = new JsonObject();
        terrain.addProperty("type", DimensionDef.VOID);
        JsonObject biomes = new JsonObject();
        biomes.addProperty("source", DimensionDef.SINGLE);
        biomes.addProperty("biome", ORBIT_BIOME);
        JsonObject json = new JsonObject();
        json.add("terrain", terrain);
        json.add("biomes", biomes);
        return json;
    }

    @Nullable private static AdAstraDef station(ResourceLocation key, JsonObject json) {
        String parent = Json.getString(json, "parent", "").trim();
        if (parent.isEmpty()) {
            ContentLog.LOGGER.error("Dimension {} is a station with no parent, so the adastra block is skipped", key);
            return null;
        }
        for (String name : NO_STATION_EQUIVALENT) {
            if (json.has(name)) { ContentLog.LOGGER.info("Dimension {} adastra sets {}, which Ad Astra has no equivalent for, ignoring it", key, name); }
        }
        return new AdAstraDef(AdAstraDef.STATION, parent, true, Json.getInt(json, "minTier", Json.getInt(json, "tier", AdAstraDef.UNSET)), false, (short) 0, false, 0, AdAstraDef.UNSET, AdAstraDef.UNSET, 1.0D, List.of(), null, null, recipe(key, json), structure(key, json));
    }

    private static Map<String, Integer> recipe(ResourceLocation key, JsonObject json) {
        Map<String, Integer> found = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, "recipe", new JsonObject()).entrySet()) {
            String name = entry.getKey().trim();
            JsonElement count = entry.getValue();
            if (!count.isJsonPrimitive() || !count.getAsJsonPrimitive().isNumber() || count.getAsInt() < 1) { ContentLog.LOGGER.error("Dimension {} asks for {} of '{}' in its station recipe, which is not a count of 1 or more, ignoring it", key, count, name); }
            else if (ingredient(key, name)) { found.put(name, count.getAsInt()); }
        }
        return Collections.unmodifiableMap(found);
    }

    private static boolean ingredient(ResourceLocation key, String name) {
        boolean tag = name.startsWith("#");
        String id = tag ? name.substring(1) : name;
        int colons = id.length() - id.replace(":", "").length();
        if (!tag && colons == 0) { ContentLog.LOGGER.error("Dimension {} asks for '{}' in its station recipe, an ore dictionary name, which this line has no equivalent for (write the item tag as '#namespace:path'), ignoring it", key, name); }
        else if (colons > 1) { ContentLog.LOGGER.error("Dimension {} asks for '{}' in its station recipe, which carries a metadata value, and items have none on this line, ignoring it", key, name); }
        else if (ResourceLocation.tryParse(id) == null) { ContentLog.LOGGER.error("Dimension {} asks for '{}' in its station recipe, which is no item or tag id, ignoring it", key, name); }
        else { return true; }
        return false;
    }

    private static ResourceLocation structure(ResourceLocation key, JsonObject json) {
        String asked = Json.getString(json, "structure", AdAstraDef.STATION_STRUCTURE.toString()).trim();
        ResourceLocation named = ResourceLocation.tryParse(asked);
        if (named != null) { return named; }
        ContentLog.LOGGER.error("Dimension {} names structure '{}', which is not a structure id, so it uses {}", key, asked, AdAstraDef.STATION_STRUCTURE);
        return AdAstraDef.STATION_STRUCTURE;
    }

    public static short celsius(float level) {
        double clamped = Mth.clamp(level, -1.0F, 1.0F);
        double value = COMFY + clamped * (clamped >= 0.0D ? HOT_SPAN : COLD_SPAN);
        return (short) (value < 0.0D ? -Math.floor(-value + 0.5D) : Math.floor(value + 0.5D));
    }

    private static int height(ResourceLocation key, JsonObject json, String name) {
        int height = Json.getInt(json, name, AdAstraDef.UNSET);
        if (height == AdAstraDef.UNSET || height > 0) { return height; }
        ContentLog.LOGGER.error("Dimension {} adastra {} is {}, which is not above 0, so the default height is used", key, name, height);
        return AdAstraDef.UNSET;
    }

    public static ResourceLocation system(ResourceLocation key, String parent) {
        if (AdAstraDef.SOL.equals(Settings.lower(parent))) { return AdAstraDef.SOLAR_SYSTEM; }
        ResourceLocation named = parent.indexOf(':') >= 0 ? ResourceLocation.tryParse(Settings.lower(parent)) : ResourceLocation.tryParse(key.getNamespace() + ":" + Settings.lower(parent));
        if (named != null) { return named; }
        ContentLog.LOGGER.error("Dimension {} names parent '{}', which is not a star system id, so it uses {}", key, parent, AdAstraDef.SOLAR_SYSTEM);
        return AdAstraDef.SOLAR_SYSTEM;
    }
}
