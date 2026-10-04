package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.AmbienceDef;
import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.RainDef;
import mctmods.resourcedatapackloader.content.def.WindDef;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public final class ContentParserDimensionTraits {
    private static final String SUN = "minecraft:textures/environment/sun.png";
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
    private static final float SOUND_CHANCE = 0.0111F;
    private static final float PARTICLE_CHANCE = 0.00625F;

    private ContentParserDimensionTraits() {}

    public static DimensionTraitsDef traits(ResourceLocation key, JsonObject json, boolean belt) {
        JsonObject physics = JsonUtils.getJsonObject(json, "physics", new JsonObject());
        JsonObject time = JsonUtils.getJsonObject(json, "time", new JsonObject());
        JsonObject weather = JsonUtils.getJsonObject(json, "weather", new JsonObject());
        JsonObject sky = JsonUtils.getJsonObject(json, "sky", new JsonObject());
        boolean still = belt && !time.has("dayLength") && !sky.has("fixedTime");
        long day = still ? DimensionTraitsDef.NO_DAY : JsonUtils.getInt(time, "dayLength", (int) DimensionTraitsDef.VANILLA_DAY);
        if (!still && day <= 0L) {
            ContentLog.LOGGER.error("Dimension {} gives a dayLength of {}, which is not above zero, using {}", key, day, DimensionTraitsDef.VANILLA_DAY);
            day = DimensionTraitsDef.VANILLA_DAY;
        }
        boolean precipitation = JsonUtils.getBoolean(weather, "precipitation", true);
        return new DimensionTraitsDef(
                factor(key, physics, "gravity"),
                factor(key, physics, "fallDamage"),
                factor(key, physics, "arrowGravity"),
                day,
                precipitation,
                JsonUtils.getBoolean(weather, "lightning", true),
                JsonUtils.getBoolean(weather, "snow", true),
                JsonUtils.getBoolean(weather, "freeze", true),
                weather.has("cycle") ? cycle(key, JsonUtils.getJsonObject(weather, "cycle")) : null,
                weather.has("rain") || weather.has("wind") ? rain(key, JsonUtils.getJsonObject(weather, "rain", new JsonObject()), weather.has("wind") ? wind(key, JsonUtils.getJsonObject(weather, "wind")) : null) : null,
                sky(key, sky),
                json.has("ambience") ? ambience(key, JsonUtils.getJsonObject(json, "ambience")) : null);
    }

    private static AmbienceDef ambience(ResourceLocation key, JsonObject json) {
        int[] delay = span(key, json, AMBIENCE, "musicDelay", MUSIC_LOW, MUSIC_HIGH);
        String particle = JsonUtils.getString(json, "particle", "").trim();
        if (!particle.isEmpty() && EnumParticleTypes.getByName(particle) == null) {
            ContentLog.LOGGER.error("Dimension {} names the ambience particle '{}', which is no particle Minecraft knows, so it shows none", key, particle);
            particle = "";
        }
        return new AmbienceDef(sound(json, "music"), delay[0], delay[1], sound(json, "loopSound"),
                JsonUtils.getString(json, "ambientSound", "").trim(),
                chance(key, json, "soundChance", SOUND_CHANCE),
                particle,
                chance(key, json, "particleChance", PARTICLE_CHANCE),
                json.has("particleColor") ? ContentTypes.color(JsonUtils.getString(json, "particleColor").trim(), key + " ambience particleColor") & 0xFFFFFF : AmbienceDef.NO_COLOR);
    }

    @Nullable private static ResourceLocation sound(JsonObject json, String member) {
        String name = JsonUtils.getString(json, member, "").trim();
        return name.isEmpty() ? null : new ResourceLocation(name);
    }

    private static float chance(ResourceLocation key, JsonObject json, String member, float fallback) {
        float chance = JsonUtils.getFloat(json, member, fallback);
        if (chance >= 0.0F && chance <= 1.0F) { return chance; }
        ContentLog.LOGGER.error("Dimension {} gives an ambience {} of {}, which is not from 0 to 1, using {}", key, member, chance, fallback);
        return fallback;
    }

    private static RainDef rain(ResourceLocation key, JsonObject json, @Nullable WindDef wind) {
        String particle = JsonUtils.getString(json, "particle", RAIN_PARTICLE).trim().toLowerCase(Locale.ROOT);
        if (EnumParticleTypes.getByName(particle) == null) {
            ContentLog.LOGGER.error("Dimension {} names rain particle '{}', which is no particle Minecraft knows, using {}", key, particle, RAIN_PARTICLE);
            particle = RAIN_PARTICLE;
        }
        float angle = JsonUtils.getFloat(json, "angle", 0.0F);
        if (angle < 0.0F || angle > MAX_ANGLE) {
            ContentLog.LOGGER.error("Dimension {} gives a rain angle of {}, which is not from 0 to 180, using 0", key, angle);
            angle = 0.0F;
        }
        return new RainDef(particle,
                new ResourceLocation(JsonUtils.getString(json, "sound", RAIN_SOUND).trim()),
                Math.max(0.0F, JsonUtils.getFloat(json, "volume", 0.2F)),
                Math.max(0, JsonUtils.getInt(json, "interval", 3)),
                ContentTypes.color(JsonUtils.getString(json, "color", WHITE).trim(), key + " rain color") & 0xFFFFFF,
                ContentTypes.color(JsonUtils.getString(json, "snowColor", WHITE).trim(), key + " rain snowColor") & 0xFFFFFF,
                angle,
                JsonUtils.getFloat(json, "heading", 0.0F),
                wind);
    }

    private static WindDef wind(ResourceLocation key, JsonObject json) {
        return new WindDef(degrees(key, json, "gust", GUST, MAX_GUST),
                span(key, json, WIND, "every", EVERY_LOW, EVERY_HIGH),
                degrees(key, json, "swing", SWING, MAX_ANGLE));
    }

    private static float degrees(ResourceLocation key, JsonObject json, String member, float fallback, float most) {
        float value = JsonUtils.getFloat(json, member, fallback);
        if (value >= 0.0F && value <= most) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives a wind {} of {}, which is not from 0 to {}, using {}", key, member, value, most, fallback);
        return fallback;
    }

    private static double factor(ResourceLocation key, JsonObject physics, String member) {
        if (!physics.has(member)) { return -1.0D; }
        double value = JsonUtils.getFloat(physics, member);
        if (value > 0.0D) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives physics {} of {}, which is not above zero, ignoring it", key, member, value);
        return -1.0D;
    }

    private static DimensionTraitsDef.Cycle cycle(ResourceLocation key, JsonObject json) {
        int[] rain = span(key, json, CYCLE, "rainTicks", CYCLE_LOW, 4600);
        int[] clear = span(key, json, CYCLE, "clearTicks", CYCLE_LOW, 3000);
        int[] thunder = json.has("thunderTicks") ? span(key, json, CYCLE, "thunderTicks", THUNDER_LOW, THUNDER_HIGH) : new int[] {0, 0};
        int[] calm = span(key, json, CYCLE, "calmTicks", CALM_LOW, CALM_HIGH);
        return new DimensionTraitsDef.Cycle(rain, clear, strength(key, json, "maxStrength", 0.6F), thunder, calm, strength(key, json, "thunderStrength", 1.0F));
    }

    private static float strength(ResourceLocation key, JsonObject json, String member, float fallback) {
        float strength = JsonUtils.getFloat(json, member, fallback);
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

    @Nullable private static DimensionTraitsDef.Sky sky(ResourceLocation key, JsonObject sky) {
        if (!sky.has("sun") && !sky.has("bodies") && !sky.has("stars")) { return null; }
        JsonObject sun = JsonUtils.getJsonObject(sky, "sun", new JsonObject());
        JsonObject stars = JsonUtils.getJsonObject(sky, "stars", new JsonObject());
        List<DimensionTraitsDef.Body> bodies = null;
        if (sky.has("bodies")) {
            List<DimensionTraitsDef.Body> found = new ArrayList<>();
            JsonArray list = JsonUtils.getJsonArray(sky, "bodies");
            for (JsonElement element : list) {
                if (!element.isJsonObject() || !element.getAsJsonObject().has("texture")) {
                    ContentLog.LOGGER.error("Dimension {} lists a sky body {} with no texture, ignoring it", key, element);
                    continue;
                }
                JsonObject body = element.getAsJsonObject();
                found.add(new DimensionTraitsDef.Body(new ResourceLocation(JsonUtils.getString(body, "texture").trim()),
                        Math.max(0.0F, JsonUtils.getFloat(body, "size", 20.0F)),
                        JsonUtils.getFloat(body, "angle", 180.0F),
                        JsonUtils.getFloat(body, "tilt", 0.0F),
                        JsonUtils.getBoolean(body, "followsTime", true)));
            }
            bodies = Collections.unmodifiableList(found);
        }
        return new DimensionTraitsDef.Sky(new ResourceLocation(JsonUtils.getString(sun, "texture", SUN).trim()),
                Math.max(0.0F, JsonUtils.getFloat(sun, "size", DimensionTraitsDef.VANILLA_SUN)),
                bodies,
                Math.max(0, JsonUtils.getInt(stars, "count", STARS)),
                Math.max(0.0F, JsonUtils.getFloat(stars, "size", STAR_SIZE)));
    }
}
