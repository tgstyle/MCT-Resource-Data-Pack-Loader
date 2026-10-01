package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentParserCelestial;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.CelestialDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.compat.GcCelestial;

import net.minecraftforge.fml.common.Loader;
import java.util.ArrayList;
import java.util.List;

public final class ContentCelestial {
    private static final String GALACTICRAFT = "galacticraftcore";
    private static final List<DimensionDef> BODIED = new ArrayList<>();
    private static boolean registered;

    private ContentCelestial() {}

    public static boolean orbits(DimensionDef def) { return def.galacticraft != null && Loader.isModLoaded(GALACTICRAFT); }

    public static void defer(DimensionDef def) { BODIED.add(def); }

    public static void register() {
        if (registered) { return; }
        registered = true;
        if (!Config.registersToClients() || !Config.content.dimensions) { return; }
        List<CelestialDef> bodies = new ArrayList<>();
        Json.eachFile(PackManager.CELESTIAL, "celestial body", (key, contents) -> {
            CelestialDef def = ContentParserCelestial.celestial(key, contents);
            if (def != null && ContentRegistry.available(def.requires, key)) { bodies.add(def); }
        });
        if (!Loader.isModLoaded(GALACTICRAFT)) {
            if (!bodies.isEmpty()) { ContentLog.LOGGER.info("Galacticraft is not loaded, so the {} pack star system(s) and body(ies) under celestial/ stay off any map", bodies.size()); }
            return;
        }
        if (!bodies.isEmpty() || !BODIED.isEmpty()) { GcCelestial.register(bodies, BODIED); }
    }

    public static void loadComplete() {
        if (BODIED.isEmpty() || !Loader.isModLoaded(GALACTICRAFT)) { return; }
        GcCelestial.extraPlanetsRadiation();
        GcCelestial.stationRecipes();
    }

    public static void serverStarted() {
        if (BODIED.isEmpty() || !Loader.isModLoaded(GALACTICRAFT)) { return; }
        GcCelestial.nameDimensions();
        GcCelestial.extraPlanetsRadiation();
    }
}
