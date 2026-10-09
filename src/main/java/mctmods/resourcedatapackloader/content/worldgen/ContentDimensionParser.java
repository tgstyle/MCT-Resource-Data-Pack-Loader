package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.AmbienceDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.DimensionPortalDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
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
import net.minecraft.util.GsonHelper;
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

    @Nullable public static DimensionDef parse(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "dimension");
        if (json == null) { return null; }
        JsonObject terrain = GsonHelper.getAsJsonObject(json, "terrain", new JsonObject());
        JsonObject biomes = GsonHelper.getAsJsonObject(json, "biomes", new JsonObject());
        JsonObject sky = GsonHelper.getAsJsonObject(json, "sky", new JsonObject());
        if (json.has("id")) { ContentLog.LOGGER.debug("Dimension {} names an id, which this line does not use, the dimension being {} everywhere", key, key); }
        if (json.has("keepLoaded")) { ContentLog.LOGGER.debug("Dimension {} asks to be kept loaded, which this line leaves to forceload", key); }
        String type = Settings.lower(GsonHelper.getAsString(terrain, "type", DimensionDef.OVERWORLD));
        if (!KNOWN_TERRAIN.contains(type)) {
            ContentLog.LOGGER.error("Dimension {} asks for terrain '{}', which is not one of {}, using {}", key, type, KNOWN_TERRAIN, DimensionDef.OVERWORLD);
            type = DimensionDef.OVERWORLD;
        }
        String source = Settings.lower(GsonHelper.getAsString(biomes, "source", DimensionDef.INHERIT));
        if (!DimensionDef.SINGLE.equals(source) && !DimensionDef.INHERIT.equals(source)) {
            ContentLog.LOGGER.error("Dimension {} asks for biome source '{}', which is not {} or {}, using {}", key, source, DimensionDef.SINGLE, DimensionDef.INHERIT, DimensionDef.INHERIT);
            source = DimensionDef.INHERIT;
        }
        int minHeight = terrain.has("minHeight") ? GsonHelper.getAsInt(terrain, "minHeight") : DimensionDef.UNSET;
        int maxHeight = terrain.has("maxHeight") ? GsonHelper.getAsInt(terrain, "maxHeight") : DimensionDef.UNSET;
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
        String fog = ContentParserSkyLook.samples(sky) ? "" : GsonHelper.getAsString(sky, "fogColor", "").trim();
        String skyColor = GsonHelper.getAsString(sky, "skyColor", "").trim();
        String cloudColor = GsonHelper.getAsString(sky, "cloudColor", "").trim();
        int cloudHeight = GsonHelper.getAsInt(sky, "cloudHeight", CLOUD_HEIGHT);
        int clouds = cloudColor.isEmpty() ? -1 : ContentParser.color(cloudColor, key + " cloudColor") & 0xFFFFFF;
        String respawn = GsonHelper.getAsString(sky, "respawnDimension", "").trim();
        ResourceLocation respawnDimension = respawn.isEmpty() ? null : ResourceLocation.tryParse(ContentFormats.dimensionId(respawn));
        if (!respawn.isEmpty() && respawnDimension == null) { ContentLog.LOGGER.error("Dimension {} names respawnDimension '{}', which is not a dimension id, so respawns stay where the game puts them", key, respawn); }
        return new DimensionDef(key, type, flatOptions(terrain), GsonHelper.getAsBoolean(terrain, "structures", true), source,
                GsonHelper.getAsString(biomes, "biome", "minecraft:plains").trim(), minHeight, maxHeight,
                options.has("seaLevel") ? GsonHelper.getAsInt(options, "seaLevel") : -1, GsonHelper.getAsBoolean(options, "useLavaOceans", false),
                GsonHelper.getAsBoolean(sky, "hasSkyLight", true), GsonHelper.getAsBoolean(sky, "surfaceWorld", true), GsonHelper.getAsBoolean(sky, "spawning", true),
                cloudHeight, Math.max(1.0E-5D, GsonHelper.getAsDouble(sky, "movementFactor", 1.0D)),
                fog.isEmpty() ? -1 : ContentParser.color(fog, key + " fogColor") & 0xFFFFFF, skyColor.isEmpty() ? -1 : ContentParser.color(skyColor, key + " skyColor") & 0xFFFFFF,
                clouds, GsonHelper.getAsLong(sky, "fixedTime", -1L),
                GsonHelper.getAsBoolean(sky, "sunriseColors", true), GsonHelper.getAsBoolean(sky, "nether", false), GsonHelper.getAsBoolean(sky, "beds", true),
                GsonHelper.getAsBoolean(sky, "waterVaporizes", false), GsonHelper.getAsBoolean(sky, "showFog", false), Mth.clamp(GsonHelper.getAsFloat(sky, "ambientLight", 0.0F), 0.0F, 1.0F),
                GsonHelper.getAsFloat(sky, "starBrightness", -1.0F), GsonHelper.getAsBoolean(sky, "renderSky", true), GsonHelper.getAsBoolean(sky, "renderClouds", true),
                GsonHelper.getAsBoolean(sky, "renderWeather", true), respawnDimension, GsonHelper.getAsBoolean(sky, "respawn", true),
                GsonHelper.getAsInt(sky, "groundLevel", 63), gameRules(key, json), Json.strings(json, "requires"), portal(key, json), options, traits(key, json),
                ContentParserSkyLook.look(key, sky, cloudHeight, clouds), Json.paths(json));
    }

    private static DimensionTraitsDef traits(ResourceLocation key, JsonObject json) {
        JsonObject physics = GsonHelper.getAsJsonObject(json, "physics", new JsonObject());
        JsonObject time = GsonHelper.getAsJsonObject(json, "time", new JsonObject());
        JsonObject weather = GsonHelper.getAsJsonObject(json, "weather", new JsonObject());
        long day = GsonHelper.getAsInt(time, "dayLength", (int) DimensionTraitsDef.VANILLA_DAY);
        if (day <= 0L) {
            ContentLog.LOGGER.error("Dimension {} gives a dayLength of {}, which is not above zero, using {}", key, day, DimensionTraitsDef.VANILLA_DAY);
            day = DimensionTraitsDef.VANILLA_DAY;
        }
        boolean precipitation = GsonHelper.getAsBoolean(weather, "precipitation", true);
        boolean alwaysRain = GsonHelper.getAsBoolean(weather, "alwaysRain", false);
        if (alwaysRain && !precipitation) {
            ContentLog.LOGGER.error("Dimension {} asks for alwaysRain with precipitation off, so it stays dry", key);
            alwaysRain = false;
        }
        JsonObject sky = GsonHelper.getAsJsonObject(json, "sky", new JsonObject());
        return new DimensionTraitsDef(factor(key, physics, "gravity"), factor(key, physics, "fallDamage"), factor(key, physics, "arrowGravity"), day,
                precipitation, GsonHelper.getAsBoolean(weather, "lightning", true), GsonHelper.getAsBoolean(weather, "snow", true),
                GsonHelper.getAsBoolean(weather, "freeze", true), alwaysRain, weather.has("cycle") ? cycle(key, GsonHelper.getAsJsonObject(weather, "cycle")) : null,
                weather.has("rain") || weather.has("wind") ? rain(key, GsonHelper.getAsJsonObject(weather, "rain", new JsonObject()), weather.has("wind") ? wind(key, GsonHelper.getAsJsonObject(weather, "wind")) : null) : null, sky(key, sky),
                sky.has("moon") ? moon(key, GsonHelper.getAsJsonObject(sky, "moon")) : null, json.has("ambience") ? ambience(key, GsonHelper.getAsJsonObject(json, "ambience")) : null);
    }

    private static DimensionTraitsDef.Moon moon(ResourceLocation key, JsonObject moon) {
        ResourceLocation texture = texture(key, GsonHelper.getAsString(moon, "texture", MOON));
        return new DimensionTraitsDef.Moon(texture == null ? ResourceLocation.parse(MOON) : texture, Math.max(0.0F, GsonHelper.getAsFloat(moon, "size", DimensionTraitsDef.VANILLA_MOON)));
    }

    private static AmbienceDef ambience(ResourceLocation key, JsonObject json) {
        int[] delay = span(key, json, AMBIENCE, "musicDelay", MUSIC_LOW, MUSIC_HIGH);
        return new AmbienceDef(id(key, json, "music"), delay[0], delay[1], id(key, json, "loopSound"), id(key, json, "ambientSound"), ranged(key, json, AMBIENCE, "soundChance", SOUND_CHANCE, 1.0F),
                id(key, json, "particle"), ranged(key, json, AMBIENCE, "particleChance", PARTICLE_CHANCE, 1.0F),
                json.has("particleColor") ? ContentParser.color(GsonHelper.getAsString(json, "particleColor"), key + " ambience particleColor") & 0xFFFFFF : AmbienceDef.NO_COLOR);
    }

    @Nullable private static ResourceLocation id(ResourceLocation key, JsonObject json, String member) {
        String name = GsonHelper.getAsString(json, member, "").trim();
        if (name.isEmpty()) { return null; }
        ResourceLocation id = ResourceLocation.tryParse(Settings.lower(name));
        if (id == null) { ContentLog.LOGGER.error("Dimension {} gives an ambience {} of '{}', which is no id, ignoring it", key, member, name); }
        return id;
    }

    private static float ranged(ResourceLocation key, JsonObject json, String block, String member, float fallback, float most) {
        float value = GsonHelper.getAsFloat(json, member, fallback);
        if (value >= 0.0F && value <= most) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives {} {} of {}, which is not from 0 to {}, using {}", key, block, member, value, most, fallback);
        return fallback;
    }

    @Nullable private static DimensionTraitsDef.Sky sky(ResourceLocation key, JsonObject sky) {
        if (!sky.has("sun") && !sky.has("moon") && !sky.has("bodies") && !sky.has("stars")) { return null; }
        JsonObject sun = GsonHelper.getAsJsonObject(sky, "sun", new JsonObject());
        JsonObject stars = GsonHelper.getAsJsonObject(sky, "stars", new JsonObject());
        List<DimensionTraitsDef.Body> bodies = null;
        if (sky.has("bodies")) {
            List<DimensionTraitsDef.Body> found = new ArrayList<>();
            for (JsonElement element : GsonHelper.getAsJsonArray(sky, "bodies")) {
                if (!element.isJsonObject() || !element.getAsJsonObject().has("texture")) {
                    ContentLog.LOGGER.error("Dimension {} lists a sky body {} with no texture, ignoring it", key, element);
                    continue;
                }
                JsonObject body = element.getAsJsonObject();
                ResourceLocation texture = texture(key, GsonHelper.getAsString(body, "texture"));
                if (texture == null) { continue; }
                found.add(new DimensionTraitsDef.Body(texture, Math.max(0.0F, GsonHelper.getAsFloat(body, "size", 20.0F)),
                        GsonHelper.getAsFloat(body, "angle", 180.0F), GsonHelper.getAsFloat(body, "tilt", 0.0F), GsonHelper.getAsBoolean(body, "followsTime", true)));
            }
            bodies = List.copyOf(found);
        }
        ResourceLocation sunTexture = texture(key, GsonHelper.getAsString(sun, "texture", SUN));
        return new DimensionTraitsDef.Sky(sunTexture == null ? ResourceLocation.parse(SUN) : sunTexture, Math.max(0.0F, GsonHelper.getAsFloat(sun, "size", DimensionTraitsDef.VANILLA_SUN)), bodies,
                Math.max(0, GsonHelper.getAsInt(stars, "count", STARS)), Math.max(0.0F, GsonHelper.getAsFloat(stars, "size", STAR_SIZE)));
    }

    @Nullable private static ResourceLocation texture(ResourceLocation key, String named) {
        ResourceLocation texture = ResourceLocation.tryParse(Settings.lower(named));
        if (texture == null) { ContentLog.LOGGER.error("Dimension {} names a sky texture '{}' that is no id, ignoring it", key, named); }
        return texture;
    }

    private static double factor(ResourceLocation key, JsonObject physics, String member) {
        if (!physics.has(member)) { return -1.0D; }
        double value = GsonHelper.getAsFloat(physics, member);
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
        float strength = GsonHelper.getAsFloat(json, member, fallback);
        if (strength > 0.0F && strength <= 1.0F) { return strength; }
        ContentLog.LOGGER.error("Dimension {} gives a weather cycle {} of {}, which is not above 0 and at most 1, using {}", key, member, strength, fallback);
        return fallback;
    }

    private static RainDef rain(ResourceLocation key, JsonObject json, @Nullable WindDef wind) {
        ResourceLocation particle = ResourceLocation.tryParse(Settings.lower(GsonHelper.getAsString(json, "particle", RAIN_PARTICLE)));
        if (particle == null) {
            ContentLog.LOGGER.error("Dimension {} names a rain particle that is no particle id, using {}", key, RAIN_PARTICLE);
            particle = ResourceLocation.parse(RAIN_PARTICLE);
        }
        ResourceLocation sound = ResourceLocation.tryParse(Settings.lower(GsonHelper.getAsString(json, "sound", RAIN_SOUND)));
        if (sound == null) {
            ContentLog.LOGGER.error("Dimension {} names a rain sound that is no sound id, using {}", key, RAIN_SOUND);
            sound = ResourceLocation.parse(RAIN_SOUND);
        }
        float angle = GsonHelper.getAsFloat(json, "angle", 0.0F);
        if (angle < 0.0F || angle > MAX_ANGLE) {
            ContentLog.LOGGER.error("Dimension {} gives a rain angle of {}, which is not from 0 to 180, using 0", key, angle);
            angle = 0.0F;
        }
        return new RainDef(particle, sound, Math.max(0.0F, GsonHelper.getAsFloat(json, "volume", 0.2F)), Math.max(0, GsonHelper.getAsInt(json, "interval", 3)),
                ContentParser.color(GsonHelper.getAsString(json, "color", WHITE), key + " rain color"),
                ContentParser.color(GsonHelper.getAsString(json, "snowColor", WHITE), key + " rain snowColor"), angle, GsonHelper.getAsFloat(json, "heading", 0.0F), GsonHelper.getAsBoolean(json, "splashUpward", false), wind);
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
            min = value.getAsJsonArray().get(0).getAsInt();
            max = value.getAsJsonArray().get(1).getAsInt();
        }
        else if (value.isJsonPrimitive()) {
            min = value.getAsInt();
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
        JsonObject entry = GsonHelper.getAsJsonObject(json, "portal");
        List<String> frames = Json.strings(entry, "frames");
        if (frames.isEmpty()) {
            ContentLog.LOGGER.error("Dimension {} has a portal section naming no frames, so nothing could ever open it", key);
            return null;
        }
        int color = ContentParser.color(GsonHelper.getAsString(entry, "color", WHITE), key + " portal color");
        String back = Settings.lower(GsonHelper.getAsString(entry, "return", DimensionPortalDef.BUILT));
        if (!DimensionPortalDef.BUILT.equals(back) && !DimensionPortalDef.PLAYER.equals(back) && !DimensionPortalDef.NONE.equals(back)) {
            ContentLog.LOGGER.error("Dimension {} asks for a return of '{}', which is none of {}, {} or {}, building one instead", key, back, DimensionPortalDef.BUILT, DimensionPortalDef.PLAYER, DimensionPortalDef.NONE);
            back = DimensionPortalDef.BUILT;
        }
        PortalDef travel = ContentParser.portal(key, json, key, false, true);
        return travel == null ? null : new DimensionPortalDef(frames, GsonHelper.getAsString(entry, "ignitedBy", "minecraft:flint_and_steel").trim(), color, back, travel);
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
        rules(key, GsonHelper.getAsJsonObject(json, "gameRules"), found);
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
