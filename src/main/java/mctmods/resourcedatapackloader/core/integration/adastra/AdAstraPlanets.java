package mctmods.resourcedatapackloader.core.integration.adastra;

import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.environment.ContentPhysics;
import mctmods.resourcedatapackloader.common.generated.ContentFormats;
import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.AdAstraDef;
import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyPoseDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionTraitsDef;
import mctmods.resourcedatapackloader.common.pack.io.GeneratedResources;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserAdAstra;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.GameData;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public final class AdAstraPlanets {
    private static final String ORBIT_SUFFIX = "_orbit";
    private static final double EARTH_GRAVITY = 9.807D;
    private static final int ORBIT_SOLAR_POWER = 32;
    private static final short ORBIT_TEMPERATURE = -270;
    private static final String ACID_RAIN = ContentFormats.BIOME_TAGS + "/has_acid_rain" + PackManager.JSON_SUFFIX;
    private static final String STATION_FOLDER = "/space_station/";
    private static final String STATION_SUFFIX = "_space_station";
    private static final ResourceLocation DEFAULT_RECIPE = ResourceLocation.fromNamespaceAndPath(ContentAdAstra.MOD_ID, ContentFormats.RECIPE_FOLDER + STATION_FOLDER + "earth_orbit" + STATION_SUFFIX + PackManager.JSON_SUFFIX);
    private static final String SUN = ContentAdAstra.MOD_ID + ":textures/environment/sun.png";
    private static final String BODY_FALLBACK = ContentAdAstra.MOD_ID + ":textures/environment/moon.png";
    private static final float HOME_SIZE = 80.0F;
    private static final int[] STAR_COLORS = {-1447239681, -1143472129, -726785, -3038977, -7697665, -12254977, -2817, -464897, -256, -65536};
    private static final int[] STAR_WEIGHTS = {3, 5, 100, 80, 150, 10, 60, 40, 20, 1};

    private AdAstraPlanets() {}

    private record Station(DimensionDef def, AdAstraDef station) {}

    public static void generate() {
        if (!ContentAdAstra.active()) { return; }
        Map<ResourceLocation, Station> stations = stations();
        Set<String> acid = new LinkedHashSet<>();
        for (DimensionDef def : ContentDimensions.available()) {
            AdAstraDef body = def.adastra();
            if (body == null || !body.reachable() || body.isStation()) { continue; }
            if (!ContentDimensions.generated(def.key())) {
                ContentLog.LOGGER.error("Dimension {} has an adastra block but is not a dimension RDPL generates, so no planet is written for it", def.key());
                continue;
            }
            Station station = stations.get(def.key());
            ResourceLocation orbit = station != null ? station.def().key() : orbit(def.key());
            if (!ContentDimensions.generateExtra(orbit, ContentParserAdAstra.orbitWorld())) {
                ContentLog.LOGGER.error("Dimension {} adastra cannot get its orbit dimension {}, so no planet is written for it", def.key(), orbit);
                continue;
            }
            ResourceLocation system = body.system() != null ? body.system() : moonSystem(body.parent());
            ContentPhysics.leaveGravityTo(def.key());
            ContentPhysics.leaveGravityTo(orbit);
            write(def.key(), planet(def.key(), body.breathable(), body.temperature(), ContentPhysics.authoredGravity(def.key(), def.traits().gravity()) * EARTH_GRAVITY, body.solarPower(), system, orbit, body.tier(), body.launchFrom()));
            ContentLog.LOGGER.info("Wrote Ad Astra planet {} ({}, tier {}, orbit {})", def.key(), body.kind(), body.tier(), orbit);
            int tier = station == null || station.station().tier() == AdAstraDef.UNSET ? body.tier() : station.station().tier();
            write(orbit, planet(orbit, false, ORBIT_TEMPERATURE, 0.0D, ORBIT_SOLAR_POWER, system, null, tier, List.of()));
            ContentLog.LOGGER.info("Wrote Ad Astra orbit planet {} for {}", orbit, def.key());
            if (station != null) { station(station, def, orbit); }
            if (body.corrosive()) { corrosive(def, acid); }
        }
        if (!acid.isEmpty()) { writeAcid(acid); }
    }

    private static ResourceLocation orbit(ResourceLocation key) { return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath() + ORBIT_SUFFIX); }

    private static ResourceLocation moonSystem(String parent) {
        for (DimensionDef def : ContentDimensions.all()) {
            AdAstraDef planet = def.adastra();
            if (planet == null || planet.isMoon() || planet.system() == null) { continue; }
            if (def.key().toString().equals(parent) || def.key().getPath().equals(parent)) { return planet.system(); }
        }
        return AdAstraDef.SOLAR_SYSTEM;
    }

    private static JsonObject planet(ResourceLocation key, boolean oxygen, short temperature, double gravity, int solarPower, ResourceLocation system, ResourceLocation orbit, int tier, List<ResourceLocation> launchFrom) {
        JsonObject json = new JsonObject();
        json.addProperty("dimension", key.toString());
        json.addProperty("oxygen", oxygen);
        json.addProperty("temperature", temperature);
        json.addProperty("gravity", (float) gravity);
        json.addProperty("solar_power", solarPower);
        json.addProperty("solar_system", system.toString());
        if (orbit != null) { json.addProperty("orbit", orbit.toString()); }
        json.addProperty("tier", tier);
        if (!launchFrom.isEmpty()) {
            JsonArray sites = new JsonArray();
            for (ResourceLocation site : launchFrom) { sites.add(site.toString()); }
            json.add("additional_launch_dimensions", sites);
        }
        return json;
    }

    private static void write(ResourceLocation key, JsonObject planet) {
        GeneratedResources.put(PackType.SERVER_DATA, key.getNamespace(), "planets/" + key.getPath() + PackManager.JSON_SUFFIX, planet.toString());
    }

    private static Map<ResourceLocation, Station> stations() {
        Map<ResourceLocation, Station> held = new LinkedHashMap<>();
        for (DimensionDef def : ContentDimensions.available()) {
            AdAstraDef station = def.adastra();
            if (station == null || !station.isStation()) { continue; }
            DimensionDef home = home(station.parent());
            if (home == null || !ContentDimensions.generated(def.key())) {
                ContentLog.LOGGER.error("Station {} orbits '{}', which is no planet or moon a pack dimension makes that a rocket reaches, so no station is written for it", def.key(), station.parent());
                continue;
            }
            Station before = held.get(home.key());
            if (before != null) {
                ContentLog.LOGGER.error("Station {} orbits '{}', and station {} already holds that body, so no station is written for it", def.key(), station.parent(), before.def().key());
                continue;
            }
            held.put(home.key(), new Station(def, station));
        }
        return held;
    }

    @Nullable private static DimensionDef home(String parent) {
        for (DimensionDef def : ContentDimensions.available()) {
            AdAstraDef body = def.adastra();
            if (body == null || body.isStation() || !body.reachable() || !ContentDimensions.generated(def.key())) { continue; }
            if (def.key().toString().equals(parent) || def.key().getPath().equals(parent)) { return def; }
        }
        return null;
    }

    private static void station(Station station, DimensionDef home, ResourceLocation orbit) {
        JsonArray ingredients = ingredients(station.station());
        if (ingredients == null) { ContentLog.LOGGER.error("Station {} cannot be built: it sets no recipe of its own and Ad Astra's own station recipe {} could not be read", orbit, DEFAULT_RECIPE); }
        else {
            JsonObject recipe = new JsonObject();
            recipe.addProperty("type", ContentAdAstra.MOD_ID + ":space_station_recipe");
            recipe.addProperty("dimension", orbit.toString());
            recipe.add("ingredients", ingredients);
            recipe.addProperty("structure", station.station().structure().toString());
            GeneratedResources.put(PackType.SERVER_DATA, orbit.getNamespace(), ContentFormats.RECIPE_FOLDER + STATION_FOLDER + orbit.getPath() + STATION_SUFFIX + PackManager.JSON_SUFFIX, recipe.toString());
        }
        boolean ownSky = ContentEnvironments.givesSky(orbit, station.def());
        if (ownSky) { homeBody(station.def(), home); }
        else { GeneratedResources.put(PackType.CLIENT_RESOURCES, orbit.getNamespace(), "planet_renderers/" + orbit.getPath() + PackManager.JSON_SUFFIX, renderer(orbit, home).toString()); }
        ContentLog.LOGGER.info("Wrote Ad Astra station {} over {} ({} recipe, {} sky)", orbit, home.key(), station.station().recipe().isEmpty() ? "Ad Astra's earth_orbit" : "its own", ownSky ? "RDPL's" : "Ad Astra's");
    }

    @Nullable private static JsonArray ingredients(AdAstraDef station) {
        if (station.recipe().isEmpty()) {
            JsonObject recipe = GameData.json(DEFAULT_RECIPE);
            JsonElement held = recipe == null ? null : recipe.get("ingredients");
            return held != null && held.isJsonArray() ? held.getAsJsonArray().deepCopy() : null;
        }
        JsonArray out = new JsonArray();
        for (Map.Entry<String, Integer> part : station.recipe().entrySet()) {
            JsonObject ingredient = new JsonObject();
            if (part.getKey().startsWith("#")) { ingredient.addProperty("tag", part.getKey().substring(1)); }
            else { ingredient.addProperty("item", part.getKey()); }
            JsonObject entry = new JsonObject();
            entry.addProperty("count", part.getValue());
            entry.add("ingredient", ingredient);
            out.add(entry);
        }
        return out;
    }

    private static JsonObject renderer(ResourceLocation orbit, DimensionDef home) {
        JsonObject json = skyProperties(orbit);
        json.add("star_colors", starColors());
        json.add("sky_renderables", renderables(home));
        return json;
    }

    private static JsonObject skyProperties(ResourceLocation orbit) {
        JsonObject json = new JsonObject();
        json.addProperty("dimension", orbit.toString());
        json.addProperty("custom_clouds", true);
        json.addProperty("custom_sky", true);
        json.addProperty("custom_weather", false);
        json.addProperty("has_thick_fog", false);
        json.addProperty("has_fog", false);
        json.addProperty("sunrise_color", 14180147);
        json.addProperty("stars", 13000);
        json.addProperty("star_brightness", 0.6F);
        json.addProperty("sunrise_angle", 0);
        json.addProperty("render_in_rain", true);
        return json;
    }

    private static JsonArray renderables(DimensionDef home) {
        JsonArray renderables = new JsonArray();
        renderables.add(renderable(SUN, 9.0F, 0.0F, "TIME_OF_DAY", -39, 27.0F));
        renderables.add(renderable(homeIcon(home), HOME_SIZE, 180.0F, "STATIC", -5261108, 240.0F));
        return renderables;
    }

    private static String homeIcon(DimensionDef home) {
        AdAstraDef body = home.adastra();
        return body != null && body.map() != null ? body.map().icon().toString() : BODY_FALLBACK;
    }

    private static void homeBody(DimensionDef station, DimensionDef home) {
        ResourceLocation icon = ResourceLocation.parse(homeIcon(home));
        DimensionTraitsDef.Sky sky = station.traits().sky();
        if (sky != null && sky.bodies() != null && sky.bodies().stream().anyMatch(held -> held.texture().equals(icon))) { return; }
        ContentDimensions.addSkyBody(station.key(), new DimensionTraitsDef.Body(icon, HOME_SIZE, 180.0F, 0.0F, false, SkyPoseDef.NONE, ""));
    }

    private static JsonArray starColors() {
        JsonArray colors = new JsonArray();
        for (int i = 0; i < STAR_COLORS.length; i++) {
            JsonObject color = new JsonObject();
            color.addProperty("data", STAR_COLORS[i]);
            color.addProperty("weight", STAR_WEIGHTS[i]);
            colors.add(color);
        }
        return colors;
    }

    private static JsonObject renderable(String texture, float scale, float tilt, String movement, int backLight, float backLightScale) {
        JsonObject json = new JsonObject();
        json.addProperty("texture", texture);
        json.addProperty("scale", scale);
        json.add("global_rotation", rotation(tilt));
        json.add("local_rotation", rotation(0.0F));
        json.addProperty("movement_type", movement);
        json.addProperty("blend", false);
        json.addProperty("back_light_color", backLight);
        json.addProperty("back_light_scale", backLightScale);
        return json;
    }

    private static JsonArray rotation(float x) {
        JsonArray rotation = new JsonArray();
        rotation.add(x);
        rotation.add(0.0F);
        rotation.add(0.0F);
        return rotation;
    }

    private static void corrosive(DimensionDef def, Set<String> acid) {
        if (DimensionDef.SINGLE.equals(def.biomeSource())) { acid.add(def.biome()); }
        else if (DimensionDef.LIST.equals(def.biomeSource())) {
            for (PickDef pick : def.biomeList()) { acid.add(pick.name()); }
        }
        else { ContentLog.LOGGER.error("Dimension {} is corrosive but takes its biomes from the terrain, so no biome joins ad_astra:has_acid_rain, set biomes.source to single or list", def.key()); }
    }

    private static void writeAcid(Set<String> biomes) {
        JsonArray values = new JsonArray();
        for (String biome : biomes) {
            JsonObject entry = new JsonObject();
            entry.addProperty("id", biome);
            entry.addProperty("required", false);
            values.add(entry);
        }
        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        GeneratedResources.put(PackType.SERVER_DATA, ContentAdAstra.MOD_ID, ACID_RAIN, tag.toString());
    }
}
