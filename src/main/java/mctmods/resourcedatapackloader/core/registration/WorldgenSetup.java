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
        ModSetup.registerIf(MinecraftForge.EVENT_BUS, ContentCaveAmbience.wanted(), ContentCaveAmbience.class);
    }

    public static void seams() {
        ModSetup.registerIf(MinecraftForge.EVENT_BUS, ContentSeams.enabled(), ContentSeams.class);
    }

    public static void biomeListeners() {
        ModSetup.registerIf(MinecraftForge.EVENT_BUS, ContentBiomeControl.enabled(), ContentBiomeControl.class);
    }

    public static void worldTypeListeners() {
        ModSetup.registerIf(MinecraftForge.EVENT_BUS, Config.worldgen.tellWorldType && !ContentTerrain.worldType().isEmpty(), ContentTerrain.class);
    }

    public static void planets() {
        GcPlanetWorldgen.register();
    }

    public static void generators() {
        if (ContentGeneratorControl.enabled()) { ContentGeneratorControl.load(); }
        ModSetup.registerIf(MinecraftForge.ORE_GEN_BUS, ContentOreControl.enabled(), ContentOreControl.class);
        MinecraftForge.EVENT_BUS.register(ContentVoidWorld.class);
        ModSetup.registerIf(MinecraftForge.EVENT_BUS, ContentBedrock.enabled(), ContentBedrock.class);
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
        ModSetup.registerIf(MinecraftForge.EVENT_BUS, ContentRetrogen.wanted() || ContentHardness.anySwaps(), ContentRetrogen.class);
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
