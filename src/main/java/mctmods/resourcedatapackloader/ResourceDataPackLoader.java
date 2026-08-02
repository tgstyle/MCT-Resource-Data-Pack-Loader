package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.command.ClientCommands;
import mctmods.resourcedatapackloader.command.ServerCommands;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.content.extra.ContentFuels;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.content.gate.GateEvents;
import mctmods.resourcedatapackloader.content.gate.VanillaPortalLink;
import mctmods.resourcedatapackloader.loot.LootInjections;
import mctmods.resourcedatapackloader.content.worldgen.ContentBedrock;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeneratorControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentPaths;
import mctmods.resourcedatapackloader.content.worldgen.ContentReplacements;
import mctmods.resourcedatapackloader.content.worldgen.ContentRetrogen;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawning;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
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
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Mod(modid = ResourceDataPackLoader.MOD_ID, name = "Resource Data Pack Loader", acceptedMinecraftVersions = "[1.12.2]", acceptableRemoteVersions = "*")
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

    @Mod.EventHandler public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(RegistryRemaps.class);
        MinecraftForge.EVENT_BUS.register(LootInjections.class);
        RegistryRemaps.reload();
        FurnaceRecipes.reload();
        if (ContentFuels.load()) { MinecraftForge.EVENT_BUS.register(ContentFuels.class); }
        ContentPotions.registerContainers();
        ContentPotions.applyBrewing();
        ContentVillagers.applyTrades();
        ContentBiomes.applyPlacement();
        ContentWorldTemplates.load();
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
        ContentGates.load();
        if (ContentGates.enabled()) { MinecraftForge.EVENT_BUS.register(GateEvents.class); }
        if (!Loader.isModLoaded("universaltweaks")) { MinecraftForge.EVENT_BUS.register(VanillaPortalLink.class); }
        if (ContentPaths.enabled()) { MinecraftForge.EVENT_BUS.register(ContentPaths.class); }
        if (ContentGeneratorControl.enabled()) { ContentGeneratorControl.load(); }
        if (ContentOreControl.enabled()) { MinecraftForge.ORE_GEN_BUS.register(ContentOreControl.class); }
        if (ContentVoidWorld.enabled()) { MinecraftForge.EVENT_BUS.register(ContentVoidWorld.class); }
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
        ContentReplacements.reload();
        if (ContentRetrogen.wanted()) { MinecraftForge.EVENT_BUS.register(ContentRetrogen.class); }
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) { ClientCommands.register(); }
    }

    @Mod.EventHandler public void loadComplete(FMLLoadCompleteEvent event) { FurnaceBlocking.apply(); }

    @Mod.EventHandler public void beforeServerStart(FMLServerAboutToStartEvent event) {
        Path root = PackManager.get().getRoot();
        if (root == null) { return; }

        PackManager.get().scan(root);
        PackManager.get().report();
        RegistryRemaps.reload();
        FurnaceRecipes.reload();
        FurnaceBlocking.apply();
        ContentReplacements.reload();
        ContentStructurePlacement.reload();
    }

    @Mod.EventHandler public void onServerStarting(FMLServerStartingEvent event) { event.registerServerCommand(new ServerCommands()); }

    @Mod.EventHandler public void onServerStopped(FMLServerStoppedEvent event) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) { return; }
        PackManager.get().close();
    }
}
