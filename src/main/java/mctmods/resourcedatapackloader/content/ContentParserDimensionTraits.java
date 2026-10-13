package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.AmbienceDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.content.def.WindDef;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentParserDimensionTraits {
    private static final String SUN = "minecraft:textures/environment/sun.png";
    private static final String MOON = "minecraft:textures/environment/moon_phases.png";
    private static final int STARS = 1500;
    private static final float STAR_SIZE = 0.15F;
    private static final int CYCLE_LOW = 1000;
    private static final int THUNDER_LOW = 3600;
    private static final int THUNDER_HIGH = 15600;
    private static final int CALM_LOW = 12000;
    private static final int CALM_HIGH = 180000;
    private static final String RAIN_PARTICLE = "droplet";
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
    private static final int RAIN_HIGH = 4600;
    private static final int CLEAR_HIGH = 3000;
    static final float SOUND_CHANCE = 0.0111F;
    static final float PARTICLE_CHANCE = 0.00625F;

    private ContentParserDimensionTraits() {}

    public static DimensionTraitsDef traits(ResourceLocation key, JsonObject json, boolean belt) {
        JsonObject physics = Json.getJsonObject(json, "physics", new JsonObject());
        JsonObject time = Json.getJsonObject(json, "time", new JsonObject());
        JsonObject weather = Json.getJsonObject(json, "weather", new JsonObject());
        JsonObject sky = Json.getJsonObject(json, "sky", new JsonObject());
        boolean still = belt && !time.has("dayLength") && !sky.has("fixedTime");
        long day = still ? DimensionTraitsDef.NO_DAY : Json.getInt(time, "dayLength", (int) DimensionTraitsDef.VANILLA_DAY);
        if (!still && day <= 0L) {
            ContentLog.LOGGER.error("Dimension {} gives a dayLength of {}, which is not above zero, using {}", key, day, DimensionTraitsDef.VANILLA_DAY);
            day = DimensionTraitsDef.VANILLA_DAY;
        }
        boolean precipitation = Json.getBoolean(weather, "precipitation", true);
        boolean alwaysRain = Json.getBoolean(weather, "alwaysRain", false);
        if (alwaysRain && !precipitation) {
            ContentLog.LOGGER.error("Dimension {} asks for alwaysRain with precipitation off, so it stays dry", key);
            alwaysRain = false;
        }
        return new DimensionTraitsDef(
                factor(key, physics, "gravity"),
                factor(key, physics, "fallDamage"),
                factor(key, physics, "arrowGravity"),
                day,
                precipitation,
                Json.getBoolean(weather, "lightning", true),
                Json.getBoolean(weather, "snow", true),
                Json.getBoolean(weather, "freeze", true),
                alwaysRain,
                weather.has("cycle") ? cycle(key, Json.getJsonObject(weather, "cycle")) : null,
                weather.has("rain") || weather.has("wind") ? rain(key, Json.getJsonObject(weather, "rain", new JsonObject()), weather.has("wind") ? wind(key, Json.getJsonObject(weather, "wind")) : null) : null,
                sky(key, sky),
                sky.has("moon") ? moon(Json.getJsonObject(sky, "moon")) : null,
                json.has("ambience") ? ambience(key, Json.getJsonObject(json, "ambience")) : null);
    }

    private static DimensionTraitsDef.Moon moon(JsonObject moon) {
        return new DimensionTraitsDef.Moon(new ResourceLocation(Json.getString(moon, "texture", MOON).trim()), Math.max(0.0F, Json.getFloat(moon, "size", DimensionTraitsDef.VANILLA_MOON)));
    }

    private static AmbienceDef ambience(ResourceLocation key, JsonObject json) {
        int[] delay = span(key, json, AMBIENCE, "musicDelay", MUSIC_LOW, MUSIC_HIGH);
        String particle = Json.getString(json, "particle", "").trim();
        if (!particle.isEmpty() && EnumParticleTypes.getByName(particle) == null) {
            ContentLog.LOGGER.error("Dimension {} names the ambience particle '{}', which is no particle Minecraft knows, so it shows none", key, particle);
            particle = "";
        }
        return new AmbienceDef(sound(json, "music"), delay[0], delay[1], sound(json, "loopSound"),
                Json.getString(json, "ambientSound", "").trim(),
                chance(key, json, "soundChance", SOUND_CHANCE),
                particle,
                chance(key, json, "particleChance", PARTICLE_CHANCE),
                json.has("particleColor") ? ContentTypes.color(Json.getString(json, "particleColor").trim(), key + " ambience particleColor") & 0xFFFFFF : AmbienceDef.NO_COLOR);
    }

    @Nullable private static ResourceLocation sound(JsonObject json, String member) {
        String name = Json.getString(json, member, "").trim();
        return name.isEmpty() ? null : new ResourceLocation(name);
    }

    private static float chance(ResourceLocation key, JsonObject json, String member, float fallback) {
        return Json.ranged(json, member, fallback, 0.0F, 1.0F, "Dimension " + key + " ambience");
    }

    private static RainDef rain(ResourceLocation key, JsonObject json, @Nullable WindDef wind) {
        String particle = Settings.lower(Json.getString(json, "particle", RAIN_PARTICLE));
        if (EnumParticleTypes.getByName(particle) == null) {
            ContentLog.LOGGER.error("Dimension {} names rain particle '{}', which is no particle Minecraft knows, using {}", key, particle, RAIN_PARTICLE);
            particle = RAIN_PARTICLE;
        }
        float angle = Json.ranged(json, "angle", 0.0F, 0.0F, MAX_ANGLE, "Dimension " + key + " rain");
        return new RainDef(particle,
                new ResourceLocation(Json.getString(json, "sound", RAIN_SOUND).trim()),
                Math.max(0.0F, Json.getFloat(json, "volume", 0.2F)),
                Math.max(0, Json.getInt(json, "interval", 3)),
                ContentTypes.color(Json.getString(json, "color", WHITE).trim(), key + " rain color") & 0xFFFFFF,
                ContentTypes.color(Json.getString(json, "snowColor", WHITE).trim(), key + " rain snowColor") & 0xFFFFFF,
                angle,
                Json.getFloat(json, "heading", 0.0F),
                Json.getBoolean(json, "splashUpward", false),
                wind);
    }

    private static WindDef wind(ResourceLocation key, JsonObject json) {
        return new WindDef(degrees(key, json, "gust", GUST, MAX_GUST),
                span(key, json, WIND, "every", EVERY_LOW, EVERY_HIGH),
                degrees(key, json, "swing", SWING, MAX_ANGLE));
    }

    private static float degrees(ResourceLocation key, JsonObject json, String member, float fallback, float most) {
        return Json.ranged(json, member, fallback, 0.0F, most, "Dimension " + key + " wind");
    }

    private static double factor(ResourceLocation key, JsonObject physics, String member) {
        if (!physics.has(member)) { return -1.0D; }
        double value = Json.getFloat(physics, member);
        if (value > 0.0D) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives physics {} of {}, which is not above zero, ignoring it", key, member, value);
        return -1.0D;
    }

    private static DimensionTraitsDef.Cycle cycle(ResourceLocation key, JsonObject json) {
        int[] rain = span(key, json, CYCLE, "rainTicks", CYCLE_LOW, RAIN_HIGH);
        int[] clear = span(key, json, CYCLE, "clearTicks", CYCLE_LOW, CLEAR_HIGH);
        int[] thunder = json.has("thunderTicks") ? span(key, json, CYCLE, "thunderTicks", THUNDER_LOW, THUNDER_HIGH) : new int[] {0, 0};
        int[] calm = span(key, json, CYCLE, "calmTicks", CALM_LOW, CALM_HIGH);
        return new DimensionTraitsDef.Cycle(rain, clear, strength(key, json, "maxStrength", 0.6F), thunder, calm, strength(key, json, "thunderStrength", 1.0F));
    }

    private static float strength(ResourceLocation key, JsonObject json, String member, float fallback) {
        float strength = Json.getFloat(json, member, fallback);
        if (strength > 0.0F && strength <= 1.0F) { return strength; }
        ContentLog.LOGGER.error("Dimension {} gives a weather cycle {} of {}, which is not above 0 and at most 1, using {}", key, member, strength, fallback);
        return fallback;
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

    @Nullable private static DimensionTraitsDef.Sky sky(ResourceLocation key, JsonObject sky) {
        if (!sky.has("sun") && !sky.has("moon") && !sky.has("bodies") && !sky.has("stars")) { return null; }
        JsonObject sun = Json.getJsonObject(sky, "sun", new JsonObject());
        JsonObject stars = Json.getJsonObject(sky, "stars", new JsonObject());
        List<DimensionTraitsDef.Body> bodies = null;
        if (sky.has("bodies")) {
            List<DimensionTraitsDef.Body> found = new ArrayList<>();
            JsonArray list = Json.getJsonArray(sky, "bodies");
            for (JsonElement element : list) {
                if (!element.isJsonObject() || !element.getAsJsonObject().has("texture")) {
                    ContentLog.LOGGER.error("Dimension {} lists a sky body {} with no texture, ignoring it", key, element);
                    continue;
                }
                JsonObject body = element.getAsJsonObject();
                String ringBody = Json.getString(body, "body", "").trim();
                found.add(new DimensionTraitsDef.Body(new ResourceLocation(Json.getString(body, "texture").trim()),
                        Math.max(0.0F, Json.getFloat(body, "size", 20.0F)),
                        Json.getFloat(body, "angle", 180.0F),
                        Json.getFloat(body, "tilt", 0.0F),
                        Json.getBoolean(body, "followsTime", true),
                        ringBody.isEmpty() ? null : ringBody));
            }
            bodies = Collections.unmodifiableList(found);
        }
        return new DimensionTraitsDef.Sky(new ResourceLocation(Json.getString(sun, "texture", SUN).trim()),
                Math.max(0.0F, Json.getFloat(sun, "size", DimensionTraitsDef.VANILLA_SUN)),
                bodies,
                Math.max(0, Json.getInt(stars, "count", STARS)),
                Math.max(0.0F, Json.getFloat(stars, "size", STAR_SIZE)));
    }
}
