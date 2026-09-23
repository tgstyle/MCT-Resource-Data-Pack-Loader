package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ConvertAssets {
    private static final Pattern LANG_BLOCK = Pattern.compile("^(block|item)\\.([a-z0-9_.-]+?)\\.([a-z0-9_/.-]+?)(\\.locked)?$");
    private static final Pattern LANG_FLUID = Pattern.compile("^fluid(?:_type)?\\.([a-z0-9_.-]+)\\.([a-z0-9_/-]+)$");
    private static final Pattern LANG_POTION = Pattern.compile("^item\\.minecraft\\.(potion|splash_potion|lingering_potion|tipped_arrow)\\.effect\\.(.+)$");
    private static final Pattern LANG_TAB = Pattern.compile("^itemGroup\\.([a-z0-9_.-]+)\\.([a-z0-9_/-]+)$");
    private static final Pattern LANG_ENTITY = Pattern.compile("^entity\\.([a-z0-9_.-]+)\\.([a-z0-9_/-]+)$");
    private static final Map<String, String> MODELS = new HashMap<>();

    static {
        String[][] models = {{"template_trapdoor_bottom", "trapdoor_bottom"}, {"template_trapdoor_top", "trapdoor_top"}, {"template_trapdoor_open", "trapdoor_open"},
                {"template_orientable_trapdoor_bottom", "trapdoor_bottom"}, {"template_orientable_trapdoor_top", "trapdoor_top"}, {"template_orientable_trapdoor_open", "trapdoor_open"},
                {"template_fence_gate", "fence_gate_closed"}, {"template_fence_gate_open", "fence_gate_open"}, {"template_fence_gate_wall", "wall_gate_closed"},
                {"template_fence_gate_wall_open", "wall_gate_open"}, {"door_bottom_left", "door_bottom"}, {"door_bottom_right", "door_bottom_rh"},
                {"door_top_left", "door_top"}, {"door_top_right", "door_top_rh"}, {"door_bottom_left_open", "door_bottom_rh"}, {"door_bottom_right_open", "door_bottom"},
                {"door_top_left_open", "door_top_rh"}, {"door_top_right_open", "door_top"}, {"slab", "half_slab"}, {"slab_top", "upper_slab"},
                {"template_glass_pane_post", "pane_post"}, {"template_glass_pane_side", "pane_side"}, {"template_glass_pane_side_alt", "pane_side_alt"},
                {"template_glass_pane_noside", "pane_noside"}, {"template_glass_pane_noside_alt", "pane_noside_alt"}, {"template_wall_post", "wall_post"},
                {"template_wall_side", "wall_side"}, {"template_wall_side_tall", "wall_side"}, {"template_torch_wall", "torch_wall"}, {"template_torch", "torch"},
                {"cube_column_horizontal", "cube_column"}};
        for (String[] pair : models) { MODELS.put(pair[0], pair[1]); }
    }

    private ConvertAssets() {}

    static String lang(String contents, Ported pack) {
        JsonObject json = new JsonParser().parse(contents).getAsJsonObject();
        Map<String, String> out = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) { continue; }
            String value = entry.getValue().getAsString().replace("\r", "").replace("\n", " ");
            for (String key : langKeys(entry.getKey(), pack)) { out.putIfAbsent(key, value); }
        }
        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, String> line : out.entrySet()) { text.append(line.getKey()).append('=').append(line.getValue()).append('\n'); }
        return text.toString();
    }

    private static List<String> langKeys(String key, Ported pack) {
        Matcher m = LANG_POTION.matcher(key);
        if (m.matches()) { return Collections.singletonList(m.group(1) + ".effect." + m.group(2)); }
        m = LANG_FLUID.matcher(key);
        if (m.matches() && pack.ownsNamespace(m.group(1))) { return Arrays.asList("fluid." + m.group(2), "tile." + m.group(1) + ":" + m.group(2) + ".name"); }
        m = LANG_TAB.matcher(key);
        if (m.matches() && pack.ownsNamespace(m.group(1))) { return Collections.singletonList("itemGroup." + m.group(2)); }
        m = LANG_BLOCK.matcher(key);
        if (m.matches() && pack.ownsNamespace(m.group(2))) {
            String namespace = m.group(2);
            String suffix = m.group(4) == null ? ".name" : ".locked";
            boolean block = "block".equals(m.group(1));
            Ported.Own own = block ? pack.ownBlock(namespace + ":" + m.group(3)) : pack.ownItem(namespace + ":" + m.group(3));
            if (own == null && block && pack.ownFluid(namespace + ":" + m.group(3))) { return Arrays.asList("tile." + namespace + ":" + m.group(3) + ".name", "fluid." + m.group(3)); }
            if (own == null) { return Collections.singletonList(key); }
            String prefix = Ported.BLOCKS.equals(own.folder) ? "tile." : "item.";
            List<String> keys = new ArrayList<>();
            keys.add(prefix + own.id() + "." + own.variant + suffix);
            if (own.variants == 1) { keys.add(prefix + own.id() + suffix); }
            return keys;
        }
        m = LANG_ENTITY.matcher(key);
        if (m.matches() && pack.ownsNamespace(m.group(1))) { return Collections.singletonList(key + ".name"); }
        return Collections.singletonList(key);
    }

    static String textureRef(String held) {
        if (held.startsWith("#")) { return held; }
        int colon = held.indexOf(':');
        String namespace = colon < 0 ? "" : held.substring(0, colon + 1);
        String rest = colon < 0 ? held : held.substring(colon + 1);
        if (rest.startsWith("block/")) { return namespace + "blocks/" + rest.substring("block/".length()); }
        if (rest.startsWith("item/")) { return namespace + "items/" + rest.substring("item/".length()); }
        if (rest.startsWith("textures/block/")) { return namespace + "textures/blocks/" + rest.substring("textures/block/".length()); }
        if (rest.startsWith("textures/item/")) { return namespace + "textures/items/" + rest.substring("textures/item/".length()); }
        return held;
    }

    static String modelRef(String held) {
        String bare = held.startsWith(Ids.MINECRAFT + ":") ? held.substring(Ids.MINECRAFT.length() + 1) : held;
        if (bare.startsWith("block/")) {
            String name = bare.substring("block/".length());
            return "block/" + MODELS.getOrDefault(name, name);
        }
        if (bare.startsWith("item/") || bare.startsWith("builtin/")) { return bare; }
        return held;
    }

    static String model(JsonObject json, Ported pack) {
        if (json.has("textures") && json.get("textures").isJsonObject()) {
            JsonObject textures = json.getAsJsonObject("textures");
            for (Map.Entry<String, JsonElement> texture : new ArrayList<>(textures.entrySet())) {
                if (texture.getValue().isJsonPrimitive()) { textures.addProperty(texture.getKey(), textureRef(texture.getValue().getAsString())); }
            }
        }
        if (json.has("parent") && json.get("parent").isJsonPrimitive()) { json.addProperty("parent", modelRef(json.get("parent").getAsString())); }
        json.remove("render_type");
        json.remove("gui_light");
        if (json.has("loader")) {
            pack.note("A model uses the loader " + json.get("loader") + ", which 1.12.2 Forge does not have, so it is served without it");
            json.remove("loader");
        }
        return Ported.GSON.toJson(json);
    }

    static String blockstate(JsonObject json) {
        if (json.has("variants") && json.get("variants").isJsonObject()) {
            JsonObject variants = json.getAsJsonObject("variants");
            JsonObject out = new JsonObject();
            for (Map.Entry<String, JsonElement> variant : variants.entrySet()) {
                stateModels(variant.getValue());
                out.add(variant.getKey().isEmpty() ? "normal" : variant.getKey(), variant.getValue());
            }
            json.add("variants", out);
        }
        if (json.has("multipart") && json.get("multipart").isJsonArray()) {
            for (JsonElement part : json.getAsJsonArray("multipart")) {
                if (part.isJsonObject() && part.getAsJsonObject().has("apply")) { stateModels(part.getAsJsonObject().get("apply")); }
            }
        }
        return Ported.GSON.toJson(json);
    }

    private static void stateModels(JsonElement element) {
        if (element.isJsonArray()) {
            for (JsonElement inner : element.getAsJsonArray()) { stateModels(inner); }
            return;
        }
        if (!element.isJsonObject() || !element.getAsJsonObject().has("model")) { return; }
        JsonObject held = element.getAsJsonObject();
        String model = held.get("model").getAsString();
        int colon = model.indexOf(':');
        String namespace = colon < 0 ? Ids.MINECRAFT : model.substring(0, colon);
        String rest = colon < 0 ? model : model.substring(colon + 1);
        if (rest.startsWith("block/")) {
            String name = rest.substring("block/".length());
            name = MODELS.containsKey(name) && Ids.MINECRAFT.equals(namespace) ? MODELS.get(name) : name;
            held.addProperty("model", Ids.MINECRAFT.equals(namespace) ? name : namespace + ":" + name);
        }
    }

    static String pixelMap(JsonObject json) {
        if (json.has("extends") && json.get("extends").isJsonPrimitive()) { json.addProperty("extends", textureRef(json.get("extends").getAsString())); }
        return Ported.GSON.toJson(json);
    }
}
