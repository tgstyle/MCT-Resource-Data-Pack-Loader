package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SeasonSensorDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.SeasonClockDef;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

final class ContentParserSeasons {
    private static final List<String> CLOCK_KEYS = Collections.unmodifiableList(Arrays.asList("dimension", "message"));
    private static final List<String> SENSOR_KEYS = Collections.unmodifiableList(Arrays.asList("dimension", "output"));

    private ContentParserSeasons() {}

    @Nullable static SeasonClockDef clock(JsonObject json) {
        JsonElement asked = json.get("seasonClock");
        if (asked == null) { return null; }
        if (asked.isJsonObject()) {
            JsonObject held = asked.getAsJsonObject();
            Json.unknownKeys(held, "seasonClock", CLOCK_KEYS);
            return new SeasonClockDef(dimension(held), Json.getBoolean(held, "message", true));
        }
        return asked.isJsonPrimitive() && asked.getAsJsonPrimitive().isBoolean() && asked.getAsBoolean() ? new SeasonClockDef(null, true) : null;
    }

    @Nullable static SeasonSensorDef sensor(JsonObject json) {
        if (!json.has("seasonSensor") || !json.get("seasonSensor").isJsonObject()) { return null; }
        JsonObject held = Json.getJsonObject(json, "seasonSensor");
        Json.unknownKeys(held, "seasonSensor", SENSOR_KEYS);
        Map<String, Integer> output = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : Json.object(held, "output").entrySet()) {
            output.put(entry.getKey(), MathHelper.clamp(Json.asInt(entry.getValue(), "output." + entry.getKey(), 0), 1, 15));
        }
        return new SeasonSensorDef(dimension(held), Collections.unmodifiableMap(output));
    }

    @Nullable private static ResourceLocation dimension(JsonObject json) {
        String named = Json.getString(json, "dimension", "").trim();
        return named.isEmpty() ? null : new ResourceLocation(named);
    }
}
