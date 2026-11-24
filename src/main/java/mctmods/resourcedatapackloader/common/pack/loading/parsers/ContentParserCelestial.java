package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.CelestialDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.GalacticraftDef;
import mctmods.resourcedatapackloader.common.types.ContentTypes;
import mctmods.resourcedatapackloader.common.util.dimension.BodyRings;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;
import static mctmods.resourcedatapackloader.core.util.json.Json.strings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public final class ContentParserCelestial {
    private static final List<String> GASES = Collections.unmodifiableList(Arrays.asList("NITROGEN", "OXYGEN", "CO2", "WATER", "METHANE", "HYDROGEN", "HELIUM", "ARGON"));
    private static final List<String> KINDS = Collections.unmodifiableList(Arrays.asList(CelestialDef.PLANET, CelestialDef.MOON, CelestialDef.ASTEROIDS, CelestialDef.STATION));
    private static final List<String> LANDINGS = Collections.unmodifiableList(Arrays.asList(GalacticraftDef.LANDER, GalacticraftDef.PARACHUTE, GalacticraftDef.BALLOONS));
    private static final List<String> ARRIVALS = Collections.unmodifiableList(Arrays.asList(GalacticraftDef.DEPARTURE, GalacticraftDef.SPAWN));
    private static final List<String> BODY_KEYS = Collections.unmodifiableList(Arrays.asList("name", "parent", "distance", "ring", "beltWidth", "icon", "relativeSize", "scaledDistance", "orbitTime", "phaseShift", "ringColor", "ringSize", "ringFront", "tier", "galaxyspace"));
    private static final List<String> CELESTIAL_KEYS = Collections.unmodifiableList(Arrays.asList("kind", "requires", "name", "parent", "distance", "ring", "beltWidth", "icon", "relativeSize", "scaledDistance", "orbitTime", "phaseShift", "ringColor", "ringSize", "ringFront", "tier", "galaxyspace"));
    private static final List<String> SYSTEM_KEYS = Collections.unmodifiableList(Arrays.asList("kind", "name", "mapPosition", "star", "galaxy", "galaxyImage", "requires"));
    private static final List<String> OVERRIDE_KEYS = Collections.unmodifiableList(Arrays.asList("kind", "body", "icon", "ring", "ringSize", "ringFront", "requires", "galaxyspace"));
    private static final List<String> GALACTICRAFT_KEYS = Collections.unmodifiableList(Arrays.asList("kind", "landing", "arrival", "rocketGui", "atmosphere", "dungeon", "reachable", "minTier", "landingHeight", "exitHeight", "checklist", "meteorFrequency", "fuelMultiplier", "soundReduction", "solarEnergy", "netherPortals", "extraplanets", "recipe", "showName", "name", "parent", "distance", "ring", "beltWidth", "icon", "relativeSize", "scaledDistance", "orbitTime", "phaseShift", "ringColor", "ringSize", "ringFront", "tier", "galaxyspace"));
    private static final List<String> ATMOSPHERE_KEYS = Collections.unmodifiableList(Arrays.asList("breathable", "gases", "corrosive", "temperature", "wind", "density"));
    private static final List<String> DUNGEON_KEYS = Collections.unmodifiableList(Arrays.asList("chest", "spacing"));
    private static final Pattern GALAXY = Pattern.compile("[a-z0-9_]+");
    private static final String RING = "#19E599";
    private static final String ICONS = "galacticraftcore:textures/gui/celestialbodies/";

    private ContentParserCelestial() {}

    @Nullable public static CelestialDef celestial(ResourceLocation key, String contents) {
        JsonObject json = Json.strictObject(contents);
        if (json == null) { return null; }
        String kind = Settings.lower(Json.getString(json, "kind", ""));
        if (CelestialDef.SYSTEM.equals(kind)) { return system(key, json); }
        if (CelestialDef.OVERRIDE.equals(kind)) { return override(key, json); }
        if (!CelestialDef.PLANET.equals(kind) && !CelestialDef.SUBSYSTEM.equals(kind) && !CelestialDef.MOON.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is of kind '{}', which is none of {}, {}, {}, {} or {}, so it is left off the map", key, kind, CelestialDef.SYSTEM, CelestialDef.PLANET, CelestialDef.SUBSYSTEM, CelestialDef.MOON, CelestialDef.OVERRIDE);
            return null;
        }
        Json.unknownKeys(json, "the top level", CELESTIAL_KEYS);
        return body(key, json, kind, key.getPath(), 0, strings(json, "requires"));
    }

    @Nullable public static GalacticraftDef galacticraft(ResourceLocation key, JsonObject dimension) {
        if (!dimension.has("galacticraft")) { return null; }
        JsonObject json = Json.getJsonObject(dimension, "galacticraft");
        Json.unknownKeys(json, "galacticraft", GALACTICRAFT_KEYS);
        String owner = "Dimension " + key + " Galacticraft";
        String kind = Json.choice(json, "kind", CelestialDef.PLANET, owner, KINDS);
        CelestialDef body = body(key, json, kind, key.getPath(), CelestialDef.STATION.equals(kind) ? -1 : 1, Collections.emptyList());
        if (body == null) { return null; }
        String landing = Json.choice(json, "landing", GalacticraftDef.LANDER, owner, LANDINGS);
        String arrival = Json.choice(json, "arrival", GalacticraftDef.DEPARTURE, owner, ARRIVALS);
        ResourceLocation gui = Json.location(json, "rocketGui");
        JsonObject air = Json.getJsonObject(json, "atmosphere", new JsonObject());
        Json.unknownKeys(air, "atmosphere", ATMOSPHERE_KEYS);
        Boolean breathable = Json.flagOrNull(air, "breathable");
        List<String> gases = gases(key, air);
        JsonObject dungeon = Json.getJsonObject(json, "dungeon", new JsonObject());
        Json.unknownKeys(dungeon, "dungeon", DUNGEON_KEYS);
        ResourceLocation chest = Json.location(dungeon, "chest");
        return new GalacticraftDef(body,
                Json.getBoolean(json, "reachable", true),
                Json.getInt(json, "minTier", body.tier),
                landing,
                Json.getFloat(json, "landingHeight", -1.0F),
                arrival,
                Json.getFloat(json, "exitHeight", -1.0F),
                gui,
                strings(json, "checklist"),
                breathable,
                gases,
                Json.getBoolean(air, "corrosive", false),
                Json.getFloat(air, "temperature", 0.0F),
                Json.atLeast(air, "wind", gases.isEmpty() ? 0.0F : 1.0F, 0.0F),
                Json.atLeast(air, "density", 1.0F, 0.0F),
                Json.getFloat(json, "meteorFrequency", -1.0F),
                Json.atLeast(json, "fuelMultiplier", 1.0F, 0.0F),
                Json.getFloat(json, "soundReduction", -1.0F),
                Json.atLeast(json, "solarEnergy", 1.0F, 0.0F),
                Json.getBoolean(json, "netherPortals", false),
                Json.atLeast(dungeon, "spacing", 0, 0),
                chest,
                ContentParserModPlanets.extraPlanets(key, json),
                recipe(key, json),
                Json.getBoolean(json, "showName", false));
    }

    private static Map<String, Integer> recipe(ResourceLocation key, JsonObject json) {
        Map<String, Integer> found = new LinkedHashMap<>();
        if (!json.has("recipe")) { return found; }
        for (Map.Entry<String, JsonElement> entry : Json.getJsonObject(json, "recipe").entrySet()) {
            JsonElement count = entry.getValue();
            if (!count.isJsonPrimitive() || !count.getAsJsonPrimitive().isNumber() || count.getAsInt() < 1) {
                ContentLog.LOGGER.error("Dimension {} asks for {} of '{}' in its station recipe, which is not a count of 1 or more, ignoring it", key, count, entry.getKey());
                continue;
            }
            found.put(entry.getKey().trim(), count.getAsInt());
        }
        return Collections.unmodifiableMap(found);
    }

    @Nullable private static CelestialDef override(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", OVERRIDE_KEYS);
        String body = Json.getString(json, "body", "").trim();
        if (body.isEmpty()) {
            ContentLog.LOGGER.error("Celestial override {} names no body, so it changes nothing", key);
            return null;
        }
        return new CelestialDef(key, CelestialDef.OVERRIDE, body, "", Json.location(json, "icon"), 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0,
                Json.location(json, "ring"), Json.atLeast(json, "ringSize", BodyRings.DEFAULT_SIZE, 0.1F), ringFront(key, json), 0.0F, -1,
                "", null, 0.0F, 0.0F, 0.0F, null, strings(json, "requires"), ContentParserModPlanets.galaxySpace(key, json));
    }

    private static BodyRings.Front ringFront(ResourceLocation key, JsonObject json) {
        String value = Json.getString(json, "ringFront", "bottom");
        BodyRings.Front front = BodyRings.Front.parse(value);
        if (front == null) {
            ContentLog.LOGGER.error("Celestial {} gives ringFront '{}', which must be bottom, top, left or right, so it uses bottom", key, value);
            return BodyRings.Front.BOTTOM;
        }
        return front;
    }

    @Nullable private static CelestialDef system(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", SYSTEM_KEYS);
        String name = Json.getString(json, "name", key.getPath()).trim();
        if (!JsonUtils.isJsonArray(json, "mapPosition")) {
            ContentLog.LOGGER.error("Star system {} has no mapPosition, so there is nowhere on the galaxy map to put it", key);
            return null;
        }
        JsonArray at = json.getAsJsonArray("mapPosition");
        if (at.size() < 2) {
            ContentLog.LOGGER.error("Star system {} gives a mapPosition of {}, which needs at least x and y", key, at);
            return null;
        }
        JsonObject starJson = Json.getJsonObject(json, "star", new JsonObject());
        Json.unknownKeys(starJson, "star", BODY_KEYS);
        CelestialDef star = body(key, starJson, CelestialDef.STAR, name, -1, Collections.emptyList());
        if (star == null) { return null; }
        String galaxy = Json.getLowerString(json, "galaxy", "milky_way");
        if (!GALAXY.matcher(galaxy).matches()) {
            ContentLog.LOGGER.error("Star system {} names galaxy '{}', which may hold only the letters a to z, digits and underscores, so it is left off the map", key, galaxy);
            return null;
        }
        return new CelestialDef(key, CelestialDef.SYSTEM, name, "", star.icon, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0, null, 0.0F, BodyRings.Front.BOTTOM, 0.0F, -1,
                galaxy, Json.location(json, "galaxyImage"),
                Json.asFloat(at.get(0), "at", 0.0F), Json.asFloat(at.get(1), "at", 0.0F), at.size() > 2 ? Json.asFloat(at.get(2), "at", 0.0F) : 0.0F,
                star, strings(json, "requires"), null);
    }

    @Nullable private static CelestialDef body(ResourceLocation key, JsonObject json, String kind, String fallbackName, int tier, List<String> requires) {
        String name = Json.getString(json, "name", fallbackName).trim();
        if (name.isEmpty()) {
            ContentLog.LOGGER.error("Celestial body {} has an empty name, so it is left off the map", key);
            return null;
        }
        boolean moon = CelestialDef.MOON.equals(kind);
        boolean station = CelestialDef.STATION.equals(kind);
        String parent = Json.getString(json, "parent", moon || station ? "" : "sol").trim();
        if (parent.isEmpty() && !CelestialDef.STAR.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is a {} with no parent, so it has nothing to circle", key, kind);
            return null;
        }
        float distance = Json.getFloat(json, "distance", station ? 9.0F : moon ? 13.0F : 1.0F);
        ResourceLocation ring = Json.location(json, "ring");
        if (ring != null && !moon && !CelestialDef.PLANET.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is a {} and sets a ring, which only a planet or moon can have, ignoring it", key, kind);
            ring = null;
        }
        float beltWidth = Json.atLeast(json, "beltWidth", 0.0F, 0.0F);
        if (beltWidth > 0.0F && !CelestialDef.ASTEROIDS.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is a {} and sets a beltWidth, which only an asteroid belt can have, ignoring it", key, kind);
            beltWidth = 0.0F;
        }
        return new CelestialDef(key, kind, name, parent,
                new ResourceLocation(Json.getString(json, "icon", ICONS + icon(kind) + ".png").trim()),
                Json.getFloat(json, "relativeSize", moon || station ? 0.2667F : 1.0F),
                distance,
                Json.getFloat(json, "scaledDistance", distance),
                Json.getFloat(json, "orbitTime", station ? 20.0F : moon ? 100.0F : 1.0F),
                Json.getFloat(json, "phaseShift", station ? Float.NaN : 0.0F),
                ContentTypes.color(Json.getString(json, "ringColor", RING), key + " ringColor"),
                ring,
                Json.atLeast(json, "ringSize", BodyRings.DEFAULT_SIZE, 0.1F),
                ringFront(key, json),
                beltWidth,
                CelestialDef.STAR.equals(kind) || CelestialDef.SUBSYSTEM.equals(kind) ? -1 : Json.getInt(json, "tier", tier),
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
