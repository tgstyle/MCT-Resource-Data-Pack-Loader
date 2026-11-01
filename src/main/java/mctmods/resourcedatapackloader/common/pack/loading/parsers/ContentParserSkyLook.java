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
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public final class ContentParserSkyLook {
    public static final String SAMPLE = "sample";
    private static final List<String> KEYS = List.of("fogDensity", "fogGroundWeight", "lightSkyColor", "lightBlockColor", "skyFactor", "cloudSpeed", "cloudLayers", "sunBrightness", "moonBrightness", "heat", "snowColor", "waterFogColor", "lavaFogColor", "starColor", "starTwinkle", "lightningColor", "skybox", "aurora", "rainbow", "grassTint", "foliageTint");
    private static final List<String> FACES = List.of("up", "down", "north", "east", "south", "west");
    private static final int AURORA = 0x40FF90;
    private static final int AURORA_TOP = 0x8040FF;
    private static final List<String> HAZE = List.of("color", "auraColor", "auraSize", "auraHorizon", "lineCount", "lineStart", "lineLength", "lineWidth", "lineColor");
    private static final int LINE_COLOR = 0x40C0C0C0;
    public static final float GROUND_WEIGHT = 0.5F;
    public static final float AURA_SIZE = 1.5F;
    private static final float HOT = 1.5F;
    private static final float HEAT_STRENGTH = 0.1F;
    private static final float HEAT_START = 32.0F;
    private static final String SCREEN = "screen";
    private static final String WORLD = "world";
    private static final List<String> MODES = List.of(SCREEN, WORLD);

    private ContentParserSkyLook() {}

    public static boolean samples(JsonObject sky) { return SAMPLE.equals(Settings.lower(Json.getString(sky, "fogColor", ""))); }

    @Nullable public static SkyLookDef look(Identifier key, JsonObject sky, int cloudHeight, int cloudColor) {
        boolean sample = samples(sky);
        JsonObject sun = Json.getJsonObject(sky, "sun", new JsonObject());
        if (!sample && KEYS.stream().noneMatch(sky::has) && HAZE.stream().noneMatch(sun::has)) { return null; }
        float cloudSpeed = Json.getFloat(sky, "cloudSpeed", 1.0F);
        return new SkyLookDef(sample, unit(key, sky, "fogDensity", 0.0F), unit(key, sky, "fogGroundWeight", GROUND_WEIGHT), color(key, sky, "lightSkyColor"), color(key, sky, "lightBlockColor"),
                unit(key, sky, "skyFactor", 1.0F), cloudSpeed, layers(key, sky, cloudHeight, cloudSpeed, cloudColor), unit(key, sky, "sunBrightness", 1.0F), unit(key, sky, "moonBrightness", 1.0F),
                sky.has("heat") ? heat(key, Json.getJsonObject(sky, "heat")) : null, color(key, sky, "snowColor"), color(key, sky, "waterFogColor"), color(key, sky, "lavaFogColor"),
                color(key, sky, "starColor"), unit(key, sky, "starTwinkle", 0.0F), color(key, sky, "lightningColor"), skybox(key, sky),
                sky.has("aurora") ? aurora(key, Json.getJsonObject(sky, "aurora")) : null, Json.getBoolean(sky, "rainbow", false),
                color(key, sun, "color"), color(key, sun, "auraColor"), auraSize(key, sun), auraHorizon(key, sun),
                (int) Json.ranged(sun, "lineCount", 0.0F, 0.0F, 64.0F, "Dimension " + key + " sky.sun"), Json.ranged(sun, "lineStart", 0.25F, 0.0F, 1000.0F, "Dimension " + key + " sky.sun"),
                Json.ranged(sun, "lineLength", 3.0F, 0.0F, 1000.0F, "Dimension " + key + " sky.sun"), Json.ranged(sun, "lineWidth", 0.02F, 0.0F, 10.0F, "Dimension " + key + " sky.sun"), argb(key, sun),
                color(key, sky, "grassTint"), color(key, sky, "foliageTint"));
    }

    @Nullable private static SkyLookDef.Skybox skybox(Identifier key, JsonObject sky) {
        if (!sky.has("skybox")) { return null; }
        JsonObject box = Json.getJsonObject(sky, "skybox");
        if (box.has("panorama")) { return new SkyLookDef.Skybox(Identifier.parse(Json.getString(box, "panorama").trim()), List.of(), yaw(key, box), pitch(key, box), roll(key, box), spin(key, box), Json.getBoolean(box, "followsTime", false)); }
        List<Identifier> faces = new ArrayList<>();
        for (String face : FACES) {
            if (!box.has(face)) {
                ContentLog.LOGGER.error("Dimension {} gives sky.skybox with no {} face and no panorama, so it draws no skybox", key, face);
                return null;
            }
            faces.add(Identifier.parse(Json.getString(box, face).trim()));
        }
        return new SkyLookDef.Skybox(null, List.copyOf(faces), yaw(key, box), pitch(key, box), roll(key, box), spin(key, box), Json.getBoolean(box, "followsTime", false));
    }

    private static SkyLookDef.Aurora aurora(Identifier key, JsonObject aurora) {
        int color = color(key, aurora, "color");
        int top = color(key, aurora, "topColor");
        return new SkyLookDef.Aurora(color == SkyLookDef.UNSET ? AURORA : color, top == SkyLookDef.UNSET ? AURORA_TOP : top, finite(key, aurora, "sky.aurora", "yaw"));
    }

    private static float yaw(Identifier key, JsonObject box) { return finite(key, box, "sky.skybox", "yaw"); }

    private static float pitch(Identifier key, JsonObject box) { return finite(key, box, "sky.skybox", "pitch"); }

    private static float roll(Identifier key, JsonObject box) { return finite(key, box, "sky.skybox", "roll"); }

    private static float spin(Identifier key, JsonObject box) { return finite(key, box, "sky.skybox", "spin"); }

    private static float auraSize(Identifier key, JsonObject sun) {
        float value = Json.getFloat(sun, "auraSize", AURA_SIZE);
        if (Float.isFinite(value)) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives sky.sun.auraSize of {}, which is not a finite number, using {}", key, value, AURA_SIZE);
        return AURA_SIZE;
    }

    private static float auraHorizon(Identifier key, JsonObject sun) { return Math.max(0.0F, finite(key, sun, "sky.sun", "auraHorizon")); }

    static float finite(Identifier key, JsonObject json, String owner, String member) {
        float value = Json.getFloat(json, member, 0.0F);
        if (Float.isFinite(value)) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives {}.{} of {}, which is not a finite number, using 0", key, owner, member, value);
        return 0.0F;
    }

    static SkyPoseDef pose(Identifier key, JsonObject json, String owner) { return new SkyPoseDef(finite(key, json, owner, "roll"), finite(key, json, owner, "spin")); }

    static float unit(Identifier key, JsonObject json, String member, float fallback) { return Json.ranged(json, member, fallback, 0.0F, 1.0F, "Dimension " + key + " sky"); }

    static int color(Identifier key, JsonObject json, String member) {
        String value = Json.getString(json, member, "").trim();
        return value.isEmpty() ? SkyLookDef.UNSET : ContentParser.color(value, key + " sky." + member) & 0xFFFFFF;
    }

    static int argb(Identifier key, JsonObject json) {
        String value = Json.getString(json, "lineColor", "").trim();
        if (value.isEmpty()) { return LINE_COLOR; }
        Long hex = ContentTypes.hex(value);
        if (hex == null) {
            ContentLog.LOGGER.error("Color '{}' in {} sky.sun.lineColor is not hexadecimal, using white", value, key);
            return 0xFFFFFFFF;
        }
        int parsed = (int) (hex & 0xFFFFFFFFL);
        return ContentTypes.hexDigits(value) <= 6 ? parsed | 0xFF000000 : parsed;
    }

    private static List<SkyLookDef.CloudLayer> layers(Identifier key, JsonObject sky, int cloudHeight, float cloudSpeed, int cloudColor) {
        if (!sky.has("cloudLayers")) { return List.of(); }
        List<SkyLookDef.CloudLayer> layers = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(sky, "cloudLayers")) {
            JsonObject layer = Json.getJsonObject(element, "cloud layer");
            String tint = Json.getString(layer, "color", "").trim();
            layers.add(new SkyLookDef.CloudLayer(Json.getFloat(layer, "height", cloudHeight), Json.getFloat(layer, "speed", cloudSpeed), tint.isEmpty() ? cloudColor : ContentParser.color(tint, key + " sky.cloudLayers") & 0xFFFFFF));
        }
        return List.copyOf(layers);
    }

    private static SkyLookDef.Heat heat(Identifier key, JsonObject heat) { return new SkyLookDef.Heat(unit(key, heat, "strength", HEAT_STRENGTH), Json.getFloat(heat, "minTemperature", HOT), Json.getBoolean(heat, "dayOnly", true), followsWorld(key, heat), startDistance(key, heat)); }

    private static boolean followsWorld(Identifier key, JsonObject heat) { return WORLD.equals(Json.choice(heat, "mode", SCREEN, "Dimension " + key + " sky.heat", MODES)); }

    private static float startDistance(Identifier key, JsonObject heat) {
        float value = Json.getFloat(heat, "startDistance", HEAT_START);
        if (value >= 0.0F) { return value; }
        ContentLog.LOGGER.error("Dimension {} gives sky.heat.startDistance {}, which is below zero, using {}", key, value, HEAT_START);
        return HEAT_START;
    }
}
