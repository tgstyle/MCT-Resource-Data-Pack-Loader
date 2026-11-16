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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class WorldgenSetup {
    public static void loadDefinitions() {
        ContentBiomes.load();
        ContentCaveRegions.load();
    }

    public static void loadGenerators() {
        ContentWorldgen.load();
    }

    public static void seamListeners() {
        MinecraftForge.EVENT_BUS.addListener(ContentSeams::onLevelTick);
    }

    public static void pregenListeners() {
        MinecraftForge.EVENT_BUS.addListener(ContentPregenDimensions::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onServerStopping);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onServerTick);
    }

    public static void pregenHoldListeners() {
        MinecraftForge.EVENT_BUS.addListener(ContentPregenHold::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentPregenHold::onLogout);
        MinecraftForge.EVENT_BUS.addListener(ContentPregenDimensions::onDimensionChange);
        MinecraftForge.EVENT_BUS.addListener(ContentPregenHold::onTravel);
        MinecraftForge.EVENT_BUS.addListener(ContentPregenHold::onTeleport);
    }

    public static void serverListeners() {
        MinecraftForge.EVENT_BUS.addListener(ContentWorldgen::onServerStarted);
    }

    public static void caveAmbience() {
        if (ContentCaveRegions.ambient()) { MinecraftForge.EVENT_BUS.addListener(ContentCaveAmbience::onPlayerTick); }
    }

    public static void generationListeners() {
        MinecraftForge.EVENT_BUS.addListener(ContentOreControl::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(ContentRetrogen::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentRetrogen::onLevelUnload);
        MinecraftForge.EVENT_BUS.addListener(ContentRetrogen::onLevelTick);
    }

    public static void worldListeners() {
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onCreateSpawn);
        MinecraftForge.EVENT_BUS.addListener(ContentCaveRegions::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentSkyLand::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentBiomes::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentCraters::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentCaveRegions::onPotentialSpawns);
    }

    public static void levelListeners() {
        MinecraftForge.EVENT_BUS.addListener(ContentPopulateControl::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentDungeons::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentOreControl::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentGeneratorControl::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onRespawn);
    }

    public static void registerTrees(IEventBus modBus) {
        ContentTreeTrunk.REGISTER.register(modBus);
        ContentTreeEvents.register(modBus);
    }

    public static void seedBiomes(ServerAboutToStartEvent event) {
        ContentListBiomes.seed(event.getServer().getWorldData().worldGenOptions().seed());
    }

    public static void reloadReplacements() {
        ContentReplacements.reload();
    }
}
