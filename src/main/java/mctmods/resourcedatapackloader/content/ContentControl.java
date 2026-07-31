package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public final class ContentControl {
    public static final String ORES = "ores";
    public static final String BIOMES = "biomes";
    public static final String GENERATORS = "generators";
    public static final String STRUCTURES = "structures";
    public static final String SPAWNING = "spawning";
    public static final String BEDROCK = "bedrock";
    public static final String VOID = "void";
    public static final String RECIPES = "recipes";
    private static final String DEFAULT = "default";
    private static final String GLOBAL = "global";
    private static final String OFF = "off";

    private ContentControl() {}

    public static boolean off(String group) { return OFF.equals(mode(group)); }

    public static boolean packDecides(String group) { return DEFAULT.equals(mode(group)); }

    public static boolean flag(String group, String key, boolean fallback) {
        JsonElement value = setting(group, key);
        return value == null ? fallback : value.getAsBoolean();
    }

    public static int number(String group, String key, int fallback) {
        JsonElement value = setting(group, key);
        return value == null ? fallback : value.getAsInt();
    }

    public static float decimal(String group, String key, float fallback) {
        JsonElement value = setting(group, key);
        return value == null ? fallback : value.getAsFloat();
    }

    public static String text(String group, String key, String fallback) {
        JsonElement value = setting(group, key);
        return value == null ? fallback : value.getAsString();
    }

    public static String[] list(String group, String key, String[] fallback) {
        JsonElement value = setting(group, key);
        if (value == null || !value.isJsonArray()) { return fallback; }

        JsonArray array = value.getAsJsonArray();
        List<String> found = new ArrayList<>();
        for (JsonElement entry : array) { found.add(entry.getAsString()); }
        return found.toArray(new String[0]);
    }

    public static int[] numbers(String group, String key, int[] fallback) {
        JsonElement value = setting(group, key);
        if (value == null || !value.isJsonArray()) { return fallback; }

        JsonArray array = value.getAsJsonArray();
        int[] found = new int[array.size()];
        for (int i = 0; i < found.length; i++) { found[i] = array.get(i).getAsInt(); }
        return found;
    }

    @Nullable private static JsonElement setting(String group, String key) {
        if (!packDecides(group)) { return null; }

        WorldTemplateDef template = ContentWorldTemplates.active();
        if (template == null || template.settings == null) { return null; }

        JsonObject settings = template.settings;
        return settings.has(key) ? settings.get(key) : null;
    }

    private static String mode(String group) {
        String value = raw(group).trim().toLowerCase(Locale.ROOT);
        if (DEFAULT.equals(value) || GLOBAL.equals(value) || OFF.equals(value)) { return value; }

        ContentLog.LOGGER.error("control.{} is '{}', which is not default, global or off, using default", group, value);
        return DEFAULT;
    }

    private static String raw(String group) {
        switch (group) {
            case ORES: return Config.control.ores;
            case BIOMES: return Config.control.biomes;
            case GENERATORS: return Config.control.generators;
            case STRUCTURES: return Config.control.structures;
            case SPAWNING: return Config.control.spawning;
            case BEDROCK: return Config.control.bedrock;
            case VOID: return Config.control.voidWorld;
            case RECIPES: return Config.control.recipes;
            default: return DEFAULT;
        }
    }
}
