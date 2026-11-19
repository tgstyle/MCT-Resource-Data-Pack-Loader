package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.items.LocatorDef;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;

final class ContentParserLocator {
    private ContentParserLocator() {}

    static LocatorDef parse(JsonObject json) {
        return new LocatorDef(Json.getString(json, "locates", "").trim(), Json.getBoolean(json, "locatesEye", false), Json.getString(json, "locatesSound", "").trim(), Math.max(0, Json.getInt(json, "locatesRange", 0)));
    }
}
