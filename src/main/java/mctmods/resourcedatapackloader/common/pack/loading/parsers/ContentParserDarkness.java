package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.DarknessDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

final class ContentParserDarkness {
    private static final String DARKNESS = "darkness";
    private static final String MOON_LIGHT = "moonLight";
    private static final List<String> MODES = List.of(DarknessDef.FULL, DarknessDef.BLOCK, DarknessDef.MOON);
    private static final List<String> DARKNESS_KEYS = List.of("mode", MOON_LIGHT, "grayNight", "endGlow", "gamma");
    private static final float[] MOON_DEFAULT = {0.0F, 0.075F, 0.15F, 0.225F, 0.3F};

    private ContentParserDarkness() {}

    @Nullable static DarknessDef darkness(ResourceLocation key, JsonObject sky) {
        if (!sky.has(DARKNESS)) { return null; }
        JsonObject darkness = Json.getJsonObject(sky, DARKNESS, new JsonObject());
        Json.unknownKeys(darkness, DARKNESS, DARKNESS_KEYS);
        String owner = "Dimension " + key + " sky.darkness";
        String mode = Json.choice(darkness, "mode", DarknessDef.FULL, owner, MODES);
        return new DarknessDef(mode, moonLight(owner, darkness), Json.getBoolean(darkness, "grayNight", false), Json.getBoolean(darkness, "endGlow", true), !sky.has("ambientLight"), Json.bounded(darkness, "gamma", 0.0F, 1.0F, owner));
    }

    private static float[] moonLight(String owner, JsonObject darkness) {
        if (!darkness.has(MOON_LIGHT)) { return MOON_DEFAULT.clone(); }
        JsonArray list = Json.getJsonArray(darkness, MOON_LIGHT, new JsonArray());
        if (list.size() != DarknessDef.PHASES) {
            ContentLog.LOGGER.error("{} gives {} values for {}, not {}, so the default is used instead", owner, list.size(), MOON_LIGHT, DarknessDef.PHASES);
            return MOON_DEFAULT.clone();
        }
        float[] light = new float[DarknessDef.PHASES];
        for (int phase = 0; phase < light.length; phase++) { light[phase] = Mth.clamp(Json.asFloat(list.get(phase), MOON_LIGHT, 0.0F), 0.0F, 1.0F); }
        return light;
    }
}
