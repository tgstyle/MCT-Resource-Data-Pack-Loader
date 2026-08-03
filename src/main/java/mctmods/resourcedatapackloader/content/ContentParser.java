package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.content.types.ContentItemTypes;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawning;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentParser {
    private static final Set<String> KNOWN_SPREADS = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            SpreadDef.EVEN, SpreadDef.CENTERED, SpreadDef.SPRAWL, SpreadDef.TERRAIN, SpreadDef.CAVERN, SpreadDef.SUBMERGED)));
    private static final Set<String> KNOWN_TERRAIN = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            DimensionDef.OVERWORLD, DimensionDef.FLAT, DimensionDef.VOID, DimensionDef.NETHER, DimensionDef.END)));
    private static final Set<String> KNOWN_SHAPES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            ShapeDef.CLUSTER, ShapeDef.PLATE, ShapeDef.GEODE, ShapeDef.LARGEVEIN, ShapeDef.DECORATION, ShapeDef.TREE, ShapeDef.VINES,
            ShapeDef.BASIN, ShapeDef.SPIRE, ShapeDef.NODULE, ShapeDef.VENT, ShapeDef.IMPRINT, ShapeDef.BELT)));
    public static final String PLACEHOLDER = "open";
    public static final String DEFAULT_STILL = "minecraft:blocks/water_still";
    public static final String DEFAULT_FLOW = "minecraft:blocks/water_flow";
    private static final Gson GSON = new GsonBuilder().create();
    private static final int[] NO_CHANCE = new int[0];

    private ContentParser() {}

    @Nullable public static BlockDef block(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Block definition {} is empty, ignoring it", key);
            return null;
        }

        Material material = ContentTypes.material(JsonUtils.getString(json, "material", "rock"), key.toString());
        MapColor mapColor = ContentTypes.mapColor(JsonUtils.getString(json, "mapColor", ""), material.getMaterialMapColor(), key.toString());
        SoundType soundType = ContentTypes.soundType(JsonUtils.getString(json, "soundType", ""), null, key.toString());

        JsonObject exp = JsonUtils.getJsonObject(json, "expDrop", new JsonObject());
        int expMin = JsonUtils.getInt(exp, "min", 0);
        int expMax = JsonUtils.getInt(exp, "max", 0);

        String type = JsonUtils.getString(json, "type", ContentBlockTypes.DEFAULT);
        int maxVariants = ContentBlockTypes.get(type, key).maxVariants();
        boolean opaque = JsonUtils.getBoolean(json, "opaque", true);

        JsonObject variants = JsonUtils.getJsonObject(json, "variants", new JsonObject());
        BlockVariant[] byMeta = new BlockVariant[maxVariants];
        List<BlockVariant> visible = new ArrayList<>();

        for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
            String name = entry.getKey();
            if (!entry.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("Block variant '{}' in {} is not an object, skipping it", name, key);
                continue;
            }
            JsonObject variant = entry.getValue().getAsJsonObject();
            int meta = JsonUtils.getInt(variant, "meta", -1);
            if (meta < 0 || meta >= maxVariants) {
                ContentLog.LOGGER.error("Block variant '{}' in {} has meta {}, which is outside 0-{}. A '{}' block cannot hold more than {} variants, so this one is skipped. Split it into another block file", name, key, meta, maxVariants - 1, type, maxVariants);
                continue;
            }
            if (byMeta[meta] != null) {
                ContentLog.LOGGER.error("Block variants '{}' and '{}' in {} both claim meta {}, skipping '{}'", byMeta[meta].name, name, key, meta, name);
                continue;
            }
            BlockVariant parsed = blockVariant(key, name, meta, variant);
            byMeta[meta] = parsed;
            visible.add(parsed);
        }

        for (int meta = 0; meta < byMeta.length; meta++) {
            if (byMeta[meta] == null) { byMeta[meta] = BlockVariant.placeholder(placeholderName(meta, 2), meta); }
        }

        return new BlockDef(key, type, material, mapColor, soundType,
                JsonUtils.getString(json, "creativeTab", ""),
                JsonUtils.getString(json, "harvestTool", "pickaxe"),
                JsonUtils.getInt(json, "harvestToolLevel", 0),
                JsonUtils.getBoolean(json, "silkHarvest", true),
                expMin, expMax,
                JsonUtils.getFloat(json, "explosionResistanceDivisor", 1.0F),
                byMeta, Collections.unmodifiableList(visible), strings(json, "requires"),
                renderLayer(JsonUtils.getString(json, "renderLayer", ""), key.toString()),
                opaque,
                JsonUtils.getBoolean(json, "fullCube", opaque),
                JsonUtils.getInt(json, "lightOpacity", opaque ? 255 : 0),
                JsonUtils.getFloat(json, "slipperiness", 0.6F),
                bounds(key, json),
                JsonUtils.getInt(json, "flammability", 0),
                JsonUtils.getInt(json, "fireSpread", 0),
                JsonUtils.getString(json, "modelBlock", "minecraft:stone"),
                JsonUtils.getInt(json, "modelMeta", 0),
                "item".equals(JsonUtils.getString(json, "itemModel", "state")),
                JsonUtils.getString(json, "particle", BlockDef.PARTICLE_FLAME).toLowerCase(Locale.ROOT),
                JsonUtils.getBoolean(json, "smoke", true),
                ContentTypes.color(JsonUtils.getString(json, "particleColor", "FFFFFF"), key.toString()),
                JsonUtils.getString(json, "seed", ""),
                JsonUtils.getString(json, "produce", ""),
                Math.max(1, Math.min(7, JsonUtils.getInt(json, "maxAge", 7))),
                sapling(json),
                portal(json),
                growth(json),
                strings(json, "plantTypes"),
                JsonUtils.getBoolean(json, "spawnsAnimals", false),
                behaviors(key, json),
                JsonUtils.getString(json, "tint", ""),
                JsonUtils.getString(json, "leafSapling", ""),
                Math.max(0, Math.min(100, JsonUtils.getInt(json, "leafSaplingChance", 5))));
    }

    @Nullable private static PortalDef portal(JsonObject json) {
        if (!json.has("portal")) { return null; }

        JsonObject entry = JsonUtils.getJsonObject(json, "portal");
        return new PortalDef(JsonUtils.getInt(entry, "dimension"),
                JsonUtils.getInt(entry, "returnDimension", 0),
                JsonUtils.getString(entry, "gate", ""),
                Math.max(0, JsonUtils.getInt(entry, "cooldown", 60)),
                JsonUtils.getBoolean(entry, "platform", true),
                JsonUtils.getString(entry, "platformBlock", ""),
                JsonUtils.getString(entry, "sound", ""),
                JsonUtils.getBoolean(entry, "owned", true));
    }

    @Nullable public static WorldTemplateDef worldTemplate(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) { return null; }

        Map<String, String> roles = new LinkedHashMap<>();
        if (json.has("roles")) {
            JsonObject entry = JsonUtils.getJsonObject(json, "roles");
            for (Map.Entry<String, JsonElement> role : entry.entrySet()) {
                String name = role.getKey().trim().toLowerCase(Locale.ROOT);
                if (!ContentWorldTemplates.knownRoles().containsKey(name)) {
                    ContentLog.LOGGER.error("World template {} names role '{}', which is not one of {}, ignoring it", key, role.getKey(), ContentWorldTemplates.describeRoles());
                    continue;
                }
                if (!role.getValue().isJsonPrimitive()) {
                    ContentLog.LOGGER.error("World template {} sets role '{}' to something that is not a biome name, ignoring it", key, role.getKey());
                    continue;
                }
                roles.put(name, role.getValue().getAsString());
            }
        }

        List<Integer> dimensions = new ArrayList<>();
        if (json.has("dimensions")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "dimensions")) { dimensions.add(element.getAsInt()); }
        }

        return new WorldTemplateDef(key,
                JsonUtils.getString(json, "name", key.getPath()),
                JsonUtils.getString(json, "default", WorldTemplateDef.VOID),
                Collections.unmodifiableMap(roles),
                structures(key, json),
                json.has("settings") ? JsonUtils.getJsonObject(json, "settings") : null,
                Collections.unmodifiableList(dimensions),
                strings(json, "requires"));
    }

    public static Map<Integer, Map<String, String>> gameRuleFile(ResourceLocation key, String contents) {
        Map<Integer, Map<String, String>> found = new LinkedHashMap<>();
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) { return found; }

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            int dimension;
            try { dimension = Integer.parseInt(entry.getKey().trim()); }
            catch (NumberFormatException broken) {
                ContentLog.LOGGER.error("Game rule file {} has key '{}', which is not a dimension id, ignoring it", key, entry.getKey());
                continue;
            }
            if (!entry.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("Game rule file {} maps dimension {} to something that is not a set of rules, ignoring it", key, dimension);
                continue;
            }
            Map<String, String> rules = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> rule : entry.getValue().getAsJsonObject().entrySet()) {
                if (!rule.getValue().isJsonPrimitive()) { continue; }

                rules.put(rule.getKey(), rule.getValue().getAsString());
            }
            found.put(dimension, rules);
        }
        return found;
    }

    private static Map<String, String> gameRules(ResourceLocation key, JsonObject json) {
        Map<String, String> found = new LinkedHashMap<>();
        if (!json.has("gameRules")) { return Collections.unmodifiableMap(found); }

        JsonObject entry = JsonUtils.getJsonObject(json, "gameRules");
        for (Map.Entry<String, JsonElement> rule : entry.entrySet()) {
            if (!rule.getValue().isJsonPrimitive()) {
                ContentLog.LOGGER.error("Dimension {} sets game rule '{}' to something that is not a value, ignoring it", key, rule.getKey());
                continue;
            }
            found.put(rule.getKey(), rule.getValue().getAsString());
        }
        return Collections.unmodifiableMap(found);
    }

    @Nullable public static DimensionDef dimension(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) { return null; }

        JsonObject terrain = json.has("terrain") ? JsonUtils.getJsonObject(json, "terrain") : new JsonObject();
        JsonObject biomes = json.has("biomes") ? JsonUtils.getJsonObject(json, "biomes") : new JsonObject();
        JsonObject sky = json.has("sky") ? JsonUtils.getJsonObject(json, "sky") : new JsonObject();
        String skyColor = JsonUtils.getString(sky, "skyColor", "").trim();
        String cloudColor = JsonUtils.getString(sky, "cloudColor", "").trim();

        String type = JsonUtils.getString(terrain, "type", DimensionDef.OVERWORLD).trim().toLowerCase(Locale.ROOT);
        String source = JsonUtils.getString(biomes, "source", DimensionDef.INHERIT).trim().toLowerCase(Locale.ROOT);
        if (!DimensionDef.SINGLE.equals(source) && !DimensionDef.INHERIT.equals(source)) {
            ContentLog.LOGGER.error("Dimension {} asks for biome source '{}', which is not {} or {}, using {}", key, source, DimensionDef.SINGLE, DimensionDef.INHERIT, DimensionDef.INHERIT);
            source = DimensionDef.INHERIT;
        }
        if (!KNOWN_TERRAIN.contains(type)) {
            ContentLog.LOGGER.error("Dimension {} asks for terrain '{}', which is not one of {}, using {}", key, type, KNOWN_TERRAIN, DimensionDef.OVERWORLD);
            type = DimensionDef.OVERWORLD;
        }
        String fog = JsonUtils.getString(sky, "fogColor", "");

        return new DimensionDef(key,
                JsonUtils.getInt(json, "id"),
                JsonUtils.getString(json, "suffix", "DIM_" + key.getPath()),
                JsonUtils.getBoolean(json, "keepLoaded", false),
                type,
                JsonUtils.getString(terrain, "generatorOptions", ""),
                JsonUtils.getBoolean(terrain, "structures", true),
                source,
                JsonUtils.getString(biomes, "biome", "minecraft:plains"),
                JsonUtils.getBoolean(sky, "hasSkyLight", true),
                JsonUtils.getBoolean(sky, "surfaceWorld", true),
                JsonUtils.getBoolean(sky, "respawn", true),
                JsonUtils.getBoolean(sky, "spawning", true),
                JsonUtils.getInt(sky, "cloudHeight", 128),
                JsonUtils.getInt(sky, "groundLevel", 63),
                JsonUtils.getFloat(sky, "movementFactor", 1.0F),
                fog.isEmpty() ? -1 : ContentTypes.color(fog, key.toString()),
                skyColor.isEmpty() ? -1 : ContentTypes.color(skyColor, key.toString()),
                JsonUtils.getInt(sky, "fixedTime", -1),
                JsonUtils.getBoolean(sky, "sunriseColors", true),
                JsonUtils.getBoolean(sky, "nether", false),
                JsonUtils.getBoolean(sky, "beds", true),
                JsonUtils.getBoolean(sky, "waterVaporizes", false),
                JsonUtils.getBoolean(sky, "showFog", false),
                Math.max(0.0F, Math.min(1.0F, JsonUtils.getFloat(sky, "ambientLight", 0.0F))),
                JsonUtils.getFloat(sky, "starBrightness", -1.0F),
                cloudColor.isEmpty() ? -1 : ContentTypes.color(cloudColor, key.toString()),
                JsonUtils.getInt(sky, "respawnDimension", Integer.MIN_VALUE),
                JsonUtils.getBoolean(sky, "renderSky", true),
                JsonUtils.getBoolean(sky, "renderClouds", true),
                JsonUtils.getBoolean(sky, "renderWeather", true),
                gameRules(key, json),
                strings(json, "requires"));
    }

    private static List<String> behaviors(ResourceLocation key, JsonObject json) {
        List<String> found = new ArrayList<>();
        for (String raw : strings(json, "behavesAs")) {
            String name = ContentSpawning.normalise(raw);
            if (!ContentSpawning.known(name)) {
                ContentLog.LOGGER.error("Block {} says it behaves as '{}', which is not one of {}, ignoring it", key, raw, ContentSpawning.describe());
                continue;
            }
            found.add(name);
        }
        return Collections.unmodifiableList(found);
    }

    private static Map<String, Boolean> structures(ResourceLocation key, JsonObject json) {
        Map<String, Boolean> settings = new LinkedHashMap<>();
        if (!json.has("structures")) { return Collections.unmodifiableMap(settings); }

        JsonObject entry = JsonUtils.getJsonObject(json, "structures");
        for (Map.Entry<String, JsonElement> value : entry.entrySet()) {
            String name = ContentStructures.normalise(value.getKey());
            if (!ContentStructures.known(name)) {
                ContentLog.LOGGER.error("World template {} names structure '{}', which is not one of {}, ignoring it", key, value.getKey(), ContentStructures.describe());
                continue;
            }
            settings.put(name, value.getValue().getAsBoolean());
        }
        return Collections.unmodifiableMap(settings);
    }

    @Nullable private static GrowthDef growth(JsonObject json) {
        if (!json.has("growth")) { return null; }

        JsonObject entry = JsonUtils.getJsonObject(json, "growth");
        return new GrowthDef(Math.max(1, JsonUtils.getInt(entry, "maxHeight", 3)),
                Math.max(1, Math.min(16, JsonUtils.getInt(entry, "stages", 16))),
                strings(entry, "soil"),
                JsonUtils.getBoolean(entry, "needsWater", false),
                Math.max(1, JsonUtils.getInt(entry, "waterRange", 1)),
                JsonUtils.getBoolean(entry, "needsSky", false),
                JsonUtils.getBoolean(entry, "damage", false),
                JsonUtils.getFloat(entry, "damageAmount", 1.0F),
                JsonUtils.getBoolean(entry, "breaksNeighbors", false),
                Math.max(0, JsonUtils.getInt(entry, "spread", 0)),
                JsonUtils.getString(entry, "drop", ""),
                Math.max(1, JsonUtils.getInt(entry, "dropCount", 1)));
    }

    @Nullable private static SaplingDef sapling(JsonObject json) {
        if (!json.has("sapling")) { return null; }

        JsonObject entry = JsonUtils.getJsonObject(json, "sapling");
        return new SaplingDef(
                strings(entry, "soil"),
                Math.max(1, JsonUtils.getInt(entry, "stages", 2)),
                Math.max(1, JsonUtils.getInt(entry, "chance", 7)),
                Math.max(0, JsonUtils.getInt(entry, "light", 9)),
                JsonUtils.getString(entry, "structure", ""),
                JsonUtils.getString(entry, "log", "minecraft:log"),
                JsonUtils.getString(entry, "leaves", "minecraft:leaves"),
                Math.max(1, JsonUtils.getInt(entry, "height", 4)),
                JsonUtils.getBoolean(entry, "vines", false));
    }

    private static BlockVariant blockVariant(ResourceLocation key, String name, int meta, JsonObject json) {
        List<DropDef> drops = new ArrayList<>();
        if (json.has("drops")) {
            JsonArray array = JsonUtils.getJsonArray(json, "drops");
            for (JsonElement element : array) {
                if (!element.isJsonObject()) { continue; }
                DropDef drop = drop(key, name, element.getAsJsonObject());
                if (drop != null) { drops.add(drop); }
            }
        }

        return new BlockVariant(name, meta,
                ContentTypes.rarity(JsonUtils.getString(json, "rarity", "COMMON"), key + " " + name),
                JsonUtils.getInt(json, "maxSize", 64),
                strings(json),
                JsonUtils.getFloat(json, "hardness", 1.0F),
                JsonUtils.getFloat(json, "resistance", 5.0F),
                JsonUtils.getInt(json, "harvestLevel", 0),
                JsonUtils.getInt(json, "light", 0),
                portal(json),
                Collections.unmodifiableList(drops), false);
    }

    @Nullable private static DropDef drop(ResourceLocation key, String name, JsonObject json) {
        String block = JsonUtils.getString(json, "block", "");
        if (block.isEmpty()) {
            ContentLog.LOGGER.error("A drop for '{}' in {} has no block, skipping it", name, key);
            return null;
        }

        int[] chances = NO_CHANCE;
        if (json.has("bonusChance")) {
            JsonArray array = JsonUtils.getJsonArray(json, "bonusChance");
            chances = new int[array.size()];
            for (int i = 0; i < array.size(); i++) { chances[i] = array.get(i).getAsInt(); }
        }

        return new DropDef(new ResourceLocation(block),
                JsonUtils.getInt(json, "meta", 0),
                JsonUtils.getInt(json, "amount", 1),
                JsonUtils.getBoolean(json, "guaranteed", true),
                chances);
    }

    @Nullable public static ItemDef item(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Item definition {} is empty, ignoring it", key);
            return null;
        }

        JsonObject variants = JsonUtils.getJsonObject(json, "variants", new JsonObject());
        Map<Integer, ItemVariant> byMeta = new LinkedHashMap<>();
        List<ItemVariant> visible = new ArrayList<>();

        for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
            String name = entry.getKey();
            if (!entry.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("Item variant '{}' in {} is not an object, skipping it", name, key);
                continue;
            }
            JsonObject variant = entry.getValue().getAsJsonObject();
            int meta = JsonUtils.getInt(variant, "meta", -1);
            if (meta < 0 || meta > Short.MAX_VALUE) {
                ContentLog.LOGGER.error("Item variant '{}' in {} has meta {}, which is outside 0-{}, skipping it", name, key, meta, (int) Short.MAX_VALUE);
                continue;
            }
            if (byMeta.containsKey(meta)) {
                ContentLog.LOGGER.error("Item variants '{}' and '{}' in {} both claim meta {}, skipping '{}'", byMeta.get(meta).name, name, key, meta, name);
                continue;
            }
            ItemVariant parsed = new ItemVariant(name, meta,
                    ContentTypes.rarity(JsonUtils.getString(variant, "rarity", "COMMON"), key + " " + name),
                    JsonUtils.getInt(variant, "maxSize", 64),
                    strings(variant),
                    JsonUtils.getInt(variant, "healAmount", 0),
                    JsonUtils.getFloat(variant, "saturation", 0.0F),
                    potion(variant));
            byMeta.put(meta, parsed);
            visible.add(parsed);
        }

        String type = JsonUtils.getString(json, "type", ContentItemTypes.DEFAULT);
        ContentItemTypes.get(type, key);

        return new ItemDef(key, type,
                JsonUtils.getString(json, "creativeTab", ""),
                JsonUtils.getBoolean(json, "alwaysEdible", false),
                Collections.unmodifiableMap(byMeta), Collections.unmodifiableList(visible), strings(json, "requires"),
                Math.max(1, JsonUtils.getInt(json, "useDuration", 32)),
                JsonUtils.getBoolean(json, "eat", false),
                JsonUtils.getString(json, "container", ""),
                JsonUtils.getString(json, "material", ""),
                JsonUtils.getString(json, "toolClass", ""),
                JsonUtils.getString(json, "slot", ""),
                JsonUtils.getString(json, "crop", ""),
                JsonUtils.getString(json, "soil", "minecraft:farmland"),
                strings(json, "potionTypes"));
    }

    @Nullable public static FluidDef fluid(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Fluid definition {} is empty, ignoring it", key);
            return null;
        }

        String name = JsonUtils.getString(json, "name", key.getPath());
        JsonObject block = JsonUtils.getJsonObject(json, "block", new JsonObject());
        boolean createBlock = json.has("block");

        return new FluidDef(key, name,
                ContentTypes.color(JsonUtils.getString(json, "color", ""), key.toString()),
                new ResourceLocation(JsonUtils.getString(json, "still", DEFAULT_STILL)),
                new ResourceLocation(JsonUtils.getString(json, "flow", DEFAULT_FLOW)),
                JsonUtils.getInt(json, "temperature", 300),
                JsonUtils.getInt(json, "density", 1000),
                JsonUtils.getInt(json, "viscosity", 1000),
                JsonUtils.getInt(json, "luminosity", 0),
                JsonUtils.getBoolean(json, "gaseous", false),
                JsonUtils.getBoolean(json, "bucket", true),
                createBlock,
                ContentTypes.material(JsonUtils.getString(block, "material", "water"), key.toString()),
                JsonUtils.getString(json, "creativeTab", ""),
                JsonUtils.getInt(block, "flammability", 0),
                JsonUtils.getInt(block, "fireSpread", 0),
                JsonUtils.getInt(block, "quantaPerBlock", 0),
                strings(block, "potions"), strings(json, "requires"));
    }

    @Nullable public static WorldgenDef worldgen(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
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
        minHeight = Math.max(0, minHeight);
        maxHeight = Math.max(minHeight, maxHeight);

        return new WorldgenDef(key, new ResourceLocation(block),
                JsonUtils.getInt(json, "meta", 0),
                weights(json),
                amount(json, "size", 8, 1),
                amount(json, "attempts", 8, 0),
                minHeight, maxHeight,
                replaces(key, json),
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
                JsonUtils.getFloat(json, "maxRainfall", 100.0F));
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
                Math.max(1, Math.min(8, JsonUtils.getInt(entry, "smoothness", 2))),
                Math.max(1, JsonUtils.getInt(entry, "veinHeight", Math.max(1, maxHeight - minHeight))),
                Math.max(1, JsonUtils.getInt(entry, "veinDiameter", 12)),
                Math.max(1, Math.min(100, JsonUtils.getInt(entry, "verticalDensity", 16))),
                Math.max(1, Math.min(100, JsonUtils.getInt(entry, "horizontalDensity", 32))),
                offsetMin,
                Math.max(offsetMin, JsonUtils.getInt(entry, "offsetMax", offsetMin)),
                JsonUtils.getBoolean(entry, "ceiling", false));
    }

    private static AmountDef amount(JsonObject json, String key, int fallback, int floor) {
        if (!json.has(key)) { return AmountDef.of(Math.max(floor, fallback)); }

        JsonElement element = json.get(key);
        if (!element.isJsonObject()) { return AmountDef.of(Math.max(floor, element.getAsInt())); }

        JsonObject range = element.getAsJsonObject();
        int least = Math.max(floor, JsonUtils.getInt(range, "min", fallback));
        return new AmountDef(least, Math.max(least, JsonUtils.getInt(range, "max", least)));
    }

    @Nullable public static GateDef gate(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) { return null; }

        String scope = JsonUtils.getString(json, "scope", GateDef.PLAYER).trim().toLowerCase(Locale.ROOT);
        if (!GateDef.PLAYER.equals(scope) && !GateDef.GLOBAL.equals(scope)) {
            ContentLog.LOGGER.error("Gate {} asks for scope '{}', which is not {} or {}, using {}", key, scope, GateDef.PLAYER, GateDef.GLOBAL, GateDef.PLAYER);
            scope = GateDef.PLAYER;
        }

        JsonObject unlock = json.has("unlock") ? JsonUtils.getJsonObject(json, "unlock") : new JsonObject();
        return new GateDef(key,
                JsonUtils.getInt(json, "dimension"),
                JsonUtils.getString(json, "name", key.getPath()),
                GateDef.GLOBAL.equals(scope),
                JsonUtils.getBoolean(json, "open", false),
                JsonUtils.getString(unlock, "craft", ""),
                JsonUtils.getString(unlock, "consume", ""),
                Math.max(1, JsonUtils.getInt(unlock, "consumeCount", 1)),
                JsonUtils.getString(unlock, "hold", ""),
                JsonUtils.getString(unlock, "advancement", ""),
                JsonUtils.getString(unlock, "killed", ""),
                Math.max(1, JsonUtils.getInt(unlock, "killedCount", 1)),
                JsonUtils.getString(unlock, "killedDrops", ""),
                strings(json, "portalBlocks"),
                JsonUtils.getString(json, "blockedMessage", "You need %item% to enter %dim%"),
                JsonUtils.getString(json, "unlockedMessage", "%dim% is now open"),
                JsonUtils.getBoolean(json, "safeReturn", false),
                strings(json, "requires"));
    }

    @Nullable public static EntityVariantDef entityVariant(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Entity file {} is empty, ignoring it", key);
            return null;
        }

        String base = JsonUtils.getString(json, "entity", "");
        if (base.isEmpty()) {
            ContentLog.LOGGER.error("Entity variant {} names no entity to copy, ignoring it", key);
            return null;
        }

        Map<String, Double> attributes = new LinkedHashMap<>();
        if (json.has("attributes")) {
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "attributes").entrySet()) {
                if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                    ContentLog.LOGGER.error("Entity variant {} sets attribute '{}' to something that is not a number, ignoring it", key, entry.getKey());
                    continue;
                }
                attributes.put(entry.getKey(), entry.getValue().getAsDouble());
            }
        }

        Map<String, String> equipment = new LinkedHashMap<>();
        if (json.has("equipment")) {
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "equipment").entrySet()) {
                if (!entry.getValue().isJsonPrimitive()) {
                    ContentLog.LOGGER.error("Entity variant {} sets slot '{}' to something that is not an item name, ignoring it", key, entry.getKey());
                    continue;
                }
                equipment.put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        List<SpawnEntryDef> spawns = new ArrayList<>();
        if (json.has("spawns")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "spawns")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("A spawn entry in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject entry = element.getAsJsonObject();
                spawns.add(new SpawnEntryDef(JsonUtils.getString(entry, "creatureType", "creature"), "",
                        Math.max(1, JsonUtils.getInt(entry, "weight", 8)),
                        Math.max(1, JsonUtils.getInt(entry, "min", 1)),
                        Math.max(1, JsonUtils.getInt(entry, "max", 4))));
            }
        }

        JsonObject sounds = json.has("sounds") ? JsonUtils.getJsonObject(json, "sounds") : new JsonObject();

        Map<String, Integer> effects = new LinkedHashMap<>();
        if (json.has("effects")) {
            for (JsonElement element : JsonUtils.getJsonArray(json, "effects")) {
                if (!element.isJsonObject()) {
                    ContentLog.LOGGER.error("An effect in {} is not an object, skipping it", key);
                    continue;
                }
                JsonObject effect = element.getAsJsonObject();
                effects.put(JsonUtils.getString(effect, "potion", ""), Math.max(0, JsonUtils.getInt(effect, "amplifier", 0)));
            }
        }

        Map<String, Float> priorities = new LinkedHashMap<>();
        if (json.has("pathPriorities")) {
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "pathPriorities").entrySet()) {
                if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                    ContentLog.LOGGER.error("Entity variant {} sets path priority '{}' to something that is not a number, ignoring it", key, entry.getKey());
                    continue;
                }
                priorities.put(entry.getKey(), entry.getValue().getAsFloat());
            }
        }

        JsonObject egg = json.has("egg") && json.get("egg").isJsonObject() ? JsonUtils.getJsonObject(json, "egg") : null;
        boolean wantsEgg = !json.has("egg") || egg != null || JsonUtils.getBoolean(json, "egg", true);

        return new EntityVariantDef(key, new ResourceLocation(base),
                JsonUtils.getString(json, "name", ""),
                JsonUtils.getString(json, "texture", ""),
                JsonUtils.getString(json, "lootTable", ""),
                JsonUtils.getString(json, "profession", ""),
                Math.max(0, JsonUtils.getInt(json, "career", 0)),
                JsonUtils.getBoolean(json, "baby", false),
                sounds.has("ambient") ? JsonUtils.getString(sounds, "ambient", "") : "",
                sounds.has("hurt") ? JsonUtils.getString(sounds, "hurt", "") : "",
                sounds.has("death") ? JsonUtils.getString(sounds, "death", "") : "",
                strings(json, "immuneTo"),
                Math.max(0.1F, JsonUtils.getFloat(json, "jumpMultiplier", 1.0F)),
                Math.max(0.0F, JsonUtils.getFloat(json, "fallDamage", 1.0F)),
                Math.max(0.0F, JsonUtils.getFloat(json, "soundVolume", 1.0F)),
                Math.max(0.1F, JsonUtils.getFloat(json, "soundPitch", 1.0F)),
                Math.max(0.0F, JsonUtils.getFloat(json, "waterSlowdown", 0.8F)),
                Math.max(-1, JsonUtils.getInt(json, "experience", -1)),
                Math.max(-1, JsonUtils.getInt(json, "maxFallHeight", -1)),
                Math.max(0.0F, JsonUtils.getFloat(json, "absorption", 0.0F)),
                JsonUtils.getString(json, "creatureAttribute", ""),
                JsonUtils.getBoolean(json, "breathesUnderwater", false),
                JsonUtils.getBoolean(json, "swims", false),
                JsonUtils.getBoolean(json, "amphibious", false),
                JsonUtils.getBoolean(json, "despawns", true),
                JsonUtils.getBoolean(json, "noAI", false),
                JsonUtils.getBoolean(json, "leftHanded", false),
                JsonUtils.getBoolean(json, "fireproof", false),
                JsonUtils.getBoolean(json, "invulnerable", false),
                JsonUtils.getBoolean(json, "glowing", false),
                JsonUtils.getBoolean(json, "invisible", false),
                Math.max(0.0F, Math.min(1.0F, JsonUtils.getFloat(json, "dropChance", 0.0F))),
                Math.max(0.05F, JsonUtils.getFloat(json, "scale", 1.0F)),
                Math.max(0.05F, JsonUtils.getFloat(json, "angryScale", JsonUtils.getFloat(json, "scale", 1.0F))),
                JsonUtils.getBoolean(json, "leashable", false),
                JsonUtils.getBoolean(json, "steerable", false),
                Math.max(0.0F, JsonUtils.getFloat(json, "width", 0.0F)),
                Math.max(0.0F, JsonUtils.getFloat(json, "height", 0.0F)),
                effects, priorities,
                wantsEgg,
                egg == null || !egg.has("primary") ? -1 : ContentTypes.color(JsonUtils.getString(egg, "primary", ""), key + " egg primary"),
                egg == null || !egg.has("secondary") ? -1 : ContentTypes.color(JsonUtils.getString(egg, "secondary", ""), key + " egg secondary"),
                Math.max(1, JsonUtils.getInt(json, "trackingRange", 80)),
                Math.max(1, JsonUtils.getInt(json, "trackingFrequency", 3)),
                JsonUtils.getBoolean(json, "trackVelocity", true),
                attributes,
                JsonUtils.getBoolean(json, "hostile", false),
                JsonUtils.getBoolean(json, "passive", false),
                strings(json, "targets"),
                JsonUtils.getBoolean(json, "persistent", false),
                JsonUtils.getBoolean(json, "silent", false),
                JsonUtils.getBoolean(json, "picksUpLoot", false),
                JsonUtils.getBoolean(json, "hideArmor", false),
                JsonUtils.getBoolean(json, "showName", false),
                equipment, spawns,
                strings(json, "biomes"), strings(json, "biomeTypes"), strings(json, "requires"));
    }

    @Nullable public static VillageDef village(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
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
                strings(json, "crops"),
                JsonUtils.getString(json, "edge", "minecraft:log"),
                JsonUtils.getString(json, "soil", "minecraft:farmland"),
                JsonUtils.getBoolean(json, "water", true),
                Math.max(1, JsonUtils.getInt(json, "rowWidth", 2)),
                structure,
                JsonUtils.getString(json, "ground", "minecraft:dirt"),
                Math.max(1, Math.min(100, JsonUtils.getInt(json, "integrity", 100))),
                Math.max(0, JsonUtils.getInt(json, "villagers", 0)),
                JsonUtils.getString(json, "villagerEntity", ""),
                JsonUtils.getInt(json, "villagerX", 1),
                JsonUtils.getInt(json, "villagerY", 1),
                JsonUtils.getInt(json, "villagerZ", 1),
                strings(json, "requires"));
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

        return new ShapeDef(type,
                amount(entry, "radius", ShapeDef.BELT.equals(type) ? 32 : 6, 0),
                amount(entry, "height", ShapeDef.GEODE.equals(type) ? 8 : ShapeDef.TREE.equals(type) ? 5 : 1, 0),
                amount(entry, "width", 12, 3),
                plane,
                JsonUtils.getBoolean(entry, "slim", false),
                JsonUtils.getString(entry, "outline", ""),
                JsonUtils.getString(entry, "fill", ""),
                surface(entry),
                amount(entry, "stackHeight", 1, 1),
                JsonUtils.getBoolean(entry, "seeSky", true),
                JsonUtils.getBoolean(entry, "checkStay", true),
                JsonUtils.getInt(entry, "scatterX", 8),
                JsonUtils.getInt(entry, "scatterY", 4),
                JsonUtils.getInt(entry, "scatterZ", 8),
                JsonUtils.getString(entry, "log", ""),
                JsonUtils.getString(entry, "leaves", ""),
                JsonUtils.getBoolean(entry, "vines", false),
                JsonUtils.getBoolean(entry, "hanging", false),
                JsonUtils.getString(entry, "structure", ""),
                Math.max(1, Math.min(100, JsonUtils.getInt(entry, "integrity", 100))),
                Math.max(1, JsonUtils.getInt(entry, "rarity", 400)),
                JsonUtils.getBoolean(entry, "rarityIsPerChunk", false));
    }

    private static List<String> surface(JsonObject entry) {
        if (!entry.has("surface")) { return Collections.emptyList(); }

        JsonElement element = entry.get("surface");
        if (!element.isJsonArray()) { return Collections.singletonList(element.getAsString()); }

        return strings(entry, "surface");
    }

    private static List<BlockMatchDef> replaces(ResourceLocation key, JsonObject json) {
        if (!json.has("replace")) { return Collections.singletonList(match(key, "minecraft:stone")); }

        JsonElement element = json.get("replace");
        if (!element.isJsonArray()) { return Collections.singletonList(match(key, element)); }

        List<BlockMatchDef> values = new ArrayList<>();
        for (JsonElement name : element.getAsJsonArray()) { values.add(match(key, name)); }
        return values.isEmpty() ? Collections.singletonList(match(key, "minecraft:stone")) : Collections.unmodifiableList(values);
    }

    private static BlockMatchDef match(ResourceLocation key, JsonElement element) {
        if (!element.isJsonObject()) { return match(key, element.getAsString()); }

        JsonObject entry = element.getAsJsonObject();
        return new BlockMatchDef(new ResourceLocation(JsonUtils.getString(entry, "block", "minecraft:stone")), JsonUtils.getInt(entry, "meta", -1), blockProperties(entry));
    }

    private static BlockMatchDef match(ResourceLocation key, String name) {
        String[] parts = name.split(":");
        if (parts.length < 3) { return new BlockMatchDef(new ResourceLocation(name), -1, Collections.emptyMap()); }

        ResourceLocation block = new ResourceLocation(parts[0] + ":" + parts[1]);
        try { return new BlockMatchDef(block, Integer.parseInt(parts[2]), Collections.emptyMap()); }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Block metadata '{}' in {} is not a number, using 0", parts[2], key);
            return new BlockMatchDef(block, 0, Collections.emptyMap());
        }
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
                    blockProperties(entry)));
        }
        return Collections.unmodifiableList(values);
    }

    private static Map<String, String> blockProperties(JsonObject entry) {
        if (!entry.has("properties")) { return Collections.emptyMap(); }

        JsonObject object = JsonUtils.getJsonObject(entry, "properties");
        Map<String, String> values = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> property : object.entrySet()) {
            if (!property.getValue().isJsonPrimitive()) { continue; }

            values.put(property.getKey(), property.getValue().getAsString());
        }
        return Collections.unmodifiableMap(values);
    }

    private static List<Integer> integers(JsonObject json) {
        if (!json.has("dimensions")) { return Collections.emptyList(); }
        JsonArray array = JsonUtils.getJsonArray(json, "dimensions");
        List<Integer> values = new ArrayList<>(array.size());
        for (JsonElement element : array) { values.add(element.getAsInt()); }
        return Collections.unmodifiableList(values);
    }

    private static BlockRenderLayer renderLayer(String value, String context) {
        if (value.isEmpty()) { return BlockRenderLayer.SOLID; }

        try { return BlockRenderLayer.valueOf(value.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) {
            ContentLog.LOGGER.error("Unknown renderLayer '{}' in {}, using solid", value, context);
            return BlockRenderLayer.SOLID;
        }
    }

    @Nullable private static AxisAlignedBB bounds(ResourceLocation key, JsonObject json) {
        if (!json.has("bounds")) { return null; }

        JsonArray array = JsonUtils.getJsonArray(json, "bounds");
        if (array.size() != 6) {
            ContentLog.LOGGER.error("Bounds in {} need six numbers, minX minY minZ maxX maxY maxZ, ignoring them", key);
            return null;
        }

        double[] v = new double[6];
        for (int i = 0; i < 6; i++) { v[i] = array.get(i).getAsDouble(); }
        return new AxisAlignedBB(v[0], v[1], v[2], v[3], v[4], v[5]);
    }

    @Nullable private static String potion(JsonObject json) {
        if (!json.has("potion") || json.get("potion").isJsonNull()) { return null; }
        String value = JsonUtils.getString(json, "potion");
        return value.trim().isEmpty() ? null : value;
    }

    private static List<String> strings(JsonObject json) { return strings(json, "oreDict"); }

    public static List<String> strings(JsonObject json, String member) {
        if (!json.has(member)) { return Collections.emptyList(); }
        JsonArray array = JsonUtils.getJsonArray(json, member);
        List<String> values = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            String value = element.getAsString();
            if (!value.isEmpty()) { values.add(value); }
        }
        return Collections.unmodifiableList(values);
    }

    public static String placeholderName(int meta, int digits) {
        StringBuilder builder = new StringBuilder(PLACEHOLDER);
        String number = Integer.toString(meta);
        for (int i = number.length(); i < digits; i++) { builder.append('0'); }
        return builder.append(number).toString();
    }
}
