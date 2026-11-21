package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SpawnShieldDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WeatherShieldDef;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

final class ContentParserShields {
    private static final int DEFAULT_RADIUS = 32;
    private static final int MAX_RADIUS = 128;
    private static final List<String> SPAWN_KEYS = Collections.unmodifiableList(Arrays.asList("mobs", "entities", "radius"));
    private static final List<String> WEATHER_KEYS = Collections.singletonList("radius");

    private ContentParserShields() {}

    @Nullable static SpawnShieldDef spawns(JsonObject json, ResourceLocation key) {
        if (!json.has("blocksSpawns") || !json.get("blocksSpawns").isJsonObject()) { return null; }
        JsonObject held = Json.getJsonObject(json, "blocksSpawns");
        Json.unknownKeys(held, "blocksSpawns", SPAWN_KEYS);
        String mobs = Json.choice(held, "mobs", "hostile", key, Arrays.asList("hostile", "passive", "all"));
        List<ResourceLocation> entities = new ArrayList<>();
        for (String named : Json.strings(held, "entities")) {
            entities.add(new ResourceLocation(named.trim()));
        }
        return new SpawnShieldDef(radius(held), SpawnShieldDef.Mobs.valueOf(mobs.toUpperCase(Locale.ROOT)), Collections.unmodifiableList(entities));
    }

    @Nullable static WeatherShieldDef weather(JsonObject json) {
        if (!json.has("blocksWeather") || !json.get("blocksWeather").isJsonObject()) { return null; }
        JsonObject held = Json.getJsonObject(json, "blocksWeather");
        Json.unknownKeys(held, "blocksWeather", WEATHER_KEYS);
        return new WeatherShieldDef(radius(held));
    }

    private static int radius(JsonObject held) { return MathHelper.clamp(Json.getInt(held, "radius", DEFAULT_RADIUS), 1, MAX_RADIUS); }
}
