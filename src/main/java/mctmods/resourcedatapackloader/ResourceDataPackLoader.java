package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.client.BossBackgrounds;
import mctmods.resourcedatapackloader.client.CardOverlay;
import mctmods.resourcedatapackloader.client.CenterCard;
import mctmods.resourcedatapackloader.client.HoldView;
import mctmods.resourcedatapackloader.client.PackListEntries;
import mctmods.resourcedatapackloader.client.PackOptionsButton;
import mctmods.resourcedatapackloader.client.PackTooltips;
import mctmods.resourcedatapackloader.client.ProspectTooltip;
import mctmods.resourcedatapackloader.client.PouchKey;
import mctmods.resourcedatapackloader.command.ClientCommands;
import mctmods.resourcedatapackloader.command.ServerCommands;
import mctmods.resourcedatapackloader.content.ContentExposures;
import mctmods.resourcedatapackloader.content.ContentAnvils;
import mctmods.resourcedatapackloader.content.ContentBoards;
import mctmods.resourcedatapackloader.content.ContentDice;
import mctmods.resourcedatapackloader.content.ContentHarvests;
import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.ContentHardnessCheck;
import mctmods.resourcedatapackloader.content.ContentOverrides;
import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.ContentTeams;
import mctmods.resourcedatapackloader.content.block.ContentBlockBell;
import mctmods.resourcedatapackloader.content.block.ContentSpawners;
import mctmods.resourcedatapackloader.content.card.CardEvents;
import mctmods.resourcedatapackloader.content.card.CardRules;
import mctmods.resourcedatapackloader.content.card.CardScan;
import mctmods.resourcedatapackloader.content.compat.ContentBlastPlaster;
import mctmods.resourcedatapackloader.content.gui.PackGuiHandler;
import mctmods.resourcedatapackloader.content.item.ContentItemContainer;
import mctmods.resourcedatapackloader.content.tile.TileEntityPackBell;
import mctmods.resourcedatapackloader.content.tile.TileEntityPackContainer;
import mctmods.resourcedatapackloader.content.entity.ContentEntityTypes;
import mctmods.resourcedatapackloader.content.entity.ContentMobExperience;
import mctmods.resourcedatapackloader.content.entity.ContentOrders;
import mctmods.resourcedatapackloader.content.entity.ContentRanks;
import mctmods.resourcedatapackloader.content.entity.EntityStorage;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveAmbience;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionAmbience;
import mctmods.resourcedatapackloader.content.worldgen.ContentFogSampler;
import mctmods.resourcedatapackloader.content.worldgen.ContentHeatShimmer;
import mctmods.resourcedatapackloader.content.worldgen.ContentPristine;
import mctmods.resourcedatapackloader.content.worldgen.ContentProspect;
import mctmods.resourcedatapackloader.util.compat.CbmpRubicParts;
import mctmods.resourcedatapackloader.util.compat.CmsRubicSpawns;
import mctmods.resourcedatapackloader.util.compat.GcPlanetWorldgen;
import mctmods.resourcedatapackloader.util.compat.StreamsRubicValleys;
import mctmods.resourcedatapackloader.util.compat.ThutRubicLifts;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.extra.ContentFuels;
import mctmods.resourcedatapackloader.content.extra.ContentTooltips;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.extra.ContentWorldIntro;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.content.gate.GateEvents;
import mctmods.resourcedatapackloader.content.gate.VanillaPortalLink;
import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.content.village.CityGrid;
import mctmods.resourcedatapackloader.content.village.ContentPierCargo;
import mctmods.resourcedatapackloader.content.village.ContentVillages;
import mctmods.resourcedatapackloader.content.village.RecurrentVillages;
import mctmods.resourcedatapackloader.content.village.VillagePieces;
import mctmods.resourcedatapackloader.content.worldgen.ContentBedrock;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeardSeat;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentSkyRenderers;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnChunks;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentCelestial;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkSaves;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkWatch;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentEnvironments;
import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeneratorControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentGrowSwitch;
import mctmods.resourcedatapackloader.content.worldgen.ContentRuleSwitch;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.portal.ContentPortalFrames;
import mctmods.resourcedatapackloader.content.portal.ContentPortals;
import mctmods.resourcedatapackloader.content.worldgen.ContentPathIntersects;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureMaps;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityMaps;
import mctmods.resourcedatapackloader.content.worldgen.ContentPaths;
import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;
import mctmods.resourcedatapackloader.content.worldgen.ContentSeams;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregenDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregenHold;
import mctmods.resourcedatapackloader.content.worldgen.ContentReplacements;
import mctmods.resourcedatapackloader.content.worldgen.ContentRetrogen;
import mctmods.resourcedatapackloader.content.entity.ContentThreat;
import mctmods.resourcedatapackloader.content.worldgen.ContentLootSwitch;
import mctmods.resourcedatapackloader.content.worldgen.ContentMobSwitch;
import mctmods.resourcedatapackloader.content.worldgen.ContentTradeSwitch;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawnSwitch;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawning;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructurePlacement;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructures;
import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.function.FunctionOverrides;
import mctmods.resourcedatapackloader.loot.BlockDrops;
import mctmods.resourcedatapackloader.loot.LootInjections;
import mctmods.resourcedatapackloader.loot.PlayerLoot;
import mctmods.resourcedatapackloader.loot.KilledName;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.recipe.FurnaceBlocking;
import mctmods.resourcedatapackloader.recipe.FurnaceRecipes;
import mctmods.resourcedatapackloader.recipe.RecipeDisabled;
import mctmods.resourcedatapackloader.registry.RegistryRemaps;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Toasts;

