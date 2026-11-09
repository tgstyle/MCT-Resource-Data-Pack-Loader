package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.client.util.ContentPixelImages;
import mctmods.resourcedatapackloader.client.util.ContentResourceText;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;

public final class CompressedBase {
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
        if (FMLEnvironment.dist != Dist.CLIENT) { return null; }
        int[][] read = read(set);
        if (read == null) { FAILED.put(set, Boolean.TRUE); }
        else { FOUND.put(set, read); }
        return read;
    }

    @Nullable private static int[][] read(CompressedSet set) {
        ResourceLocation texture = texture(set);
        if (texture == null) { return null; }
        int[][] image = ContentPixelImages.read(texture.getNamespace(), "textures/" + texture.getPath() + ".png");
        if (image == null) { ContentLog.LOGGER.error("The compressable {} builds on the texture {}, which no pack or the game provides, so its layers are drawn grey", set.def.key(), texture); }
        return image == null || !set.source.fromItem ? image : opaque(image);
    }

    private static int[][] opaque(int[][] image) {
        int side = image[0][0];
        int[] pixels = image[1];
        int count = Math.min(pixels.length, side * side);
        long red = 0;
        long green = 0;
        long blue = 0;
        long seen = 0;
        for (int index = 0; index < count; index++) {
            int color = pixels[index];
            if ((color >>> 24) == 0) { continue; }
            red += (color >> 16) & 0xFF;
            green += (color >> 8) & 0xFF;
            blue += color & 0xFF;
            seen++;
        }
        int fill = seen == 0 ? 0 : (int) (red / seen / 2) << 16 | (int) (green / seen / 2) << 8 | (int) (blue / seen / 2);
        int[] result = new int[pixels.length];
        for (int index = 0; index < pixels.length; index++) { result[index] = (pixels[index] >>> 24) == 0 ? 0xFF000000 | fill : pixels[index] | 0xFF000000; }
        return new int[][] { image[0], result };
    }

    @Nullable private static ResourceLocation itemTexture(CompressedSet set) {
        ResourceLocation name = set.source.base;
        Map<String, String> textures = new HashMap<>();
        follow(textures, name.getNamespace(), "item/" + name.getPath());
        String value = resolved(textures, "layer0");
        if (value == null) { value = resolved(textures, "particle"); }
        if (value == null) { return missing(set, "its item model has no 'layer0' or 'particle' texture"); }
        return located(set, value);
    }

    private static void follow(Map<String, String> textures, String namespace, String path) {
        String at = namespace;
        String model = path;
        for (int hop = 0; hop < HOPS; hop++) {
            JsonObject json = ContentResourceText.json(at, "models/" + model + ".json");
            if (json == null) { break; }
            merge(textures, json);
            String parent = text(json, "parent");
            ResourceLocation next = parent == null ? null : ResourceLocation.tryParse(parent);
            if (next == null) { break; }
            at = next.getNamespace();
            model = next.getPath();
        }
    }

    @Nullable private static ResourceLocation texture(CompressedSet set) {
        String override = set.def.texture();
        if (override != null) { return located(set, override.indexOf(':') < 0 ? set.def.key().getNamespace() + ":" + override : override); }
        if (set.source.fromItem) { return itemTexture(set); }
        ResourceLocation name = set.source.base;
        Block block = Registered.find(ForgeRegistries.BLOCKS, name);
        if (block == null) { return missing(set, "its block is not registered"); }
        JsonObject state = ContentResourceText.json(name.getNamespace(), "blockstates/" + name.getPath() + ".json");
        if (state == null) { return missing(set, "the blockstate " + name.getNamespace() + ":blockstates/" + name.getPath() + ".json cannot be read"); }
        JsonObject entry = entry(state, key(block.defaultBlockState()));
        if (entry == null) { return missing(set, "its blockstate names no model for the default state"); }
        Map<String, String> textures = new HashMap<>();
        String model = text(entry, "model");
        merge(textures, entry);
        if (model == null) { return missing(set, "its blockstate names no model"); }
        ResourceLocation at = ResourceLocation.tryParse(model);
        if (at == null) { return missing(set, "its model name '" + model + "' is not valid"); }
        String namespace = at.getNamespace();
        String path = at.getPath().indexOf('/') < 0 ? "block/" + at.getPath() : at.getPath();
        follow(textures, namespace, path);
        String value = resolved(textures, "all");
        if (value == null) { value = resolved(textures, "particle"); }
        if (value == null) { return missing(set, "its model has no 'all' or 'particle' texture"); }
        return located(set, value);
    }

    @Nullable private static ResourceLocation located(CompressedSet set, String name) {
        ResourceLocation found = ResourceLocation.tryParse(name);
        return found == null ? missing(set, "the texture name '" + name + "' is not valid") : found;
    }

    @Nullable private static ResourceLocation missing(CompressedSet set, String why) {
        ContentLog.LOGGER.error("The compressable {} cannot find a texture to build on for {}: {}. Give the file a \"texture\", so its layers are drawn grey until then", set.def.key(), set.source.base, why);
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

    private static String key(BlockState state) {
        List<String> parts = new ArrayList<>();
        for (Map.Entry<Property<?>, Comparable<?>> property : state.getValues().entrySet()) { parts.add(property.getKey().getName() + "=" + name(property.getKey(), property.getValue())); }
        parts.sort(null);
        return String.join(",", parts);
    }

    @SuppressWarnings("unchecked") private static <T extends Comparable<T>> String name(Property<T> property, Comparable<?> value) { return property.getName((T) value); }

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
            return array.isEmpty() ? null : object(array.get(0));
        }
        return element.isJsonObject() ? element.getAsJsonObject() : null;
    }
}
