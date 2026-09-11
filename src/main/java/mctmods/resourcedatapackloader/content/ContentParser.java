package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.TeamDef;
import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockMatchDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.DropDef;
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
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgenParser;

import java.util.HashSet;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.Team;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentParser {
    private static final Map<String, String> BAUBLE_SLOTS = Map.of("amulet", "necklace", "trinket", "charm");
    private static final Set<String> TOLD_BAUBLE = new HashSet<>();
    public static final String DEFAULT_TYPE = "basic";
    public static final String DEFAULT_STILL = "minecraft:block/water_still";
    public static final String DEFAULT_FLOW = "minecraft:block/water_flow";
    public static final String VARIANTS = "variants";
    public static final String TAGS = "tags";
    private static final String ORE_DICT = "oreDict";
    private static final Gson GSON = new GsonBuilder().create();
    private static final int[] NO_CHANCE = new int[0];

    private ContentParser() {}

    @Nullable public static BlockDef block(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Block definition {} is empty, ignoring it", key);
            return null;
        }
        JsonObject exp = GsonHelper.getAsJsonObject(json, "expDrop", new JsonObject());
        String type = GsonHelper.getAsString(json, "type", DEFAULT_TYPE).trim().toLowerCase(Locale.ROOT);
        boolean opaque = GsonHelper.getAsBoolean(json, "opaque", !chested(json));
        List<BlockVariant> variants = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, VARIANTS, new JsonObject()).entrySet()) {
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
        return new BlockDef(key, type,
                GsonHelper.getAsString(json, "material", "rock").trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsString(json, "mapColor", "").trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsString(json, "soundType", "").trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsString(json, "creativeTab", "").trim(),
                GsonHelper.getAsString(json, "harvestTool", "pickaxe").trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsInt(json, "harvestToolLevel", 0),
                GsonHelper.getAsBoolean(json, "silkHarvest", true),
                GsonHelper.getAsInt(exp, "min", 0), GsonHelper.getAsInt(exp, "max", 0),
                GsonHelper.getAsFloat(json, "explosionResistanceDivisor", 1.0F),
                Collections.unmodifiableList(variants), Json.strings(json, "requires"),
                GsonHelper.getAsString(json, "renderLayer", "").trim().toLowerCase(Locale.ROOT),
                opaque,
                GsonHelper.getAsBoolean(json, "fullCube", opaque),
                GsonHelper.getAsFloat(json, "slipperiness", 0.6F),
                bounds(key, json),
                GsonHelper.getAsInt(json, "flammability", 0),
                GsonHelper.getAsInt(json, "fireSpread", 0),
                GsonHelper.getAsString(json, "modelBlock", "minecraft:stone").trim(),
                "item".equals(GsonHelper.getAsString(json, "itemModel", "state")),
                GsonHelper.getAsString(json, "particle", BlockDef.PARTICLE_FLAME).trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsBoolean(json, "smoke", true),
                color(GsonHelper.getAsString(json, "particleColor", "FFFFFF"), key),
                GsonHelper.getAsString(json, "seed", "").trim(),
                GsonHelper.getAsString(json, "produce", "").trim(),
                Mth.clamp(GsonHelper.getAsInt(json, "maxAge", 7), 1, 7),
                sapling(json),
                growth(json),
                lowered(Json.strings(json, "plantTypes")),
                lowered(Json.strings(json, "behavesAs")),
                GsonHelper.getAsString(json, "tint", "").trim(),
                GsonHelper.getAsString(json, "leafSapling", "").trim(),
                Mth.clamp(GsonHelper.getAsInt(json, "leafSaplingChance", 5), 0, 100),
                location(GsonHelper.getAsString(json, "opensWith", "")),
                GsonHelper.getAsString(json, "openSound", "").trim(),
                portal(key, json, null, true, false),
                container(key, json));
    }

    @Nullable public static PortalDef portal(ResourceLocation key, JsonObject json, @Nullable ResourceLocation ownDimension, boolean ownedDefault, boolean walkInDefault) {
        if (!json.has("portal")) { return null; }
        JsonObject entry = GsonHelper.getAsJsonObject(json, "portal");
        String named = GsonHelper.getAsString(entry, "dimension", ownDimension == null ? "" : ownDimension.toString()).trim();
        ResourceLocation dimension = named.isEmpty() ? null : ResourceLocation.tryParse(ContentFormats.dimensionId(named));
        if (dimension == null) {
            ContentLog.LOGGER.error("The portal of {} names dimension '{}', which is not a dimension id, so it leads nowhere", key, named);
            return null;
        }
        String backNamed = GsonHelper.getAsString(entry, "returnDimension", "minecraft:overworld").trim();
        ResourceLocation back = ResourceLocation.tryParse(ContentFormats.dimensionId(backNamed));
        if (back == null) {
            ContentLog.LOGGER.error("The portal of {} names return dimension '{}', which is not a dimension id, returning to the overworld", key, backNamed);
            back = ResourceLocation.parse("minecraft:overworld");
        }
        return new PortalDef(dimension, back, GsonHelper.getAsString(entry, "gate", "").trim(), Math.max(0, GsonHelper.getAsInt(entry, "cooldown", 60)),
                GsonHelper.getAsBoolean(entry, "platform", true), GsonHelper.getAsString(entry, "platformBlock", "").trim(), GsonHelper.getAsString(entry, "sound", "").trim(),
                GsonHelper.getAsBoolean(entry, "owned", ownedDefault), GsonHelper.getAsBoolean(entry, "walkIn", walkInDefault));
    }

    @Nullable private static ResourceLocation variantId(ResourceLocation key, String name, String kind) {
        ResourceLocation id = ResourceLocation.tryBuild(key.getNamespace(), name);
        if (id == null) { ContentLog.LOGGER.error("{} variant '{}' in {} is not a usable registry name (lowercase letters, digits, '_', '-', '.' and '/'), skipping it", kind, name, key); }
        return id;
    }

    private static BlockVariant blockVariant(ResourceLocation key, ResourceLocation id, JsonObject json) {
        List<DropDef> drops = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "drops", new JsonArray())) {
            if (!element.isJsonObject()) { continue; }
            DropDef drop = drop(key, id.getPath(), element.getAsJsonObject());
            if (drop != null) { drops.add(drop); }
        }
        return new BlockVariant(id, id.getPath(),
                GsonHelper.getAsString(json, "rarity", "common").trim().toLowerCase(Locale.ROOT),
                Mth.clamp(GsonHelper.getAsInt(json, "maxSize", 64), 1, 64),
                tags(key, id.getPath(), json),
                GsonHelper.getAsFloat(json, "hardness", 1.0F),
                GsonHelper.getAsFloat(json, "resistance", 5.0F),
                GsonHelper.getAsInt(json, "harvestLevel", -1),
                Mth.clamp(GsonHelper.getAsInt(json, "light", 0), 0, 15),
                Collections.unmodifiableList(drops));
    }

    @Nullable public static BlockMatchDef match(ResourceLocation key, JsonElement element) {
        if (!element.isJsonObject()) { return match(key, element.getAsString()); }
        JsonObject entry = element.getAsJsonObject();
        if (entry.has("meta")) { ContentLog.LOGGER.warn("A block match in {} sets 'meta', which this line does not read. Name the state under 'properties' instead", key); }
        String name = GsonHelper.getAsString(entry, "block", "");
        ResourceLocation block = ResourceLocation.tryParse(name);
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
        ResourceLocation block = ResourceLocation.tryParse(name);
        if (block == null) { ContentLog.LOGGER.error("Block match '{}' in {} is not a valid block id, leaving it out", name, key); }
        return block == null ? null : new BlockMatchDef(block, Collections.emptyMap());
    }

    public static List<PotionEffectDef> effects(ResourceLocation key, JsonObject json) {
        if (!json.has("effects")) { return Collections.emptyList(); }
        List<PotionEffectDef> effects = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "effects")) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("An effect in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String potion = GsonHelper.getAsString(entry, "potion", "");
            if (potion.isEmpty()) {
                ContentLog.LOGGER.error("An effect in {} names no potion, skipping it", key);
                continue;
            }
            effects.add(new PotionEffectDef(potion,
                    Math.max(1, GsonHelper.getAsInt(entry, "duration", 3600)),
                    Math.max(0, GsonHelper.getAsInt(entry, "amplifier", 0)),
                    GsonHelper.getAsBoolean(entry, "ambient", false),
                    GsonHelper.getAsBoolean(entry, "showParticles", true)));
        }
        return Collections.unmodifiableList(effects);
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
        String block = GsonHelper.getAsString(json, "block", "").trim();
        String entity = GsonHelper.getAsString(json, "entity", "").trim();
        if (block.isEmpty() && entity.isEmpty()) {
            ContentLog.LOGGER.error("A drop for '{}' in {} names neither a block nor an entity, skipping it", name, key);
            return null;
        }
        if (!block.isEmpty() && !entity.isEmpty()) { ContentLog.LOGGER.error("A drop for '{}' in {} names both block {} and entity {}, using the entity", name, key, block, entity); }
        int[] chances = NO_CHANCE;
        if (json.has("bonusChance")) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "bonusChance");
            chances = new int[array.size()];
            for (int i = 0; i < array.size(); i++) { chances[i] = array.get(i).getAsInt(); }
        }
        boolean guaranteed = GsonHelper.getAsBoolean(json, "guaranteed", true);
        ResourceLocation item = entity.isEmpty() ? location(block) : null;
        ResourceLocation spawned = entity.isEmpty() ? null : location(entity);
        if (item == null && spawned == null) {
            ContentLog.LOGGER.error("A drop for '{}' in {} names '{}', which is not a valid id, skipping it", name, key, entity.isEmpty() ? block : entity);
            return null;
        }
        return new DropDef(item, spawned, amount(json, "amount", 1, 0), Mth.clamp(GsonHelper.getAsInt(json, "chance", guaranteed ? 100 : 0), 0, 100), Math.max(0, GsonHelper.getAsInt(json, "weight", 0)), chances);
    }

    @Nullable public static ItemDef item(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Item definition {} is empty, ignoring it", key);
            return null;
        }
        List<ItemVariant> variants = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, VARIANTS, new JsonObject()).entrySet()) {
            ResourceLocation id = variantId(key, entry.getKey(), "Item");
            if (id == null || !entry.getValue().isJsonObject()) {
                if (id != null) { ContentLog.LOGGER.error("Item variant '{}' in {} is not an object, skipping it", entry.getKey(), key); }
                continue;
            }
            JsonObject variant = entry.getValue().getAsJsonObject();
            variants.add(new ItemVariant(id, id.getPath(),
                    GsonHelper.getAsString(variant, "rarity", "common").trim().toLowerCase(Locale.ROOT),
                    Mth.clamp(GsonHelper.getAsInt(variant, "maxSize", 64), 1, 64),
                    tags(key, id.getPath(), variant),
                    GsonHelper.getAsInt(variant, "healAmount", 0),
                    GsonHelper.getAsFloat(variant, "saturation", 0.0F),
                    potion(variant)));
        }
        if (variants.isEmpty()) {
            ContentLog.LOGGER.error("Item definition {} has no usable variants, so it registers nothing", key);
            return null;
        }
        return new ItemDef(key, GsonHelper.getAsString(json, "type", DEFAULT_TYPE).trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsString(json, "creativeTab", "").trim(),
                GsonHelper.getAsBoolean(json, "alwaysEdible", false),
                Collections.unmodifiableList(variants), Json.strings(json, "requires"),
                Math.max(1, GsonHelper.getAsInt(json, "useDuration", 32)),
                GsonHelper.getAsBoolean(json, "eat", false),
                remainder(json),
                GsonHelper.getAsString(json, "material", "").trim(),
                GsonHelper.getAsString(json, "toolClass", "").trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsString(json, "slot", "").trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsString(json, "crop", "").trim(),
                GsonHelper.getAsString(json, "soil", "minecraft:farmland").trim(),
                Json.strings(json, "potionTypes"),
                GsonHelper.getAsFloat(json, "attackSpeed", Float.NaN),
                Math.max(0, GsonHelper.getAsInt(json, "cooldown", 0)),
                holds(key, json));
    }

    @Nullable public static FluidDef fluid(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Fluid definition {} is empty, ignoring it", key);
            return null;
        }
        String name = GsonHelper.getAsString(json, "name", key.getPath()).trim();
        if (ResourceLocation.tryBuild(key.getNamespace(), name) == null) {
            ContentLog.LOGGER.error("Fluid definition {} names itself '{}', which is not a usable registry name, ignoring it", key, name);
            return null;
        }
        JsonObject block = GsonHelper.getAsJsonObject(json, "block", new JsonObject());
        ResourceLocation still = location(GsonHelper.getAsString(json, "still", DEFAULT_STILL));
        ResourceLocation flow = location(GsonHelper.getAsString(json, "flow", DEFAULT_FLOW));
        return new FluidDef(key, name,
                color(GsonHelper.getAsString(json, "color", ""), key),
                still == null ? ResourceLocation.parse(DEFAULT_STILL) : still,
                flow == null ? ResourceLocation.parse(DEFAULT_FLOW) : flow,
                GsonHelper.getAsInt(json, "temperature", 300),
                GsonHelper.getAsInt(json, "density", 1000),
                GsonHelper.getAsInt(json, "viscosity", 1000),
                Mth.clamp(GsonHelper.getAsInt(json, "luminosity", 0), 0, 15),
                GsonHelper.getAsBoolean(json, "gaseous", false),
                GsonHelper.getAsBoolean(json, "bucket", true),
                json.has("block"),
                GsonHelper.getAsString(block, "material", "water").trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsString(json, "creativeTab", "").trim(),
                GsonHelper.getAsInt(block, "flammability", 0),
                GsonHelper.getAsInt(block, "fireSpread", 0),
                GsonHelper.getAsInt(block, "quantaPerBlock", 0),
                Json.strings(block, "potions"), Json.strings(json, "requires"));
    }

    @Nullable public static MaterialDef material(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Material file {} is empty, ignoring it", key);
            return null;
        }
        int[] reduction = {2, 5, 6, 2};
        if (json.has("reduction")) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "reduction");
            if (array.size() != 4) { ContentLog.LOGGER.error("Reduction in {} needs four numbers, boots leggings chestplate helmet, using the defaults", key); }
            else {
                for (int i = 0; i < 4; i++) { reduction[i] = array.get(i).getAsInt(); }
            }
        }
        return new MaterialDef(key,
                Math.max(0, GsonHelper.getAsInt(json, "harvestLevel", 1)),
                Math.max(1, GsonHelper.getAsInt(json, "durability", 250)),
                GsonHelper.getAsFloat(json, "efficiency", 6.0F),
                GsonHelper.getAsFloat(json, "damage", 2.0F),
                Math.max(0, GsonHelper.getAsInt(json, "enchantability", 14)),
                reduction,
                GsonHelper.getAsFloat(json, "toughness", 0.0F),
                GsonHelper.getAsString(json, "equipSound", "minecraft:item.armor.equip_iron").trim(),
                GsonHelper.getAsString(json, "armorTexture", key.toString()).trim(),
                GsonHelper.getAsString(json, "repairItem", "").trim(),
                Json.strings(json, "requires"));
    }

    @Nullable public static ExposureDef exposure(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Exposure definition {} is empty, ignoring it", key);
            return null;
        }
        List<ExposureLevelDef> levels = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "levels", new JsonArray())) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A level in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject level = element.getAsJsonObject();
            String effect = GsonHelper.getAsString(level, "effect", "").trim();
            if (effect.isEmpty()) {
                ContentLog.LOGGER.error("A level in {} names no effect, skipping it", key);
                continue;
            }
            List<PotionEffectDef> extras = new ArrayList<>();
            for (JsonElement extraElement : GsonHelper.getAsJsonArray(level, "effects", new JsonArray())) {
                if (!extraElement.isJsonObject()) { continue; }
                JsonObject extra = extraElement.getAsJsonObject();
                String potion = GsonHelper.getAsString(extra, "potion", "").trim();
                if (potion.isEmpty()) {
                    ContentLog.LOGGER.error("An extra effect in {} names no potion, skipping it", key);
                    continue;
                }
                extras.add(new PotionEffectDef(potion, Math.max(0, GsonHelper.getAsInt(extra, "duration", 0)), Math.max(0, GsonHelper.getAsInt(extra, "amplifier", 0)), GsonHelper.getAsBoolean(extra, "ambient", false), GsonHelper.getAsBoolean(extra, "showParticles", false)));
            }
            levels.add(new ExposureLevelDef(effect, Math.max(0.0F, GsonHelper.getAsFloat(level, "damage", 0.0F)), Math.max(0, GsonHelper.getAsInt(level, "damageInterval", 160)), Collections.unmodifiableList(extras)));
        }
        if (levels.isEmpty()) {
            ContentLog.LOGGER.error("Exposure {} has no usable levels, ignoring it", key);
            return null;
        }
        Map<ResourceLocation, Integer> blocks = leveledNames(key, json, "blocks");
        Map<ResourceLocation, Integer> items = leveledNames(key, json, "items");
        if (blocks.isEmpty() && items.isEmpty()) {
            ContentLog.LOGGER.error("Exposure {} names no blocks and no items, ignoring it", key);
            return null;
        }
        return new ExposureDef(key, Math.max(1, GsonHelper.getAsInt(json, "scanInterval", 20)), Math.max(0, GsonHelper.getAsInt(json, "range", 10)), GsonHelper.getAsBoolean(json, "skipsCreative", true),
                Math.max(0, GsonHelper.getAsInt(json, "sourcesForNextLevel", 0)), GsonHelper.getAsString(json, "immunity", "").trim(), blocks, items, Collections.unmodifiableList(levels));
    }

    private static Map<ResourceLocation, Integer> leveledNames(ResourceLocation key, JsonObject json, String member) {
        Map<ResourceLocation, Integer> found = new LinkedHashMap<>();
        for (String entry : Json.strings(json, member)) {
            String named = entry.trim();
            int level = 1;
            int split = named.indexOf('=');
            if (split >= 0) {
                try { level = Math.max(1, Integer.parseInt(named.substring(split + 1).trim())); }
                catch (NumberFormatException bad) { ContentLog.LOGGER.error("The {} entry '{}' in {} has a level that is not a number, so it counts as level 1", member, entry, key); }
                named = named.substring(0, split).trim();
            }
            if (named.isEmpty()) { continue; }
            ResourceLocation name = ResourceLocation.tryParse(named);
            if (name == null) {
                ContentLog.LOGGER.error("The {} entry '{}' in {} is not a name, ignoring it", member, entry, key);
                continue;
            }
            found.put(name, level);
        }
        return found;
    }

    @Nullable public static TabDef tab(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Creative tab {} is empty, ignoring it", key);
            return null;
        }
        return new TabDef(key, GsonHelper.getAsString(json, "label", key.getPath()).trim(), GsonHelper.getAsString(json, "icon", "").trim(), Json.strings(json, "requires"));
    }

    @Nullable private static GrowthDef growth(JsonObject json) {
        if (!json.has("growth")) { return null; }
        JsonObject entry = GsonHelper.getAsJsonObject(json, "growth");
        return new GrowthDef(Math.max(1, GsonHelper.getAsInt(entry, "maxHeight", 3)),
                Mth.clamp(GsonHelper.getAsInt(entry, "stages", 16), 1, 16),
                Json.strings(entry, "soil"),
                GsonHelper.getAsBoolean(entry, "needsWater", false),
                Math.max(1, GsonHelper.getAsInt(entry, "waterRange", 1)),
                GsonHelper.getAsBoolean(entry, "needsSky", false),
                GsonHelper.getAsBoolean(entry, "damage", false),
                GsonHelper.getAsFloat(entry, "damageAmount", 1.0F),
                GsonHelper.getAsBoolean(entry, "breaksNeighbors", false),
                Math.max(0, GsonHelper.getAsInt(entry, "spread", 0)),
                GsonHelper.getAsString(entry, "drop", "").trim(),
                Math.max(1, GsonHelper.getAsInt(entry, "dropCount", 1)));
    }

    @Nullable private static SaplingDef sapling(JsonObject json) {
        if (!json.has("sapling")) { return null; }
        JsonObject entry = GsonHelper.getAsJsonObject(json, "sapling");
        return new SaplingDef(Json.strings(entry, "soil"),
                Math.max(1, GsonHelper.getAsInt(entry, "stages", 2)),
                Math.max(1, GsonHelper.getAsInt(entry, "chance", 7)),
                Math.max(0, GsonHelper.getAsInt(entry, "light", 9)),
                GsonHelper.getAsString(entry, "structure", "").trim(),
                ContentWorldgenParser.picks(entry, "structures", "structure"),
                GsonHelper.getAsString(entry, "log", "minecraft:oak_log").trim(),
                GsonHelper.getAsString(entry, "leaves", "minecraft:oak_leaves").trim(),
                Math.max(1, GsonHelper.getAsInt(entry, "height", 4)),
                GsonHelper.getAsBoolean(entry, "vines", false));
    }

    public static AmountDef amount(JsonObject json, String key, int fallback, int floor) {
        if (!json.has(key)) { return AmountDef.of(Math.max(floor, fallback)); }
        JsonElement element = json.get(key);
        if (!element.isJsonObject()) { return AmountDef.of(Math.max(floor, element.getAsInt())); }
        JsonObject range = element.getAsJsonObject();
        int least = Math.max(floor, GsonHelper.getAsInt(range, "min", fallback));
        return new AmountDef(least, Math.max(least, GsonHelper.getAsInt(range, "max", least)));
    }

    @Nullable private static double[] bounds(ResourceLocation key, JsonObject json) {
        if (!json.has("bounds")) { return null; }
        JsonArray array = GsonHelper.getAsJsonArray(json, "bounds");
        if (array.size() != 6) {
            ContentLog.LOGGER.error("Bounds in {} need six numbers, minX minY minZ maxX maxY maxZ, ignoring them", key);
            return null;
        }
        double[] values = new double[6];
        for (int i = 0; i < 6; i++) { values[i] = array.get(i).getAsDouble(); }
        return values;
    }

    @Nullable private static String potion(JsonObject json) {
        if (!json.has("potion") || json.get("potion").isJsonNull()) { return null; }
        String value = GsonHelper.getAsString(json, "potion").trim();
        return value.isEmpty() ? null : value;
    }

    public static int color(String value, Object context) {
        if (value == null || value.trim().isEmpty()) { return 0xFFFFFF; }
        String cleaned = value.trim();
        if (cleaned.startsWith("#")) { cleaned = cleaned.substring(1); }
        if (cleaned.startsWith("0x") || cleaned.startsWith("0X")) { cleaned = cleaned.substring(2); }
        try { return (int) (Long.parseLong(cleaned, 16) & 0xFFFFFFL); }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Color '{}' in {} is not hexadecimal, using white", value, context);
            return 0xFFFFFF;
        }
    }

    @Nullable public static ResourceLocation location(String value) {
        String named = value == null ? "" : value.trim();
        return named.isEmpty() ? null : ResourceLocation.tryParse(named);
    }

    private static List<String> lowered(List<String> values) {
        List<String> out = new ArrayList<>(values.size());
        for (String value : values) { out.add(value.trim().toLowerCase(Locale.ROOT)); }
        return Collections.unmodifiableList(out);
    }

    private static String remainder(JsonObject json) {
        String named = GsonHelper.getAsString(json, "containerItem", "").trim();
        if (!named.isEmpty()) { return named; }
        return json.has("container") && json.get("container").isJsonPrimitive() ? GsonHelper.getAsString(json, "container", "").trim() : "";
    }

    @Nullable private static ContainerDef holds(ResourceLocation key, JsonObject json) {
        return json.has("container") && json.get("container").isJsonObject() ? container(key, json) : null;
    }

    private static boolean chested(JsonObject json) {
        if (!json.has("container") || !json.get("container").isJsonObject()) { return false; }
        JsonElement asked = json.getAsJsonObject("container").get("chestModel");
        return asked != null && asked.isJsonPrimitive() && (asked.getAsJsonPrimitive().isString() || asked.getAsBoolean());
    }

    @Nullable private static ContainerDef container(ResourceLocation key, JsonObject json) {
        if (!json.has("container") || !json.get("container").isJsonObject()) { return null; }
        JsonObject held = GsonHelper.getAsJsonObject(json, "container");
        int askedRows = GsonHelper.getAsInt(held, "rows", 3);
        int askedColumns = GsonHelper.getAsInt(held, "columns", 9);
        int rows = Mth.clamp(askedRows, 1, ContainerDef.MOST_ROWS);
        int columns = Mth.clamp(askedColumns, 1, ContainerDef.MOST_COLUMNS);
        if (askedRows != rows || askedColumns != columns) {
            ContentLog.LOGGER.error("The container on {} asks for {} by {}, which is past the largest a screen can show, so it is cut to {} by {}", key, askedColumns, askedRows, columns, rows);
        }
        String named = GsonHelper.getAsString(held, "guiTexture", "").trim();
        ResourceLocation texture = named.isEmpty() ? null : ResourceLocation.tryParse(named);
        int wide = GsonHelper.getAsInt(held, "guiWidth", 0);
        int tall = GsonHelper.getAsInt(held, "guiHeight", 0);
        if (texture != null && (wide <= 0 || tall <= 0)) {
            ContentLog.LOGGER.error("The container on {} names a guiTexture without a guiWidth and guiHeight, so the drawn background is used instead", key);
            texture = null;
        }
        JsonElement asked = held.get("chestModel");
        boolean chest = asked != null && asked.isJsonPrimitive() && (asked.getAsJsonPrimitive().isString() || asked.getAsBoolean());
        ResourceLocation sheet = chest && asked.getAsJsonPrimitive().isString() ? sheetOf(asked.getAsString().trim(), key) : null;
        return new ContainerDef(rows, columns, GsonHelper.getAsString(held, "lootTable", "").trim(),
                chest, sheet, texture, wide, tall, curioSlot(key, held));
    }

    private static String curioSlot(ResourceLocation key, JsonObject held) {
        String asked = GsonHelper.getAsString(held, "curioSlot", "").trim().toLowerCase(Locale.ROOT);
        if (!asked.isEmpty()) { return asked; }
        String old = GsonHelper.getAsString(held, "bauble", "").trim().toLowerCase(Locale.ROOT);
        if (old.isEmpty()) { return ""; }
        String mapped = BAUBLE_SLOTS.getOrDefault(old, old);
        if (TOLD_BAUBLE.add(key.toString())) {
            ContentLog.LOGGER.info("The container on {} names the 1.12.2 setting 'bauble' as '{}'. It is read as curioSlot '{}'; write curioSlot on this line", key, old, mapped);
        }
        return mapped;
    }

    @Nullable private static ResourceLocation sheetOf(String named, ResourceLocation key) {
        if (named.isEmpty()) {
            ContentLog.LOGGER.error("The container on {} names an empty chestModel texture, so the vanilla chest is drawn instead", key);
            return null;
        }
        ResourceLocation asked = ResourceLocation.tryParse(named);
        if (asked == null) {
            ContentLog.LOGGER.error("The container on {} names the chestModel texture '{}', which is not a valid id, so the vanilla chest is drawn instead", key, named);
            return null;
        }
        return ResourceLocation.fromNamespaceAndPath(asked.getNamespace(), "textures/" + asked.getPath() + ".png");
    }


    @Nullable public static ScoreDef scoreFile(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Score file {} is empty, ignoring it", key);
            return null;
        }
        String name = GsonHelper.getAsString(json, "name", key.getPath()).trim();
        if (name.isEmpty() || name.length() > 16) {
            ContentLog.LOGGER.error("Score file {} names the objective '{}', and an objective name is 1 to 16 characters, so it is left out", key, name);
            return null;
        }
        String wanted = GsonHelper.getAsString(json, "criterion", "dummy").trim();
        ObjectiveCriteria criterion = ObjectiveCriteria.byName(wanted).orElse(null);
        if (criterion == null) {
            ContentLog.LOGGER.error("Objective {} scores on '{}', which is not a criterion the game knows, so it is left out. dummy, deathCount, playerKillCount, totalKillCount, health and any stat name are the ones there are", name, wanted);
            return null;
        }
        String slot = GsonHelper.getAsString(json, "display", "").trim();
        slot = "below_name".equals(slot) ? "belowName" : slot;
        if (!slot.isEmpty() && !displaySlotKnown(slot)) {
            ContentLog.LOGGER.error("Objective {} asks to be shown in '{}', which is not list, sidebar, belowName, below_name or sidebar.team.<color>, so it is not shown", name, slot);
            slot = "";
        }
        ObjectiveCriteria.RenderType render = json.has("render") ? ObjectiveCriteria.RenderType.byId(GsonHelper.getAsString(json, "render", "integer").trim()) : null;
        JsonObject results = GsonHelper.getAsJsonObject(json, "results", new JsonObject());
        JsonObject points = GsonHelper.getAsJsonObject(json, "points", new JsonObject());
        JsonObject ends = GsonHelper.getAsJsonObject(json, "ends", new JsonObject());
        Map<String, Integer> kills = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(points, "kill", new JsonObject()).entrySet()) { kills.put(entry.getKey().trim(), entry.getValue().getAsInt()); }
        return new ScoreDef(name, GsonHelper.getAsString(json, "displayName", name), criterion, slot, render,
                GsonHelper.getAsBoolean(json, "teamTotals", true),
                GsonHelper.getAsBoolean(json, "individuals", false),
                Map.copyOf(kills),
                GsonHelper.getAsInt(points, "death", 0),
                GsonHelper.getAsInt(ends, "atScore", 0),
                GsonHelper.getAsInt(ends, "afterMinutes", 0),
                GsonHelper.getAsInt(ends, "afterRounds", 0),
                GsonHelper.getAsBoolean(results, "card", false),
                GsonHelper.getAsString(results, "title", name + " results"),
                GsonHelper.getAsString(results, "icon", "").trim(),
                GsonHelper.getAsString(results, "image", "").trim(),
                cardColor(results, name),
                Math.max(20, GsonHelper.getAsInt(results, "seconds", 8) * 20),
                GsonHelper.getAsBoolean(json, "carries", false),
                GsonHelper.getAsBoolean(ends, "resets", false),
                Math.max(0, GsonHelper.getAsInt(ends, "intermissionSeconds", 10)),
                GsonHelper.getAsString(json, "awardsTo", "").trim(),
                GsonHelper.getAsBoolean(ends, "locksTeams", true),
                GsonHelper.getAsInt(points, "ownKill", 0),
                GsonHelper.getAsString(ends, "intermissionSays", "Round cooldown {seconds}"),
                GsonHelper.getAsString(ends, "startsSays", "Round starting in {seconds}"));
    }

    private static int cardColor(JsonObject results, String name) {
        String asked = GsonHelper.getAsString(results, "background", "").trim();
        return asked.isEmpty() ? 0x1E2630 : color(asked, name + " results background") & 0xFFFFFF;
    }

    @Nullable public static TeamDef teamFile(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Team file {} is empty, ignoring it", key);
            return null;
        }
        String name = GsonHelper.getAsString(json, "name", key.getPath()).trim();
        if (name.isEmpty() || name.length() > 16) {
            ContentLog.LOGGER.error("Team file {} names the team '{}', and a team name is 1 to 16 characters, so the team is left out", key, name);
            return null;
        }
        ChatFormatting color = ChatFormatting.getByName(GsonHelper.getAsString(json, "color", "white").trim().toLowerCase(Locale.ROOT));
        if (color == null || !color.isColor()) {
            ContentLog.LOGGER.error("Team {} asks for the color '{}', which is not one of the sixteen text colors, so it is white", name, GsonHelper.getAsString(json, "color", ""));
            color = ChatFormatting.WHITE;
        }
        boolean friendlyFire = GsonHelper.getAsBoolean(json, "friendlyFire", false);
        return new TeamDef(name, GsonHelper.getAsString(json, "displayName", name), color,
                GsonHelper.getAsString(json, "prefix", ""), GsonHelper.getAsString(json, "suffix", ""),
                friendlyFire, GsonHelper.getAsBoolean(json, "mobFriendlyFire", friendlyFire),
                GsonHelper.getAsBoolean(json, "seeFriendlyInvisibles", true),
                visible(json, "nameTags", key), visible(json, "deathMessages", key), collision(json, key),
                Json.strings(json, "entities"), Json.strings(json, "players"), box(json, key),
                GsonHelper.getAsBoolean(json, "joinable", true),
                leadWay(json, name), GsonHelper.getAsString(json, "leadOn", "").trim(), GsonHelper.getAsString(json, "leadIs", "").trim(),
                GsonHelper.getAsBoolean(json, "balance", false), GsonHelper.getAsBoolean(json, "scoreboard", true));
    }

    private static String leadWay(JsonObject json, String team) {
        String asked = GsonHelper.getAsString(json, "lead", TeamDef.NONE).trim();
        if (TeamDef.NONE.equals(asked) || TeamDef.TOP_SCORE.equals(asked) || TeamDef.APPOINTED.equals(asked) || TeamDef.VOTE.equals(asked) || TeamDef.CLAIM.equals(asked)) { return asked; }
        ContentLog.LOGGER.error("Team {} chooses its lead by '{}', which is not none, topScore, appointed, vote or claim, so it has no lead", team, asked);
        return TeamDef.NONE;
    }

    private static Team.Visibility visible(JsonObject json, String field, ResourceLocation key) {
        String asked = GsonHelper.getAsString(json, field, "always").trim();
        Team.Visibility held = Team.Visibility.byName(asked);
        if (held != null) { return held; }
        ContentLog.LOGGER.error("Team file {} sets {} to '{}', which is not always, never, hideForOtherTeams or hideForOwnTeam, so it is always", key, field, asked);
        return Team.Visibility.ALWAYS;
    }

    private static Team.CollisionRule collision(JsonObject json, ResourceLocation key) {
        String asked = GsonHelper.getAsString(json, "collision", "always").trim();
        Team.CollisionRule held = Team.CollisionRule.byName(asked);
        if (held != null) { return held; }
        ContentLog.LOGGER.error("Team file {} sets collision to '{}', which is not always, never, pushOtherTeams or pushOwnTeam, so it is always", key, asked);
        return Team.CollisionRule.ALWAYS;
    }

    @Nullable private static int[] box(JsonObject json, ResourceLocation key) {
        if (!json.has("spawnBox")) { return null; }
        if (!json.get("spawnBox").isJsonArray() || json.getAsJsonArray("spawnBox").size() != 6) {
            ContentLog.LOGGER.error("Team file {} has a spawnBox that is not six whole numbers, x y z to x y z, so nothing joins by where it spawns", key);
            return null;
        }
        int[] box = new int[6];
        int at = 0;
        for (JsonElement entry : json.getAsJsonArray("spawnBox")) { box[at++] = entry.getAsInt(); }
        for (int side = 0; side < 3; side++) {
            if (box[side] > box[side + 3]) {
                int swap = box[side];
                box[side] = box[side + 3];
                box[side + 3] = swap;
            }
        }
        return box;
    }

    private static boolean displaySlotKnown(String slot) { return Scoreboard.getDisplaySlotByName(slot) >= 0; }

}
