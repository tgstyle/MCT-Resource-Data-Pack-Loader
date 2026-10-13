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
        String kind = Settings.lower(Json.getString(json, "kind", ""));
        if (CelestialDef.SYSTEM.equals(kind)) { return system(key, json); }
        if (!CelestialDef.PLANET.equals(kind) && !CelestialDef.SUBSYSTEM.equals(kind) && !CelestialDef.MOON.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is of kind '{}', which is none of {}, {}, {} or {}, so it is left off the map", key, kind, CelestialDef.SYSTEM, CelestialDef.PLANET, CelestialDef.SUBSYSTEM, CelestialDef.MOON);
            return null;
        }
        return body(key, json, kind, key.getPath(), 0, strings(json, "requires"));
    }

    @Nullable public static GalacticraftDef galacticraft(ResourceLocation key, JsonObject dimension) {
        if (!dimension.has("galacticraft")) { return null; }
        JsonObject json = Json.getJsonObject(dimension, "galacticraft");
        String owner = "Dimension " + key + " Galacticraft";
        String kind = Json.choice(json, "kind", CelestialDef.PLANET, owner, KINDS);
        CelestialDef body = body(key, json, kind, key.getPath(), CelestialDef.STATION.equals(kind) ? -1 : 1, Collections.emptyList());
        if (body == null) { return null; }
        String landing = Json.choice(json, "landing", GalacticraftDef.LANDER, owner, LANDINGS);
        String arrival = Json.choice(json, "arrival", GalacticraftDef.DEPARTURE, owner, ARRIVALS);
        String gui = Json.getString(json, "rocketGui", "").trim();
        JsonObject air = Json.getJsonObject(json, "atmosphere", new JsonObject());
        Boolean breathable = air.has("breathable") ? Json.getBoolean(air, "breathable") : null;
        List<String> gases = gases(key, air);
        JsonObject dungeon = Json.getJsonObject(json, "dungeon", new JsonObject());
        String chest = Json.getString(dungeon, "chest", "").trim();
        return new GalacticraftDef(body,
                Json.getBoolean(json, "reachable", true),
                Json.getInt(json, "minTier", body.tier),
                landing,
                Json.getFloat(json, "landingHeight", -1.0F),
                arrival,
                Json.getFloat(json, "exitHeight", -1.0F),
                gui.isEmpty() ? null : new ResourceLocation(gui),
                strings(json, "checklist"),
                breathable,
                gases,
                Json.getBoolean(air, "corrosive", false),
                Json.getFloat(air, "temperature", 0.0F),
                Math.max(0.0F, Json.getFloat(air, "wind", gases.isEmpty() ? 0.0F : 1.0F)),
                Math.max(0.0F, Json.getFloat(air, "density", 1.0F)),
                Json.getFloat(json, "meteorFrequency", -1.0F),
                Math.max(0.0F, Json.getFloat(json, "fuelMultiplier", 1.0F)),
                Json.getFloat(json, "soundReduction", -1.0F),
                Math.max(0.0F, Json.getFloat(json, "solarEnergy", 1.0F)),
                Json.getBoolean(json, "netherPortals", false),
                Math.max(0, Json.getInt(dungeon, "spacing", 0)),
                chest.isEmpty() ? null : new ResourceLocation(chest),
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

    @Nullable private static CelestialDef system(ResourceLocation key, JsonObject json) {
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
        CelestialDef star = body(key, starJson, CelestialDef.STAR, name, -1, Collections.emptyList());
        if (star == null) { return null; }
        String galaxy = Json.getString(json, "galaxy", "milky_way").trim().toLowerCase(Locale.ROOT);
        if (!GALAXY.matcher(galaxy).matches()) {
            ContentLog.LOGGER.error("Star system {} names galaxy '{}', which may hold only the letters a to z, digits and underscores, so it is left off the map", key, galaxy);
            return null;
        }
        String image = Json.getString(json, "galaxyImage", "").trim();
        return new CelestialDef(key, CelestialDef.SYSTEM, name, "", star.icon, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0, null, 0.0F, 0.0F, -1,
                galaxy, image.isEmpty() ? null : new ResourceLocation(image),
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
        String ringPath = Json.getString(json, "ring", "").trim();
        ResourceLocation ring = ringPath.isEmpty() ? null : new ResourceLocation(ringPath);
        if (ring != null && !moon && !CelestialDef.PLANET.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is a {} and sets a ring, which only a planet or moon can have, ignoring it", key, kind);
            ring = null;
        }
        float beltWidth = Math.max(0.0F, Json.getFloat(json, "beltWidth", 0.0F));
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
                Math.max(0.1F, Json.getFloat(json, "ringSize", BodyRings.DEFAULT_SIZE)),
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
