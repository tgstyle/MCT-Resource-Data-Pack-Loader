package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.def.SpreadDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

final class ContentCofhFields {
    private static final Map<String, String> TURN_NAMES = new HashMap<>();

    static {
        TURN_NAMES.put("NONE", ShapeDef.NO_TURN);
        TURN_NAMES.put("CLOCKWISE_90", ShapeDef.QUARTER);
        TURN_NAMES.put("CLOCKWISE_180", ShapeDef.HALF);
        TURN_NAMES.put("COUNTERCLOCKWISE_90", ShapeDef.THREEQUARTER);
        TURN_NAMES.put("LEFT_RIGHT", ShapeDef.LEFTRIGHT);
        TURN_NAMES.put("FRONT_BACK", ShapeDef.FRONTBACK);
    }

    private ContentCofhFields() {}

    static double doubled(JsonObject source) {
        JsonElement element = source.get("integrity");
        if (!number(element)) { return 1.0; }
        return element.getAsDouble();
    }

    static JsonObject range(int least, int most) {
        JsonObject out = new JsonObject();
        out.addProperty("min", least);
        out.addProperty("max", Math.max(least, most));
        return out;
    }

    static void spread(String name, JsonObject source, JsonObject out) {
        String distribution = JsonUtils.getString(source, "distribution", "uniform").trim().toLowerCase(Locale.ROOT);
        JsonObject spread = new JsonObject();
        switch (distribution) {
            case "gaussian":
                int center = integer(source, "center-height", 64);
                int range = Math.max(1, integer(source, "spread", 16));
                spread.addProperty("type", SpreadDef.CENTERED);
                spread.addProperty("center", center);
                spread.addProperty("range", range);
                spread.addProperty("smoothness", integer(source, "smoothness", 2));
                out.addProperty("minHeight", Math.max(0, center - range));
                out.addProperty("maxHeight", center + range);
                out.add("spread", spread);
                return;
            case "fractal":
                int least = bound(source, "min-height", 0, true);
                int veinHeight = integer(source, "vein-height", 64);
                spread.addProperty("type", SpreadDef.SPRAWL);
                spread.addProperty("veinHeight", veinHeight);
                spread.addProperty("veinDiameter", integer(source, "vein-diameter", 12));
                spread.addProperty("verticalDensity", integer(source, "vertical-density", 16));
                spread.addProperty("horizontalDensity", integer(source, "horizontal-density", 32));
                out.addProperty("minHeight", least);
                out.addProperty("maxHeight", least + veinHeight);
                out.add("spread", spread);
                return;
            case "custom":
                out.addProperty("minHeight", bound(source, "y-offset", 0, true));
                out.addProperty("maxHeight", bound(source, "y-offset", 64, false));
                return;
        }
        String surface = surfaced(distribution);
        if (surface != null) {
            spread.addProperty("type", surface);
            if ("cave".equals(distribution) && "true".equalsIgnoreCase(JsonUtils.getString(source, "ceiling", "false"))) { spread.addProperty("ceiling", true); }
            out.addProperty("minHeight", bound(source, "min-height", 0, true));
            out.addProperty("maxHeight", bound(source, "max-height", 256, false) - 1);
            out.add("spread", spread);
            return;
        }
        if (!"uniform".equals(distribution)) {
            ContentLog.LOGGER.warn("CoFH World entry '{}' uses the '{}' distribution, which has nothing to convert to, spreading it evenly instead", name, distribution);
        }
        out.addProperty("minHeight", bound(source, "min-height", 0, true));
        out.addProperty("maxHeight", bound(source, "max-height", 64, false) - 1);
    }

    @Nullable private static String surfaced(String distribution) {
        if ("surface".equals(distribution) || "decoration".equals(distribution)) { return SpreadDef.TERRAIN; }
        if ("cave".equals(distribution)) { return SpreadDef.CAVERN; }
        if ("underwater".equals(distribution) || "underfluid".equals(distribution)) { return SpreadDef.SUBMERGED; }
        return null;
    }

    static JsonArray names(JsonObject generator, String key) {
        JsonArray out = new JsonArray();
        JsonElement element = generator.get(key);
        if (element == null) { return out; }
        if (!element.isJsonArray()) {
            String one = named(element);
            if (!one.isEmpty()) { out.add(one); }
            return out;
        }
        for (JsonElement each : element.getAsJsonArray()) {
            String one = named(each);
            if (!one.isEmpty()) { out.add(one); }
        }
        return out;
    }

    static String single(JsonObject generator, String key) {
        JsonArray all = names(generator, key);
        return all.size() == 0 ? "" : all.get(0).getAsString();
    }

    static String raw(JsonObject generator) {
        JsonElement element = generator.get("structure");
        if (element == null) { return ""; }
        if (element.isJsonArray()) { element = element.getAsJsonArray().size() == 0 ? null : element.getAsJsonArray().get(0); }
        if (element == null) { return ""; }
        if (element.isJsonObject()) { return JsonUtils.getString(element.getAsJsonObject(), "value", ""); }
        return element.getAsString();
    }

