package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.IntRange;
import mctmods.resourcedatapackloader.util.Settings;
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
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentParserWorldgen {
    private static final Set<String> KNOWN_SPREADS = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            SpreadDef.EVEN, SpreadDef.CENTERED, SpreadDef.SPRAWL, SpreadDef.TERRAIN, SpreadDef.CAVERN, SpreadDef.SUBMERGED)));
    private static final Set<String> KNOWN_SHAPES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            ShapeDef.CLUSTER, ShapeDef.PLATE, ShapeDef.GEODE, ShapeDef.LARGEVEIN, ShapeDef.DECORATION, ShapeDef.TREE, ShapeDef.VINES,
            ShapeDef.BASIN, ShapeDef.SPIRE, ShapeDef.NODULE, ShapeDef.VENT, ShapeDef.IMPRINT, ShapeDef.BELT, ShapeDef.FIELD, ShapeDef.VEIN, ShapeDef.SPRING)));

    private static final List<String> SHAPE_ONLY_KEYS = Collections.unmodifiableList(Arrays.asList("rarity", "rarityIsPerChunk", "pattern", "threshold", "density", "taper", "rich", "poor"));
    private static final List<String> SNAPS = Collections.unmodifiableList(Arrays.asList("floor", "ceiling"));
    private static final List<String> VILLAGE_TYPES = Collections.unmodifiableList(Arrays.asList(VillageDef.FARM, VillageDef.TEMPLATE));
    private static final List<String> PLANES = Collections.unmodifiableList(Arrays.asList(ShapeDef.CIRCLE, ShapeDef.SQUARE));
    private static final List<String> PATTERNS = Collections.unmodifiableList(Arrays.asList("default", "banded", "tube"));
    private static final List<String> TAPERS = Collections.unmodifiableList(Arrays.asList(ShapeDef.STRAIGHT, ShapeDef.BELL, ShapeDef.NEEDLE));

    private ContentParserWorldgen() {}

    @Nullable public static WorldgenDef worldgen(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "worldgen definition");
        if (json == null) { return null; }
        String block = Json.getString(json, "block", "");
        if (block.isEmpty()) {
            ContentLog.LOGGER.error("Worldgen definition {} has no block, ignoring it", key);
            return null;
        }
        IntRange heights = ContentParser.heights("Worldgen definition", key, json, 0, 64);
        int minHeight = Math.max(-Config.worldgen.rubicHeightLimit, heights.getMin());
        int maxHeight = Math.max(minHeight, heights.getMax());
        WorldgenDef made = new WorldgenDef(key, new ResourceLocation(block),
                Json.getInt(json, "meta", 0),
                weights(json),
                ContentParser.amount(json, "size", 8, 1),
                ContentParser.amount(json, "attempts", 8, 0),
                minHeight, maxHeight,
                replaces(key, json),
                adjacent(key, json),
                Json.getBoolean(json, "sparse", false),
                integers(json),
                Json.getBoolean(json, "dimensionsAreBlacklist", false),
                strings(json, "biomes"), strings(json, "biomeTypes"),
                Json.getBoolean(json, "biomesAreBlacklist", false),
                strings(json, "requires"),
                Json.getBoolean(json, "retrogen", false),
                Json.getString(json, "retrogenKey", ""),
                Math.max(0, Json.getInt(json, "minDistanceFromSpawn", 0)),
                spread(key, json, minHeight, maxHeight),
                shape(key, json),
                Json.getFloat(json, "minTemperature", -100.0F),
                Json.getFloat(json, "maxTemperature", 100.0F),
                Json.getFloat(json, "minRainfall", -100.0F),
                Json.getFloat(json, "maxRainfall", 100.0F),
                json.has("replace"));
        List<ResourceLocation> regions = new ArrayList<>();
        for (String name : strings(json, "caveRegions")) {
            regions.add(name.indexOf(':') >= 0 ? new ResourceLocation(name) : new ResourceLocation(key.getNamespace(), name));
        }
        if (!regions.isEmpty()) { made.caveRegions = regions; }
        made.indicators.load(strings(json, "indicators").toArray(new String[0]));
        made.indicatorCount = ContentParser.amount(json, "indicatorCount", 1, 0);
        made.indicatorSpread = Math.max(0, Json.getInt(json, "indicatorSpread", 0));
        List<FollowDef> followers = new ArrayList<>();
        if (JsonUtils.isJsonArray(json, "then")) {
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
                    name = split.name;
                    weight = split.weight;
                    if (split.unreadable) { ContentLog.LOGGER.error("Worldgen definition {} has a then entry '{}' whose weight is not a number, reading it as 1", key, entry); }
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
                if (!WeightedPicks.EMPTY.equals(name) && name.indexOf(':') < 0) { name = key.getNamespace() + ":" + name; }
                followers.add(new FollowDef(name, weight, spread, depth));
            }
        }
        made.then = Collections.unmodifiableList(followers);
        made.thenCount = ContentParser.amount(json, "thenCount", 1, 0);
        made.thenSpread = json.has("thenSpread") ? Math.max(0, Json.getInt(json, "thenSpread", 0)) : -1;
        made.thenDepth = ContentParser.amount(json, "thenDepth", 0, Integer.MIN_VALUE);
        made.prospectAs = Json.getString(json, "prospectAs", "").trim();
        made.snap = Json.choice(json, "snap", "", "Worldgen " + key, SNAPS);
        int depth = Json.getInt(json, "snapDepth", 0);
        if (depth < 0) {
            ContentLog.LOGGER.error("Worldgen {} asks for snapDepth {}, which is below zero, so it stays at the surface", key, depth);
            depth = 0;
        }
        made.snapDepth = made.snap.isEmpty() ? 0 : depth;
        for (String misplaced : SHAPE_ONLY_KEYS) {
            if (json.has(misplaced)) { ContentLog.LOGGER.error("Worldgen {} writes '{}' outside its shape object, where it is ignored", key, misplaced); }
        }
        return made;
    }

    @Nullable public static CaveRegionDef caveRegion(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "cave region definition");
        if (json == null) { return null; }
        IntRange heights = ContentParser.heights("Cave region definition", key, json, -Config.worldgen.rubicHeightLimit, 48);
        int minHeight = heights.getMin();
        int maxHeight = heights.getMax();
        int waterLevel = CaveRegionDef.NO_WATER;
        if (json.has("waterLevel")) { waterLevel = Json.getInt(json, "waterLevel", CaveRegionDef.NO_WATER); }
        List<SpawnEntryDef> spawns = ContentParser.spawns(key, json, "type", true);
        return new CaveRegionDef(key,
                Math.max(0, Json.getInt(json, "weight", 1)),
                minHeight, maxHeight,
                integers(json),
                Json.getString(json, "floorCover", ""),
                MathHelper.clamp(Json.getFloat(json, "floorChance", 1.0F), 0.0F, 1.0F),
                Json.getString(json, "ceilingCover", ""),
                MathHelper.clamp(Json.getFloat(json, "ceilingChance", 1.0F), 0.0F, 1.0F),
                strings(json, "coverReplace"),
                waterLevel,
                spawns,
                Json.getBoolean(json, "keepDefaultSpawns", false),
                ContentParser.picks(json, "structures", "structure"),
                MathHelper.clamp(Json.getFloat(json, "structureChance", 1.0F), 0.0F, 1.0F),
                Json.getString(json, "structureLoot", ""),
                Json.getString(json, "biome", ""),
                SkyStone.read(json, key),
                new AmbienceDef(null, 0, 0, null,
                        Json.getString(json, "ambientSound", "").trim(),
                        MathHelper.clamp(Json.getFloat(json, "soundChance", ContentParserDimensionTraits.SOUND_CHANCE), 0.0F, 1.0F),
                        Json.getString(json, "particle", "").trim(),
                        MathHelper.clamp(Json.getFloat(json, "particleChance", ContentParserDimensionTraits.PARTICLE_CHANCE), 0.0F, 1.0F),
                        AmbienceDef.NO_COLOR));
    }

    private static SpreadDef spread(ResourceLocation key, JsonObject json, int minHeight, int maxHeight) {
        if (!json.has("spread")) { return SpreadDef.even(); }
        JsonObject entry = Json.getJsonObject(json, "spread");
        String type = Json.choice(entry, "type", SpreadDef.EVEN, "Worldgen " + key + " spread", KNOWN_SPREADS);
        int center = Json.getInt(entry, "center", (minHeight + maxHeight) / 2);
        int offsetMin = Json.getInt(entry, "offsetMin", 0);
        return new SpreadDef(type,
                center,
                Math.max(1, Json.getInt(entry, "range", Math.max(2, (maxHeight - minHeight) / 2))),
                MathHelper.clamp(Json.getInt(entry, "smoothness", 2), 1, 8),
                Math.max(1, Json.getInt(entry, "veinHeight", Math.max(1, maxHeight - minHeight))),
                Math.max(1, Json.getInt(entry, "veinDiameter", 12)),
                MathHelper.clamp(Json.getInt(entry, "verticalDensity", 16), 1, 100),
                MathHelper.clamp(Json.getInt(entry, "horizontalDensity", 32), 1, 100),
                offsetMin,
                Math.max(offsetMin, Json.getInt(entry, "offsetMax", offsetMin)),
                Json.getBoolean(entry, "ceiling", false));
    }

    @Nullable public static VillageDef village(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "village file");
        if (json == null) { return null; }
        String type = Json.choice(json, "type", VillageDef.FARM, "Village plot " + key, VILLAGE_TYPES);
        String structure = Json.getString(json, "structure", "");
        if (VillageDef.TEMPLATE.equals(type) && structure.isEmpty()) {
            ContentLog.LOGGER.error("Village plot {} is a template but names no structure, ignoring it", key);
            return null;
        }
        return new VillageDef(key, type,
                Math.max(1, Json.getInt(json, "weight", 3)),
                Math.max(0, Json.getInt(json, "leastCount", 1)),
                Math.max(0, Json.getInt(json, "mostCount", 4)),
                Math.max(3, Json.getInt(json, "width", 7)),
                Math.max(1, Json.getInt(json, "height", 4)),
                Math.max(3, Json.getInt(json, "depth", 9)),
                Math.max(0, Json.getInt(json, "apron", 2)),
                strings(json, "crops"),
                Json.getString(json, "edge", "minecraft:log"),
                Json.getString(json, "soil", "minecraft:farmland"),
                Json.getBoolean(json, "water", true),
                Math.max(1, Json.getInt(json, "rowWidth", 2)),
                structure,
                Json.getString(json, "ground", "minecraft:dirt"),
                MathHelper.clamp(Json.getInt(json, "integrity", 100), 1, 100),
                Math.max(0, Json.getInt(json, "villagers", 0)),
                Json.getString(json, "villagerEntity", ""),
                Json.getInt(json, "villagerX", 1),
                Json.getInt(json, "villagerY", 1),
                Json.getInt(json, "villagerZ", 1),
                strings(json, "requires"),
                Json.getString(json, "lootTable", ""));
    }

    private static ShapeDef shape(ResourceLocation key, JsonObject json) {
        if (!json.has("shape")) { return ShapeDef.cluster(); }
        JsonObject entry = Json.getJsonObject(json, "shape");
        String owner = "Worldgen " + key + " shape";
        String type = Json.choice(entry, "type", ShapeDef.CLUSTER, owner, KNOWN_SHAPES);
        String plane = Json.choice(entry, "plane", ShapeDef.CIRCLE, owner, PLANES);
        ShapeDef made = new ShapeDef(type,
                ContentParser.amount(entry, "radius", ShapeDef.BELT.equals(type) ? 32 : 6, 0),
                ContentParser.amount(entry, "height", ShapeDef.GEODE.equals(type) ? 8 : ShapeDef.TREE.equals(type) ? 5 : 1, 0),
                ContentParser.amount(entry, "width", 12, 3),
                plane,
                Json.getBoolean(entry, "slim", false),
                Json.getString(entry, "outline", ""),
                Json.getString(entry, "fill", ""),
                surface(entry),
                ContentParser.amount(entry, "stackHeight", 1, 1),
                Json.getBoolean(entry, "seeSky", true),
                Json.getBoolean(entry, "checkStay", true),
                Json.getInt(entry, "scatterX", 8),
                Json.getInt(entry, "scatterY", 4),
                Json.getInt(entry, "scatterZ", 8),
                Json.getString(entry, "log", ""),
                Json.getString(entry, "leaves", ""),
                Json.getBoolean(entry, "hanging", false),
                Json.getString(entry, "structure", ""),
                ContentParser.picks(entry, "structures", "structure"),
                ContentParser.picks(entry, "turns", "turn"),
                ContentParser.picks(entry, "mirrors", "mirror"),
                taper(key, entry),
                MathHelper.clamp(Json.getInt(entry, "integrity", 100), 1, 100),
                Math.max(0, Json.getInt(entry, "rarity", 0)),
                Json.getBoolean(entry, "rarityIsPerChunk", false),
                ShapeDef.FIELD.equals(type) ? ContentHardness.fieldFrom(Json.getJsonObject(entry, "field", new JsonObject())) : null,
                Json.getFloat(entry, "threshold", ShapeDef.VEIN.equals(type) ? 0.4F : 0.5F),
                Json.getString(entry, "lootTable", ""));
        made.locateAs = Json.getString(entry, "locateAs", "");
        made.fade = Math.max(0, Json.getInt(entry, "fade", 0));
        made.pattern = Json.choice(entry, "pattern", "default", "Worldgen " + key + " shape", PATTERNS);
        made.density = MathHelper.clamp(Json.getFloat(entry, "density", 1.0F), 0.0F, 1.0F);
        made.rich = Json.getString(entry, "rich", "");
        made.poor = Json.getString(entry, "poor", "");
        made.richAt = MathHelper.clamp(Json.getFloat(entry, "richAt", 0.88F), 0.0F, 1.0F);
        made.poorAt = MathHelper.clamp(Json.getFloat(entry, "poorAt", 0.4F), 0.0F, made.richAt);
        made.middle = Json.getString(entry, "middle", "");
        made.budding = Json.getString(entry, "budding", "");
        made.buddingChance = MathHelper.clamp(Json.getFloat(entry, "buddingChance", 0.083F), 0.0F, 1.0F);
        made.crystal = Json.getString(entry, "crystal", "");
        made.crystalChance = MathHelper.clamp(Json.getFloat(entry, "crystalChance", 0.35F), 0.0F, 1.0F);
        made.crack = MathHelper.clamp(Json.getFloat(entry, "crack", 0.0F), 0.0F, 1.0F);
        if (entry.has("at")) {
            JsonArray pinned = Json.getJsonArray(entry, "at");
            if (pinned.size() == 2) { made.at = new int[] { Json.asInt(pinned.get(0), "at", 0), Json.asInt(pinned.get(1), "at", 0) }; }
            else { ContentLog.LOGGER.error("Worldgen {} pins its imprint with 'at', which needs exactly [x, z], so it places by chance instead", key); }
        }
        return made;
    }

    private static String taper(ResourceLocation key, JsonObject entry) {
        return Json.choice(entry, "taper", ShapeDef.STRAIGHT, "Worldgen " + key + " shape", TAPERS);
    }

    private static List<String> surface(JsonObject entry) {
        if (!entry.has("surface")) { return Collections.emptyList(); }
        JsonElement element = entry.get("surface");
        if (element.isJsonPrimitive()) { return Collections.singletonList(element.getAsString()); }
        if (!element.isJsonArray()) { return Collections.emptyList(); }
        return strings(entry, "surface");
    }

    private static List<BlockMatchDef> replaces(ResourceLocation key, JsonObject json) {
        List<BlockMatchDef> values = ContentParser.matches(key, json, "replace");
        return values.isEmpty() ? Collections.singletonList(ContentParser.match(key, "minecraft:stone")) : Collections.unmodifiableList(values);
    }

    private static List<BlockMatchDef> adjacent(ResourceLocation key, JsonObject json) {
        return Collections.unmodifiableList(ContentParser.matches(key, json, "adjacent"));
    }

    private static List<BlockWeightDef> weights(JsonObject json) {
        if (!json.has("blocks")) { return Collections.emptyList(); }
        JsonArray array = Json.getJsonArray(json, "blocks");
        List<BlockWeightDef> values = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            JsonObject entry = element.getAsJsonObject();
            String name = Json.getString(entry, "block", "");
            if (name.isEmpty()) { continue; }
            values.add(new BlockWeightDef(new ResourceLocation(name),
                    Json.getInt(entry, "meta", 0),
                    Math.max(1, Json.getInt(entry, "weight", 1)),
                    Json.map(entry, "properties")));
        }
        return Collections.unmodifiableList(values);
    }

    private static List<Integer> integers(JsonObject json) {
        if (!json.has("dimensions")) { return Collections.emptyList(); }
        JsonArray array = Json.getJsonArray(json, "dimensions");
        List<Integer> values = Json.ints(array, "dimensions");
        return Collections.unmodifiableList(values);
    }
}
