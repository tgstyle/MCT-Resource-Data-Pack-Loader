package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.generated.ContentPixelMaps;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.types.PropertyVariant;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class CompressedAssets {
    public static final String MODEL_FOLDER = "models/block/";
    private static final String TEXTURE_DIR = "blocks/";
    private static final String TEXTURE_FOLDER = "textures/" + TEXTURE_DIR;
    private static final String JSON = ".json";
    private static final Map<String, byte[]> MADE = new ConcurrentHashMap<>();
    private static volatile Map<String, Asset> index = new HashMap<>();
    private static volatile int indexed;

    static String texturePath(CompressedSet set, int layer) { return TEXTURE_FOLDER + set.assetName(layer) + ContentPixelMaps.PNG; }

    public static String texture(String path) { return "textures/" + path + ContentPixelMaps.PNG; }

    private CompressedAssets() {}

    public static void forget() {
        MADE.clear();
        index = new HashMap<>();
        indexed = 0;
        CompressedBase.forget();
    }

    public static boolean has(String namespace, String path) { return find(namespace, path) != null; }

    public static List<String> paths() { List<String> out = new ArrayList<>(); for (String key : table().keySet()) { int colon = key.indexOf(':'); out.add("assets/" + key.substring(0, colon) + "/" + key.substring(colon + 1)); } return out; }

    @Nullable public static byte[] open(String namespace, String path) {
        Asset asset = find(namespace, path);
        if (asset == null) { return null; }
        String key = namespace + ":" + path;
        return MADE.computeIfAbsent(key, k -> asset.kind == Kind.TEXTURE ? CompressedTexture.draw(asset.set, asset.layer) : json(asset).getBytes(StandardCharsets.UTF_8));
    }

    @Nullable private static Asset find(String namespace, String path) {
        if (!path.startsWith(PackManager.STATE_FOLDER) && !path.startsWith(MODEL_FOLDER) && !path.startsWith(TEXTURE_FOLDER)) { return null; }
        return table().get(namespace + ":" + path);
    }

    private static Map<String, Asset> table() {
        int count = ContentCompressables.sets().size();
        if (count == 0) { return index; }
        if (count != indexed) {
            Map<String, Asset> built = new HashMap<>();
            for (CompressedSet set : ContentCompressables.sets()) {
                String namespace = set.def.key.getNamespace() + ":";
                for (int layer = set.low(); layer <= set.layers; layer++) {
                    built.put(namespace + MODEL_FOLDER + set.assetName(layer) + JSON, new Asset(set, Kind.MODEL, layer));
                    built.put(namespace + texturePath(set, layer), new Asset(set, Kind.TEXTURE, layer));
                    if (set.meta(layer) == 0) { built.put(namespace + PackManager.STATE_FOLDER + set.blockName(layer).getPath() + JSON, new Asset(set, Kind.STATE, layer)); }
                }
            }
            index = built;
            indexed = count;
        }
        return index;
    }

    private static String json(Asset asset) {
        CompressedSet set = asset.set;
        String namespace = set.def.key.getNamespace();
        JsonObject root = new JsonObject();
        if (asset.kind == Kind.MODEL) {
            root.addProperty(CompressedKeys.PARENT, "block/cube_all");
            JsonObject textures = new JsonObject();
            textures.addProperty(CompressedKeys.ALL, namespace + ":" + TEXTURE_DIR + set.assetName(asset.layer));
            root.add(CompressedKeys.TEXTURES, textures);
            return Json.GSON.toJson(root);
        }
        JsonObject variants = new JsonObject();
        int last = set.last(set.group(asset.layer));
        for (int layer = asset.layer; layer <= last; layer++) {
            JsonObject model = new JsonObject();
            model.addProperty(CompressedKeys.MODEL, namespace + ":" + set.assetName(layer));
            variants.add(PropertyVariant.NAME + "=" + set.variantName(layer), model);
        }
        root.add(CompressedKeys.VARIANTS, variants);
        return Json.GSON.toJson(root);
    }

    private enum Kind { STATE, MODEL, TEXTURE }

    private static final class Asset {
        final CompressedSet set;
        final Kind kind;
        final int layer;

        Asset(CompressedSet set, Kind kind, int layer) {
            this.set = set;
            this.kind = kind;
            this.layer = layer;
        }
    }
}
