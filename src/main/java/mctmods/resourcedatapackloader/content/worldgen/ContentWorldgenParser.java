package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.BlockWeightDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.def.SpreadDef;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentWorldgenParser {
    private static final Gson GSON = new Gson();
    private static final Set<String> KNOWN_SHAPES = Set.of(ShapeDef.CLUSTER, ShapeDef.PLATE, ShapeDef.GEODE, ShapeDef.LARGEVEIN, ShapeDef.DECORATION,
            ShapeDef.TREE, ShapeDef.VINES, ShapeDef.BASIN, ShapeDef.SPIRE, ShapeDef.NODULE, ShapeDef.VENT, ShapeDef.IMPRINT, ShapeDef.BELT, ShapeDef.FIELD);
    private static final Set<String> KNOWN_SPREADS = Set.of(SpreadDef.EVEN, SpreadDef.CENTERED, SpreadDef.SPRAWL, SpreadDef.TERRAIN, SpreadDef.CAVERN, SpreadDef.SUBMERGED);

    private ContentWorldgenParser() {}

    @Nullable public static WorldgenDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Worldgen definition {} is empty, ignoring it", key);
            return null;
        }
        ResourceLocation block = ContentParser.location(GsonHelper.getAsString(json, "block", ""));
        if (block == null) {
            ContentLog.LOGGER.error("Worldgen definition {} names no block, ignoring it", key);
            return null;
        }
        if (json.has("meta")) { ContentLog.LOGGER.warn("Worldgen {} sets 'meta', which this line does not read. Name the state under 'properties' in the 'blocks' list instead", key); }
        int minHeight = GsonHelper.getAsInt(json, "minHeight", 0);
        int maxHeight = GsonHelper.getAsInt(json, "maxHeight", 64);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("Worldgen definition {} has maxHeight below minHeight, swapping them", key);
            int swap = minHeight;
            minHeight = maxHeight;
            maxHeight = swap;
        }
        List<ResourceLocation> regions = new ArrayList<>();
        for (String name : Json.strings(json, "caveRegions")) {
            ResourceLocation region = name.indexOf(':') >= 0 ? ResourceLocation.tryParse(name) : ResourceLocation.tryBuild(key.getNamespace(), name);
            if (region != null) { regions.add(region); }
        }
        String snap = GsonHelper.getAsString(json, "snap", "").trim().toLowerCase(Locale.ROOT);
        if (!snap.isEmpty() && !WorldgenDef.FLOOR.equals(snap) && !WorldgenDef.CEILING.equals(snap)) {
            ContentLog.LOGGER.error("Worldgen {} asks for snap '{}', which is not floor or ceiling, so it does not snap", key, snap);
            snap = "";
        }
        int depth = GsonHelper.getAsInt(json, "snapDepth", 0);
        if (depth < 0) {
            ContentLog.LOGGER.error("Worldgen {} asks for snapDepth {}, which is below zero, so it stays at the surface", key, depth);
            depth = 0;
        }
        return new WorldgenDef(key, block, weights(key, json),
                ContentParser.amount(json, "size", 8, 1),
                ContentParser.amount(json, "attempts", 8, 0),
                minHeight, maxHeight,
                matches(key, json, "replace"), matches(key, json, "adjacent"),
                json.has("replace"),
                GsonHelper.getAsBoolean(json, "sparse", false),
                Json.strings(json, "dimensions"),
                GsonHelper.getAsBoolean(json, "dimensionsAreBlacklist", false),
                Json.strings(json, "biomes"), Json.strings(json, "biomeTypes"),
                GsonHelper.getAsBoolean(json, "biomesAreBlacklist", false),
                GsonHelper.getAsFloat(json, "minTemperature", -100.0F),
                GsonHelper.getAsFloat(json, "maxTemperature", 100.0F),
                GsonHelper.getAsFloat(json, "minRainfall", -100.0F),
                GsonHelper.getAsFloat(json, "maxRainfall", 100.0F),
                Math.max(0, GsonHelper.getAsInt(json, "minDistanceFromSpawn", 0)),
                Json.strings(json, "requires"),
                GsonHelper.getAsBoolean(json, "retrogen", false),
                GsonHelper.getAsString(json, "retrogenKey", "").trim(),
                List.copyOf(regions), snap, snap.isEmpty() ? 0 : depth,
                spread(key, json, minHeight, maxHeight), shape(key, json));
    }

    private static List<BlockWeightDef> weights(ResourceLocation key, JsonObject json) {
        if (!json.has("blocks")) { return List.of(); }
        List<BlockWeightDef> values = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "blocks")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A blocks entry in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            if (entry.has("meta")) { ContentLog.LOGGER.warn("A blocks entry in {} sets 'meta', which this line does not read. Name the state under 'properties' instead", key); }
            ResourceLocation block = ContentParser.location(GsonHelper.getAsString(entry, "block", ""));
            if (block == null) {
                ContentLog.LOGGER.error("A blocks entry in {} names no block, skipping it", key);
                continue;
            }
            values.add(new BlockWeightDef(block, Math.max(1, GsonHelper.getAsInt(entry, "weight", 1)), Json.map(entry, "properties")));
        }
        return List.copyOf(values);
    }

    private static List<BlockMatchDef> matches(ResourceLocation key, JsonObject json, String member) {
        boolean replacing = "replace".equals(member);
        if (!json.has(member)) { return replacing ? stone(key) : List.of(); }
        JsonElement element = json.get(member);
        List<BlockMatchDef> values = new ArrayList<>();
        if (!element.isJsonArray()) {
            BlockMatchDef one = ContentParser.match(key, element);
            if (one != null) { values.add(one); }
        }
        else {
            for (JsonElement name : element.getAsJsonArray()) {
                BlockMatchDef match = ContentParser.match(key, name);
                if (match != null) { values.add(match); }
            }
        }
        if (values.isEmpty() && replacing) { return stone(key); }
        return List.copyOf(values);
    }

    private static List<BlockMatchDef> stone(ResourceLocation key) {
        BlockMatchDef match = ContentParser.match(key, new JsonPrimitive("minecraft:stone"));
        return match == null ? List.of() : List.of(match);
    }

    private static SpreadDef spread(ResourceLocation key, JsonObject json, int minHeight, int maxHeight) {
        if (!json.has("spread")) { return SpreadDef.even(); }
        JsonObject entry = GsonHelper.getAsJsonObject(json, "spread");
        String type = GsonHelper.getAsString(entry, "type", SpreadDef.EVEN).trim().toLowerCase(Locale.ROOT);
        if (!KNOWN_SPREADS.contains(type)) {
            ContentLog.LOGGER.error("Worldgen {} asks for spread type '{}', which is not one of {}, using {}", key, type, KNOWN_SPREADS, SpreadDef.EVEN);
            type = SpreadDef.EVEN;
        }
        int offsetMin = GsonHelper.getAsInt(entry, "offsetMin", 0);
        return new SpreadDef(type,
                GsonHelper.getAsInt(entry, "center", (minHeight + maxHeight) / 2),
                Math.max(1, GsonHelper.getAsInt(entry, "range", Math.max(2, (maxHeight - minHeight) / 2))),
                Mth.clamp(GsonHelper.getAsInt(entry, "smoothness", 2), 1, 8),
                Math.max(1, GsonHelper.getAsInt(entry, "veinHeight", Math.max(1, maxHeight - minHeight))),
                Math.max(1, GsonHelper.getAsInt(entry, "veinDiameter", 12)),
                Mth.clamp(GsonHelper.getAsInt(entry, "verticalDensity", 16), 1, 100),
                Mth.clamp(GsonHelper.getAsInt(entry, "horizontalDensity", 32), 1, 100),
                offsetMin, Math.max(offsetMin, GsonHelper.getAsInt(entry, "offsetMax", offsetMin)),
                GsonHelper.getAsBoolean(entry, "ceiling", false));
    }

    private static ShapeDef shape(ResourceLocation key, JsonObject json) {
        if (!json.has("shape")) { return ShapeDef.cluster(); }
        JsonObject entry = GsonHelper.getAsJsonObject(json, "shape");
        String type = GsonHelper.getAsString(entry, "type", ShapeDef.CLUSTER).trim().toLowerCase(Locale.ROOT);
        if (!KNOWN_SHAPES.contains(type)) {
            ContentLog.LOGGER.error("Worldgen {} asks for shape '{}', which is not one of {}, using {}", key, type, KNOWN_SHAPES, ShapeDef.CLUSTER);
            type = ShapeDef.CLUSTER;
        }
        String plane = GsonHelper.getAsString(entry, "plane", ShapeDef.CIRCLE).trim().toLowerCase(Locale.ROOT);
        if (!ShapeDef.CIRCLE.equals(plane) && !ShapeDef.SQUARE.equals(plane)) {
            ContentLog.LOGGER.error("Worldgen {} asks for plane '{}', which is not {} or {}, using {}", key, plane, ShapeDef.CIRCLE, ShapeDef.SQUARE, ShapeDef.CIRCLE);
            plane = ShapeDef.CIRCLE;
        }
        return new ShapeDef(type,
                ContentParser.amount(entry, "radius", ShapeDef.BELT.equals(type) ? 32 : 6, 0),
                ContentParser.amount(entry, "height", ShapeDef.GEODE.equals(type) ? 8 : ShapeDef.TREE.equals(type) ? 5 : 1, 0),
                ContentParser.amount(entry, "width", 12, 3),
                plane,
                GsonHelper.getAsBoolean(entry, "slim", false),
                GsonHelper.getAsString(entry, "outline", "").trim(),
                GsonHelper.getAsString(entry, "fill", "").trim(),
                surface(entry),
                ContentParser.amount(entry, "stackHeight", 1, 1),
                GsonHelper.getAsBoolean(entry, "seeSky", true),
                GsonHelper.getAsBoolean(entry, "checkStay", true),
                GsonHelper.getAsInt(entry, "scatterX", 8),
                GsonHelper.getAsInt(entry, "scatterY", 4),
                GsonHelper.getAsInt(entry, "scatterZ", 8),
                GsonHelper.getAsString(entry, "log", "").trim(),
                GsonHelper.getAsString(entry, "leaves", "").trim(),
                GsonHelper.getAsBoolean(entry, "hanging", false),
                GsonHelper.getAsString(entry, "structure", "").trim(),
                picks(entry, "structures", "structure"), picks(entry, "turns", "turn"), picks(entry, "mirrors", "mirror"),
                taper(key, entry),
                Mth.clamp(GsonHelper.getAsInt(entry, "integrity", 100), 1, 100),
                Math.max(0, GsonHelper.getAsInt(entry, "rarity", 0)),
                GsonHelper.getAsBoolean(entry, "rarityIsPerChunk", false),
                ShapeDef.FIELD.equals(type) ? ContentHardness.fieldFrom(GsonHelper.getAsJsonObject(entry, "field", new JsonObject())) : null,
                GsonHelper.getAsFloat(entry, "threshold", 0.5F),
                Math.max(0, GsonHelper.getAsInt(entry, "fade", 0)),
                GsonHelper.getAsString(entry, "lootTable", "").trim(),
                GsonHelper.getAsString(entry, "locateAs", "").trim(),
                pinned(key, entry));
    }

    @Nullable private static int[] pinned(ResourceLocation key, JsonObject entry) {
        if (!entry.has("at")) { return null; }
        JsonArray at = GsonHelper.getAsJsonArray(entry, "at");
        if (at.size() == 2) { return new int[] { at.get(0).getAsInt(), at.get(1).getAsInt() }; }
        ContentLog.LOGGER.error("Worldgen {} pins its imprint with 'at', which needs exactly [x, z], so it places by chance instead", key);
        return null;
    }

    private static List<String> surface(JsonObject entry) {
        if (!entry.has("surface")) { return List.of(); }
        JsonElement element = entry.get("surface");
        if (!element.isJsonArray()) { return List.of(element.getAsString()); }
        return Json.strings(entry, "surface");
    }

    private static List<PickDef> picks(JsonObject entry, String listKey, String nameKey) {
        if (!entry.has(listKey)) { return List.of(); }
        List<PickDef> picked = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(entry, listKey)) {
            if (element.isJsonPrimitive()) { picked.add(new PickDef(element.getAsString().trim().toLowerCase(Locale.ROOT), 1)); }
            else if (element.isJsonObject()) {
                JsonObject held = element.getAsJsonObject();
                picked.add(new PickDef(GsonHelper.getAsString(held, nameKey, "").trim().toLowerCase(Locale.ROOT), Math.max(1, GsonHelper.getAsInt(held, "weight", 1))));
            }
        }
        return List.copyOf(picked);
    }

    private static String taper(ResourceLocation key, JsonObject entry) {
        String named = GsonHelper.getAsString(entry, "taper", ShapeDef.STRAIGHT).trim().toLowerCase(Locale.ROOT);
        if (ShapeDef.STRAIGHT.equals(named) || ShapeDef.BELL.equals(named) || ShapeDef.NEEDLE.equals(named)) { return named; }
        ContentLog.LOGGER.error("Worldgen {} asks for taper '{}', which is not {}, {} or {}, using {}", key, named, ShapeDef.STRAIGHT, ShapeDef.BELL, ShapeDef.NEEDLE, ShapeDef.STRAIGHT);
        return ShapeDef.STRAIGHT;
    }
}
