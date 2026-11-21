package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.SpawnShieldDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.WeatherShieldDef;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

final class ContentParserShields {
    private static final int DEFAULT_RADIUS = 32;
    private static final int MAX_RADIUS = 128;
    private static final List<String> MOBS = List.of("hostile", "passive", "all");
    private static final List<String> SPAWN_KEYS = List.of("mobs", "entities", "radius");
    private static final List<String> WEATHER_KEYS = List.of("radius");

    private ContentParserShields() {}

    @Nullable static SpawnShieldDef spawns(JsonObject json, ResourceLocation key) {
        if (!json.has("blocksSpawns") || !json.get("blocksSpawns").isJsonObject()) { return null; }
        JsonObject held = Json.getJsonObject(json, "blocksSpawns");
        Json.unknownKeys(held, "blocksSpawns", SPAWN_KEYS);
        String mobs = Json.choice(held, "mobs", "hostile", key, MOBS);
        List<ResourceLocation> entities = new ArrayList<>();
        List<ResourceLocation> tags = new ArrayList<>();
        for (String named : Json.strings(held, "entities")) {
            String trimmed = named.trim();
            boolean tag = trimmed.startsWith("#");
            ResourceLocation id = ResourceLocation.tryParse(tag ? trimmed.substring(1) : trimmed);
            if (id == null) { ContentLog.LOGGER.error("Block {} blocksSpawns lists '{}', which is not a valid id, so it is skipped", key, named); }
            else { (tag ? tags : entities).add(id); }
        }
        return new SpawnShieldDef(radius(held), SpawnShieldDef.Mobs.valueOf(mobs.toUpperCase(Locale.ROOT)), Collections.unmodifiableList(entities), Collections.unmodifiableList(tags));
    }

    @Nullable static WeatherShieldDef weather(JsonObject json) {
        if (!json.has("blocksWeather") || !json.get("blocksWeather").isJsonObject()) { return null; }
        JsonObject held = Json.getJsonObject(json, "blocksWeather");
        Json.unknownKeys(held, "blocksWeather", WEATHER_KEYS);
        return new WeatherShieldDef(radius(held));
    }

    private static int radius(JsonObject held) { return Mth.clamp(Json.getInt(held, "radius", DEFAULT_RADIUS), 1, MAX_RADIUS); }
}
