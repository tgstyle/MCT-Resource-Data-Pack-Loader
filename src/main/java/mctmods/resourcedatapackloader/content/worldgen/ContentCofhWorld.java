package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.def.SpreadDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Nullable;

public final class ContentCofhWorld {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String WORLD = "world";
    private static final Map<String, String> SHAPES = shapes();
    private static final Map<ResourceLocation, File> TEMPLATE_JARS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, String> TEMPLATE_ENTRIES = new LinkedHashMap<>();
    private static File source;
    private static String folder = "";
    private static String owner = "";

    private ContentCofhWorld() {}

    public static Map<ResourceLocation, String> collect() {
        Map<ResourceLocation, String> found = new LinkedHashMap<>();
        for (ModContainer container : Loader.instance().getModList()) {
            File source = container.getSource();
            if (source == null || !source.isFile()) { continue; }

            readJar(source, container.getModId(), found);
        }
        if (found.isEmpty()) { return found; }

        ContentLog.LOGGER.warn("Converted {} CoFH World entry/entries from mod jars. Anything with no equivalent here is named in the lines above and was left out. Translating them into a pack is still the supported way, and the only way to change what they generate", found.size());
        return found;
    }

    private static void readJar(File jar, String modid, Map<ResourceLocation, String> found) {
        try (ZipFile zip = new ZipFile(jar)) {
            String prefix = "assets/" + modid + "/" + WORLD + "/";
            java.util.Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().startsWith(prefix) || !entry.getName().endsWith(".json")) { continue; }

                try (InputStream stream = zip.getInputStream(entry)) {
                    source = jar;
                    owner = modid;
                    folder = entry.getName().substring(0, entry.getName().lastIndexOf('/') + 1);
                    convert(modid, entry.getName(), new String(bytes(stream, (int) entry.getSize()), StandardCharsets.UTF_8), found);
                }
                catch (RuntimeException ex) { ContentLog.LOGGER.error("CoFH World file {} in {} is malformed, leaving it out: {}", entry.getName(), jar.getName(), ex.toString()); }
            }
        }
        catch (IOException ex) { ContentLog.LOGGER.error("Could not read CoFH World files from {}: {}", jar.getName(), ex.getMessage()); }
    }

    private static byte[] bytes(InputStream stream, int length) throws IOException {
        byte[] out = new byte[Math.max(0, length)];
        int read = 0;
        while (read < out.length) {
            int step = stream.read(out, read, out.length - read);
            if (step < 0) { break; }

            read += step;
        }
        return out;
    }

    @Nullable public static InputStream openTemplate(ResourceLocation id) {
        File jar = TEMPLATE_JARS.get(id);
        String entry = TEMPLATE_ENTRIES.get(id);
        if (jar == null || entry == null) { return null; }

        try (ZipFile zip = new ZipFile(jar)) {
            ZipEntry found = zip.getEntry(entry);
            if (found == null) { return null; }

            try (InputStream stream = zip.getInputStream(found)) { return new ByteArrayInputStream(bytes(stream, (int) found.getSize())); }
        }
        catch (IOException ex) {
            ContentLog.LOGGER.error("Could not read structure {} out of {}: {}", id, jar.getName(), ex.getMessage());
            return null;
        }
    }

    private static void convert(String modid, String path, String contents, Map<ResourceLocation, String> found) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null || !json.has("populate")) { return; }

        JsonObject populate = JsonUtils.getJsonObject(json, "populate");
        for (Map.Entry<String, JsonElement> entry : populate.entrySet()) {
            if (!entry.getValue().isJsonObject()) { continue; }

            expand(modid, path, entry.getKey(), entry.getValue().getAsJsonObject(), found);
        }
    }

    private static void expand(String modid, String path, String name, JsonObject source, Map<ResourceLocation, String> found) {
        if ("sequential".equals(JsonUtils.getString(source, "distribution", "uniform").trim().toLowerCase(Locale.ROOT))) {
            JsonElement features = source.get("features");
            if (features == null || !features.isJsonArray()) {
                ContentLog.LOGGER.warn("CoFH World entry '{}' is a sequential distribution with no features, leaving it out", name);
                return;
            }
            int step = 0;
            for (JsonElement feature : features.getAsJsonArray()) {
                step++;
                if (feature.isJsonObject()) { expand(modid, path, name + "_" + step, feature.getAsJsonObject(), found); }
            }
            return;
        }

        List<JsonObject> generators = new ArrayList<>();
        flatten(JsonUtils.getJsonObject(source, "generator", new JsonObject()), generators);
        int index = 0;
        for (JsonObject generator : generators) {
            JsonObject translated = translate(name, source, generator);
            index++;
            if (translated == null) { continue; }

            String suffix = generators.size() > 1 ? "_" + index : "";
            found.put(new ResourceLocation(modid, name(path, name + suffix)), GSON.toJson(translated));
        }
    }

    private static String name(String path, String entry) {
        String file = path.substring(path.lastIndexOf('/') + 1).replace(".json", "");
        return (file + "_" + entry).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "_");
    }

    private static void flatten(JsonObject generator, List<JsonObject> out) {
        String type = type(generator);
        JsonElement nested = generator.get("generators");
        if (!"sequential".equals(type) && !"consecutive".equals(type)) { out.add(generator); return; }
        if (nested == null || !nested.isJsonArray()) { return; }

        for (JsonElement element : nested.getAsJsonArray()) {
            if (element.isJsonObject()) { flatten(element.getAsJsonObject(), out); }
        }
    }

    private static Map<String, String> shapes() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("", ShapeDef.CLUSTER);
        values.put("cluster", ShapeDef.CLUSTER);
        values.put("sparse-cluster", ShapeDef.CLUSTER);
        values.put("large-vein", ShapeDef.LARGEVEIN);
        values.put("plate", ShapeDef.PLATE);
        values.put("geode", ShapeDef.GEODE);
        values.put("decoration", ShapeDef.DECORATION);
        values.put("small-tree", ShapeDef.TREE);
        values.put("lake", ShapeDef.BASIN);
        values.put("spike", ShapeDef.SPIRE);
        values.put("stalagmite", ShapeDef.SPIRE);
        values.put("stalactite", ShapeDef.SPIRE);
        values.put("boulder", ShapeDef.NODULE);
        values.put("spout", ShapeDef.VENT);
        values.put("structure", ShapeDef.IMPRINT);
        return Collections.unmodifiableMap(values);
    }

    private static String type(JsonObject generator) { return JsonUtils.getString(generator, "type", "").trim().toLowerCase(Locale.ROOT); }

    @Nullable private static JsonObject translate(String name, JsonObject source, JsonObject generator) {
        String type = type(generator);
        String shape = SHAPES.get(type);
        if (shape == null) {
            ContentLog.LOGGER.warn("CoFH World entry '{}' uses the '{}' generator, which has nothing to convert to, leaving it out", name, type);
            return null;
        }

        JsonArray weighted = blocks(generator);
        if (weighted == null && ShapeDef.IMPRINT.equals(shape)) {
            weighted = new JsonArray();
            weighted.add(weighted("minecraft:stone", 0, 100, new JsonObject()));
        }
        if (weighted == null) { return null; }

        JsonObject out = new JsonObject();
        JsonObject first = weighted.get(0).getAsJsonObject();
        out.addProperty("block", first.get("block").getAsString());
        out.addProperty("meta", first.get("meta").getAsInt());
        if (weighted.size() > 1 || first.has("properties")) { out.add("blocks", weighted); }

        amount(source, "cluster-count", out, "attempts", 1);
        out.add("replace", material(generator));
        out.addProperty("retrogen", "true".equalsIgnoreCase(JsonUtils.getString(source, "retrogen", "false")));
        if ("sparse-cluster".equals(type) || sparse(generator, type)) { out.addProperty("sparse", true); }

        if (!shaped(name, generator, type, shape, out)) { return null; }

        spread(name, source, out);
        dimensions(source, out);
        biomes(source, out);
        return out;
    }

    private static boolean sparse(JsonObject generator, String type) {
        boolean fallback = "large-vein".equals(type);
        return generator.has("sparse") ? "true".equalsIgnoreCase(JsonUtils.getString(generator, "sparse", "false")) : fallback;
    }

    private static boolean shaped(String name, JsonObject generator, String type, String value, JsonObject out) {
        JsonObject shape = new JsonObject();
        shape.addProperty("type", value);
        if (ShapeDef.PLATE.equals(value)) {
            amount(generator, "radius", shape, "radius", 6);
            amount(generator, "height", shape, "height", 1);
            shape.addProperty("slim", "true".equalsIgnoreCase(JsonUtils.getString(generator, "slim", "false")));
            shape.addProperty("plane", "square".equalsIgnoreCase(JsonUtils.getString(generator, "shape", "circle")) ? ShapeDef.SQUARE : ShapeDef.CIRCLE);
        }
        else if (ShapeDef.GEODE.equals(value)) {
            String crust = single(generator, "crust");
            String filler = single(generator, "filler");
            shape.addProperty("outline", crust.isEmpty() ? "minecraft:stone" : crust);
            if (!filler.isEmpty()) { shape.addProperty("fill", filler); }
        }
        else if (ShapeDef.DECORATION.equals(value)) {
            amount(generator, "cluster-size", out, "size", 8);
            shape.add("surface", names(generator, "surface"));
            shape.addProperty("seeSky", !"false".equalsIgnoreCase(JsonUtils.getString(generator, "see-sky", "true")));
            shape.addProperty("checkStay", !"false".equalsIgnoreCase(JsonUtils.getString(generator, "check-stay", "true")));
            amount(generator, "stack-height", shape, "stackHeight", 1);
            shape.addProperty("scatterX", integer(generator, "x-variance", 8));
            shape.addProperty("scatterY", integer(generator, "y-variance", 4));
            shape.addProperty("scatterZ", integer(generator, "z-variance", 8));
        }
        else if (ShapeDef.TREE.equals(value)) {
            String leaves = single(generator, "leaves");
            if (leaves.isEmpty()) {
                ContentLog.LOGGER.warn("CoFH World entry '{}' grows a tree with no leaves, which cannot be expressed here, leaving it out", name);
                return false;
            }
            shape.addProperty("log", single(generator, "block"));
            shape.addProperty("leaves", leaves);
            shape.add("surface", names(generator, "surface"));
            int least = integer(generator, "min-height", 5);
            JsonObject height = new JsonObject();
            height.addProperty("min", least);
            height.addProperty("max", least + Math.max(0, integer(generator, "height-variance", 0)));
            shape.add("height", height);
        }
        else if (ShapeDef.BASIN.equals(value)) {
            shape.addProperty("radius", 8);
            shape.addProperty("height", 4);
        }
        else if (ShapeDef.SPIRE.equals(value)) {
            int least = integer(generator, "min-height", 4);
            shape.add("height", range(least, least + Math.max(0, integer(generator, "height-variance", 3))));
            int reach = integer(generator, "gen-size", 2);
            shape.add("radius", range(reach, reach + Math.max(0, integer(generator, "size-variance", 1))));
            shape.addProperty("hanging", "stalactite".equals(type));
        }
        else if (ShapeDef.NODULE.equals(value)) {
            int across = Math.max(1, Math.max(1, integer(generator, "diameter", 4)) / 2);
            shape.add("radius", range(across, across + Math.max(0, integer(generator, "size-variance", 1))));
            shape.addProperty("slim", "true".equalsIgnoreCase(JsonUtils.getString(generator, "hollow", "false")));
        }
        else if (ShapeDef.VENT.equals(value)) {
            amount(generator, "height", shape, "height", 4);
            amount(generator, "radius", shape, "radius", 0);
            shape.addProperty("plane", "square".equalsIgnoreCase(JsonUtils.getString(generator, "shape", "circle")) ? ShapeDef.SQUARE : ShapeDef.CIRCLE);
        }
        else if (ShapeDef.IMPRINT.equals(value)) {
            String file = raw(generator);
            if (file.isEmpty()) {
                ContentLog.LOGGER.warn("CoFH World entry '{}' places a structure but names no file, leaving it out", name);
                return false;
            }

            ResourceLocation id = new ResourceLocation(owner, file.toLowerCase(Locale.ROOT).replace(".nbt", "").replaceAll("[^a-z0-9_]", "_"));
            TEMPLATE_JARS.put(id, source);
            TEMPLATE_ENTRIES.put(id, folder + file);
            shape.addProperty("structure", id.toString());
            shape.addProperty("integrity", (int) Math.round(100.0D * doubled(generator)));
        }
        else {
            amount(generator, "cluster-size", out, "size", 8);
            if (ShapeDef.LARGEVEIN.equals(value)) { shape.addProperty("slim", "true".equalsIgnoreCase(JsonUtils.getString(generator, "spindly", "false"))); }
        }

        out.add("shape", shape);
        return true;
    }

    private static double doubled(JsonObject source) {
        JsonElement element = source.get("integrity");
        if (!number(element)) { return 1.0; }

        return element.getAsDouble();
    }

    private static JsonObject range(int least, int most) {
        JsonObject out = new JsonObject();
        out.addProperty("min", least);
        out.addProperty("max", Math.max(least, most));
        return out;
    }

    private static void spread(String name, JsonObject source, JsonObject out) {
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

    private static JsonArray names(JsonObject generator, String key) {
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

    private static String single(JsonObject generator, String key) {
        JsonArray all = names(generator, key);
        return all.size() == 0 ? "" : all.get(0).getAsString();
    }

    private static String raw(JsonObject generator) {
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

    private static void amount(JsonObject source, String key, JsonObject out, String name, int fallback) {
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

    private static int integer(JsonObject source, String key, int fallback) {
        JsonElement element = source.get(key);
        if (!number(element)) { return fallback; }

        return element.getAsInt();
    }

    private static boolean number(@Nullable JsonElement element) { return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber(); }

    @Nullable private static JsonArray blocks(JsonObject generator) {
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
        int meta = properties.entrySet().isEmpty() ? Math.max(0, Math.min(15, integer(one, "data", integer(one, "metadata", 0)))) : 0;
        return weighted(qualified(JsonUtils.getString(one, "name", "")), meta, Math.max(1, Math.min(1000000, integer(one, "weight", 100))), properties);
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

    private static JsonObject weighted(String block, int meta, int weight, JsonObject properties) {
        JsonObject one = new JsonObject();
        one.addProperty("block", block);
        one.addProperty("meta", meta);
        one.addProperty("weight", weight);
        if (!properties.entrySet().isEmpty()) { one.add("properties", properties); }

        return one;
    }

    private static String qualified(String name) { return name.contains(":") ? name : "minecraft:" + name; }

    private static JsonArray material(JsonObject generator) {
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

    private static void dimensions(JsonObject source, JsonObject out) {
        JsonElement dimension = source.get("dimension");
        if (dimension == null || !dimension.isJsonObject()) { return; }

        JsonObject one = dimension.getAsJsonObject();
        JsonArray values = JsonUtils.getJsonArray(one, "value", new JsonArray());
        if (values.size() == 0) { return; }

        out.add("dimensions", values);
        if ("blacklist".equalsIgnoreCase(JsonUtils.getString(one, "restriction", "whitelist"))) { out.addProperty("dimensionsAreBlacklist", true); }
    }

    private static void biomes(JsonObject source, JsonObject out) {
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
