package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

final class Blockstates {
    private static final String[] FACINGS = {"east", "south", "west", "north"};
    private static final int[] FACING_Y = {0, 90, 180, 270};
    private static final String[] SHAPES = {"straight", "inner_left", "inner_right", "outer_left", "outer_right"};
    private static final int[] BOTTOM_TURN = {0, 270, 0, 270, 0};
    private static final int[] TOP_TURN = {0, 0, 90, 0, 90};
    private static final String[] SIDES = {"north", "east", "south", "west"};
    private static final int CROP_AGES = 8;
    private static final int CANE_AGES = 16;

    private Blockstates() {}

    static void generate(Ported pack) {
        for (Map.Entry<String, JsonObject> entry : pack.blockDefs().entrySet()) {
            String namespace = entry.getKey().substring(0, entry.getKey().indexOf(':'));
            String file = entry.getKey().substring(entry.getKey().indexOf(':') + 1);
            JsonObject def = entry.getValue();
            if (pack.exposes(namespace, "blockstates/" + file + ".json")) { continue; }
            List<String> variants = new ArrayList<>();
            if (def.has("variants") && def.get("variants").isJsonObject()) {
                for (Map.Entry<String, JsonElement> variant : def.getAsJsonObject("variants").entrySet()) { variants.add(variant.getKey()); }
            }
            if (variants.isEmpty()) { continue; }
            String type = def.has("type") && def.get("type").isJsonPrimitive() ? def.get("type").getAsString().toLowerCase(Locale.ROOT) : "basic";
            new Blockstates.Maker(pack, namespace, file, variants, type, def).make();
        }
        for (Map.Entry<String, JsonObject> entry : pack.itemDefs().entrySet()) {
            String namespace = entry.getKey().substring(0, entry.getKey().indexOf(':'));
            String file = entry.getKey().substring(entry.getKey().indexOf(':') + 1);
            JsonObject def = entry.getValue();
            if (!def.has("variants") || !def.get("variants").isJsonObject()) { continue; }
            String type = def.has("type") && def.get("type").isJsonPrimitive() ? def.get("type").getAsString().toLowerCase(Locale.ROOT) : "";
            int count = def.getAsJsonObject("variants").size();
            for (Map.Entry<String, JsonElement> variant : def.getAsJsonObject("variants").entrySet()) {
                String nested = "models/item/" + file + "/" + variant.getKey() + ".json";
                if (pack.exposes(namespace, nested)) { continue; }
                String texture = texture(pack, namespace, "items", variant.getKey(), file);
                if (texture == null) {
                    pack.note("The item " + namespace + ":" + variant.getKey() + " has no model and no texture under textures/item, so it has no model on 1.12.2 either");
                    continue;
                }
                JsonObject model = obj("parent", type.contains("pickaxe") || type.contains("axe") || type.contains("sword") || type.contains("shovel") || type.contains("hoe") || type.contains("tool") ? "item/handheld" : "item/generated", "textures", obj("layer0", texture));
                model(pack, namespace, nested, model);
                if (count == 1 && !pack.exposes(namespace, "models/item/" + file + ".json")) { model(pack, namespace, "models/item/" + file + ".json", model); }
                pack.note("The item " + namespace + ":" + variant.getKey() + " had its model generated on the modern line, so " + namespace + ":" + nested + " is generated for 1.12.2");
            }
        }
    }

    @Nullable private static String texture(Ported pack, String namespace, String folder, String variant, String file) { return texture(pack, namespace, folder, variant, file, ""); }

    @Nullable private static String texture(Ported pack, String namespace, String folder, String variant, String file, String suffix) {
        List<String> names = new ArrayList<>();
        names.add(variant + suffix);
        if (variant.startsWith(file + "_")) { names.add(variant.substring(file.length() + 1) + suffix); }
        names.add(file + suffix);
        for (String name : names) {
            String path = "textures/" + folder + "/" + name + ".png";
            if (pack.hasTexture(namespace, path) || pack.hasTexture(namespace, path + ".json")) { return namespace + ":" + folder + "/" + name; }
        }
        return null;
    }

