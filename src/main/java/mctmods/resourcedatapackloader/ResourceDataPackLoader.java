package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.client.PouchKey;
import mctmods.resourcedatapackloader.client.ChatHistoryKeeper;
import mctmods.resourcedatapackloader.client.CardOverlay;
import mctmods.resourcedatapackloader.client.HoldView;
import mctmods.resourcedatapackloader.client.ProspectTooltip;
import mctmods.resourcedatapackloader.client.SeamSkyRenderer;
import mctmods.resourcedatapackloader.client.PackOptionsButton;
import mctmods.resourcedatapackloader.client.FaceCacheReset;
import mctmods.resourcedatapackloader.command.ClientCommands;
import mctmods.resourcedatapackloader.command.ServerCommands;
import mctmods.resourcedatapackloader.content.worldgen.ContentPristine;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.ContentTeams;
import mctmods.resourcedatapackloader.content.ContentClient;
import mctmods.resourcedatapackloader.content.ContentContainers;
import mctmods.resourcedatapackloader.content.ContentEvents;
import mctmods.resourcedatapackloader.content.ContentExposures;
import mctmods.resourcedatapackloader.content.ContentAnvils;
import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.ContentHardnessCheck;
import mctmods.resourcedatapackloader.content.ContentOverrides;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentWelcome;
import mctmods.resourcedatapackloader.content.block.ContentBlock;
import mctmods.resourcedatapackloader.content.block.ContentFluids;
import mctmods.resourcedatapackloader.content.block.ContentSpawners;
import mctmods.resourcedatapackloader.content.compat.ContentBlastPlaster;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.entity.ContentMobExperience;
import mctmods.resourcedatapackloader.content.extra.ContentFuels;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.worldgen.ContentPaths;
import mctmods.resourcedatapackloader.content.worldgen.ContentProspect;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldScreen;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldShape;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveAmbience;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveRegions;
import mctmods.resourcedatapackloader.content.worldgen.ContentPopulateControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentCity;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityMaps;
import mctmods.resourcedatapackloader.content.worldgen.ContentPathIntersects;
import mctmods.resourcedatapackloader.content.worldgen.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentRetrogen;
import mctmods.resourcedatapackloader.content.entity.ContentThreat;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.content.gate.GateEvents;
import mctmods.resourcedatapackloader.content.gate.VanillaPortalLink;
import mctmods.resourcedatapackloader.content.portal.ContentPortalFrames;
import mctmods.resourcedatapackloader.content.portal.ContentPortals;
import mctmods.resourcedatapackloader.content.portal.PortalEvents;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentGeneratorControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;
import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;
import mctmods.resourcedatapackloader.content.entity.ContentEntitySpawns;
import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.entity.ContentEntityTypes;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.extra.ContentWorldIntro;
import mctmods.resourcedatapackloader.content.worldgen.CityBorder;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregenDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregenHold;
import mctmods.resourcedatapackloader.content.worldgen.ContentTreeTrunk;
import mctmods.resourcedatapackloader.content.worldgen.ContentReplacements;
import mctmods.resourcedatapackloader.content.worldgen.ContentSeams;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawning;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureMaps;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.loot.BlockDrops;
import mctmods.resourcedatapackloader.loot.LootFunctions;
import mctmods.resourcedatapackloader.loot.LootInjections;
import mctmods.resourcedatapackloader.loot.PlayerLoot;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.pack.PackFinder;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.PackOptionsWorld;
import mctmods.resourcedatapackloader.pack.PackRequirements;
import mctmods.resourcedatapackloader.recipe.RecipeLoading;
import mctmods.resourcedatapackloader.recipe.interfaces.IRecipeFilter;
import mctmods.resourcedatapackloader.registry.RegistryRemaps;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.nio.file.Path;
import java.util.Set;

@Mod(ResourceDataPackLoader.MOD_ID) public class ResourceDataPackLoader {
    public static final String MOD_ID = "resourcedatapackloader";
    public static final Logger LOGGER = LogManager.getLogger("RDPL");

