package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.TallDef;
import mctmods.resourcedatapackloader.common.types.ContentBlockTypes;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class ContentParserCrops {
    private static final List<String> SOURCES = Collections.unmodifiableList(Arrays.asList(TallDef.FROM_BASE, TallDef.FROM_ALL));

    private static final List<String> AGE_KEYS = Collections.unmodifiableList(Arrays.asList("rise", "regrow", "produceFrom"));
    private static final List<String> TALL_KEYS = Collections.unmodifiableList(Arrays.asList("parts", "rise", "regrow", "produceFrom"));

    private ContentParserCrops() {}

    @Nullable static TallDef tall(JsonObject json, ResourceLocation key, int maxAge) {
        JsonObject held = Json.objectOrNull(json, "tall");
        if (held == null) { return null; }
        String type = Json.getString(json, "type", ContentBlockTypes.DEFAULT);
        boolean flower = "flower".equals(type);
        if (!flower && !"crop".equals(type)) {
            ContentLog.LOGGER.warn("Block {} has a 'tall' section but its type is not 'crop' or 'flower', ignoring it", key);
            return null;
        }
        Json.unknownKeys(held, "tall", TALL_KEYS);
        int parts = Json.clamped(held, "parts", TallDef.DEFAULT_PARTS, TallDef.MIN_PARTS, TallDef.MAX_PARTS);
        if (flower) {
            for (String ignored : Json.present(held, AGE_KEYS)) { ContentLog.LOGGER.warn("Block {} is a flower, so its tall '{}' is ignored", key, ignored); }
            return new TallDef(parts, TallDef.defaultRise(parts, maxAge), TallDef.FROM_BASE, false);
        }
        return new TallDef(parts, rise(held, key, parts, maxAge), Json.choice(held, "produceFrom", TallDef.FROM_BASE, key, SOURCES), Json.getBoolean(held, "regrow", false));
    }

    private static int[] rise(JsonObject held, ResourceLocation key, int parts, int maxAge) {
        int[] fallback = TallDef.defaultRise(parts, maxAge);
        if (!held.has("rise")) { return fallback; }
        JsonElement element = held.get("rise");
        if (!element.isJsonArray()) {
            ContentLog.LOGGER.error("Block {} has a tall 'rise' that is not a list, using {}", key, Arrays.toString(fallback));
            return fallback;
        }
        JsonArray array = element.getAsJsonArray();
        List<Integer> values = Json.ints(array, "rise");
        if (values.size() != parts - 1 || array.size() != values.size()) {
            ContentLog.LOGGER.error("Block {} has a tall 'rise' of {} entries, but {} parts need {}, using {}", key, array.size(), parts, parts - 1, Arrays.toString(fallback));
            return fallback;
        }
        int[] rise = new int[values.size()];
        int previous = 0;
        for (int index = 0; index < rise.length; index++) {
            int age = values.get(index);
            if (age < 1 || age > maxAge || age <= previous) {
                ContentLog.LOGGER.error("Block {} has a tall 'rise' {} that is not strictly increasing within 1 to {}, using {}", key, array, maxAge, Arrays.toString(fallback));
                return fallback;
            }
            rise[index] = age;
            previous = age;
        }
        return rise;
    }
}
