package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.BellDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.DropDef;
import mctmods.resourcedatapackloader.content.def.ExposureBandDef;
import mctmods.resourcedatapackloader.content.def.ExposureDef;
import mctmods.resourcedatapackloader.content.def.ExposureLevelDef;
import mctmods.resourcedatapackloader.content.def.FluidDef;
import mctmods.resourcedatapackloader.content.def.GrowthDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.def.MaterialDef;
import mctmods.resourcedatapackloader.content.def.PortalDef;
import mctmods.resourcedatapackloader.content.def.PotionEffectDef;
import mctmods.resourcedatapackloader.content.def.SaplingDef;
import mctmods.resourcedatapackloader.content.def.TabDef;
import mctmods.resourcedatapackloader.content.types.ContentToolTypes;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgenParser;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;

public final class ContentParser {
    public static final String DEFAULT_TYPE = "basic";
    public static final String DEFAULT_STILL = "minecraft:block/water_still";
    public static final String DEFAULT_FLOW = "minecraft:block/water_flow";
    public static final String VARIANTS = "variants";
    public static final String TAGS = "tags";
    private static final String ORE_DICT = "oreDict";
    private static final List<String> BEHAVIORS = List.of("animals", "bush", "path", "till");
    private static final List<String> RENDER_LAYERS = List.of("solid", "cutout", "cutout_mipped", "translucent");
    private static final String PLANT_TYPES = "plantTypes";
    private static final int[] NO_CHANCE = new int[0];

    private ContentParser() {}

