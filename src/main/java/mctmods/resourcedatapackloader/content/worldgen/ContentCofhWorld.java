package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
        JsonArray weighted = ContentCofhFields.blocks(generator);
        if (weighted == null && ShapeDef.IMPRINT.equals(shape)) {
            weighted = new JsonArray();
            weighted.add(ContentCofhFields.weighted("minecraft:stone", 0, 100, new JsonObject()));
        }
        if (weighted == null) { return null; }
        JsonObject out = new JsonObject();
        JsonObject first = weighted.get(0).getAsJsonObject();
        out.addProperty("block", first.get("block").getAsString());
        out.addProperty("meta", first.get("meta").getAsInt());
        if (weighted.size() > 1 || first.has("properties")) { out.add("blocks", weighted); }
        ContentCofhFields.amount(source, "cluster-count", out, "attempts", 1);
        out.add("replace", ShapeDef.IMPRINT.equals(shape) && generator.has("ignored-block") ? ContentCofhFields.ignored(generator) : ContentCofhFields.material(generator));
        out.addProperty("retrogen", "true".equalsIgnoreCase(JsonUtils.getString(source, "retrogen", "false")));
        if ("sparse-cluster".equals(type) || sparse(generator, type)) { out.addProperty("sparse", true); }
        if (!shaped(name, generator, type, shape, out)) { return null; }
        ContentCofhFields.spread(name, source, out);
        ContentCofhFields.dimensions(source, out);
        ContentCofhFields.biomes(source, out);
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
            ContentCofhFields.amount(generator, "radius", shape, "radius", 6);
            ContentCofhFields.amount(generator, "height", shape, "height", 1);
            shape.addProperty("slim", "true".equalsIgnoreCase(JsonUtils.getString(generator, "slim", "false")));
            shape.addProperty("plane", "square".equalsIgnoreCase(JsonUtils.getString(generator, "shape", "circle")) ? ShapeDef.SQUARE : ShapeDef.CIRCLE);
        }
        else if (ShapeDef.GEODE.equals(value)) {
            String crust = ContentCofhFields.single(generator, "crust");
            String filler = ContentCofhFields.single(generator, "filler");
            shape.addProperty("outline", crust.isEmpty() ? "minecraft:stone" : crust);
            if (!filler.isEmpty()) { shape.addProperty("fill", filler); }
        }
        else if (ShapeDef.DECORATION.equals(value)) {
            ContentCofhFields.amount(generator, "cluster-size", out, "size", 8);
            shape.add("surface", ContentCofhFields.names(generator, "surface"));
            shape.addProperty("seeSky", !"false".equalsIgnoreCase(JsonUtils.getString(generator, "see-sky", "true")));
            shape.addProperty("checkStay", !"false".equalsIgnoreCase(JsonUtils.getString(generator, "check-stay", "true")));
            ContentCofhFields.amount(generator, "stack-height", shape, "stackHeight", 1);
            shape.addProperty("scatterX", ContentCofhFields.integer(generator, "x-variance", 8));
            shape.addProperty("scatterY", ContentCofhFields.integer(generator, "y-variance", 4));
            shape.addProperty("scatterZ", ContentCofhFields.integer(generator, "z-variance", 8));
        }
        else if (ShapeDef.TREE.equals(value)) {
            String leaves = ContentCofhFields.single(generator, "leaves");
            if (leaves.isEmpty()) {
                ContentLog.LOGGER.warn("CoFH World entry '{}' grows a tree with no leaves, which cannot be expressed here, leaving it out", name);
                return false;
            }
            shape.addProperty("log", ContentCofhFields.single(generator, "block"));
            shape.addProperty("leaves", leaves);
            shape.add("surface", ContentCofhFields.names(generator, "surface"));
            int least = ContentCofhFields.integer(generator, "min-height", 5);
            JsonObject height = new JsonObject();
            height.addProperty("min", least);
            height.addProperty("max", least + Math.max(0, ContentCofhFields.integer(generator, "height-variance", 0)));
            shape.add("height", height);
        }
        else if (ShapeDef.BASIN.equals(value)) {
            shape.addProperty("radius", 8);
            shape.addProperty("height", 4);
        }
        else if (ShapeDef.SPIRE.equals(value)) {
            int least = ContentCofhFields.integer(generator, "min-height", 4);
            shape.add("height", ContentCofhFields.range(least, least + Math.max(0, ContentCofhFields.integer(generator, "height-variance", 3))));
            int reach = ContentCofhFields.integer(generator, "gen-size", 2);
            shape.add("radius", ContentCofhFields.range(reach, reach + Math.max(0, ContentCofhFields.integer(generator, "size-variance", 1))));
            shape.addProperty("hanging", "stalactite".equals(type));
            shape.addProperty("taper", "false".equalsIgnoreCase(JsonUtils.getString(generator, "fat", "true")) ? ShapeDef.NEEDLE : ShapeDef.BELL);
        }
        else if (ShapeDef.NODULE.equals(value)) {
            int across = Math.max(1, Math.max(1, ContentCofhFields.integer(generator, "diameter", 4)) / 2);
            shape.add("radius", ContentCofhFields.range(across, across + Math.max(0, ContentCofhFields.integer(generator, "size-variance", 1))));
            shape.addProperty("slim", "true".equalsIgnoreCase(JsonUtils.getString(generator, "hollow", "false")));
        }
        else if (ShapeDef.VENT.equals(value)) {
            ContentCofhFields.amount(generator, "height", shape, "height", 4);
            ContentCofhFields.amount(generator, "radius", shape, "radius", 0);
            shape.addProperty("plane", "square".equalsIgnoreCase(JsonUtils.getString(generator, "shape", "circle")) ? ShapeDef.SQUARE : ShapeDef.CIRCLE);
        }
        else if (ShapeDef.IMPRINT.equals(value)) {
            String file = ContentCofhFields.raw(generator);
            if (file.isEmpty()) {
                ContentLog.LOGGER.warn("CoFH World entry '{}' places a structure but names no file, leaving it out", name);
                return false;
            }
            ResourceLocation id = new ResourceLocation(owner, file.toLowerCase(Locale.ROOT).replace(".nbt", "").replaceAll("[^a-z0-9_]", "_"));
            TEMPLATE_JARS.put(id, source);
            TEMPLATE_ENTRIES.put(id, folder + file);
            shape.addProperty("structure", id.toString());
            shape.addProperty("integrity", (int) Math.round(100.0D * ContentCofhFields.doubled(generator)));
            ContentCofhFields.addTurns(shape, generator);
            addStructures(shape, generator, owner, folder, source);
        }
        else {
            ContentCofhFields.amount(generator, "cluster-size", out, "size", 8);
            if (ShapeDef.LARGEVEIN.equals(value)) { shape.addProperty("slim", "true".equalsIgnoreCase(JsonUtils.getString(generator, "spindly", "false"))); }
        }
        out.add("shape", shape);
        return true;
    }

    private static void addStructures(JsonObject shape, JsonObject generator, String owner, String folder, File source) {
        JsonElement held = generator.get("structure");
        if (held == null || !held.isJsonArray() || held.getAsJsonArray().size() < 2) { return; }
        JsonArray out = new JsonArray();
        for (JsonElement element : held.getAsJsonArray()) {
            boolean object = element.isJsonObject();
            String file = object ? JsonUtils.getString(element.getAsJsonObject(), "value", "") : element.getAsString();
            if (file.isEmpty()) { continue; }
            ResourceLocation id = new ResourceLocation(owner, file.toLowerCase(Locale.ROOT).replace(".nbt", "").replaceAll("[^a-z0-9_]", "_"));
            TEMPLATE_JARS.put(id, source);
            TEMPLATE_ENTRIES.put(id, folder + file);
            JsonObject pick = new JsonObject();
            pick.addProperty("structure", id.toString());
            if (object && element.getAsJsonObject().has("weight")) { pick.addProperty("weight", JsonUtils.getInt(element.getAsJsonObject(), "weight", 1)); }
            out.add(pick);
        }
        if (out.size() > 1) { shape.add("structures", out); }
    }
}
