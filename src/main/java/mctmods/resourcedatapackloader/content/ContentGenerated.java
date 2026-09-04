package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.DropDef;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.SaplingDef;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
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
import net.minecraft.util.Mth;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentGenerated {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String BLOCK = "minecraft:block/";
    private static final String ITEM_GENERATED = "minecraft:item/generated";
    private static final String ITEM_HANDHELD = "minecraft:item/handheld";
    private static final String SURVIVES = "{\"condition\":\"minecraft:survives_explosion\"}";
    private static final String DECAY = "{\"function\":\"minecraft:explosion_decay\"}";
    private static final String[] FACINGS = {"east", "south", "west", "north"};
    private static final int[] FACING_Y = {0, 90, 180, 270};
    private static final String[] SHAPES = {"straight", "inner_left", "inner_right", "outer_left", "outer_right"};
    private static final int[] BOTTOM_TURN = {0, 270, 0, 270, 0};
    private static final int[] TOP_TURN = {0, 0, 90, 0, 90};

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
        tags(blockTags, ContentFormats.BLOCK_TAGS);
        tags(itemTags, ContentFormats.ITEM_TAGS);
        if (GeneratedResources.count() > 0) { Summary.info("generated", "Generated " + GeneratedResources.count() + " blockstate, model, loot table, tag and feature file(s) that the packs did not ship themselves"); }
    }

    private static void block(ContentRegistry.BlockEntry entry, Map<String, Set<String>> blockTags, Map<String, Set<String>> itemTags) {
        BlockDef def = entry.def();
        String namespace = entry.id().getNamespace();
        String name = entry.id().getPath();
        String type = def.type();
        boolean hasItem = ContentRegistry.items().stream().anyMatch(item -> item.block() == entry);
        if (!provided(PackType.CLIENT_RESOURCES, namespace, "blockstates/" + name + ".json")) { models(entry, namespace, name, type); }
        if (hasItem && !def.itemModelFromFile() && !provided(PackType.CLIENT_RESOURCES, namespace, "models/item/" + name + ".json")) { itemModel(namespace, name, type); }
        if (!provided(PackType.SERVER_DATA, namespace, ContentFormats.LOOT_FOLDER + "/blocks/" + name + ".json")) { data(namespace, ContentFormats.LOOT_FOLDER + "/blocks/" + name + ".json", loot(entry, type)); }
        if (entry.isMain()) {
            tagBlock(entry, blockTags, itemTags, hasItem);
            SaplingDef sapling = def.sapling();
            if (ContentBlockTypes.SAPLING.equals(type) && sapling != null && !sapling.usesStructure() && !provided(PackType.SERVER_DATA, namespace, "worldgen/configured_feature/" + name + "_tree.json")) {
                data(namespace, "worldgen/configured_feature/" + name + "_tree.json", tree(sapling));
            }
        }
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
    }

    private static void models(ContentRegistry.BlockEntry entry, String namespace, String name, String type) {
        BlockDef def = entry.def();
        String main = namespace + ":block/" + name;
        String texture = texture(namespace, name);
        switch (type) {
            case ContentBlockTypes.LOG -> {
                String top = textureOr(namespace, name + "_top", texture);
                model(def, namespace, name, column(def, texture, top, "cube_column"));
                model(def, namespace, name + "_horizontal", column(def, texture, top, "cube_column_horizontal"));
                blockstate(namespace, name, obj("variants", obj(
                        "axis=x", obj("model", main + "_horizontal", "x", 90, "y", 90),
                        "axis=y", obj("model", main),
                        "axis=z", obj("model", main + "_horizontal", "x", 90))));
            }
            case ContentBlockTypes.SLAB -> {
                model(def, namespace, name, sided(def, texture, "slab"));
                model(def, namespace, name + "_top", sided(def, texture, "slab_top"));
                model(def, namespace, name + "_double", cube(def, texture, "cube_all"));
                blockstate(namespace, name, obj("variants", obj(
                        "type=bottom", obj("model", main),
                        "type=top", obj("model", main + "_top"),
                        "type=double", obj("model", main + "_double"))));
            }
            case ContentBlockTypes.STAIRS -> {
                model(def, namespace, name, sided(def, texture, "stairs"));
                model(def, namespace, name + "_inner", sided(def, texture, "inner_stairs"));
                model(def, namespace, name + "_outer", sided(def, texture, "outer_stairs"));
                blockstate(namespace, name, obj("variants", stairs(main)));
            }
            case ContentBlockTypes.FENCE -> {
                model(def, namespace, name + "_post", textured(def, texture, "fence_post", "texture"));
                model(def, namespace, name + "_side", textured(def, texture, "fence_side", "texture"));
                model(def, namespace, name + "_inventory", textured(def, texture, "fence_inventory", "texture"));
                blockstate(namespace, name, obj("multipart", arr(
                        obj("apply", obj("model", main + "_post")),
                        obj("when", obj("north", "true"), "apply", obj("model", main + "_side", "uvlock", true)),
                        obj("when", obj("east", "true"), "apply", obj("model", main + "_side", "y", 90, "uvlock", true)),
                        obj("when", obj("south", "true"), "apply", obj("model", main + "_side", "y", 180, "uvlock", true)),
                        obj("when", obj("west", "true"), "apply", obj("model", main + "_side", "y", 270, "uvlock", true)))));
            }
            case ContentBlockTypes.WALL -> {
                model(def, namespace, name + "_post", textured(def, texture, "template_wall_post", "wall"));
                model(def, namespace, name + "_side", textured(def, texture, "template_wall_side", "wall"));
                model(def, namespace, name + "_side_tall", textured(def, texture, "template_wall_side_tall", "wall"));
                model(def, namespace, name + "_inventory", textured(def, texture, "wall_inventory", "wall"));
                JsonArray parts = arr(obj("when", obj("up", "true"), "apply", obj("model", main + "_post")));
                for (int i = 0; i < 4; i++) {
                    String side = FACINGS[(i + 3) % 4];
                    parts.add(obj("when", obj(side, "low"), "apply", rotated(main + "_side", FACING_Y[i] == 0 ? 0 : (FACING_Y[i] + 270) % 360, true)));
                    parts.add(obj("when", obj(side, "tall"), "apply", rotated(main + "_side_tall", FACING_Y[i] == 0 ? 0 : (FACING_Y[i] + 270) % 360, true)));
                }
                blockstate(namespace, name, obj("multipart", parts));
            }
            case ContentBlockTypes.PANE -> {
                String edge = textureOr(namespace, name + "_top", texture);
                for (String part : new String[] {"post", "side", "side_alt", "noside", "noside_alt"}) {
                    JsonObject textures = part.startsWith("noside") ? obj("pane", texture) : obj("pane", texture, "edge", edge);
                    model(def, namespace, name + "_" + part, texture == null ? obj("parent", parent(def)) : obj("parent", BLOCK + "template_glass_pane_" + part, "textures", textures));
                }
                blockstate(namespace, name, obj("multipart", arr(
                        obj("apply", obj("model", main + "_post")),
                        obj("when", obj("north", "true"), "apply", obj("model", main + "_side")),
                        obj("when", obj("east", "true"), "apply", obj("model", main + "_side", "y", 90)),
                        obj("when", obj("south", "true"), "apply", obj("model", main + "_side_alt")),
                        obj("when", obj("west", "true"), "apply", obj("model", main + "_side_alt", "y", 90)),
                        obj("when", obj("north", "false"), "apply", obj("model", main + "_noside")),
                        obj("when", obj("east", "false"), "apply", obj("model", main + "_noside_alt")),
                        obj("when", obj("south", "false"), "apply", obj("model", main + "_noside_alt", "y", 90)),
                        obj("when", obj("west", "false"), "apply", obj("model", main + "_noside", "y", 270)))));
            }
            case ContentBlockTypes.DOOR -> {
                String top = textureOr(namespace, name + "_top", texture);
                String bottom = textureOr(namespace, name + "_bottom", texture);
                JsonObject variants = new JsonObject();
                for (String half : new String[] {"lower", "upper"}) {
                    String part = "lower".equals(half) ? "bottom" : "top";
                    for (String hinge : new String[] {"left", "right"}) {
                        for (String open : new String[] {"false", "true"}) {
                            String suffix = "_" + part + "_" + hinge + ("true".equals(open) ? "_open" : "");
                            model(def, namespace, name + suffix, texture == null ? obj("parent", BLOCK + "door" + suffix) : obj("parent", BLOCK + "door" + suffix, "textures", obj("top", top, "bottom", bottom)));
                            for (int i = 0; i < 4; i++) {
                                int turn = FACING_Y[i] + ("true".equals(open) ? ("left".equals(hinge) ? 90 : 270) : 0);
                                variants.add("facing=" + FACINGS[i] + ",half=" + half + ",hinge=" + hinge + ",open=" + open, rotated(main + suffix, turn % 360, false));
                            }
                        }
                    }
                }
                blockstate(namespace, name, obj("variants", variants));
            }
            case ContentBlockTypes.TRAPDOOR -> {
                for (String part : new String[] {"bottom", "top", "open"}) { model(def, namespace, name + "_" + part, textured(def, texture, "template_orientable_trapdoor_" + part, "texture")); }
                JsonObject variants = new JsonObject();
                String[] order = {"north", "east", "south", "west"};
                for (int i = 0; i < 4; i++) {
                    int y = i * 90;
                    variants.add("facing=" + order[i] + ",half=bottom,open=false", rotated(main + "_bottom", y, false));
                    variants.add("facing=" + order[i] + ",half=top,open=false", rotated(main + "_top", y, false));
                    variants.add("facing=" + order[i] + ",half=bottom,open=true", rotated(main + "_open", y, false));
                    JsonObject flipped = rotated(main + "_open", (y + 180) % 360, false);
                    flipped.addProperty("x", 180);
                    variants.add("facing=" + order[i] + ",half=top,open=true", flipped);
                }
                blockstate(namespace, name, obj("variants", variants));
            }
            case ContentBlockTypes.FENCE_GATE -> {
                for (String part : new String[] {"", "_open", "_wall", "_wall_open"}) { model(def, namespace, name + part, textured(def, texture, "template_fence_gate" + part, "texture")); }
                JsonObject variants = new JsonObject();
                String[] order = {"south", "west", "north", "east"};
                for (int i = 0; i < 4; i++) {
                    for (String wall : new String[] {"false", "true"}) {
                        for (String open : new String[] {"false", "true"}) {
                            String part = ("true".equals(wall) ? "_wall" : "") + ("true".equals(open) ? "_open" : "");
                            variants.add("facing=" + order[i] + ",in_wall=" + wall + ",open=" + open, rotated(main + part, i * 90, true));
                        }
                    }
                }
                blockstate(namespace, name, obj("variants", variants));
            }
            case ContentBlockTypes.LADDER -> {
                model(def, namespace, name, texture == null ? obj("parent", BLOCK + "ladder") : obj("parent", BLOCK + "ladder", "textures", obj("texture", texture, "particle", texture)));
                blockstate(namespace, name, obj("variants", obj(
                        "facing=north", obj("model", main),
                        "facing=east", obj("model", main, "y", 90),
                        "facing=south", obj("model", main, "y", 180),
                        "facing=west", obj("model", main, "y", 270))));
            }
            case ContentBlockTypes.TORCH -> {
                if (entry.isMain()) {
                    model(def, namespace, name, textured(def, texture, "template_torch", "torch"));
                    blockstate(namespace, name, obj("variants", obj("", obj("model", main))));
                }
                else {
                    String torch = texture(namespace, entry.variant().name());
                    model(def, namespace, name, textured(def, torch, "template_torch_wall", "torch"));
                    blockstate(namespace, name, obj("variants", obj(
                            "facing=east", obj("model", main),
                            "facing=south", obj("model", main, "y", 90),
                            "facing=west", obj("model", main, "y", 180),
                            "facing=north", obj("model", main, "y", 270))));
                }
            }
            case ContentBlockTypes.CROP -> {
                JsonObject variants = new JsonObject();
                int last = def.cropMaxAge();
                for (int stage = 0; stage <= last; stage++) {
                    String stageTexture = textureOr(namespace, name + "_stage" + stage, texture);
                    model(def, namespace, name + "_stage" + stage, stageTexture == null ? obj("parent", BLOCK + "crop") : obj("parent", BLOCK + "crop", "textures", obj("crop", stageTexture)));
                }
                for (int age = 0; age <= 7; age++) { variants.add("age=" + age, obj("model", main + "_stage" + Math.min(age, last))); }
                blockstate(namespace, name, obj("variants", variants));
            }
            case ContentBlockTypes.SAPLING, ContentBlockTypes.FLOWER, ContentBlockTypes.CANE -> {
                model(def, namespace, name, textured(def, texture, "cross", "cross"));
                blockstate(namespace, name, obj("variants", obj("", obj("model", main))));
            }
            case ContentBlockTypes.VINE -> {
                model(def, namespace, name, texture == null ? obj("parent", BLOCK + "vine") : obj("parent", BLOCK + "vine", "textures", obj("vine", texture, "particle", texture)));
                blockstate(namespace, name, obj("multipart", arr(
                        obj("when", obj("north", "true"), "apply", obj("model", main)),
                        obj("when", obj("east", "true"), "apply", obj("model", main, "y", 90)),
                        obj("when", obj("south", "true"), "apply", obj("model", main, "y", 180)),
                        obj("when", obj("west", "true"), "apply", obj("model", main, "y", 270)),
                        obj("when", obj("up", "true"), "apply", obj("model", main, "x", 270, "y", 90)),
                        obj("when", obj("north", "false", "east", "false", "south", "false", "west", "false", "up", "false"), "apply", obj("model", main)))));
            }
            case ContentBlockTypes.LEAVES -> {
                model(def, namespace, name, cube(def, texture, "leaves"));
                blockstate(namespace, name, obj("variants", obj("", obj("model", main))));
            }
            default -> {
                model(def, namespace, name, cube(def, texture, "cube_all"));
                blockstate(namespace, name, obj("variants", obj("", obj("model", main))));
            }
        }
    }

    private static JsonObject stairs(String main) {
        JsonObject variants = new JsonObject();
        for (int facing = 0; facing < 4; facing++) {
            for (String half : new String[] {"bottom", "top"}) {
                boolean top = "top".equals(half);
                for (int shape = 0; shape < SHAPES.length; shape++) {
                    String model = shape == 0 ? main : shape < 3 ? main + "_inner" : main + "_outer";
                    int y = (FACING_Y[facing] + (top ? TOP_TURN[shape] : BOTTOM_TURN[shape])) % 360;
                    JsonObject entry = rotated(model, y, true);
                    if (top) {
                        entry.addProperty("x", 180);
                        entry.addProperty("uvlock", true);
                    }
                    variants.add("facing=" + FACINGS[facing] + ",half=" + half + ",shape=" + SHAPES[shape], entry);
                }
            }
        }
        return variants;
    }

    private static JsonObject rotated(String model, int y, boolean uvlock) {
        JsonObject entry = obj("model", model);
        if (y != 0) {
            entry.addProperty("y", y);
            if (uvlock) { entry.addProperty("uvlock", true); }
        }
        return entry;
    }

    private static JsonObject cube(BlockDef def, @Nullable String texture, String template) {
        return texture == null ? obj("parent", parent(def)) : obj("parent", BLOCK + template, "textures", obj("all", texture));
    }

    private static JsonObject column(BlockDef def, @Nullable String side, @Nullable String end, String template) {
        return side == null ? obj("parent", parent(def)) : obj("parent", BLOCK + template, "textures", obj("end", end, "side", side));
    }

    private static JsonObject sided(BlockDef def, @Nullable String texture, String template) {
        return texture == null ? obj("parent", parent(def)) : obj("parent", BLOCK + template, "textures", obj("bottom", texture, "top", texture, "side", texture));
    }

    private static JsonObject textured(BlockDef def, @Nullable String texture, String template, String slot) {
        return texture == null ? obj("parent", parent(def)) : obj("parent", BLOCK + template, "textures", obj(slot, texture));
    }

    private static String parent(BlockDef def) {
        ResourceLocation model = ContentParser.location(def.modelBlock());
        if (model == null) { model = ResourceLocation.parse("minecraft:stone"); }
        return model.getNamespace() + ":block/" + model.getPath();
    }

    private static void itemModel(String namespace, String name, String type) {
        String main = namespace + ":block/" + name;
        JsonObject model = switch (type) {
            case ContentBlockTypes.FENCE, ContentBlockTypes.WALL -> obj("parent", main + "_inventory");
            case ContentBlockTypes.TRAPDOOR -> obj("parent", main + "_bottom");
            case ContentBlockTypes.DOOR, ContentBlockTypes.LADDER, ContentBlockTypes.TORCH, ContentBlockTypes.SAPLING, ContentBlockTypes.FLOWER, ContentBlockTypes.CANE, ContentBlockTypes.VINE, ContentBlockTypes.PANE -> {
                String flat = provided(PackType.CLIENT_RESOURCES, namespace, "textures/item/" + name + ".png") ? namespace + ":item/" + name : texture(namespace, name);
                yield flat == null ? obj("parent", main) : obj("parent", ITEM_GENERATED, "textures", obj("layer0", flat));
            }
            default -> obj("parent", main);
        };
        asset(namespace, "models/item/" + name + ".json", model);
    }

    private static JsonObject loot(ContentRegistry.BlockEntry entry, String type) {
        BlockDef def = entry.def();
        BlockVariant variant = entry.variant();
        String self = entry.isMain() ? entry.id().toString() : variant.id().toString();
        JsonArray pools = new JsonArray();
        switch (type) {
            case ContentBlockTypes.CROP -> {
                JsonObject grown = obj("condition", "minecraft:block_state_property", "block", entry.id().toString(), "properties", obj("age", String.valueOf(def.cropMaxAge())));
                String produce = def.cropProduce().isEmpty() ? self : def.cropProduce();
                String seed = def.cropSeed().isEmpty() ? produce : def.cropSeed();
                pools.add(pool(arr(item(produce)), arr(grown)));
                pools.add(pool(arr(item(seed)), arr(obj("condition", "minecraft:inverted", "term", grown))));
                pools.add(pool(arr(entryWith(seed, arr(obj("function", "minecraft:apply_bonus", "enchantment", "minecraft:fortune", "formula", "minecraft:binomial_with_bonus_count", "parameters", obj("extra", 3, "probability", 0.5714286))))), arr(grown)));
            }
            case ContentBlockTypes.LEAVES -> {
                JsonObject silk = ContentFormats.silkTouch();
                JsonObject shears = ContentFormats.shears();
                pools.add(pool(arr(item(self)), arr(silk)));
                pools.add(pool(arr(item(self)), arr(shears, inverted(silk))));
                ResourceLocation sapling = ContentParser.location(def.leafSapling());
                if (sapling != null && def.leafSaplingChance() > 0) {
                    double chance = def.leafSaplingChance() / 100.0;
                    JsonObject bonus = obj("condition", "minecraft:table_bonus", "enchantment", "minecraft:fortune", "chances", arr(chance, chance + 0.0125, chance + 0.025, chance + 0.05));
                    pools.add(pool(arr(entryWith(sapling.toString(), arr(json(DECAY)))), arr(inverted(silk), inverted(shears), bonus, json(SURVIVES))));
                }
            }
            case ContentBlockTypes.DOOR -> pools.add(pool(arr(item(self)), arr(obj("condition", "minecraft:block_state_property", "block", entry.id().toString(), "properties", obj("half", "lower")), json(SURVIVES))));
            case ContentBlockTypes.SLAB -> {
                JsonObject doubled = obj("function", "minecraft:set_count", "count", 2, "add", false, "conditions", arr(obj("condition", "minecraft:block_state_property", "block", entry.id().toString(), "properties", obj("type", "double"))));
                pools.add(pool(arr(entryWith(self, arr(doubled, json(DECAY)))), arr(json(SURVIVES))));
            }
            case ContentBlockTypes.SAPLING, ContentBlockTypes.FLOWER, ContentBlockTypes.CANE, ContentBlockTypes.VINE -> {
                String dropped = def.growth() == null || def.growth().drop().isEmpty() ? self : def.growth().drop();
                int count = def.growth() == null ? 1 : def.growth().dropCount();
                JsonArray functions = count > 1 ? arr(obj("function", "minecraft:set_count", "count", count, "add", false)) : arr();
                pools.add(pool(arr(entryWith(dropped, functions)), arr(json(SURVIVES))));
            }
            default -> {
                if (variant.drops().stream().allMatch(DropDef::isEntity) || def.opensWith() != null) {
                    pools.add(pool(arr(item(self)), arr(json(SURVIVES))));
                    break;
                }
                JsonObject silk = ContentFormats.silkTouch();
                if (def.silkHarvest()) { pools.add(pool(arr(item(self)), arr(silk))); }
                JsonArray weighted = new JsonArray();
                JsonArray weightedBonus = new JsonArray();
                for (DropDef drop : variant.drops()) {
                    if (drop.isEntity() || drop.item() == null) { continue; }
                    JsonObject dropped = entryWith(drop.item().toString(), dropFunctions(drop));
                    if (drop.weighted()) {
                        dropped.addProperty("weight", drop.weight());
                        weighted.add(dropped);
                        if (drop.hasBonus()) {
                            JsonObject extra = entryWith(drop.item().toString(), dropFunctions(drop));
                            extra.addProperty("weight", drop.weight());
                            extra.add("conditions", arr(bonus(drop)));
                            weightedBonus.add(extra);
                        }
                        continue;
                    }
                    JsonArray conditions = def.silkHarvest() ? arr(inverted(silk)) : arr();
                    if (drop.chance() < 100) { conditions.add(obj("condition", "minecraft:random_chance", "chance", drop.chance() / 100.0)); }
                    pools.add(pool(arr(dropped), conditions));
                    if (drop.hasBonus()) {
                        JsonArray bonusConditions = conditions.deepCopy();
                        bonusConditions.add(bonus(drop));
                        pools.add(pool(arr(entryWith(drop.item().toString(), dropFunctions(drop))), bonusConditions));
                    }
                }
                if (!weighted.isEmpty()) { pools.add(pool(weighted, def.silkHarvest() ? arr(inverted(silk)) : arr())); }
                if (!weightedBonus.isEmpty()) { pools.add(pool(weightedBonus, def.silkHarvest() ? arr(inverted(silk)) : arr())); }
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
        functions.add(json(DECAY));
        return functions;
    }

    private static JsonObject bonus(DropDef drop) {
        JsonArray chances = new JsonArray();
        for (int chance : drop.bonusChance()) { chances.add(Mth.clamp(chance, 0, 100) / 100.0); }
        return obj("condition", "minecraft:table_bonus", "enchantment", "minecraft:fortune", "chances", chances);
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
        switch (def.harvestTool()) {
            case "pickaxe", "axe", "shovel", "hoe" -> tag(blockTags, "minecraft:mineable/" + def.harvestTool(), id);
            default -> { }
        }
        int level = entry.variant().harvestLevelOr(def.harvestToolLevel());
        if (level == 1) { tag(blockTags, "minecraft:needs_stone_tool", id); }
        else if (level == 2) { tag(blockTags, "minecraft:needs_iron_tool", id); }
        else if (level >= 3) { tag(blockTags, "minecraft:needs_diamond_tool", id); }
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
        tags(poiTags, ContentFormats.POI_TAGS);
    }

    private static void tag(Map<String, Set<String>> tags, String tag, ResourceLocation id) { tags.computeIfAbsent(tag, k -> new LinkedHashSet<>()).add(id.toString()); }

    private static void tags(Map<String, Set<String>> tags, String folder) {
        for (Map.Entry<String, Set<String>> entry : tags.entrySet()) {
            ResourceLocation tag = ResourceLocation.tryParse(entry.getKey());
            if (tag == null) { continue; }
            JsonArray values = new JsonArray();
            for (String value : entry.getValue()) { values.add(value); }
            data(tag.getNamespace(), folder + "/" + tag.getPath() + ".json", obj("replace", false, "values", values));
        }
    }

    private static JsonObject tree(SaplingDef sapling) {
        JsonArray decoratorList = arr();
        if (sapling.vines()) { decoratorList.add(obj("type", "minecraft:leave_vine", "probability", 0.25)); }
        JsonObject config = obj(
                "trunk_provider", state(sapling.log()),
                "trunk_placer", obj("type", "minecraft:straight_trunk_placer", "base_height", Math.max(1, sapling.height()), "height_rand_a", 2, "height_rand_b", 0),
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

    @Nullable private static String texture(String namespace, String name) { return provided(PackType.CLIENT_RESOURCES, namespace, "textures/block/" + name + ".png") ? namespace + ":block/" + name : null; }

    @Nullable private static String textureOr(String namespace, String name, @Nullable String fallback) {
        String found = texture(namespace, name);
        return found == null ? fallback : found;
    }

    private static boolean provided(PackType type, String namespace, String path) { return !PackManager.get().holders(type, namespace, path).isEmpty(); }

    private static void blockstate(String namespace, String name, JsonObject json) { asset(namespace, "blockstates/" + name + ".json", json); }

    private static void model(BlockDef def, String namespace, String name, JsonObject json) {
        String layer = ContentBlockTypes.renderType(def);
        if (layer != null && !json.has("render_type")) { json.addProperty("render_type", layer); }
        asset(namespace, "models/block/" + name + ".json", json);
    }

    private static void asset(String namespace, String path, JsonObject json) { GeneratedResources.put(PackType.CLIENT_RESOURCES, namespace, path, GSON.toJson(json)); }

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
