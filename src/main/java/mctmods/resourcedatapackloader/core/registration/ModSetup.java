package mctmods.resourcedatapackloader.core.registration;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.client.ClientProxy;
import mctmods.resourcedatapackloader.common.affixes.ContentAffixes;
import mctmods.resourcedatapackloader.common.anvils.AnvilCosts;
import mctmods.resourcedatapackloader.common.anvils.ContentAnvils;
import mctmods.resourcedatapackloader.common.blocks.ContentOverrides;
import mctmods.resourcedatapackloader.common.blocks.ContentSetup;
import mctmods.resourcedatapackloader.common.blocks.ContentSpawners;
import mctmods.resourcedatapackloader.common.blocks.ContentVeins;
import mctmods.resourcedatapackloader.common.blocks.compress.ContentCompressables;
import mctmods.resourcedatapackloader.common.blocks.bell.ContentBellBlock;
import mctmods.resourcedatapackloader.common.blocks.bell.logic.TileEntityPackBell;
import mctmods.resourcedatapackloader.common.blocks.container.logic.TileEntityPackContainer;
import mctmods.resourcedatapackloader.common.blocks.fluid.FluidReactionQueue;
import mctmods.resourcedatapackloader.common.blocks.fluid.VanillaFluidRules;
import mctmods.resourcedatapackloader.common.blocks.portal.logic.TileEntityPortway;
import mctmods.resourcedatapackloader.common.board.ContentBoards;
import mctmods.resourcedatapackloader.common.board.ContentDice;
import mctmods.resourcedatapackloader.common.cards.CardEvents;
import mctmods.resourcedatapackloader.common.cards.CardRules;
import mctmods.resourcedatapackloader.common.cards.CardScan;
import mctmods.resourcedatapackloader.common.carry.ContentCarry;
import mctmods.resourcedatapackloader.common.command.ServerCommands;
import mctmods.resourcedatapackloader.common.containers.ContentStorageSwap;
import mctmods.resourcedatapackloader.common.dialogs.ContentDialogs;
import mctmods.resourcedatapackloader.common.dialogs.DialogEvents;
import mctmods.resourcedatapackloader.common.diet.ContentDiet;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.dimension.ContentWorldTemplates;
import mctmods.resourcedatapackloader.common.enchantments.ContentEnchanting;
import mctmods.resourcedatapackloader.common.entities.ContentEntities;
import mctmods.resourcedatapackloader.common.entities.ContentEntityTypes;
import mctmods.resourcedatapackloader.common.entities.EntityStorage;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentEntityTicks;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentMobExperience;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentRanks;
import mctmods.resourcedatapackloader.common.entities.behavior.ContentThreat;
import mctmods.resourcedatapackloader.common.entities.orders.ContentOrders;
import mctmods.resourcedatapackloader.common.entities.spawning.SpawnLocks;
import mctmods.resourcedatapackloader.common.environment.ContentCelestial;
import mctmods.resourcedatapackloader.common.environment.ContentPhysics;
import mctmods.resourcedatapackloader.common.exposures.ContentExposures;
import mctmods.resourcedatapackloader.common.fuels.ContentFuels;
import mctmods.resourcedatapackloader.common.functions.FunctionOverrides;
import mctmods.resourcedatapackloader.common.gates.ContentGates;
import mctmods.resourcedatapackloader.common.gates.GateEvents;
import mctmods.resourcedatapackloader.common.gates.GateSync;
import mctmods.resourcedatapackloader.common.gates.VanillaPortalLink;
import mctmods.resourcedatapackloader.common.gui.PackGuiHandler;
import mctmods.resourcedatapackloader.common.hardness.ContentHardness;
import mctmods.resourcedatapackloader.common.hardness.ContentHarvests;
import mctmods.resourcedatapackloader.common.hearts.ContentHearts;
import mctmods.resourcedatapackloader.common.intro.ContentIntroPlay;
import mctmods.resourcedatapackloader.common.intro.ContentWorldIntro;
import mctmods.resourcedatapackloader.common.itemlocks.ContentItemLocks;
import mctmods.resourcedatapackloader.common.itemlocks.ItemLockSync;
import mctmods.resourcedatapackloader.common.items.ContentWeapons;
import mctmods.resourcedatapackloader.common.items.ContentWorn;
import mctmods.resourcedatapackloader.common.locate.ContentProspect;
import mctmods.resourcedatapackloader.common.locate.ContentStructureSearch;
import mctmods.resourcedatapackloader.common.loot.BlockDrops;
import mctmods.resourcedatapackloader.common.loot.KilledName;
import mctmods.resourcedatapackloader.common.loot.LootInjections;
import mctmods.resourcedatapackloader.common.loot.PlayerLoot;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.WorldgenDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.ContentServer;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.portals.ContentPortalFrames;
import mctmods.resourcedatapackloader.common.portals.ContentPortals;
import mctmods.resourcedatapackloader.common.potions.ContentPotions;
import mctmods.resourcedatapackloader.common.pregen.ContentChunkSaves;
import mctmods.resourcedatapackloader.common.pregen.ContentChunkWatch;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.pregen.ContentPregenDimensions;
import mctmods.resourcedatapackloader.common.pregen.ContentPregenHold;
import mctmods.resourcedatapackloader.common.pregen.ContentPristine;
import mctmods.resourcedatapackloader.common.raids.ContentRaids;
import mctmods.resourcedatapackloader.common.recipes.FurnaceBlocking;
import mctmods.resourcedatapackloader.common.recipes.FurnaceRecipes;
import mctmods.resourcedatapackloader.common.recipes.RecipeDisabled;
import mctmods.resourcedatapackloader.common.rubic.Rubic;
import mctmods.resourcedatapackloader.common.rubic.server.chunkio.CubicChunksWorld;
import mctmods.resourcedatapackloader.common.rules.ContentGameRules;
import mctmods.resourcedatapackloader.common.rules.ContentGrowSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentLootSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentMobSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentRuleSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentSpawnChunks;
import mctmods.resourcedatapackloader.common.rules.ContentSpawnSwitch;
import mctmods.resourcedatapackloader.common.rules.ContentSpawning;
import mctmods.resourcedatapackloader.common.rules.ContentTradeSwitch;
import mctmods.resourcedatapackloader.common.scoring.ContentScoring;
import mctmods.resourcedatapackloader.common.scoring.ContentTeams;
import mctmods.resourcedatapackloader.common.sleeping.ContentSleepingBags;
import mctmods.resourcedatapackloader.common.trades.ContentTradeGate;
import mctmods.resourcedatapackloader.common.trades.ContentVillagers;
import mctmods.resourcedatapackloader.common.util.messages.Toasts;
import mctmods.resourcedatapackloader.common.villages.CityGrid;
import mctmods.resourcedatapackloader.common.villages.ContentPathIntersects;
import mctmods.resourcedatapackloader.common.villages.ContentPaths;
import mctmods.resourcedatapackloader.common.villages.ContentPierCargo;
import mctmods.resourcedatapackloader.common.villages.ContentVillages;
import mctmods.resourcedatapackloader.common.villages.RecurrentVillages;
import mctmods.resourcedatapackloader.common.villages.VillagePieces;
import mctmods.resourcedatapackloader.common.weights.ContentWeights;
import mctmods.resourcedatapackloader.common.weights.PlayerWeights;
import mctmods.resourcedatapackloader.common.weights.WeightEnforce;
import mctmods.resourcedatapackloader.common.weights.WeightSync;
import mctmods.resourcedatapackloader.common.worldgen.biome.ContentBiomeControl;
import mctmods.resourcedatapackloader.common.worldgen.biome.ContentBiomes;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveAmbience;
import mctmods.resourcedatapackloader.common.worldgen.feature.ContentOreControl;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentGeneratorControl;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentReplacements;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentRetrogen;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentStructurePlacement;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentWorldgen;
import mctmods.resourcedatapackloader.common.worldgen.structure.ContentStructures;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.dress.ContentBeardSeat;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentCityMaps;
import mctmods.resourcedatapackloader.common.worldgen.structure.map.ContentStructureMaps;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentBedrock;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentSeams;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentTerrain;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentVoidWorld;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.integration.blastplaster.ContentBlastPlaster;
import mctmods.resourcedatapackloader.core.integration.forgemultipartcbe.CbmpRubicParts;
import mctmods.resourcedatapackloader.core.integration.galacticraftcore.GcPlanetWorldgen;
import mctmods.resourcedatapackloader.core.integration.streams.StreamsRubicValleys;
import mctmods.resourcedatapackloader.core.integration.thuttech.ThutRubicLifts;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ModSetup {
    public static void buckets() {
        try {
            if (ContentRegistry.wantsBuckets()) { FluidRegistry.enableUniversalBucket(); }
        }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Could not work out whether any pack wants a bucket, so the universal bucket stays off. Fluids and their blocks still load", ex);
        }
    }

    public static void preInit() { Rubic.preInit(); }

    public static void init() {
        Rubic.init();
        MinecraftForge.EVENT_BUS.register(RegistryRemaps.class);
        MinecraftForge.EVENT_BUS.register(LootInjections.class);
        MinecraftForge.EVENT_BUS.register(BlockDrops.class);
        MinecraftForge.EVENT_BUS.register(ContentHarvests.class);
        MinecraftForge.EVENT_BUS.register(ContentVeins.class);
        MinecraftForge.EVENT_BUS.register(ContentCarry.class);
        MinecraftForge.EVENT_BUS.register(PlayerWeights.class);
        MinecraftForge.EVENT_BUS.register(WeightEnforce.class);
        MinecraftForge.EVENT_BUS.register(WeightSync.class);
        MinecraftForge.EVENT_BUS.register(ContentStorageSwap.class);
        MinecraftForge.EVENT_BUS.register(PlayerLoot.class);
        LootFunctionManager.registerFunction(new KilledName.Serializer());
        LootTableList.register(ContentPierCargo.LOOT_TABLE);
        RegistryRemaps.reload();
        FurnaceRecipes.reload();
        if (ContentFuels.load()) { MinecraftForge.EVENT_BUS.register(ContentFuels.class); }
        RecurrentVillages.register();
        VillagePieces.rails();
        GameRegistry.registerTileEntity(TileEntityPackContainer.class, ResourceDataPackLoader.id("container"));
        GameRegistry.registerTileEntity(TileEntityPackBell.class, ResourceDataPackLoader.id("bell"));
        GameRegistry.registerTileEntity(TileEntityPortway.class, ResourceDataPackLoader.id("portway"));
        NetworkRegistry.INSTANCE.registerGuiHandler(ResourceDataPackLoader.INSTANCE, new PackGuiHandler());
        ContentPotions.registerContainers();
        ContentPotions.applyBrewing();
        ContentRegistry.resolveItemPotions();
        ContentVillagers.applyTrades();
        if (ContentTradeGate.active()) { MinecraftForge.EVENT_BUS.register(ContentTradeGate.class); }
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
        MinecraftForge.EVENT_BUS.register(AnvilCosts.class);
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
        VanillaFluidRules.load();
        if (VanillaFluidRules.wanted()) { MinecraftForge.EVENT_BUS.register(VanillaFluidRules.class); }
        if (FluidReactionQueue.wanted()) { MinecraftForge.EVENT_BUS.register(FluidReactionQueue.class); }
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
        if (ContentRaids.belled()) { MinecraftForge.EVENT_BUS.register(ContentBellBlock.class); }
        SpawnLocks.load();
        ContentDiet.load();
        MinecraftForge.EVENT_BUS.register(ContentDiet.class);
        if (ContentEntities.load()) {
            MinecraftForge.EVENT_BUS.register(ContentEntities.class);
            MinecraftForge.EVENT_BUS.register(ContentMobExperience.class);
            MinecraftForge.EVENT_BUS.register(EntityStorage.class);
            if (ContentOrders.load()) { MinecraftForge.EVENT_BUS.register(ContentOrders.class); }
        }
        if (ContentRanks.load()) { MinecraftForge.EVENT_BUS.register(ContentRanks.class); }
        ContentHearts.load();
        MinecraftForge.EVENT_BUS.register(ContentHearts.class);
        if (ContentVillages.load()) { ContentVillages.register(); }
        ContentBlastPlaster.install();
        ContentGates.load();
        if (ContentGates.enabled()) { MinecraftForge.EVENT_BUS.register(GateEvents.class); }
        MinecraftForge.EVENT_BUS.register(GateSync.class);
        MinecraftForge.EVENT_BUS.register(ItemLockSync.class);
        MinecraftForge.EVENT_BUS.register(ContentSleepingBags.class);
        CardRules.load();
        ContentDialogs.load();
        MinecraftForge.EVENT_BUS.register(DialogEvents.class);
        MinecraftForge.EVENT_BUS.register(CardEvents.class);
        MinecraftForge.EVENT_BUS.register(CardScan.class);
        if (!Loader.isModLoaded("universaltweaks")) { MinecraftForge.EVENT_BUS.register(VanillaPortalLink.class); }
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
        if (Config.worldgen.blockOres && veins.isEmpty()) { ContentLog.LOGGER.warn("blockOres is on and no pack vein survived, so nothing will generate ore at all. Check the skipped entries above"); }
        boolean anvils = ContentAnvils.load();
        if (ContentAffixes.load()) { MinecraftForge.EVENT_BUS.register(ContentAffixes.class); }
        if (anvils || ContentAffixes.sockets()) { MinecraftForge.EVENT_BUS.register(ContentAnvils.class); }
        ContentEnchanting.load();
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
        ContentWorn.register();
        if (Config.worldgen.worldgenDebug) {
            MinecraftForge.EVENT_BUS.register(ContentEntityTicks.class);
            MinecraftForge.EVENT_BUS.register(ContentChunkWatch.class);
        }
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) { ClientProxy.init(); }
    }

    public static void interModComms() {
        ContentDimensions.settle();
        ContentCelestial.register();
    }

    public static void postInit() {
        ContentEntityTypes.registerLate();
        ContentBiomes.resolveSpawns();
    }

    public static void loadComplete() {
        RecipeDisabled.apply();
        FurnaceBlocking.apply();
        ContentSpawners.apply();
        ContentPortals.bind();
        ContentCelestial.loadComplete();
        ContentRanks.verify();
        ContentRegistry.resolveFluidReactions();
        VanillaFluidRules.resolve();
    }

    public static void beforeServerStart(MinecraftServer server) {
        ContentPristine.beforeWorldsLoad(server);
        loadPackContent();
        ContentServer.dedicatedStarting(server);
        CubicChunksWorld.offer(server);
    }

    private static void loadPackContent() {
        Path root = PackManager.get().getRoot();
        if (root == null) { return; }
        PackManager.get().scan(root);
        PackManager.get().report();
        RegistryRemaps.reload();
        FurnaceRecipes.reload();
        FurnaceBlocking.apply();
        RecipeDisabled.furnace();
        ContentOverrides.reload();
        ContentWeights.load();
        ContentReplacements.reload();
        ContentStructurePlacement.reload();
        ContentEntityTicks.reload();
        ContentVillages.reload();
        ContentBeardSeat.reload();
        ContentCityMaps.reload();
        ContentChunkSaves.reload();
        ContentSpawnChunks.reload();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) { ClientProxy.reload(); }
        CardRules.load();
        ContentDialogs.load();
        ContentHearts.load();
        if (ContentVillages.load()) { ContentVillages.register(); }
    }

    public static void serverCommands(FMLServerStartingEvent event) { event.registerServerCommand(new ServerCommands()); }

    public static void onServerStarted() {
        ContentCelestial.serverStarted();
        ContentBoards.rebuild(FMLCommonHandler.instance().getMinecraftServerInstance());
        ContentEnvironments.serverStarted(FMLCommonHandler.instance().getMinecraftServerInstance());
        ContentMobSwitch.serverStarted(FMLCommonHandler.instance().getMinecraftServerInstance());
        ContentLootSwitch.serverStarted(FMLCommonHandler.instance().getMinecraftServerInstance());
        ContentTradeSwitch.serverStarted();
        ContentItemLocks.report(FMLCommonHandler.instance().getMinecraftServerInstance());
        FunctionOverrides.serverStarted(FMLCommonHandler.instance().getMinecraftServerInstance());
    }

    public static void onServerStopping() { ContentPregen.serverStopping(); }

    public static void onIdMapping() { ContentCompressables.mapOrphanMetas(); }

    public static void onServerStopped() {
        ContentStructureSearch.forget();
        ContentWeapons.clear();
        ContentEnvironments.serverStopped();
        ContentSpawnSwitch.serverStopped();
        ContentGrowSwitch.serverStopped();
        ContentMobSwitch.serverStopped();
        ContentTradeSwitch.serverStopped();
        FunctionOverrides.serverStopped();
        GateEvents.serverStopped();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) { return; }
        PackManager.get().close();
    }
}
