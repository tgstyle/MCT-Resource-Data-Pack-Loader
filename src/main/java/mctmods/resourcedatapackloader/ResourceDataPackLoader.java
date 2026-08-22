package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.command.ClientCommands;
import mctmods.resourcedatapackloader.command.ServerCommands;
import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.ContentHardnessCheck;
import mctmods.resourcedatapackloader.content.ContentOverrides;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.block.ContentSpawners;
import mctmods.resourcedatapackloader.content.compat.ContentBlastPlaster;
import mctmods.resourcedatapackloader.util.compat.CbmpRubicParts;
import mctmods.resourcedatapackloader.util.compat.CmsRubicSpawns;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.extra.ContentFuels;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.extra.ContentWorldIntro;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.content.gate.GateEvents;
import mctmods.resourcedatapackloader.content.gate.VanillaPortalLink;
import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.village.RecurrentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentBedrock;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkSaves;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeneratorControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentPathIntersects;
import mctmods.resourcedatapackloader.content.worldgen.ContentPaths;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentReplacements;
import mctmods.resourcedatapackloader.content.worldgen.ContentRetrogen;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawning;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.loot.LootInjections;
import mctmods.resourcedatapackloader.loot.PlayerLoot;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.recipe.FurnaceBlocking;
import mctmods.resourcedatapackloader.recipe.FurnaceRecipes;
import mctmods.resourcedatapackloader.registry.RegistryRemaps;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Mod(modid = ResourceDataPackLoader.MOD_ID, name = "Resource Data Pack Loader", acceptedMinecraftVersions = "[1.12.2]", acceptableRemoteVersions = "*", dependencies = "required-after:blastplaster@[1.0.6,);")
public class ResourceDataPackLoader {
    public static final String MOD_ID = "resourcedatapackloader";

