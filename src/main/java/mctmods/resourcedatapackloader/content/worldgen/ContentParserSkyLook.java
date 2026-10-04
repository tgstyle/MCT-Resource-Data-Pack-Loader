package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public final class ContentParserSkyLook {
    public static final String SAMPLE = "sample";
    private static final List<String> KEYS = List.of("fogDensity", "fogGroundWeight", "lightSkyColor", "lightBlockColor", "skyFactor", "cloudSpeed", "cloudLayers", "sunBrightness", "moonBrightness", "heat", "snowColor", "waterFogColor", "lavaFogColor", "starColor", "starTwinkle", "lightningColor");
    private static final float GROUND_WEIGHT = 0.5F;
    private static final float HOT = 1.5F;
    private static final float HEAT_STRENGTH = 0.1F;
    private static final float HEAT_START = 32.0F;
    private static final String SCREEN = "screen";
    private static final String WORLD = "world";

    private ContentParserSkyLook() {}

    public static boolean samples(JsonObject sky) { return SAMPLE.equals(GsonHelper.getAsString(sky, "fogColor", "").trim().toLowerCase(Locale.ROOT)); }

    @Nullable public static SkyLookDef look(Identifier key, JsonObject sky, int cloudHeight, int cloudColor) {
        boolean sample = samples(sky);
        if (!sample && KEYS.stream().noneMatch(sky::has)) { return null; }
        float cloudSpeed = GsonHelper.getAsFloat(sky, "cloudSpeed", 1.0F);
        return new SkyLookDef(sample, unit(key, sky, "fogDensity", 0.0F), unit(key, sky, "fogGroundWeight", GROUND_WEIGHT), color(key, sky, "lightSkyColor"), color(key, sky, "lightBlockColor"),
                unit(key, sky, "skyFactor", 1.0F), cloudSpeed, layers(key, sky, cloudHeight, cloudSpeed, cloudColor), unit(key, sky, "sunBrightness", 1.0F), unit(key, sky, "moonBrightness", 1.0F),
                sky.has("heat") ? heat(key, GsonHelper.getAsJsonObject(sky, "heat")) : null, color(key, sky, "snowColor"), color(key, sky, "waterFogColor"), color(key, sky, "lavaFogColor"),
                color(key, sky, "starColor"), unit(key, sky, "starTwinkle", 0.0F), color(key, sky, "lightningColor"));
    }

    private static float unit(Identifier key, JsonObject json, String member, float fallback) {
        float value = GsonHelper.getAsFloat(json, member, fallback);
        if (value >= 0.0F && value <= 1.0F) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives sky.{} {}, which is not from 0 to 1, using {}", key, member, value, fallback);
        return fallback;
    }

    private static int color(Identifier key, JsonObject json, String member) {
        String value = GsonHelper.getAsString(json, member, "").trim();
        return value.isEmpty() ? SkyLookDef.UNSET : ContentParser.color(value, key + " sky." + member) & 0xFFFFFF;
    }

    private static List<SkyLookDef.CloudLayer> layers(Identifier key, JsonObject sky, int cloudHeight, float cloudSpeed, int cloudColor) {
        if (!sky.has("cloudLayers")) { return List.of(); }
        List<SkyLookDef.CloudLayer> layers = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(sky, "cloudLayers")) {
            JsonObject layer = GsonHelper.convertToJsonObject(element, "cloud layer");
            String tint = GsonHelper.getAsString(layer, "color", "").trim();
            layers.add(new SkyLookDef.CloudLayer(GsonHelper.getAsFloat(layer, "height", cloudHeight), GsonHelper.getAsFloat(layer, "speed", cloudSpeed), tint.isEmpty() ? cloudColor : ContentParser.color(tint, key + " sky.cloudLayers") & 0xFFFFFF));
        }
        return List.copyOf(layers);
    }

    private static SkyLookDef.Heat heat(Identifier key, JsonObject heat) {
        return new SkyLookDef.Heat(unit(key, heat, "strength", HEAT_STRENGTH), GsonHelper.getAsFloat(heat, "minTemperature", HOT), GsonHelper.getAsBoolean(heat, "dayOnly", true), followsWorld(key, heat), startDistance(key, heat));
    }

    private static boolean followsWorld(Identifier key, JsonObject heat) {
        String mode = GsonHelper.getAsString(heat, "mode", SCREEN).trim().toLowerCase(Locale.ROOT);
        if (WORLD.equals(mode)) { return true; }
        if (!SCREEN.equals(mode)) { ContentLog.LOGGER.error("Dimension {} gives sky.heat.mode {}, which is neither {} nor {}, using {}", key, mode, SCREEN, WORLD, SCREEN); }
        return false;
    }

    private static float startDistance(Identifier key, JsonObject heat) {
        float value = GsonHelper.getAsFloat(heat, "startDistance", HEAT_START);
        if (value >= 0.0F) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives sky.heat.startDistance {}, which is below zero, using {}", key, value, HEAT_START);
        return HEAT_START;
    }
}
