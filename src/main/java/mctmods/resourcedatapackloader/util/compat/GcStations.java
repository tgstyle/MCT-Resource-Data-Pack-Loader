package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.def.CelestialDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.def.GalacticraftDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe;
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo;
import micdoodle8.mods.galacticraft.api.world.SpaceStationType;
import micdoodle8.mods.galacticraft.core.dimension.TeleportTypeOrbit;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import micdoodle8.mods.galacticraft.core.world.gen.BiomeOrbit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

final class GcStations {
    private static final float SPREAD = 2.3999632F;
    private static final Map<Integer, Station> BY_TYPE = new HashMap<>();
    private static final List<Station> STATIONS = new ArrayList<>();
    private static final Map<Satellite, Planet> HOMES = new IdentityHashMap<>();

    private GcStations() {}

    @Nullable static Station byType(int type) { return BY_TYPE.get(type); }

    static List<Station> stations() { return STATIONS; }

    static Planet home(Satellite station) {
        Planet home = HOMES.get(station);
        return home == null ? station.getParentPlanet() : home;
    }

    static boolean register(DimensionDef dimension, GalacticraftDef gc) {
        CelestialDef def = gc.body;
        Planet planet = GcCelestial.planet(def.parent);
        Moon moon = planet == null ? GcCelestial.moon(def.parent) : null;
        CelestialBody home = planet == null ? moon : planet;
        Planet parent = moon == null ? planet : moon.getParentPlanet();
        if (home == null || parent == null || !GcCelestial.packBody(home) || !home.isReachable()) {
            ContentLog.LOGGER.error("Station {} orbits '{}', which is no planet or moon a pack dimension makes that a rocket reaches", def.key, def.parent);
            return false;
        }
        int beside = 0;
        for (Satellite held : GalaxyRegistry.getSatellites()) {
            if (held.getParentPlanet() == parent) { beside++; }
            if (home(held).getDimensionID() == home.getDimensionID() || held.getName().equalsIgnoreCase(def.name)) {
                ContentLog.LOGGER.error("Station {} is named '{}' around '{}', and station {} already has that name or that body", def.key, def.name, def.parent, held.getTranslationKey());
                return false;
            }
        }
        int fixed = dimension.id + 1;
        if (taken(dimension.id) || taken(fixed)) {
            ContentLog.LOGGER.error("Station {} wants dimension type ids {} and {}, and one of them is already held. Change the id or remove the conflicting mod", def.key, dimension.id, fixed);
            return false;
        }
        Satellite body = new Satellite(def.name).setParentBody(parent);
        GcCelestial.map(body, def);
        if (Float.isNaN(def.phaseShift)) { body.setPhaseShift(beside * SPREAD); }
        if (def.tier < 0) { body.setTierRequired(home.getTierRequirement()); }
        body.setDimensionInfo(dimension.id, fixed, GcStationWorldProvider.class);
        body.setAtmosphere(new AtmosphereInfo(false, false, false, 0.0F, 0.1F, 0.02F));
        body.setBiomeInfo(BiomeOrbit.space);
        if (!gc.checklist.isEmpty()) { body.addChecklistKeys(gc.checklist.toArray(new String[0])); }
        GalacticraftRegistry.registerDimension(dimension.getName(), dimension.suffix, dimension.id, GcStationWorldProvider.class, false);
        GalacticraftRegistry.registerDimension(dimension.getName(), dimension.suffix, fixed, GcStationWorldProvider.class, true);
        if (STATIONS.isEmpty()) {
            GalacticraftRegistry.registerTeleportType(GcStationWorldProvider.class, new TeleportTypeOrbit());
            GalacticraftRegistry.registerRocketGui(GcStationWorldProvider.class, GcCelestial.ROCKET_GUI);
        }
        GalaxyRegistry.register(body);
        Station made = new Station(dimension, body, home);
        if (moon != null) { HOMES.put(body, new Home(moon)); }
        BY_TYPE.put(dimension.id, made);
        BY_TYPE.put(fixed, made);
        STATIONS.add(made);
        return true;
    }

    static void recipes() {
        for (Station held : STATIONS) {
            int home = held.home.getDimensionID();
            if (WorldUtil.getSpaceStationRecipe(home) != null) {
                ContentLog.LOGGER.error("Station {} cannot be built: something else already registered a station recipe for dimension {}", held.def.registryName, home);
                continue;
            }
            HashMap<Object, Integer> input = input(held.def);
            SpaceStationRecipe recipe = input.isEmpty() ? WorldUtil.getSpaceStationRecipe(ConfigManagerCore.idDimensionOverworld) : new SpaceStationRecipe(input);
            if (recipe == null) {
                ContentLog.LOGGER.error("Station {} cannot be built: it sets no recipe of its own and Galacticraft has none for its own station", held.def.registryName);
                continue;
            }
            GalacticraftRegistry.registerSpaceStation(new SpaceStationType(held.def.id, home, recipe));
        }
    }

    private static boolean taken(int id) {
        if (DimensionManager.isDimensionRegistered(id) || GalaxyRegistry.getCelestialBodyFromDimensionID(id) != null) { return true; }
        for (DimensionType held : DimensionType.values()) {
            if (held.getId() == id) { return true; }
        }
        return false;
    }

    private static HashMap<Object, Integer> input(DimensionDef def) {
        HashMap<Object, Integer> input = new HashMap<>();
        if (def.galacticraft == null) { return input; }
        for (Map.Entry<String, Integer> entry : def.galacticraft.stationRecipe.entrySet()) {
            Object wanted = ingredient(entry.getKey());
            if (wanted == null) { ContentLog.LOGGER.error("Station {} asks for '{}' in its recipe, which is no item or ore dictionary name, ignoring it", def.registryName, entry.getKey()); }
            else { input.put(wanted, entry.getValue()); }
        }
        return input;
    }

    @Nullable private static Object ingredient(String name) {
        if (!name.contains(":")) { return OreDictionary.doesOreNameExist(name) ? name : null; }
        String[] parts = name.split(":");
        if (parts.length > 3) { return null; }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
        if (item == null) { return null; }
        try { return new ItemStack(item, 1, parts.length == 3 ? Integer.parseInt(parts[2]) : 0); }
        catch (NumberFormatException ignored) { return null; }
    }

    static final class Station {
        final DimensionDef def;
        final Satellite body;
        final CelestialBody home;

        Station(DimensionDef def, Satellite body, CelestialBody home) {
            this.def = def;
            this.body = body;
            this.home = home;
        }
    }

    private static final class Home extends Planet {
        private final Moon moon;

        Home(Moon moon) {
            super(moon.getName());
            this.moon = moon;
        }

        @Override public int getDimensionID() { return moon.getDimensionID(); }
    }
}
