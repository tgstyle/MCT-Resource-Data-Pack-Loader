package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.DropDef;
import mctmods.resourcedatapackloader.content.def.FluidDef;
import mctmods.resourcedatapackloader.content.def.GrowthDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.ItemVariant;
import mctmods.resourcedatapackloader.content.def.MaterialDef;
import mctmods.resourcedatapackloader.content.def.SaplingDef;
import mctmods.resourcedatapackloader.content.def.TabDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentParser {
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
        boolean opaque = GsonHelper.getAsBoolean(json, "opaque", true);
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
                GsonHelper.getAsString(json, "openSound", "").trim());
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
                GsonHelper.getAsString(json, "container", "").trim(),
                GsonHelper.getAsString(json, "material", "").trim(),
                GsonHelper.getAsString(json, "toolClass", "").trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsString(json, "slot", "").trim().toLowerCase(Locale.ROOT),
                GsonHelper.getAsString(json, "crop", "").trim(),
                GsonHelper.getAsString(json, "soil", "minecraft:farmland").trim(),
                Json.strings(json, "potionTypes"),
                GsonHelper.getAsFloat(json, "attackSpeed", Float.NaN),
                Math.max(0, GsonHelper.getAsInt(json, "cooldown", 0)));
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
}
