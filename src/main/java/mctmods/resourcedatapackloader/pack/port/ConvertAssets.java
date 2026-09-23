package mctmods.resourcedatapackloader.pack.port;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ConvertAssets {
    private static final Pattern LANG_TILE = Pattern.compile("^(tile|item)\\.([a-z0-9_]+)[:.]([a-z0-9_./-]+?)(?:\\.([a-z0-9_]+))?\\.(name|locked)$");
    private static final Pattern LANG_ENTITY = Pattern.compile("^entity\\.([a-z0-9_]+)\\.([a-z0-9_]+)\\.name$");
    private static final Pattern LANG_FLUID = Pattern.compile("^fluid\\.(?:([a-z0-9_]+)\\.)?([a-z0-9_]+)$");
    private static final Pattern LANG_POTION_TYPE = Pattern.compile("^(potion|splash_potion|lingering_potion|tipped_arrow)\\.effect\\.(.+)$");
    private static final Pattern LANG_TAB = Pattern.compile("^itemGroup\\.([a-z0-9_]+)$");
    private static final Map<String, String> RENAMED_MODELS = Map.ofEntries(
            Map.entry("trapdoor_bottom", "template_trapdoor_bottom"), Map.entry("trapdoor_top", "template_trapdoor_top"), Map.entry("trapdoor_open", "template_trapdoor_open"),
            Map.entry("fence_gate_closed", "template_fence_gate"), Map.entry("fence_gate_open", "template_fence_gate_open"), Map.entry("wall_gate_closed", "template_fence_gate_wall"),
            Map.entry("wall_gate_open", "template_fence_gate_wall_open"), Map.entry("door_bottom", "door_bottom_left"), Map.entry("door_bottom_rh", "door_bottom_right"),
            Map.entry("door_top", "door_top_left"), Map.entry("door_top_rh", "door_top_right"), Map.entry("half_slab", "slab"), Map.entry("upper_slab", "slab_top"),
            Map.entry("pane_post", "template_glass_pane_post"), Map.entry("pane_side", "template_glass_pane_side"), Map.entry("pane_side_alt", "template_glass_pane_side_alt"),
            Map.entry("pane_noside", "template_glass_pane_noside"), Map.entry("pane_noside_alt", "template_glass_pane_noside_alt"), Map.entry("wall_post", "template_wall_post"),
            Map.entry("wall_side", "template_wall_side"), Map.entry("torch_wall", "template_torch_wall"));

    private ConvertAssets() {}

    public static String lang(String contents, Ported pack) {
        Map<String, String> out = new LinkedHashMap<>();
        for (String line : contents.split("\r?\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) { continue; }
            int eq = trimmed.indexOf('=');
            if (eq <= 0) { continue; }
            String key = trimmed.substring(0, eq).trim();
            String value = trimmed.substring(eq + 1);
            for (String mapped : langKeys(key, pack)) { out.put(mapped, value); }
        }
        JsonObject json = new JsonObject();
        for (Map.Entry<String, String> entry : out.entrySet()) { json.addProperty(entry.getKey(), entry.getValue()); }
        return Ported.GSON.toJson(json);
    }

    private static List<String> langKeys(String key, Ported pack) {
        Matcher m = LANG_TILE.matcher(key);
        if (m.matches()) {
            String namespace = m.group(2);
            String file = m.group(3);
            String variant = m.group(4);
            String suffix = "locked".equals(m.group(5)) ? ".locked" : "";
            boolean tile = "tile".equals(m.group(1));
            String name = variant != null ? pack.renamedIn(namespace, tile ? "blocks" : "items", file).getOrDefault(variant, variant) : tile ? pack.mainVariantOfBlockFile(namespace, file) : pack.mainVariantOfItemFile(namespace, file);
            if (name == null) { name = file.substring(file.lastIndexOf('/') + 1); }
            List<String> keys = new ArrayList<>();
            keys.add((tile ? "block." : "item.") + namespace + "." + name + suffix);
            if (!tile && pack.ownBlock(namespace + ":" + name, 0) != null) { keys.add("block." + namespace + "." + name + suffix); }
            pack.rewrote();
            return keys;
        }
        m = LANG_ENTITY.matcher(key);
        if (m.matches() && pack.ownsNamespace(m.group(1))) {
            pack.rewrote();
            return List.of("entity." + m.group(1) + "." + m.group(2));
        }
        m = LANG_FLUID.matcher(key);
        if (m.matches()) {
            String namespace = m.group(1) != null ? m.group(1) : pack.mainNamespace();
            pack.rewrote();
            return List.of("fluid." + namespace + "." + m.group(2), "fluid_type." + namespace + "." + m.group(2));
        }
        m = LANG_POTION_TYPE.matcher(key);
        if (m.matches()) {
            pack.rewrote();
            return List.of("item.minecraft." + m.group(1) + ".effect." + m.group(2));
        }
        m = LANG_TAB.matcher(key);
        if (m.matches()) {
            pack.rewrote();
            return List.of("itemGroup." + pack.mainNamespace() + "." + m.group(1));
        }
        return List.of(key);
    }

    public static String texturePath(String path) {
        String namespace = path.indexOf(':') < 0 ? "minecraft" : path.substring(0, path.indexOf(':'));
        String rest = path.indexOf(':') < 0 ? path : path.substring(path.indexOf(':') + 1);
        if (rest.startsWith("blocks/")) { rest = "block/" + rest.substring("blocks/".length()); }
        else if (rest.startsWith("items/")) { rest = "item/" + rest.substring("items/".length()); }
        else if (rest.startsWith("textures/blocks/")) { rest = "textures/block/" + rest.substring("textures/blocks/".length()); }
        else if (rest.startsWith("textures/items/")) { rest = "textures/item/" + rest.substring("textures/items/".length()); }
        else { return path; }
        return namespace + ":" + rest;
    }

    public static String model(JsonObject json, Ported pack) {
        if (json.has("textures") && json.get("textures").isJsonObject()) {
            JsonObject textures = json.getAsJsonObject("textures");
            for (Map.Entry<String, JsonElement> texture : new ArrayList<>(textures.entrySet())) {
                if (!texture.getValue().isJsonPrimitive()) { continue; }
                String held = texture.getValue().getAsString();
                if (held.startsWith("#")) { continue; }
                String mapped = texturePath(held);
                if (!mapped.equals(held)) {
                    textures.addProperty(texture.getKey(), mapped);
                    pack.rewrote();
                }
            }
        }
        if (json.has("parent") && json.get("parent").isJsonPrimitive()) {
            String parent = json.get("parent").getAsString();
            String bare = parent.startsWith("minecraft:") ? parent.substring("minecraft:".length()) : parent;
            String renamed = RENAMED_MODELS.get(bare.startsWith("block/") ? bare.substring("block/".length()) : bare);
            if (renamed != null && (bare.startsWith("block/") || bare.indexOf('/') < 0)) {
                json.addProperty("parent", "minecraft:block/" + renamed);
                pack.rewrote();
            }
            else if (parent.indexOf(':') < 0 && !parent.startsWith("block/") && !parent.startsWith("item/") && !parent.startsWith("builtin/")) {
                json.addProperty("parent", "minecraft:block/" + parent);
                pack.rewrote();
            }
        }
        return Ported.GSON.toJson(json);
    }

    public static String pixelMap(JsonObject json, Ported pack) {
        if (json.has("extends") && json.get("extends").isJsonPrimitive()) {
            String held = json.get("extends").getAsString();
            String mapped = texturePath(held);
            if (!mapped.equals(held)) {
                json.addProperty("extends", mapped);
                pack.rewrote();
            }
        }
        return Ported.GSON.toJson(json);
    }
}
