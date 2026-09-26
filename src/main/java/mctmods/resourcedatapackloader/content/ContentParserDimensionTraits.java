package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentParserDimensionTraits {
    private static final String SUN = "minecraft:textures/environment/sun.png";
    private static final int STARS = 1500;
    private static final float STAR_SIZE = 0.15F;
    private static final int CYCLE_LOW = 1000;

    private ContentParserDimensionTraits() {}

    public static DimensionTraitsDef traits(ResourceLocation key, JsonObject json) {
        JsonObject physics = JsonUtils.getJsonObject(json, "physics", new JsonObject());
        JsonObject time = JsonUtils.getJsonObject(json, "time", new JsonObject());
        JsonObject weather = JsonUtils.getJsonObject(json, "weather", new JsonObject());
        long day = JsonUtils.getInt(time, "dayLength", (int) DimensionTraitsDef.VANILLA_DAY);
        if (day <= 0L) {
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
                sky(key, JsonUtils.getJsonObject(json, "sky", new JsonObject())));
    }

    private static double factor(ResourceLocation key, JsonObject physics, String member) {
        if (!physics.has(member)) { return -1.0D; }
        double value = JsonUtils.getFloat(physics, member);
        if (value > 0.0D) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives physics {} of {}, which is not above zero, ignoring it", key, member, value);
        return -1.0D;
    }

    private static DimensionTraitsDef.Cycle cycle(ResourceLocation key, JsonObject json) {
        int[] rain = span(key, json, "rainTicks", 4600);
        int[] clear = span(key, json, "clearTicks", 3000);
        float strength = JsonUtils.getFloat(json, "maxStrength", 0.6F);
        if (strength <= 0.0F || strength > 1.0F) {
            ContentLog.LOGGER.error("Dimension {} gives a weather cycle maxStrength of {}, which is not above 0 and at most 1, using 0.6", key, strength);
            strength = 0.6F;
        }
        return new DimensionTraitsDef.Cycle(rain[0], rain[1], clear[0], clear[1], strength);
    }

    private static int[] span(ResourceLocation key, JsonObject json, String member, int high) {
        int low = CYCLE_LOW;
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
            ContentLog.LOGGER.error("Dimension {} gives a weather cycle {} of {}, which is neither a number nor [min, max], using [{}, {}]", key, member, value, low, high);
            return new int[] {low, high};
        }
        if (min <= 0 || max < min) {
            ContentLog.LOGGER.error("Dimension {} gives a weather cycle {} of {}, which needs 0 < min <= max, using [{}, {}]", key, member, value, low, high);
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
