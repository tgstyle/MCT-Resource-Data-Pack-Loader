package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.block.ContentFluids;
import mctmods.resourcedatapackloader.content.def.ItemDef;
import mctmods.resourcedatapackloader.content.def.MaterialDef;
import mctmods.resourcedatapackloader.content.item.ContentPotionItem;
import mctmods.resourcedatapackloader.content.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.content.types.ContentItemTypes;
import mctmods.resourcedatapackloader.content.util.ContentMaterials;
import mctmods.resourcedatapackloader.content.util.ContentTints;
import mctmods.resourcedatapackloader.pack.GeneratedResources;
import mctmods.resourcedatapackloader.pack.PackManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.ARGB;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

final class ContentGeneratedItems {
    static final Identifier BANNER_RENDERER = Identifier.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "banner");
    private static final int POTION_DEFAULT = -13083194;
    private static final double BANNER_SCALE = 0.6666667;
    private static final String[][] ARMOR_LAYERS = {{"humanoid", "_layer_1"}, {"humanoid_leggings", "_layer_2"}};
    private static final String BABY_LAYER = "humanoid_baby";
    private static final String LAYER_TEXTURE = "layer";
    private ContentGeneratedItems() {}

    static void item(ContentRegistry.ItemEntry entry) {
        JsonObject model = model(entry.id());
        if (entry.item() instanceof ContentPotionItem) { model.add("tints", ContentGenerated.arr(ContentGenerated.obj("type", "minecraft:potion", "default", POTION_DEFAULT))); }
        definition(entry.id(), model);
    }

    static void blockItem(ContentRegistry.BlockEntry entry) {
        Identifier id = entry.id();
        if (ContentBlockTypes.BANNER.equals(entry.def().type())) {
            JsonObject transformation = ContentGenerated.obj("left_rotation", ContentGenerated.arr(0.0, 0.0, 0.0, 1.0), "right_rotation", ContentGenerated.arr(0.0, 0.0, 0.0, 1.0),
                    "scale", ContentGenerated.arr(BANNER_SCALE, -BANNER_SCALE, -BANNER_SCALE), "translation", ContentGenerated.arr(0.5, 0.0, 0.5));
            definition(id, ContentGenerated.obj("type", "minecraft:special", "base", itemModel(id), "model", ContentGenerated.obj("type", BANNER_RENDERER.toString(), "block", id.toString()), "transformation", transformation));
            return;
        }
        JsonObject model = model(id);
        String tint = ContentTints.mode(entry.def().tint());
        int fixed = tint.isEmpty() ? -1 : ContentTints.fixed(tint, id);
        if (fixed >= 0) {
            JsonArray tints = new JsonArray();
            JsonObject constant = ContentGenerated.obj("type", "minecraft:constant", "value", ARGB.opaque(fixed));
            for (int index = tintLayers(Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath())); index > 0; index--) { tints.add(constant.deepCopy()); }
            model.add("tints", tints);
        }
        definition(id, model);
    }

    private static int tintLayers(Identifier start) {
        int layers = 1;
        Set<Identifier> seen = new HashSet<>();
        Identifier at = start;
        while (at != null && seen.add(at)) {
            JsonObject json = modelJson(at);
            if (json == null) { break; }
            layers = Math.max(layers, tintLayers(json));
            at = json.has("parent") && json.get("parent").isJsonPrimitive() ? Identifier.tryParse(json.get("parent").getAsString()) : null;
        }
        return layers;
    }

    private static int tintLayers(JsonObject json) {
        int layers = 0;
        if (json.has("elements") && json.get("elements").isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("elements")) {
                if (!element.isJsonObject() || !element.getAsJsonObject().has("faces") || !element.getAsJsonObject().get("faces").isJsonObject()) { continue; }
                for (Map.Entry<String, JsonElement> face : element.getAsJsonObject().getAsJsonObject("faces").entrySet()) {
                    if (!face.getValue().isJsonObject()) { continue; }
                    JsonElement index = face.getValue().getAsJsonObject().get("tintindex");
                    if (index != null && index.isJsonPrimitive() && index.getAsJsonPrimitive().isNumber()) { layers = Math.max(layers, index.getAsInt() + 1); }
                }
            }
        }
        if (json.has("textures") && json.get("textures").isJsonObject()) {
            for (String key : json.getAsJsonObject("textures").keySet()) {
                if (key.startsWith(LAYER_TEXTURE) && key.length() > LAYER_TEXTURE.length() && key.substring(LAYER_TEXTURE.length()).chars().allMatch(Character::isDigit)) {
                    layers = Math.max(layers, Integer.parseInt(key.substring(LAYER_TEXTURE.length())) + 1);
                }
            }
        }
        return layers;
    }

    static int blockTintLayers(Identifier block) {
        JsonObject states = assetJson(block.getNamespace(), "blockstates/" + block.getPath() + ".json");
        if (states == null) { return tintLayers(block.withPrefix("block/")); }
        Set<Identifier> models = new HashSet<>();
        stateModels(states, models);
        int layers = 1;
        for (Identifier model : models) { layers = Math.max(layers, tintLayers(model)); }
        return layers;
    }

    private static void stateModels(JsonElement json, Set<Identifier> models) {
        if (json.isJsonArray()) {
            for (JsonElement one : json.getAsJsonArray()) { stateModels(one, models); }
            return;
        }
        if (!json.isJsonObject()) { return; }
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
            if ("model".equals(entry.getKey()) && entry.getValue().isJsonPrimitive()) {
                Identifier model = Identifier.tryParse(entry.getValue().getAsString());
                if (model != null) { models.add(model); }
            }
            else { stateModels(entry.getValue(), models); }
        }
    }

    @Nullable private static JsonObject modelJson(Identifier model) { return assetJson(model.getNamespace(), "models/" + model.getPath() + ".json"); }

    @Nullable private static JsonObject assetJson(String namespace, String path) {
        byte[] bytes = PackManager.get().bytes(PackType.CLIENT_RESOURCES, namespace, path);
        if (bytes == null) { bytes = GeneratedResources.get(PackType.CLIENT_RESOURCES, namespace, path); }
        if (bytes == null) { return null; }
        try {
            JsonElement json = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8));
            return json.isJsonObject() ? json.getAsJsonObject() : null;
        }
        catch (RuntimeException ex) { return null; }
    }

    static void bucket(ContentFluids.Made made) {
        Identifier id = made.bucketId();
        if (ContentGenerated.provided(PackType.CLIENT_RESOURCES, id.getNamespace(), "models/item/" + id.getPath() + ".json")) {
            JsonObject model = model(id);
            model.add("tints", ContentGenerated.arr(ContentGenerated.obj("type", "minecraft:constant", "value", -1), ContentGenerated.obj("type", "neoforge:fluid_contents_tint")));
            definition(id, model);
            return;
        }
        definition(id, ContentGenerated.obj("type", "neoforge:fluid_container", "textures", ContentGenerated.obj("base", "minecraft:item/bucket", "fluid", "neoforge:item/mask/bucket_fluid"), "fluid", made.def.id().toString()));
    }

    static void equipment() {
        Set<Identifier> assets = new LinkedHashSet<>();
        for (ContentRegistry.ItemEntry entry : ContentRegistry.items()) {
            ItemDef def = entry.def();
            if (def == null || !ContentItemTypes.ARMOR.equals(def.type())) { continue; }
            MaterialDef material = ContentRegistry.material(def.material(), entry.id());
            if (material != null && ContentMaterials.customArmor(material)) { assets.add(ContentMaterials.armor(material).assetId().identifier()); }
        }
        for (Identifier asset : assets) { equipment(asset); }
    }

    private static void equipment(Identifier asset) {
        String namespace = asset.getNamespace();
        String path = "equipment/" + asset.getPath() + ".json";
        if (ContentGenerated.provided(PackType.CLIENT_RESOURCES, namespace, path)) { return; }
        JsonObject layers = new JsonObject();
        for (String[] layer : ARMOR_LAYERS) {
            String texture = equipmentTexture(layer[0], asset);
            boolean found = ContentGenerated.provided(PackType.CLIENT_RESOURCES, namespace, texture) || served(namespace, "textures/models/armor/" + asset.getPath() + layer[1] + ".png", texture);
            if (found || !Identifier.DEFAULT_NAMESPACE.equals(namespace)) { layers.add(layer[0], layer(asset)); }
        }
        String baby = equipmentTexture(BABY_LAYER, asset);
        boolean shipped = ContentGenerated.provided(PackType.CLIENT_RESOURCES, namespace, baby);
        if (!shipped && !layers.isEmpty()) {
            String outer = equipmentTexture(ARMOR_LAYERS[0][0], asset);
            String inner = equipmentTexture(ARMOR_LAYERS[1][0], asset);
            GeneratedResources.putLater(PackType.CLIENT_RESOURCES, namespace, baby, () -> ContentBabyArmor.draw(asset.toString(), worn(namespace, outer), worn(namespace, inner)));
        }
        if (shipped || !layers.isEmpty()) { layers.add(BABY_LAYER, layer(asset)); }
        if (!layers.isEmpty()) { ContentGenerated.asset(namespace, path, ContentGenerated.obj("layers", layers)); }
    }

    private static String equipmentTexture(String layer, Identifier asset) { return "textures/entity/equipment/" + layer + "/" + asset.getPath() + ".png"; }

    private static JsonArray layer(Identifier asset) { return ContentGenerated.arr(ContentGenerated.obj("texture", asset.toString())); }

    @Nullable private static byte[] worn(String namespace, String texture) {
        byte[] bytes = PackManager.get().bytes(PackType.CLIENT_RESOURCES, namespace, texture);
        return bytes != null ? bytes : GeneratedResources.get(PackType.CLIENT_RESOURCES, namespace, texture);
    }

    private static boolean served(String namespace, String from, String to) {
        if (!ContentGenerated.provided(PackType.CLIENT_RESOURCES, namespace, from)) { return false; }
        GeneratedResources.putLater(PackType.CLIENT_RESOURCES, namespace, to, () -> PackManager.get().bytes(PackType.CLIENT_RESOURCES, namespace, from));
        return true;
    }

    private static String itemModel(Identifier id) { return id.getNamespace() + ":item/" + id.getPath(); }

    private static JsonObject model(Identifier id) { return ContentGenerated.obj("type", "minecraft:model", "model", itemModel(id)); }

    private static void definition(Identifier id, JsonObject model) {
        String path = "items/" + id.getPath() + ".json";
        if (!ContentGenerated.provided(PackType.CLIENT_RESOURCES, id.getNamespace(), path)) { ContentGenerated.asset(id.getNamespace(), path, ContentGenerated.obj("model", model)); }
    }
}
