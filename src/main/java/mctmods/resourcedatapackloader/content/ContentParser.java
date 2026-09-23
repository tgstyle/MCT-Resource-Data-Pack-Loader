package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.*;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.content.types.ContentItemTypes;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawning;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import static mctmods.resourcedatapackloader.util.Json.strings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentParser {
    public static final String PLACEHOLDER = "open";
    public static final String DEFAULT_STILL = "minecraft:blocks/water_still";
    public static final String DEFAULT_FLOW = "minecraft:blocks/water_flow";
    static final Gson GSON = new GsonBuilder().create();
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
        boolean opaque = JsonUtils.getBoolean(json, "opaque", !ContentParserContainers.chested(json));
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
                MathHelper.clamp(JsonUtils.getInt(json, "maxAge", 7), 1, 7),
                sapling(json),
                portal(json),
                growth(json),
                strings(json, "plantTypes"),
                behaviors(key, json),
                JsonUtils.getString(json, "tint", ""),
                JsonUtils.getString(json, "leafSapling", ""),
                MathHelper.clamp(JsonUtils.getInt(json, "leafSaplingChance", 5), 0, 100),
                ContentParserContainers.opensWith(json),
                JsonUtils.getString(json, "openSound", "").trim(),
                ContentParserContainers.container(key, json),
                bell(json));
    }

    @Nullable private static BellDef bell(JsonObject json) {
        if (!json.has("bell") || !json.get("bell").isJsonObject()) { return null; }
        JsonObject held = JsonUtils.getJsonObject(json, "bell");
        return new BellDef(JsonUtils.getBoolean(held, "swing", BellDef.PLAIN.swing),
                JsonUtils.getString(held, "sound", BellDef.PLAIN.sound).trim(),
                JsonUtils.getString(held, "resonateSound", BellDef.PLAIN.resonateSound).trim());
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
                JsonUtils.getBoolean(entry, "owned", true),
                JsonUtils.getBoolean(entry, "walkIn", false));
    }

    private static List<String> behaviors(ResourceLocation key, JsonObject json) {
        List<String> found = new ArrayList<>();
        for (String raw : strings(json, "behavesAs")) {
            String name = ContentSpawning.normalize(raw);
            if (!ContentSpawning.known(name)) {
                ContentLog.LOGGER.error("Block {} says it behaves as '{}', which is not one of {}, ignoring it", key, raw, ContentSpawning.describe());
                continue;
            }
            found.add(name);
        }
        return Collections.unmodifiableList(found);
    }

    @Nullable private static GrowthDef growth(JsonObject json) {
        if (!json.has("growth")) { return null; }
        JsonObject entry = JsonUtils.getJsonObject(json, "growth");
        return new GrowthDef(Math.max(1, JsonUtils.getInt(entry, "maxHeight", 3)),
                MathHelper.clamp(JsonUtils.getInt(entry, "stages", 16), 1, 16),
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
                picks(entry, "structures", "structure"),
                JsonUtils.getString(entry, "log", "minecraft:log"),
                JsonUtils.getString(entry, "leaves", "minecraft:leaves"),
                Math.max(1, JsonUtils.getInt(entry, "height", 4)));
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
                strings(json, "oreDict"),
                JsonUtils.getFloat(json, "hardness", 1.0F),
                JsonUtils.getFloat(json, "resistance", 5.0F),
                JsonUtils.getInt(json, "harvestLevel", 0),
                JsonUtils.getInt(json, "light", 0),
                portal(json),
                Collections.unmodifiableList(drops), false);
    }

    @Nullable private static DropDef drop(ResourceLocation key, String name, JsonObject json) {
        String block = JsonUtils.getString(json, "block", "");
        String entity = JsonUtils.getString(json, "entity", "");
        if (block.isEmpty() && entity.isEmpty()) {
            ContentLog.LOGGER.error("A drop for '{}' in {} names neither a block nor an entity, skipping it", name, key);
            return null;
        }
        if (!block.isEmpty() && !entity.isEmpty()) {
            ContentLog.LOGGER.error("A drop for '{}' in {} names both block {} and entity {}, using the entity", name, key, block, entity);
        }
        int[] chances = NO_CHANCE;
        if (json.has("bonusChance")) {
            JsonArray array = JsonUtils.getJsonArray(json, "bonusChance");
            chances = new int[array.size()];
            for (int i = 0; i < array.size(); i++) { chances[i] = array.get(i).getAsInt(); }
        }
        boolean guaranteed = JsonUtils.getBoolean(json, "guaranteed", true);
        return new DropDef(block.isEmpty() ? null : new ResourceLocation(block),
                entity.isEmpty() ? null : new ResourceLocation(entity),
                JsonUtils.getInt(json, "meta", 0),
                amount(json, "amount", 1, 0),
                MathHelper.clamp(JsonUtils.getInt(json, "chance", guaranteed ? 100 : 0), 0, 100),
                Math.max(0, JsonUtils.getInt(json, "weight", 0)),
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
                    strings(variant, "oreDict"),
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
                ContentParserContainers.remainder(json),
                JsonUtils.getString(json, "material", ""),
                JsonUtils.getString(json, "toolClass", ""),
                JsonUtils.getString(json, "slot", ""),
                JsonUtils.getString(json, "crop", ""),
                JsonUtils.getString(json, "soil", "minecraft:farmland"),
                strings(json, "potionTypes"),
                JsonUtils.getFloat(json, "attackSpeed", Float.NaN),
                Math.max(0, JsonUtils.getInt(json, "cooldown", 0)),
                ContentParserContainers.holds(key, json));
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

    @Nullable public static ExposureDef exposure(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Exposure definition {} is empty, ignoring it", key);
            return null;
        }
        List<ExposureLevelDef> levels = new ArrayList<>();
        for (JsonElement element : JsonUtils.getJsonArray(json, "levels", new JsonArray())) {
            if (!element.isJsonObject()) {
                ContentLog.LOGGER.error("A level in {} is not an object, skipping it", key);
                continue;
            }
            JsonObject level = element.getAsJsonObject();
            String effect = JsonUtils.getString(level, "effect", "");
            if (effect.isEmpty()) {
                ContentLog.LOGGER.error("A level in {} names no effect, skipping it", key);
                continue;
            }
            List<PotionEffectDef> extras = new ArrayList<>();
            for (JsonElement extraElement : JsonUtils.getJsonArray(level, "effects", new JsonArray())) {
                if (!extraElement.isJsonObject()) { continue; }
                JsonObject extra = extraElement.getAsJsonObject();
                String potion = JsonUtils.getString(extra, "potion", "");
                if (potion.isEmpty()) {
                    ContentLog.LOGGER.error("An extra effect in {} names no potion, skipping it", key);
                    continue;
                }
                extras.add(new PotionEffectDef(potion,
                        Math.max(0, JsonUtils.getInt(extra, "duration", 0)),
                        Math.max(0, JsonUtils.getInt(extra, "amplifier", 0)),
                        JsonUtils.getBoolean(extra, "ambient", false),
                        JsonUtils.getBoolean(extra, "showParticles", false)));
            }
            levels.add(new ExposureLevelDef(effect,
                    Math.max(0.0F, JsonUtils.getFloat(level, "damage", 0.0F)),
                    Math.max(0, JsonUtils.getInt(level, "damageInterval", 160)),
                    Collections.unmodifiableList(extras)));
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
        return new ExposureDef(key,
                Math.max(1, JsonUtils.getInt(json, "scanInterval", 20)),
                Math.max(0, JsonUtils.getInt(json, "range", 10)),
                JsonUtils.getBoolean(json, "skipsCreative", true),
                Math.max(0, JsonUtils.getInt(json, "sourcesForNextLevel", 0)),
                JsonUtils.getString(json, "immunity", "").trim(),
                blocks, items, Collections.unmodifiableList(levels));
    }

    private static Map<ResourceLocation, Integer> leveledNames(ResourceLocation key, JsonObject json, String member) {
        Map<ResourceLocation, Integer> out = new LinkedHashMap<>();
        for (String entry : strings(json, member)) {
            String named = entry.trim();
            int level = 1;
            int split = named.indexOf('=');
            if (split >= 0) {
                try {
                    level = Math.max(1, Integer.parseInt(named.substring(split + 1).trim()));
                } catch (NumberFormatException bad) {
                    ContentLog.LOGGER.error("The {} entry '{}' in {} has a level that is not a number, so it counts as level 1", member, entry, key);
                }
                named = named.substring(0, split).trim();
            }
            if (named.isEmpty()) { continue; }
            out.put(new ResourceLocation(named), level);
        }
        return out;
    }

    static AmountDef amount(JsonObject json, String key, int fallback, int floor) {
        if (!json.has(key)) { return AmountDef.of(Math.max(floor, fallback)); }
        JsonElement element = json.get(key);
        if (!element.isJsonObject()) { return AmountDef.of(Math.max(floor, element.getAsInt())); }
        JsonObject range = element.getAsJsonObject();
        int least = Math.max(floor, JsonUtils.getInt(range, "min", fallback));
        return new AmountDef(least, Math.max(least, JsonUtils.getInt(range, "max", least)));
    }

    static List<PickDef> picks(JsonObject entry, String listKey, String nameKey) {
        if (!entry.has(listKey)) { return Collections.emptyList(); }
        List<PickDef> picked = new ArrayList<>();
        for (JsonElement element : JsonUtils.getJsonArray(entry, listKey)) {
            if (element.isJsonPrimitive()) { picked.add(new PickDef(element.getAsString().trim().toLowerCase(Locale.ROOT), 1)); }
            else {
                JsonObject held = element.getAsJsonObject();
                picked.add(new PickDef(JsonUtils.getString(held, nameKey, "").trim().toLowerCase(Locale.ROOT), JsonUtils.getInt(held, "weight", 1)));
            }
        }
        return Collections.unmodifiableList(picked);
    }

    public static BlockMatchDef match(ResourceLocation key, JsonElement element) {
        if (!element.isJsonObject()) { return match(key, element.getAsString()); }
        JsonObject entry = element.getAsJsonObject();
        return new BlockMatchDef(new ResourceLocation(JsonUtils.getString(entry, "block", "minecraft:stone")), JsonUtils.getInt(entry, "meta", -1), Json.map(entry, "properties"));
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

    public static String placeholderName(int meta, int digits) {
        StringBuilder builder = new StringBuilder(PLACEHOLDER);
        String number = Integer.toString(meta);
        for (int i = number.length(); i < digits; i++) { builder.append('0'); }
        return builder.append(number).toString();
    }
}
