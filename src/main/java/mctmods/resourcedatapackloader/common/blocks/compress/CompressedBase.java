package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.client.util.ContentPixelImages;
import mctmods.resourcedatapackloader.client.util.ContentResourceText;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("deprecation") public final class CompressedBase {
    private static final int HOPS = 8;
    private static final Map<CompressedSet, int[][]> FOUND = new ConcurrentHashMap<>();
    private static final Map<CompressedSet, Boolean> FAILED = new ConcurrentHashMap<>();

    private CompressedBase() {}

    public static void forget() {
        FOUND.clear();
        FAILED.clear();
    }

    public static boolean failed(CompressedSet set) { return FAILED.containsKey(set); }

    @Nullable public static int[][] image(CompressedSet set) {
        int[][] held = FOUND.get(set);
        if (held != null) { return held; }
        if (FAILED.containsKey(set)) { return null; }
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) { return null; }
        int[][] read = read(set);
        if (read == null) { FAILED.put(set, Boolean.TRUE); }
        else { FOUND.put(set, read); }
        return read;
    }

    @Nullable private static int[][] read(CompressedSet set) {
        ResourceLocation texture = texture(set);
        if (texture == null) { return null; }
        int[][] image = ContentPixelImages.read(texture.getNamespace(), "textures/" + texture.getPath() + ".png");
        if (image == null) { ContentLog.LOGGER.error("The compressable {} builds on the texture {}, which no pack or the game provides, so its layers are drawn grey", set.def.key, texture); }
        return image;
    }

    @Nullable private static ResourceLocation texture(CompressedSet set) {
        String override = set.def.texture;
        if (override != null) { return override.indexOf(':') < 0 ? new ResourceLocation(set.def.key.getNamespace(), override) : new ResourceLocation(override); }
        ResourceLocation name = set.source.baseBlock;
        Block block = Registries.find(ForgeRegistries.BLOCKS, name);
        if (block == null) { return missing(set, "its block is not registered"); }
        JsonObject state = ContentResourceText.json(name.getNamespace(), "blockstates/" + name.getPath() + ".json");
        if (state == null) { return missing(set, "the blockstate " + name.getNamespace() + ":blockstates/" + name.getPath() + ".json cannot be read"); }
        JsonObject entry = entry(state, key(block.getStateFromMeta(set.source.baseMeta)));
        if (entry == null) { return missing(set, "its blockstate names no model for meta " + set.source.baseMeta); }
        Map<String, String> textures = new HashMap<>();
        JsonObject defaults = state.has("defaults") && state.get("defaults").isJsonObject() ? state.getAsJsonObject("defaults") : new JsonObject();
        String model = text(entry, "model");
        if (model == null) { model = text(defaults, "model"); }
        merge(textures, defaults);
        merge(textures, entry);
        if (model == null) { return missing(set, "its blockstate names no model"); }
        ResourceLocation at = new ResourceLocation(model);
        String namespace = at.getNamespace();
        String path = at.getPath().indexOf('/') < 0 ? "block/" + at.getPath() : at.getPath();
        for (int hop = 0; hop < HOPS; hop++) {
            JsonObject json = ContentResourceText.json(namespace, "models/" + path + ".json");
            if (json == null) { break; }
            merge(textures, json);
            String parent = text(json, "parent");
            if (parent == null) { break; }
            ResourceLocation next = new ResourceLocation(parent);
            namespace = next.getNamespace();
            path = next.getPath();
        }
        String value = resolved(textures, "all");
        if (value == null) { value = resolved(textures, "particle"); }
        if (value == null) { return missing(set, "its model has no 'all' or 'particle' texture"); }
        return new ResourceLocation(value);
    }

    @Nullable private static ResourceLocation missing(CompressedSet set, String why) {
        ContentLog.LOGGER.error("The compressable {} cannot find a texture to build on for {}: {}. Give the file a \"texture\", so its layers are drawn grey until then", set.def.key, set.source.baseBlock, why);
        return null;
    }

    @Nullable private static String resolved(Map<String, String> textures, String name) {
        String value = textures.get(name);
        for (int hop = 0; hop < HOPS && value != null && value.startsWith("#"); hop++) { value = textures.get(value.substring(1)); }
        return value == null || value.startsWith("#") ? null : value;
    }

    private static void merge(Map<String, String> textures, JsonObject json) {
        if (!json.has("textures") || !json.get("textures").isJsonObject()) { return; }
        for (Map.Entry<String, JsonElement> one : json.getAsJsonObject("textures").entrySet()) {
            if (one.getValue().isJsonPrimitive()) { textures.putIfAbsent(one.getKey(), one.getValue().getAsString()); }
        }
    }

    @Nullable private static String text(JsonObject json, String member) { return json.has(member) && json.get(member).isJsonPrimitive() ? json.get(member).getAsString() : null; }

    private static String key(IBlockState state) {
        List<String> parts = new ArrayList<>();
        for (Map.Entry<IProperty<?>, Comparable<?>> property : state.getProperties().entrySet()) { parts.add(property.getKey().getName() + "=" + name(property.getKey(), property.getValue())); }
        parts.sort(null);
        return String.join(",", parts);
    }

    @SuppressWarnings("unchecked") private static <T extends Comparable<T>> String name(IProperty<T> property, Comparable<?> value) { return property.getName((T) value); }

    @Nullable private static JsonObject entry(JsonObject state, String key) {
        if (state.has("variants") && state.get("variants").isJsonObject()) {
            JsonObject variants = state.getAsJsonObject("variants");
            JsonObject found = object(variants.get(key));
            if (found == null) { found = object(variants.get("normal")); }
            if (found == null) {
                for (Map.Entry<String, JsonElement> one : variants.entrySet()) {
                    found = object(one.getValue());
                    if (found != null) { break; }
                }
            }
            return found;
        }
        if (state.has("multipart") && state.get("multipart").isJsonArray()) {
            for (JsonElement part : state.getAsJsonArray("multipart")) {
                JsonObject apply = part.isJsonObject() ? object(part.getAsJsonObject().get("apply")) : null;
                if (apply != null) { return apply; }
            }
        }
        return null;
    }

    @Nullable private static JsonObject object(@Nullable JsonElement element) {
        if (element == null) { return null; }
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            return array.size() == 0 ? null : object(array.get(0));
        }
        return element.isJsonObject() ? element.getAsJsonObject() : null;
    }
}
