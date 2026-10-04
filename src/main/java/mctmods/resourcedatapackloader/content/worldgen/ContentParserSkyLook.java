package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.SkyLookDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public final class ContentParserSkyLook {
    public static final String SAMPLE = "sample";
    private static final List<String> KEYS = List.of("fogDensity", "fogGroundWeight", "lightSkyColor", "lightBlockColor", "skyFactor", "cloudSpeed", "cloudLayers", "sunBrightness", "moonBrightness", "heat", "snowColor", "waterFogColor", "lavaFogColor", "starColor", "starTwinkle", "lightningColor", "skybox", "aurora", "rainbow");
    private static final List<String> FACES = List.of("up", "down", "north", "east", "south", "west");
    private static final int AURORA = 0x40FF90;
    private static final int AURORA_TOP = 0x8040FF;
    private static final float GROUND_WEIGHT = 0.5F;
    private static final float HOT = 1.5F;
    private static final float HEAT_STRENGTH = 0.1F;
    private static final float HEAT_START = 32.0F;
    private static final String SCREEN = "screen";
    private static final String WORLD = "world";

    private ContentParserSkyLook() {}

    public static boolean samples(JsonObject sky) { return SAMPLE.equals(GsonHelper.getAsString(sky, "fogColor", "").trim().toLowerCase(Locale.ROOT)); }

    @Nullable public static SkyLookDef look(ResourceLocation key, JsonObject sky, int cloudHeight, int cloudColor) {
        boolean sample = samples(sky);
        if (!sample && KEYS.stream().noneMatch(sky::has)) { return null; }
        float cloudSpeed = GsonHelper.getAsFloat(sky, "cloudSpeed", 1.0F);
        return new SkyLookDef(sample, unit(key, sky, "fogDensity", 0.0F), unit(key, sky, "fogGroundWeight", GROUND_WEIGHT), color(key, sky, "lightSkyColor"), color(key, sky, "lightBlockColor"),
                unit(key, sky, "skyFactor", 1.0F), cloudSpeed, layers(key, sky, cloudHeight, cloudSpeed, cloudColor), unit(key, sky, "sunBrightness", 1.0F), unit(key, sky, "moonBrightness", 1.0F),
                sky.has("heat") ? heat(key, GsonHelper.getAsJsonObject(sky, "heat")) : null, color(key, sky, "snowColor"), color(key, sky, "waterFogColor"), color(key, sky, "lavaFogColor"),
                color(key, sky, "starColor"), unit(key, sky, "starTwinkle", 0.0F), color(key, sky, "lightningColor"), skybox(key, sky),
                sky.has("aurora") ? aurora(key, GsonHelper.getAsJsonObject(sky, "aurora")) : null, GsonHelper.getAsBoolean(sky, "rainbow", false));
    }

    @Nullable private static SkyLookDef.Skybox skybox(ResourceLocation key, JsonObject sky) {
        if (!sky.has("skybox")) { return null; }
        JsonObject box = GsonHelper.getAsJsonObject(sky, "skybox");
        if (box.has("panorama")) { return new SkyLookDef.Skybox(ResourceLocation.parse(GsonHelper.getAsString(box, "panorama").trim()), List.of()); }
        List<ResourceLocation> faces = new ArrayList<>();
        for (String face : FACES) {
            if (!box.has(face)) {
                ContentLog.LOGGER.error("Dimension {} gives sky.skybox with no {} face and no panorama, so it draws no skybox", key, face);
                return null;
            }
            faces.add(ResourceLocation.parse(GsonHelper.getAsString(box, face).trim()));
        }
        return new SkyLookDef.Skybox(null, List.copyOf(faces));
    }

    private static SkyLookDef.Aurora aurora(ResourceLocation key, JsonObject aurora) {
        int color = color(key, aurora, "color");
        int top = color(key, aurora, "topColor");
        return new SkyLookDef.Aurora(color == SkyLookDef.UNSET ? AURORA : color, top == SkyLookDef.UNSET ? AURORA_TOP : top);
    }

    private static float unit(ResourceLocation key, JsonObject json, String member, float fallback) {
        float value = GsonHelper.getAsFloat(json, member, fallback);
        if (value >= 0.0F && value <= 1.0F) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives sky.{} {}, which is not from 0 to 1, using {}", key, member, value, fallback);
        return fallback;
    }

    private static int color(ResourceLocation key, JsonObject json, String member) {
        String value = GsonHelper.getAsString(json, member, "").trim();
        return value.isEmpty() ? SkyLookDef.UNSET : ContentParser.color(value, key + " sky." + member) & 0xFFFFFF;
    }

    private static List<SkyLookDef.CloudLayer> layers(ResourceLocation key, JsonObject sky, int cloudHeight, float cloudSpeed, int cloudColor) {
        if (!sky.has("cloudLayers")) { return List.of(); }
        List<SkyLookDef.CloudLayer> layers = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(sky, "cloudLayers")) {
            JsonObject layer = GsonHelper.convertToJsonObject(element, "cloud layer");
            String tint = GsonHelper.getAsString(layer, "color", "").trim();
            layers.add(new SkyLookDef.CloudLayer(GsonHelper.getAsFloat(layer, "height", cloudHeight), GsonHelper.getAsFloat(layer, "speed", cloudSpeed), tint.isEmpty() ? cloudColor : ContentParser.color(tint, key + " sky.cloudLayers") & 0xFFFFFF));
        }
        return List.copyOf(layers);
    }

    private static SkyLookDef.Heat heat(ResourceLocation key, JsonObject heat) {
        return new SkyLookDef.Heat(unit(key, heat, "strength", HEAT_STRENGTH), GsonHelper.getAsFloat(heat, "minTemperature", HOT), GsonHelper.getAsBoolean(heat, "dayOnly", true), followsWorld(key, heat), startDistance(key, heat));
    }

    private static boolean followsWorld(ResourceLocation key, JsonObject heat) {
        String mode = GsonHelper.getAsString(heat, "mode", SCREEN).trim().toLowerCase(Locale.ROOT);
        if (WORLD.equals(mode)) { return true; }
        if (!SCREEN.equals(mode)) { ContentLog.LOGGER.error("Dimension {} gives sky.heat.mode {}, which is neither {} nor {}, using {}", key, mode, SCREEN, WORLD, SCREEN); }
        return false;
    }

    private static float startDistance(ResourceLocation key, JsonObject heat) {
        float value = GsonHelper.getAsFloat(heat, "startDistance", HEAT_START);
        if (value >= 0.0F) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives sky.heat.startDistance {}, which is below zero, using {}", key, value, HEAT_START);
        return HEAT_START;
    }
}
