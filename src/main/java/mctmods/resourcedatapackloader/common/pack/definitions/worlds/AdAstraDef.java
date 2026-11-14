package mctmods.resourcedatapackloader.common.pack.definitions.worlds;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.CelestialDef;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record AdAstraDef(String kind, String parent, boolean reachable, int tier, boolean breathable, short temperature, boolean corrosive, int solarPower, int exitHeight, int landingHeight, double fuelMultiplier, List<ResourceLocation> launchFrom, @Nullable ResourceLocation system, @Nullable CelestialDef map, Map<String, Integer> recipe, ResourceLocation structure) {
    public static final String PLANET = "planet";
    public static final String MOON = "moon";
    public static final String STATION = "station";
    public static final String ASTEROIDS = "asteroids";
    public static final String SOL = "sol";
    public static final int UNSET = -1;
    public static final ResourceLocation SOLAR_SYSTEM = ResourceLocation.fromNamespaceAndPath("ad_astra", "solar_system");
    public static final ResourceLocation STATION_STRUCTURE = ResourceLocation.fromNamespaceAndPath("ad_astra", "space_station");

    public boolean isMoon() { return MOON.equals(kind); }

    public boolean isStation() { return STATION.equals(kind); }
}
