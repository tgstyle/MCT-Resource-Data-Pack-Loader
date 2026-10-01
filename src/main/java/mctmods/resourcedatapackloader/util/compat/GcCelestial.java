package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.ContentParserModPlanets;
import mctmods.resourcedatapackloader.content.def.CelestialDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.GalacticraftDef;
import mctmods.resourcedatapackloader.content.def.GalaxySpaceDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo;
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Loader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class GcCelestial {
    static final ResourceLocation ROCKET_GUI = new ResourceLocation("galacticraftcore", "textures/gui/overworld_rocket_gui.png");
    private static final Map<Integer, CelestialBody> BODIES = new HashMap<>();
    private static final List<Integer> ROCKET_DIMENSIONS = new ArrayList<>();
    private static final Map<String, Integer> RADIATION = new HashMap<>();
    private static final boolean GALAXY_SPACE = Loader.isModLoaded(ContentParserModPlanets.GALAXY_SPACE);
    private static final boolean EXTRA_PLANETS = Loader.isModLoaded(ContentParserModPlanets.EXTRA_PLANETS);
    static final String PLANETS = "galacticraftplanets";
    private static boolean stations;

    private GcCelestial() {}

    @Nullable public static CelestialBody body(int dimension) { return BODIES.get(dimension); }

    public static void register(List<CelestialDef> files, List<DimensionDef> dimensions) {
        GcTeleport teleport = new GcTeleport();
        List<Class<? extends GcWorldProvider>> providers = new ArrayList<>(Arrays.asList(GcWorldProvider.class, GcWeatherWorldProvider.class));
        if (GALAXY_SPACE) { providers.addAll(GsBodies.providers()); }
        for (Class<? extends GcWorldProvider> provider : providers) {
            GalacticraftRegistry.registerTeleportType(provider, teleport);
            GalacticraftRegistry.registerRocketGui(provider, ROCKET_GUI);
        }
        List<String> made = new ArrayList<>();
        for (CelestialDef def : files) {
            if (CelestialDef.SYSTEM.equals(def.kind) && system(def)) { made.add("system " + def.name); }
        }
        for (String kind : new String[] {CelestialDef.PLANET, CelestialDef.MOON, CelestialDef.ASTEROIDS}) {
            for (CelestialDef def : files) {
                if (!kind.equals(def.kind)) { continue; }
                missingMods(def, null);
                CelestialBody body = body(def, 0L, 0.0F);
                if (body == null) { continue; }
                GalaxyRegistry.register(body);
                made.add(kind + " " + def.name);
            }
            for (DimensionDef dimension : dimensions) {
                GalacticraftDef gc = dimension.galacticraft;
                if (gc != null && kind.equals(gc.body.kind) && dimension(dimension, gc)) { made.add(kind + " " + gc.body.name + " (dimension " + dimension.id + ")"); }
            }
        }
        for (DimensionDef dimension : dimensions) {
            GalacticraftDef gc = dimension.galacticraft;
            if (gc == null || !CelestialDef.STATION.equals(gc.body.kind)) { continue; }
            if (GcStations.register(dimension, gc)) {
                stations = true;
                made.add(CelestialDef.STATION + " " + gc.body.name + " (dimension types " + dimension.id + " and " + (dimension.id + 1) + ")");
            }
            else { ContentLog.LOGGER.error("Dimension {} could not be put on the Galacticraft map, so it is not registered", dimension.registryName); }
        }
        if (!made.isEmpty()) { Summary.info("galacticraft", "Put " + made.size() + " pack body(ies) on the Galacticraft map: " + made); }
    }

    public static void stationRecipes() {
        if (stations) { GcStations.recipes(); }
    }

    public static Planet stationHome(Satellite station) { return stations ? GcStations.home(station) : station.getParentPlanet(); }

    static boolean packBody(CelestialBody body) { return BODIES.containsValue(body); }

    public static void nameDimensions() {
        for (Integer id : ROCKET_DIMENSIONS) {
            CelestialBody body = BODIES.get(id);
            if (body != null && WorldUtil.dimNames.containsKey(id)) { WorldUtil.dimNames.put(id, body.getTranslationKey()); }
        }
    }

    private static boolean system(CelestialDef def) {
        for (SolarSystem held : GalaxyRegistry.getSolarSystems()) {
            if (held.getName().equalsIgnoreCase(def.name)) {
                ContentLog.LOGGER.error("Star system {} is named '{}', which another star system already has, so it is left off the map", def.key, def.name);
                return false;
            }
        }
        SolarSystem system = new SolarSystem(def.name, def.galaxy).setMapPosition(new Vector3(def.mapX, def.mapY, def.mapZ));
        CelestialDef starDef = def.star == null ? def : def.star;
        missingMods(starDef, null);
        Star star = new Star(starDef.name).setParentSolarSystem(system);
        map(star, starDef);
        GalaxySpaceDef gs = galaxySpace(starDef);
        if (gs != null) { GsBodies.star(star, gs); }
        system.setMainStar(star);
        GalaxyRegistry.register(system);
        return true;
    }

    @Nullable private static CelestialBody body(CelestialDef def, long dayLength, float gravity) {
        GalaxySpaceDef gs = galaxySpace(def);
        CelestialBody body;
        if (CelestialDef.MOON.equals(def.kind) || CelestialDef.ASTEROIDS.equals(def.kind) && solarSystem(def.parent) == null) {
            Planet parent = planet(def.parent);
            if (parent == null) {
                if (CelestialDef.MOON.equals(def.kind)) { ContentLog.LOGGER.error("Moon {} circles '{}', which is no planet Galacticraft knows, so it is left off the map", def.key, def.parent); }
                else { ContentLog.LOGGER.error("Asteroid belt {} circles '{}', which is no star system or planet Galacticraft knows, so it is left off the map", def.key, def.parent); }
                return null;
            }
            body = gs != null ? GsBodies.moon(def.name, parent) : new Moon(def.name).setParentPlanet(parent);
        }
        else {
            SolarSystem parent = solarSystem(def.parent);
            if (parent == null) {
                ContentLog.LOGGER.error("Planet {} circles '{}', which is no star system Galacticraft knows, so it is left off the map", def.key, def.parent);
                return null;
            }
            body = gs != null ? GsBodies.planet(def.name, parent) : new Planet(def.name).setParentSolarSystem(parent);
        }
        if (GalaxyRegistry.getPlanetOrMoonFromTranslationkey(body.getTranslationKey()) != null) {
            ContentLog.LOGGER.error("Celestial body {} is named '{}', which another {} already has, so it is left off the map", def.key, def.name, def.kind);
            return null;
        }
        map(body, def);
        if (gs != null) { GsBodies.body(body, gs, dayLength, gravity); }
        return body;
    }

    @Nullable private static GalaxySpaceDef galaxySpace(CelestialDef def) { return GALAXY_SPACE ? def.galaxySpace : null; }

    private static void missingMods(CelestialDef def, @Nullable GalacticraftDef gc) {
        if (!GALAXY_SPACE && def.galaxySpace != null) { ContentLog.LOGGER.error("Celestial body {} sets galaxyspace keys, but GalaxySpace is not loaded, so they do nothing", def.key); }
        if (!EXTRA_PLANETS && gc != null && gc.extraPlanets != null) { ContentLog.LOGGER.error("Celestial body {} sets extraplanets keys, but ExtraPlanets is not loaded, so they do nothing", def.key); }
    }

    public static void extraPlanetsRadiation() {
        if (EXTRA_PLANETS && !RADIATION.isEmpty()) { EpPlanets.radiation(RADIATION); }
    }

    static void map(CelestialBody body, CelestialDef def) {
        body.setRelativeSize(def.relativeSize);
        body.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(def.distance, def.scaledDistance));
        body.setRelativeOrbitTime(def.orbitTime);
        body.setPhaseShift(def.phaseShift);
        body.setRingColorRGB(((def.ringColor >> 16) & 255) / 255.0F, ((def.ringColor >> 8) & 255) / 255.0F, (def.ringColor & 255) / 255.0F);
        body.setTierRequired(def.tier);
        body.setBodyIcon(def.icon);
    }

    private static boolean dimension(DimensionDef dimension, GalacticraftDef gc) {
        missingMods(gc.body, gc);
        if (CelestialDef.ASTEROIDS.equals(gc.body.kind) && beltless(dimension)) { return false; }
        CelestialBody body = body(gc.body, dimension.traits.dayLength, GcWorldProvider.gravity(dimension.id));
        if (body == null) {
            ContentLog.LOGGER.error("Dimension {} could not be put on the Galacticraft map, so it is not registered", dimension.registryName);
            return false;
        }
        CelestialBody holder = GalaxyRegistry.getCelestialBodyFromDimensionID(dimension.id);
        if (holder != null || DimensionManager.isDimensionRegistered(dimension.id)) {
            ContentLog.LOGGER.error("Dimension {} wants id {}, which {} already holds. Change the id or remove the conflicting mod", dimension.registryName, dimension.id, holder == null ? "another dimension" : holder.getTranslationKey());
            return false;
        }
        GalaxySpaceDef gs = galaxySpace(gc.body);
        Class<? extends GcWorldProvider> provider = gs != null ? GsBodies.provider(gc, gs) : gc.rain == null ? GcWorldProvider.class : GcWeatherWorldProvider.class;
        body.setDimensionInfo(dimension.id, provider, gc.reachable);
        body.setDimensionSuffix(dimension.suffix);
        body.setForceStaticLoad(dimension.keepLoaded);
        body.setAtmosphere(atmosphere(dimension, gc));
        if (CelestialDef.ASTEROIDS.equals(gc.body.kind)) { body.setBiomeInfo(GcAsteroids.biome()); }
        if (!gc.checklist.isEmpty()) { body.addChecklistKeys(gc.checklist.toArray(new String[0])); }
        if (gc.reachable) {
            ContentDimensions.claim(dimension);
            ROCKET_DIMENSIONS.add(dimension.id);
        }
        else {
            body.setUnreachable();
            if (!ContentDimensions.register(dimension, provider)) { return false; }
        }
        GalaxyRegistry.register(body);
        BODIES.put(dimension.id, body);
        if (gc.extraPlanets != null && gc.extraPlanets.radiation >= 0) { RADIATION.put(body.getTranslationKey(), gc.extraPlanets.radiation); }
        return true;
    }

    private static boolean beltless(DimensionDef dimension) {
        if (!Loader.isModLoaded(PLANETS)) {
            ContentLog.LOGGER.error("Dimension {} is an asteroid belt, which Galacticraft Planets generates, and Galacticraft Planets is not loaded, so it is not registered", dimension.registryName);
            return true;
        }
        if (!DimensionDef.VOID.equals(dimension.terrain)) {
            ContentLog.LOGGER.error("Dimension {} is an asteroid belt on '{}' terrain, and asteroids generate only in a terrain of type {}, so it is not registered", dimension.registryName, dimension.terrain, DimensionDef.VOID);
            return true;
        }
        return false;
    }

    private static AtmosphereInfo atmosphere(DimensionDef dimension, GalacticraftDef gc) {
        AtmosphereInfo info = new AtmosphereInfo(gc.breathable, dimension.traits.precipitation, gc.corrosive, gc.temperature, gc.wind, gc.density);
        for (String gas : gc.gases) { info.composition.add(EnumAtmosphericGas.valueOf(gas)); }
        return info;
    }

    @Nullable private static SolarSystem solarSystem(String name) {
        for (SolarSystem held : GalaxyRegistry.getSolarSystems()) {
            if (held.getName().equalsIgnoreCase(name) || held.getTranslationKey().equalsIgnoreCase(name)) { return held; }
        }
        return null;
    }

    @Nullable static Planet planet(String name) {
        for (Planet held : GalaxyRegistry.getPlanets()) {
            if (held.getName().equalsIgnoreCase(name) || held.getTranslationKey().equalsIgnoreCase(name)) { return held; }
        }
        return null;
    }

    @Nullable static Moon moon(String name) {
        for (Moon held : GalaxyRegistry.getMoons()) {
            if (held.getName().equalsIgnoreCase(name) || held.getTranslationKey().equalsIgnoreCase(name)) { return held; }
        }
        return null;
    }
}
