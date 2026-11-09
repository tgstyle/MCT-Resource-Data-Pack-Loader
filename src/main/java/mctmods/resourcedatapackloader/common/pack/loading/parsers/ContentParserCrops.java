package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TallDef;
import mctmods.resourcedatapackloader.common.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class ContentParserCrops {
    private static final String MEMBER = "tall";
    private static final List<String> SOURCES = List.of(TallDef.FROM_BASE, TallDef.FROM_ALL);

    private static final List<String> AGE_KEYS = List.of("rise", "regrow", "produceFrom");

    private ContentParserCrops() {}

    @Nullable public static TallDef tall(JsonObject json, ResourceLocation key, int maxAge) {
        if (!json.has(MEMBER) || !json.get(MEMBER).isJsonObject()) { return null; }
        String type = Settings.lower(Json.getString(json, "type", ContentParser.DEFAULT_TYPE));
        boolean flower = ContentBlockTypes.FLOWER.equals(type);
        if (!flower && !ContentBlockTypes.CROP.equals(type)) {
            ContentLog.LOGGER.warn("Block {} has a 'tall' section but its type is not 'crop' or 'flower', ignoring it", key);
            return null;
        }
        JsonObject held = Json.getJsonObject(json, MEMBER);
        int parts = Mth.clamp(Json.getInt(held, "parts", TallDef.DEFAULT_PARTS), TallDef.MIN_PARTS, TallDef.MAX_PARTS);
        if (flower) {
            for (String ignored : AGE_KEYS) {
                if (held.has(ignored)) { ContentLog.LOGGER.warn("Block {} is a flower, so its tall '{}' is ignored", key, ignored); }
            }
            return new TallDef(parts, TallDef.defaultRise(parts, maxAge), TallDef.FROM_BASE, false);
        }
        return new TallDef(parts, rise(held, key, parts, maxAge), Json.choice(held, "produceFrom", TallDef.FROM_BASE, key, SOURCES), Json.getBoolean(held, "regrow", false));
    }

    private static int[] rise(JsonObject held, ResourceLocation key, int parts, int maxAge) {
        int[] fallback = TallDef.defaultRise(parts, maxAge);
        if (!held.has("rise")) { return fallback; }
        if (!held.get("rise").isJsonArray()) {
            ContentLog.LOGGER.error("Block {} has a tall 'rise' that is not a list, using {}", key, Arrays.toString(fallback));
            return fallback;
        }
        JsonArray list = Json.getJsonArray(held, "rise");
        List<Integer> values = Json.ints(list, "rise");
        if (values.size() != parts - 1 || list.size() != values.size()) {
            ContentLog.LOGGER.error("Block {} has a tall 'rise' of {} entries, but {} parts need {}, using {}", key, list.size(), parts, parts - 1, Arrays.toString(fallback));
            return fallback;
        }
        int[] rise = new int[values.size()];
        int previous = 0;
        for (int index = 0; index < rise.length; index++) {
            int age = values.get(index);
            if (age < 1 || age > maxAge || age <= previous) {
                ContentLog.LOGGER.error("Block {} has a tall 'rise' {} that is not strictly increasing within 1 to {}, using {}", key, list, maxAge, Arrays.toString(fallback));
                return fallback;
            }
            rise[index] = age;
            previous = age;
        }
        return rise;
    }
}