    @Nullable public static BlockDef block(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "block definition");
        if (json == null) { return null; }
        JsonObject exp = Json.getJsonObject(json, "expDrop", new JsonObject());
        String type = Settings.lower(Json.getString(json, "type", DEFAULT_TYPE));
        boolean opaque = Json.getBoolean(json, "opaque", !ContentParserContainers.chested(json));
        List<BlockVariant> variants = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, VARIANTS, new JsonObject()).entrySet()) {
            ResourceLocation id = variantId(key, entry.getKey(), "Block");
            if (id == null || !entry.getValue().isJsonObject()) {
                if (id != null) { ContentLog.LOGGER.error("Block variant '{}' in {} is not an object, skipping it", entry.getKey(), key); }
                continue;
            }
            variants.add(blockVariant(key, id, entry.getValue().getAsJsonObject()));
        }
        if (variants.isEmpty()) {
            ContentLog.LOGGER.error("Block definition {} has no usable variants, so it registers nothing", key);
            return null;
        }
        String harvestTool = Settings.lower(Json.getString(json, "harvestTool", "pickaxe"));
        if (!harvestTool.isEmpty() && !ContentToolTypes.HARVEST.contains(harvestTool)) { ContentLog.LOGGER.warn("Block {} names harvestTool '{}', which no tool on this line answers to, so only the material decides what harvests it. Known tools are {}; a modded tool reads its own block tag, which a variant can name under '{}'", key, harvestTool, ContentToolTypes.HARVEST, TAGS); }
        retiredPlantTypes(key, json);
        return new BlockDef(key, type,
                Settings.lower(Json.getString(json, "material", "rock")),
                Settings.lower(Json.getString(json, "mapColor", "")),
                Settings.lower(Json.getString(json, "soundType", "")),
                Json.getString(json, "creativeTab", "").trim(),
                harvestTool,
                Json.getInt(json, "harvestToolLevel", 0),
                Json.getBoolean(json, "silkHarvest", true),
                Json.getInt(exp, "min", 0), Json.getInt(exp, "max", 0),
                Json.getFloat(json, "explosionResistanceDivisor", 1.0F),
                Collections.unmodifiableList(variants), Json.strings(json, "requires"),
                renderLayer(key, json),
                opaque,
                Json.getBoolean(json, "fullCube", opaque),
                Json.getInt(json, "lightOpacity", opaque ? 255 : 0),
                Json.getFloat(json, "slipperiness", 0.6F),
                bounds(key, json),
                Json.getInt(json, "flammability", 0),
                Json.getInt(json, "fireSpread", 0),
                Json.getString(json, "modelBlock", "minecraft:stone").trim(),
                "item".equals(Json.getString(json, "itemModel", "state")),
                Settings.lower(Json.getString(json, "particle", BlockDef.PARTICLE_FLAME)),
                Json.getBoolean(json, "smoke", true),
                color(Json.getString(json, "particleColor", "FFFFFF"), key),
                Json.getString(json, "seed", "").trim(),
                Json.getString(json, "produce", "").trim(),
                Mth.clamp(Json.getInt(json, "maxAge", 7), 1, 7),
                sapling(json),
                growth(json),
                behaviors(key, json),
                Json.getString(json, "tint", "").trim(),
                Json.getString(json, "leafSapling", "").trim(),
                Mth.clamp(Json.getInt(json, "leafSaplingChance", 5), 0, 100),
                location(Json.getString(json, "opensWith", "")),
                Json.getString(json, "openSound", "").trim(),
                portal(key, json, null, true, false),
                ContentParserContainers.container(key, json),
                bell(json),
                Collections.unmodifiableList(Json.strings(json, "spreadsOnto")));
    }

    private static String renderLayer(ResourceLocation key, JsonObject json) {
        String layer = Settings.lower(Json.getString(json, "renderLayer", ""));
        if (!layer.isEmpty() && !RENDER_LAYERS.contains(layer)) { ContentLog.LOGGER.error("Unknown renderLayer '{}' in {}, using the type default", layer, key); }
        return layer;
    }

    @Nullable private static BellDef bell(JsonObject json) {
        if (!GsonHelper.isObjectNode(json, "bell")) { return null; }
        JsonObject held = Json.getJsonObject(json, "bell");
        return new BellDef(Json.getBoolean(held, "swing", BellDef.PLAIN.swing()),
                Json.getString(held, "sound", BellDef.PLAIN.sound()).trim(),
                Json.getString(held, "resonateSound", BellDef.PLAIN.resonateSound()).trim());
    }

    @Nullable public static PortalDef portal(ResourceLocation key, JsonObject json, @Nullable ResourceLocation ownDimension, boolean ownedDefault, boolean walkInDefault) {
        if (!json.has("portal")) { return null; }
        JsonObject entry = Json.getJsonObject(json, "portal");
        String named = Json.getString(entry, "dimension", ownDimension == null ? "" : ownDimension.toString()).trim();
        ResourceLocation dimension = named.isEmpty() ? null : ResourceLocation.tryParse(ContentFormats.dimensionId(named));
        if (dimension == null) {
            ContentLog.LOGGER.error("The portal of {} names dimension '{}', which is not a dimension id, so it leads nowhere", key, named);
            return null;
        }
        String backNamed = Json.getString(entry, "returnDimension", ContentBiomes.OVERWORLD).trim();
        ResourceLocation back = ResourceLocation.tryParse(ContentFormats.dimensionId(backNamed));
        if (back == null) {
            ContentLog.LOGGER.error("The portal of {} names return dimension '{}', which is not a dimension id, returning to the overworld", key, backNamed);
            back = ResourceLocation.parse(ContentBiomes.OVERWORLD);
        }
        return new PortalDef(dimension, back, Json.getString(entry, "gate", "").trim(), Math.max(0, Json.getInt(entry, "cooldown", 60)),
                Json.getBoolean(entry, "platform", true), Json.getString(entry, "platformBlock", "").trim(), Json.getString(entry, "sound", "").trim(),
                Json.getBoolean(entry, "owned", ownedDefault), Json.getBoolean(entry, "walkIn", walkInDefault));
    }

    @Nullable private static ResourceLocation variantId(ResourceLocation key, String name, String kind) {
        ResourceLocation id = ResourceLocation.tryBuild(key.getNamespace(), name.toLowerCase(Locale.ROOT));
        if (id == null) { ContentLog.LOGGER.error("{} variant '{}' in {} is not a usable registry name (lowercase letters, digits, '_', '-', '.' and '/'), skipping it", kind, name, key); }
        return id;
    }

    private static BlockVariant blockVariant(ResourceLocation key, ResourceLocation id, JsonObject json) {
        List<DropDef> drops = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(json, "drops", new JsonArray())) {
            if (!element.isJsonObject()) { continue; }
            DropDef drop = drop(key, id.getPath(), element.getAsJsonObject());
            if (drop != null) { drops.add(drop); }
        }
        return new BlockVariant(id, id.getPath(),
                Settings.lower(Json.getString(json, "rarity", "common")),
                Mth.clamp(Json.getInt(json, "maxSize", 64), 1, 64),
                tags(key, id.getPath(), json),
                Json.getFloat(json, "hardness", 1.0F),
                Json.getFloat(json, "resistance", 5.0F),
                Json.getInt(json, "harvestLevel", 0),
                Mth.clamp(Json.getInt(json, "light", 0), 0, 15),
                Collections.unmodifiableList(drops),
                portal(key, json, null, true, false));
    }

    private static void retiredPlantTypes(ResourceLocation key, JsonObject json) { if (!Json.strings(json, PLANT_TYPES).isEmpty()) { ContentLog.LOGGER.warn("Block {} sets '{}', which this line does not read: NeoForge 1.21.1 has no plant types. Use 'bush' under 'behavesAs' for what 'plains' did, and let any other plant name this block in its own soil list", key, PLANT_TYPES); } }

    @Nullable public static BlockMatchDef match(ResourceLocation key, JsonElement element) {
        if (!element.isJsonObject()) { return match(key, element.getAsString()); }
        JsonObject entry = element.getAsJsonObject();
        if (entry.has("meta")) { ContentLog.LOGGER.warn("A block match in {} sets 'meta', which this line does not read. Name the state under 'properties' instead", key); }
        String name = Json.getString(entry, "block", "minecraft:stone");
        ResourceLocation block = location(name);
        if (block == null) {
            ContentLog.LOGGER.error("A block match in {} names '{}', which is not a valid block id, leaving it out", key, name);
            return null;
        }
        return new BlockMatchDef(block, Json.map(entry, "properties"));
    }

    @Nullable private static BlockMatchDef match(ResourceLocation key, String name) {
        String[] parts = name.split(":");
        if (parts.length >= 3) {
            ContentLog.LOGGER.warn("Block match '{}' in {} carries metadata, which this line does not read, so every state of {}:{} is matched", name, key, parts[0], parts[1]);
            name = parts[0] + ":" + parts[1];
        }
        ResourceLocation block = location(name);
        if (block == null) { ContentLog.LOGGER.error("Block match '{}' in {} is not a valid block id, leaving it out", name, key); }
        return block == null ? null : new BlockMatchDef(block, Collections.emptyMap());
    }

    public static List<PotionEffectDef> effects(ResourceLocation key, JsonObject json) {
        if (!json.has("effects")) { return Collections.emptyList(); }
        return Collections.unmodifiableList(effects(key, Json.getJsonArray(json, "effects"), 3600, true));
    }

    public static List<PotionEffectDef> effects(ResourceLocation key, JsonArray array, int durationDefault, boolean particlesDefault) {
        List<PotionEffectDef> effects = new ArrayList<>();
        for (JsonElement element : array) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("An effect in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String potion = Json.getString(entry, "potion", "");
            if (potion.isEmpty()) {
                ContentLog.LOGGER.error("An effect in {} names no potion, skipping it", key);
                continue;
            }
            effects.add(new PotionEffectDef(potion,
                    Math.max(durationDefault > 0 ? 1 : 0,Json.getInt(entry, "duration", durationDefault)),
                    Math.max(0, Json.getInt(entry, "amplifier", 0)),
                    Json.getBoolean(entry, "ambient", false),
                    Json.getBoolean(entry, "showParticles", particlesDefault)));
        }
        return effects;
    }

    private static List<String> tags(ResourceLocation key, String name, JsonObject json) {
        if (json.has(ORE_DICT)) { ContentLog.LOGGER.warn("Variant '{}' in {} uses '{}', which this line does not read. Name tags under '{}' instead, such as c:ores/ruby or forge:ores/ruby", name, key, ORE_DICT, TAGS); }
        List<String> tags = new ArrayList<>();
        for (String tag : Json.strings(json, TAGS)) {
            if (ResourceLocation.tryParse(tag) == null) {
                ContentLog.LOGGER.error("Variant '{}' in {} names tag '{}', which is not a valid tag id, skipping it", name, key, tag);
                continue;
            }
            tags.add(tag);
        }
        return Collections.unmodifiableList(tags);
    }

    @Nullable private static DropDef drop(ResourceLocation key, String name, JsonObject json) {
        String block = Json.getString(json, "block", "").trim();
        String entity = Json.getString(json, "entity", "").trim();
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
        ResourceLocation item = entity.isEmpty() ? location(block) : null;
        ResourceLocation spawned = entity.isEmpty() ? null : location(entity);
        if (item == null && spawned == null) {
            ContentLog.LOGGER.error("A drop for '{}' in {} names '{}', which is not a valid id, skipping it", name, key, entity.isEmpty() ? block : entity);
            return null;
        }
        return new DropDef(item, spawned, amount(json, "amount", 1, 0), Mth.clamp(Json.getInt(json, "chance", guaranteed ? 100 : 0), 0, 100), Math.max(0, Json.getInt(json, "weight", 0)), chances);
    }

    @Nullable public static ItemDef item(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "item definition");
        if (json == null) { return null; }
        List<ItemVariant> variants = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, VARIANTS, new JsonObject()).entrySet()) {
            ResourceLocation id = variantId(key, entry.getKey(), "Item");
            if (id == null || !entry.getValue().isJsonObject()) {
                if (id != null) { ContentLog.LOGGER.error("Item variant '{}' in {} is not an object, skipping it", entry.getKey(), key); }
                continue;
            }
            JsonObject variant = entry.getValue().getAsJsonObject();
            variants.add(new ItemVariant(id, id.getPath(),
                    Settings.lower(Json.getString(variant, "rarity", "common")),
                    Mth.clamp(Json.getInt(variant, "maxSize", 64), 1, 64),
                    tags(key, id.getPath(), variant),
                    Json.getInt(variant, "healAmount", 0),
                    Json.getFloat(variant, "saturation", 0.0F),
                    potion(variant)));
        }
        if (variants.isEmpty()) {
            ContentLog.LOGGER.error("Item definition {} has no usable variants, so it registers nothing", key);
            return null;
        }
        return new ItemDef(key, Settings.lower(Json.getString(json, "type", DEFAULT_TYPE)),
                Json.getString(json, "creativeTab", "").trim(),
                Json.getBoolean(json, "alwaysEdible", false),
                Collections.unmodifiableList(variants), Json.strings(json, "requires"),
                Math.max(1, Json.getInt(json, "useDuration", 32)),
                Json.getBoolean(json, "eat", false),
                ContentParserContainers.remainder(json),
                Json.getString(json, "material", "").trim(),
                Settings.lower(Json.getString(json, "toolClass", "")),
                Settings.lower(Json.getString(json, "slot", "")),
                Json.getString(json, "crop", "").trim(),
                Json.getString(json, "soil", "minecraft:farmland").trim(),
                Json.strings(json, "potionTypes"),
                Json.getFloat(json, "attackSpeed", Float.NaN),
                Math.max(0, Json.getInt(json, "cooldown", 0)),
                ContentParserContainers.holds(key, json),
                Json.getString(json, "rolls", "").trim(),
                Json.getString(json, "rollStore", "").trim(),
                Json.getBoolean(json, "passesTurn", false),
                Json.getString(json, "locates", "").trim(),
                ContentParserContainers.curioSlot(key, json),
                Collections.unmodifiableList(effects(key, Json.getJsonArray(json, "wornEffects", new JsonArray()), 0, false)),
                Collections.unmodifiableList(ContentParserContainers.wornAttributes(key, json)),
                ContentParserWeapons.weapon(key, json));
    }

    @Nullable public static FluidDef fluid(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "fluid definition");
        if (json == null) { return null; }
        String name = Json.getString(json, "name", key.getPath()).trim();
        if (ResourceLocation.tryBuild(key.getNamespace(), name) == null) {
            ContentLog.LOGGER.error("Fluid definition {} names itself '{}', which is not a usable registry name, ignoring it", key, name);
            return null;
        }
        JsonObject block = Json.getJsonObject(json, "block", new JsonObject());
        ResourceLocation still = location(Json.getString(json, "still", DEFAULT_STILL));
        ResourceLocation flow = location(Json.getString(json, "flow", DEFAULT_FLOW));
        return new FluidDef(key, name,
                color(Json.getString(json, "color", ""), key),
                still == null ? ResourceLocation.parse(DEFAULT_STILL) : still,
                flow == null ? ResourceLocation.parse(DEFAULT_FLOW) : flow,
                Json.getInt(json, "temperature", 300),
                Json.getInt(json, "density", 1000),
                Json.getInt(json, "viscosity", 1000),
                Mth.clamp(Json.getInt(json, "luminosity", 0), 0, 15),
                Json.getBoolean(json, "gaseous", false),
                Json.getBoolean(json, "bucket", true),
                json.has("block"),
                Settings.lower(Json.getString(block, "material", "water")),
                Json.getString(json, "creativeTab", "").trim(),
                Json.getInt(block, "flammability", 0),
                Json.getInt(block, "fireSpread", 0),
                Json.getInt(block, "quantaPerBlock", 0),
                Json.strings(block, "potions"), Json.strings(json, "requires"));
    }

    @Nullable public static MaterialDef material(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "material file");
        if (json == null) { return null; }
        int[] reduction = {2, 5, 6, 2};
        if (json.has("reduction")) {
            JsonArray array = Json.getJsonArray(json, "reduction");
            if (array.size() != 4) { ContentLog.LOGGER.error("Reduction in {} needs four numbers, boots leggings chestplate helmet, using the defaults", key); }
            else {
                for (int i = 0; i < 4; i++) { reduction[i] = Json.asInt(array.get(i), "reduction", 0); }
            }
        }
        return new MaterialDef(key,
                Math.max(0, Json.getInt(json, "harvestLevel", 1)),
                Math.max(0, Json.getInt(json, "durability", 250)),
                Json.getFloat(json, "efficiency", 6.0F),
                Json.getFloat(json, "damage", 2.0F),
                Math.max(0, Json.getInt(json, "enchantability", 14)),
                reduction,
                Json.getFloat(json, "toughness", 0.0F),
                Json.getString(json, "equipSound", "minecraft:item.armor.equip_iron").trim(),
                Json.getString(json, "armorTexture", key.toString()).trim(),
                Json.getString(json, "repairItem", "").trim(),
                Json.strings(json, "requires"));
    }

    @Nullable public static ExposureDef exposure(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "exposure definition");
        if (json == null) { return null; }
        List<ExposureLevelDef> levels = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(json, "levels", new JsonArray())) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A level in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject level = element.getAsJsonObject();
            String effect = Json.getString(level, "effect", "");
            if (effect.isEmpty()) {
                ContentLog.LOGGER.error("A level in {} names no effect, skipping it", key);
                continue;
            }
            List<PotionEffectDef> extras = effects(key, Json.getJsonArray(level, "effects", new JsonArray()), 0, false);
            levels.add(new ExposureLevelDef(effect, Math.max(0.0F, Json.getFloat(level, "damage", 0.0F)), Math.max(0, Json.getInt(level, "damageInterval", 160)), Collections.unmodifiableList(extras), Json.getString(level, "onEnter", "").trim()));
        }
        if (levels.isEmpty()) {
            ContentLog.LOGGER.error("Exposure {} has no usable levels, ignoring it", key);
            return null;
        }
        Map<ResourceLocation, Integer> blocks = leveledNames(key, json, "blocks");
        Map<ResourceLocation, Integer> items = leveledNames(key, json, "items");
        Map<ResourceLocation, Integer> dimensions = leveledNames(key, json, "dimensions");
        Map<ResourceLocation, Integer> carriers = leveledNames(key, json, "carriers");
        Map<String, Integer> weather = weather(key, json);
        Map<String, Integer> biomes = leveled(key, json, "biomes", named -> named);
        Map<String, Integer> structures = leveled(key, json, "structures", named -> named);
        Map<ResourceLocation, Integer> caveRegions = leveled(key, json, "caveRegions", named -> location(named.contains(":") ? named : key.getNamespace() + ":" + named));
        List<ExposureBandDef> heights = bands(key, json);
        if (blocks.isEmpty() && items.isEmpty() && dimensions.isEmpty() && carriers.isEmpty() && weather.isEmpty() && biomes.isEmpty() && structures.isEmpty() && caveRegions.isEmpty() && heights.isEmpty()) {
            ContentLog.LOGGER.error("Exposure {} names no blocks, items, dimensions, carriers, weather, biomes, structures, caveRegions or heights, ignoring it", key);
            return null;
        }
        Set<ResourceLocation> catchers = names(key, json.has("catchers") ? Json.strings(json, "catchers") : List.of("minecraft:player"), "catchers");
        Set<String> immuneTeams = new LinkedHashSet<>();
        for (String named : Json.strings(json, "immuneTeams")) {
            if (!named.trim().isEmpty()) { immuneTeams.add(named.trim()); }
        }
        return new ExposureDef(key, Math.max(1, Json.getInt(json, "scanInterval", 20)), Math.max(0, Json.getInt(json, "range", 10)), Json.getBoolean(json, "skipsCreative", true),
                Math.max(0, Json.getInt(json, "sourcesForNextLevel", 0)), Json.getString(json, "immunity", "").trim(), blocks, items, dimensions, Collections.unmodifiableList(levels),
                carriers, Json.getBoolean(json, "contagious", false), catchers, Math.max(0, Json.getInt(json, "contagionRange", 4)),
                Mth.clamp(Json.getFloat(json, "contagionChance", 0.1F), 0.0F, 1.0F), Math.max(1, Json.getInt(json, "contagionDuration", 1200)),
                weather, names(key, Json.strings(json, "weatherDimensions"), "weatherDimensions"), biomes, structures, caveRegions, Collections.unmodifiableList(heights),
                names(key, Json.strings(json, "immuneItems"), "immuneItems"), immuneTeams, Json.getString(json, "onClear", "").trim());
    }

    private static List<ExposureBandDef> bands(ResourceLocation key, JsonObject json) {
        List<ExposureBandDef> bands = new ArrayList<>();
        for (String entry : Json.strings(json, "heights")) {
            Settings.Weighted split = Settings.weighted(entry, 1);
            if (split.unreadable()) { ContentLog.LOGGER.error("The heights entry '{}' in {} has a level that is not a number, so it counts as level 1", entry, key); }
            int colon = split.name().indexOf(':');
            Integer from = colon < 0 ? null : bound(split.name().substring(0, colon), Integer.MIN_VALUE);
            Integer to = colon < 0 ? null : bound(split.name().substring(colon + 1), Integer.MAX_VALUE);
            if (from == null || to == null || from > to) {
                ContentLog.LOGGER.error("The heights entry '{}' in {} is not a from:to band, so it is ignored", entry, key);
                continue;
            }
            bands.add(new ExposureBandDef(from, to, Math.max(1, split.weight())));
        }
        return bands;
    }

    @Nullable private static Integer bound(String text, int open) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) { return open; }
        try { return Integer.parseInt(trimmed); }
        catch (NumberFormatException bad) { return null; }
    }

    private static Map<String, Integer> weather(ResourceLocation key, JsonObject json) {
        Map<String, Integer> found = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, Integer> entry : leveledNames(key, json, "weather").entrySet()) {
            String kind = entry.getKey().getPath();
            if (entry.getKey().getNamespace().equals("minecraft") && (kind.equals("rain") || kind.equals("thunder"))) { found.put(kind, entry.getValue()); }
            else { ContentLog.LOGGER.error("The weather entry '{}' in {} is neither rain nor thunder, so it is ignored", entry.getKey(), key); }
        }
        return found;
    }

    private static Set<ResourceLocation> names(ResourceLocation key, List<String> entries, String member) {
        Set<ResourceLocation> found = new LinkedHashSet<>();
        for (String entry : entries) {
            ResourceLocation name = location(entry.trim());
            if (name == null) { ContentLog.LOGGER.error("The {} entry '{}' in {} is not a name, ignoring it", member, entry, key); }
            else { found.add(name); }
        }
        return found;
    }

    private static Map<ResourceLocation, Integer> leveledNames(ResourceLocation key, JsonObject json, String member) { return leveled(key, json, member, ContentParser::location); }

    private static <T> Map<T, Integer> leveled(ResourceLocation key, JsonObject json, String member, Function<String, T> parse) {
        Map<T, Integer> found = new LinkedHashMap<>();
        for (JsonElement element : Json.getJsonArray(json, member, new JsonArray())) {
            if (!element.isJsonPrimitive()) { continue; }
            String entry = element.getAsString();
            Settings.Weighted split = Settings.weighted(entry, 1);
            if (split.unreadable()) { ContentLog.LOGGER.error("The {} entry '{}' in {} has a level that is not a number, so it counts as level 1", member, entry, key); }
            if (split.name().isEmpty()) { continue; }
            T name = parse.apply(split.name());
            if (name == null) {
                ContentLog.LOGGER.error("The {} entry '{}' in {} is not a name, ignoring it", member, entry, key);
                continue;
            }
            found.put(name, Math.max(1, split.weight()));
        }
        return found;
    }

    @Nullable public static TabDef tab(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "creative tab");
        if (json == null) { return null; }
        String icon = Json.getString(json, "icon", "").trim();
        if (icon.isEmpty()) { ContentLog.LOGGER.error("Creative tab {} has no icon, it will fall back to the first block that uses it", key); }
        return new TabDef(key, Json.getString(json, "label", key.getPath()).trim(), icon, Json.strings(json, "requires"));
    }

    @Nullable private static GrowthDef growth(JsonObject json) {
        if (!json.has("growth")) { return null; }
        JsonObject entry = Json.getJsonObject(json, "growth");
        return new GrowthDef(Math.max(1, Json.getInt(entry, "maxHeight", 3)),
                Mth.clamp(Json.getInt(entry, "stages", 16), 1, 16),
                Json.strings(entry, "soil"),
                Json.getBoolean(entry, "needsWater", false),
                Math.max(1, Json.getInt(entry, "waterRange", 1)),
                Json.getBoolean(entry, "needsSky", false),
                Json.getBoolean(entry, "damage", false),
                Json.getFloat(entry, "damageAmount", 1.0F),
                Json.getBoolean(entry, "breaksNeighbors", false),
                Math.max(0, Json.getInt(entry, "spread", 0)),
                Json.getString(entry, "drop", "").trim(),
                Math.max(1, Json.getInt(entry, "dropCount", 1)));
    }

    @Nullable private static SaplingDef sapling(JsonObject json) {
        if (!json.has("sapling")) { return null; }
        JsonObject entry = Json.getJsonObject(json, "sapling");
        return new SaplingDef(Json.strings(entry, "soil"),
                Math.max(1, Json.getInt(entry, "stages", 2)),
                Math.max(1, Json.getInt(entry, "chance", 7)),
                Math.max(0, Json.getInt(entry, "light", 9)),
                Json.getString(entry, "structure", "").trim(),
                ContentWorldgenParser.picks(entry, "structures", "structure"),
                Json.getString(entry, "log", "minecraft:oak_log").trim(),
                Json.getString(entry, "leaves", "minecraft:oak_leaves").trim(),
                Math.max(1, Json.getInt(entry, "height", 4)),
                Json.getBoolean(entry, "vines", false));
    }

    public static AmountDef amount(JsonObject json, String key, int fallback, int floor) {
        if (!json.has(key)) { return AmountDef.of(Math.max(floor, fallback)); }
        JsonElement element = json.get(key);
        if (!element.isJsonObject()) { return AmountDef.of(Math.max(floor, Json.asInt(element, key, floor))); }
        JsonObject range = element.getAsJsonObject();
        int least = Math.max(floor, Json.getInt(range, "min", fallback));
        return new AmountDef(least, Math.max(least, Json.getInt(range, "max", least)));
    }

    @Nullable private static double[] bounds(ResourceLocation key, JsonObject json) {
        if (!json.has("bounds")) { return null; }
        JsonArray array = Json.getJsonArray(json, "bounds");
        if (array.size() != 6) {
            ContentLog.LOGGER.error("Bounds in {} need six numbers, minX minY minZ maxX maxY maxZ, ignoring them", key);
            return null;
        }
        double[] values = new double[6];
        for (int i = 0; i < 6; i++) { values[i] = Json.asDouble(array.get(i), "box", 0.0); }
        return values;
    }

    @Nullable private static String potion(JsonObject json) {
        if (!json.has("potion") || json.get("potion").isJsonNull()) { return null; }
        String value = Json.getString(json, "potion").trim();
        return value.isEmpty() ? null : value;
    }

    public static int color(String value, Object context) {
        if (value == null || value.trim().isEmpty()) { return 0xFFFFFF; }
        Long parsed = ContentTypes.hex(value);
        if (parsed != null) { return (int) (parsed & 0xFFFFFFL); }
        ContentLog.LOGGER.error("Color '{}' in {} is not hexadecimal, using white", value, context);
        return 0xFFFFFF;
    }

    @Nullable public static ResourceLocation location(String value) {
        String named = value == null ? "" : value.trim();
        return named.isEmpty() ? null : ResourceLocation.tryParse(named.toLowerCase(Locale.ROOT));
    }

    private static List<String> behaviors(ResourceLocation key, JsonObject json) {
        List<String> found = new ArrayList<>();
        for (String raw : Json.strings(json, "behavesAs")) {
            String name = Settings.lower(raw);
            if (!BEHAVIORS.contains(name)) {
                ContentLog.LOGGER.error("Block {} says it behaves as '{}', which is not one of {}, ignoring it", key, raw, String.join(", ", BEHAVIORS));
                continue;
            }
            found.add(name);
        }
        return Collections.unmodifiableList(found);
    }
}
