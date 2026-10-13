package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.BlockWeightDef;
import mctmods.resourcedatapackloader.content.def.FollowDef;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.def.SpreadDef;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentWorldgenParser {
    private static final Set<String> KNOWN_SHAPES = Set.of(ShapeDef.CLUSTER, ShapeDef.PLATE, ShapeDef.GEODE, ShapeDef.LARGEVEIN, ShapeDef.DECORATION,
            ShapeDef.TREE, ShapeDef.VINES, ShapeDef.BASIN, ShapeDef.SPIRE, ShapeDef.NODULE, ShapeDef.VENT, ShapeDef.IMPRINT, ShapeDef.BELT, ShapeDef.FIELD, ShapeDef.VEIN, ShapeDef.SPRING);
    private static final Set<String> KNOWN_SPREADS = Set.of(SpreadDef.EVEN, SpreadDef.CENTERED, SpreadDef.SPRAWL, SpreadDef.TERRAIN, SpreadDef.CAVERN, SpreadDef.SUBMERGED);

    private static final List<String> SHAPE_ONLY_KEYS = List.of("rarity", "rarityIsPerChunk", "pattern", "threshold", "density", "taper", "rich", "poor");

    private ContentWorldgenParser() {}

    @Nullable public static WorldgenDef parse(Identifier key, String contents) {
        JsonObject json = Json.object(key, contents, "worldgen definition");
        if (json == null) { return null; }
        Identifier block = ContentParser.location(Json.getString(json, "block", ""));
        if (block == null) {
            ContentLog.LOGGER.error("Worldgen definition {} names no block, ignoring it", key);
            return null;
        }
        if (json.has("meta")) { ContentLog.LOGGER.warn("Worldgen {} sets 'meta', which this line does not read. Name the state under 'properties' in the 'blocks' list instead", key); }
        int minHeight = Json.getInt(json, "minHeight", 0);
        int maxHeight = Json.getInt(json, "maxHeight", 64);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("Worldgen definition {} has maxHeight below minHeight, swapping them", key);
            int swap = minHeight;
            minHeight = maxHeight;
            maxHeight = swap;
        }
        List<Identifier> regions = new ArrayList<>();
        for (String name : Json.strings(json, "caveRegions")) {
            Identifier region = name.indexOf(':') >= 0 ? Identifier.tryParse(name) : Identifier.tryBuild(key.getNamespace(), name);
            if (region != null) { regions.add(region); }
        }
        String snap = Settings.lower(Json.getString(json, "snap", ""));
        if (!snap.isEmpty() && !WorldgenDef.FLOOR.equals(snap) && !WorldgenDef.CEILING.equals(snap)) {
            ContentLog.LOGGER.error("Worldgen {} asks for snap '{}', which is not floor or ceiling, so it does not snap", key, snap);
            snap = "";
        }
        int depth = Json.getInt(json, "snapDepth", 0);
        if (depth < 0) {
            ContentLog.LOGGER.error("Worldgen {} asks for snapDepth {}, which is below zero, so it stays at the surface", key, depth);
            depth = 0;
        }
        for (String misplaced : SHAPE_ONLY_KEYS) {
            if (json.has(misplaced)) { ContentLog.LOGGER.error("Worldgen {} writes '{}' outside its shape object, where it is ignored", key, misplaced); }
        }
        return new WorldgenDef(key, block, weights(key, json),
                ContentParser.amount(json, "size", 8, 1),
                ContentParser.amount(json, "attempts", 8, 0),
                minHeight, maxHeight,
                matches(key, json, "replace"), matches(key, json, "adjacent"),
                json.has("replace"),
                Json.getBoolean(json, "sparse", false),
                Json.strings(json, "dimensions"),
                Json.getBoolean(json, "dimensionsAreBlacklist", false),
                Json.strings(json, "biomes"), Json.strings(json, "biomeTypes"),
                Json.getBoolean(json, "biomesAreBlacklist", false),
                Json.getFloat(json, "minTemperature", -WorldgenDef.NO_LIMIT),
                Json.getFloat(json, "maxTemperature", WorldgenDef.NO_LIMIT),
                Json.getFloat(json, "minRainfall", -WorldgenDef.NO_LIMIT),
                Json.getFloat(json, "maxRainfall", WorldgenDef.NO_LIMIT),
                Math.max(0, Json.getInt(json, "minDistanceFromSpawn", 0)),
                Json.strings(json, "requires"),
                Json.getBoolean(json, "retrogen", false),
                Json.getString(json, "retrogenKey", "").trim(),
                List.copyOf(regions), snap, snap.isEmpty() ? 0 : depth,
                spread(key, json, minHeight, maxHeight), shape(key, json),
                indicators(key, json), ContentParser.amount(json, "indicatorCount", 1, 0), Math.max(0, Json.getInt(json, "indicatorSpread", 0)),
                followers(key, json), ContentParser.amount(json, "thenCount", 1, 0),
                json.has("thenSpread") ? Math.max(0, Json.getInt(json, "thenSpread", 0)) : -1,
                ContentParser.amount(json, "thenDepth", 0, Integer.MIN_VALUE),
                Json.getString(json, "prospectAs", "").trim());
    }

    private static List<PickDef> indicators(Identifier key, JsonObject json) {
        List<PickDef> picked = new ArrayList<>();
        for (String entry : Json.strings(json, "indicators")) {
            int at = entry.indexOf('=', Math.max(0, entry.indexOf(']')));
            String name = at < 0 ? "" : Settings.lower(entry.substring(0, at));
            String said = at < 0 ? "" : entry.substring(at + 1).trim();
            int comma = said.indexOf(',');
            if (comma >= 0) { said = said.substring(0, comma).trim(); }
            if (name.isEmpty() || said.isEmpty()) {
                ContentLog.LOGGER.error("Worldgen definition {} has an indicators entry '{}', which is not written as name=weight, ignoring it", key, entry);
                continue;
            }
            int weight;
            try { weight = Integer.parseInt(said); }
            catch (NumberFormatException bad) {
                ContentLog.LOGGER.error("Worldgen definition {} has an indicators entry '{}' giving a weight of '{}', which is not a whole number, ignoring the entry", key, entry, said);
                continue;
            }
            if (weight < 1) {
                ContentLog.LOGGER.error("Worldgen definition {} has an indicators entry '{}' asking for a weight of {}, which is below 1, ignoring the entry", key, entry, weight);
                continue;
            }
            picked.add(new PickDef(name, weight));
        }
        return List.copyOf(picked);
    }

    private static List<FollowDef> followers(Identifier key, JsonObject json) {
        if (!GsonHelper.isArrayNode(json, "then")) { return List.of(); }
        List<FollowDef> followers = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray("then")) {
            String name;
            int weight;
            int spread = -1;
            AmountDef depth = null;
            if (element.isJsonObject()) {
                JsonObject follow = element.getAsJsonObject();
                name = Json.getString(follow, "name", "");
                weight = Json.getInt(follow, "weight", 1);
                spread = follow.has("spread") ? Math.max(0, Json.getInt(follow, "spread", 0)) : -1;
                depth = follow.has("depth") ? ContentParser.amount(follow, "depth", 0, Integer.MIN_VALUE) : null;
            }
            else if (element.isJsonPrimitive()) {
                String entry = element.getAsString();
                Settings.Weighted split = Settings.weighted(entry, 1);
                name = split.name();
                weight = split.weight();
                if (split.unreadable()) { ContentLog.LOGGER.error("Worldgen definition {} has a then entry '{}' whose weight is not a number, reading it as 1", key, entry); }
            }
            else {
                ContentLog.LOGGER.error("Worldgen definition {} has a then entry that is neither a name nor an object, ignoring it", key);
                continue;
            }
            name = name.trim();
            if (name.isEmpty()) {
                ContentLog.LOGGER.error("Worldgen definition {} has a then entry with no name, ignoring it", key);
                continue;
            }
            if (!FollowDef.EMPTY.equals(name) && name.indexOf(':') < 0) { name = key.getNamespace() + ":" + name; }
            followers.add(new FollowDef(name, weight, spread, depth));
        }
        return List.copyOf(followers);
    }

    private static List<BlockWeightDef> weights(Identifier key, JsonObject json) {
        if (!json.has("blocks")) { return List.of(); }
        List<BlockWeightDef> values = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(json, "blocks")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A blocks entry in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            if (entry.has("meta")) { ContentLog.LOGGER.warn("A blocks entry in {} sets 'meta', which this line does not read. Name the state under 'properties' instead", key); }
            Identifier block = ContentParser.location(Json.getString(entry, "block", ""));
            if (block == null) {
                ContentLog.LOGGER.error("A blocks entry in {} names no block, skipping it", key);
                continue;
            }
            values.add(new BlockWeightDef(block, Math.max(1, Json.getInt(entry, "weight", 1)), Json.map(entry, "properties")));
        }
        return List.copyOf(values);
    }

    private static List<BlockMatchDef> matches(Identifier key, JsonObject json, String member) {
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

    private static List<BlockMatchDef> stone(Identifier key) {
        BlockMatchDef match = ContentParser.match(key, new JsonPrimitive("minecraft:stone"));
        return match == null ? List.of() : List.of(match);
    }

    private static SpreadDef spread(Identifier key, JsonObject json, int minHeight, int maxHeight) {
        if (!json.has("spread")) { return SpreadDef.even(); }
        JsonObject entry = Json.getJsonObject(json, "spread");
        String type = choice(key, entry, "type", "spread type", SpreadDef.EVEN, KNOWN_SPREADS);
        int offsetMin = Json.getInt(entry, "offsetMin", 0);
        return new SpreadDef(type,
                Json.getInt(entry, "center", (minHeight + maxHeight) / 2),
                Math.max(1, Json.getInt(entry, "range", Math.max(2, (maxHeight - minHeight) / 2))),
                Mth.clamp(Json.getInt(entry, "smoothness", 2), 1, 8),
                Math.max(1, Json.getInt(entry, "veinHeight", Math.max(1, maxHeight - minHeight))),
                Math.max(1, Json.getInt(entry, "veinDiameter", 12)),
                Mth.clamp(Json.getInt(entry, "verticalDensity", 16), 1, 100),
                Mth.clamp(Json.getInt(entry, "horizontalDensity", 32), 1, 100),
                offsetMin, Math.max(offsetMin, Json.getInt(entry, "offsetMax", offsetMin)),
                Json.getBoolean(entry, "ceiling", false));
    }

    private static ShapeDef shape(Identifier key, JsonObject json) {
        if (!json.has("shape")) { return ShapeDef.cluster(); }
        JsonObject entry = Json.getJsonObject(json, "shape");
        String type = choice(key, entry, "type", "shape", ShapeDef.CLUSTER, KNOWN_SHAPES);
        String plane = choice(key, entry, "plane", "plane", ShapeDef.CIRCLE, List.of(ShapeDef.CIRCLE, ShapeDef.SQUARE));
        float richAt = Mth.clamp(Json.getFloat(entry, "richAt", 0.88F), 0.0F, 1.0F);
        return new ShapeDef(type,
                ContentParser.amount(entry, "radius", ShapeDef.BELT.equals(type) ? 32 : 6, 0),
                ContentParser.amount(entry, "height", ShapeDef.GEODE.equals(type) ? 8 : ShapeDef.TREE.equals(type) ? 5 : 1, 0),
                ContentParser.amount(entry, "width", 12, 3),
                plane,
                Json.getBoolean(entry, "slim", false),
                Json.getString(entry, "outline", "").trim(),
                Json.getString(entry, "fill", "").trim(),
                surface(entry),
                ContentParser.amount(entry, "stackHeight", 1, 1),
                Json.getBoolean(entry, "seeSky", true),
                Json.getBoolean(entry, "checkStay", true),
                Json.getInt(entry, "scatterX", 8),
                Json.getInt(entry, "scatterY", 4),
                Json.getInt(entry, "scatterZ", 8),
                Json.getString(entry, "log", "").trim(),
                Json.getString(entry, "leaves", "").trim(),
                Json.getBoolean(entry, "hanging", false),
                Json.getString(entry, "structure", "").trim(),
                picks(entry, "structures", "structure"), picks(entry, "turns", "turn"), picks(entry, "mirrors", "mirror"),
                choice(key, entry, "taper", "taper", ShapeDef.STRAIGHT, List.of(ShapeDef.STRAIGHT, ShapeDef.BELL, ShapeDef.NEEDLE)),
                Mth.clamp(Json.getInt(entry, "integrity", ShapeDef.FULL_INTEGRITY), 1, ShapeDef.FULL_INTEGRITY),
                Math.max(0, Json.getInt(entry, "rarity", 0)),
                Json.getBoolean(entry, "rarityIsPerChunk", false),
                ShapeDef.FIELD.equals(type) ? ContentHardness.fieldFrom(Json.getJsonObject(entry, "field", new JsonObject())) : null,
                Json.getFloat(entry, "threshold", ShapeDef.VEIN.equals(type) ? 0.4F : 0.5F),
                Math.max(0, Json.getInt(entry, "fade", 0)),
                Json.getString(entry, "lootTable", "").trim(),
                Json.getString(entry, "locateAs", "").trim(),
                pinned(key, entry, type),
                choice(key, entry, "pattern", "vein pattern", ShapeDef.DEFAULT, List.of(ShapeDef.DEFAULT, ShapeDef.BANDED, ShapeDef.TUBE)),
                Mth.clamp(Json.getFloat(entry, "density", 1.0F), 0.0F, 1.0F),
                Json.getString(entry, "rich", "").trim(),
                Json.getString(entry, "poor", "").trim(),
                richAt,
                Mth.clamp(Json.getFloat(entry, "poorAt", 0.4F), 0.0F, richAt),
                Json.getString(entry, "middle", "").trim(),
                Json.getString(entry, "budding", "").trim(),
                Mth.clamp(Json.getFloat(entry, "buddingChance", 0.083F), 0.0F, 1.0F),
                Json.getString(entry, "crystal", "").trim(),
                Mth.clamp(Json.getFloat(entry, "crystalChance", 0.35F), 0.0F, 1.0F),
                Mth.clamp(Json.getFloat(entry, "crack", 0.0F), 0.0F, 1.0F));
    }

    private static String choice(Identifier key, JsonObject entry, String member, String what, String fallback, Collection<String> allowed) {
        String named = Settings.lower(Json.getString(entry, member, fallback));
        if (allowed.contains(named)) { return named; }
        ContentLog.LOGGER.error("Worldgen {} asks for {} '{}', which is not one of {}, using {}", key, what, named, allowed, fallback);
        return fallback;
    }

    @Nullable private static int[] pinned(Identifier key, JsonObject entry, String type) {
        if (!entry.has("at")) { return null; }
        JsonArray at = Json.getJsonArray(entry, "at");
        if (at.size() == 2) { return ShapeDef.IMPRINT.equals(type) ? new int[] { Json.asInt(at.get(0), "at", 0), Json.asInt(at.get(1), "at", 0) } : null; }
        ContentLog.LOGGER.error("Worldgen {} pins its imprint with 'at', which needs exactly [x, z], so it places by chance instead", key);
        return null;
    }

    private static List<String> surface(JsonObject entry) {
        if (!entry.has("surface")) { return List.of(); }
        JsonElement element = entry.get("surface");
        if (element.isJsonPrimitive()) { return List.of(element.getAsString()); }
        if (!element.isJsonArray()) { return List.of(); }
        return Json.strings(entry, "surface");
    }

    public static List<PickDef> picks(JsonObject entry, String listKey, String nameKey) { return picks(entry, listKey, nameKey, 1); }

    public static List<PickDef> picks(JsonObject entry, String listKey, String nameKey, int weight) {
        if (!entry.has(listKey)) { return List.of(); }
        List<PickDef> picked = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(entry, listKey)) {
            if (element.isJsonPrimitive()) { picked.add(new PickDef(Settings.lower(element.getAsString()), weight)); }
            else if (element.isJsonObject()) {
                JsonObject held = element.getAsJsonObject();
                picked.add(new PickDef(Settings.lower(Json.getString(held, nameKey, "")), Math.max(1, Json.getInt(held, "weight", weight))));
            }
        }
        return List.copyOf(picked);
    }
}