import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Mod(modid = ResourceDataPackLoader.MOD_ID, name = "Resource Data Pack Loader", acceptedMinecraftVersions = "[1.12.2]", acceptableRemoteVersions = "*", dependencies = "required-after:blastplaster@[1.0.14,);after:baubles;before:jei;")
public class ResourceDataPackLoader {
    public static final String MOD_ID = "resourcedatapackloader";
    public static final String ICON = "textures/gui/icon.png";
    @Mod.Instance(MOD_ID) public static ResourceDataPackLoader INSTANCE;

    public static ResourceLocation id(String path) { return new ResourceLocation(MOD_ID, path); }

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
        MinecraftForge.EVENT_BUS.register(BlockDrops.class);
        MinecraftForge.EVENT_BUS.register(ContentHarvests.class);
        MinecraftForge.EVENT_BUS.register(PlayerLoot.class);
        LootFunctionManager.registerFunction(new KilledName.Serializer());
        LootTableList.register(ContentPierCargo.LOOT_TABLE);
        RegistryRemaps.reload();
        FurnaceRecipes.reload();
        if (ContentFuels.load()) { MinecraftForge.EVENT_BUS.register(ContentFuels.class); }
        RecurrentVillages.register();
        VillagePieces.rails();
        GameRegistry.registerTileEntity(TileEntityPackContainer.class, id("container"));
        GameRegistry.registerTileEntity(TileEntityPackBell.class, id("bell"));
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new PackGuiHandler());
        ContentPotions.registerContainers();
        ContentPotions.applyBrewing();
        ContentRegistry.resolveItemPotions();
        ContentVillagers.applyTrades();
        ContentBiomes.applyPlacement();
        ContentWorldTemplates.load();
        ContentPathIntersects.load();
        ContentStructureMaps.load();
        ContentCityMaps.load();
        ContentPortalFrames.load();
        ContentWorldIntro.load();
        RDPLNetwork.register();
        MinecraftForge.EVENT_BUS.register(Toasts.class);
        MinecraftForge.EVENT_BUS.register(ContentEnvironments.class);
        MinecraftForge.EVENT_BUS.register(ContentGrowSwitch.class);
        MinecraftForge.EVENT_BUS.register(ContentRuleSwitch.class);
        MinecraftForge.EVENT_BUS.register(ContentMobSwitch.class);
        MinecraftForge.EVENT_BUS.register(ContentTradeSwitch.class);
        if (ContentIntroPlay.enabled()) { MinecraftForge.EVENT_BUS.register(ContentIntroPlay.class); }
        ContentBiomeControl.apply();
        ContentSetup.applyFire();
        MinecraftForge.TERRAIN_GEN_BUS.register(ContentBiomes.class);
        MinecraftForge.TERRAIN_GEN_BUS.register(ContentBiomeControl.class);
        MinecraftForge.TERRAIN_GEN_BUS.register(ContentStructures.class);
        ContentSpawning.applyCaps();
        if (ContentSpawning.rateControlled()) {
            MinecraftForge.EVENT_BUS.register(ContentSpawning.class);
            MinecraftForge.EVENT_BUS.register(ContentThreat.class);
            MinecraftForge.EVENT_BUS.register(ContentSpawnSwitch.class);
        }
        if (ContentPhysics.enabled()) { MinecraftForge.EVENT_BUS.register(ContentPhysics.class); }
        if (ContentCaveAmbience.wanted()) { MinecraftForge.EVENT_BUS.register(ContentCaveAmbience.class); }
        MinecraftForge.EVENT_BUS.register(ContentProspect.class);
        if (ContentSeams.enabled()) { MinecraftForge.EVENT_BUS.register(ContentSeams.class); }
        if (ContentExposures.enabled()) { MinecraftForge.EVENT_BUS.register(ContentExposures.class); }
        if (ContentExposures.spreads()) { MinecraftForge.EVENT_BUS.register(ContentExposures.Mobs.class); }
        if (ContentBiomeControl.enabled()) { MinecraftForge.EVENT_BUS.register(ContentBiomeControl.class); }
        if (Config.worldgen.tellWorldType && !ContentTerrain.worldType().isEmpty()) { MinecraftForge.EVENT_BUS.register(ContentTerrain.class); }
        ContentDimensions.load();
        ContentGameRules.load();
        ContentTeams.load();
        ContentScoring.load();
        ContentDice.load();
        ContentBoards.load();
        if (ContentRaids.load()) { MinecraftForge.EVENT_BUS.register(ContentRaids.class); }
        if (ContentRaids.belled()) { MinecraftForge.EVENT_BUS.register(ContentBlockBell.class); }
        if (ContentEntities.load()) {
            MinecraftForge.EVENT_BUS.register(ContentEntities.class);
            MinecraftForge.EVENT_BUS.register(ContentMobExperience.class);
            MinecraftForge.EVENT_BUS.register(EntityStorage.class);
            if (ContentOrders.load()) { MinecraftForge.EVENT_BUS.register(ContentOrders.class); }
        }
        if (ContentRanks.load()) { MinecraftForge.EVENT_BUS.register(ContentRanks.class); }
        if (ContentVillages.load()) { ContentVillages.register(); }
        ContentBlastPlaster.install();
        ContentGates.load();
        if (ContentGates.enabled()) { MinecraftForge.EVENT_BUS.register(GateEvents.class); }
        CardRules.load();
        MinecraftForge.EVENT_BUS.register(CardEvents.class);
        MinecraftForge.EVENT_BUS.register(CardScan.class);
        if (!Loader.isModLoaded("universaltweaks")) { MinecraftForge.EVENT_BUS.register(VanillaPortalLink.class); }
        CmsRubicSpawns.register();
        CbmpRubicParts.register();
        StreamsRubicValleys.register();
        GcPlanetWorldgen.register();
        ThutRubicLifts.register();
        MinecraftForge.EVENT_BUS.register(ContentPaths.class);
        if (ContentGeneratorControl.enabled()) { ContentGeneratorControl.load(); }
        if (ContentOreControl.enabled()) { MinecraftForge.ORE_GEN_BUS.register(ContentOreControl.class); }
        MinecraftForge.EVENT_BUS.register(ContentVoidWorld.class);
        if (ContentBedrock.enabled()) { MinecraftForge.EVENT_BUS.register(ContentBedrock.class); }
        List<WorldgenDef> veins = Config.content.load && Config.worldgen.load ? ContentRegistry.resolveWorldgen() : Collections.emptyList();
        if (veins.isEmpty() && ContentStructureMaps.none()) { ContentRetrogen.setup(veins, null); }
        else {
            ContentWorldgen worldgen = new ContentWorldgen(veins);
            GameRegistry.registerWorldGenerator(worldgen, 3);
            ContentRetrogen.setup(veins, worldgen);
        }
        if (Config.worldgen.blockOres && veins.isEmpty()) {
            ContentLog.LOGGER.warn("blockOres is on and no pack vein survived, so nothing will generate ore at all. Check the skipped entries above");
        }
        if (ContentAnvils.load()) { MinecraftForge.EVENT_BUS.register(ContentAnvils.class); }
        if (ContentHardness.load()) {
            ContentHardness.resolve();
            MinecraftForge.EVENT_BUS.register(ContentHardness.class);
            if (ContentHardness.anySwaps()) { GameRegistry.registerWorldGenerator(new ContentHardness.Swaps(), 100); }
        }
        ContentOverrides.reload();
        ContentReplacements.reload();
        if (ContentRetrogen.wanted() || ContentHardness.anySwaps()) { MinecraftForge.EVENT_BUS.register(ContentRetrogen.class); }
        MinecraftForge.EVENT_BUS.register(ContentChunkSaves.class);
        MinecraftForge.EVENT_BUS.register(ContentPregenDimensions.class);
        MinecraftForge.EVENT_BUS.register(ContentPregenHold.class);
        MinecraftForge.EVENT_BUS.register(CityGrid.class);
        if (Config.worldgen.worldgenDebug) {
            MinecraftForge.EVENT_BUS.register(ContentEntityTicks.class);
            MinecraftForge.EVENT_BUS.register(ContentChunkWatch.class);
        }
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ClientCommands.register();
            ContentSkyRenderers.listen();
            if (Config.worldgen.worldgenDebug) {
                MinecraftForge.EVENT_BUS.register(ContentHardnessCheck.class);
                ContentHardnessCheck.watching();
            }
            MinecraftForge.EVENT_BUS.register(new PackOptionsButton.Handler());
            MinecraftForge.EVENT_BUS.register(BossBackgrounds.class);
            MinecraftForge.EVENT_BUS.register(CardOverlay.class);
            MinecraftForge.EVENT_BUS.register(CenterCard.class);
            MinecraftForge.EVENT_BUS.register(HoldView.class);
            MinecraftForge.EVENT_BUS.register(ContentFogSampler.class);
            MinecraftForge.EVENT_BUS.register(ContentHeatShimmer.class);
            MinecraftForge.EVENT_BUS.register(ContentDimensionAmbience.class);
            MinecraftForge.EVENT_BUS.register(ProspectTooltip.class);
            if (ContentTooltips.load()) { MinecraftForge.EVENT_BUS.register(PackTooltips.class); }
            MinecraftForge.EVENT_BUS.register(new PackListEntries.Handler());
            if (ContentItemContainer.baubled()) { PouchKey.register(); }
        }
    }

    @Mod.EventHandler public void interModComms(FMLInterModComms.IMCEvent event) {
        ContentDimensions.settle();
        ContentCelestial.register();
    }

    @Mod.EventHandler public void postInit(FMLPostInitializationEvent event) {
        ContentEntityTypes.registerLate();
        ContentBiomes.resolveSpawns();
    }

    @Mod.EventHandler public void loadComplete(FMLLoadCompleteEvent event) {
        RecipeDisabled.apply();
        FurnaceBlocking.apply();
        ContentSpawners.apply();
        ContentPortals.bind();
        ContentCelestial.loadComplete();
        ContentRanks.verify();
    }

    @Mod.EventHandler public void beforeServerStart(FMLServerAboutToStartEvent event) {
        ContentPristine.beforeWorldsLoad(event.getServer());
        Path root = PackManager.get().getRoot();
        if (root == null) { return; }
        PackManager.get().scan(root);
        PackManager.get().report();
        RegistryRemaps.reload();
        FurnaceRecipes.reload();
        FurnaceBlocking.apply();
        RecipeDisabled.furnace();
        ContentOverrides.reload();
        ContentReplacements.reload();
        ContentStructurePlacement.reload();
        ContentEntityTicks.reload();
        ContentVillages.reload();
        ContentBeardSeat.reload();
        ContentCityMaps.reload();
        ContentChunkSaves.reload();
        ContentSpawnChunks.reload();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) { ContentSkyRenderers.reload(); }
        CardRules.load();
        if (ContentVillages.load()) { ContentVillages.register(); }
    }

    @Mod.EventHandler public void onServerStarting(FMLServerStartingEvent event) { event.registerServerCommand(new ServerCommands()); }

    @Mod.EventHandler public void onServerStarted(FMLServerStartedEvent event) {
        ContentCelestial.serverStarted();
        ContentBoards.rebuild(FMLCommonHandler.instance().getMinecraftServerInstance());
        ContentEnvironments.serverStarted(FMLCommonHandler.instance().getMinecraftServerInstance());
        ContentMobSwitch.serverStarted(FMLCommonHandler.instance().getMinecraftServerInstance());
        ContentLootSwitch.serverStarted(FMLCommonHandler.instance().getMinecraftServerInstance());
        ContentTradeSwitch.serverStarted();
        FunctionOverrides.serverStarted(FMLCommonHandler.instance().getMinecraftServerInstance());
    }

    @Mod.EventHandler public void onServerStopping(FMLServerStoppingEvent event) { ContentPregen.serverStopping(); }

    @Mod.EventHandler public void onServerStopped(FMLServerStoppedEvent event) {
        ContentStructureSearch.forget();
        ContentEnvironments.serverStopped();
        ContentSpawnSwitch.serverStopped();
        ContentGrowSwitch.serverStopped();
        ContentMobSwitch.serverStopped();
        ContentTradeSwitch.serverStopped();
        FunctionOverrides.serverStopped();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) { return; }
        PackManager.get().close();
    }
}
