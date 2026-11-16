package mctmods.resourcedatapackloader.core.registration;

import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.pregen.ContentPregenDimensions;
import mctmods.resourcedatapackloader.common.pregen.ContentPregenHold;
import mctmods.resourcedatapackloader.common.worldgen.biome.ContentBiomes;
import mctmods.resourcedatapackloader.common.worldgen.biome.ContentListBiomes;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveAmbience;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveRegions;
import mctmods.resourcedatapackloader.common.worldgen.feature.ContentOreControl;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.ContentTreeEvents;
import mctmods.resourcedatapackloader.common.worldgen.feature.tree.ContentTreeTrunk;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentGeneratorControl;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPopulateControl;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentReplacements;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentRetrogen;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentWorldgen;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.ContentDungeons;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentCraters;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentSeams;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentSkyLand;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentWorldShape;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

public class WorldgenSetup {
    public static void loadDefinitions() {
        ContentBiomes.load();
        ContentCaveRegions.load();
    }

    public static void loadGenerators() {
        ContentWorldgen.load();
    }

    public static void seamListeners() {
        NeoForge.EVENT_BUS.addListener(ContentSeams::onLevelTick);
    }

    public static void pregenListeners() {
        NeoForge.EVENT_BUS.addListener(ContentPregenDimensions::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ContentPregen::onServerStopping);
        NeoForge.EVENT_BUS.addListener(ContentPregen::onServerTick);
    }

    public static void pregenHoldListeners() {
        NeoForge.EVENT_BUS.addListener(ContentPregenHold::onLogin);
        NeoForge.EVENT_BUS.addListener(ContentPregenHold::onLogout);
        NeoForge.EVENT_BUS.addListener(ContentPregenDimensions::onDimensionChange);
        NeoForge.EVENT_BUS.addListener(ContentPregenHold::onTravel);
        NeoForge.EVENT_BUS.addListener(ContentPregenHold::onTeleport);
    }

    public static void serverListeners() {
        NeoForge.EVENT_BUS.addListener(ContentWorldgen::onServerStarted);
    }

    public static void caveAmbience() {
        if (ContentCaveRegions.ambient()) { NeoForge.EVENT_BUS.addListener(ContentCaveAmbience::onPlayerTick); }
    }

    public static void generationListeners() {
        NeoForge.EVENT_BUS.addListener(ContentOreControl::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ContentRetrogen::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(ContentRetrogen::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(ContentRetrogen::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onCreateSpawn);
        NeoForge.EVENT_BUS.addListener(ContentCaveRegions::onLevelLoad);
    }

    public static void worldListeners() {
        NeoForge.EVENT_BUS.addListener(ContentSkyLand::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentCaveRegions::onPotentialSpawns);
    }

    public static void levelListeners() {
        NeoForge.EVENT_BUS.addListener(ContentBiomes::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentCraters::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentPopulateControl::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentDungeons::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentOreControl::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentGeneratorControl::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onLogin);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onRespawn);
    }

    public static void registerTrees(IEventBus modBus) {
        ContentTreeTrunk.REGISTER.register(modBus);
        ContentTreeEvents.register();
    }

    public static void seedBiomes(ServerAboutToStartEvent event) {
        ContentListBiomes.seed(event.getServer().getWorldGenSettings().options().seed());
    }

    public static void reloadReplacements() {
        ContentReplacements.reload();
    }
}
