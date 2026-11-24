package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.hardness.ContentHardness;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockWeightDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.FollowDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnEntryDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.AmbienceDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyStone;
import mctmods.resourcedatapackloader.common.pack.definitions.villages.VillageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.CaveRegionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.SpreadDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.WorldgenDef;
import mctmods.resourcedatapackloader.common.util.worldgen.WeightedPicks;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;
import static mctmods.resourcedatapackloader.core.util.json.Json.strings;
import mctmods.resourcedatapackloader.core.util.math.IntRange;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public final class ContentParserWorldgen {
    private static final Set<String> KNOWN_SPREADS = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            SpreadDef.EVEN, SpreadDef.CENTERED, SpreadDef.SPRAWL, SpreadDef.TERRAIN, SpreadDef.CAVERN, SpreadDef.SUBMERGED)));
    private static final Set<String> KNOWN_SHAPES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            ShapeDef.CLUSTER, ShapeDef.PLATE, ShapeDef.GEODE, ShapeDef.LARGEVEIN, ShapeDef.DECORATION, ShapeDef.TREE, ShapeDef.VINES,
            ShapeDef.BASIN, ShapeDef.SPIRE, ShapeDef.NODULE, ShapeDef.VENT, ShapeDef.IMPRINT, ShapeDef.BELT, ShapeDef.FIELD, ShapeDef.VEIN, ShapeDef.SPRING)));

    private static final List<String> SHAPE_ONLY_KEYS = Collections.unmodifiableList(Arrays.asList("rarity", "rarityIsPerChunk", "pattern", "threshold", "density", "taper", "rich", "poor"));
    private static final List<String> WORLDGEN_KEYS = Collections.unmodifiableList(Arrays.asList("block", "meta", "blocks", "size", "attempts", "minHeight", "maxHeight", "replace", "adjacent", "sparse", "dimensions", "dimensionsAreBlacklist", "biomes", "biomeTypes", "biomesAreBlacklist", "requires", "retrogen", "retrogenKey", "minDistanceFromSpawn", "spread", "shape", "minTemperature", "maxTemperature", "minRainfall", "maxRainfall", "caveRegions", "indicators", "indicatorCount", "indicatorSpread", "then", "thenCount", "thenSpread", "thenDepth", "prospectAs", "snap", "snapDepth"));
    private static final List<String> SPREAD_KEYS = Collections.unmodifiableList(Arrays.asList("type", "center", "range", "smoothness", "veinHeight", "veinDiameter", "verticalDensity", "horizontalDensity", "offsetMin", "offsetMax", "ceiling"));
    private static final List<String> SHAPE_KEYS = Collections.unmodifiableList(Arrays.asList("type", "plane", "radius", "height", "width", "slim", "outline", "fill", "surface", "stackHeight", "seeSky", "checkStay", "scatterX", "scatterY", "scatterZ", "log", "leaves", "hanging", "structure", "structures", "turns", "mirrors", "taper", "integrity", "rarity", "rarityIsPerChunk", "field", "threshold", "lootTable", "respawn", "locateAs", "fade", "pattern", "density", "rich", "poor", "richAt", "poorAt", "middle", "budding", "buddingChance", "crystal", "crystalChance", "crack", "at"));
    private static final List<String> FIELD_KEYS = Collections.unmodifiableList(Arrays.asList("type", "spread", "chances", "cell", "seeds", "reach", "arms", "armReach"));
    private static final List<String> THEN_KEYS = Collections.unmodifiableList(Arrays.asList("name", "weight", "spread", "depth"));
    private static final List<String> SNAPS = Collections.unmodifiableList(Arrays.asList("floor", "ceiling"));
    private static final List<String> VILLAGE_TYPES = Collections.unmodifiableList(Arrays.asList(VillageDef.FARM, VillageDef.TEMPLATE));
    private static final List<String> PLANES = Collections.unmodifiableList(Arrays.asList(ShapeDef.CIRCLE, ShapeDef.SQUARE));
    private static final List<String> PATTERNS = Collections.unmodifiableList(Arrays.asList("default", "banded", "tube"));
    private static final List<String> TAPERS = Collections.unmodifiableList(Arrays.asList(ShapeDef.STRAIGHT, ShapeDef.BELL, ShapeDef.NEEDLE));

    private ContentParserWorldgen() {}

    @Nullable public static WorldgenDef worldgen(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "worldgen definition");
        if (json == null) { return null; }
        Json.unknownKeys(json, "the top level", WORLDGEN_KEYS);
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
                Json.dimensions(json),
                Json.getBoolean(json, "dimensionsAreBlacklist", false),
                strings(json, "biomes"), strings(json, "biomeTypes"),
                Json.getBoolean(json, "biomesAreBlacklist", false),
                strings(json, "requires"),
                Json.getBoolean(json, "retrogen", false),
                Json.getString(json, "retrogenKey", ""),
                Json.atLeast(json, "minDistanceFromSpawn", 0, 0),
                spread(key, json, minHeight, maxHeight),
                shape(key, json),
                Json.getFloat(json, "minTemperature", -100.0F),
                Json.getFloat(json, "maxTemperature", 100.0F),
                Json.getFloat(json, "minRainfall", -100.0F),
                Json.getFloat(json, "maxRainfall", 100.0F),
                json.has("replace"));
        List<ResourceLocation> regions = new ArrayList<>();
        for (String name : strings(json, "caveRegions")) { regions.add(name.indexOf(':') >= 0 ? new ResourceLocation(name) : new ResourceLocation(key.getNamespace(), name)); }
        if (!regions.isEmpty()) { made.caveRegions = regions; }
        made.indicators.load(strings(json, "indicators").toArray(new String[0]));
        made.indicatorCount = ContentParser.amount(json, "indicatorCount", 1, 0);
        made.indicatorSpread = Json.atLeast(json, "indicatorSpread", 0, 0);
        List<FollowDef> followers = new ArrayList<>();
        if (JsonUtils.isJsonArray(json, "then")) {
            for (JsonElement element : json.getAsJsonArray("then")) {
                String name;
                int weight;
                int spread = -1;
                AmountDef depth = null;
                if (element.isJsonObject()) {
                    JsonObject follow = element.getAsJsonObject();
                    Json.unknownKeys(follow, "then", THEN_KEYS);
                    name = Json.getString(follow, "name", "");
                    weight = Json.getInt(follow, "weight", 1);
                    spread = follow.has("spread") ? Json.atLeast(follow, "spread", 0, 0) : -1;
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
        made.thenSpread = json.has("thenSpread") ? Json.atLeast(json, "thenSpread", 0, 0) : -1;
        made.thenDepth = ContentParser.amount(json, "thenDepth", 0, Integer.MIN_VALUE);
        made.prospectAs = Json.trimmed(json, "prospectAs", "");
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
                Json.atLeast(json, "weight", 1, 0),
                minHeight, maxHeight,
                Json.dimensions(json),
                Json.getString(json, "floorCover", ""),
                Json.chance(json, "floorChance", 1.0F),
                Json.getString(json, "ceilingCover", ""),
                Json.chance(json, "ceilingChance", 1.0F),
                strings(json, "coverReplace"),
                waterLevel,
                spawns,
                Json.getBoolean(json, "keepDefaultSpawns", false),
                ContentParser.picks(json, "structures", "structure"),
                Json.chance(json, "structureChance", 1.0F),
                Json.getString(json, "structureLoot", ""),
                Json.getString(json, "biome", ""),
                SkyStone.read(json, key),
                new AmbienceDef(null, 0, 0, null,
                        Json.trimmed(json, "ambientSound", ""),
                        Json.chance(json, "soundChance", ContentParserDimensionTraits.SOUND_CHANCE),
                        Json.trimmed(json, "particle", ""),
                        Json.chance(json, "particleChance", ContentParserDimensionTraits.PARTICLE_CHANCE),
                        AmbienceDef.NO_COLOR),
                ContentParserRespawn.parse(key, json));
    }

    private static SpreadDef spread(ResourceLocation key, JsonObject json, int minHeight, int maxHeight) {
        if (!json.has("spread")) { return SpreadDef.even(); }
        JsonObject entry = Json.getJsonObject(json, "spread");
        Json.unknownKeys(entry, "spread", SPREAD_KEYS);
        String type = Json.choice(entry, "type", SpreadDef.EVEN, "Worldgen " + key + " spread", KNOWN_SPREADS);
        int center = Json.getInt(entry, "center", (minHeight + maxHeight) / 2);
        int offsetMin = Json.getInt(entry, "offsetMin", 0);
        return new SpreadDef(type,
                center,
                Json.atLeast(entry, "range", Math.max(2, (maxHeight - minHeight) / 2), 1),
                MathHelper.clamp(Json.getInt(entry, "smoothness", 2), 1, 8),
                Json.atLeast(entry, "veinHeight", Math.max(1, maxHeight - minHeight), 1),
                Json.atLeast(entry, "veinDiameter", 12, 1),
                MathHelper.clamp(Json.getInt(entry, "verticalDensity", 16), 1, 100),
                MathHelper.clamp(Json.getInt(entry, "horizontalDensity", 32), 1, 100),
                offsetMin,
                Json.atLeast(entry, "offsetMax", offsetMin, offsetMin),
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
        String orderSign = Json.trimmed(json, "orderSign", "");
        if (!orderSign.isEmpty() && !VillageDef.TEMPLATE.equals(type)) {
            ContentLog.LOGGER.error("Village plot {} is a farm and names the order sign '{}', which only a template plot stands, ignoring the sign", key, orderSign);
            orderSign = "";
        }
        return new VillageDef(key, type,
                Json.atLeast(json, "weight", 3, 1),
                Json.atLeast(json, "leastCount", 1, 0),
                Json.atLeast(json, "mostCount", 4, 0),
                Json.atLeast(json, "width", 7, 3),
                Json.atLeast(json, "height", 4, 1),
                Json.atLeast(json, "depth", 9, 3),
                Json.atLeast(json, "apron", 2, 0),
                strings(json, "crops"),
                Json.getString(json, "edge", "minecraft:log"),
                Json.getString(json, "soil", "minecraft:farmland"),
                Json.getBoolean(json, "water", true),
                Json.atLeast(json, "rowWidth", 2, 1),
                structure,
                Json.getString(json, "ground", "minecraft:dirt"),
                MathHelper.clamp(Json.getInt(json, "integrity", 100), 1, 100),
                Json.atLeast(json, "villagers", 0, 0),
                Json.getString(json, "villagerEntity", ""),
                Json.getInt(json, "villagerX", 1),
                Json.getInt(json, "villagerY", 1),
                Json.getInt(json, "villagerZ", 1),
                strings(json, "requires"),
                Json.getString(json, "lootTable", ""),
                orderSign,
                Json.getInt(json, "orderX", 1),
                Json.getInt(json, "orderY", 1),
                Json.getInt(json, "orderZ", 1),
                ContentParserRespawn.parse(key, json));
    }

    private static ShapeDef shape(ResourceLocation key, JsonObject json) {
        if (!json.has("shape")) { return ShapeDef.cluster(); }
        JsonObject entry = Json.getJsonObject(json, "shape");
        Json.unknownKeys(entry, "shape", SHAPE_KEYS);
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
                taper(owner, entry),
                MathHelper.clamp(Json.getInt(entry, "integrity", 100), 1, 100),
                Json.atLeast(entry, "rarity", 0, 0),
                Json.getBoolean(entry, "rarityIsPerChunk", false),
                ShapeDef.FIELD.equals(type) ? ContentHardness.fieldFrom(field(entry)) : null,
                Json.getFloat(entry, "threshold", ShapeDef.VEIN.equals(type) ? 0.4F : 0.5F),
                Json.getString(entry, "lootTable", ""),
                ContentParserRespawn.parse(key, entry));
        made.locateAs = Json.getString(entry, "locateAs", "");
        made.fade = Json.atLeast(entry, "fade", 0, 0);
        made.pattern = Json.choice(entry, "pattern", "default", owner, PATTERNS);
        made.density = Json.chance(entry, "density", 1.0F);
        made.rich = Json.getString(entry, "rich", "");
        made.poor = Json.getString(entry, "poor", "");
        made.richAt = Json.chance(entry, "richAt", 0.88F);
        made.poorAt = MathHelper.clamp(Json.getFloat(entry, "poorAt", 0.4F), 0.0F, made.richAt);
        made.middle = Json.getString(entry, "middle", "");
        made.budding = Json.getString(entry, "budding", "");
        made.buddingChance = Json.chance(entry, "buddingChance", 0.083F);
        made.crystal = Json.getString(entry, "crystal", "");
        made.crystalChance = Json.chance(entry, "crystalChance", 0.35F);
        made.crack = Json.chance(entry, "crack", 0.0F);
        if (entry.has("at")) {
            JsonArray pinned = Json.getJsonArray(entry, "at");
            if (pinned.size() == 2) { made.at = new int[] { Json.asInt(pinned.get(0), "at", 0), Json.asInt(pinned.get(1), "at", 0) }; }
            else { ContentLog.LOGGER.error("Worldgen {} pins its imprint with 'at', which needs exactly [x, z], so it places by chance instead", key); }
        }
        return made;
    }

    private static JsonObject field(JsonObject entry) {
        JsonObject field = Json.getJsonObject(entry, "field", new JsonObject());
        Json.unknownKeys(field, "field", FIELD_KEYS);
        return field;
    }

    private static String taper(String owner, JsonObject entry) { return Json.choice(entry, "taper", ShapeDef.STRAIGHT, owner, TAPERS); }

    private static List<String> surface(JsonObject entry) {
        if (!entry.has("surface")) { return Collections.emptyList(); }
        JsonElement element = entry.get("surface");
        if (element.isJsonPrimitive()) { return Collections.singletonList(element.getAsString()); }
        if (!element.isJsonArray()) { return Collections.emptyList(); }
        return strings(entry, "surface");
    }

    private static List<BlockMatchDef> replaces(ResourceLocation key, JsonObject json) {
        List<BlockMatchDef> values = ContentParser.matches(key, json, "replace");
        return values.isEmpty() ? Collections.singletonList(ContentParser.match(key, ContentParser.STONE)) : Collections.unmodifiableList(values);
    }

    private static List<BlockMatchDef> adjacent(ResourceLocation key, JsonObject json) { return Collections.unmodifiableList(ContentParser.matches(key, json, "adjacent")); }

    private static List<BlockWeightDef> weights(JsonObject json) { return weights(json, "blocks"); }

    public static List<BlockWeightDef> weights(JsonObject json, String member) {
        if (!json.has(member)) { return Collections.emptyList(); }
        JsonArray array = Json.getJsonArray(json, member);
        List<BlockWeightDef> values = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            JsonObject entry = element.getAsJsonObject();
            String name = Json.getString(entry, "block", "");
            if (name.isEmpty()) { continue; }
            values.add(new BlockWeightDef(new ResourceLocation(name),
                    Json.getInt(entry, "meta", 0),
                    Json.atLeast(entry, "weight", 1, 1),
                    Json.map(entry, "properties")));
        }
        return Collections.unmodifiableList(values);
    }
}
