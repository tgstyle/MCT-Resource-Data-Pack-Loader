package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.def.CelestialDef;
import mctmods.resourcedatapackloader.util.BodyRings;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialObject;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import java.util.ArrayList;
import java.util.List;

public final class GcOverrides {
    private static final String STAR = "star.";

    private GcOverrides() {}

    public static void apply(List<CelestialDef> overrides) {
        boolean client = FMLCommonHandler.instance().getSide() == Side.CLIENT;
        boolean ringed = false;
        int changed = 0;
        for (CelestialDef def : overrides) {
            List<CelestialBody> bodies = matches(def.name);
            if (bodies.isEmpty()) {
                ContentLog.LOGGER.error("Celestial override {} names body '{}', which no registered star, planet, moon or station has, so it changes nothing", def.key, def.name);
                continue;
            }
            if (!GcCelestial.GALAXY_SPACE && def.galaxySpace != null) { ContentLog.LOGGER.error("Celestial override {} sets galaxyspace keys, but GalaxySpace is not loaded, so they do nothing", def.key); }
            for (CelestialBody body : bodies) {
                if (def.icon != null) { body.setBodyIcon(def.icon); }
                if (GcCelestial.GALAXY_SPACE && def.galaxySpace != null) { GsBodies.override(body, def.galaxySpace); }
                if (client && def.ring != null) {
                    if (body instanceof Planet || body instanceof Moon) {
                        BodyRings.register(body.getName(), def.ring, def.ringSize);
                        ringed = true;
                    }
                    else { ContentLog.LOGGER.error("Celestial override {} sets a ring on {}, which is neither a planet nor a moon, ignoring it", def.key, body.getTranslationKey()); }
                }
                changed++;
            }
        }
        if (ringed) { GcRings.hook(); }
        if (changed > 0) { Summary.info("galacticraft", "Changed " + changed + " existing body(ies) through " + overrides.size() + " celestial override(s)"); }
    }

    private static List<CelestialBody> matches(String translationKey) {
        List<CelestialBody> found = new ArrayList<>();
        if (translationKey.startsWith(STAR)) {
            for (SolarSystem system : GalaxyRegistry.getSolarSystems()) {
                Star star = system.getMainStar();
                if (star != null && translationKey.equalsIgnoreCase(star.getTranslationKey())) { found.add(star); }
            }
            return found;
        }
        for (CelestialObject object : GalaxyRegistry.getAllRegisteredObjects()) {
            if (object instanceof CelestialBody && translationKey.equalsIgnoreCase(object.getTranslationKey())) { found.add((CelestialBody) object); }
        }
        return found;
    }
}
