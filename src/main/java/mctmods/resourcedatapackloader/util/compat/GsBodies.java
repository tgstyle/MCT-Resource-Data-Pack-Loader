package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.def.GalaxySpaceDef;

import asmodeuscore.api.dimension.IAdvancedSpace;
import asmodeuscore.core.astronomy.BodiesData;
import asmodeuscore.core.astronomy.BodiesRegistry;
import asmodeuscore.core.astronomy.WeatherData;
import asmodeuscore.core.prefab.celestialbody.ExMoon;
import asmodeuscore.core.prefab.celestialbody.ExPlanet;
import galaxyspace.core.configs.GSConfigCore;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

final class GsBodies {
    private static final float COLDEST = -9.102F;

    private GsBodies() {}

    static List<Class<? extends GcWorldProvider>> providers() { return Arrays.asList(GsWorldProvider.class, GsRainWorldProvider.class, GsStormWorldProvider.class, GsRainStormWorldProvider.class); }

    static Class<? extends GcWorldProvider> provider(boolean rain, GalaxySpaceDef gs) {
        boolean storm = gs.weather != null;
        if (rain) { return storm ? GsRainStormWorldProvider.class : GsRainWorldProvider.class; }
        return storm ? GsStormWorldProvider.class : GsWorldProvider.class;
    }

    static Planet planet(String name, SolarSystem parent) { return new ExPlanet(name).setParentSolarSystem(parent); }

    static Moon moon(String name, Planet parent) { return new ExMoon(name).setParentPlanet(parent); }

    static void body(CelestialBody body, GalaxySpaceDef gs, long dayLength, float gravity) {
        BodiesRegistry.setPlanetData(body, gs.pressure, dayLength, gravity, gs.radiation);
        IAdvancedSpace.ClassBody bodyClass = gs.bodyClass == null ? null : IAdvancedSpace.ClassBody.valueOf(gs.bodyClass);
        if (bodyClass != null) { BodiesRegistry.setClassBody(body, bodyClass); }
        BodiesRegistry.setOrbitData(body, body.getPhaseShift(), body.getRelativeSize(), body.getRelativeOrbitTime(), gs.eccentricityX, gs.eccentricityY, gs.offsetX, gs.offsetY);
        BodiesRegistry.registerBodyData(body, new BodiesData(bodyClass, Math.round(gs.pressure), gs.radiation));
    }

    static void star(Star star, GalaxySpaceDef gs) {
        BodiesData data = new BodiesData(null, 0, false);
        if (gs.starType != null) { data.setStarType(IAdvancedSpace.StarType.valueOf(gs.starType)); }
        if (gs.starColor != null) { data.setStarColor(IAdvancedSpace.StarColor.valueOf(gs.starColor)); }
        data.setStarHabitableZone(gs.zoneDistance, gs.zoneSize);
        BodiesRegistry.registerBodyData(star, data);
    }

    static float thermal(@Nullable World world, @Nullable GalaxySpaceDef gs, float base) {
        if (world == null || gs == null || gs.thermalVariation <= 0.0F || !GSConfigCore.enableAdvancedThermalSystem) { return base; }
        float value = MathHelper.cos(world.getCelestialAngle(0.0F) * (float) Math.PI * 2.0F) * gs.thermalVariation;
        if (base == 0.0F) { return value; }
        if (base < 0.0F) { value = -value; }
        return Math.max(COLDEST, value * base + base);
    }

    static double solarWind(@Nullable GalaxySpaceDef gs, float solarSize) {
        if (gs != null && gs.solarWind >= 0.0D) { return gs.solarWind; }
        return solarSize * solarSize;
    }

    @Nullable static WeatherData weather(@Nullable GalaxySpaceDef gs) {
        if (gs == null || gs.weather == null) { return null; }
        return new WeatherData(WeatherData.WeatherType.valueOf(gs.weather), gs.weatherFrequency);
    }
}