    public ResourceDataPackLoader(IEventBus modBus, ModContainer container) {
        Lang.load();
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ContentLog.LOGGER.setDebug(Config.worldgen.worldgenDebug());
        PackFinder.ensureScanned();
        ContentRegistry.load();
        ContentEntities.load();
        ContentWorldTemplates.load();
        ContentBiomes.load();
        ContentCaveRegions.load();
        ContentStructureMaps.load();
        ContentVillages.load();
        ContentPathIntersects.load();
        ContentCityMaps.load();
        ContentDimensions.load();
        ContentGameRules.load();
        ContentGates.load();
        ContentPortalFrames.load();
        ContentPortals.prepare();
        ContentWorldIntro.load();
        ContentWorldgen.load();
        modBus.addListener(EventPriority.LOWEST, ContentEvents::onRegister);
        modBus.addListener(ContentEvents::onBuildTab);
        NeoForge.EVENT_BUS.addListener(RegisterBrewingRecipesEvent.class, event -> ContentPotions.applyBrewing(event.getBuilder()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentFuels::onFuelBurnTime);
        NeoForge.EVENT_BUS.addListener(ContentVillagers::applyTrades);
        NeoForge.EVENT_BUS.addListener(ContentTeams::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentTeams::onJoin);
        NeoForge.EVENT_BUS.addListener(ContentTeams::onLogin);
        NeoForge.EVENT_BUS.addListener(ContentTeams::onLogout);
        NeoForge.EVENT_BUS.addListener(ContentTeams::onServerTick);
        NeoForge.EVENT_BUS.addListener(PackOptionsWorld::onLogin);
        NeoForge.EVENT_BUS.addListener(PackOptionsWorld::onLevelSave);
        NeoForge.EVENT_BUS.addListener(ContentTeams::onFriendlyFire);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onDeath);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onServerTick);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onPlayerDeath);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onRespawn);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onBack);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onStill);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onHeldHurt);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onHeldHit);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onHeldDig);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onHeldUse);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onHeldItem);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onHeldTouch);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onHeldBreak);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onHeldPlace);
        NeoForge.EVENT_BUS.addListener(ContentScoring::onHeldToss);
        NeoForge.EVENT_BUS.addListener(ContentRaids::onRing);
        NeoForge.EVENT_BUS.addListener(ContentRaids::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ContentRaids::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(ContentRaids::onJoin);
        NeoForge.EVENT_BUS.addListener(ContentRaids::onRaiderHit);
        NeoForge.EVENT_BUS.addListener(ContentRaids::onRaiderTarget);
        if (FMLEnvironment.dist == Dist.CLIENT) { NeoForge.EVENT_BUS.addListener(PouchKey::tick); NeoForge.EVENT_BUS.addListener(PouchKey::screen); }
        if (ContentPaths.enabled()) { NeoForge.EVENT_BUS.addListener(ContentPaths::onRightClick); }
        NeoForge.EVENT_BUS.addListener(ContentHardness::onBreakSpeed);
        NeoForge.EVENT_BUS.addListener(ContentAnvils::onAnvil);
        NeoForge.EVENT_BUS.addListener(ContentAnvils::onTaken);
        NeoForge.EVENT_BUS.addListener(ContentAnvils::onHeld);
        NeoForge.EVENT_BUS.addListener(ContentAnvils::onAttack);
        NeoForge.EVENT_BUS.addListener(ContentAnvils::onUse);
        NeoForge.EVENT_BUS.addListener(ContentAnvils::onUseOn);
        NeoForge.EVENT_BUS.addListener(ContentAnvils::onDig);
        NeoForge.EVENT_BUS.addListener(ContentAnvils::onServerStarted);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ContentHardness::onBreak);
        NeoForge.EVENT_BUS.addListener(ContentHardness::onAdvancement);
        NeoForge.EVENT_BUS.addListener(ContentHardness::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(ContentHardness::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, ContentBlock::onDrops);
        NeoForge.EVENT_BUS.addListener(ContentHardness::onDrops);
        NeoForge.EVENT_BUS.addListener(ContentHardness::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onJoin);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onShot);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onLeave);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onInteract);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onFall);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ContentEntities::onExperience);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onBreathe);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onHurt);
        NeoForge.EVENT_BUS.addListener(EntityTickEvent.Post.class, event -> {
            if (!(event.getEntity() instanceof LivingEntity living)) { return; }
            ContentEntities.tick(living);
            ContentPhysics.tick(living);
        });
        NeoForge.EVENT_BUS.addListener(ContentPhysics::onFall);
        NeoForge.EVENT_BUS.addListener(ContentPhysics::onJump);
        NeoForge.EVENT_BUS.addListener(ContentDimensions::onClone);
        NeoForge.EVENT_BUS.addListener(ContentDimensions::onRespawn);
        NeoForge.EVENT_BUS.addListener(ContentDimensions::onLogout);
        NeoForge.EVENT_BUS.addListener(GateEvents::onLogout);
        NeoForge.EVENT_BUS.addListener(GateEvents::onTravel);
        NeoForge.EVENT_BUS.addListener(GateEvents::onKill);
        NeoForge.EVENT_BUS.addListener(GateEvents::onCraft);
        NeoForge.EVENT_BUS.addListener(GateEvents::onRightClick);
        NeoForge.EVENT_BUS.addListener(GateEvents::onAdvancement);
        NeoForge.EVENT_BUS.addListener(VanillaPortalLink::onTravel);
        NeoForge.EVENT_BUS.addListener(VanillaPortalLink::onDimensionChange);
        NeoForge.EVENT_BUS.addListener(VanillaPortalLink::onJoin);
        NeoForge.EVENT_BUS.addListener(ContentProspect::onBreakSpeed);
        NeoForge.EVENT_BUS.addListener(ContentProspect::onBreak);
        NeoForge.EVENT_BUS.addListener(ContentProspect::onDrops);
        NeoForge.EVENT_BUS.addListener(PortalEvents::onLogout);
        NeoForge.EVENT_BUS.addListener(PortalEvents::onBroken);
        NeoForge.EVENT_BUS.addListener(PortalEvents::onLit);
        NeoForge.EVENT_BUS.addListener(ContentThreat::onServerTick);
        NeoForge.EVENT_BUS.addListener(ContentThreat::onLogout);
        NeoForge.EVENT_BUS.addListener(ContentThreat::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(ContentSeams::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ContentPregenDimensions::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ContentPregen::onServerStopping);
        NeoForge.EVENT_BUS.addListener(ContentPregen::onServerTick);
        NeoForge.EVENT_BUS.addListener(CityBorder::onServerTick);
        NeoForge.EVENT_BUS.addListener(ContentPregenHold::onLogin);
        NeoForge.EVENT_BUS.addListener(ContentPregenHold::onLogout);
        NeoForge.EVENT_BUS.addListener(ContentPregenDimensions::onDimensionChange);
        NeoForge.EVENT_BUS.addListener(ContentPregenHold::onTravel);
        NeoForge.EVENT_BUS.addListener(ContentPregenHold::onTeleport);
        NeoForge.EVENT_BUS.addListener(ContentReplacements::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(ContentReplacements::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ContentHardness::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ContentReplacements::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(ContentEntityTicks::onServerTick);
        NeoForge.EVENT_BUS.addListener(ContentEntitySpawns::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onEngagement);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onDeathWatch);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onStruck);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onKnockBack);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onEffect);
        NeoForge.EVENT_BUS.addListener(ContentMobExperience::onHurt);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentMobExperience::onDeath);
        NeoForge.EVENT_BUS.addListener(ContentEntityTicks::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(ContentIntroPlay::onLogin);
        NeoForge.EVENT_BUS.addListener(ContentIntroPlay::onLogout);
        modBus.addListener(ContentEntityTypes::attributes);
        modBus.addListener(ContentFluids::registerCapabilities);
        modBus.addListener(ContentContainers::registerCapabilities);
        modBus.addListener(ContentEntityTypes::extraAttributes);
        modBus.addListener(ContentEntityTypes::placements);
        modBus.addListener(RDPLNetwork::register);
        if (ContentExposures.enabled()) { NeoForge.EVENT_BUS.addListener(ContentExposures::onPlayerTick); }
        if (ContentCaveRegions.ambient()) { NeoForge.EVENT_BUS.addListener(ContentCaveAmbience::onPlayerTick); }
        NeoForge.EVENT_BUS.addListener(ContentSpawning::onPlacementCheck);
        NeoForge.EVENT_BUS.addListener(ContentSpawning::onPositionCheck);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ContentEntities::onPositionCheck);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onDespawnCheck);
        NeoForge.EVENT_BUS.addListener(ContentOreControl::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ContentRetrogen::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(ContentRetrogen::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(ContentRetrogen::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onCreateSpawn);
        NeoForge.EVENT_BUS.addListener(ContentCaveRegions::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentCaveRegions::onPotentialSpawns);
        NeoForge.EVENT_BUS.addListener(ContentBiomes::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentPopulateControl::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentOreControl::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentGeneratorControl::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onLogin);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onRespawn);
        NeoForge.EVENT_BUS.addListener(ContentWelcome::onLogin);
        NeoForge.EVENT_BUS.addListener(ContentWelcome::onLogout);
        NeoForge.EVENT_BUS.addListener(ContentWelcome::onDimensionChange);
        modBus.addListener(this::onConfig);
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onAddPackFinders);
        LootFunctions.REGISTER.register(modBus);
        ContentTreeTrunk.REGISTER.register(modBus);
        BlockDrops.REGISTER.register(modBus);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, LootInjections::onLootTableLoad);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, PlayerLoot::onDrops);
        NeoForge.EVENT_BUS.addListener(this::onTagsUpdated);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::beforeServerStart);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener((RegisterClientReloadListenersEvent held) -> held.registerReloadListener(FaceCacheReset.INSTANCE));
            NeoForge.EVENT_BUS.addListener(ClientCommands::register);
            NeoForge.EVENT_BUS.addListener(ContentWorldScreen::onScreenInit);
            NeoForge.EVENT_BUS.addListener(CardOverlay::onClientTick);
            modBus.addListener((RegisterGuiLayersEvent held) -> held.registerAboveAll(ResourceLocation.fromNamespaceAndPath(MOD_ID, "cards"), CardOverlay::onLayer));
            NeoForge.EVENT_BUS.addListener(CardOverlay::onScreen);
            NeoForge.EVENT_BUS.addListener(PackOptionsButton::onInit);
            NeoForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPre);
            NeoForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPost);
            NeoForge.EVENT_BUS.addListener(ChatHistoryKeeper::onOpening);
            NeoForge.EVENT_BUS.addListener(SeamSkyRenderer::onRenderStage);
            NeoForge.EVENT_BUS.addListener(HoldView::onFog);
            NeoForge.EVENT_BUS.addListener(HoldView::onHud);
            NeoForge.EVENT_BUS.addListener(ProspectTooltip::onTooltip);
            NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> HoldView.tick());
            NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event -> HoldView.reset());
            ContentClient.register(modBus);
            if (Config.worldgen.worldgenDebug()) {
                NeoForge.EVENT_BUS.addListener(ContentHardnessCheck::onLevelLoad);
                ContentHardnessCheck.watching();
            }
        }
    }

    private void onConfig(ModConfigEvent event) {
        if (event.getConfig().getSpec() != Config.SPEC) { return; }
        ContentLog.LOGGER.setDebug(Config.worldgen.worldgenDebug());
        LOGGER.info("Config packs: rootDirectory={} overrideResourcePacks={} warnOnCaseMismatch={} logContents={} traceUnresolvedVariables={}",
                Config.packs.rootDirectory(), Config.packs.overrideResourcePacks(), Config.packs.warnOnCaseMismatch(), Config.packs.logContents(), Config.packs.traceUnresolvedVariables());
        LOGGER.info("Config content: load={} sounds={} fuels={} potions={} brewing={} villagers={} biomes={}",
                !Config.content.loadOff(), Config.content.sounds(), Config.content.fuels(), Config.content.potions(), Config.content.brewing(), Config.content.villagers(), Config.content.biomes());
        LOGGER.info("Config recipes: furnace={} removals={} skipMissingItems={} blockRecipes={} recipeMatch={} blockFurnaceRecipes={}",
                Config.recipes.furnace(), Config.recipes.removals(), Config.recipes.skipMissingItems(), Config.recipes.blockRecipes(), Config.recipes.recipeMatch(), Config.recipes.blockFurnaceRecipes());
        LOGGER.info("Config data: lootInjections={} functions={} registryRemaps={}", !Config.data.lootInjectionsOff(), !Config.data.functionsOff(), !Config.data.registryRemapsOff());
        LOGGER.info("Config worldgen: load={} retrogen={} adoptExistingChunks={} retrogenKey='{}' retrogenChunksPerTick={} blockOres={} oreWhitelist={} oreTypes={} oreTypesAreBlacklist={} flatBedrock={} blockBiomes={} biomeNames={} biomeNamesAreBlacklist={} worldgenDebug={}",
                !Config.worldgen.loadOff(), Config.worldgen.retrogen(), Config.worldgen.adoptExistingChunks(), Config.worldgen.retrogenKey(), Config.worldgen.retrogenChunksPerTick(), Config.worldgen.blockOres(), Config.worldgen.oreWhitelist(), Config.worldgen.oreTypes(), Config.worldgen.oreTypesAreBlacklist(), Config.worldgen.flatBedrock(), Config.worldgen.blockBiomes(), Config.worldgen.biomeNames(), Config.worldgen.biomeNamesAreBlacklist(), Config.worldgen.worldgenDebug());
        LOGGER.info("Config terrain: generatorOptions='{}'", Config.worldgen.generatorOptions());
        LOGGER.info("Config chunks: spawnChunkRadius={} spawnChunkRadii={}", Config.chunks.spawnChunkRadius(), Config.chunks.spawnChunkRadii());
        LOGGER.info("Config tweaks: promptLeafDecay={} lenientPaths={}", Config.tweaks.promptLeafDecay(), Config.tweaks.lenientPaths());
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        PackFinder.ensureScanned();
        PackManager.get().warnAboutDisabledFeatures();
        event.enqueueWork(ContentRegistry::resolveStacks);
        Set<String> missing = PackRequirements.required();
        if (missing.isEmpty()) {
            RegistryRemaps.applyAliases();
            event.enqueueWork(() -> {
                ContentSpawners.apply();
                ContentHardness.setup();
                ContentAnvils.load();
                ContentOverrides.reload();
            });
            return;
        }
        String message = "Packs require mods that are not installed: " + String.join(", ", missing) + ". Install them or remove the packs that need them";
        ContentLog.LOGGER.fatal(message);
        throw new IllegalStateException(message);
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener((barrier, manager, profiler, profiler2, executor, executor2) -> barrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
            if (event.getServerResources().getRecipeManager() instanceof IRecipeFilter filter) { RecipeLoading.afterReload(filter); }
        }, executor2));
    }

    private void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) { RecipeLoading.onTagsBound(); }
    }

    private void onAddPackFinders(AddPackFindersEvent event) { event.addRepositorySource(new PackFinder(event.getPackType())); }

    private void onRegisterCommands(RegisterCommandsEvent event) { ServerCommands.register(event.getDispatcher()); }

    private void beforeServerStart(ServerAboutToStartEvent event) {
        Path root = PackManager.get().getRoot();
        if (root == null) { return; }
        PackManager.get().scan(root);
        PackManager.get().report();
        RegistryRemaps.applyAliases();
        ContentOverrides.reload();
        ContentReplacements.reload();
        ContentWorldTemplates.load();
        ContentEntityTicks.reload();
        ContentTeams.load();
        ContentScoring.load();
        ContentRaids.load();
        PackOptionsWorld.beforeWorldsLoad(event.getServer());
        ContentPristine.beforeWorldsLoad(event.getServer());
        ContentCity.begin();
        ContentBlastPlaster.install();
        ContentSpawning.applyCaps();
    }

    private void onServerStopped(ServerStoppedEvent event) {
        ContentStructureSearch.forget();
        if (FMLEnvironment.dist == Dist.CLIENT) { return; }
        PackManager.get().close();
    }
}
