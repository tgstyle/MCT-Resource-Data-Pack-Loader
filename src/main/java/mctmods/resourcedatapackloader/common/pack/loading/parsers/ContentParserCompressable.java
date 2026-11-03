package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.CompressableDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;

public final class ContentParserCompressable {
    public static final int MAX_LAYERS = 20;
    public static final String MEMBER = "compressable";

    private ContentParserCompressable() {}

    public static boolean standalone(JsonObject json) { return json.has(MEMBER) && !json.has("variants"); }

    @Nullable public static CompressableDef parse(Identifier key, JsonObject json) {
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
        boolean variants = json.has("variants");
        if (!of.isEmpty() && variants) {
            ContentLog.LOGGER.error("The 'compressable' member of {} names 'of' but the file also has 'variants'. 'of' is only for a standalone file with no block of its own, skipping its compressed blocks", key);
            return null;
        }
        if (of.isEmpty() && !variants) {
            ContentLog.LOGGER.error("The 'compressable' member of {} has no 'of' and the file has no 'variants', so there is nothing to compress, skipping its compressed blocks", key);
            return null;
        }
        Identifier block = null;
        if (!of.isEmpty()) {
            block = block(of);
            if (block == null) {
                ContentLog.LOGGER.error("The 'of' value '{}' in {} is not 'id', skipping its compressed blocks", of, key);
                return null;
            }
        }
        String texture = Json.getString(held, "texture", "").trim();
        return new CompressableDef(key, block, layers, texture.isEmpty() ? null : texture, Json.getBoolean(held, "recipes", true));
    }

    @Nullable private static Identifier block(String of) {
        String[] parts = of.split(":", -1);
        return parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty() ? Identifier.tryParse(of) : null;
    }
}
