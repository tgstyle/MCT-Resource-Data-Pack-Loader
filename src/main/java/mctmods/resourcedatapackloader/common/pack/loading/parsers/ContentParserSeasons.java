package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SeasonSensorDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.SeasonClockDef;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

final class ContentParserSeasons {
    private ContentParserSeasons() {}

    @Nullable static SeasonClockDef clock(JsonObject json) {
        JsonElement asked = json.get("seasonClock");
        if (asked == null) { return null; }
        if (asked.isJsonObject()) { return new SeasonClockDef(dimension(asked.getAsJsonObject()), Json.getBoolean(asked.getAsJsonObject(), "message", true)); }
        return asked.isJsonPrimitive() && asked.getAsJsonPrimitive().isBoolean() && asked.getAsBoolean() ? new SeasonClockDef(null, true) : null;
    }

    @Nullable static SeasonSensorDef sensor(JsonObject json) {
        if (!GsonHelper.isObjectNode(json, "seasonSensor")) { return null; }
        JsonObject held = Json.getJsonObject(json, "seasonSensor");
        Map<String, Integer> output = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : Json.object(held, "output").entrySet()) {
            output.put(entry.getKey(), Mth.clamp(Json.asInt(entry.getValue(), "output." + entry.getKey(), 0), 1, 15));
        }
        return new SeasonSensorDef(dimension(held), Map.copyOf(output));
    }

    @Nullable private static ResourceLocation dimension(JsonObject json) { return ContentParser.location(Json.getString(json, "dimension", "")); }
}
