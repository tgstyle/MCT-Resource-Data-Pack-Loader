package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.items.LocatorDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

final class ContentParserLocator {
    private ContentParserLocator() {}

    static LocatorDef parse(ResourceLocation key, JsonObject json) {
        String place = Json.getString(json, "locates", "").trim();
        if (place.startsWith("#")) {
            ContentLog.LOGGER.error("Item {} locates '{}', a tag, which 1.12.2 has no structure tags for, so locates is ignored", key, place);
            place = "";
        }
        return new LocatorDef(place, Json.getBoolean(json, "locatesEye", false), Json.getString(json, "locatesSound", "").trim(), Math.max(0, Json.getInt(json, "locatesRange", 0)));
    }
}