    private static String named(JsonElement element) {
        if (!element.isJsonObject()) { return qualified(element.getAsString()); }
        JsonObject one = element.getAsJsonObject();
        String name = JsonUtils.getString(one, "name", "");
        if (name.isEmpty()) { return ""; }
        int meta = integer(one, "data", integer(one, "metadata", -1));
        return meta < 0 ? qualified(name) : qualified(name) + ":" + Math.min(15, meta);
    }

    static void amount(JsonObject source, String key, JsonObject out, String name, int fallback) {
        JsonElement element = source.get(key);
        if (element == null) { out.addProperty(name, fallback); return; }
        if (number(element)) { out.addProperty(name, element.getAsInt()); return; }
        if (element.isJsonObject()) {
            JsonObject range = element.getAsJsonObject();
            if (number(range.get("min")) && number(range.get("max"))) {
                JsonObject copy = new JsonObject();
                copy.addProperty("min", range.get("min").getAsInt());
                copy.addProperty("max", range.get("max").getAsInt());
                out.add(name, copy);
                return;
            }
            if (number(range.get("value")) && number(range.get("variance"))) {
                int middle = range.get("value").getAsInt();
                int swing = Math.abs(range.get("variance").getAsInt());
                out.add(name, range(middle - swing, middle + swing));
                return;
            }
            if (number(range.get("value"))) { out.addProperty(name, range.get("value").getAsInt()); return; }
            if (number(range.get("variance"))) {
                int swing = Math.abs(range.get("variance").getAsInt());
                out.add(name, range(-swing, swing));
                return;
            }
        }
        ContentLog.LOGGER.warn("A CoFH World entry gives '{}' as a value this conversion cannot express, using {}", key, fallback);
        out.addProperty(name, fallback);
    }

    private static int bound(JsonObject source, String key, int fallback, boolean low) {
        JsonElement element = source.get(key);
        if (element == null) { return fallback; }
        if (number(element)) { return element.getAsInt(); }
        if (element.isJsonObject()) {
            JsonObject range = element.getAsJsonObject();
            JsonElement end = low ? range.get("min") : range.get("max");
            if (number(end)) { return end.getAsInt(); }
            if (number(range.get("value")) && number(range.get("variance"))) {
                int swing = Math.abs(range.get("variance").getAsInt());
                return range.get("value").getAsInt() + (low ? -swing : swing);
            }
            if (number(range.get("value"))) { return range.get("value").getAsInt(); }
        }
        ContentLog.LOGGER.warn("A CoFH World entry gives '{}' as a value this conversion cannot express, using {}", key, fallback);
        return fallback;
    }

    static int integer(JsonObject source, String key, int fallback) {
        JsonElement element = source.get(key);
        if (!number(element)) { return fallback; }
        return element.getAsInt();
    }

    private static boolean number(@Nullable JsonElement element) { return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber(); }

    @Nullable static JsonArray blocks(JsonObject generator) {
        JsonElement block = generator.get("block");
        if (block == null) { return null; }
        JsonArray out = new JsonArray();
        if (block.isJsonPrimitive()) {
            out.add(weighted(qualified(block.getAsString()), 0, 100, new JsonObject()));
            return out;
        }
        if (block.isJsonObject()) {
            out.add(entry(block.getAsJsonObject()));
            return out;
        }
        for (JsonElement element : block.getAsJsonArray()) {
            if (element.isJsonPrimitive()) { out.add(weighted(qualified(element.getAsString()), 0, 100, new JsonObject())); }
            else if (element.isJsonObject()) { out.add(entry(element.getAsJsonObject())); }
        }
        return out.size() == 0 ? null : out;
    }

    private static JsonObject entry(JsonObject one) {
        JsonObject properties = properties(one);
        int meta = properties.entrySet().isEmpty() ? MathHelper.clamp(integer(one, "data", integer(one, "metadata", 0)), 0, 15) : 0;
        return weighted(qualified(JsonUtils.getString(one, "name", "")), meta, MathHelper.clamp(integer(one, "weight", 100), 1, 1000000), properties);
    }

    private static JsonObject properties(JsonObject one) {
        JsonObject out = new JsonObject();
        JsonElement properties = one.get("properties");
        if (properties == null || !properties.isJsonObject()) { return out; }
        for (Map.Entry<String, JsonElement> property : properties.getAsJsonObject().entrySet()) {
            if (!property.getValue().isJsonPrimitive()) { continue; }
            out.addProperty(property.getKey(), property.getValue().getAsString());
        }
        return out;
    }

