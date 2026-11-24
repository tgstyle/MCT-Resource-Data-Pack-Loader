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
import net.minecraft.util.math.MathHelper;

final class ContentParserSeasons {
    private static final List<String> CLOCK_KEYS = Collections.unmodifiableList(Arrays.asList("dimension", "message"));
    private static final List<String> SENSOR_KEYS = Collections.unmodifiableList(Arrays.asList("dimension", "output"));

    private ContentParserSeasons() {}

    @Nullable static SeasonClockDef clock(JsonObject json) {
        JsonObject asked = Json.objectOrFlag(json, "seasonClock");
        if (asked == null) { return null; }
        Json.unknownKeys(asked, "seasonClock", CLOCK_KEYS);
        return new SeasonClockDef(Json.location(asked, "dimension"), Json.getBoolean(asked, "message", true));
    }

    @Nullable static SeasonSensorDef sensor(JsonObject json) {
        JsonObject held = Json.objectOrNull(json, "seasonSensor");
        if (held == null) { return null; }
        Json.unknownKeys(held, "seasonSensor", SENSOR_KEYS);
        Map<String, Integer> output = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : Json.object(held, "output").entrySet()) {
            output.put(entry.getKey(), MathHelper.clamp(Json.asInt(entry.getValue(), "output." + entry.getKey(), 0), 1, 15));
        }
        return new SeasonSensorDef(Json.location(held, "dimension"), Collections.unmodifiableMap(output));
    }
}
