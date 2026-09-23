package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ContainerDef;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;

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
                if (def.container() != null && def.container().chestModel()) {
                    blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj("", ContentGenerated.obj("model", "resourcedatapackloader:block/pack_chest"))));
                }
                else {
                    model(def, namespace, name, cube(def, texture, "cube_all"));
                    blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj("", ContentGenerated.obj("model", main))));
                }
            }
            case ContentBlockTypes.BELL -> {
                boolean swings = def.bell() == null || def.bell().swing();
                for (String part : BELL_PARTS) { model(def, namespace, name + "_" + part, bellFrame(texture, part, swings)); }
                if (swings) { model(def, namespace, name + "_body", bellBody(texture)); }
                blockstate(namespace, name, ContentGenerated.obj("variants", bell(main)));
            }
            case ContentBlockTypes.LOG -> {
                String top = textureOr(namespace, name + "_top", texture);
                model(def, namespace, name, column(def, texture, top, "cube_column"));
                model(def, namespace, name + "_horizontal", column(def, texture, top, "cube_column_horizontal"));
                blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj(
                        "axis=x", ContentGenerated.obj("model", main + "_horizontal", "x", 90, "y", 90),
                        "axis=y", ContentGenerated.obj("model", main),
                        "axis=z", ContentGenerated.obj("model", main + "_horizontal", "x", 90))));
            }
            case ContentBlockTypes.SLAB -> {
                model(def, namespace, name, sided(def, texture, "slab"));
                model(def, namespace, name + "_top", sided(def, texture, "slab_top"));
                model(def, namespace, name + "_double", cube(def, texture, "cube_all"));
                blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj(
                        "type=bottom", ContentGenerated.obj("model", main),
                        "type=top", ContentGenerated.obj("model", main + "_top"),
                        "type=double", ContentGenerated.obj("model", main + "_double"))));
            }
            case ContentBlockTypes.STAIRS -> {
                model(def, namespace, name, sided(def, texture, "stairs"));
                model(def, namespace, name + "_inner", sided(def, texture, "inner_stairs"));
                model(def, namespace, name + "_outer", sided(def, texture, "outer_stairs"));
                blockstate(namespace, name, ContentGenerated.obj("variants", stairs(main)));
            }
            case ContentBlockTypes.FENCE -> {
                model(def, namespace, name + "_post", textured(def, texture, "fence_post", "texture"));
                model(def, namespace, name + "_side", textured(def, texture, "fence_side", "texture"));
                model(def, namespace, name + "_inventory", textured(def, texture, "fence_inventory", "texture"));
                blockstate(namespace, name, ContentGenerated.obj("multipart", ContentGenerated.arr(
                        ContentGenerated.obj("apply", ContentGenerated.obj("model", main + "_post")),
                        ContentGenerated.obj("when", ContentGenerated.obj("north", "true"), "apply", ContentGenerated.obj("model", main + "_side", "uvlock", true)),
                        ContentGenerated.obj("when", ContentGenerated.obj("east", "true"), "apply", ContentGenerated.obj("model", main + "_side", "y", 90, "uvlock", true)),
                        ContentGenerated.obj("when", ContentGenerated.obj("south", "true"), "apply", ContentGenerated.obj("model", main + "_side", "y", 180, "uvlock", true)),
                        ContentGenerated.obj("when", ContentGenerated.obj("west", "true"), "apply", ContentGenerated.obj("model", main + "_side", "y", 270, "uvlock", true)))));
            }
            case ContentBlockTypes.WALL -> {
                model(def, namespace, name + "_post", textured(def, texture, "template_wall_post", "wall"));
                model(def, namespace, name + "_side", textured(def, texture, "template_wall_side", "wall"));
                model(def, namespace, name + "_side_tall", textured(def, texture, "template_wall_side_tall", "wall"));
                model(def, namespace, name + "_inventory", textured(def, texture, "wall_inventory", "wall"));
                JsonArray parts = ContentGenerated.arr(ContentGenerated.obj("when", ContentGenerated.obj("up", "true"), "apply", ContentGenerated.obj("model", main + "_post")));
                for (int i = 0; i < 4; i++) {
                    String side = FACINGS[(i + 3) % 4];
                    parts.add(ContentGenerated.obj("when", ContentGenerated.obj(side, "low"), "apply", rotated(main + "_side", i * 90, true)));
                    parts.add(ContentGenerated.obj("when", ContentGenerated.obj(side, "tall"), "apply", rotated(main + "_side_tall", i * 90, true)));
                }
                blockstate(namespace, name, ContentGenerated.obj("multipart", parts));
            }
            case ContentBlockTypes.PANE -> {
                String edge = textureOr(namespace, name + "_top", texture);
                for (String part : new String[] {"post", "side", "side_alt", "noside", "noside_alt"}) {
                    JsonObject textures = part.startsWith("noside") ? ContentGenerated.obj("pane", texture) : ContentGenerated.obj("pane", texture, "edge", edge);
                    model(def, namespace, name + "_" + part, texture == null ? ContentGenerated.obj("parent", parent(def)) : ContentGenerated.obj("parent", BLOCK + "template_glass_pane_" + part, "textures", textures));
                }
                blockstate(namespace, name, ContentGenerated.obj("multipart", ContentGenerated.arr(
                        ContentGenerated.obj("apply", ContentGenerated.obj("model", main + "_post")),
                        ContentGenerated.obj("when", ContentGenerated.obj("north", "true"), "apply", ContentGenerated.obj("model", main + "_side")),
                        ContentGenerated.obj("when", ContentGenerated.obj("east", "true"), "apply", ContentGenerated.obj("model", main + "_side", "y", 90)),
                        ContentGenerated.obj("when", ContentGenerated.obj("south", "true"), "apply", ContentGenerated.obj("model", main + "_side_alt")),
                        ContentGenerated.obj("when", ContentGenerated.obj("west", "true"), "apply", ContentGenerated.obj("model", main + "_side_alt", "y", 90)),
                        ContentGenerated.obj("when", ContentGenerated.obj("north", "false"), "apply", ContentGenerated.obj("model", main + "_noside")),
                        ContentGenerated.obj("when", ContentGenerated.obj("east", "false"), "apply", ContentGenerated.obj("model", main + "_noside_alt")),
                        ContentGenerated.obj("when", ContentGenerated.obj("south", "false"), "apply", ContentGenerated.obj("model", main + "_noside_alt", "y", 90)),
                        ContentGenerated.obj("when", ContentGenerated.obj("west", "false"), "apply", ContentGenerated.obj("model", main + "_noside", "y", 270)))));
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
                            model(def, namespace, name + suffix, texture == null ? ContentGenerated.obj("parent", BLOCK + "door" + suffix) : ContentGenerated.obj("parent", BLOCK + "door" + suffix, "textures", ContentGenerated.obj("top", top, "bottom", bottom)));
                            for (int i = 0; i < 4; i++) {
                                int turn = FACING_Y[i] + ("true".equals(open) ? ("left".equals(hinge) ? 90 : 270) : 0);
                                variants.add("facing=" + FACINGS[i] + ",half=" + half + ",hinge=" + hinge + ",open=" + open, rotated(main + suffix, turn % 360, false));
                            }
                        }
                    }
                }
                blockstate(namespace, name, ContentGenerated.obj("variants", variants));
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
                blockstate(namespace, name, ContentGenerated.obj("variants", variants));
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
                blockstate(namespace, name, ContentGenerated.obj("variants", variants));
            }
            case ContentBlockTypes.LADDER -> {
                model(def, namespace, name, texture == null ? ContentGenerated.obj("parent", BLOCK + "ladder") : ContentGenerated.obj("parent", BLOCK + "ladder", "textures", ContentGenerated.obj("texture", texture, "particle", texture)));
                blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj(
                        "facing=north", ContentGenerated.obj("model", main),
                        "facing=east", ContentGenerated.obj("model", main, "y", 90),
                        "facing=south", ContentGenerated.obj("model", main, "y", 180),
                        "facing=west", ContentGenerated.obj("model", main, "y", 270))));
            }
            case ContentBlockTypes.BANNER -> {
                model(def, namespace, name, ContentGenerated.obj("parent", BLOCK + "banner"));
                blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj("", ContentGenerated.obj("model", main))));
            }
            case ContentBlockTypes.TORCH -> {
                if (entry.isMain()) {
                    model(def, namespace, name, textured(def, texture, "template_torch", "torch"));
                    blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj("", ContentGenerated.obj("model", main))));
                }
                else {
                    String torch = texture(namespace, entry.variant().name());
                    model(def, namespace, name, textured(def, torch, "template_torch_wall", "torch"));
                    blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj(
                            "facing=east", ContentGenerated.obj("model", main),
                            "facing=south", ContentGenerated.obj("model", main, "y", 90),
                            "facing=west", ContentGenerated.obj("model", main, "y", 180),
                            "facing=north", ContentGenerated.obj("model", main, "y", 270))));
                }
            }
            case ContentBlockTypes.CROP -> {
                JsonObject variants = new JsonObject();
                int last = def.cropMaxAge();
                for (int stage = 0; stage <= last; stage++) {
                    String stageTexture = textureOr(namespace, name + "_stage" + stage, texture);
                    model(def, namespace, name + "_stage" + stage, stageTexture == null ? ContentGenerated.obj("parent", BLOCK + "crop") : ContentGenerated.obj("parent", BLOCK + "crop", "textures", ContentGenerated.obj("crop", stageTexture)));
                }
                for (int age = 0; age <= 7; age++) { variants.add("age=" + age, ContentGenerated.obj("model", main + "_stage" + Math.min(age, last))); }
                blockstate(namespace, name, ContentGenerated.obj("variants", variants));
            }
            case ContentBlockTypes.SAPLING, ContentBlockTypes.FLOWER, ContentBlockTypes.CANE -> {
                model(def, namespace, name, textured(def, texture, "cross", "cross"));
                blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj("", ContentGenerated.obj("model", main))));
            }
            case ContentBlockTypes.VINE -> {
                model(def, namespace, name, texture == null ? ContentGenerated.obj("parent", BLOCK + "vine") : ContentGenerated.obj("parent", BLOCK + "vine", "textures", ContentGenerated.obj("vine", texture, "particle", texture)));
                blockstate(namespace, name, ContentGenerated.obj("multipart", ContentGenerated.arr(
                        ContentGenerated.obj("when", ContentGenerated.obj("north", "true"), "apply", ContentGenerated.obj("model", main)),
                        ContentGenerated.obj("when", ContentGenerated.obj("east", "true"), "apply", ContentGenerated.obj("model", main, "y", 90)),
                        ContentGenerated.obj("when", ContentGenerated.obj("south", "true"), "apply", ContentGenerated.obj("model", main, "y", 180)),
                        ContentGenerated.obj("when", ContentGenerated.obj("west", "true"), "apply", ContentGenerated.obj("model", main, "y", 270)),
                        ContentGenerated.obj("when", ContentGenerated.obj("up", "true"), "apply", ContentGenerated.obj("model", main, "x", 270, "y", 90)),
                        ContentGenerated.obj("when", ContentGenerated.obj("north", "false", "east", "false", "south", "false", "west", "false", "up", "false"), "apply", ContentGenerated.obj("model", main)))));
            }
            case ContentBlockTypes.LEAVES -> {
                model(def, namespace, name, cube(def, texture, "leaves"));
                blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj("", ContentGenerated.obj("model", main))));
            }
            case ContentBlockTypes.PORTAL -> {
                String face = texture == null ? "minecraft:block/nether_portal" : texture;
                if (def.fullCube()) {
                    model(def, namespace, name, ContentGenerated.obj("parent", BLOCK + "cube_all", "textures", ContentGenerated.obj("all", face)));
                    blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj("", ContentGenerated.obj("model", main))));
                }
                else {
                    model(def, namespace, name + "_x", portal(face, ContentGenerated.arr(0, 0, 6), ContentGenerated.arr(16, 16, 10), "north", "south"));
                    model(def, namespace, name + "_z", portal(face, ContentGenerated.arr(6, 0, 0), ContentGenerated.arr(10, 16, 16), "east", "west"));
                    model(def, namespace, name + "_flat", portal(face, ContentGenerated.arr(0, 6, 0), ContentGenerated.arr(16, 10, 16), "up", "down"));
                    blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj("axis=x", ContentGenerated.obj("model", main + "_x"), "axis=z", ContentGenerated.obj("model", main + "_z"), "axis=y", ContentGenerated.obj("model", main + "_flat"))));
                }
            }
            default -> {
                String top = texture(namespace, name + "_top");
                String bottom = texture(namespace, name + "_bottom");
                model(def, namespace, name, top == null && bottom == null ? cube(def, texture, "cube_all") : bottomTop(def, texture, top, bottom));
                blockstate(namespace, name, ContentGenerated.obj("variants", ContentGenerated.obj("", ContentGenerated.obj("model", main))));
            }
        }
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
        JsonObject model = ContentGenerated.obj("parent", swings ? BLOCK + "bell_" + part : "resourcedatapackloader:block/pack_bell_" + part + "_whole");
        if (texture == null) { return model; }
        model.add("textures", swings ? ContentGenerated.obj("bar", texture, "post", texture, "particle", texture)
                : ContentGenerated.obj("bar", texture, "post", texture, "bell_side", texture, "bell_top", texture, "bell_bottom", texture, "particle", texture));
        return model;
    }

    private static JsonObject bellBody(@Nullable String texture) {
        JsonObject model = ContentGenerated.obj("parent", "resourcedatapackloader:block/pack_bell_body");
        if (texture != null) { model.add("textures", ContentGenerated.obj("bell_side", texture, "bell_top", texture, "bell_bottom", texture, "particle", texture)); }
        return model;
    }

    private static JsonObject bellItem(@Nullable String texture) {
        JsonObject model = ContentGenerated.obj("parent", "resourcedatapackloader:block/pack_bell_item");
        if (texture != null) { model.add("textures", ContentGenerated.obj("bar", texture, "post", texture, "bell_side", texture, "bell_top", texture, "bell_bottom", texture, "particle", texture)); }
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
        JsonObject face = ContentGenerated.obj("uv", ContentGenerated.arr(0, 0, 16, 16), "texture", "#portal", "tintindex", 0);
        return ContentGenerated.obj("textures", ContentGenerated.obj("particle", texture, "portal", texture), "elements", ContentGenerated.arr(ContentGenerated.obj("from", from, "to", to, "faces", ContentGenerated.obj(first, face, second, face.deepCopy()))));
    }

    private static JsonObject rotated(String model, int y, boolean uvlock) {
        JsonObject entry = ContentGenerated.obj("model", model);
        if (y != 0) {
            entry.addProperty("y", y);
            if (uvlock) { entry.addProperty("uvlock", true); }
        }
        return entry;
    }

    private static JsonObject cube(BlockDef def, @Nullable String texture, String template) {
        return texture == null ? ContentGenerated.obj("parent", parent(def)) : ContentGenerated.obj("parent", BLOCK + template, "textures", ContentGenerated.obj("all", texture));
    }

    private static JsonObject bottomTop(BlockDef def, @Nullable String side, @Nullable String top, @Nullable String bottom) {
        return side == null ? ContentGenerated.obj("parent", parent(def)) : ContentGenerated.obj("parent", BLOCK + "cube_bottom_top", "textures", ContentGenerated.obj("bottom", bottom == null ? side : bottom, "top", top == null ? side : top, "side", side));
    }

    private static JsonObject column(BlockDef def, @Nullable String side, @Nullable String end, String template) {
        return side == null ? ContentGenerated.obj("parent", parent(def)) : ContentGenerated.obj("parent", BLOCK + template, "textures", ContentGenerated.obj("end", end, "side", side));
    }

    private static JsonObject sided(BlockDef def, @Nullable String texture, String template) {
        return texture == null ? ContentGenerated.obj("parent", parent(def)) : ContentGenerated.obj("parent", BLOCK + template, "textures", ContentGenerated.obj("bottom", texture, "top", texture, "side", texture));
    }

    private static JsonObject textured(BlockDef def, @Nullable String texture, String template, String slot) {
        return texture == null ? ContentGenerated.obj("parent", parent(def)) : ContentGenerated.obj("parent", BLOCK + template, "textures", ContentGenerated.obj(slot, texture));
    }

    private static String parent(BlockDef def) {
        ResourceLocation model = ContentParser.location(def.modelBlock());
        if (model == null) { model = ResourceLocation.parse("minecraft:stone"); }
        return model.getNamespace() + ":block/" + model.getPath();
    }

    private static JsonObject chestItem(BlockDef def, String main) {
        ContainerDef held = def.container();
        if (held == null || !held.chestModel()) { return ContentGenerated.obj("parent", main); }
        if (held.chestTexture() == null) { return ContentGenerated.obj("parent", "resourcedatapackloader:block/pack_chest"); }
        String path = held.chestTexture().getPath();
        if (path.startsWith("textures/")) { path = path.substring("textures/".length()); }
        if (path.endsWith(".png")) { path = path.substring(0, path.length() - ".png".length()); }
        return ContentGenerated.obj("parent", "resourcedatapackloader:block/pack_chest", "textures", ContentGenerated.obj("texture", held.chestTexture().getNamespace() + ":" + path));
    }

    static void itemModel(BlockDef def, String namespace, String name, String type) {
        String main = namespace + ":block/" + name;
        JsonObject model = switch (type) {
            case ContentBlockTypes.FENCE, ContentBlockTypes.WALL -> ContentGenerated.obj("parent", main + "_inventory");
            case ContentBlockTypes.TRAPDOOR -> ContentGenerated.obj("parent", main + "_bottom");
            case ContentBlockTypes.BANNER -> ContentGenerated.obj("parent", "minecraft:item/template_banner");
            case ContentBlockTypes.CONTAINER -> chestItem(def, main);
            case ContentBlockTypes.PORTAL -> ContentGenerated.obj("parent", def.fullCube() ? main : main + "_x");
            case ContentBlockTypes.BELL -> bellItem(texture(namespace, name));
            case ContentBlockTypes.DOOR, ContentBlockTypes.LADDER, ContentBlockTypes.TORCH, ContentBlockTypes.SAPLING, ContentBlockTypes.FLOWER, ContentBlockTypes.CANE, ContentBlockTypes.VINE, ContentBlockTypes.PANE -> {
                String flat = ContentGenerated.provided(PackType.CLIENT_RESOURCES, namespace, "textures/item/" + name + ".png") ? namespace + ":item/" + name : texture(namespace, name);
                yield flat == null ? ContentGenerated.obj("parent", main) : ContentGenerated.obj("parent", ContentGenerated.ITEM_GENERATED, "textures", ContentGenerated.obj("layer0", flat));
            }
            default -> ContentGenerated.obj("parent", main);
        };
        ContentGenerated.asset(namespace, "models/item/" + name + ".json", model);
    }

    @Nullable private static String texture(String namespace, String name) { return ContentGenerated.provided(PackType.CLIENT_RESOURCES, namespace, "textures/block/" + name + ".png") ? namespace + ":block/" + name : null; }

    @Nullable private static String textureOr(String namespace, String name, @Nullable String fallback) {
        String found = texture(namespace, name);
        return found == null ? fallback : found;
    }

    static void blockstate(String namespace, String name, JsonObject json) { ContentGenerated.asset(namespace, "blockstates/" + name + ".json", json); }

    private static void model(BlockDef def, String namespace, String name, JsonObject json) {
        String layer = ContentBlockTypes.renderType(def);
        if (layer != null && !json.has("render_type")) { json.addProperty("render_type", layer); }
        ContentGenerated.asset(namespace, "models/block/" + name + ".json", json);
    }
}
