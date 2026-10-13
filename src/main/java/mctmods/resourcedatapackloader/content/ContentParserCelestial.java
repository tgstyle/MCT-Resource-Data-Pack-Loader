package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.def.CelestialDef;
import mctmods.resourcedatapackloader.content.def.GalacticraftDef;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.util.BodyRings;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;
import static mctmods.resourcedatapackloader.util.Json.strings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public final class ContentParserCelestial {
    private static final List<String> GASES = Collections.unmodifiableList(Arrays.asList("NITROGEN", "OXYGEN", "CO2", "WATER", "METHANE", "HYDROGEN", "HELIUM", "ARGON"));
    private static final List<String> KINDS = Collections.unmodifiableList(Arrays.asList(CelestialDef.PLANET, CelestialDef.MOON, CelestialDef.ASTEROIDS, CelestialDef.STATION));
    private static final List<String> LANDINGS = Collections.unmodifiableList(Arrays.asList(GalacticraftDef.LANDER, GalacticraftDef.PARACHUTE, GalacticraftDef.BALLOONS));
    private static final List<String> ARRIVALS = Collections.unmodifiableList(Arrays.asList(GalacticraftDef.DEPARTURE, GalacticraftDef.SPAWN));
    private static final Pattern GALAXY = Pattern.compile("[a-z0-9_]+");
    private static final String RING = "#19E599";
    private static final String ICONS = "galacticraftcore:textures/gui/celestialbodies/";

    private ContentParserCelestial() {}

    @Nullable public static CelestialDef celestial(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(Json.GSON, contents, JsonObject.class);
        if (json == null) { return null; }
        String kind = Settings.lower(JsonUtils.getString(json, "kind", ""));
        if (CelestialDef.SYSTEM.equals(kind)) { return system(key, json); }
        if (!CelestialDef.PLANET.equals(kind) && !CelestialDef.SUBSYSTEM.equals(kind) && !CelestialDef.MOON.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is of kind '{}', which is none of {}, {}, {} or {}, so it is left off the map", key, kind, CelestialDef.SYSTEM, CelestialDef.PLANET, CelestialDef.SUBSYSTEM, CelestialDef.MOON);
            return null;
        }
        return body(key, json, kind, key.getPath(), 0, strings(json, "requires"));
    }

    @Nullable public static GalacticraftDef galacticraft(ResourceLocation key, JsonObject dimension) {
        if (!dimension.has("galacticraft")) { return null; }
        JsonObject json = JsonUtils.getJsonObject(dimension, "galacticraft");
        String owner = "Dimension " + key + " Galacticraft";
        String kind = Json.choice(json, "kind", CelestialDef.PLANET, owner, KINDS);
        CelestialDef body = body(key, json, kind, key.getPath(), CelestialDef.STATION.equals(kind) ? -1 : 1, Collections.emptyList());
        if (body == null) { return null; }
        String landing = Json.choice(json, "landing", GalacticraftDef.LANDER, owner, LANDINGS);
        String arrival = Json.choice(json, "arrival", GalacticraftDef.DEPARTURE, owner, ARRIVALS);
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
                ContentParserModPlanets.extraPlanets(key, json),
                recipe(key, json),
                JsonUtils.getBoolean(json, "showName", false));
    }

    private static Map<String, Integer> recipe(ResourceLocation key, JsonObject json) {
        Map<String, Integer> found = new LinkedHashMap<>();
        if (!json.has("recipe")) { return found; }
        for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "recipe").entrySet()) {
            JsonElement count = entry.getValue();
            if (!count.isJsonPrimitive() || !count.getAsJsonPrimitive().isNumber() || count.getAsInt() < 1) {
                ContentLog.LOGGER.error("Dimension {} asks for {} of '{}' in its station recipe, which is not a count of 1 or more, ignoring it", key, count, entry.getKey());
                continue;
            }
            found.put(entry.getKey().trim(), count.getAsInt());
        }
        return Collections.unmodifiableMap(found);
    }

    @Nullable private static CelestialDef system(ResourceLocation key, JsonObject json) {
        String name = JsonUtils.getString(json, "name", key.getPath()).trim();
        if (!JsonUtils.isJsonArray(json, "mapPosition")) {
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
        String galaxy = JsonUtils.getString(json, "galaxy", "milky_way").trim().toLowerCase(Locale.ROOT);
        if (!GALAXY.matcher(galaxy).matches()) {
            ContentLog.LOGGER.error("Star system {} names galaxy '{}', which may hold only the letters a to z, digits and underscores, so it is left off the map", key, galaxy);
            return null;
        }
        String image = JsonUtils.getString(json, "galaxyImage", "").trim();
        return new CelestialDef(key, CelestialDef.SYSTEM, name, "", star.icon, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0, null, 0.0F, 0.0F, -1,
                galaxy, image.isEmpty() ? null : new ResourceLocation(image),
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
        boolean station = CelestialDef.STATION.equals(kind);
        String parent = JsonUtils.getString(json, "parent", moon || station ? "" : "sol").trim();
        if (parent.isEmpty() && !CelestialDef.STAR.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is a {} with no parent, so it has nothing to circle", key, kind);
            return null;
        }
        float distance = JsonUtils.getFloat(json, "distance", station ? 9.0F : moon ? 13.0F : 1.0F);
        String ringPath = JsonUtils.getString(json, "ring", "").trim();
        ResourceLocation ring = ringPath.isEmpty() ? null : new ResourceLocation(ringPath);
        if (ring != null && !moon && !CelestialDef.PLANET.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is a {} and sets a ring, which only a planet or moon can have, ignoring it", key, kind);
            ring = null;
        }
        float beltWidth = Math.max(0.0F, JsonUtils.getFloat(json, "beltWidth", 0.0F));
        if (beltWidth > 0.0F && !CelestialDef.ASTEROIDS.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is a {} and sets a beltWidth, which only an asteroid belt can have, ignoring it", key, kind);
            beltWidth = 0.0F;
        }
        return new CelestialDef(key, kind, name, parent,
                new ResourceLocation(JsonUtils.getString(json, "icon", ICONS + icon(kind) + ".png").trim()),
                JsonUtils.getFloat(json, "relativeSize", moon || station ? 0.2667F : 1.0F),
                distance,
                JsonUtils.getFloat(json, "scaledDistance", distance),
                JsonUtils.getFloat(json, "orbitTime", station ? 20.0F : moon ? 100.0F : 1.0F),
                JsonUtils.getFloat(json, "phaseShift", station ? Float.NaN : 0.0F),
                ContentTypes.color(JsonUtils.getString(json, "ringColor", RING), key + " ringColor"),
                ring,
                Math.max(0.1F, JsonUtils.getFloat(json, "ringSize", BodyRings.DEFAULT_SIZE)),
                beltWidth,
                CelestialDef.STAR.equals(kind) || CelestialDef.SUBSYSTEM.equals(kind) ? -1 : JsonUtils.getInt(json, "tier", tier),
                "", null, 0.0F, 0.0F, 0.0F, null, requires, ContentParserModPlanets.galaxySpace(key, json));
    }

    private static String icon(String kind) {
        switch (kind) {
            case CelestialDef.STAR:
            case CelestialDef.SUBSYSTEM: return "sun";
            case CelestialDef.MOON: return "moon";
            case CelestialDef.ASTEROIDS: return "asteroid";
            case CelestialDef.STATION: return "space_station";
            default: return "mars";
        }
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
