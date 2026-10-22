package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.AmbienceDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionPortalDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.PortalDef;
import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.content.def.WindDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentDimensionParser {
    private static final int CLOUD_HEIGHT = 128;
    private static final int CYCLE_LOW = 1000;
    private static final int THUNDER_LOW = 3600;
    private static final int THUNDER_HIGH = 15600;
    private static final int CALM_LOW = 12000;
    private static final int CALM_HIGH = 180000;
    private static final String RAIN_PARTICLE = "minecraft:rain";
    private static final String RAIN_SOUND = "minecraft:weather.rain";
    private static final String WHITE = "#FFFFFF";
    private static final float MAX_ANGLE = 180.0F;
    private static final String CYCLE = "a weather cycle";
    private static final String AMBIENCE = "an ambience";
    private static final String WIND = "a wind";
    private static final float GUST = 15.0F;
    private static final float MAX_GUST = 90.0F;
    private static final float SWING = 30.0F;
    private static final int EVERY_LOW = 200;
    private static final int EVERY_HIGH = 600;
    private static final int MUSIC_LOW = 12000;
    private static final int MUSIC_HIGH = 24000;
    private static final float SOUND_CHANCE = 0.0111F;
    private static final float PARTICLE_CHANCE = 0.00625F;
    private static final String SUN = "minecraft:textures/environment/sun.png";
    private static final String MOON = "minecraft:textures/environment/moon_phases.png";
    private static final int STARS = 1500;
    private static final float STAR_SIZE = 0.15F;
    private static final Set<String> KNOWN_TERRAIN = Set.of(DimensionDef.OVERWORLD, DimensionDef.FLAT, DimensionDef.VOID, DimensionDef.NETHER, DimensionDef.END);
    private static final List<String> FLAT_DEFAULT = List.of("minecraft:bedrock", "59*minecraft:stone", "3*minecraft:dirt", "minecraft:grass_block");

    private ContentDimensionParser() {}

    static DimensionDef parse(ResourceLocation key, JsonObject json) {
        JsonObject terrain = Json.getJsonObject(json, "terrain", new JsonObject());
        JsonObject biomes = Json.getJsonObject(json, "biomes", new JsonObject());
        JsonObject sky = Json.getJsonObject(json, "sky", new JsonObject());
        if (json.has("id")) { ContentLog.LOGGER.debug("Dimension {} names an id, which this line does not use, the dimension being {} everywhere", key, key); }
        if (json.has("keepLoaded")) { ContentLog.LOGGER.debug("Dimension {} asks to be kept loaded, which this line leaves to forceload", key); }
        String type = Settings.lower(Json.getString(terrain, "type", DimensionDef.OVERWORLD));
        if (!KNOWN_TERRAIN.contains(type)) {
            ContentLog.LOGGER.error("Dimension {} asks for terrain '{}', which is not one of {}, using {}", key, type, KNOWN_TERRAIN, DimensionDef.OVERWORLD);
            type = DimensionDef.OVERWORLD;
        }
        String source = Settings.lower(Json.getString(biomes, "source", DimensionDef.INHERIT));
        List<PickDef> biomeList = ContentWorldgenParser.picks(biomes, "biomeList", "biome", ContentListBiomes.WEIGHT);
        if (!DimensionDef.SINGLE.equals(source) && !DimensionDef.INHERIT.equals(source) && !DimensionDef.LIST.equals(source)) {
            ContentLog.LOGGER.error("Dimension {} asks for biome source '{}', which is not {}, {} or {}, using {}", key, source, DimensionDef.SINGLE, DimensionDef.INHERIT, DimensionDef.LIST, DimensionDef.INHERIT);
            source = DimensionDef.INHERIT;
        }
        else if (DimensionDef.LIST.equals(source) && biomeList.isEmpty()) {
            ContentLog.LOGGER.error("Dimension {} asks for biome source '{}' but gives no biomeList, using {}", key, source, DimensionDef.INHERIT);
            source = DimensionDef.INHERIT;
        }
        int minHeight = terrain.has("minHeight") ? Json.getInt(terrain, "minHeight") : DimensionDef.UNSET;
        int maxHeight = terrain.has("maxHeight") ? Json.getInt(terrain, "maxHeight") : DimensionDef.UNSET;
        if ((minHeight == DimensionDef.UNSET) != (maxHeight == DimensionDef.UNSET)) {
            ContentLog.LOGGER.error("Dimension {} names only one of minHeight and maxHeight, so both are left to the terrain type", key);
            minHeight = DimensionDef.UNSET;
            maxHeight = DimensionDef.UNSET;
        }
        else if (minHeight != DimensionDef.UNSET) {
            minHeight = Mth.floor(minHeight / 16.0D) * 16;
            maxHeight = Mth.ceil(maxHeight / 16.0D) * 16;
            if (maxHeight - minHeight < 16 || maxHeight - minHeight > 4064 || minHeight < -2032 || maxHeight > 2032) {
                ContentLog.LOGGER.error("Dimension {} asks for heights {} to {}, which are outside what a dimension can hold (-2032 to 2032, at least 16 apart, at most 4064), so they are left to the terrain type", key, minHeight, maxHeight);
                minHeight = DimensionDef.UNSET;
                maxHeight = DimensionDef.UNSET;
            }
        }
        JsonObject options = customized(key, terrain);
        String fog = ContentParserSkyLook.samples(sky) ? "" : Json.getString(sky, "fogColor", "").trim();
        String skyColor = Json.getString(sky, "skyColor", "").trim();
        String cloudColor = Json.getString(sky, "cloudColor", "").trim();
        int cloudHeight = Json.getInt(sky, "cloudHeight", CLOUD_HEIGHT);
        int clouds = cloudColor.isEmpty() ? -1 : ContentParser.color(cloudColor, key + " cloudColor") & 0xFFFFFF;
        String respawn = Json.getString(sky, "respawnDimension", "").trim();
        ResourceLocation respawnDimension = respawn.isEmpty() ? null : ResourceLocation.tryParse(ContentFormats.dimensionId(respawn));
        if (!respawn.isEmpty() && respawnDimension == null) { ContentLog.LOGGER.error("Dimension {} names respawnDimension '{}', which is not a dimension id, so respawns stay where the game puts them", key, respawn); }
        DimensionTraitsDef traits = traits(key, json);
        return new DimensionDef(key, type, flatOptions(terrain), Json.getBoolean(terrain, "structures", true), source,
                Json.getString(biomes, "biome", "minecraft:plains").trim(), biomeList, minHeight, maxHeight,
                options.has("seaLevel") ? Json.getInt(options, "seaLevel") : -1, Json.getBoolean(options, "useLavaOceans", false),
                Json.getBoolean(sky, "hasSkyLight", true), Json.getBoolean(sky, "surfaceWorld", true), Json.getBoolean(sky, "spawning", true),
                cloudHeight, Math.max(1.0E-5D, Json.getDouble(sky, "movementFactor", 1.0D)),
                fog.isEmpty() ? -1 : ContentParser.color(fog, key + " fogColor") & 0xFFFFFF, skyColor.isEmpty() ? -1 : ContentParser.color(skyColor, key + " skyColor") & 0xFFFFFF,
                clouds, Json.getLong(sky, "fixedTime", -1L),
                Json.getBoolean(sky, "sunriseColors", true), Json.getBoolean(sky, "nether", false), Json.getBoolean(sky, "beds", true),
                Json.getBoolean(sky, "waterVaporizes", false), Json.getBoolean(sky, "showFog", false), Mth.clamp(Json.getFloat(sky, "ambientLight", 0.0F), 0.0F, 1.0F),
                Json.getFloat(sky, "starBrightness", -1.0F), Json.getBoolean(sky, "renderSky", true), Json.getBoolean(sky, "renderClouds", true),
                Json.getBoolean(sky, "renderWeather", true), respawnDimension, Json.getBoolean(sky, "respawn", true),
                Json.getInt(sky, "groundLevel", 63), gameRules(key, json), Json.strings(json, "requires"), portal(key, json), options, traits,
                ContentParserSkyLook.look(key, sky, cloudHeight, clouds), ContentParserTimeWindows.windows(key, json, traits.precipitation()), Json.paths(json));
    }

    private static DimensionTraitsDef traits(ResourceLocation key, JsonObject json) {
        JsonObject physics = Json.getJsonObject(json, "physics", new JsonObject());
        JsonObject time = Json.getJsonObject(json, "time", new JsonObject());
        JsonObject weather = Json.getJsonObject(json, "weather", new JsonObject());
        long day = Json.getInt(time, "dayLength", (int) DimensionTraitsDef.VANILLA_DAY);
        if (day <= 0L) {
            ContentLog.LOGGER.error("Dimension {} gives a dayLength of {}, which is not above zero, using {}", key, day, DimensionTraitsDef.VANILLA_DAY);
            day = DimensionTraitsDef.VANILLA_DAY;
        }
        boolean precipitation = Json.getBoolean(weather, "precipitation", true);
        boolean alwaysRain = Json.getBoolean(weather, "alwaysRain", false);
        if (alwaysRain && !precipitation) {
            ContentLog.LOGGER.error("Dimension {} asks for alwaysRain with precipitation off, so it stays dry", key);
            alwaysRain = false;
        }
        JsonObject sky = Json.getJsonObject(json, "sky", new JsonObject());
        return new DimensionTraitsDef(factor(key, physics, "gravity"), factor(key, physics, "fallDamage"), factor(key, physics, "arrowGravity"), day,
                precipitation, Json.getBoolean(weather, "lightning", true), Json.getBoolean(weather, "snow", true),
                Json.getBoolean(weather, "freeze", true), alwaysRain, weather.has("cycle") ? cycle(key, Json.getJsonObject(weather, "cycle")) : null,
                weather.has("rain") || weather.has("wind") ? rain(key, Json.getJsonObject(weather, "rain", new JsonObject()), weather.has("wind") ? wind(key, Json.getJsonObject(weather, "wind")) : null) : null, sky(key, sky),
                sky.has("moon") ? moon(key, Json.getJsonObject(sky, "moon")) : null, json.has("ambience") ? ambience(key, Json.getJsonObject(json, "ambience")) : null);
    }

    private static DimensionTraitsDef.Moon moon(ResourceLocation key, JsonObject moon) {
        ResourceLocation texture = texture(key, Json.getString(moon, "texture", MOON));
        return new DimensionTraitsDef.Moon(texture == null ? ResourceLocation.parse(MOON) : texture, Math.max(0.0F, Json.getFloat(moon, "size", DimensionTraitsDef.VANILLA_MOON)));
    }

    private static AmbienceDef ambience(ResourceLocation key, JsonObject json) {
        int[] delay = span(key, json, AMBIENCE, "musicDelay", MUSIC_LOW, MUSIC_HIGH);
        return new AmbienceDef(id(key, json, "music"), delay[0], delay[1], id(key, json, "loopSound"), id(key, json, "ambientSound"), ranged(key, json, AMBIENCE, "soundChance", SOUND_CHANCE, 1.0F),
                id(key, json, "particle"), ranged(key, json, AMBIENCE, "particleChance", PARTICLE_CHANCE, 1.0F),
                json.has("particleColor") ? ContentParser.color(Json.getString(json, "particleColor"), key + " ambience particleColor") & 0xFFFFFF : AmbienceDef.NO_COLOR);
    }

    @Nullable private static ResourceLocation id(ResourceLocation key, JsonObject json, String member) {
        String name = Json.getString(json, member, "").trim();
        if (name.isEmpty()) { return null; }
        ResourceLocation id = ResourceLocation.tryParse(Settings.lower(name));
        if (id == null) { ContentLog.LOGGER.error("Dimension {} gives an ambience {} of '{}', which is no id, ignoring it", key, member, name); }
        return id;
    }

    private static float ranged(ResourceLocation key, JsonObject json, String block, String member, float fallback, float most) {
        float value = Json.getFloat(json, member, fallback);
        if (value >= 0.0F && value <= most) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives {} {} of {}, which is not from 0 to {}, using {}", key, block, member, value, most, fallback);
        return fallback;
    }

    @Nullable private static DimensionTraitsDef.Sky sky(ResourceLocation key, JsonObject sky) {
        if (!sky.has("sun") && !sky.has("moon") && !sky.has("bodies") && !sky.has("stars")) { return null; }
        JsonObject sun = Json.getJsonObject(sky, "sun", new JsonObject());
        JsonObject stars = Json.getJsonObject(sky, "stars", new JsonObject());
        List<DimensionTraitsDef.Body> bodies = null;
        if (sky.has("bodies")) {
            List<DimensionTraitsDef.Body> found = new ArrayList<>();
            for (JsonElement element : Json.getJsonArray(sky, "bodies")) {
                if (!element.isJsonObject() || !element.getAsJsonObject().has("texture")) {
                    ContentLog.LOGGER.error("Dimension {} lists a sky body {} with no texture, ignoring it", key, element);
                    continue;
                }
                JsonObject body = element.getAsJsonObject();
                ResourceLocation texture = texture(key, Json.getString(body, "texture"));
                if (texture == null) { continue; }
                found.add(new DimensionTraitsDef.Body(texture, Math.max(0.0F, Json.getFloat(body, "size", 20.0F)),
                        Json.getFloat(body, "angle", 180.0F), Json.getFloat(body, "tilt", 0.0F), Json.getBoolean(body, "followsTime", true)));
            }
            bodies = List.copyOf(found);
        }
        ResourceLocation sunTexture = texture(key, Json.getString(sun, "texture", SUN));
        return new DimensionTraitsDef.Sky(sunTexture == null ? ResourceLocation.parse(SUN) : sunTexture, Math.max(0.0F, Json.getFloat(sun, "size", DimensionTraitsDef.VANILLA_SUN)), bodies,
                Math.max(0, Json.getInt(stars, "count", STARS)), Math.max(0.0F, Json.getFloat(stars, "size", STAR_SIZE)));
    }

    @Nullable private static ResourceLocation texture(ResourceLocation key, String named) {
        ResourceLocation texture = ResourceLocation.tryParse(Settings.lower(named));
        if (texture == null) { ContentLog.LOGGER.error("Dimension {} names a sky texture '{}' that is no id, ignoring it", key, named); }
        return texture;
    }

    private static double factor(ResourceLocation key, JsonObject physics, String member) {
        if (!physics.has(member)) { return -1.0D; }
        double value = Json.getFloat(physics, member);
        if (value > 0.0D) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives physics {} of {}, which is not above zero, ignoring it", key, member, value);
        return -1.0D;
    }

    private static DimensionTraitsDef.Cycle cycle(ResourceLocation key, JsonObject json) {
        int[] rain = span(key, json, CYCLE, "rainTicks", CYCLE_LOW, 4600);
        int[] clear = span(key, json, CYCLE, "clearTicks", CYCLE_LOW, 3000);
        int[] thunder = json.has("thunderTicks") ? span(key, json, CYCLE, "thunderTicks", THUNDER_LOW, THUNDER_HIGH) : new int[] {0, 0};
        int[] calm = span(key, json, CYCLE, "calmTicks", CALM_LOW, CALM_HIGH);
        return new DimensionTraitsDef.Cycle(rain[0], rain[1], clear[0], clear[1], strength(key, json, "maxStrength", 0.6F), thunder[0], thunder[1], calm[0], calm[1],
                strength(key, json, "thunderStrength", 1.0F));
    }

    private static float strength(ResourceLocation key, JsonObject json, String member, float fallback) {
        float strength = Json.getFloat(json, member, fallback);
        if (strength > 0.0F && strength <= 1.0F) { return strength; }
        ContentLog.LOGGER.error("Dimension {} gives a weather cycle {} of {}, which is not above 0 and at most 1, using {}", key, member, strength, fallback);
        return fallback;
    }

    private static RainDef rain(ResourceLocation key, JsonObject json, @Nullable WindDef wind) {
        ResourceLocation particle = ResourceLocation.tryParse(Settings.lower(Json.getString(json, "particle", RAIN_PARTICLE)));
        if (particle == null) {
            ContentLog.LOGGER.error("Dimension {} names a rain particle that is no particle id, using {}", key, RAIN_PARTICLE);
            particle = ResourceLocation.parse(RAIN_PARTICLE);
        }
        ResourceLocation sound = ResourceLocation.tryParse(Settings.lower(Json.getString(json, "sound", RAIN_SOUND)));
        if (sound == null) {
            ContentLog.LOGGER.error("Dimension {} names a rain sound that is no sound id, using {}", key, RAIN_SOUND);
            sound = ResourceLocation.parse(RAIN_SOUND);
        }
        float angle = Json.getFloat(json, "angle", 0.0F);
        if (angle < 0.0F || angle > MAX_ANGLE) {
            ContentLog.LOGGER.error("Dimension {} gives a rain angle of {}, which is not from 0 to 180, using 0", key, angle);
            angle = 0.0F;
        }
        return new RainDef(particle, sound, Math.max(0.0F, Json.getFloat(json, "volume", 0.2F)), Math.max(0, Json.getInt(json, "interval", 3)),
                ContentParser.color(Json.getString(json, "color", WHITE), key + " rain color"),
                ContentParser.color(Json.getString(json, "snowColor", WHITE), key + " rain snowColor"), angle, Json.getFloat(json, "heading", 0.0F), Json.getBoolean(json, "splashUpward", false), wind);
    }

    private static WindDef wind(ResourceLocation key, JsonObject json) {
        int[] every = span(key, json, WIND, "every", EVERY_LOW, EVERY_HIGH);
        return new WindDef(ranged(key, json, WIND, "gust", GUST, MAX_GUST), every[0], every[1], ranged(key, json, WIND, "swing", SWING, MAX_ANGLE));
    }

    private static int[] span(ResourceLocation key, JsonObject json, String block, String member, int low, int high) {
        if (!json.has(member)) { return new int[] {low, high}; }
        JsonElement value = json.get(member);
        int min;
        int max;
        if (value.isJsonArray() && value.getAsJsonArray().size() == 2) {
            min = Json.asInt(value.getAsJsonArray().get(0), member, 0);
            max = Json.asInt(value.getAsJsonArray().get(1), member, 0);
        }
        else if (value.isJsonPrimitive()) {
            min = Json.asInt(value, member, 0);
            max = min;
        }
        else {
            ContentLog.LOGGER.error("Dimension {} gives {} {} of {}, which is neither a number nor [min, max], using [{}, {}]", key, block, member, value, low, high);
            return new int[] {low, high};
        }
        if (min <= 0 || max < min) {
            ContentLog.LOGGER.error("Dimension {} gives {} {} of {}, which needs 0 < min <= max, using [{}, {}]", key, block, member, value, low, high);
            return new int[] {low, high};
        }
        return new int[] {min, max};
    }

    @Nullable private static DimensionPortalDef portal(ResourceLocation key, JsonObject json) {
        if (!json.has("portal")) { return null; }
        JsonObject entry = Json.getJsonObject(json, "portal");
        List<String> frames = Json.strings(entry, "frames");
        if (frames.isEmpty()) {
            ContentLog.LOGGER.error("Dimension {} has a portal section naming no frames, so nothing could ever open it", key);
            return null;
        }
        int color = ContentParser.color(Json.getString(entry, "color", WHITE), key + " portal color");
        String back = Settings.lower(Json.getString(entry, "return", DimensionPortalDef.BUILT));
        if (!DimensionPortalDef.BUILT.equals(back) && !DimensionPortalDef.PLAYER.equals(back) && !DimensionPortalDef.NONE.equals(back)) {
            ContentLog.LOGGER.error("Dimension {} asks for a return of '{}', which is none of {}, {} or {}, building one instead", key, back, DimensionPortalDef.BUILT, DimensionPortalDef.PLAYER, DimensionPortalDef.NONE);
            back = DimensionPortalDef.BUILT;
        }
        PortalDef travel = ContentParser.portal(key, json, key, false, true);
        return travel == null ? null : new DimensionPortalDef(frames, Json.getString(entry, "ignitedBy", "minecraft:flint_and_steel").trim(), color, back, travel);
    }

    private static List<String> flatOptions(JsonObject terrain) {
        if (!terrain.has("generatorOptions")) { return FLAT_DEFAULT; }
        JsonElement options = terrain.get("generatorOptions");
        if (options.isJsonArray()) { return Json.strings(terrain, "generatorOptions"); }
        if (!options.isJsonPrimitive() || options.getAsString().trim().isEmpty() || options.getAsString().trim().startsWith("{")) { return FLAT_DEFAULT; }
        return List.of(options.getAsString().trim());
    }

    private static JsonObject customized(ResourceLocation key, JsonObject terrain) {
        JsonElement options = terrain.get("generatorOptions");
        if (options == null) { return new JsonObject(); }
        if (options.isJsonObject()) { return options.getAsJsonObject(); }
        if (!options.isJsonPrimitive() || !options.getAsString().trim().startsWith("{")) { return new JsonObject(); }
        try { return Json.GSON.fromJson(options.getAsString(), JsonObject.class); }
        catch (JsonParseException unreadable) {
            ContentLog.LOGGER.error("Dimension {} has generatorOptions '{}', which is not readable JSON, so the terrain keeps its own settings", key, options.getAsString());
            return new JsonObject();
        }
    }

    private static Map<String, String> gameRules(ResourceLocation key, JsonObject json) {
        if (!json.has("gameRules")) { return Map.of(); }
        Map<String, String> found = new LinkedHashMap<>();
        rules(key, Json.getJsonObject(json, "gameRules"), found);
        return Map.copyOf(found);
    }

    static void rules(ResourceLocation key, JsonObject set, Map<String, String> into) {
        for (Map.Entry<String, JsonElement> rule : set.entrySet()) {
            if (!rule.getValue().isJsonPrimitive()) {
                ContentLog.LOGGER.error("Dimension {} sets game rule '{}' to something that is not a value, ignoring it", key, rule.getKey());
                continue;
            }
            into.put(rule.getKey(), rule.getValue().getAsString());
        }
    }
}
