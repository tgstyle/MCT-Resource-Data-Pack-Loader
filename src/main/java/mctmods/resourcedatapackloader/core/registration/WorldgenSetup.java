package mctmods.resourcedatapackloader.core.registration;

import mctmods.resourcedatapackloader.common.hardness.ContentHardness;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.WorldgenDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.pregen.ContentPregenDimensions;
import mctmods.resourcedatapackloader.common.pregen.ContentPregenHold;
import mctmods.resourcedatapackloader.common.worldgen.biome.ContentBiomeControl;
import mctmods.resourcedatapackloader.common.worldgen.biome.ContentBiomes;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveAmbience;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveRegions;
import mctmods.resourcedatapackloader.common.worldgen.feature.ContentOreControl;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentGeneratorControl;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentReplacements;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentRetrogen;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentWorldgen;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeons;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentBedrock;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentSeams;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentTerrain;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentVoidWorld;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.integration.galacticraftcore.GcPlanetWorldgen;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collections;
import java.util.List;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class WorldgenSetup {
    public static void applyPlacement() {
        ContentBiomes.applyPlacement();
    }

    public static void applyBiomeControl() {
        ContentBiomeControl.apply();
    }

    public static void terrainListeners() {
        MinecraftForge.TERRAIN_GEN_BUS.register(ContentBiomes.class);
        MinecraftForge.TERRAIN_GEN_BUS.register(ContentBiomeControl.class);
    }

    public static void caveAmbience() {
        if (ContentCaveAmbience.wanted()) { MinecraftForge.EVENT_BUS.register(ContentCaveAmbience.class); }
    }

    public static void seams() {
        if (ContentSeams.enabled()) { MinecraftForge.EVENT_BUS.register(ContentSeams.class); }
    }

    public static void biomeListeners() {
        if (ContentBiomeControl.enabled()) { MinecraftForge.EVENT_BUS.register(ContentBiomeControl.class); }
    }

    public static void worldTypeListeners() {
        if (Config.worldgen.tellWorldType && !ContentTerrain.worldType().isEmpty()) { MinecraftForge.EVENT_BUS.register(ContentTerrain.class); }
    }

    public static void planets() {
        GcPlanetWorldgen.register();
    }

    public static void generators() {
        if (ContentGeneratorControl.enabled()) { ContentGeneratorControl.load(); }
        if (ContentOreControl.enabled()) { MinecraftForge.ORE_GEN_BUS.register(ContentOreControl.class); }
        MinecraftForge.EVENT_BUS.register(ContentVoidWorld.class);
        if (ContentBedrock.enabled()) { MinecraftForge.EVENT_BUS.register(ContentBedrock.class); }
        List<WorldgenDef> veins = Config.content.load && Config.worldgen.load ? ContentRegistry.resolveWorldgen() : Collections.emptyList();
        if (veins.isEmpty() && ContentStructureMaps.none() && ContentDungeons.none() && !ContentCaveRegions.any()) { ContentRetrogen.setup(veins, null); }
        else {
            ContentWorldgen worldgen = new ContentWorldgen(veins);
            GameRegistry.registerWorldGenerator(worldgen, 3);
            ContentRetrogen.setup(veins, worldgen);
        }
        if (Config.worldgen.blockOres && veins.isEmpty()) { ContentLog.LOGGER.warn("blockOres is on and no pack vein survived, so nothing will generate ore at all. Check the skipped entries above"); }
    }

    public static void replacements() {
        ContentReplacements.reload();
        if (ContentRetrogen.wanted() || ContentHardness.anySwaps()) { MinecraftForge.EVENT_BUS.register(ContentRetrogen.class); }
    }

    public static void pregenListeners() {
        MinecraftForge.EVENT_BUS.register(ContentPregenDimensions.class);
        MinecraftForge.EVENT_BUS.register(ContentPregenHold.class);
    }

    public static void serverStopping() {
        ContentPregen.serverStopping();
    }

    public static void reloadReplacements() {
        ContentReplacements.reload();
    }

    public static void resolveSpawns() {
        ContentBiomes.resolveSpawns();
    }
}
