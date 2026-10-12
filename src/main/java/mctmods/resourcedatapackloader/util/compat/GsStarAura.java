package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.def.SkyLookDef;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import java.util.IdentityHashMap;
import java.util.Map;

public final class GsStarAura {
    private static final Map<CelestialBody, Integer> COLORS = new IdentityHashMap<>();

    private GsStarAura() {}

    public static void register(CelestialBody body, int rgb) {
        if (rgb != SkyLookDef.UNSET) { COLORS.put(body, rgb); }
    }

    public static int color(CelestialBody body) { return COLORS.getOrDefault(body, SkyLookDef.UNSET); }
}
