package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.PortwayDef;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import javax.annotation.Nullable;

final class ContentGeneratedModels {
    private static final String BLOCK = "minecraft:block/";
    private static final String[] FACINGS = {"east", "south", "west", "north"};
    private static final String[] BELL_PARTS = {"floor", "ceiling", "wall", "between_walls"};
    private static final String[] BELL_ATTACHMENTS = {"floor", "ceiling", "single_wall", "double_wall"};
    private static final String[][] BELL_TURN = {{"north", "east", "south", "west"}, {"north", "east", "south", "west"}, {"east", "south", "west", "north"}, {"east", "south", "west", "north"}};
    private static final int[] FACING_Y = {0, 90, 180, 270};
    private static final String[] SHAPES = {"straight", "inner_left", "inner_right", "outer_left", "outer_right"};
    private static final int[] BOTTOM_TURN = {0, 270, 0, 270, 0};
    private static final int[] TOP_TURN = {0, 0, 90, 0, 90};

    private ContentGeneratedModels() {}

    static void models(ContentRegistry.BlockEntry entry, String namespace, String name, String type) {
        BlockDef def = entry.def();
        String main = namespace + ":block/" + name;
        String texture = texture(namespace, name);
        switch (type) {
            case ContentBlockTypes.CONTAINER -> {
                if (def.chested()) {
                    blockstate(namespace, name, singleVariant("resourcedatapackloader:block/pack_chest"));
                }
                else {
                    model(def, namespace, name, cube(def, texture, "cube_all"));
                    blockstate(namespace, name, singleVariant(main));
                }
            }
            case ContentBlockTypes.BELL -> {
                boolean swings = def.bell() == null || def.bell().swing();
                for (String part : BELL_PARTS) { model(def, namespace, name + "_" + part, bellFrame(texture, part, swings)); }
                if (swings) { model(def, namespace, name + "_body", bellBody(texture)); }
                blockstate(namespace, name, Json.obj("variants", bell(main)));
            }
            case ContentBlockTypes.LOG -> {
                String top = textureOr(namespace, name + "_top", texture);
                model(def, namespace, name, column(def, texture, top, "cube_column"));
                model(def, namespace, name + "_horizontal", column(def, texture, top, "cube_column_horizontal"));
                blockstate(namespace, name, Json.obj("variants", Json.obj(
                        "axis=x", Json.obj("model", main + "_horizontal", "x", 90, "y", 90),
                        "axis=y", Json.obj("model", main),
                        "axis=z", Json.obj("model", main + "_horizontal", "x", 90))));
            }
            case ContentBlockTypes.SLAB -> {
                model(def, namespace, name, sided(def, texture, "slab"));
                model(def, namespace, name + "_top", sided(def, texture, "slab_top"));
                model(def, namespace, name + "_double", cube(def, texture, "cube_all"));
                blockstate(namespace, name, Json.obj("variants", Json.obj(
                        "type=bottom", Json.obj("model", main),
                        "type=top", Json.obj("model", main + "_top"),
                        "type=double", Json.obj("model", main + "_double"))));
            }
            case ContentBlockTypes.STAIRS -> {
                model(def, namespace, name, sided(def, texture, "stairs"));
                model(def, namespace, name + "_inner", sided(def, texture, "inner_stairs"));
                model(def, namespace, name + "_outer", sided(def, texture, "outer_stairs"));
                blockstate(namespace, name, Json.obj("variants", stairs(main)));
            }
            case ContentBlockTypes.FENCE -> {
                model(def, namespace, name + "_post", textured(def, texture, "fence_post", "texture"));
                model(def, namespace, name + "_side", textured(def, texture, "fence_side", "texture"));
                model(def, namespace, name + "_inventory", textured(def, texture, "fence_inventory", "texture"));
                blockstate(namespace, name, Json.obj("multipart", Json.arr(
                        Json.obj("apply", Json.obj("model", main + "_post")),
                        Json.obj("when", Json.obj("north", "true"), "apply", Json.obj("model", main + "_side", "uvlock", true)),
                        Json.obj("when", Json.obj("east", "true"), "apply", Json.obj("model", main + "_side", "y", 90, "uvlock", true)),
                        Json.obj("when", Json.obj("south", "true"), "apply", Json.obj("model", main + "_side", "y", 180, "uvlock", true)),
                        Json.obj("when", Json.obj("west", "true"), "apply", Json.obj("model", main + "_side", "y", 270, "uvlock", true)))));
            }
            case ContentBlockTypes.WALL -> {
                model(def, namespace, name + "_post", textured(def, texture, "template_wall_post", "wall"));
                model(def, namespace, name + "_side", textured(def, texture, "template_wall_side", "wall"));
                model(def, namespace, name + "_side_tall", textured(def, texture, "template_wall_side_tall", "wall"));
                model(def, namespace, name + "_inventory", textured(def, texture, "wall_inventory", "wall"));
                JsonArray parts = Json.arr(Json.obj("when", Json.obj("up", "true"), "apply", Json.obj("model", main + "_post")));
                for (int i = 0; i < 4; i++) {
                    String side = FACINGS[(i + 3) % 4];
                    parts.add(Json.obj("when", Json.obj(side, "low"), "apply", rotated(main + "_side", i * 90, true)));
                    parts.add(Json.obj("when", Json.obj(side, "tall"), "apply", rotated(main + "_side_tall", i * 90, true)));
                }
                blockstate(namespace, name, Json.obj("multipart", parts));
            }
            case ContentBlockTypes.PANE -> {
                String edge = textureOr(namespace, name + "_top", texture);
                for (String part : new String[] {"post", "side", "side_alt", "noside", "noside_alt"}) {
                    JsonObject textures = part.startsWith("noside") ? Json.obj("pane", texture) : Json.obj("pane", texture, "edge", edge);
                    model(def, namespace, name + "_" + part, texture == null ? parented(parent(def), null) : parented(BLOCK + "template_glass_pane_" + part, textures));
                }
                blockstate(namespace, name, Json.obj("multipart", Json.arr(
                        Json.obj("apply", Json.obj("model", main + "_post")),
                        Json.obj("when", Json.obj("north", "true"), "apply", Json.obj("model", main + "_side")),
                        Json.obj("when", Json.obj("east", "true"), "apply", Json.obj("model", main + "_side", "y", 90)),
                        Json.obj("when", Json.obj("south", "true"), "apply", Json.obj("model", main + "_side_alt")),
                        Json.obj("when", Json.obj("west", "true"), "apply", Json.obj("model", main + "_side_alt", "y", 90)),
                        Json.obj("when", Json.obj("north", "false"), "apply", Json.obj("model", main + "_noside")),
                        Json.obj("when", Json.obj("east", "false"), "apply", Json.obj("model", main + "_noside_alt")),
                        Json.obj("when", Json.obj("south", "false"), "apply", Json.obj("model", main + "_noside_alt", "y", 90)),
                        Json.obj("when", Json.obj("west", "false"), "apply", Json.obj("model", main + "_noside", "y", 270)))));
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
                            model(def, namespace, name + suffix, parented(BLOCK + "door" + suffix, texture == null ? null : Json.obj("top", top, "bottom", bottom)));
                            for (int i = 0; i < 4; i++) {
                                int turn = FACING_Y[i] + ("true".equals(open) ? ("left".equals(hinge) ? 90 : 270) : 0);
                                variants.add("facing=" + FACINGS[i] + ",half=" + half + ",hinge=" + hinge + ",open=" + open, rotated(main + suffix, turn % 360, false));
                            }
                        }
                    }
                }
                blockstate(namespace, name, Json.obj("variants", variants));
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
                blockstate(namespace, name, Json.obj("variants", variants));
            }
            case ContentBlockTypes.FENCE_GATE -> {
                for (String part : new String[] {"", "_open", ContentTypes.WALL_SUFFIX, ContentTypes.WALL_SUFFIX + "_open"}) { model(def, namespace, name + part, textured(def, texture, "template_fence_gate" + part, "texture")); }
                JsonObject variants = new JsonObject();
                String[] order = {"south", "west", "north", "east"};
                for (int i = 0; i < 4; i++) {
                    for (String wall : new String[] {"false", "true"}) {
                        for (String open : new String[] {"false", "true"}) {
                            String part = ("true".equals(wall) ? ContentTypes.WALL_SUFFIX : "") + ("true".equals(open) ? "_open" : "");
                            variants.add("facing=" + order[i] + ",in_wall=" + wall + ",open=" + open, rotated(main + part, i * 90, true));
                        }
                    }
                }
                blockstate(namespace, name, Json.obj("variants", variants));
            }
            case ContentBlockTypes.LADDER -> {
                model(def, namespace, name, parented(BLOCK + "ladder", texture == null ? null : Json.obj("texture", texture, "particle", texture)));
                blockstate(namespace, name, Json.obj("variants", Json.obj(
                        "facing=north", Json.obj("model", main),
                        "facing=east", Json.obj("model", main, "y", 90),
                        "facing=south", Json.obj("model", main, "y", 180),
                        "facing=west", Json.obj("model", main, "y", 270))));
            }
            case ContentBlockTypes.BANNER -> {
                model(def, namespace, name, Json.obj("parent", BLOCK + "banner"));
                blockstate(namespace, name, singleVariant(main));
            }
            case ContentBlockTypes.TORCH -> {
                if (entry.isMain()) {
                    model(def, namespace, name, textured(def, texture, "template_torch", "torch"));
                    blockstate(namespace, name, singleVariant(main));
                }
                else {
                    String torch = texture(namespace, entry.variant().name());
                    model(def, namespace, name, textured(def, torch, "template_torch_wall", "torch"));
                    blockstate(namespace, name, Json.obj("variants", Json.obj(
                            "facing=east", Json.obj("model", main),
                            "facing=south", Json.obj("model", main, "y", 90),
                            "facing=west", Json.obj("model", main, "y", 180),
                            "facing=north", Json.obj("model", main, "y", 270))));
                }
            }
            case ContentBlockTypes.CROP -> {
                JsonObject variants = new JsonObject();
                int last = def.cropMaxAge();
                for (int stage = 0; stage <= last; stage++) {
                    String stageTexture = textureOr(namespace, name + "_stage" + stage, texture);
                    model(def, namespace, name + "_stage" + stage, parented(BLOCK + "crop", stageTexture == null ? null : Json.obj("crop", stageTexture)));
                }
                for (int age = 0; age <= 7; age++) { variants.add("age=" + age, Json.obj("model", main + "_stage" + Math.min(age, last))); }
                blockstate(namespace, name, Json.obj("variants", variants));
            }
            case ContentBlockTypes.SAPLING, ContentBlockTypes.FLOWER, ContentBlockTypes.CANE -> {
                model(def, namespace, name, textured(def, texture, "cross", "cross"));
                blockstate(namespace, name, singleVariant(main));
            }
            case ContentBlockTypes.VINE -> {
                model(def, namespace, name, parented(BLOCK + "vine", texture == null ? null : Json.obj("vine", texture, "particle", texture)));
                blockstate(namespace, name, Json.obj("multipart", Json.arr(
                        Json.obj("when", Json.obj("north", "true"), "apply", Json.obj("model", main)),
                        Json.obj("when", Json.obj("east", "true"), "apply", Json.obj("model", main, "y", 90)),
                        Json.obj("when", Json.obj("south", "true"), "apply", Json.obj("model", main, "y", 180)),
                        Json.obj("when", Json.obj("west", "true"), "apply", Json.obj("model", main, "y", 270)),
                        Json.obj("when", Json.obj("up", "true"), "apply", Json.obj("model", main, "x", 270, "y", 90)),
                        Json.obj("when", Json.obj("north", "false", "east", "false", "south", "false", "west", "false", "up", "false"), "apply", Json.obj("model", main)))));
            }
            case ContentBlockTypes.LEAVES -> {
                model(def, namespace, name, cube(def, texture, "leaves"));
                blockstate(namespace, name, singleVariant(main));
            }
            case ContentBlockTypes.PORTAL -> {
                String face = texture == null ? "minecraft:block/nether_portal" : texture;
                if (def.fullCube()) {
                    model(def, namespace, name, parented(BLOCK + "cube_all", Json.obj("all", face)));
                    blockstate(namespace, name, singleVariant(main));
                }
                else {
                    model(def, namespace, name + "_x", portal(face, Json.arr(0, 0, 6), Json.arr(16, 16, 10), "north", "south"));
                    model(def, namespace, name + "_z", portal(face, Json.arr(6, 0, 0), Json.arr(10, 16, 16), "east", "west"));
                    model(def, namespace, name + "_flat", portal(face, Json.arr(0, 6, 0), Json.arr(16, 10, 16), "up", "down"));
                    blockstate(namespace, name, Json.obj("variants", Json.obj("axis=x", Json.obj("model", main + "_x"), "axis=z", Json.obj("model", main + "_z"), "axis=y", Json.obj("model", main + "_flat"))));
                }
            }
            default -> {
                String top = texture(namespace, name + "_top");
                String bottom = texture(namespace, name + "_bottom");
                model(def, namespace, name, top == null && bottom == null ? cube(def, texture, "cube_all") : bottomTop(def, texture, top, bottom));
                blockstate(namespace, name, ContentBlockTypes.PORTWAY.equals(type) ? portwayStates(def.portway(), main) : singleVariant(main));
            }
        }
    }

    private static JsonObject portwayStates(@Nullable PortwayDef portway, String main) {
        boolean facing = portway != null && portway.facing();
        boolean tall = portway != null && portway.tall();
        if (!facing && !tall) { return singleVariant(main); }
        JsonObject variants = new JsonObject();
        for (int i = 0; i < (facing ? FACINGS.length : 1); i++) {
            for (String half : tall ? new String[]{"lower", "upper"} : new String[]{""}) {
                String key = (facing ? "facing=" + FACINGS[i] : "") + (facing && tall ? "," : "") + (tall ? "half=" + half : "");
                variants.add(key, rotated(main, facing ? FACING_Y[i] : 0, false));
            }
        }
        return Json.obj("variants", variants);
    }

    private static JsonObject bell(String main) {
        JsonObject variants = new JsonObject();
        for (int attachment = 0; attachment < BELL_ATTACHMENTS.length; attachment++) {
            for (int turn = 0; turn < 4; turn++) {
                variants.add("attachment=" + BELL_ATTACHMENTS[attachment] + ",facing=" + BELL_TURN[attachment][turn], rotated(main + "_" + BELL_PARTS[attachment], turn * 90, false));
            }
        }
        return variants;
    }

    private static JsonObject bellFrame(@Nullable String texture, String part, boolean swings) {
        JsonObject model = Json.obj("parent", swings ? BLOCK + "bell_" + part : "resourcedatapackloader:block/pack_bell_" + part + "_whole");
        if (texture == null) { return model; }
        model.add("textures", swings ? Json.obj("bar", texture, "post", texture, "particle", texture)
                : Json.obj("bar", texture, "post", texture, "bell_side", texture, "bell_top", texture, "bell_bottom", texture, "particle", texture));
        return model;
    }

    private static JsonObject bellBody(@Nullable String texture) {
        JsonObject model = Json.obj("parent", "resourcedatapackloader:block/pack_bell_body");
        if (texture != null) { model.add("textures", Json.obj("bell_side", texture, "bell_top", texture, "bell_bottom", texture, "particle", texture)); }
        return model;
    }

    private static JsonObject bellItem(@Nullable String texture) {
        JsonObject model = Json.obj("parent", "resourcedatapackloader:block/pack_bell_item");
        if (texture != null) { model.add("textures", Json.obj("bar", texture, "post", texture, "bell_side", texture, "bell_top", texture, "bell_bottom", texture, "particle", texture)); }
        return model;
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

    private static JsonObject portal(String texture, JsonArray from, JsonArray to, String first, String second) {
        JsonObject face = Json.obj("uv", Json.arr(0, 0, 16, 16), "texture", "#portal", "tintindex", 0);
        return Json.obj("textures", Json.obj("particle", texture, "portal", texture), "elements", Json.arr(Json.obj("from", from, "to", to, "faces", Json.obj(first, face, second, face.deepCopy()))));
    }

    private static JsonObject rotated(String model, int y, boolean uvlock) {
        JsonObject entry = Json.obj("model", model);
        if (y != 0) {
            entry.addProperty("y", y);
            if (uvlock) { entry.addProperty("uvlock", true); }
        }
        return entry;
    }

    private static JsonObject parented(String parent, @Nullable JsonObject textures) { return textures == null ? Json.obj("parent", parent) : Json.obj("parent", parent, "textures", textures); }

    private static JsonObject singleVariant(String model) { return Json.obj("variants", Json.obj("", Json.obj("model", model))); }

    private static JsonObject cube(BlockDef def, @Nullable String texture, String template) { return textured(def, texture, template, "all"); }

    private static JsonObject bottomTop(BlockDef def, @Nullable String side, @Nullable String top, @Nullable String bottom) { return side == null ? parented(parent(def), null) : parented(BLOCK + "cube_bottom_top", Json.obj("bottom", bottom == null ? side : bottom, "top", top == null ? side : top, "side", side)); }

    private static JsonObject column(BlockDef def, @Nullable String side, @Nullable String end, String template) { return side == null ? parented(parent(def), null) : parented(BLOCK + template, Json.obj("end", end, "side", side)); }

    private static JsonObject sided(BlockDef def, @Nullable String texture, String template) { return texture == null ? parented(parent(def), null) : parented(BLOCK + template, Json.obj("bottom", texture, "top", texture, "side", texture)); }

    private static JsonObject textured(BlockDef def, @Nullable String texture, String template, String slot) { return texture == null ? parented(parent(def), null) : parented(BLOCK + template, Json.obj(slot, texture)); }

    private static String parent(BlockDef def) {
        ResourceLocation model = ContentParser.location(def.modelBlock());
        if (model == null) { model = ResourceLocation.parse("minecraft:stone"); }
        return model.getNamespace() + ":block/" + model.getPath();
    }

    private static JsonObject chestItem(BlockDef def, String main) {
        ContainerDef held = def.container();
        if (held == null || !held.chestModel()) { return Json.obj("parent", main); }
        if (held.chestTexture() == null) { return Json.obj("parent", "resourcedatapackloader:block/pack_chest"); }
        String path = held.chestTexture().getPath();
        if (path.startsWith("textures/")) { path = path.substring("textures/".length()); }
        if (path.endsWith(".png")) { path = path.substring(0, path.length() - ".png".length()); }
        return Json.obj("parent", "resourcedatapackloader:block/pack_chest", "textures", Json.obj("texture", held.chestTexture().getNamespace() + ":" + path));
    }

    static void itemModel(BlockDef def, String namespace, String name, String type) {
        String main = namespace + ":block/" + name;
        JsonObject model = switch (type) {
            case ContentBlockTypes.FENCE, ContentBlockTypes.WALL -> Json.obj("parent", main + "_inventory");
            case ContentBlockTypes.TRAPDOOR -> Json.obj("parent", main + "_bottom");
            case ContentBlockTypes.BANNER -> Json.obj("parent", "minecraft:item/template_banner");
            case ContentBlockTypes.CONTAINER -> chestItem(def, main);
            case ContentBlockTypes.PORTAL -> Json.obj("parent", def.fullCube() ? main : main + "_x");
            case ContentBlockTypes.BELL -> bellItem(texture(namespace, name));
            case ContentBlockTypes.DOOR, ContentBlockTypes.LADDER, ContentBlockTypes.TORCH, ContentBlockTypes.SAPLING, ContentBlockTypes.FLOWER, ContentBlockTypes.CANE, ContentBlockTypes.VINE, ContentBlockTypes.PANE -> {
                String flat = ContentGenerated.provided(PackType.CLIENT_RESOURCES, namespace, "textures/item/" + name + ".png") ? namespace + ":item/" + name : texture(namespace, name);
                yield flat == null ? Json.obj("parent", main) : Json.obj("parent", ContentGenerated.ITEM_GENERATED, "textures", Json.obj("layer0", flat));
            }
            default -> Json.obj("parent", main);
        };
        ContentGenerated.asset(namespace, "models/item/" + name + PackManager.JSON_SUFFIX, model);
    }

    @Nullable private static String texture(String namespace, String name) { return ContentGenerated.provided(PackType.CLIENT_RESOURCES, namespace, "textures/block/" + name + ".png") ? namespace + ":block/" + name : null; }

    @Nullable private static String textureOr(String namespace, String name, @Nullable String fallback) {
        String found = texture(namespace, name);
        return found == null ? fallback : found;
    }

    static void blockstate(String namespace, String name, JsonObject json) { ContentGenerated.asset(namespace, "blockstates/" + name + PackManager.JSON_SUFFIX, json); }

    private static void model(BlockDef def, String namespace, String name, JsonObject json) {
        String layer = ContentBlockTypes.renderType(def);
        if (layer != null && !json.has("render_type")) { json.addProperty("render_type", layer); }
        ContentGenerated.asset(namespace, "models/block/" + name + PackManager.JSON_SUFFIX, json);
    }
}
