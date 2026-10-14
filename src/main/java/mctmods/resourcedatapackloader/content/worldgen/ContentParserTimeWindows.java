package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.def.TimeWindowDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class ContentParserTimeWindows {
    private static final int DAY = 24000;
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String SKY = "sky";
    private static final String SUN = "sun";
    private static final String WEATHER = "weather";
    private static final String FOG_DENSITY = "fogDensity";
    private static final String FOG_COLOR = "fogColor";
    private static final String COLOR = "color";
    private static final String AURA = "auraColor";
    private static final String PRECIPITATION = "precipitation";
    private static final String ALWAYS_RAIN = "alwaysRain";
    private static final List<String> WINDOW_KEYS = List.of(FROM, TO, SKY, WEATHER);
    private static final List<String> SKY_KEYS = List.of(FOG_DENSITY, FOG_COLOR, SUN);
    private static final List<String> SUN_KEYS = List.of(COLOR, AURA);
    private static final List<String> WEATHER_KEYS = List.of(PRECIPITATION, ALWAYS_RAIN);

    private ContentParserTimeWindows() {}

    static List<TimeWindowDef> windows(ResourceLocation key, JsonObject json, boolean precipitation) {
        JsonObject time = Json.getJsonObject(json, "time", new JsonObject());
        if (!time.has("windows")) { return List.of(); }
        List<TimeWindowDef> windows = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(time, "windows")) {
            TimeWindowDef window = window(key, Json.getJsonObject(element, "time window"), precipitation);
            if (window != null) { windows.add(window); }
        }
        return List.copyOf(windows);
    }

    @Nullable private static TimeWindowDef window(ResourceLocation key, JsonObject window, boolean precipitation) {
        String owner = "Dimension " + key + " time window";
        if (!window.has(FROM) || !window.has(TO)) {
            ContentLog.LOGGER.error("{} needs both from and to, ignoring it", owner);
            return null;
        }
        int from = Json.getInt(window, FROM);
        int to = Json.getInt(window, TO);
        if (from < 0 || from > DAY || to < 0 || to > DAY) {
            ContentLog.LOGGER.error("{} gives from {} and to {}, which must each be 0 to {}, ignoring it", owner, from, to, DAY);
            return null;
        }
        JsonObject sky = Json.getJsonObject(window, SKY, new JsonObject());
        JsonObject sun = Json.getJsonObject(sky, SUN, new JsonObject());
        JsonObject weather = Json.getJsonObject(window, WEATHER, new JsonObject());
        unknown(owner, window, WINDOW_KEYS);
        unknown(owner + " sky", sky, SKY_KEYS);
        unknown(owner + " sky.sun", sun, SUN_KEYS);
        unknown(owner + " weather", weather, WEATHER_KEYS);
        boolean sample = ContentParserSkyLook.samples(sky);
        int fog = sample ? SkyLookDef.UNSET : ContentParserSkyLook.color(key, sky, FOG_COLOR);
        int sunColor = ContentParserSkyLook.color(key, sun, COLOR);
        int aura = ContentParserSkyLook.color(key, sun, AURA);
        boolean rains = Json.getBoolean(weather, PRECIPITATION, true);
        boolean always = Json.getBoolean(weather, ALWAYS_RAIN, false);
        Set<String> given = new HashSet<>();
        if (sky.has(FOG_DENSITY)) { given.add(TimeWindowDef.FOG_DENSITY); }
        if (sample || fog != SkyLookDef.UNSET) { given.add(TimeWindowDef.FOG_COLOR); }
        if (sunColor != SkyLookDef.UNSET) { given.add(TimeWindowDef.SUN_COLOR); }
        if (aura != SkyLookDef.UNSET) { given.add(TimeWindowDef.SUN_AURA); }
        if (weather.has(PRECIPITATION) && rains && !precipitation) { ContentLog.LOGGER.error("{} turns precipitation on, but the dimension has it off, ignoring it", owner); }
        else if (weather.has(PRECIPITATION)) { given.add(TimeWindowDef.PRECIPITATION); }
        if (weather.has(ALWAYS_RAIN) && always && !precipitation) { ContentLog.LOGGER.error("{} asks for alwaysRain, but the dimension has precipitation off, ignoring it", owner); }
        else if (weather.has(ALWAYS_RAIN)) { given.add(TimeWindowDef.ALWAYS_RAIN); }
        return new TimeWindowDef(from, to, ContentParserSkyLook.unit(key, sky, FOG_DENSITY, 0.0F), fog, sample, sunColor, aura, rains, always, Set.copyOf(given));
    }

    private static void unknown(String owner, JsonObject json, List<String> known) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!known.contains(entry.getKey())) { ContentLog.LOGGER.error("{} gives '{}', which a time window cannot change, ignoring it", owner, entry.getKey()); }
        }
    }
}
