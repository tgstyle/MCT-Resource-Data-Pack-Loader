package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BellDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatchDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockVariant;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.DropDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.FluidDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowthDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SaplingDef;
import mctmods.resourcedatapackloader.common.pack.definitions.effects.PotionEffectDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.ExposureBandDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.ExposureDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.ExposureLevelDef;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.SpawnEntryDef;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.ItemVariant;
import mctmods.resourcedatapackloader.common.pack.definitions.items.LocatorDef;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortalDef;
import mctmods.resourcedatapackloader.common.pack.definitions.scripts.ScriptRef;
import mctmods.resourcedatapackloader.common.rules.ContentSpawning;
import mctmods.resourcedatapackloader.common.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.common.types.ContentItemTypes;
import mctmods.resourcedatapackloader.common.types.ContentTypes;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;
import mctmods.resourcedatapackloader.core.util.json.Json;
import static mctmods.resourcedatapackloader.core.util.json.Json.strings;
import mctmods.resourcedatapackloader.core.util.math.IntRange;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public final class ContentParser {
    public static final String BLOCKS = "blocks";
    public static final String PLACEHOLDER = "open";
    public static final String DEFAULT_STILL = "minecraft:blocks/water_still";
    public static final String DEFAULT_FLOW = "minecraft:blocks/water_flow";
    public static final int DEFAULT_EFFECT_TICKS = 3600;
    static final String STONE = "minecraft:stone";
    private static final int[] NO_CHANCE = new int[0];
    private static final List<String> PORTAL_LANDINGS = Collections.unmodifiableList(Arrays.asList("surface", "same"));
    private static final List<String> PORTAL_RETURNS = Collections.unmodifiableList(Arrays.asList("fixed", "origin"));
    private static final List<String> BELL_KEYS = Collections.unmodifiableList(Arrays.asList("swing", "sound", "resonateSound"));
    private static final List<String> EXP_DROP_KEYS = Collections.unmodifiableList(Arrays.asList("min", "max"));
    private static final List<String> GROWTH_KEYS = Collections.unmodifiableList(Arrays.asList("maxHeight", "stages", "soil", "needsWater", "waterRange", "needsSky", "damage", "damageAmount", "breaksNeighbors", "spread", "drop", "dropCount"));
    private static final List<String> PORTAL_KEYS = Collections.unmodifiableList(Arrays.asList("dimension", "returnDimension", "gate", "cooldown", "platform", "platformBlock", "sound", "owned", "walkIn", "landing", "torches", "useRange", "mounted", "returnTo"));
    private static final List<String> SAPLING_KEYS = Collections.unmodifiableList(Arrays.asList("soil", "stages", "chance", "light", "structure", "structures", "log", "leaves", "height", "trees", "steps", "blocked", "growBy", "stepTicks", "catchUp", "catchUpLimit", "bonemeal", "bonemealTicks", "bonemealChecks", "biomes", "needsSky", "needsWater", "waterRange"));
    private static final List<String> BLOCK_VARIANT_KEYS = Collections.unmodifiableList(Arrays.asList("meta", "rarity", "maxSize", "oreDict", "hardness", "resistance", "harvestLevel", "light", "portal", "drops"));
    private static final List<String> ITEM_VARIANT_KEYS = Collections.unmodifiableList(Arrays.asList("meta", "rarity", "maxSize", "oreDict", "healAmount", "saturation", "potion"));
    private static final List<String> MATCH_KEYS = Collections.unmodifiableList(Arrays.asList("block", "meta", "properties"));
    private static final List<String> DROP_KEYS = Collections.unmodifiableList(Arrays.asList("block", "entity", "meta", "amount", "chance", "weight", "bonusChance", "guaranteed"));

    private ContentParser() {}

    @Nullable public static BlockDef block(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "block definition");
        if (json == null) { return null; }
        ContentParserContainers.blockKeys(json);
        String type = Json.getString(json, "type", ContentBlockTypes.DEFAULT);
        Material material = ContentTypes.material(Json.getString(json, "material", "banner".equals(type) ? "wood" : "rock"), key.toString());
        MapColor mapColor = ContentTypes.mapColor(Json.getString(json, "mapColor", ""), material.getMaterialMapColor(), key.toString());
        SoundType soundType = ContentTypes.soundType(Json.getString(json, "soundType", ""), null, key.toString());
        JsonObject exp = Json.getJsonObject(json, "expDrop", new JsonObject());
        Json.unknownKeys(exp, "expDrop", EXP_DROP_KEYS);
        int expMin = Json.getInt(exp, "min", 0);
        int expMax = Json.getInt(exp, "max", 0);
        int maxVariants = ContentBlockTypes.get(type, key).maxVariants();
        boolean opaque = Json.getBoolean(json, "opaque", !ContentParserContainers.chested(json) && !ContentBlockTypes.plant(type));
        JsonObject variants = Json.getJsonObject(json, "variants", new JsonObject());
        BlockVariant[] byMeta = new BlockVariant[maxVariants];
        List<BlockVariant> visible = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
            String name = entry.getKey();
            if (!entry.getValue().isJsonObject()) {
                ContentLog.LOGGER.error("Block variant '{}' in {} is not an object, skipping it", name, key);
                continue;
            }
            JsonObject variant = entry.getValue().getAsJsonObject();
            Json.unknownKeys(variant, "variants", BLOCK_VARIANT_KEYS);
            int meta = Json.getInt(variant, "meta", -1);
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
        int maxAge = MathHelper.clamp(Json.getInt(json, "maxAge", 7), 1, 7);
        return new BlockDef(key, type, material, mapColor, soundType,
                Json.getString(json, "creativeTab", ""),
                Json.getString(json, "harvestTool", "pickaxe"),
                Json.getInt(json, "harvestToolLevel", 0),
                Json.getBoolean(json, "silkHarvest", true),
                expMin, expMax,
                Json.atLeast(json, "explosionResistanceDivisor", 1.0F, 0.01F),
                byMeta, Collections.unmodifiableList(visible), strings(json, "requires"),
                renderLayer(Json.getString(json, "renderLayer", ""), type, opaque, key.toString()),
                opaque,
                Json.getBoolean(json, "fullCube", opaque),
                Json.getInt(json, "lightOpacity", opaque ? 255 : 0),
                Json.getFloat(json, "slipperiness", 0.6F),
                bounds(key, json),
                Json.getInt(json, "flammability", 0),
                Json.getInt(json, "fireSpread", 0),
                Json.getString(json, "modelBlock", STONE),
                Json.getInt(json, "modelMeta", 0),
                "item".equals(Json.getString(json, "itemModel", "state")),
                Json.getString(json, "particle", BlockDef.PARTICLE_FLAME).toLowerCase(Locale.ROOT),
                Json.getBoolean(json, "smoke", true),
                ContentTypes.color(Json.getString(json, "particleColor", "FFFFFF"), key.toString()),
                Json.getString(json, "seed", ""),
                Json.getString(json, "produce", ""),
                maxAge,
                sapling(json),
                portal(json),
                growth(json),
                strings(json, "plantTypes").stream().map(Settings::lower).collect(Collectors.toList()),
                behaviors(key, json),
                Json.getString(json, "tint", ""),
                Json.getString(json, "leafSapling", ""),
                MathHelper.clamp(Json.getInt(json, "leafSaplingChance", 5), 0, 100),
                Json.location(json, "opensWith"),
                Json.getString(json, "openSound", "").trim(),
                ContentParserContainers.container(key, json),
                bell(json),
                ContentParserSeasons.sensor(json),
                Collections.unmodifiableList(strings(json, "spreadsOnto")),
                Json.getBoolean(json, "needsOxygen", true),
                ContentParserPortways.portway(json),
                ContentParserWeights.weight(json, key),
                ContentParserCrops.tall(json, key, maxAge),
                ContentParserShields.spawns(json, key),
                ContentParserShields.weather(json),
                Json.getBoolean(json, "tagged", true));
    }

    @Nullable private static BellDef bell(JsonObject json) {
        JsonObject held = Json.optionalObject(json, "bell");
        if (held == null) { return null; }
        Json.unknownKeys(held, "bell", BELL_KEYS);
        return new BellDef(Json.getBoolean(held, "swing", BellDef.PLAIN.swing),
                Json.getString(held, "sound", BellDef.PLAIN.sound).trim(),
                Json.getString(held, "resonateSound", BellDef.PLAIN.resonateSound).trim());
    }

    @Nullable private static PortalDef portal(JsonObject json) {
        if (!json.has("portal")) { return null; }
        JsonObject entry = Json.getJsonObject(json, "portal");
        Json.unknownKeys(entry, "portal", PORTAL_KEYS);
        return portal(entry, Json.getInt(entry, "dimension"), true, false);
    }

    static PortalDef portal(JsonObject entry, int dimension, boolean ownedDefault, boolean walkInDefault) {
        String owner = "Portal to dimension " + dimension;
        return new PortalDef(dimension,
                Json.getInt(entry, "returnDimension", 0),
                Json.getString(entry, "gate", ""),
                Json.atLeast(entry, "cooldown", 60, 0),
                Json.getBoolean(entry, "platform", true),
                Json.getString(entry, "platformBlock", ""),
                Json.getString(entry, "sound", ""),
                Json.getBoolean(entry, "owned", ownedDefault),
                Json.getBoolean(entry, "walkIn", walkInDefault),
                Json.choiceIs(entry, "landing", "surface", owner, PORTAL_LANDINGS, "same"),
                Json.getBoolean(entry, "torches", false),
                Json.atLeast(entry, "useRange", 0, 0),
                Json.getBoolean(entry, "mounted", true),
                Json.choiceIs(entry, "returnTo", "fixed", owner, PORTAL_RETURNS, "origin"));
    }

    public static List<PotionEffectDef> effects(ResourceLocation key, JsonArray array, int durationDefault, boolean particlesDefault) {
        List<PotionEffectDef> found = new ArrayList<>();
        for (JsonObject entry : Json.objectsOrSkip(array, "An effect in {} is not an object, skipping it", key)) {
            String potion = Json.getString(entry, "potion", "");
            if (potion.isEmpty()) {
                ContentLog.LOGGER.error("An effect in {} names no potion, skipping it", key);
                continue;
            }
            found.add(new PotionEffectDef(potion,
                    Json.atLeast(entry, "duration", durationDefault, durationDefault > 0 ? 1 : 0),
                    Json.atLeast(entry, "amplifier", 0, 0),
                    Json.getBoolean(entry, "ambient", false),
                    Json.getBoolean(entry, "showParticles", particlesDefault)));
        }
        return found;
    }

    public static List<BlockMatchDef> matches(ResourceLocation key, JsonObject json, String member) {
        List<BlockMatchDef> values = new ArrayList<>();
        if (!json.has(member)) { return values; }
        JsonElement element = json.get(member);
        if (!element.isJsonArray()) {
            values.add(match(key, element));
            return values;
        }
        for (JsonElement held : element.getAsJsonArray()) { values.add(match(key, held)); }
        return values;
    }

    private static List<String> behaviors(ResourceLocation key, JsonObject json) {
        List<String> found = new ArrayList<>();
        for (String raw : strings(json, "behavesAs")) {
            String name = ContentSpawning.normalize(raw);
            if (ContentSpawning.unknown(name)) {
                ContentLog.LOGGER.error("Block {} says it behaves as '{}', which is not one of {}, ignoring it", key, raw, ContentSpawning.describe());
                continue;
            }
            found.add(name);
        }
        return Collections.unmodifiableList(found);
    }

    @Nullable private static GrowthDef growth(JsonObject json) {
        if (!json.has("growth")) { return null; }
        JsonObject entry = Json.getJsonObject(json, "growth");
        Json.unknownKeys(entry, "growth", GROWTH_KEYS);
        return new GrowthDef(Json.atLeast(entry, "maxHeight", 3, 1),
                MathHelper.clamp(Json.getInt(entry, "stages", 16), 1, 16),
                strings(entry, "soil"),
                Json.getBoolean(entry, "needsWater", false),
                Json.atLeast(entry, "waterRange", 1, 1),
                Json.getBoolean(entry, "needsSky", false),
                Json.getBoolean(entry, "damage", false),
                Json.getFloat(entry, "damageAmount", 1.0F),
                Json.getBoolean(entry, "breaksNeighbors", false),
                Json.atLeast(entry, "spread", 0, 0),
                Json.getString(entry, "drop", ""),
                Json.atLeast(entry, "dropCount", 1, 1));
    }

    @Nullable private static SaplingDef sapling(JsonObject json) {
        if (!json.has("sapling")) { return null; }
        JsonObject entry = Json.getJsonObject(json, "sapling");
        Json.unknownKeys(entry, "sapling", SAPLING_KEYS);
        return new SaplingDef(
                strings(entry, "soil"),
                Json.atLeast(entry, "stages", 2, 1),
                Json.atLeast(entry, "chance", ContentParserTrees.SAPLING_CHANCE, 1),
                Json.atLeast(entry, "light", 9, 0),
                Json.getString(entry, "structure", ""),
                picks(entry, "structures", "structure"),
                Json.getString(entry, "log", "minecraft:log"),
                Json.getString(entry, "leaves", "minecraft:leaves"),
                Json.atLeast(entry, "height", 4, 1), ContentParserTrees.growth(entry));
    }

    private static BlockVariant blockVariant(ResourceLocation key, String name, int meta, JsonObject json) {
        List<DropDef> drops = new ArrayList<>();
        if (json.has("drops")) {
            JsonArray array = Json.getJsonArray(json, "drops");
            for (JsonElement element : array) {
                if (!element.isJsonObject()) { continue; }
                DropDef drop = drop(key, name, element.getAsJsonObject());
                if (drop != null) { drops.add(drop); }
            }
        }
        return new BlockVariant(name, meta,
                ContentTypes.rarity(Json.getString(json, "rarity", "COMMON"), key + " " + name),
                Json.getInt(json, "maxSize", 64),
                strings(json, "oreDict"),
                Json.getFloat(json, "hardness", 1.0F),
                Json.getFloat(json, "resistance", 5.0F),
                Json.getInt(json, "harvestLevel", 0),
                Json.getInt(json, "light", 0),
                portal(json),
                Collections.unmodifiableList(drops), false);
    }

    @Nullable private static DropDef drop(ResourceLocation key, String name, JsonObject json) {
        Json.unknownKeys(json, "drops", DROP_KEYS);
        String block = Json.getString(json, "block", "");
        String entity = Json.getString(json, "entity", "");
        if (block.isEmpty() && entity.isEmpty()) {
            ContentLog.LOGGER.error("A drop for '{}' in {} names neither a block nor an entity, skipping it", name, key);
            return null;
        }
        if (!block.isEmpty() && !entity.isEmpty()) { ContentLog.LOGGER.error("A drop for '{}' in {} names both block {} and entity {}, using the entity", name, key, block, entity); }
        int[] chances = NO_CHANCE;
        if (json.has("bonusChance")) {
            JsonArray array = Json.getJsonArray(json, "bonusChance");
            chances = new int[array.size()];
            for (int i = 0; i < array.size(); i++) { chances[i] = Json.asInt(array.get(i), "bonusChance", 0); }
        }
        boolean guaranteed = Json.getBoolean(json, "guaranteed", true);
        return new DropDef(IdNames.optional(block),
                IdNames.optional(entity),
                Json.getInt(json, "meta", 0),
                amount(json, "amount", 1, 0),
                MathHelper.clamp(Json.getInt(json, "chance", guaranteed ? 100 : 0), 0, 100),
                Json.atLeast(json, "weight", 0, 0),
                chances);
    }

    @Nullable public static ItemDef item(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "item definition");
        if (json == null) { return null; }
        if (json.has("item")) { return ContentParserDerived.item(key, json); }
        ContentParserContainers.itemKeys(json);
        JsonObject variants = Json.getJsonObject(json, "variants", new JsonObject());
        Map<Integer, ItemVariant> byMeta = Json.variants(variants, key, "Item", Short.MAX_VALUE, "", ITEM_VARIANT_KEYS, (name, meta, variant) -> new ItemVariant(name, meta,
                ContentTypes.rarity(Json.getString(variant, "rarity", "COMMON"), key + " " + name),
                Json.getInt(variant, "maxSize", 64),
                strings(variant, "oreDict"),
                Json.getInt(variant, "healAmount", 0),
                Json.getFloat(variant, "saturation", 0.0F),
                potion(variant)));
        List<ItemVariant> visible = new ArrayList<>(byMeta.values());
        String type = Json.getString(json, "type", ContentItemTypes.DEFAULT);
        ContentItemTypes.get(type, key);
        LocatorDef locator = ContentParserLocator.parse(key, json);
        return new ItemDef(key, type,
                Json.getString(json, "creativeTab", ""),
                Json.getBoolean(json, "alwaysEdible", false),
                Collections.unmodifiableMap(byMeta), Collections.unmodifiableList(visible), strings(json, "requires"),
                Json.atLeast(json, "useDuration", 32, 1),
                Json.getBoolean(json, "eat", false),
                ContentParserContainers.remainder(json),
                Json.getString(json, "material", ""),
                Json.getString(json, "toolClass", ""),
                Json.getString(json, "slot", ""),
                Json.getString(json, "crop", ""),
                Json.getString(json, "soil", "minecraft:farmland"),
                strings(json, "potionTypes"),
                Json.getFloat(json, "attackSpeed", Float.NaN),
                Json.atLeast(json, "cooldown", 0, 0),
                ContentParserContainers.holds(key, json),
                Json.getString(json, "rocket", ""),
                Json.getString(json, "rolls", "").trim(),
                Json.getString(json, "rollStore", "").trim(),
                Json.getBoolean(json, "passesTurn", false),
                locator,
                ContentParserContainers.bauble(json),
                Collections.unmodifiableList(effects(key, Json.getJsonArray(json, "wornEffects", new JsonArray()), 0, false)),
                Collections.unmodifiableList(ContentParserContainers.wornAttributes(key, json)),
                ContentParserWeapons.weapon(key, json),
                ContentParserPortways.item(json),
                Json.getBoolean(json, "sleepingBag", false),
                ContentParserContainers.upgrade(key, type, json),
                ContentParserWeights.weight(json, key),
                ContentParserFireworks.firework(key, json),
                ContentParserSeasons.clock(json),
                Json.getString(json, Json.RUNS, "").trim(),
                ScriptRef.read(key, json),
                ContentParserRecords.record(key, type, json));
    }

    @Nullable public static FluidDef fluid(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "fluid definition");
        if (json == null) { return null; }
        ContentParserFluids.fluidKeys(json);
        String name = Json.getString(json, "name", key.getPath());
        JsonObject asked = Json.objectOrFlag(json, "block");
        JsonObject block = asked != null ? asked : new JsonObject();
        boolean createBlock = asked != null;
        return new FluidDef(key, name,
                ContentTypes.color(Json.getString(json, "color", ""), key.toString()),
                new ResourceLocation(Json.getString(json, "still", DEFAULT_STILL)),
                new ResourceLocation(Json.getString(json, "flow", DEFAULT_FLOW)),
                Json.getInt(json, "temperature", 300),
                Json.getInt(json, "density", 1000),
                Json.getInt(json, "viscosity", 1000),
                Json.getInt(json, "luminosity", 0),
                Json.getBoolean(json, "gaseous", false),
                Json.getBoolean(json, "bucket", true),
                createBlock,
                ContentTypes.material(Json.getString(block, "material", "water"), key.toString()),
                Json.getString(json, "creativeTab", ""),
                Json.getInt(block, "flammability", 0),
                Json.getInt(block, "fireSpread", 0),
                Json.getInt(block, "quantaPerBlock", 0),
                strings(block, "potions"), strings(json, "requires"), ContentParserFluids.behavior(key, block));
    }

    @Nullable public static ExposureDef exposure(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "exposure definition");
        if (json == null) { return null; }
        List<ExposureLevelDef> levels = new ArrayList<>();
        for (JsonObject level : Json.objectsOrSkip(Json.getJsonArray(json, "levels", new JsonArray()), "A level in {} is not an object, skipping it", key)) {
            String effect = Json.getString(level, "effect", "");
            if (effect.isEmpty()) {
                ContentLog.LOGGER.error("A level in {} names no effect, skipping it", key);
                continue;
            }
            List<PotionEffectDef> extras = effects(key, Json.getJsonArray(level, "effects", new JsonArray()), 0, false);
            levels.add(new ExposureLevelDef(effect,
                    Json.atLeast(level, "damage", 0.0F, 0.0F),
                    Json.atLeast(level, "damageInterval", 160, 0),
                    Collections.unmodifiableList(extras),
                    Json.getString(level, "onEnter", "").trim()));
        }
        if (levels.isEmpty()) {
            ContentLog.LOGGER.error("Exposure {} has no usable levels, ignoring it", key);
            return null;
        }
        Map<ResourceLocation, Integer> blocks = leveled(key, json, "blocks", ResourceLocation::new);
        Map<ResourceLocation, Integer> items = leveled(key, json, "items", ResourceLocation::new);
        Map<Integer, Integer> dimensions = leveled(key, json, "dimensions", named -> dimension(key, named));
        Map<ResourceLocation, Integer> carriers = leveled(key, json, "carriers", ResourceLocation::new);
        Map<String, Integer> weather = leveled(key, json, "weather", named -> weather(key, named));
        Map<String, Integer> biomes = leveled(key, json, "biomes", named -> named);
        Map<String, Integer> environments = leveled(key, json, "environments", named -> named);
        Map<String, Integer> structures = leveled(key, json, "structures", named -> named);
        Map<ResourceLocation, Integer> caveRegions = leveled(key, json, "caveRegions", named -> new ResourceLocation(named.contains(":") ? named : key.getNamespace() + ":" + named));
        List<ExposureBandDef> heights = bands(key, json);
        if (blocks.isEmpty() && items.isEmpty() && dimensions.isEmpty() && carriers.isEmpty() && weather.isEmpty() && biomes.isEmpty() && environments.isEmpty() && structures.isEmpty() && caveRegions.isEmpty() && heights.isEmpty()) {
            ContentLog.LOGGER.error("Exposure {} names no blocks, items, dimensions, carriers, weather, biomes, environments, structures, caveRegions or heights, ignoring it", key);
            return null;
        }
        Set<ResourceLocation> catchers = new LinkedHashSet<>();
        for (String named : json.has("catchers") ? strings(json, "catchers") : Collections.singletonList(IdNames.PLAYER)) { catchers.add(new ResourceLocation(named.trim())); }
        Set<Integer> weatherDimensions = new LinkedHashSet<>();
        for (String named : strings(json, "weatherDimensions")) {
            Integer dimension = dimension(key, named.trim());
            if (dimension != null) { weatherDimensions.add(dimension); }
        }
        Set<ResourceLocation> immuneItems = new LinkedHashSet<>();
        for (String named : strings(json, "immuneItems")) { immuneItems.add(new ResourceLocation(named.trim())); }
        Set<String> immuneTeams = new LinkedHashSet<>();
        for (String named : strings(json, "immuneTeams")) {
            if (!named.trim().isEmpty()) { immuneTeams.add(named.trim()); }
        }
        return new ExposureDef(key,
                Json.atLeast(json, "scanInterval", 20, 1),
                Json.atLeast(json, "range", 10, 0),
                Json.getBoolean(json, "skipsCreative", true),
                Json.atLeast(json, "sourcesForNextLevel", 0, 0),
                Json.getString(json, "immunity", "").trim(),
                blocks, items, dimensions, Collections.unmodifiableList(levels),
                carriers, Json.getBoolean(json, "contagious", false), catchers,
                Json.atLeast(json, "contagionRange", 4, 0),
                Json.chance(json, "contagionChance", 0.1F),
                Json.atLeast(json, "contagionDuration", 1200, 1),
                weather, weatherDimensions, biomes, environments, structures, caveRegions, Collections.unmodifiableList(heights),
                immuneItems, immuneTeams, Json.getString(json, "onClear", "").trim());
    }

    private static List<ExposureBandDef> bands(ResourceLocation key, JsonObject json) {
        List<ExposureBandDef> bands = new ArrayList<>();
        for (String entry : strings(json, "heights")) {
            Settings.Weighted split = Settings.weighted(entry, 1);
            if (split.unreadable) { ContentLog.LOGGER.error("The heights entry '{}' in {} has a level that is not a number, so it counts as level 1", entry, key); }
            int colon = split.name.indexOf(':');
            Integer from = colon < 0 ? null : bound(split.name.substring(0, colon), Integer.MIN_VALUE);
            Integer to = colon < 0 ? null : bound(split.name.substring(colon + 1), Integer.MAX_VALUE);
            if (from == null || to == null || from > to) {
                ContentLog.LOGGER.error("The heights entry '{}' in {} is not a from:to band, so it is ignored", entry, key);
                continue;
            }
            bands.add(new ExposureBandDef(from, to, Math.max(1, split.weight)));
        }
        return bands;
    }

    @Nullable private static Integer bound(String text, int open) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) { return open; }
        try { return Integer.parseInt(trimmed); }
        catch (NumberFormatException bad) { return null; }
    }

    @Nullable private static String weather(ResourceLocation key, String named) {
        String kind = named.toLowerCase(Locale.ROOT);
        if (kind.equals("rain") || kind.equals("thunder")) { return kind; }
        ContentLog.LOGGER.error("The weather entry '{}' in {} is neither rain nor thunder, so it is ignored", named, key);
        return null;
    }

    @Nullable private static Integer dimension(ResourceLocation key, String named) {
        try {
            return Integer.parseInt(named);
        }
        catch (NumberFormatException bad) {
            ContentLog.LOGGER.error("The dimensions entry '{}' in {} is not a numeric dimension id, so it is ignored", named, key);
            return null;
        }
    }

    private static <T> Map<T, Integer> leveled(ResourceLocation key, JsonObject json, String member, Function<String, T> parse) {
        Map<T, Integer> out = new LinkedHashMap<>();
        for (String entry : strings(json, member)) {
            Settings.Weighted split = Settings.weighted(entry, 1);
            if (split.unreadable) { ContentLog.LOGGER.error("The {} entry '{}' in {} has a level that is not a number, so it counts as level 1", member, entry, key); }
            if (split.name.isEmpty()) { continue; }
            T parsed = parse.apply(split.name);
            if (parsed != null) { out.put(parsed, Math.max(1, split.weight)); }
        }
        return out;
    }

    public static AmountDef amount(JsonObject json, String key, int fallback, int floor) {
        if (!json.has(key)) { return AmountDef.of(Math.max(floor, fallback)); }
        JsonElement element = json.get(key);
        if (!element.isJsonObject()) { return AmountDef.of(Math.max(floor, Json.asInt(element, key, floor))); }
        JsonObject range = element.getAsJsonObject();
        int least = Json.atLeast(range, "min", fallback, floor);
        return new AmountDef(least, Json.atLeast(range, "max", least, least));
    }

    public static IntRange heights(String what, ResourceLocation key, JsonObject json, int minFallback, int maxFallback) {
        int minHeight = Json.getInt(json, "minHeight", minFallback);
        int maxHeight = Json.getInt(json, "maxHeight", maxFallback);
        if (maxHeight < minHeight) {
            ContentLog.LOGGER.error("{} {} has maxHeight below minHeight, swapping them", what, key);
            return new IntRange(maxHeight, minHeight);
        }
        return new IntRange(minHeight, maxHeight);
    }

    public static List<PickDef> picks(JsonObject entry, String listKey, String nameKey) {
        if (!entry.has(listKey)) { return Collections.emptyList(); }
        List<PickDef> picked = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(entry, listKey)) {
            if (element.isJsonPrimitive()) { picked.add(new PickDef(Settings.lower(element.getAsString()), 1)); }
            else {
                JsonObject held = element.getAsJsonObject();
                picked.add(new PickDef(Settings.lower(Json.getString(held, nameKey, "")),Json.getInt(held, "weight", 1)));
            }
        }
        return Collections.unmodifiableList(picked);
    }

    public static BlockMatchDef match(ResourceLocation key, JsonElement element) {
        if (!element.isJsonObject()) { return match(key, element.getAsString()); }
        JsonObject entry = element.getAsJsonObject();
        Json.unknownKeys(entry, "match", MATCH_KEYS);
        return new BlockMatchDef(new ResourceLocation(Json.getString(entry, "block", STONE)), Json.getInt(entry, "meta", -1), Json.map(entry, "properties"));
    }

    static BlockMatchDef match(ResourceLocation key, String name) {
        String[] parts = name.split(":");
        if (parts.length < 3) { return new BlockMatchDef(new ResourceLocation(name), -1, Collections.emptyMap()); }
        ResourceLocation block = new ResourceLocation(parts[0] + ":" + parts[1]);
        try { return new BlockMatchDef(block, Integer.parseInt(parts[2]), Collections.emptyMap()); }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Block metadata '{}' in {} is not a number, using 0", parts[2], key);
            return new BlockMatchDef(block, 0, Collections.emptyMap());
        }
    }

    private static BlockRenderLayer renderLayer(String value, String type, boolean opaque, String context) {
        if (value.isEmpty()) { return ContentBlockTypes.defaultLayer(type, opaque); }
        try { return BlockRenderLayer.valueOf(value.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) {
            BlockRenderLayer layer = ContentBlockTypes.defaultLayer(type, opaque);
            ContentLog.LOGGER.error("Unknown renderLayer '{}' in {}, using {}", value, context, layer.name().toLowerCase(Locale.ROOT));
            return layer;
        }
    }

    @Nullable private static AxisAlignedBB bounds(ResourceLocation key, JsonObject json) {
        if (!json.has("bounds")) { return null; }
        JsonArray array = Json.getJsonArray(json, "bounds");
        if (array.size() != 6) {
            ContentLog.LOGGER.error("Bounds in {} need six numbers, minX minY minZ maxX maxY maxZ, ignoring them", key);
            return null;
        }
        double[] v = new double[6];
        for (int i = 0; i < 6; i++) { v[i] = Json.asDouble(array.get(i), "box", 0.0); }
        return new AxisAlignedBB(v[0], v[1], v[2], v[3], v[4], v[5]);
    }

    @Nullable private static String potion(JsonObject json) {
        if (!json.has("potion") || json.get("potion").isJsonNull()) { return null; }
        String value = Json.getString(json, "potion");
        return value.trim().isEmpty() ? null : value;
    }

    static List<SpawnEntryDef> spawns(ResourceLocation key, JsonObject json, String typeKey, boolean needsEntity) {
        List<SpawnEntryDef> spawns = new ArrayList<>();
        if (!json.has("spawns")) { return spawns; }
        for (JsonObject entry : Json.objectsOrSkip(Json.getJsonArray(json, "spawns"), "A spawn entry in {} is not an object, skipping it", key)) {
            String entity = needsEntity ? Json.getString(entry, "entity", "") : "";
            if (needsEntity && entity.isEmpty()) {
                ContentLog.LOGGER.error("A spawn entry in {} names no entity, skipping it", key);
                continue;
            }
            int min = Json.atLeast(entry, "min", 1, 1);
            spawns.add(new SpawnEntryDef(Json.getString(entry, typeKey, "creature"), entity,
                    Json.atLeast(entry, "weight", 8, 1),
                    min, Json.atLeast(entry, "max", 4, min)));
        }
        return spawns;
    }

    public static String placeholderName(int meta, int digits) {
        StringBuilder builder = new StringBuilder(PLACEHOLDER);
        String number = Integer.toString(meta);
        for (int i = number.length(); i < digits; i++) { builder.append('0'); }
        return builder.append(number).toString();
    }
}
