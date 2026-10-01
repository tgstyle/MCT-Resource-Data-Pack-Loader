package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class GalacticraftDef {
    public static final String LANDER = "lander";
    public static final String PARACHUTE = "parachute";
    public static final String BALLOONS = "balloons";
    public static final String DEPARTURE = "departure";
    public static final String SPAWN = "spawn";
    public final CelestialDef body;
    public final boolean reachable;
    public final int minTier;
    public final String landing;
    public final double landingHeight;
    public final String arrival;
    public final double exitHeight;
    @Nullable public final ResourceLocation rocketGui;
    public final List<String> checklist;
    @Nullable public final Boolean breathable;
    public final List<String> gases;
    public final boolean corrosive;
    public final float temperature;
    public final float wind;
    public final float density;
    public final double meteorFrequency;
    public final double fuelMultiplier;
    public final float soundReduction;
    public final double solarEnergy;
    public final boolean netherPortals;
    public final int dungeonSpacing;
    @Nullable public final ResourceLocation dungeonChest;
    @Nullable public final Rain rain;
    @Nullable public final ExtraPlanetsDef extraPlanets;
    public final Map<String, Integer> stationRecipe;
    public final boolean showName;

    public GalacticraftDef(CelestialDef body, boolean reachable, int minTier, String landing, double landingHeight, String arrival, double exitHeight, @Nullable ResourceLocation rocketGui, List<String> checklist, @Nullable Boolean breathable, List<String> gases, boolean corrosive, float temperature, float wind, float density, double meteorFrequency, double fuelMultiplier, float soundReduction, double solarEnergy, boolean netherPortals, int dungeonSpacing, @Nullable ResourceLocation dungeonChest, @Nullable Rain rain, @Nullable ExtraPlanetsDef extraPlanets, Map<String, Integer> stationRecipe, boolean showName) {
        this.body = body;
        this.reachable = reachable;
        this.minTier = minTier;
        this.landing = landing;
        this.landingHeight = landingHeight;
        this.arrival = arrival;
        this.exitHeight = exitHeight;
        this.rocketGui = rocketGui;
        this.checklist = checklist;
        this.breathable = breathable;
        this.gases = gases;
        this.corrosive = corrosive;
        this.temperature = temperature;
        this.wind = wind;
        this.density = density;
        this.meteorFrequency = meteorFrequency;
        this.fuelMultiplier = fuelMultiplier;
        this.soundReduction = soundReduction;
        this.solarEnergy = solarEnergy;
        this.netherPortals = netherPortals;
        this.dungeonSpacing = dungeonSpacing;
        this.dungeonChest = dungeonChest;
        this.rain = rain;
        this.extraPlanets = extraPlanets;
        this.stationRecipe = stationRecipe;
        this.showName = showName;
    }

    public static final class Rain {
        public final String particle;
        public final ResourceLocation sound;
        public final float volume;
        public final int interval;

        public Rain(String particle, ResourceLocation sound, float volume, int interval) {
            this.particle = particle;
            this.sound = sound;
            this.volume = volume;
            this.interval = interval;
        }
    }
}
