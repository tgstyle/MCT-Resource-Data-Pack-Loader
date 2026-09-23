package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentBlock;
import mctmods.resourcedatapackloader.content.block.ContentContainerBlock;
import mctmods.resourcedatapackloader.content.block.ContentCropBlock;
import mctmods.resourcedatapackloader.content.block.ContentFluids;
import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.DropDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.PotionDef;
import mctmods.resourcedatapackloader.content.def.SaplingDef;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.content.worldgen.ContentTreeTrunk;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.loot.LootFunctions;
import mctmods.resourcedatapackloader.pack.FallbackIcon;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentGenerated {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    static final String ITEM_GENERATED = "minecraft:item/generated";
    private static final String ITEM_HANDHELD = "minecraft:item/handheld";
    private static final String SURVIVES = "{\"condition\":\"minecraft:survives_explosion\"}";
    private static final String DECAY = "{\"function\":\"minecraft:explosion_decay\"}";
    private static final Set<String> CURIOS_PRESETS = Set.of("back", "belt", "body", "bracelet", "charm", "curio", "hands", "head", "necklace", "ring");
    private static final List<String> BAUBLES_SLOTS = List.of("necklace", "ring", "belt", "head", "body", "charm");
    private static final int SHEET_COLUMNS = 8;
    private static final String[] STATUS_SHEET = {"speed", "slowness", "haste", "mining_fatigue", "strength", "weakness", "poison", "regeneration", "invisibility", "hunger", "jump_boost", "nausea", "night_vision", "blindness", "resistance", "fire_resistance", "water_breathing", "wither", "absorption", "levitation", "glowing", "luck", "unluck", "health_boost"};

    private ContentGenerated() {}

    public static void generate() {
        GeneratedResources.clear();
        Map<String, Set<String>> blockTags = new LinkedHashMap<>();
        Map<String, Set<String>> itemTags = new LinkedHashMap<>();
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            try { block(entry, blockTags, itemTags); }
            catch (RuntimeException ex) { ContentLog.LOGGER.error("Could not generate the files for block {}", entry.id(), ex); }
        }
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            if (entry.block() != null) { continue; }
            try { item(entry, itemTags); }
            catch (RuntimeException ex) { ContentLog.LOGGER.error("Could not generate the files for item {}", entry.id(), ex); }
        }
        for (ContentFluids.Made made : ContentFluids.made()) {
            try {
                fluidBlock(made);
                fluidBucket(made);
            }
            catch (RuntimeException ex) { ContentLog.LOGGER.error("Could not generate the files for fluid {}", made.def.id(), ex); }
        }
        blockTags(blockTags);
        tags(itemTags, Map.of(), ContentFormats.ITEM_TAGS);
        worn();
        potionIcons();
        if (GeneratedResources.count() > 0) { Summary.info("generated", "Generated " + GeneratedResources.count() + " blockstate, model, loot table, tag and feature file(s) that the packs did not ship themselves"); }
    }

    static void cropLoot() {
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            if (!(entry.block() instanceof ContentCropBlock)) { continue; }
            String path = ContentFormats.LOOT_FOLDER + "/blocks/" + entry.id().getPath() + ".json";
            if (!provided(PackType.SERVER_DATA, entry.id().getNamespace(), path)) { data(entry.id().getNamespace(), path, loot(entry, entry.def().type())); }
        }
    }

    private static void block(ContentRegistry.BlockEntry entry, Map<String, Set<String>> blockTags, Map<String, Set<String>> itemTags) {
        BlockDef def = entry.def();
        String namespace = entry.id().getNamespace();
        String name = entry.id().getPath();
        String type = def.type();
        boolean hasItem = ContentRegistry.items().stream().anyMatch(item -> item.block() == entry);
        if (!provided(PackType.CLIENT_RESOURCES, namespace, "blockstates/" + name + ".json")) { ContentGeneratedModels.models(entry, namespace, name, type); }
        if (hasItem && !def.itemModelFromFile() && !provided(PackType.CLIENT_RESOURCES, namespace, "models/item/" + name + ".json")) { ContentGeneratedModels.itemModel(def, namespace, name, type); }
        if (!provided(PackType.SERVER_DATA, namespace, ContentFormats.LOOT_FOLDER + "/blocks/" + name + ".json")) { data(namespace, ContentFormats.LOOT_FOLDER + "/blocks/" + name + ".json", loot(entry, type)); }
        if (entry.isMain()) {
            tagBlock(entry, blockTags, itemTags, hasItem);
            SaplingDef sapling = def.sapling();
            if (ContentBlockTypes.SAPLING.equals(type) && sapling != null && sapling.growsVanilla() && !provided(PackType.SERVER_DATA, namespace, "worldgen/configured_feature/" + name + "_tree.json")) {
                data(namespace, "worldgen/configured_feature/" + name + "_tree.json", tree(sapling));
            }
        }
    }

    private static void fluidBucket(ContentFluids.Made made) {
        if (made.bucket == null) { return; }
        String namespace = made.bucketId().getNamespace();
        String path = "models/item/" + made.bucketId().getPath() + ".json";
        if (provided(PackType.CLIENT_RESOURCES, namespace, path)) { return; }
        asset(namespace, path, obj("parent", "forge:item/bucket", "loader", "forge:fluid_container", "fluid", made.def.id().toString()));
    }

    private static void fluidBlock(ContentFluids.Made made) {
        if (made.block == null) { return; }
        String namespace = made.def.key().getNamespace();
        String name = made.def.key().getPath();
        if (!provided(PackType.CLIENT_RESOURCES, namespace, "blockstates/" + name + ".json")) {
            ContentGeneratedModels.blockstate(namespace, name, obj("variants", obj("", obj("model", namespace + ":block/" + name))));
        }
        if (!provided(PackType.CLIENT_RESOURCES, namespace, "models/block/" + name + ".json")) {
            asset(namespace, "models/block/" + name + ".json", obj("textures", obj("particle", made.def.still().toString())));
        }
    }

    private static void worn() {
        Set<String> slots = new LinkedHashSet<>();
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            ItemDef def = entry.def();
            if (entry.block() == null && def != null && def.holds() != null && def.holds().worn()) {
                if (ContainerDef.ANY_SLOT.equals(def.holds().curioSlot())) { slots.addAll(BAUBLES_SLOTS); }
                else { slots.add(def.holds().curioSlot()); }
            }
        }
        if (slots.isEmpty()) { return; }
        JsonArray named = arr();
        for (String slot : slots) {
            named.add(slot);
            if (ContainerDef.RING_SLOT.equals(slot)) { data(ResourceDataPackLoader.MOD_ID, "curios/slots/" + slot + ".json", obj("size", 2)); }
            else if (!CURIOS_PRESETS.contains(slot)) { data(ResourceDataPackLoader.MOD_ID, "curios/slots/" + slot + ".json", obj("validators", arr("curios:tag"))); }
        }
        data(ResourceDataPackLoader.MOD_ID, "curios/entities/worn.json", obj("entities", arr("minecraft:player"), "slots", named));
    }

    private static void item(ContentRegistry.ItemEntry entry, Map<String, Set<String>> itemTags) {
        String namespace = entry.id().getNamespace();
        String name = entry.id().getPath();
        ItemDef def = entry.def();
        if (!provided(PackType.CLIENT_RESOURCES, namespace, "models/item/" + name + ".json")) {
            String parent = def != null && "tool".equals(def.type()) ? ITEM_HANDHELD : ITEM_GENERATED;
            asset(namespace, "models/item/" + name + ".json", obj("parent", parent, "textures", obj("layer0", namespace + ":item/" + name)));
        }
        if (entry.variant() != null) {
            for (String tag : entry.variant().tags()) { tag(itemTags, tag, entry.id()); }
        }
        if (def != null) {
            for (String tag : ContentFormats.equipmentTags(def)) { tag(itemTags, tag, entry.id()); }
            if (def.holds() != null && def.holds().worn()) { tag(itemTags, "curios:" + def.holds().curioSlot(), entry.id()); }
        }
    }

    private static JsonObject loot(ContentRegistry.BlockEntry entry, String type) {
        BlockDef def = entry.def();
        BlockVariant variant = entry.variant();
        String self = entry.isMain() ? entry.id().toString() : variant.id().toString();
        JsonArray pools = new JsonArray();
        switch (type) {
            case ContentBlockTypes.CROP -> {
                JsonObject grown = obj("condition", "minecraft:block_state_property", "block", entry.id().toString(), "properties", obj("age", String.valueOf(def.cropMaxAge())));
                ContentCropBlock crop = (ContentCropBlock) entry.block();
                String produce = ContentStacks.id(crop.produce());
                String seed = ContentStacks.id(crop.seed());
                pools.add(pool(arr(item(produce)), arr(grown)));
                pools.add(pool(arr(item(seed)), arr(obj("condition", "minecraft:inverted", "term", grown))));
                pools.add(pool(arr(entryWith(seed, arr(obj("function", "minecraft:set_count", "count", 0, "add", false), obj("function", "minecraft:apply_bonus", "enchantment", "minecraft:fortune", "formula", "minecraft:binomial_with_bonus_count", "parameters", obj("extra", 3, "probability", (def.cropMaxAge() + 1) / (2.0 * def.cropMaxAge())))))), arr(grown)));
            }
            case ContentBlockTypes.LEAVES -> {
                JsonObject silk = ContentFormats.silkTouch();
                JsonObject shears = ContentFormats.shears();
                pools.add(pool(arr(item(self)), arr(silk)));
                pools.add(pool(arr(item(self)), arr(shears, inverted(silk))));
                ResourceLocation sapling = ContentParser.location(def.leafSapling());
                if (sapling != null && def.leafSaplingChance() > 0) {
                    double chance = def.leafSaplingChance() / 100.0;
                    JsonObject bonus = obj("condition", "minecraft:table_bonus", "enchantment", "minecraft:fortune", "chances", arr(chance, Math.min(1.0, chance + 0.02), Math.min(1.0, chance + 0.04), Math.min(1.0, chance + 0.06)));
                    pools.add(pool(arr(entryWith(sapling.toString(), arr(json(DECAY)))), arr(inverted(silk), inverted(shears), bonus, json(SURVIVES))));
                }
            }
            case ContentBlockTypes.DOOR -> pools.add(pool(arr(item(self)), arr(obj("condition", "minecraft:block_state_property", "block", entry.id().toString(), "properties", obj("half", "lower")), json(SURVIVES))));
            case ContentBlockTypes.SLAB -> {
                JsonObject doubled = obj("function", "minecraft:set_count", "count", 2, "add", false, "conditions", arr(obj("condition", "minecraft:block_state_property", "block", entry.id().toString(), "properties", obj("type", "double"))));
                pools.add(pool(arr(entryWith(self, arr(doubled, json(DECAY)))), arr(json(SURVIVES))));
            }
            case ContentBlockTypes.SAPLING, ContentBlockTypes.FLOWER -> pools.add(pool(arr(item(self)), arr(json(SURVIVES))));
            case ContentBlockTypes.CANE, ContentBlockTypes.VINE -> {
                boolean own = def.growth() == null || def.growth().drop().isEmpty();
                String dropped = own ? self : def.growth().drop();
                int count = def.growth() == null ? 1 : def.growth().dropCount();
                JsonArray functions = count > 1 ? arr(obj("function", "minecraft:set_count", "count", count, "add", false)) : arr();
                if (own || !ContentBlockTypes.VINE.equals(type)) {
                    pools.add(pool(arr(entryWith(dropped, functions)), arr(json(SURVIVES))));
                    break;
                }
                JsonObject shears = ContentFormats.shears();
                pools.add(pool(arr(item(self)), arr(shears)));
                pools.add(pool(arr(entryWith(dropped, functions)), arr(inverted(shears), json(SURVIVES))));
            }
            default -> {
                boolean rolled = entry.block() instanceof ContentBlock || entry.block() instanceof ContentContainerBlock;
                if (!rolled || variant.drops().isEmpty() || def.opensWith() != null) {
                    pools.add(pool(arr(item(self)), arr(json(SURVIVES))));
                    break;
                }
                JsonObject silk = ContentFormats.silkTouch();
                if (def.silkHarvest()) { pools.add(pool(arr(item(self)), arr(silk))); }
                JsonArray unsilked = def.silkHarvest() ? arr(inverted(silk), json(SURVIVES)) : arr(json(SURVIVES));
                JsonArray weighted = new JsonArray();
                for (DropDef drop : variant.drops()) {
                    if (drop.isEntity() || drop.item() == null) { continue; }
                    JsonObject dropped = entryWith(drop.item().toString(), dropFunctions(drop));
                    if (drop.weighted()) {
                        dropped.addProperty("weight", drop.weight());
                        weighted.add(dropped);
                    }
                    else { pools.add(pool(arr(dropped), unsilked)); }
                }
                if (!weighted.isEmpty()) { pools.add(pool(weighted, unsilked)); }
            }
        }
        return obj("type", "minecraft:block", "pools", pools);
    }

    private static JsonArray dropFunctions(DropDef drop) {
        JsonArray functions = new JsonArray();
        if (!drop.amount().fixed() || drop.amount().least() != 1) {
            JsonElement count = drop.amount().fixed() ? new JsonPrimitive(drop.amount().least()) : obj("type", "minecraft:uniform", "min", drop.amount().least(), "max", drop.amount().most());
            functions.add(obj("function", "minecraft:set_count", "count", count, "add", false));
        }
        if (drop.chance() < 100 || drop.hasBonus()) { functions.add(obj("function", LootFunctions.NAMESPACE + ":drop_roll", "chance", drop.chance(), "bonusChance", arr(Arrays.stream(drop.bonusChance()).boxed().toArray()))); }
        return functions;
    }

    private static JsonObject pool(JsonArray entries, JsonArray conditions) {
        JsonObject pool = obj("rolls", 1, "bonus_rolls", 0, "entries", entries);
        if (!conditions.isEmpty()) { pool.add("conditions", conditions); }
        return pool;
    }

    private static JsonObject item(String id) { return obj("type", "minecraft:item", "name", id); }

    private static JsonObject entryWith(String id, JsonArray functions) {
        JsonObject entry = item(id);
        if (!functions.isEmpty()) { entry.add("functions", functions); }
        return entry;
    }

    private static JsonObject inverted(JsonObject term) { return obj("condition", "minecraft:inverted", "term", term); }

    private static void tagBlock(ContentRegistry.BlockEntry entry, Map<String, Set<String>> blockTags, Map<String, Set<String>> itemTags, boolean hasItem) {
        BlockDef def = entry.def();
        ResourceLocation id = entry.id();
        for (String tag : entry.variant().tags()) {
            tag(blockTags, tag, id);
            if (hasItem) { tag(itemTags, tag, id); }
        }
        harvestTags(blockTags, id, def.harvestTool(), ContentBlockTypes.harvestLevel(def, entry.variant()));
        materialTags(blockTags, id, def);
        boolean wood = "wood".equals(def.material());
        switch (def.type()) {
            case ContentBlockTypes.FENCE -> {
                both(blockTags, itemTags, hasItem, "minecraft:fences", id);
                if (wood) { both(blockTags, itemTags, hasItem, "minecraft:wooden_fences", id); }
            }
            case ContentBlockTypes.WALL -> both(blockTags, itemTags, hasItem, "minecraft:walls", id);
            case ContentBlockTypes.SLAB -> both(blockTags, itemTags, hasItem, "minecraft:slabs", id);
            case ContentBlockTypes.STAIRS -> both(blockTags, itemTags, hasItem, "minecraft:stairs", id);
            case ContentBlockTypes.DOOR -> {
                both(blockTags, itemTags, hasItem, "minecraft:doors", id);
                if (wood) { both(blockTags, itemTags, hasItem, "minecraft:wooden_doors", id); }
            }
            case ContentBlockTypes.TRAPDOOR -> both(blockTags, itemTags, hasItem, "minecraft:trapdoors", id);
            case ContentBlockTypes.FENCE_GATE -> both(blockTags, itemTags, hasItem, "minecraft:fence_gates", id);
            case ContentBlockTypes.LEAVES -> both(blockTags, itemTags, hasItem, "minecraft:leaves", id);
            case ContentBlockTypes.LOG -> both(blockTags, itemTags, hasItem, "minecraft:logs", id);
            case ContentBlockTypes.SAPLING -> both(blockTags, itemTags, hasItem, "minecraft:saplings", id);
            case ContentBlockTypes.FLOWER -> {
                both(blockTags, itemTags, hasItem, "minecraft:small_flowers", id);
                both(blockTags, itemTags, hasItem, "minecraft:flowers", id);
            }
            case ContentBlockTypes.CROP -> tag(blockTags, "minecraft:crops", id);
            case ContentBlockTypes.LADDER, ContentBlockTypes.VINE -> tag(blockTags, "minecraft:climbable", id);
            default -> { }
        }
    }

    private static void both(Map<String, Set<String>> blockTags, Map<String, Set<String>> itemTags, boolean hasItem, String tag, ResourceLocation id) {
        tag(blockTags, tag, id);
        if (hasItem) { tag(itemTags, tag, id); }
    }

    public static void jobSites(Set<ResourceLocation> sites) {
        if (sites.isEmpty()) { return; }
        Map<String, Set<String>> poiTags = new LinkedHashMap<>();
        for (ResourceLocation site : sites) {
            tag(poiTags, "minecraft:acquirable_job_site", site);
            tag(poiTags, "minecraft:job_site", site);
        }
        tags(poiTags, Map.of(), ContentFormats.POI_TAGS);
    }

    public static void retag() {
        GeneratedResources.remove(PackType.SERVER_DATA, ContentFormats.BLOCK_TAGS + "/");
        Map<String, Set<String>> blockTags = new LinkedHashMap<>();
        Map<String, Set<String>> itemTags = new LinkedHashMap<>();
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            if (entry.isMain()) { tagBlock(entry, blockTags, itemTags, false); }
        }
        blockTags(blockTags);
    }

    private static void blockTags(Map<String, Set<String>> blockTags) {
        Map<String, Set<String>> removed = new LinkedHashMap<>();
        ContentOverrides.harvestTags(blockTags, removed);
        tags(blockTags, removed, ContentFormats.BLOCK_TAGS);
    }

    public static void harvestTags(Map<String, Set<String>> tags, ResourceLocation id, String tool, int level) {
        switch (tool) {
            case "pickaxe", "axe", "shovel", "hoe" -> tag(tags, "minecraft:mineable/" + tool, id);
            case "sword" -> tag(tags, ContentFormats.MINEABLE_SWORD, id);
            default -> { }
        }
        if (level == 1) { tag(tags, "minecraft:needs_stone_tool", id); }
        else if (level == 2) { tag(tags, "minecraft:needs_iron_tool", id); }
        else if (level == 3) { tag(tags, "minecraft:needs_diamond_tool", id); }
        else if (level >= 4) { tag(tags, ContentFormats.NEEDS_NETHERITE_TOOL, id); }
    }

    private static void materialTags(Map<String, Set<String>> tags, ResourceLocation id, BlockDef def) {
        if (ContentBlockTypes.STAIRS.equals(def.type()) || ContentBlockTypes.WALL.equals(def.type())) { return; }
        switch (ContentTypes.materialName(def)) {
            case "rock", "iron", "anvil" -> tag(tags, "minecraft:mineable/pickaxe", id);
            case "wood" -> tag(tags, "minecraft:mineable/axe", id);
            case "plants", "vine" -> {
                tag(tags, "minecraft:mineable/axe", id);
                tag(tags, "minecraft:sword_efficient", id);
            }
            case "coral", "leaves", "gourd" -> tag(tags, "minecraft:sword_efficient", id);
            default -> { }
        }
    }

    public static void tag(Map<String, Set<String>> tags, String tag, ResourceLocation id) { tags.computeIfAbsent(tag, k -> new LinkedHashSet<>()).add(id.toString()); }

    private static void tags(Map<String, Set<String>> tags, Map<String, Set<String>> removed, String folder) {
        Set<String> names = new LinkedHashSet<>(tags.keySet());
        names.addAll(removed.keySet());
        for (String name : names) {
            ResourceLocation tag = ResourceLocation.tryParse(name);
            if (tag == null) { continue; }
            JsonArray values = new JsonArray();
            for (String value : tags.getOrDefault(name, Set.of())) { values.add(value); }
            JsonObject file = obj("replace", false, "values", values);
            Set<String> gone = removed.get(name);
            if (gone != null) {
                JsonArray remove = new JsonArray();
                for (String value : gone) { remove.add(value); }
                file.add("remove", remove);
            }
            data(tag.getNamespace(), folder + "/" + tag.getPath() + ".json", file);
        }
    }

    private static JsonObject tree(SaplingDef sapling) {
        JsonArray decoratorList = arr();
        if (sapling.vines()) { decoratorList.add(obj("type", "minecraft:leave_vine", "probability", 0.25)); }
        JsonObject config = obj(
                "trunk_provider", state(sapling.log()),
                "trunk_placer", obj("type", LootFunctions.NAMESPACE + ":" + ContentTreeTrunk.NAME, "base_height", Math.max(1, sapling.height()), "height_rand_a", 2, "height_rand_b", 0),
                "foliage_provider", state(sapling.leaves()),
                "foliage_placer", obj("type", "minecraft:blob_foliage_placer", "radius", 2, "offset", 0, "height", 3),
                "dirt_provider", state("minecraft:dirt"),
                "minimum_size", obj("type", "minecraft:two_layers_feature_size", "limit", 1, "lower_size", 0, "upper_size", 1),
                "ignore_vines", true,
                "force_dirt", false,
                "decorators", decoratorList);
        return obj("type", "minecraft:tree", "config", config);
    }

    private static JsonObject state(String block) { return obj("type", "minecraft:simple_state_provider", "state", obj("Name", block)); }

    private static void potionIcons() {
        JsonArray aliases = new JsonArray();
        for (PotionDef def : ContentPotions.defs()) {
            ResourceLocation key = def.key();
            String path = "textures/mob_effect/" + key.getPath() + ".png";
            if (provided(PackType.CLIENT_RESOURCES, key.getNamespace(), path)) { continue; }
            String vanilla = def.iconTexture().isEmpty() ? sheetIcon(def) : null;
            if (vanilla != null) {
                aliases.add(obj("type", "minecraft:single", "resource", "minecraft:mob_effect/" + vanilla, "sprite", key.getNamespace() + ":mob_effect/" + key.getPath()));
                continue;
            }
            byte[] icon = potionIcon(def);
            if (icon != null) { GeneratedResources.put(PackType.CLIENT_RESOURCES, key.getNamespace(), path, icon); }
        }
        if (!aliases.isEmpty()) { asset(ResourceLocation.DEFAULT_NAMESPACE, "atlases/mob_effects.json", obj("sources", aliases)); }
    }

    @Nullable private static String sheetIcon(PotionDef def) {
        int index = def.iconX() + def.iconY() * SHEET_COLUMNS;
        if (index >= 0 && index < STATUS_SHEET.length) { return STATUS_SHEET[index]; }
        ContentLog.LOGGER.error("Potion {} names icon {}, {}, which is not on the vanilla status icon sheet, using the RDPL icon", def.key(), def.iconX(), def.iconY());
        return null;
    }

    @Nullable private static byte[] potionIcon(PotionDef def) {
        ResourceLocation texture = def.iconTexture().isEmpty() || FallbackIcon.TEXTURE.equals(def.iconTexture()) ? null : ResourceLocation.tryParse(def.iconTexture());
        byte[] icon = texture == null ? null : PackManager.get().bytes(PackType.CLIENT_RESOURCES, texture.getNamespace(), texture.getPath());
        if (texture != null && icon == null) { ContentLog.LOGGER.error("Potion {} names iconTexture {}, which no pack provides, using the RDPL icon", def.key(), def.iconTexture()); }
        return icon == null ? FallbackIcon.bytes() : icon;
    }

    static boolean provided(PackType type, String namespace, String path) { return PackManager.get().provides(type, namespace, path); }

    static void asset(String namespace, String path, JsonObject json) { GeneratedResources.put(PackType.CLIENT_RESOURCES, namespace, path, GSON.toJson(json)); }

    private static void data(String namespace, String path, JsonObject json) { GeneratedResources.put(PackType.SERVER_DATA, namespace, path, GSON.toJson(json)); }

    private static JsonObject json(String literal) { return JsonParser.parseString(literal).getAsJsonObject(); }

    static JsonObject obj(Object... pairs) {
        JsonObject json = new JsonObject();
        for (int i = 0; i + 1 < pairs.length; i += 2) { json.add(String.valueOf(pairs[i]), element(pairs[i + 1])); }
        return json;
    }

    static JsonArray arr(Object... values) {
        JsonArray array = new JsonArray();
        for (Object value : values) { array.add(element(value)); }
        return array;
    }

    private static JsonElement element(Object value) {
        if (value instanceof JsonElement held) { return held; }
        if (value instanceof Boolean held) { return new JsonPrimitive(held); }
        if (value instanceof Number held) { return new JsonPrimitive(held); }
        if (value instanceof List<?> held) { return arr(held.toArray()); }
        return new JsonPrimitive(String.valueOf(value));
    }
}
