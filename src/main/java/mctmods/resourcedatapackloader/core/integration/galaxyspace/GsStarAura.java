package mctmods.resourcedatapackloader.core.integration.galaxyspace;

import mctmods.resourcedatapackloader.common.pack.definitions.sky.SkyLookDef;

import java.util.IdentityHashMap;
import java.util.Map;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;

public final class GsStarAura {
    private static final Map<CelestialBody, Integer> COLORS = new IdentityHashMap<>();

    private GsStarAura() {}

    public static void register(CelestialBody body, int rgb) {
        if (rgb != SkyLookDef.UNSET) { COLORS.put(body, rgb); }
    }

    public static int color(CelestialBody body) { return COLORS.getOrDefault(body, SkyLookDef.UNSET); }
}
