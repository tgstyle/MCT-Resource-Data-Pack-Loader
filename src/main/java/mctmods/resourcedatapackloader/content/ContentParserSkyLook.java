package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public final class ContentParserSkyLook {
    public static final String SAMPLE = "sample";
    private static final List<String> KEYS = Arrays.asList("fogDensity", "fogGroundWeight", "lightSkyColor", "lightBlockColor", "skyFactor", "cloudSpeed", "cloudLayers", "sunBrightness", "moonBrightness", "heat");
    private static final float GROUND_WEIGHT = 0.5F;
    private static final float HOT = 1.5F;
    private static final float HEAT_STRENGTH = 0.1F;
    private static final float HEAT_START = 32.0F;
    private static final String SCREEN = "screen";
    private static final String WORLD = "world";

    private ContentParserSkyLook() {}

    public static boolean samples(JsonObject sky) { return SAMPLE.equals(JsonUtils.getString(sky, "fogColor", "").trim().toLowerCase(Locale.ROOT)); }

    @Nullable public static SkyLookDef look(ResourceLocation key, JsonObject sky, int cloudHeight, int cloudColor) {
        boolean sample = samples(sky);
        if (!sample && KEYS.stream().noneMatch(sky::has)) { return null; }
        float cloudSpeed = JsonUtils.getFloat(sky, "cloudSpeed", 1.0F);
        return new SkyLookDef(sample,
                unit(key, sky, "fogDensity", 0.0F),
                unit(key, sky, "fogGroundWeight", GROUND_WEIGHT),
                color(key, sky, "lightSkyColor"),
                color(key, sky, "lightBlockColor"),
                unit(key, sky, "skyFactor", 1.0F),
                cloudSpeed,
                layers(key, sky, cloudHeight, cloudSpeed, cloudColor),
                unit(key, sky, "sunBrightness", 1.0F),
                unit(key, sky, "moonBrightness", 1.0F),
                sky.has("heat") ? heat(key, JsonUtils.getJsonObject(sky, "heat")) : null);
    }

    private static float unit(ResourceLocation key, JsonObject json, String member, float fallback) {
        float value = JsonUtils.getFloat(json, member, fallback);
        if (value >= 0.0F && value <= 1.0F) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives sky.{} {}, which is not from 0 to 1, using {}", key, member, value, fallback);
        return fallback;
    }

    private static int color(ResourceLocation key, JsonObject json, String member) {
        String value = JsonUtils.getString(json, member, "").trim();
        return value.isEmpty() ? SkyLookDef.UNSET : ContentTypes.color(value, key + " sky." + member) & 0xFFFFFF;
    }

    private static List<SkyLookDef.CloudLayer> layers(ResourceLocation key, JsonObject sky, int cloudHeight, float cloudSpeed, int cloudColor) {
        if (!sky.has("cloudLayers")) { return Collections.emptyList(); }
        List<SkyLookDef.CloudLayer> layers = new ArrayList<>();
        for (JsonElement element : JsonUtils.getJsonArray(sky, "cloudLayers")) {
            JsonObject layer = JsonUtils.getJsonObject(element, "cloud layer");
            String tint = JsonUtils.getString(layer, "color", "").trim();
            layers.add(new SkyLookDef.CloudLayer(JsonUtils.getFloat(layer, "height", cloudHeight), JsonUtils.getFloat(layer, "speed", cloudSpeed), tint.isEmpty() ? cloudColor : ContentTypes.color(tint, key + " sky.cloudLayers") & 0xFFFFFF));
        }
        return Collections.unmodifiableList(layers);
    }

    private static SkyLookDef.Heat heat(ResourceLocation key, JsonObject heat) {
        return new SkyLookDef.Heat(unit(key, heat, "strength", HEAT_STRENGTH), JsonUtils.getFloat(heat, "minTemperature", HOT), JsonUtils.getBoolean(heat, "dayOnly", true), followsWorld(key, heat), startDistance(key, heat));
    }

    private static boolean followsWorld(ResourceLocation key, JsonObject heat) {
        String mode = JsonUtils.getString(heat, "mode", SCREEN).trim().toLowerCase(Locale.ROOT);
        if (WORLD.equals(mode)) { return true; }
        if (!SCREEN.equals(mode)) { ContentLog.LOGGER.error("Dimension {} gives sky.heat.mode {}, which is neither {} nor {}, using {}", key, mode, SCREEN, WORLD, SCREEN); }
        return false;
    }

    private static float startDistance(ResourceLocation key, JsonObject heat) {
        float value = JsonUtils.getFloat(heat, "startDistance", HEAT_START);
        if (value >= 0.0F) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives sky.heat.startDistance {}, which is below zero, using {}", key, value, HEAT_START);
        return HEAT_START;
    }
}