    private static void model(Ported pack, String namespace, String path, JsonObject json) { pack.made(namespace, path, Ported.GSON.toJson(json), "generated"); }

    static JsonObject obj(Object... pairs) {
        JsonObject out = new JsonObject();
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            Object value = pairs[i + 1];
            String key = (String) pairs[i];
            if (value instanceof JsonElement) { out.add(key, (JsonElement) value); }
            else if (value instanceof Number) { out.addProperty(key, (Number) value); }
            else if (value instanceof Boolean) { out.addProperty(key, (Boolean) value); }
            else { out.addProperty(key, String.valueOf(value)); }
        }
        return out;
    }

    private static JsonArray one(JsonObject held) {
        JsonArray out = new JsonArray();
        out.add(held);
        return out;
    }

    private static final class Maker {
        private final Ported pack;
        private final String namespace;
        private final String file;
        private final List<String> variants;
        private final String type;
        private final JsonObject def;
        private final List<String> missing = new ArrayList<>();

        Maker(Ported pack, String namespace, String file, List<String> variants, String type, JsonObject def) {
            this.pack = pack;
            this.namespace = namespace;
            this.file = file;
            this.variants = variants;
            this.type = type;
            this.def = def;
        }

        private String tex(String variant, String suffix, @Nullable String fallback) {
            String found = texture(pack, namespace, "blocks", variant, file, suffix);
            if (found != null) { return found; }
            if (fallback != null) { return fallback; }
            missing.add(variant + suffix);
            return namespace + ":blocks/" + variant + suffix;
        }

        private String face(String variant) { return tex(variant, "", null); }

        private String own(String model) { return namespace + ":" + model; }

        void make() {
            String first = variants.get(0);
            switch (type) {
                case "log": log(); break;
                case "leaves": leaves(); break;
                case "slab": slab(); break;
                case "fence": fence(); break;
                case "wall": wall(); break;
                case "pane": pane(); break;
                case "flower": flower(); break;
                case "cane": cane(first); break;
                case "sapling": sapling(first); break;
                case "crop": crop(first); break;
                case "door": door(first); break;
                case "trapdoor": trapdoor(first); break;
                case "fence_gate": gate(first); break;
                case "ladder": ladder(first); break;
                case "torch": torch(first); break;
                case "stairs": stairs(first); break;
                case "vine": vine(first); break;
                case "banner":
                    pack.note("The banner " + namespace + ":" + file + " is drawn by its tile entity on 1.12.2, so no blockstate is generated for it");
                    return;
                default: cube(); break;
            }
            if (!missing.isEmpty()) { pack.note("The generated blockstate " + namespace + ":blockstates/" + file + ".json points at the textures " + String.join(", ", missing) + " under textures/block, which the pack does not have; add them or edit the blockstate"); }
            else { pack.note("The block file " + namespace + ":blocks/" + file + " had its blockstate and models generated on the modern line, so a 1.12.2 blockstate is generated for it"); }
        }

        private void state(JsonObject json) { model(pack, namespace, "blockstates/" + file + ".json", json); }

        private void state(String name, JsonObject json) { model(pack, namespace, "blockstates/" + name + ".json", json); }

        private void blockModel(String name, JsonObject json) { model(pack, namespace, "models/block/" + name + ".json", json); }

        private void itemModel(JsonObject json) {
            if (!pack.exposes(namespace, "models/item/" + file + ".json")) { model(pack, namespace, "models/item/" + file + ".json", json); }
        }

        private JsonObject forge(JsonObject defaults, JsonObject blocks, boolean inventory) {
            JsonObject variantsJson = new JsonObject();
            if (inventory) {
                variantsJson.add("inventory", one(new JsonObject()));
                variantsJson.add("normal", one(new JsonObject()));
            }
            variantsJson.add("blocks", blocks);
            return obj("forge_marker", 1, "defaults", defaults, "variants", variantsJson);
        }

        private void cube() {
            JsonObject blocks = new JsonObject();
            for (String variant : variants) {
                String side = face(variant);
                String top = texture(pack, namespace, "blocks", variant + "_top", " ");
                String bottom = texture(pack, namespace, "blocks", variant + "_bottom", " ");
                if (top == null && bottom == null) { blocks.add(variant, obj("textures", obj("all", side))); }
                else { blocks.add(variant, obj("model", "cube_bottom_top", "textures", obj("top", top == null ? side : top, "bottom", bottom == null ? side : bottom, "side", side))); }
            }
            state(forge(obj("transform", "forge:default-block", "model", "cube_all"), blocks, true));
        }

        private void leaves() {
            JsonObject blocks = new JsonObject();
            for (String variant : variants) { blocks.add(variant, obj("textures", obj("all", face(variant)))); }
            state(forge(obj("transform", "forge:default-block", "model", "leaves"), blocks, true));
        }

        private void log() {
            JsonObject blocks = new JsonObject();
            for (String variant : variants) {
                String side = face(variant);
                String end = tex(variant, "_top", side);
                blocks.add(variant, obj("textures", obj("end", end, "side", side, "all", side)));
            }
            JsonObject axis = obj("y", new JsonObject(), "x", obj("x", 90, "y", 90), "z", obj("x", 90), "none", obj("model", "cube_all"));
            JsonObject json = forge(obj("transform", "forge:default-block", "model", "cube_column"), blocks, false);
            json.getAsJsonObject("variants").add("inventory", one(new JsonObject()));
            json.getAsJsonObject("variants").add("axis", axis);
            state(json);
        }

        private void slab() {
            JsonObject blocks = new JsonObject();
            JsonObject doubled = new JsonObject();
            for (String variant : variants) {
                String side = face(variant);
                JsonObject textures = obj("bottom", tex(variant, "_bottom", side), "top", tex(variant, "_top", side), "side", side, "all", side);
                blocks.add(variant, obj("textures", textures));
                doubled.add(variant, obj("textures", textures));
            }
            JsonObject json = forge(obj("transform", "forge:default-block", "model", "half_slab"), blocks, false);
            json.getAsJsonObject("variants").add("half", obj("bottom", obj("model", "half_slab"), "top", obj("model", "upper_slab")));
            state(json);
            state(file + "_double", forge(obj("transform", "forge:default-block", "model", "cube_all"), doubled, true));
        }

        private void fence() {
            JsonObject blocks = new JsonObject();
            for (String variant : variants) {
                String texture = face(variant);
                blocks.add(variant, obj("textures", obj("texture", texture)));
                model(pack, namespace, "models/item/" + file + "/" + variant + ".json", obj("parent", "block/fence_inventory", "textures", obj("texture", texture)));
            }
            JsonObject json = forge(obj("model", "fence_post", "uvlock", true), blocks, false);
            for (int i = 0; i < SIDES.length; i++) { json.getAsJsonObject("variants").add(SIDES[i], obj("true", obj("submodel", obj(SIDES[i], obj("model", "fence_side", "y", i * 90, "uvlock", true))), "false", new JsonObject())); }
            state(json);
        }

        private void wall() {
            JsonObject blocks = new JsonObject();
            for (String variant : variants) {
                String texture = face(variant);
                blocks.add(variant, obj("textures", obj("wall", texture)));
                model(pack, namespace, "models/item/" + file + "/" + variant + ".json", obj("parent", "block/wall_inventory", "textures", obj("wall", texture)));
            }
            JsonObject json = forge(obj("uvlock", true), blocks, false);
            JsonObject variantsJson = json.getAsJsonObject("variants");
            variantsJson.add("up", obj("true", obj("submodel", obj("post", obj("model", "wall_post"))), "false", obj("submodel", obj("nopost", obj("model", "block")))));
            for (int i = 0; i < SIDES.length; i++) { variantsJson.add(SIDES[i], obj("true", obj("submodel", obj(SIDES[i], obj("model", "wall_side", "y", i * 90))), "false", new JsonObject())); }
            state(json);
        }

        private void pane() {
            JsonObject blocks = new JsonObject();
            for (String variant : variants) {
                String texture = face(variant);
                blocks.add(variant, obj("textures", obj("pane", texture, "edge", tex(variant, "_top", texture))));
                model(pack, namespace, "models/item/" + file + "/" + variant + ".json", obj("parent", "item/generated", "textures", obj("layer0", texture)));
            }
            JsonObject json = forge(obj("model", "pane_post", "uvlock", true), blocks, false);
            JsonObject variantsJson = json.getAsJsonObject("variants");
            variantsJson.add("north", obj("true", obj("submodel", obj("north", obj("model", "pane_side"))), "false", obj("submodel", obj("north_off", obj("model", "pane_noside")))));
            variantsJson.add("east", obj("true", obj("submodel", obj("east", obj("model", "pane_side", "y", 90))), "false", obj("submodel", obj("east_off", obj("model", "pane_noside_alt")))));
            variantsJson.add("south", obj("true", obj("submodel", obj("south", obj("model", "pane_side_alt"))), "false", obj("submodel", obj("south_off", obj("model", "pane_noside_alt", "y", 90)))));
            variantsJson.add("west", obj("true", obj("submodel", obj("west", obj("model", "pane_side_alt", "y", 90))), "false", obj("submodel", obj("west_off", obj("model", "pane_noside", "y", 270)))));
            state(json);
        }

        private void flower() {
            JsonObject blocks = new JsonObject();
            for (String variant : variants) { blocks.add(variant, obj("textures", obj("cross", face(variant), "layer0", face(variant)))); }
            JsonObject json = forge(obj("model", "cross"), blocks, false);
            json.getAsJsonObject("variants").add("inventory", one(obj("model", "builtin/generated", "transform", "forge:default-item")));
            json.getAsJsonObject("variants").add("normal", one(new JsonObject()));
            state(json);
        }

        private void cane(String variant) {
            String texture = face(variant);
            JsonObject ages = new JsonObject();
            for (int age = 0; age < CANE_AGES; age++) { ages.add(String.valueOf(age), new JsonObject()); }
            state(obj("forge_marker", 1, "defaults", obj("model", "cross", "textures", obj("cross", texture)), "variants", obj("inventory", one(obj("model", "builtin/generated", "transform", "forge:default-item", "textures", obj("layer0", texture))), "age", ages)));
        }

        private void sapling(String variant) {
            JsonObject sapling = def.has("sapling") && def.get("sapling").isJsonObject() ? def.getAsJsonObject("sapling") : new JsonObject();
            int stages = Math.max(1, sapling.has("stages") && sapling.get("stages").isJsonPrimitive() ? sapling.get("stages").getAsInt() : 2);
            JsonObject stage = new JsonObject();
            for (int i = 0; i < stages; i++) { stage.add(String.valueOf(i), new JsonObject()); }
            String texture = face(variant);
            state(obj("forge_marker", 1, "defaults", obj("model", "cross", "textures", obj("cross", texture)), "variants", obj("stage", stage)));
            itemModel(obj("parent", "item/generated", "textures", obj("layer0", texture)));
        }

        private void crop(String variant) {
            JsonObject json = new JsonObject();
            String plain = face(variant);
            for (int age = 0; age < CROP_AGES; age++) {
                String texture = texture(pack, namespace, "blocks", variant, file, "_stage" + age);
                if (texture == null) { texture = texture(pack, namespace, "blocks", variant, file, "_" + age); }
                String name = file + "_stage" + age;
                blockModel(name, obj("parent", "block/crop", "textures", obj("crop", texture == null ? plain : texture)));
                json.add("age=" + age, obj("model", own(name)));
            }
            state(obj("variants", json));
        }

        private void door(String variant) {
            String plain = face(variant);
            String top = tex(variant, "_top", plain);
            String bottom = tex(variant, "_bottom", plain);
            for (String part : new String[] {"door_bottom", "door_bottom_rh", "door_top", "door_top_rh"}) { blockModel(file + "_" + part, obj("parent", "block/" + part, "textures", obj("top", top, "bottom", bottom))); }
            JsonObject json = new JsonObject();
            for (String half : new String[] {"lower", "upper"}) {
                String part = "lower".equals(half) ? "door_bottom" : "door_top";
                for (String hinge : new String[] {"left", "right"}) {
                    for (String open : new String[] {"false", "true"}) {
                        boolean rightHanded = "right".equals(hinge) != "true".equals(open);
                        for (int i = 0; i < 4; i++) {
                            int turn = FACING_Y[i] + ("true".equals(open) ? ("left".equals(hinge) ? 90 : 270) : 0);
                            json.add("facing=" + FACINGS[i] + ",half=" + half + ",hinge=" + hinge + ",open=" + open, rotated(own(file + "_" + part + (rightHanded ? "_rh" : "")), turn % 360, false));
                        }
                    }
                }
            }
            state(obj("variants", json));
            itemModel(obj("parent", "item/generated", "textures", obj("layer0", itemTexture(variant, plain))));
        }

        private String itemTexture(String variant, String fallback) {
            String found = texture(pack, namespace, "items", variant, file);
            return found == null ? fallback : found;
        }

        private void trapdoor(String variant) {
            String texture = face(variant);
            for (String part : new String[] {"bottom", "top", "open"}) { blockModel(file + "_trapdoor_" + part, obj("parent", "block/trapdoor_" + part, "textures", obj("texture", texture))); }
            JsonObject json = new JsonObject();
            String[] order = {"north", "east", "south", "west"};
            for (int i = 0; i < 4; i++) {
                json.add("facing=" + order[i] + ",half=bottom,open=false", obj("model", own(file + "_trapdoor_bottom")));
                json.add("facing=" + order[i] + ",half=top,open=false", obj("model", own(file + "_trapdoor_top")));
                json.add("facing=" + order[i] + ",half=bottom,open=true", rotated(own(file + "_trapdoor_open"), i * 90, false));
                json.add("facing=" + order[i] + ",half=top,open=true", rotated(own(file + "_trapdoor_open"), i * 90, false));
            }
            state(obj("variants", json));
            itemModel(obj("parent", own("block/" + file + "_trapdoor_bottom")));
        }

        private void gate(String variant) {
            String texture = face(variant);
            String[][] parts = {{"closed", "fence_gate_closed"}, {"open", "fence_gate_open"}, {"wall_closed", "wall_gate_closed"}, {"wall_open", "wall_gate_open"}};
            for (String[] part : parts) { blockModel(file + "_gate_" + part[0], obj("parent", "block/" + part[1], "textures", obj("texture", texture))); }
            JsonObject json = new JsonObject();
            String[] order = {"south", "west", "north", "east"};
            for (int i = 0; i < 4; i++) {
                for (String wall : new String[] {"false", "true"}) {
                    for (String open : new String[] {"false", "true"}) {
                        String part = ("true".equals(wall) ? "wall_" : "") + ("true".equals(open) ? "open" : "closed");
                        json.add("facing=" + order[i] + ",in_wall=" + wall + ",open=" + open, rotated(own(file + "_gate_" + part), i * 90, true));
                    }
                }
            }
            state(obj("variants", json));
            itemModel(obj("parent", own("block/" + file + "_gate_closed")));
        }

        private void ladder(String variant) {
            String texture = face(variant);
            blockModel(file + "_ladder", obj("parent", "block/ladder", "textures", obj("texture", texture, "particle", texture)));
            JsonObject json = new JsonObject();
            String[] order = {"north", "east", "south", "west"};
            for (int i = 0; i < 4; i++) { json.add("facing=" + order[i], rotated(own(file + "_ladder"), i * 90, false)); }
            state(obj("variants", json));
            itemModel(obj("parent", "item/generated", "textures", obj("layer0", itemTexture(variant, texture))));
        }

        private void torch(String variant) {
            String texture = face(variant);
            blockModel(file + "_torch", obj("parent", "block/torch", "textures", obj("torch", texture)));
            blockModel(file + "_torch_wall", obj("parent", "block/torch_wall", "textures", obj("torch", texture)));
            JsonObject json = new JsonObject();
            json.add("facing=up", obj("model", own(file + "_torch")));
            for (int i = 0; i < 4; i++) { json.add("facing=" + FACINGS[i], rotated(own(file + "_torch_wall"), FACING_Y[i], false)); }
            state(obj("variants", json));
            itemModel(obj("parent", "item/generated", "textures", obj("layer0", itemTexture(variant, texture))));
        }

        private void stairs(String variant) {
            String side = face(variant);
            JsonObject textures = obj("bottom", tex(variant, "_bottom", side), "top", tex(variant, "_top", side), "side", side);
            blockModel(file + "_stairs", obj("parent", "block/stairs", "textures", textures));
            blockModel(file + "_inner_stairs", obj("parent", "block/inner_stairs", "textures", textures));
            blockModel(file + "_outer_stairs", obj("parent", "block/outer_stairs", "textures", textures));
            JsonObject json = new JsonObject();
            for (int facing = 0; facing < 4; facing++) {
                for (String half : new String[] {"bottom", "top"}) {
                    boolean top = "top".equals(half);
                    for (int shape = 0; shape < SHAPES.length; shape++) {
                        String model = own(file + (shape == 0 ? "_stairs" : shape < 3 ? "_inner_stairs" : "_outer_stairs"));
                        JsonObject entry = rotated(model, (FACING_Y[facing] + (top ? TOP_TURN[shape] : BOTTOM_TURN[shape])) % 360, true);
                        if (top) {
                            entry.addProperty("x", 180);
                            entry.addProperty("uvlock", true);
                        }
                        json.add("facing=" + FACINGS[facing] + ",half=" + half + ",shape=" + SHAPES[shape], entry);
                    }
                }
            }
            state(obj("variants", json));
            itemModel(obj("parent", own("block/" + file + "_stairs")));
        }

        private void vine(String variant) {
            String texture = face(variant);
            JsonArray elements = new JsonArray();
            elements.add(obj("from", numbers(0, 0, 0.8), "to", numbers(16, 16, 0.8), "shade", false, "faces", obj("north", obj("uv", numbers(0, 0, 16, 16), "texture", "#vine", "tintindex", 0), "south", obj("uv", numbers(16, 0, 0, 16), "texture", "#vine", "tintindex", 0))));
            blockModel(file + "_vine", obj("ambientocclusion", false, "textures", obj("particle", texture, "vine", texture), "elements", elements));
            JsonArray parts = new JsonArray();
            parts.add(obj("when", obj("up", "true"), "apply", obj("model", own(file + "_vine"), "x", 270, "uvlock", true)));
            for (int i = 0; i < SIDES.length; i++) { parts.add(obj("when", obj(SIDES[i], "true"), "apply", rotated(own(file + "_vine"), i * 90, true))); }
            state(obj("multipart", parts));
            itemModel(obj("parent", "item/generated", "textures", obj("layer0", itemTexture(variant, texture))));
        }

        private static JsonObject rotated(String model, int y, boolean uvlock) {
            JsonObject entry = obj("model", model);
            if (y != 0) {
                entry.addProperty("y", y);
                if (uvlock) { entry.addProperty("uvlock", true); }
            }
            return entry;
        }

        private static JsonArray numbers(double... values) {
            JsonArray out = new JsonArray();
            for (double value : values) { out.add(value); }
            return out;
        }
    }
}
