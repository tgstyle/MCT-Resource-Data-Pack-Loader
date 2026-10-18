package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.compat.LineCompat;
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
import mctmods.resourcedatapackloader.content.item.ContentBucketItem;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.content.worldgen.ContentTreeTrunk;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.loot.LootFunctions;
import mctmods.resourcedatapackloader.pack.FallbackIcon;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.util.WorldgenJson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentGenerated {
    static final String ITEM_GENERATED = "minecraft:item/generated";
    private static final String ITEM_HANDHELD = "minecraft:item/handheld";
    private static final String SURVIVES = "{\"condition\":\"minecraft:survives_explosion\"}";
    private static final String DECAY = "{\"function\":\"minecraft:explosion_decay\"}";
    private static final Set<String> CURIOS_PRESETS = Set.of("back", "belt", "body", "bracelet", "charm", "curio", "hands", "head", "necklace", "ring");
    private static final List<String> BAUBLES_SLOTS = List.of("necklace", "ring", "belt", "head", "body", "charm");
    private static final int SHEET_COLUMNS = 8;
    private static final int FORTUNE_BONUS = 2;
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
            if (entry.block() != null || entry.item() instanceof ContentBucketItem) { continue; }
            try { item(entry, itemTags); }
            catch (RuntimeException ex) { ContentLog.LOGGER.error("Could not generate the files for item {}", entry.id(), ex); }
        }
        for (ContentFluids.Made made : ContentFluids.made()) {
            try {
                fluidBlock(made);
                if (made.bucket != null) { ContentGeneratedItems.bucket(made); }
            }
            catch (RuntimeException ex) { ContentLog.LOGGER.error("Could not generate the files for fluid {}", made.def.id(), ex); }
        }
        blockTags(blockTags);
        tags(itemTags, Map.of(), ContentFormats.ITEM_TAGS);
        worn();
        ContentGeneratedItems.equipment();
        potionIcons();
        if (GeneratedResources.count() > 0) { Summary.info("generated", "Generated " + GeneratedResources.count() + " blockstate, model, loot table, tag and feature file(s) that the packs did not ship themselves"); }
    }

    static void cropLoot() {
        for (ContentRegistry.BlockEntry entry : ContentRegistry.blocks()) {
            if (!(entry.block() instanceof ContentCropBlock)) { continue; }
            String path = lootPath(entry.id().getPath());
            if (!provided(PackType.SERVER_DATA, entry.id().getNamespace(), path)) { data(entry.id().getNamespace(), path, loot(entry, entry.def().type())); }
        }
    }

    private static void block(ContentRegistry.BlockEntry entry, Map<String, Set<String>> blockTags, Map<String, Set<String>> itemTags) {
        BlockDef def = entry.def();
        String namespace = entry.id().getNamespace();
        String name = entry.id().getPath();
        String type = def.type();
        boolean hasItem = ContentRegistry.items().stream().anyMatch(item -> item.block() == entry);
        if (!provided(PackType.CLIENT_RESOURCES, namespace, "blockstates/" + name + PackManager.JSON_SUFFIX)) { ContentGeneratedModels.models(entry, namespace, name, type); }
        if (hasItem && !def.itemModelFromFile() && !provided(PackType.CLIENT_RESOURCES, namespace, "models/item/" + name + PackManager.JSON_SUFFIX)) { ContentGeneratedModels.itemModel(def, namespace, name, type); }
        if (hasItem) { ContentGeneratedItems.blockItem(entry); }
        if (!provided(PackType.SERVER_DATA, namespace, lootPath(name))) { data(namespace, lootPath(name), loot(entry, type)); }
        if (entry.isMain()) {
            tagBlock(entry, blockTags, itemTags, hasItem);
            SaplingDef sapling = def.sapling();
            String treePath = ContentFormats.CONFIGURED_FEATURES + "/" + name + "_tree.json";
            if (ContentBlockTypes.SAPLING.equals(type) && sapling != null && sapling.growsVanilla() && !provided(PackType.SERVER_DATA, namespace, treePath)) { data(namespace, treePath, LineCompat.worldgenOut(treePath, tree(sapling))); }
        }
    }

    private static void fluidBlock(ContentFluids.Made made) {
        if (made.block == null) { return; }
        String namespace = made.def.key().getNamespace();
        String name = made.def.key().getPath();
        if (!provided(PackType.CLIENT_RESOURCES, namespace, "blockstates/" + name + PackManager.JSON_SUFFIX)) {
            ContentGeneratedModels.blockstate(namespace, name, Json.obj("variants", Json.obj("", Json.obj("model", namespace + ":block/" + name))));
        }
        if (!provided(PackType.CLIENT_RESOURCES, namespace, "models/block/" + name + PackManager.JSON_SUFFIX)) {
            asset(namespace, "models/block/" + name + PackManager.JSON_SUFFIX, Json.obj("textures", Json.obj("particle", made.def.still().toString())));
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
        JsonArray named = Json.arr();
        for (String slot : slots) {
            named.add(slot);
            if (ContainerDef.RING_SLOT.equals(slot)) { data(ResourceDataPackLoader.MOD_ID, "curios/slots/" + slot + PackManager.JSON_SUFFIX, Json.obj("size", 2)); }
            else if (!CURIOS_PRESETS.contains(slot)) { data(ResourceDataPackLoader.MOD_ID, "curios/slots/" + slot + PackManager.JSON_SUFFIX, Json.obj("validators", Json.arr("curios:tag"))); }
        }
        data(ResourceDataPackLoader.MOD_ID, "curios/entities/worn.json", Json.obj("entities", Json.arr("minecraft:player"), "slots", named));
    }

    private static void item(ContentRegistry.ItemEntry entry, Map<String, Set<String>> itemTags) {
        String namespace = entry.id().getNamespace();
        String name = entry.id().getPath();
        ItemDef def = entry.def();
        if (!provided(PackType.CLIENT_RESOURCES, namespace, "models/item/" + name + PackManager.JSON_SUFFIX)) {
            String parent = def != null && "tool".equals(def.type()) ? ITEM_HANDHELD : ITEM_GENERATED;
            asset(namespace, "models/item/" + name + PackManager.JSON_SUFFIX, Json.obj("parent", parent, "textures", Json.obj("layer0", namespace + ":item/" + name)));
        }
        ContentGeneratedItems.item(entry);
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
                JsonObject grown = stateCondition(entry, "age", String.valueOf(def.cropMaxAge()));
                ContentCropBlock crop = (ContentCropBlock) entry.block();
                String produce = ContentStacks.id(crop.produce());
                String seed = ContentStacks.id(crop.seed());
                pools.add(pool(Json.arr(item(produce)), Json.arr(grown)));
                pools.add(pool(Json.arr(item(seed)), Json.arr(inverted(grown))));
                pools.add(pool(Json.arr(entryWith(seed, Json.arr(setCount(0), Json.obj("function", "minecraft:apply_bonus", "enchantment", "minecraft:fortune", "formula", "minecraft:binomial_with_bonus_count", "parameters", Json.obj("extra", 3, "probability", (def.cropMaxAge() + 1) / (2.0 * def.cropMaxAge())))))), Json.arr(grown)));
            }
            case ContentBlockTypes.LEAVES -> {
                JsonObject silk = ContentFormats.silkTouch();
                JsonObject shears = ContentFormats.shears();
                pools.add(pool(Json.arr(item(self)), Json.arr(silk)));
                pools.add(pool(Json.arr(item(self)), Json.arr(shears, inverted(silk))));
                Identifier sapling = ContentParser.location(def.leafSapling());
                if (sapling != null) {
                    JsonArray chances = new JsonArray();
                    int percent = def.leafSaplingChance();
                    chances.add(Math.min(100, percent) / 100.0);
                    while (percent < 100) {
                        percent += FORTUNE_BONUS;
                        chances.add(Math.min(100, percent) / 100.0);
                    }
                    JsonObject bonus = Json.obj("condition", "minecraft:table_bonus", "enchantment", "minecraft:fortune", "chances", chances);
                    pools.add(pool(Json.arr(entryWith(sapling.toString(), Json.arr(json(DECAY)))), Json.arr(inverted(silk), inverted(shears), bonus, json(SURVIVES))));
                }
            }
            case ContentBlockTypes.DOOR -> pools.add(pool(Json.arr(item(self)), Json.arr(stateCondition(entry, "half", "lower"), json(SURVIVES))));
            case ContentBlockTypes.SLAB -> {
                JsonObject doubled = setCount(2);
                doubled.add("conditions", Json.arr(stateCondition(entry, "type", "double")));
                pools.add(pool(Json.arr(entryWith(self, Json.arr(doubled, json(DECAY)))), Json.arr(json(SURVIVES))));
            }
            case ContentBlockTypes.SAPLING, ContentBlockTypes.FLOWER -> pools.add(pool(Json.arr(item(self)), Json.arr(json(SURVIVES))));
            case ContentBlockTypes.CANE, ContentBlockTypes.VINE -> {
                boolean own = def.growth() == null || def.growth().drop().isEmpty();
                String dropped = own ? self : def.growth().drop();
                int count = def.growth() == null ? 1 : def.growth().dropCount();
                JsonArray functions = count > 1 ? Json.arr(setCount(count)) : Json.arr();
                if (own || !ContentBlockTypes.VINE.equals(type)) {
                    pools.add(pool(Json.arr(entryWith(dropped, functions)), Json.arr(json(SURVIVES))));
                    break;
                }
                JsonObject shears = ContentFormats.shears();
                pools.add(pool(Json.arr(item(self)), Json.arr(shears)));
                pools.add(pool(Json.arr(entryWith(dropped, functions)), Json.arr(inverted(shears), json(SURVIVES))));
            }
            default -> {
                boolean rolled = entry.block() instanceof ContentBlock || entry.block() instanceof ContentContainerBlock;
                if (!rolled || variant.drops().isEmpty() || def.opensWith() != null) {
                    pools.add(pool(Json.arr(item(self)), Json.arr(json(SURVIVES))));
                    break;
                }
                JsonObject silk = ContentFormats.silkTouch();
                if (def.silkHarvest()) { pools.add(pool(Json.arr(item(self)), Json.arr(silk))); }
                JsonArray unsilked = def.silkHarvest() ? Json.arr(inverted(silk), json(SURVIVES)) : Json.arr(json(SURVIVES));
                JsonArray weighted = new JsonArray();
                for (DropDef drop : variant.drops()) {
                    if (drop.isEntity() || drop.item() == null) { continue; }
                    JsonObject dropped = entryWith(drop.item().toString(), dropFunctions(drop));
                    if (drop.weighted()) {
                        dropped.addProperty("weight", drop.weight());
                        weighted.add(dropped);
                    }
                    else { pools.add(pool(Json.arr(dropped), unsilked)); }
                }
                if (!weighted.isEmpty()) { pools.add(pool(weighted, unsilked)); }
            }
        }
        return Json.obj("type", "minecraft:block", "pools", pools);
    }

    private static JsonArray dropFunctions(DropDef drop) {
        JsonArray functions = new JsonArray();
        if (!drop.amount().fixed() || drop.amount().least() != 1) {
            JsonElement count = drop.amount().fixed() ? new JsonPrimitive(drop.amount().least()) : Json.obj("type", "minecraft:uniform", "min", drop.amount().least(), "max", drop.amount().most());
            functions.add(setCount(count));
        }
        if (drop.chance() < 100 || drop.hasBonus()) { functions.add(Json.obj("function", LootFunctions.DROP_ROLL.getId().toString(), "chance", drop.chance(), "bonusChance", Json.arr(Arrays.stream(drop.bonusChance()).boxed().toArray()))); }
        return functions;
    }

    private static JsonObject pool(JsonArray entries, JsonArray conditions) {
        JsonObject pool = Json.obj("rolls", 1, "bonus_rolls", 0, "entries", entries);
        if (!conditions.isEmpty()) { pool.add("conditions", conditions); }
        return pool;
    }

    private static JsonObject item(String id) { return Json.obj("type", "minecraft:item", "name", id); }

    private static JsonObject entryWith(String id, JsonArray functions) {
        JsonObject entry = item(id);
        if (!functions.isEmpty()) { entry.add("functions", functions); }
        return entry;
    }

    private static String lootPath(String name) { return ContentFormats.LOOT_FOLDER + "/blocks/" + name + PackManager.JSON_SUFFIX; }

    private static JsonObject stateCondition(ContentRegistry.BlockEntry entry, String property, String value) { return Json.obj("condition", "minecraft:block_state_property", "block", entry.id().toString(), "properties", Json.obj(property, value)); }

    private static JsonObject setCount(Object count) { return Json.obj("function", "minecraft:set_count", "count", count, "add", false); }

    private static JsonObject inverted(JsonObject term) { return Json.obj("condition", "minecraft:inverted", "term", term); }

    private static void tagBlock(ContentRegistry.BlockEntry entry, Map<String, Set<String>> blockTags, Map<String, Set<String>> itemTags, boolean hasItem) {
        BlockDef def = entry.def();
        Identifier id = entry.id();
        for (String tag : entry.variant().tags()) {
            tag(blockTags, tag, id);
            if (hasItem) { tag(itemTags, tag, id); }
        }
        harvestTags(blockTags, id, def.harvestTool(), ContentBlockTypes.harvestLevel(def, entry.variant()));
        materialTags(blockTags, id, def);
        LineCompat.fluidTags(entry.block().defaultBlockState(), tag -> tag(blockTags, tag, id));
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
            case ContentBlockTypes.CROP -> {
                tag(blockTags, "minecraft:crops", id);
                tag(blockTags, "minecraft:maintains_farmland", id);
            }
            case ContentBlockTypes.LADDER, ContentBlockTypes.VINE -> tag(blockTags, "minecraft:climbable", id);
            default -> { }
        }
    }

    private static void both(Map<String, Set<String>> blockTags, Map<String, Set<String>> itemTags, boolean hasItem, String tag, Identifier id) {
        tag(blockTags, tag, id);
        if (hasItem) { tag(itemTags, tag, id); }
    }

    public static void jobSites(Set<Identifier> sites) {
        if (sites.isEmpty()) { return; }
        Map<String, Set<String>> poiTags = new LinkedHashMap<>();
        for (Identifier site : sites) {
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

    public static void harvestTags(Map<String, Set<String>> tags, Identifier id, String tool, int level) {
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

    private static void materialTags(Map<String, Set<String>> tags, Identifier id, BlockDef def) {
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

    public static void tag(Map<String, Set<String>> tags, String tag, Identifier id) { tags.computeIfAbsent(tag, _ -> new LinkedHashSet<>()).add(id.toString()); }

    private static void tags(Map<String, Set<String>> tags, Map<String, Set<String>> removed, String folder) {
        Set<String> names = new LinkedHashSet<>(tags.keySet());
        names.addAll(removed.keySet());
        for (String name : names) {
            Identifier tag = Identifier.tryParse(name);
            if (tag == null) { continue; }
            JsonArray values = new JsonArray();
            for (String value : tags.getOrDefault(name, Set.of())) { values.add(value); }
            JsonObject file = Json.obj("replace", false, "values", values);
            Set<String> gone = removed.get(name);
            if (gone != null) {
                JsonArray remove = new JsonArray();
                for (String value : gone) { remove.add(value); }
                file.add("remove", remove);
            }
            data(tag.getNamespace(), folder + "/" + tag.getPath() + PackManager.JSON_SUFFIX, file);
        }
    }

    private static JsonObject tree(SaplingDef sapling) {
        JsonArray decoratorList = Json.arr();
        if (sapling.vines()) { decoratorList.add(Json.obj("type", "minecraft:leave_vine", "probability", 0.25)); }
        JsonObject config = Json.obj(
                "trunk_provider", WorldgenJson.simpleState(sapling.log()),
                "trunk_placer", Json.obj("type", LootFunctions.NAMESPACE + ":" + ContentTreeTrunk.NAME, "base_height", Math.max(1, sapling.height()), "height_rand_a", 2, "height_rand_b", 0),
                "foliage_provider", WorldgenJson.simpleState(sapling.leaves()),
                "foliage_placer", Json.obj("type", "minecraft:blob_foliage_placer", "radius", 2, "offset", 0, "height", 3),
                "minimum_size", Json.obj("type", "minecraft:two_layers_feature_size", "limit", 1, "lower_size", 0, "upper_size", 1),
                "ignore_vines", true,
                "decorators", decoratorList,
                "below_trunk_provider", WorldgenJson.belowTrunk());
        return Json.obj("type", "minecraft:tree", "config", config);
    }

    private static void potionIcons() {
        JsonArray aliases = new JsonArray();
        for (PotionDef def : ContentPotions.defs()) {
            Identifier key = def.key();
            String path = "textures/mob_effect/" + key.getPath() + ".png";
            if (provided(PackType.CLIENT_RESOURCES, key.getNamespace(), path)) { continue; }
            String vanilla = def.iconTexture().isEmpty() ? sheetIcon(def) : null;
            if (vanilla != null) {
                aliases.add(Json.obj("type", "minecraft:single", "resource", "minecraft:mob_effect/" + vanilla, "sprite", key.getNamespace() + ":mob_effect/" + key.getPath()));
                continue;
            }
            byte[] icon = potionIcon(def);
            if (icon != null) { GeneratedResources.put(PackType.CLIENT_RESOURCES, key.getNamespace(), path, icon); }
        }
        if (!aliases.isEmpty()) { asset(Identifier.DEFAULT_NAMESPACE, "atlases/gui.json", Json.obj("sources", aliases)); }
    }

    @Nullable private static String sheetIcon(PotionDef def) {
        int index = def.iconX() + def.iconY() * SHEET_COLUMNS;
        if (index >= 0 && index < STATUS_SHEET.length) { return STATUS_SHEET[index]; }
        ContentLog.LOGGER.error("Potion {} names icon {}, {}, which is not on the vanilla status icon sheet, using the RDPL icon", def.key(), def.iconX(), def.iconY());
        return null;
    }

    @Nullable private static byte[] potionIcon(PotionDef def) {
        Identifier texture = def.iconTexture().isEmpty() || FallbackIcon.TEXTURE.equals(def.iconTexture()) ? null : Identifier.tryParse(def.iconTexture());
        byte[] icon = texture == null ? null : PackManager.get().bytes(PackType.CLIENT_RESOURCES, texture.getNamespace(), texture.getPath());
        if (texture != null && icon == null) { ContentLog.LOGGER.error("Potion {} names iconTexture {}, which no pack provides, using the RDPL icon", def.key(), def.iconTexture()); }
        return icon == null ? FallbackIcon.bytes() : icon;
    }

    static boolean provided(PackType type, String namespace, String path) { return PackManager.get().provides(type, namespace, path); }

    static void asset(String namespace, String path, JsonObject json) { GeneratedResources.put(PackType.CLIENT_RESOURCES, namespace, path, Json.PRETTY.toJson(json)); }

    private static void data(String namespace, String path, JsonObject json) { GeneratedResources.put(PackType.SERVER_DATA, namespace, path, Json.PRETTY.toJson(json)); }

    private static JsonObject json(String literal) { return JsonParser.parseString(literal).getAsJsonObject(); }

}