    static JsonObject weighted(String block, int meta, int weight, JsonObject properties) {
        JsonObject one = new JsonObject();
        one.addProperty("block", block);
        one.addProperty("meta", meta);
        one.addProperty("weight", weight);
        if (!properties.entrySet().isEmpty()) { one.add("properties", properties); }
        return one;
    }

    private static String qualified(String name) { return name.contains(":") ? name : "minecraft:" + name; }

    static void addTurns(JsonObject shape, JsonObject generator) {
        JsonArray turns = named(generator, "rotation", "turn");
        if (turns.size() > 0) { shape.add("turns", turns); }
        JsonArray mirrors = named(generator, "mirror", "mirror");
        if (mirrors.size() > 0) { shape.add("mirrors", mirrors); }
    }

    private static JsonArray named(JsonObject generator, String key, String nameKey) {
        JsonArray out = new JsonArray();
        JsonElement held = generator.get(key);
        if (held == null) { return out; }
        JsonArray listed = new JsonArray();
        if (held.isJsonArray()) { listed = held.getAsJsonArray(); }
        else { listed.add(held); }
        for (JsonElement element : listed) {
            boolean object = element.isJsonObject();
            String raw = object ? JsonUtils.getString(element.getAsJsonObject(), "value", "") : element.getAsString();
            String mapped = TURN_NAMES.get(raw.trim().toUpperCase(Locale.ROOT));
            if (mapped == null) { continue; }
            JsonObject pick = new JsonObject();
            pick.addProperty(nameKey, mapped);
            if (object && element.getAsJsonObject().has("weight")) { pick.addProperty("weight", JsonUtils.getInt(element.getAsJsonObject(), "weight", 1)); }
            out.add(pick);
        }
        return out;
    }

    static JsonArray ignored(JsonObject generator) {
        JsonArray out = new JsonArray();
        JsonElement held = generator.get("ignored-block");
        if (held == null) { return out; }
        if (!held.isJsonArray()) { out.add(target(held)); return out; }
        for (JsonElement element : held.getAsJsonArray()) { out.add(target(element)); }
        return out;
    }

    static JsonArray material(JsonObject generator) {
        JsonArray out = new JsonArray();
        JsonElement material = generator.get("material");
        if (material == null) { out.add("minecraft:stone"); return out; }
        if (!material.isJsonArray()) { out.add(target(material)); return out; }
        for (JsonElement element : material.getAsJsonArray()) { out.add(target(element)); }
        return out;
    }

    private static JsonElement target(JsonElement material) {
        if (!material.isJsonObject()) { return new JsonPrimitive(qualified(material.getAsString())); }
        JsonObject one = material.getAsJsonObject();
        JsonObject out = new JsonObject();
        out.addProperty("block", qualified(JsonUtils.getString(one, "name", "minecraft:stone")));
        JsonObject properties = properties(one);
        if (!properties.entrySet().isEmpty()) {
            out.add("properties", properties);
            return out;
        }
        int meta = integer(one, "data", integer(one, "metadata", -1));
        if (meta >= 0) { out.addProperty("meta", Math.min(15, meta)); }
        return out;
    }

    static void dimensions(JsonObject source, JsonObject out) {
        JsonElement dimension = source.get("dimension");
        if (dimension == null || !dimension.isJsonObject()) { return; }
        JsonObject one = dimension.getAsJsonObject();
        JsonArray values = JsonUtils.getJsonArray(one, "value", new JsonArray());
        if (values.size() == 0) { return; }
        out.add("dimensions", values);
        if ("blacklist".equalsIgnoreCase(JsonUtils.getString(one, "restriction", "whitelist"))) { out.addProperty("dimensionsAreBlacklist", true); }
    }

    static void biomes(JsonObject source, JsonObject out) {
        JsonElement biome = source.get("biome");
        if (biome == null || !biome.isJsonObject()) { return; }
        JsonObject one = biome.getAsJsonObject();
        JsonArray names = new JsonArray();
        JsonArray types = new JsonArray();
        for (JsonElement element : JsonUtils.getJsonArray(one, "value", new JsonArray())) {
            if (element.isJsonPrimitive()) { names.add(element.getAsString()); continue; }
            if (!element.isJsonObject()) { continue; }
            JsonObject value = element.getAsJsonObject();
            if ("dictionary".equalsIgnoreCase(JsonUtils.getString(value, "type", ""))) { types.add(JsonUtils.getString(value, "entry", "").toUpperCase(Locale.ROOT)); }
            else { names.add(JsonUtils.getString(value, "entry", JsonUtils.getString(value, "name", ""))); }
        }
        if (names.size() > 0) { out.add("biomes", names); }
        if (types.size() > 0) { out.add("biomeTypes", types); }
        if ((names.size() > 0 || types.size() > 0) && "blacklist".equalsIgnoreCase(JsonUtils.getString(one, "restriction", "whitelist"))) { out.addProperty("biomesAreBlacklist", true); }
    }
}
