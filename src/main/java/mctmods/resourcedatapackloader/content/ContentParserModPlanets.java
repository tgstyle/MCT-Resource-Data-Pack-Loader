package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.ExtraPlanetsDef;
import mctmods.resourcedatapackloader.content.def.GalaxySpaceDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public final class ContentParserModPlanets {
    public static final String GALAXY_SPACE = "galaxyspace";
    public static final String EXTRA_PLANETS = "extraplanets";
    private static final List<String> CLASSES = Collections.unmodifiableList(Arrays.asList("SELENA", "DESERT", "TERRA", "OCEANIDE", "GASGIANT", "ICEGIANT", "ASTEROID", "TITAN", "ICEWORLD"));
    private static final List<String> WEATHER = Collections.unmodifiableList(Arrays.asList("DUST_STORM", "FROZEN_STORM", "LIGHTNING_STORM", "METEORIC_RAIN"));
    private static final List<String> STAR_TYPES = Collections.unmodifiableList(Arrays.asList("SUBDWARF", "DWARF", "SUBGIANT", "GIANT", "SUPERGIANT", "HYPERGIANT", "BLACKHOLE"));
    private static final List<String> STAR_COLORS = Collections.unmodifiableList(Arrays.asList("BROWN", "RED", "ORANGE", "YELLOW", "WHITE", "LIGHTBLUE", "BLUE", "M1", "M2", "M3", "K1", "K2", "K3", "G1", "G2", "G3", "F1", "F2", "F3", "A1", "A2", "A3", "B1", "B2", "B3", "O1", "O2", "O3"));
    private static final List<String> LANDERS = Collections.unmodifiableList(Arrays.asList("general", "jupiter", "saturn", "mercury", "neptune", "uranus"));

    private ContentParserModPlanets() {}

    @Nullable public static GalaxySpaceDef galaxySpace(ResourceLocation key, JsonObject body) {
        if (!body.has(GALAXY_SPACE)) { return null; }
        JsonObject json = JsonUtils.getJsonObject(body, GALAXY_SPACE);
        float[] eccentricity = pair(key, json, "orbitEccentricity");
        float[] offset = pair(key, json, "orbitOffset");
        float[] zone = pair(key, json, "habitableZone");
        return new GalaxySpaceDef(Math.max(0.0F, JsonUtils.getFloat(json, "pressure", 0.0F)),
                JsonUtils.getBoolean(json, "radiation", false),
                named(key, json, "class", CLASSES),
                eccentricity[0], eccentricity[1], offset[0], offset[1],
                JsonUtils.getBoolean(json, "freezeBlocks", true),
                Math.max(0.0F, JsonUtils.getFloat(json, "thermalVariation", 0.0F)),
                JsonUtils.getFloat(json, "solarWind", -1.0F),
                named(key, json, "weather", WEATHER),
                Math.max(0.0F, JsonUtils.getFloat(json, "weatherFrequency", 1.0F)),
                named(key, json, "starType", STAR_TYPES),
                named(key, json, "starColor", STAR_COLORS),
                zone[0], zone[1]);
    }

    @Nullable public static ExtraPlanetsDef extraPlanets(ResourceLocation key, JsonObject galacticraft) {
        if (!galacticraft.has(EXTRA_PLANETS)) { return null; }
        JsonObject json = JsonUtils.getJsonObject(galacticraft, EXTRA_PLANETS);
        float day = JsonUtils.getFloat(json, "temperatureDay", Float.NaN);
        String lander = JsonUtils.getString(json, "lander", "").trim().toLowerCase(Locale.ROOT);
        if (!lander.isEmpty() && !LANDERS.contains(lander)) {
            ContentLog.LOGGER.error("Dimension {} asks for ExtraPlanets lander '{}', which is not one of {}, using Galacticraft's lander", key, lander, LANDERS);
            lander = "";
        }
        return new ExtraPlanetsDef(level(json, "pressure"), level(json, "radiation"), day,
                JsonUtils.getFloat(json, "temperatureNight", day),
                lander.isEmpty() ? null : lander);
    }

    private static int level(JsonObject json, String member) { return json.has(member) ? MathHelper.clamp(JsonUtils.getInt(json, member), 0, 100) : -1; }

    @Nullable private static String named(ResourceLocation key, JsonObject json, String member, List<String> options) {
        if (!json.has(member)) { return null; }
        String asked = JsonUtils.getString(json, member).trim().toUpperCase(Locale.ROOT);
        if (options.contains(asked)) { return asked; }
        ContentLog.LOGGER.error("Celestial body {} sets GalaxySpace {} '{}', which is not one of {}, ignoring it", key, member, asked, options);
        return null;
    }

    private static float[] pair(ResourceLocation key, JsonObject json, String member) {
        if (!json.has(member)) { return new float[2]; }
        JsonArray values = JsonUtils.getJsonArray(json, member);
        if (values.size() != 2) {
            ContentLog.LOGGER.error("Celestial body {} sets GalaxySpace {} to {}, which needs exactly two numbers, ignoring it", key, member, values);
            return new float[2];
        }
        return new float[] {values.get(0).getAsFloat(), values.get(1).getAsFloat()};
    }
}
