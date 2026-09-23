package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.WeightedPicks;
import mctmods.resourcedatapackloader.util.Json;
import static mctmods.resourcedatapackloader.util.Json.strings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentParserWorldgen {
    private static final Set<String> KNOWN_SPREADS = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            SpreadDef.EVEN, SpreadDef.CENTERED, SpreadDef.SPRAWL, SpreadDef.TERRAIN, SpreadDef.CAVERN, SpreadDef.SUBMERGED)));
    private static final Set<String> KNOWN_SHAPES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            ShapeDef.CLUSTER, ShapeDef.PLATE, ShapeDef.GEODE, ShapeDef.LARGEVEIN, ShapeDef.DECORATION, ShapeDef.TREE, ShapeDef.VINES,
            ShapeDef.BASIN, ShapeDef.SPIRE, ShapeDef.NODULE, ShapeDef.VENT, ShapeDef.IMPRINT, ShapeDef.BELT, ShapeDef.FIELD, ShapeDef.VEIN, ShapeDef.SPRING)));

    private ContentParserWorldgen() {}

    @Nullable public static WorldgenDef worldgen(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Worldgen definition {} is empty, ignoring it", key);
            return null;
        }
        String block = JsonUtils.getString(json, "block", "");
        if (block.isEmpty()) {
            ContentLog.LOGGER.error("Worldgen definition {} has no block, ignoring it", key);
            return null;
        }
        int minHeight = JsonUtils.getInt(json, "minHeight", 0);
        int maxHeight = JsonUtils.getInt(json, "maxHeight", 64);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("Worldgen definition {} has maxHeight below minHeight, swapping them", key);
            int swap = minHeight;
            minHeight = maxHeight;
            maxHeight = swap;
        }
        minHeight = Math.max(-Config.worldgen.rubicHeightLimit, minHeight);
        maxHeight = Math.max(minHeight, maxHeight);
        WorldgenDef made = new WorldgenDef(key, new ResourceLocation(block),
                JsonUtils.getInt(json, "meta", 0),
                weights(json),
                ContentParser.amount(json, "size", 8, 1),
                ContentParser.amount(json, "attempts", 8, 0),
                minHeight, maxHeight,
                replaces(key, json),
                adjacent(key, json),
                JsonUtils.getBoolean(json, "sparse", false),
                integers(json),
                JsonUtils.getBoolean(json, "dimensionsAreBlacklist", false),
                strings(json, "biomes"), strings(json, "biomeTypes"),
                JsonUtils.getBoolean(json, "biomesAreBlacklist", false),
                strings(json, "requires"),
                JsonUtils.getBoolean(json, "retrogen", false),
                JsonUtils.getString(json, "retrogenKey", ""),
                Math.max(0, JsonUtils.getInt(json, "minDistanceFromSpawn", 0)),
                spread(key, json, minHeight, maxHeight),
                shape(key, json),
                JsonUtils.getFloat(json, "minTemperature", -100.0F),
                JsonUtils.getFloat(json, "maxTemperature", 100.0F),
                JsonUtils.getFloat(json, "minRainfall", -100.0F),
                JsonUtils.getFloat(json, "maxRainfall", 100.0F),
                json.has("replace"));
        List<ResourceLocation> regions = new ArrayList<>();
        for (String name : strings(json, "caveRegions")) {
            regions.add(name.indexOf(':') >= 0 ? new ResourceLocation(name) : new ResourceLocation(key.getNamespace(), name));
        }
        if (!regions.isEmpty()) { made.caveRegions = regions; }
        made.indicators.load(strings(json, "indicators").toArray(new String[0]));
        made.indicatorCount = ContentParser.amount(json, "indicatorCount", 1, 0);
        made.indicatorSpread = Math.max(0, JsonUtils.getInt(json, "indicatorSpread", 0));
        List<FollowDef> followers = new ArrayList<>();
        if (json.has("then") && json.get("then").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("then")) {
                String name;
                int weight = 1;
                int spread = -1;
                AmountDef depth = null;
                if (element.isJsonObject()) {
                    JsonObject follow = element.getAsJsonObject();
                    name = JsonUtils.getString(follow, "name", "");
                    weight = JsonUtils.getInt(follow, "weight", 1);
                    spread = follow.has("spread") ? Math.max(0, JsonUtils.getInt(follow, "spread", 0)) : -1;
                    depth = follow.has("depth") ? ContentParser.amount(follow, "depth", 0, Integer.MIN_VALUE) : null;
                }
                else {
                    String entry = element.getAsString();
                    int at = entry.indexOf('=');
                    name = at < 0 ? entry : entry.substring(0, at);
                    if (at >= 0) {
                        try { weight = Integer.parseInt(entry.substring(at + 1).trim()); }
                        catch (NumberFormatException bad) { ContentLog.LOGGER.error("Worldgen definition {} has a then entry '{}' whose weight is not a number, reading it as 1", key, entry); }
                    }
                }
                name = name.trim();
                if (name.isEmpty()) {
                    ContentLog.LOGGER.error("Worldgen definition {} has a then entry with no name, ignoring it", key);
                    continue;
                }
                if (!WeightedPicks.EMPTY.equals(name) && name.indexOf(':') < 0) { name = key.getNamespace() + ":" + name; }
                followers.add(new FollowDef(name, weight, spread, depth));
            }
        }
        made.then = Collections.unmodifiableList(followers);
        made.thenCount = ContentParser.amount(json, "thenCount", 1, 0);
        made.thenSpread = json.has("thenSpread") ? Math.max(0, JsonUtils.getInt(json, "thenSpread", 0)) : -1;
        made.thenDepth = ContentParser.amount(json, "thenDepth", 0, Integer.MIN_VALUE);
        made.prospectAs = JsonUtils.getString(json, "prospectAs", "").trim();
        String snap = JsonUtils.getString(json, "snap", "").trim().toLowerCase(Locale.ROOT);
        if (!snap.isEmpty() && !"floor".equals(snap) && !"ceiling".equals(snap)) {
            ContentLog.LOGGER.error("Worldgen {} asks for snap '{}', which is not floor or ceiling, so it does not snap", key, snap);
            snap = "";
        }
        made.snap = snap;
        int depth = JsonUtils.getInt(json, "snapDepth", 0);
        if (depth < 0) {
            ContentLog.LOGGER.error("Worldgen {} asks for snapDepth {}, which is below zero, so it stays at the surface", key, depth);
            depth = 0;
        }
        made.snapDepth = snap.isEmpty() ? 0 : depth;
        return made;
    }

    @Nullable public static CaveRegionDef caveRegion(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Cave region definition {} is empty, ignoring it", key);
            return null;
        }
        int minHeight = JsonUtils.getInt(json, "minHeight", -Config.worldgen.rubicHeightLimit);
        int maxHeight = JsonUtils.getInt(json, "maxHeight", 48);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("Cave region definition {} has maxHeight below minHeight, swapping them", key);
            int swap = minHeight;
            minHeight = maxHeight;
            maxHeight = swap;
        }
        int waterLevel = CaveRegionDef.NO_WATER;
        if (json.has("waterLevel")) { waterLevel = JsonUtils.getInt(json, "waterLevel", CaveRegionDef.NO_WATER); }
        List<SpawnEntryDef> spawns = new ArrayList<>();
        if (json.has("spawns")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "spawns")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("A spawn entry in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject entry = element.getAsJsonObject();
                String entity = JsonUtils.getString(entry, "entity", "");
                if (entity.isEmpty()) {
                    ContentLog.LOGGER.error("A spawn entry in {} names no entity, skipping it", key);
                    continue;
                }
                int min = Math.max(1, JsonUtils.getInt(entry, "min", 1));
                spawns.add(new SpawnEntryDef(JsonUtils.getString(entry, "type", "creature"), entity,
                        Math.max(1, JsonUtils.getInt(entry, "weight", 8)),
                        min, Math.max(min, JsonUtils.getInt(entry, "max", 4))));
            }
        }
        return new CaveRegionDef(key,
                Math.max(0, JsonUtils.getInt(json, "weight", 1)),
                minHeight, maxHeight,
                integers(json),
                JsonUtils.getString(json, "floorCover", ""),
                MathHelper.clamp(JsonUtils.getFloat(json, "floorChance", 1.0F), 0.0F, 1.0F),
                JsonUtils.getString(json, "ceilingCover", ""),
                MathHelper.clamp(JsonUtils.getFloat(json, "ceilingChance", 1.0F), 0.0F, 1.0F),
                strings(json, "coverReplace"),
                waterLevel,
                spawns,
                JsonUtils.getBoolean(json, "keepDefaultSpawns", false),
                ContentParser.picks(json, "structures", "structure"),
                MathHelper.clamp(JsonUtils.getFloat(json, "structureChance", 1.0F), 0.0F, 1.0F),
                JsonUtils.getString(json, "structureLoot", ""),
                JsonUtils.getString(json, "biome", ""),
                JsonUtils.getString(json, "skyStone", ""),
                Json.bounded(json, "skyIslands", -1.0F, 1.0F, key),
                Json.bounded(json, "skyThickness", 0.0F, 8.0F, key),
                JsonUtils.getString(json, "ambientSound", "").trim(),
                MathHelper.clamp(JsonUtils.getFloat(json, "soundChance", 0.0111F), 0.0F, 1.0F),
                JsonUtils.getString(json, "particle", "").trim(),
                MathHelper.clamp(JsonUtils.getFloat(json, "particleChance", 0.00625F), 0.0F, 1.0F));
    }

    private static SpreadDef spread(ResourceLocation key, JsonObject json, int minHeight, int maxHeight) {
        if (!json.has("spread")) { return SpreadDef.even(); }
        JsonObject entry = JsonUtils.getJsonObject(json, "spread");
        String type = JsonUtils.getString(entry, "type", SpreadDef.EVEN).trim().toLowerCase(Locale.ROOT);
        if (!KNOWN_SPREADS.contains(type)) {
            ContentLog.LOGGER.error("Worldgen {} asks for spread type '{}', which is not one of {}, using {}", key, type, KNOWN_SPREADS, SpreadDef.EVEN);
            type = SpreadDef.EVEN;
        }
        int center = JsonUtils.getInt(entry, "center", (minHeight + maxHeight) / 2);
        int offsetMin = JsonUtils.getInt(entry, "offsetMin", 0);
        return new SpreadDef(type,
                center,
                Math.max(1, JsonUtils.getInt(entry, "range", Math.max(2, (maxHeight - minHeight) / 2))),
                MathHelper.clamp(JsonUtils.getInt(entry, "smoothness", 2), 1, 8),
                Math.max(1, JsonUtils.getInt(entry, "veinHeight", Math.max(1, maxHeight - minHeight))),
                Math.max(1, JsonUtils.getInt(entry, "veinDiameter", 12)),
                MathHelper.clamp(JsonUtils.getInt(entry, "verticalDensity", 16), 1, 100),
                MathHelper.clamp(JsonUtils.getInt(entry, "horizontalDensity", 32), 1, 100),
                offsetMin,
                Math.max(offsetMin, JsonUtils.getInt(entry, "offsetMax", offsetMin)),
                JsonUtils.getBoolean(entry, "ceiling", false));
    }

    @Nullable public static VillageDef village(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Village file {} is empty, ignoring it", key);
            return null;
        }
        String type = JsonUtils.getString(json, "type", VillageDef.FARM).trim().toLowerCase(Locale.ROOT);
        if (!VillageDef.FARM.equals(type) && !VillageDef.TEMPLATE.equals(type)) {
            ContentLog.LOGGER.error("Village plot {} asks for type '{}', which is not {} or {}, using {}", key, type, VillageDef.FARM, VillageDef.TEMPLATE, VillageDef.FARM);
            type = VillageDef.FARM;
        }
        String structure = JsonUtils.getString(json, "structure", "");
        if (VillageDef.TEMPLATE.equals(type) && structure.isEmpty()) {
            ContentLog.LOGGER.error("Village plot {} is a template but names no structure, ignoring it", key);
            return null;
        }
        return new VillageDef(key, type,
                Math.max(1, JsonUtils.getInt(json, "weight", 3)),
                Math.max(0, JsonUtils.getInt(json, "leastCount", 1)),
                Math.max(0, JsonUtils.getInt(json, "mostCount", 4)),
                Math.max(3, JsonUtils.getInt(json, "width", 7)),
                Math.max(1, JsonUtils.getInt(json, "height", 4)),
                Math.max(3, JsonUtils.getInt(json, "depth", 9)),
                Math.max(0, JsonUtils.getInt(json, "apron", 2)),
                strings(json, "crops"),
                JsonUtils.getString(json, "edge", "minecraft:log"),
                JsonUtils.getString(json, "soil", "minecraft:farmland"),
                JsonUtils.getBoolean(json, "water", true),
                Math.max(1, JsonUtils.getInt(json, "rowWidth", 2)),
                structure,
                JsonUtils.getString(json, "ground", "minecraft:dirt"),
                MathHelper.clamp(JsonUtils.getInt(json, "integrity", 100), 1, 100),
                Math.max(0, JsonUtils.getInt(json, "villagers", 0)),
                JsonUtils.getString(json, "villagerEntity", ""),
                JsonUtils.getInt(json, "villagerX", 1),
                JsonUtils.getInt(json, "villagerY", 1),
                JsonUtils.getInt(json, "villagerZ", 1),
                strings(json, "requires"),
                JsonUtils.getString(json, "lootTable", ""));
    }

    private static ShapeDef shape(ResourceLocation key, JsonObject json) {
        if (!json.has("shape")) { return ShapeDef.cluster(); }
        JsonObject entry = JsonUtils.getJsonObject(json, "shape");
        String type = JsonUtils.getString(entry, "type", ShapeDef.CLUSTER).trim().toLowerCase(Locale.ROOT);
        if (!KNOWN_SHAPES.contains(type)) {
            ContentLog.LOGGER.error("Worldgen {} asks for shape '{}', which is not one of {}, using {}", key, type, KNOWN_SHAPES, ShapeDef.CLUSTER);
            type = ShapeDef.CLUSTER;
        }
        String plane = JsonUtils.getString(entry, "plane", ShapeDef.CIRCLE).trim().toLowerCase(Locale.ROOT);
        if (!ShapeDef.CIRCLE.equals(plane) && !ShapeDef.SQUARE.equals(plane)) {
            ContentLog.LOGGER.error("Worldgen {} asks for plane '{}', which is not {} or {}, using {}", key, plane, ShapeDef.CIRCLE, ShapeDef.SQUARE, ShapeDef.CIRCLE);
            plane = ShapeDef.CIRCLE;
        }
        ShapeDef made = new ShapeDef(type,
                ContentParser.amount(entry, "radius", ShapeDef.BELT.equals(type) ? 32 : 6, 0),
                ContentParser.amount(entry, "height", ShapeDef.GEODE.equals(type) ? 8 : ShapeDef.TREE.equals(type) ? 5 : 1, 0),
                ContentParser.amount(entry, "width", 12, 3),
                plane,
                JsonUtils.getBoolean(entry, "slim", false),
                JsonUtils.getString(entry, "outline", ""),
                JsonUtils.getString(entry, "fill", ""),
                surface(entry),
                ContentParser.amount(entry, "stackHeight", 1, 1),
                JsonUtils.getBoolean(entry, "seeSky", true),
                JsonUtils.getBoolean(entry, "checkStay", true),
                JsonUtils.getInt(entry, "scatterX", 8),
                JsonUtils.getInt(entry, "scatterY", 4),
                JsonUtils.getInt(entry, "scatterZ", 8),
                JsonUtils.getString(entry, "log", ""),
                JsonUtils.getString(entry, "leaves", ""),
                JsonUtils.getBoolean(entry, "hanging", false),
                JsonUtils.getString(entry, "structure", ""),
                ContentParser.picks(entry, "structures", "structure"),
                ContentParser.picks(entry, "turns", "turn"),
                ContentParser.picks(entry, "mirrors", "mirror"),
                taper(key, entry),
                MathHelper.clamp(JsonUtils.getInt(entry, "integrity", 100), 1, 100),
                Math.max(0, JsonUtils.getInt(entry, "rarity", 0)),
                JsonUtils.getBoolean(entry, "rarityIsPerChunk", false),
                ShapeDef.FIELD.equals(type) ? ContentHardness.fieldFrom(JsonUtils.getJsonObject(entry, "field", new JsonObject())) : null,
                JsonUtils.getFloat(entry, "threshold", ShapeDef.VEIN.equals(type) ? 0.4F : 0.5F),
                JsonUtils.getString(entry, "lootTable", ""));
        made.locateAs = JsonUtils.getString(entry, "locateAs", "");
        made.fade = Math.max(0, JsonUtils.getInt(entry, "fade", 0));
        String pattern = JsonUtils.getString(entry, "pattern", "default").trim().toLowerCase(Locale.ROOT);
        if (!"default".equals(pattern) && !"banded".equals(pattern) && !"tube".equals(pattern)) {
            ContentLog.LOGGER.error("Worldgen {} asks for vein pattern '{}', which is not default, banded or tube, using default", key, pattern);
            pattern = "default";
        }
        made.pattern = pattern;
        made.density = MathHelper.clamp(JsonUtils.getFloat(entry, "density", 1.0F), 0.0F, 1.0F);
        made.rich = JsonUtils.getString(entry, "rich", "");
        made.poor = JsonUtils.getString(entry, "poor", "");
        made.richAt = MathHelper.clamp(JsonUtils.getFloat(entry, "richAt", 0.88F), 0.0F, 1.0F);
        made.poorAt = MathHelper.clamp(JsonUtils.getFloat(entry, "poorAt", 0.4F), 0.0F, made.richAt);
        made.middle = JsonUtils.getString(entry, "middle", "");
        made.budding = JsonUtils.getString(entry, "budding", "");
        made.buddingChance = MathHelper.clamp(JsonUtils.getFloat(entry, "buddingChance", 0.083F), 0.0F, 1.0F);
        made.crystal = JsonUtils.getString(entry, "crystal", "");
        made.crystalChance = MathHelper.clamp(JsonUtils.getFloat(entry, "crystalChance", 0.35F), 0.0F, 1.0F);
        made.crack = MathHelper.clamp(JsonUtils.getFloat(entry, "crack", 0.0F), 0.0F, 1.0F);
        if (entry.has("at")) {
            JsonArray pinned = JsonUtils.getJsonArray(entry, "at");
            if (pinned.size() == 2) { made.at = new int[] { pinned.get(0).getAsInt(), pinned.get(1).getAsInt() }; }
            else { ContentLog.LOGGER.error("Worldgen {} pins its imprint with 'at', which needs exactly [x, z], so it places by chance instead", key); }
        }
        return made;
    }

    private static String taper(ResourceLocation key, JsonObject entry) {
        String named = JsonUtils.getString(entry, "taper", ShapeDef.STRAIGHT).trim().toLowerCase(Locale.ROOT);
        if (ShapeDef.STRAIGHT.equals(named) || ShapeDef.BELL.equals(named) || ShapeDef.NEEDLE.equals(named)) { return named; }
        ContentLog.LOGGER.error("Worldgen {} asks for taper '{}', which is not {}, {} or {}, using {}", key, named, ShapeDef.STRAIGHT, ShapeDef.BELL, ShapeDef.NEEDLE, ShapeDef.STRAIGHT);
        return ShapeDef.STRAIGHT;
    }

    private static List<String> surface(JsonObject entry) {
        if (!entry.has("surface")) { return Collections.emptyList(); }
        JsonElement element = entry.get("surface");
        if (!element.isJsonArray()) { return Collections.singletonList(element.getAsString()); }
        return strings(entry, "surface");
    }

    private static List<BlockMatchDef> replaces(ResourceLocation key, JsonObject json) {
        if (!json.has("replace")) { return Collections.singletonList(ContentParser.match(key, "minecraft:stone")); }
        JsonElement element = json.get("replace");
        if (!element.isJsonArray()) { return Collections.singletonList(ContentParser.match(key, element)); }
        List<BlockMatchDef> values = new ArrayList<>();
        for (JsonElement name : element.getAsJsonArray()) { values.add(ContentParser.match(key, name)); }
        return values.isEmpty() ? Collections.singletonList(ContentParser.match(key, "minecraft:stone")) : Collections.unmodifiableList(values);
    }

    private static List<BlockMatchDef> adjacent(ResourceLocation key, JsonObject json) {
        if (!json.has("adjacent")) { return Collections.emptyList(); }
        JsonElement element = json.get("adjacent");
        if (!element.isJsonArray()) { return Collections.singletonList(ContentParser.match(key, element)); }
        List<BlockMatchDef> values = new ArrayList<>();
        for (JsonElement name : element.getAsJsonArray()) { values.add(ContentParser.match(key, name)); }
        return Collections.unmodifiableList(values);
    }

    private static List<BlockWeightDef> weights(JsonObject json) {
        if (!json.has("blocks")) { return Collections.emptyList(); }
        JsonArray array = JsonUtils.getJsonArray(json, "blocks");
        List<BlockWeightDef> values = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            JsonObject entry = element.getAsJsonObject();
            String name = JsonUtils.getString(entry, "block", "");
            if (name.isEmpty()) { continue; }
            values.add(new BlockWeightDef(new ResourceLocation(name),
                    JsonUtils.getInt(entry, "meta", 0),
                    Math.max(1, JsonUtils.getInt(entry, "weight", 1)),
                    Json.map(entry, "properties")));
        }
        return Collections.unmodifiableList(values);
    }

    private static List<Integer> integers(JsonObject json) {
        if (!json.has("dimensions")) { return Collections.emptyList(); }
        JsonArray array = JsonUtils.getJsonArray(json, "dimensions");
        List<Integer> values = new ArrayList<>(array.size());
        for (JsonElement element : array) { values.add(element.getAsInt()); }
        return Collections.unmodifiableList(values);
    }
}
