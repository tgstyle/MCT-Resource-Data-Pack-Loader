package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyPoseDef;
import mctmods.resourcedatapackloader.common.types.ContentTypes;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class ContentParserSkyLook {
    public static final String SAMPLE = "sample";
    private static final List<String> KEYS = Arrays.asList("fogDensity", "fogGroundWeight", "lightSkyColor", "lightBlockColor", "skyFactor", "cloudSpeed", "cloudLayers", "sunBrightness", "moonBrightness", "heat", "snowColor", "waterFogColor", "lavaFogColor", "starColor", "starTwinkle", "lightningColor", "skybox", "aurora", "rainbow", "grassTint", "foliageTint", "birchTint", "nightVisionColor");
    private static final List<String> FACES = Arrays.asList("up", "down", "north", "east", "south", "west");
    private static final List<String> SUN_KEYS = Collections.unmodifiableList(Arrays.asList("texture", "size", "tilt", "roll", "spin", "flip", "color", "auraColor", "auraSize", "auraHorizon", "lineCount", "lineStart", "lineLength", "lineWidth", "lineColor"));
    private static final List<String> SKYBOX_KEYS = Collections.unmodifiableList(Arrays.asList("yaw", "pitch", "roll", "spin", "flip", "followsTime", "panorama", "up", "down", "north", "east", "south", "west"));
    private static final List<String> AURORA_KEYS = Collections.unmodifiableList(Arrays.asList("color", "topColor", "yaw"));
    private static final List<String> HEAT_KEYS = Collections.unmodifiableList(Arrays.asList("strength", "minTemperature", "dayOnly", "mode", "startDistance"));
    private static final List<String> LAYER_KEYS = Collections.unmodifiableList(Arrays.asList("color", "height", "speed"));
    static final List<String> SKY_KEYS = Collections.unmodifiableList(Arrays.asList("skyColor", "cloudColor", "fogColor", "cloudHeight", "hasSkyLight", "surfaceWorld", "respawn", "spawning", "groundLevel", "movementFactor", "fixedTime", "sunriseColors", "nether", "beds", "waterVaporizes", "showFog", "ambientLight", "starBrightness", "respawnDimension", "renderSky", "renderClouds", "renderWeather",
            "fogDensity", "fogGroundWeight", "lightSkyColor", "lightBlockColor", "skyFactor", "cloudSpeed", "cloudLayers", "sunBrightness", "moonBrightness", "heat", "snowColor", "waterFogColor", "lavaFogColor", "starColor", "starTwinkle", "lightningColor", "skybox", "aurora", "rainbow", "grassTint", "foliageTint", "birchTint", "nightVisionColor",
            "sun", "moon", "bodies", "stars", "darkness"));
    private static final int AURORA = 0x40FF90;
    private static final int AURORA_TOP = 0x8040FF;
    private static final List<String> HAZE = Arrays.asList("color", "auraColor", "auraSize", "auraHorizon", "lineCount", "lineStart", "lineLength", "lineWidth", "lineColor");
    public static final float AURA_SIZE = 1.5F;
    private static final int LINE_COLOR = 0x40C0C0C0;
    public static final float GROUND_WEIGHT = 0.5F;
    private static final float HOT = 1.5F;
    private static final float HEAT_STRENGTH = 0.1F;
    private static final float HEAT_START = 32.0F;
    private static final String SCREEN = "screen";
    private static final String WORLD = "world";
    private static final List<String> MODES = Arrays.asList(SCREEN, WORLD);
    private static final String HORIZONTAL = "horizontal";
    private static final String VERTICAL = "vertical";
    private static final String BOTH = "both";
    private static final List<String> FLIPS = Arrays.asList(HORIZONTAL, VERTICAL, BOTH);

    private ContentParserSkyLook() {}

    public static boolean samples(JsonObject sky) { return SAMPLE.equals(Settings.lower(Json.getString(sky, "fogColor", ""))); }

    @Nullable public static SkyLookDef look(ResourceLocation key, JsonObject sky, int cloudHeight, int cloudColor) {
        boolean sample = samples(sky);
        JsonObject sun = Json.getJsonObject(sky, "sun", new JsonObject());
        Json.unknownKeys(sun, "sun", SUN_KEYS);
        if (!sample && KEYS.stream().noneMatch(sky::has) && HAZE.stream().noneMatch(sun::has)) { return null; }
        float cloudSpeed = Json.getFloat(sky, "cloudSpeed", 1.0F);
        String owner = "Dimension " + key + " sky.sun";
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
                sky.has("heat") ? heat(key, Json.getJsonObject(sky, "heat")) : null,
                color(key, sky, "snowColor"),
                color(key, sky, "waterFogColor"),
                color(key, sky, "lavaFogColor"),
                color(key, sky, "starColor"),
                unit(key, sky, "starTwinkle", 0.0F),
                color(key, sky, "lightningColor"),
                skybox(key, sky),
                sky.has("aurora") ? aurora(key, Json.getJsonObject(sky, "aurora")) : null,
                Json.getBoolean(sky, "rainbow", false),
                color(key, sun, "color"),
                color(key, sun, "auraColor"),
                Json.ranged(sun, "auraSize", AURA_SIZE, 0.0F, 1000.0F, owner),
                Json.ranged(sun, "auraHorizon", 0.0F, 0.0F, 90.0F, owner),
                (int) Json.ranged(sun, "lineCount", 0.0F, 0.0F, 64.0F, owner),
                Json.ranged(sun, "lineStart", 0.25F, 0.0F, 1000.0F, owner),
                Json.ranged(sun, "lineLength", 3.0F, 0.0F, 1000.0F, owner),
                Json.ranged(sun, "lineWidth", 0.02F, 0.0F, 10.0F, owner),
                argb(key, sun),
                color(key, sky, "grassTint"),
                color(key, sky, "foliageTint"),
                color(key, sky, "birchTint"),
                color(key, sky, "nightVisionColor"));
    }

    @Nullable private static SkyLookDef.Skybox skybox(ResourceLocation key, JsonObject sky) {
        if (!sky.has("skybox")) { return null; }
        JsonObject box = Json.getJsonObject(sky, "skybox");
        Json.unknownKeys(box, "skybox", SKYBOX_KEYS);
        float yaw = finite(key, box, "sky.skybox", "yaw");
        float pitch = finite(key, box, "sky.skybox", "pitch");
        float roll = finite(key, box, "sky.skybox", "roll");
        float spin = finite(key, box, "sky.skybox", "spin");
        String flip = flip(key, box, "sky.skybox");
        boolean flipU = flipsU(flip);
        boolean flipV = flipsV(flip);
        boolean follows = Json.getBoolean(box, "followsTime", false);
        if (box.has("panorama")) { return new SkyLookDef.Skybox(new ResourceLocation(Json.getString(box, "panorama").trim()), Collections.emptyList(), yaw, pitch, roll, spin, flipU, flipV, follows); }
        List<ResourceLocation> faces = new ArrayList<>();
        for (String face : FACES) {
            if (!box.has(face)) {
                ContentLog.LOGGER.error("Dimension {} gives sky.skybox with no {} face and no panorama, so it draws no skybox", key, face);
                return null;
            }
            faces.add(new ResourceLocation(Json.getString(box, face).trim()));
        }
        return new SkyLookDef.Skybox(null, Collections.unmodifiableList(faces), yaw, pitch, roll, spin, flipU, flipV, follows);
    }

    private static SkyLookDef.Aurora aurora(ResourceLocation key, JsonObject aurora) {
        Json.unknownKeys(aurora, "aurora", AURORA_KEYS);
        int color = color(key, aurora, "color");
        int top = color(key, aurora, "topColor");
        return new SkyLookDef.Aurora(color == SkyLookDef.UNSET ? AURORA : color, top == SkyLookDef.UNSET ? AURORA_TOP : top, finite(key, aurora, "sky.aurora", "yaw"));
    }

    static float finite(ResourceLocation key, JsonObject json, String owner, String member) {
        float value = Json.getFloat(json, member, 0.0F);
        if (Float.isFinite(value)) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives {}.{} of {}, which is not a finite number, using 0", key, owner, member, value);
        return 0.0F;
    }

    static SkyPoseDef pose(ResourceLocation key, JsonObject json, String owner) {
        String flip = flip(key, json, owner);
        return new SkyPoseDef(finite(key, json, owner, "roll"), finite(key, json, owner, "spin"), flipsU(flip), flipsV(flip));
    }

    private static String flip(ResourceLocation key, JsonObject json, String owner) {
        if (!json.has("flip")) { return ""; }
        String value = Settings.lower(Json.getString(json, "flip", ""));
        if (FLIPS.contains(value)) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives {}.flip of '{}', which is not horizontal, vertical or both, using none", key, owner, value);
        return "";
    }

    private static boolean flipsU(String flip) { return HORIZONTAL.equals(flip) || BOTH.equals(flip); }

    private static boolean flipsV(String flip) { return VERTICAL.equals(flip) || BOTH.equals(flip); }

    static float unit(ResourceLocation key, JsonObject json, String member, float fallback) { return Json.ranged(json, member, fallback, 0.0F, 1.0F, "Dimension " + key + " sky"); }

    static int color(ResourceLocation key, JsonObject json, String member) {
        String value = Json.getString(json, member, "").trim();
        return value.isEmpty() ? SkyLookDef.UNSET : ContentTypes.rgb(value, key + " sky." + member);
    }

    static int argb(ResourceLocation key, JsonObject json) {
        String value = Json.getString(json, "lineColor", "").trim();
        if (value.isEmpty()) { return LINE_COLOR; }
        int parsed = ContentTypes.color(value, key + " sky.sun.lineColor");
        return ContentTypes.hexDigits(value) <= 6 ? parsed | 0xFF000000 : parsed;
    }

    private static List<SkyLookDef.CloudLayer> layers(ResourceLocation key, JsonObject sky, int cloudHeight, float cloudSpeed, int cloudColor) {
        if (!sky.has("cloudLayers")) { return Collections.emptyList(); }
        List<SkyLookDef.CloudLayer> layers = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(sky, "cloudLayers")) {
            JsonObject layer = Json.getJsonObject(element, "cloud layer");
            Json.unknownKeys(layer, "cloudLayers", LAYER_KEYS);
            String tint = Json.getString(layer, "color", "").trim();
            layers.add(new SkyLookDef.CloudLayer(Json.getFloat(layer, "height", cloudHeight), Json.getFloat(layer, "speed", cloudSpeed), tint.isEmpty() ? cloudColor : ContentTypes.rgb(tint, key + " sky.cloudLayers")));
        }
        return Collections.unmodifiableList(layers);
    }

    private static SkyLookDef.Heat heat(ResourceLocation key, JsonObject heat) {
        Json.unknownKeys(heat, "heat", HEAT_KEYS);
        return new SkyLookDef.Heat(unit(key, heat, "strength", HEAT_STRENGTH), Json.getFloat(heat, "minTemperature", HOT), Json.getBoolean(heat, "dayOnly", true), followsWorld(key, heat), startDistance(key, heat));
    }

    private static boolean followsWorld(ResourceLocation key, JsonObject heat) { return Json.choiceIs(heat, "mode", SCREEN, "Dimension " + key + " sky.heat", MODES, WORLD); }

    private static float startDistance(ResourceLocation key, JsonObject heat) {
        float value = Json.getFloat(heat, "startDistance", HEAT_START);
        if (value >= 0.0F) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives sky.heat.startDistance {}, which is below zero, using {}", key, value, HEAT_START);
        return HEAT_START;
    }
}
