package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.CompressableDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.AbstractMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class ContentParserCompressable {
    public static final int MAX_LAYERS = 20;
    public static final String MEMBER = "compressable";
    private static final int MAX_BLOCK_META = 15;
    private static final int MAX_ITEM_META = 32766;

    private ContentParserCompressable() {}

    public static boolean standalone(JsonObject json) { return json.has(MEMBER) && !json.has("variants"); }

    @Nullable public static CompressableDef parse(ResourceLocation key, JsonObject json) {
        if (!json.has(MEMBER)) { return null; }
        if (!json.get(MEMBER).isJsonObject()) {
            ContentLog.LOGGER.error("The 'compressable' member of {} is not an object, skipping its compressed blocks", key);
            return null;
        }
        JsonObject held = Json.getJsonObject(json, MEMBER);
        int layers = Json.getInt(held, "layers", 0);
        if (layers < 1 || layers > MAX_LAYERS) {
            ContentLog.LOGGER.error("The 'compressable' member of {} needs 'layers' between 1 and {}, not {}, skipping its compressed blocks", key, MAX_LAYERS, layers);
            return null;
        }
        String of = Json.getString(held, "of", "").trim();
        String item = Json.getString(held, "item", "").trim();
        boolean variants = json.has("variants");
        if (!of.isEmpty() && !item.isEmpty()) {
            ContentLog.LOGGER.error("The 'compressable' member of {} names both 'of' and 'item', skipping its compressed blocks", key);
            return null;
        }
        if (!of.isEmpty() && variants) {
            ContentLog.LOGGER.error("The 'compressable' member of {} names 'of' but the file also has 'variants'. 'of' is only for a standalone file with no block of its own, skipping its compressed blocks", key);
            return null;
        }
        if (!item.isEmpty() && variants) {
            ContentLog.LOGGER.error("The 'compressable' member of {} names 'item' but the file also has 'variants'. 'item' is only for a standalone file with no block of its own, skipping its compressed blocks", key);
            return null;
        }
        if (of.isEmpty() && item.isEmpty() && !variants) {
            ContentLog.LOGGER.error("The 'compressable' member of {} has no 'of' or 'item' and the file has no 'variants', so there is nothing to compress, skipping its compressed blocks", key);
            return null;
        }
        Map.Entry<ResourceLocation, Integer> block = null;
        Map.Entry<ResourceLocation, Integer> stack = null;
        if (!of.isEmpty()) {
            block = split("of", of, MAX_BLOCK_META, key);
            if (block == null) { return null; }
        }
        if (!item.isEmpty()) {
            stack = split("item", item, MAX_ITEM_META, key);
            if (stack == null) { return null; }
        }
        String texture = Json.getString(held, "texture", "").trim();
        return new CompressableDef(key, block == null ? null : block.getKey(), block == null ? 0 : block.getValue(), stack == null ? null : stack.getKey(), stack == null ? 0 : stack.getValue(), layers, texture.isEmpty() ? null : texture, Json.getBoolean(held, "recipes", true));
    }

    @Nullable private static Map.Entry<ResourceLocation, Integer> split(String member, String text, int maxMeta, ResourceLocation key) {
        String[] parts = text.split(":", -1);
        if (parts.length < 2 || parts.length > 3 || parts[0].isEmpty() || parts[1].isEmpty()) {
            ContentLog.LOGGER.error("The '{}' value '{}' in {} is not 'id' or 'id:meta', skipping its compressed blocks", member, text, key);
            return null;
        }
        int meta = 0;
        if (parts.length == 3) {
            try { meta = Integer.parseInt(parts[2]); }
            catch (NumberFormatException ex) { meta = -1; }
            if (meta < 0 || meta > maxMeta) {
                ContentLog.LOGGER.error("The '{}' value '{}' in {} has a meta outside 0-{}, skipping its compressed blocks", member, text, key, maxMeta);
                return null;
            }
        }
        return new AbstractMap.SimpleImmutableEntry<>(new ResourceLocation(parts[0], parts[1]), meta);
    }
}
