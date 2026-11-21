package mctmods.resourcedatapackloader.common.pack.loading.parsers;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.CelestialDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.RingFront;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class ContentParserCelestial {
    private static final List<String> UNUSED = List.of("galaxy", "galaxyImage", "mapPosition", "subsystem", "scaledDistance", "galaxyspace", "extraplanets");
    private static final List<String> CELESTIAL_KEYS = List.of("kind", "requires", "name", "parent", "distance", "ring", "beltWidth", "icon", "relativeSize", "orbitTime", "phaseShift", "ringColor", "ringSize", "ringFront", "galaxy", "galaxyImage", "mapPosition", "subsystem", "scaledDistance", "galaxyspace", "extraplanets");
    private static final List<String> SYSTEM_KEYS = List.of("kind", "name", "star", "requires", "ringColor", "galaxy", "galaxyImage", "mapPosition", "subsystem", "scaledDistance", "galaxyspace", "extraplanets");
    private static final List<String> STAR_KEYS = List.of("icon", "relativeSize");
    private static final String RING = "#19E599";
    private static final String SUBSYSTEM = "subsystem";
    private static final String OVERRIDE = "override";
    private static final String ICONS = "ad_astra:textures/environment/";

    private ContentParserCelestial() {}

    @Nullable public static CelestialDef celestial(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "celestial body");
        if (json == null) { return null; }
        String kind = Settings.lower(Json.getString(json, "kind", ""));
        if (SUBSYSTEM.equals(kind) || OVERRIDE.equals(kind)) {
            ContentLog.LOGGER.info("Celestial body {} is of kind '{}', which this line does not read, so it is skipped", key, kind);
            return null;
        }
        if (CelestialDef.SYSTEM.equals(kind)) { return system(key, json); }
        if (!CelestialDef.PLANET.equals(kind) && !CelestialDef.MOON.equals(kind)) {
            ContentLog.LOGGER.error("Celestial body {} is of kind '{}', which is none of {}, {} or {}, so it is left off the map", key, kind, CelestialDef.SYSTEM, CelestialDef.PLANET, CelestialDef.MOON);
            return null;
        }
        Json.unknownKeys(json, "the top level", CELESTIAL_KEYS);
        return body(key, json, kind, key.getPath(), Json.strings(json, "requires"));
    }

    @Nullable public static CelestialDef mapBody(ResourceLocation key, JsonObject adastra, String kind) { return body(key, adastra, kind, key.getPath(), List.of()); }

    private static CelestialDef system(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "the top level", SYSTEM_KEYS);
        String name = Json.getString(json, "name", key.getPath()).trim();
        skipped(key, json);
        JsonObject star = Json.getJsonObject(json, "star", new JsonObject());
        Json.unknownKeys(star, "star", STAR_KEYS);
        return new CelestialDef(key, CelestialDef.SYSTEM, name.isEmpty() ? key.getPath() : name, "", ResourceLocation.parse(Json.getString(star, "icon", ICONS + "sun.png").trim()), Json.getFloat(star, "relativeSize", 1.0F), 0.0F, 1.0F, 0.0F, ringColor(key, json), null,
                CelestialDef.DEFAULT_RING_SIZE, RingFront.BOTTOM, 0.0F, Json.strings(json, "requires"));
    }

    @Nullable private static CelestialDef body(ResourceLocation key, JsonObject json, String kind, String fallbackName, List<String> requires) {
        String name = Json.getString(json, "name", fallbackName).trim();
        if (name.isEmpty()) {
            ContentLog.LOGGER.error("Celestial body {} has an empty name, so it is left off the map", key);
            return null;
        }
        boolean moon = CelestialDef.MOON.equals(kind);
        String parent = Json.getString(json, "parent", moon ? "" : "sol").trim();
        if (parent.isEmpty()) {
            ContentLog.LOGGER.error("Celestial body {} is a moon with no parent, so it has nothing to circle", key);
            return null;
        }
        skipped(key, json);
        String ringPath = Json.getString(json, "ring", "").trim();
        ResourceLocation ring = ringPath.isEmpty() ? null : ResourceLocation.parse(ringPath);
        return new CelestialDef(key, kind, name, parent, ResourceLocation.parse(Json.getString(json, "icon", ICONS + (moon ? "moon" : "mars") + ".png").trim()), Json.getFloat(json, "relativeSize", moon ? 0.2667F : 1.0F), Json.getFloat(json, "distance", moon ? 13.0F : 1.0F),
                Json.getFloat(json, "orbitTime", moon ? 100.0F : 1.0F), Json.getFloat(json, "phaseShift", 0.0F), ringColor(key, json), ring, Math.max(0.1F, Json.getFloat(json, "ringSize", CelestialDef.DEFAULT_RING_SIZE)), ringFront(key, json),
                Math.max(0.0F, Json.getFloat(json, "beltWidth", 0.0F)), requires);
    }

    private static int ringColor(ResourceLocation key, JsonObject json) { return 0xFF000000 | ContentParser.color(Json.getString(json, "ringColor", RING), key + " ringColor"); }

    private static RingFront ringFront(ResourceLocation key, JsonObject json) {
        String value = Json.getString(json, "ringFront", "bottom");
        RingFront front = RingFront.parse(value);
        if (front == null) {
            ContentLog.LOGGER.error("Celestial {} gives ringFront '{}', which must be bottom, top, left or right, so it uses bottom", key, value);
            return RingFront.BOTTOM;
        }
        return front;
    }

    private static void skipped(ResourceLocation key, JsonObject json) {
        for (String unused : UNUSED) {
            if (json.has(unused)) { ContentLog.LOGGER.info("Celestial {} sets '{}', which this line does not read, so it is skipped", key, unused); }
        }
    }
}
