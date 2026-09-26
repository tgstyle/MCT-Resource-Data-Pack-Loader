package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.CelestialDef;
import mctmods.resourcedatapackloader.content.def.GalacticraftDef;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.util.ContentLog;
import static mctmods.resourcedatapackloader.util.Json.strings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public final class ContentParserCelestial {
    private static final List<String> GASES = Collections.unmodifiableList(Arrays.asList("NITROGEN", "OXYGEN", "CO2", "WATER", "METHANE", "HYDROGEN", "HELIUM", "ARGON"));
    private static final String RING = "#19E599";
    private static final String ICONS = "galacticraftcore:textures/gui/celestialbodies/";
    private static final String RAIN_PARTICLE = "droplet";
    private static final String RAIN_SOUND = "minecraft:weather.rain";

    private ContentParserCelestial() {}

    @Nullable public static CelestialDef celestial(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(ContentParser.GSON, contents, JsonObject.class);
        if (json == null) { return null; }
        String kind = JsonUtils.getString(json, "kind", "").trim().toLowerCase(Locale.ROOT);
        if (CelestialDef.SYSTEM.equals(kind)) { return system(key, json); }
        if (!CelestialDef.PLANET.equals(kind) && !CelestialDef.MOON.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is of kind '{}', which is none of {}, {} or {}, so it is left off the map", key, kind, CelestialDef.SYSTEM, CelestialDef.PLANET, CelestialDef.MOON);
            return null;
        }
        return body(key, json, kind, key.getPath(), 0, strings(json, "requires"));
    }

    @Nullable public static GalacticraftDef galacticraft(ResourceLocation key, JsonObject dimension) {
        if (!dimension.has("galacticraft")) { return null; }
        JsonObject json = JsonUtils.getJsonObject(dimension, "galacticraft");
        String kind = JsonUtils.getString(json, "kind", CelestialDef.PLANET).trim().toLowerCase(Locale.ROOT);
        if (!CelestialDef.PLANET.equals(kind) && !CelestialDef.MOON.equals(kind)) {
            ContentLog.LOGGER.error("Dimension {} asks to be a Galacticraft '{}', which is not {} or {}, making it a {}", key, kind, CelestialDef.PLANET, CelestialDef.MOON, CelestialDef.PLANET);
            kind = CelestialDef.PLANET;
        }
        CelestialDef body = body(key, json, kind, key.getPath(), 1, Collections.emptyList());
        if (body == null) { return null; }
        String landing = choice(key, json, "landing", GalacticraftDef.LANDER, GalacticraftDef.PARACHUTE, GalacticraftDef.BALLOONS);
        String arrival = choice(key, json, "arrival", GalacticraftDef.DEPARTURE, GalacticraftDef.SPAWN);
        String gui = JsonUtils.getString(json, "rocketGui", "").trim();
        JsonObject air = JsonUtils.getJsonObject(json, "atmosphere", new JsonObject());
        Boolean breathable = air.has("breathable") ? JsonUtils.getBoolean(air, "breathable") : null;
        List<String> gases = gases(key, air);
        JsonObject dungeon = JsonUtils.getJsonObject(json, "dungeon", new JsonObject());
        String chest = JsonUtils.getString(dungeon, "chest", "").trim();
        return new GalacticraftDef(body,
                JsonUtils.getBoolean(json, "reachable", true),
                JsonUtils.getInt(json, "minTier", body.tier),
                landing,
                JsonUtils.getFloat(json, "landingHeight", -1.0F),
                arrival,
                JsonUtils.getFloat(json, "exitHeight", -1.0F),
                gui.isEmpty() ? null : new ResourceLocation(gui),
                strings(json, "checklist"),
                breathable,
                gases,
                JsonUtils.getBoolean(air, "corrosive", false),
                JsonUtils.getFloat(air, "temperature", 0.0F),
                Math.max(0.0F, JsonUtils.getFloat(air, "wind", gases.isEmpty() ? 0.0F : 1.0F)),
                Math.max(0.0F, JsonUtils.getFloat(air, "density", 1.0F)),
                JsonUtils.getFloat(json, "meteorFrequency", -1.0F),
                Math.max(0.0F, JsonUtils.getFloat(json, "fuelMultiplier", 1.0F)),
                JsonUtils.getFloat(json, "soundReduction", -1.0F),
                Math.max(0.0F, JsonUtils.getFloat(json, "solarEnergy", 1.0F)),
                JsonUtils.getBoolean(json, "netherPortals", false),
                Math.max(0, JsonUtils.getInt(dungeon, "spacing", 0)),
                chest.isEmpty() ? null : new ResourceLocation(chest),
                json.has("rain") ? rain(key, JsonUtils.getJsonObject(json, "rain")) : null,
                ContentParserModPlanets.extraPlanets(key, json));
    }

    private static GalacticraftDef.Rain rain(ResourceLocation key, JsonObject json) {
        String particle = JsonUtils.getString(json, "particle", RAIN_PARTICLE).trim().toLowerCase(Locale.ROOT);
        if (EnumParticleTypes.getByName(particle) == null) {
            ContentLog.LOGGER.error("Dimension {} names rain particle '{}', which is no particle Minecraft knows, using {}", key, particle, RAIN_PARTICLE);
            particle = RAIN_PARTICLE;
        }
        return new GalacticraftDef.Rain(particle,
                new ResourceLocation(JsonUtils.getString(json, "sound", RAIN_SOUND).trim()),
                Math.max(0.0F, JsonUtils.getFloat(json, "volume", 0.2F)),
                Math.max(0, JsonUtils.getInt(json, "interval", 3)));
    }

    @Nullable private static CelestialDef system(ResourceLocation key, JsonObject json) {
        String name = JsonUtils.getString(json, "name", key.getPath()).trim();
        if (!json.has("mapPosition") || !json.get("mapPosition").isJsonArray()) {
            ContentLog.LOGGER.error("Star system {} has no mapPosition, so there is nowhere on the galaxy map to put it", key);
            return null;
        }
        JsonArray at = json.getAsJsonArray("mapPosition");
        if (at.size() < 2) {
            ContentLog.LOGGER.error("Star system {} gives a mapPosition of {}, which needs at least x and y", key, at);
            return null;
        }
        JsonObject starJson = JsonUtils.getJsonObject(json, "star", new JsonObject());
        CelestialDef star = body(key, starJson, CelestialDef.STAR, name, -1, Collections.emptyList());
        if (star == null) { return null; }
        return new CelestialDef(key, CelestialDef.SYSTEM, name, "", star.icon, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0, -1,
                JsonUtils.getString(json, "galaxy", "milky_way").trim(),
                at.get(0).getAsFloat(), at.get(1).getAsFloat(), at.size() > 2 ? at.get(2).getAsFloat() : 0.0F,
                star, strings(json, "requires"), null);
    }

    @Nullable private static CelestialDef body(ResourceLocation key, JsonObject json, String kind, String fallbackName, int tier, List<String> requires) {
        String name = JsonUtils.getString(json, "name", fallbackName).trim();
        if (name.isEmpty()) {
            ContentLog.LOGGER.error("Celestial body {} has an empty name, so it is left off the map", key);
            return null;
        }
        boolean moon = CelestialDef.MOON.equals(kind);
        String parent = JsonUtils.getString(json, "parent", moon ? "" : "sol").trim();
        if (parent.isEmpty() && !CelestialDef.STAR.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is a {} with no parent, so it has nothing to circle", key, kind);
            return null;
        }
        float distance = JsonUtils.getFloat(json, "distance", moon ? 13.0F : 1.0F);
        return new CelestialDef(key, kind, name, parent,
                new ResourceLocation(JsonUtils.getString(json, "icon", ICONS + icon(kind) + ".png").trim()),
                JsonUtils.getFloat(json, "relativeSize", moon ? 0.2667F : 1.0F),
                distance,
                JsonUtils.getFloat(json, "scaledDistance", distance),
                JsonUtils.getFloat(json, "orbitTime", moon ? 100.0F : 1.0F),
                JsonUtils.getFloat(json, "phaseShift", 0.0F),
                ContentTypes.color(JsonUtils.getString(json, "ringColor", RING), key + " ringColor"),
                CelestialDef.STAR.equals(kind) ? -1 : JsonUtils.getInt(json, "tier", tier),
                "", 0.0F, 0.0F, 0.0F, null, requires, ContentParserModPlanets.galaxySpace(key, json));
    }

    private static String icon(String kind) {
        switch (kind) {
            case CelestialDef.STAR: return "sun";
            case CelestialDef.MOON: return "moon";
            default: return "mars";
        }
    }

    private static String choice(ResourceLocation key, JsonObject json, String member, String... options) {
        String asked = JsonUtils.getString(json, member, options[0]).trim().toLowerCase(Locale.ROOT);
        if (Arrays.asList(options).contains(asked)) { return asked; }
        ContentLog.LOGGER.error("Dimension {} asks for a Galacticraft {} of '{}', which is not one of {}, using {}", key, member, asked, Arrays.toString(options), options[0]);
        return options[0];
    }

    private static List<String> gases(ResourceLocation key, JsonObject air) {
        List<String> found = new ArrayList<>();
        for (String gas : strings(air, "gases")) {
            String name = gas.trim().toUpperCase(Locale.ROOT);
            if (GASES.contains(name)) { found.add(name); }
            else { ContentLog.LOGGER.error("Dimension {} names gas '{}', which is not one of {}, ignoring it", key, gas, GASES); }
        }
        return Collections.unmodifiableList(found);
    }
}