    static {
        try {
            if (ContentRegistry.wantsBuckets()) { FluidRegistry.enableUniversalBucket(); }
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Could not work out whether any pack wants a bucket, so the universal bucket stays off. Fluids and their blocks still load", ex);
        }
    }

    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) { Rubic.preInit(); }

    @Mod.EventHandler public void init(FMLInitializationEvent event) {
        Rubic.init();
        MinecraftForge.EVENT_BUS.register(RegistryRemaps.class);
        MinecraftForge.EVENT_BUS.register(LootInjections.class);
        MinecraftForge.EVENT_BUS.register(PlayerLoot.class);
        RegistryRemaps.reload();
        FurnaceRecipes.reload();
        if (ContentFuels.load()) { MinecraftForge.EVENT_BUS.register(ContentFuels.class); }
        RecurrentVillages.register();
        ContentPotions.registerContainers();
        ContentPotions.applyBrewing();
        ContentVillagers.applyTrades();
        ContentBiomes.applyPlacement();
        ContentWorldTemplates.load();
        ContentPathIntersects.load();
        ContentWorldIntro.load();
        RDPLNetwork.register();
        if (ContentIntroPlay.enabled()) { MinecraftForge.EVENT_BUS.register(ContentIntroPlay.class); }
        ContentBiomeControl.apply();
        ContentSetup.applyFire();
        MinecraftForge.TERRAIN_GEN_BUS.register(ContentBiomes.class);
        MinecraftForge.TERRAIN_GEN_BUS.register(ContentBiomeControl.class);
        MinecraftForge.TERRAIN_GEN_BUS.register(ContentStructures.class);
        ContentSpawning.applyCaps();
        if (ContentSpawning.rateControlled()) { MinecraftForge.EVENT_BUS.register(ContentSpawning.class); }
        if (ContentBiomeControl.enabled()) { MinecraftForge.EVENT_BUS.register(ContentBiomeControl.class); }
        if (Config.worldgen.tellWorldType && !ContentTerrain.worldType().isEmpty()) { MinecraftForge.EVENT_BUS.register(ContentTerrain.class); }
        ContentDimensions.load();
        ContentGameRules.load();
        if (ContentEntities.load()) { MinecraftForge.EVENT_BUS.register(ContentEntities.class); }
        if (ContentVillages.load()) { ContentVillages.register(); }
        ContentBlastPlaster.install();
        ContentGates.load();
        if (ContentGates.enabled()) { MinecraftForge.EVENT_BUS.register(GateEvents.class); }
        if (!Loader.isModLoaded("universaltweaks")) { MinecraftForge.EVENT_BUS.register(VanillaPortalLink.class); }
        CmsRubicSpawns.register();
        CbmpRubicParts.register();
        if (ContentPaths.enabled()) { MinecraftForge.EVENT_BUS.register(ContentPaths.class); }
        if (ContentGeneratorControl.enabled()) { ContentGeneratorControl.load(); }
        if (ContentOreControl.enabled()) { MinecraftForge.ORE_GEN_BUS.register(ContentOreControl.class); }
        MinecraftForge.EVENT_BUS.register(ContentVoidWorld.class);
        if (ContentBedrock.enabled()) { MinecraftForge.EVENT_BUS.register(ContentBedrock.class); }
        List<WorldgenDef> veins = Config.content.load && Config.worldgen.load ? ContentRegistry.resolveWorldgen() : Collections.emptyList();
        if (veins.isEmpty()) { ContentRetrogen.setup(veins, null); }
        else {
            ContentWorldgen worldgen = new ContentWorldgen(veins);
            GameRegistry.registerWorldGenerator(worldgen, 3);
            ContentRetrogen.setup(veins, worldgen);
        }
        if (Config.worldgen.blockOres && veins.isEmpty()) {
            ContentLog.LOGGER.warn("blockOres is on and no pack vein survived, so nothing will generate ore at all. Check the skipped entries above");
        }
        if (ContentHardness.load()) {
            ContentHardness.resolve();
            MinecraftForge.EVENT_BUS.register(ContentHardness.class);
        }
        ContentOverrides.reload();
        ContentReplacements.reload();
        if (ContentRetrogen.wanted()) { MinecraftForge.EVENT_BUS.register(ContentRetrogen.class); }
        MinecraftForge.EVENT_BUS.register(ContentChunkSaves.class);
        MinecraftForge.EVENT_BUS.register(ContentPregen.class);
        if (Config.worldgen.worldgenDebug) {
            MinecraftForge.EVENT_BUS.register(ContentEntityTicks.class);
            MinecraftForge.EVENT_BUS.register(ContentChunkWatch.class);
        }
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ClientCommands.register();
            if (Config.worldgen.worldgenDebug) {
                MinecraftForge.EVENT_BUS.register(ContentHardnessCheck.class);
                ContentHardnessCheck.watching();
            }
            MinecraftForge.EVENT_BUS.register(new mctmods.resourcedatapackloader.client.PackOptionsButton.Handler());
            MinecraftForge.EVENT_BUS.register(new mctmods.resourcedatapackloader.client.PackListEntries.Handler());
        }
    }

    @Mod.EventHandler public void loadComplete(FMLLoadCompleteEvent event) {
        FurnaceBlocking.apply();
        ContentSpawners.apply();
    }

    @Mod.EventHandler public void beforeServerStart(FMLServerAboutToStartEvent event) {
        Path root = PackManager.get().getRoot();
        if (root == null) { return; }
        PackManager.get().scan(root);
        PackManager.get().report();
        RegistryRemaps.reload();
        FurnaceRecipes.reload();
        FurnaceBlocking.apply();
        ContentOverrides.reload();
        ContentReplacements.reload();
        ContentStructurePlacement.reload();
        ContentEntityTicks.reload();
        ContentVillages.reload();
    }

    @Mod.EventHandler public void onServerStarting(FMLServerStartingEvent event) { event.registerServerCommand(new ServerCommands()); }

    @Mod.EventHandler public void onServerStopping(FMLServerStoppingEvent event) { ContentPregen.serverStopping(); }

    @Mod.EventHandler public void onServerStopped(FMLServerStoppedEvent event) {
        ContentStructureSearch.forget();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) { return; }
        PackManager.get().close();
    }
}
