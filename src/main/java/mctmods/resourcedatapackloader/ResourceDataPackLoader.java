package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.client.PouchKey;
import mctmods.resourcedatapackloader.client.CardOverlay;
import mctmods.resourcedatapackloader.client.CenterCard;
import mctmods.resourcedatapackloader.client.HoldView;
import mctmods.resourcedatapackloader.client.ProspectTooltip;
import mctmods.resourcedatapackloader.client.SeamSkyRenderer;
import mctmods.resourcedatapackloader.client.ChatHistoryKeeper;
import mctmods.resourcedatapackloader.client.PackOptionsButton;
import mctmods.resourcedatapackloader.client.FaceCacheReset;
import mctmods.resourcedatapackloader.command.ClientCommands;
import mctmods.resourcedatapackloader.command.ServerCommands;
import mctmods.resourcedatapackloader.content.worldgen.ContentPristine;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.ContentRaids;
import mctmods.resourcedatapackloader.content.ContentTeams;
import mctmods.resourcedatapackloader.content.ContentClient;
import mctmods.resourcedatapackloader.content.ContentEvents;
import mctmods.resourcedatapackloader.content.ContentExposures;
import mctmods.resourcedatapackloader.content.ContentAnvils;
import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.ContentHardnessCheck;
import mctmods.resourcedatapackloader.content.ContentOverrides;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentWelcome;
import mctmods.resourcedatapackloader.content.util.ContentDisabledEvents;
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
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkTokens;
import mctmods.resourcedatapackloader.content.worldgen.ContentCity;
import mctmods.resourcedatapackloader.content.worldgen.ContentCityMaps;
import mctmods.resourcedatapackloader.content.worldgen.ContentPathIntersects;
import mctmods.resourcedatapackloader.content.worldgen.ContentVillages;
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentRetrogen;
import mctmods.resourcedatapackloader.content.entity.ContentThreat;
import mctmods.resourcedatapackloader.content.gate.ContentGates;
import mctmods.resourcedatapackloader.content.card.CardEvents;
import mctmods.resourcedatapackloader.content.card.CardRules;
import mctmods.resourcedatapackloader.content.card.CardScan;
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
import mctmods.resourcedatapackloader.util.AtomicModConfig;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Toasts;

import net.minecraft.util.Unit;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.nio.file.Path;
import java.util.Set;

@Mod(ResourceDataPackLoader.MOD_ID) public class ResourceDataPackLoader {
    public static final String MOD_ID = "resourcedatapackloader";
    public static final Logger LOGGER = LogManager.getLogger("RDPL");

    public ResourceDataPackLoader(FMLJavaModLoadingContext context) {
        Lang.load();
        context.getContainer().addConfig(new AtomicModConfig(ModConfig.Type.COMMON, Config.SPEC, context.getContainer()));
        ContentLog.LOGGER.setDebug(Config.worldgen.worldgenDebug());
        IEventBus modBus = context.getModEventBus();
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
        CardRules.load();
        ContentPortalFrames.load();
        ContentPortals.prepare();
        ContentWorldIntro.load();
        ContentWorldgen.load();
        modBus.addListener(EventPriority.LOWEST, ContentEvents::onRegister);
        modBus.addListener(ContentEvents::onBuildTab);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentFuels::onFuelBurnTime);
        MinecraftForge.EVENT_BUS.addListener(ContentVillagers::applyTrades);
        MinecraftForge.EVENT_BUS.addListener(ContentTeams::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentTeams::onJoin);
        MinecraftForge.EVENT_BUS.addListener(ContentTeams::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentTeams::onLogout);
        MinecraftForge.EVENT_BUS.addListener(ContentTeams::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(PackOptionsWorld::onLogin);
        MinecraftForge.EVENT_BUS.addListener(PackOptionsWorld::onLevelSave);
        MinecraftForge.EVENT_BUS.addListener(ContentTeams::onFriendlyFire);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onDeath);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onPlayerDeath);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onRespawn);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onBack);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onStill);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onHeldHurt);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onHeldHit);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onHeldDig);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onHeldUse);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onHeldItem);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onHeldTouch);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onHeldBreak);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onHeldPlace);
        MinecraftForge.EVENT_BUS.addListener(ContentScoring::onHeldToss);
        MinecraftForge.EVENT_BUS.addListener(ContentRaids::onRing);
        MinecraftForge.EVENT_BUS.addListener(ContentRaids::onLevelTick);
        MinecraftForge.EVENT_BUS.addListener(ContentRaids::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(ContentRaids::onJoin);
        MinecraftForge.EVENT_BUS.addListener(ContentRaids::onRaiderHit);
        MinecraftForge.EVENT_BUS.addListener(ContentRaids::onRaiderTarget);
        if (ContentPaths.enabled()) { MinecraftForge.EVENT_BUS.addListener(ContentPaths::onRightClick); }
        MinecraftForge.EVENT_BUS.addListener(ContentHardness::onBreakSpeed);
        MinecraftForge.EVENT_BUS.addListener(ContentAnvils::onAnvil);
        MinecraftForge.EVENT_BUS.addListener(ContentAnvils::onTaken);
        MinecraftForge.EVENT_BUS.addListener(ContentAnvils::onHeld);
        MinecraftForge.EVENT_BUS.addListener(ContentAnvils::onAttack);
        MinecraftForge.EVENT_BUS.addListener(ContentAnvils::onUse);
        MinecraftForge.EVENT_BUS.addListener(ContentAnvils::onUseOn);
        MinecraftForge.EVENT_BUS.addListener(ContentAnvils::onDig);
        MinecraftForge.EVENT_BUS.addListener(ContentDisabledEvents::onUse);
        MinecraftForge.EVENT_BUS.addListener(ContentDisabledEvents::onUseOn);
        MinecraftForge.EVENT_BUS.addListener(ContentDisabledEvents::onDig);
        MinecraftForge.EVENT_BUS.addListener(ContentDisabledEvents::onAttack);
        MinecraftForge.EVENT_BUS.addListener(ContentDisabledEvents::onPlace);
        MinecraftForge.EVENT_BUS.addListener(ContentDisabledEvents::onPickup);
        MinecraftForge.EVENT_BUS.addListener(ContentDisabledEvents::onJoin);
        MinecraftForge.EVENT_BUS.addListener(ContentDisabledEvents::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentDisabledEvents::onOpen);
        MinecraftForge.EVENT_BUS.addListener(ContentDisabledEvents::onTick);
        MinecraftForge.EVENT_BUS.addListener(ContentDisabledEvents::onChunk);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, ContentHardness::onBreak);
        MinecraftForge.EVENT_BUS.addListener(ContentHardness::onAdvancement);
        MinecraftForge.EVENT_BUS.addListener(ContentHardness::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentHardness::onLevelUnload);
        if (FMLEnvironment.dist == Dist.CLIENT) { MinecraftForge.EVENT_BUS.addListener(PouchKey::tick); MinecraftForge.EVENT_BUS.addListener(PouchKey::screen); }
        MinecraftForge.EVENT_BUS.addListener(ContentHardness::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onJoin);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onShot);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onLeave);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onExplosion);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onInteract);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onFall);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ContentEntities::onExperience);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onBreathe);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onHurt);
        MinecraftForge.EVENT_BUS.addListener((LivingEvent.LivingTickEvent event) -> {
            ContentEntities.tick(event.getEntity());
            ContentPhysics.tick(event.getEntity());
        });
        MinecraftForge.EVENT_BUS.addListener(ContentPhysics::onJoin);
        MinecraftForge.EVENT_BUS.addListener(ContentPhysics::onFall);
        MinecraftForge.EVENT_BUS.addListener(ContentPhysics::onJump);
        MinecraftForge.EVENT_BUS.addListener(ContentDimensions::onClone);
        MinecraftForge.EVENT_BUS.addListener(ContentDimensions::onRespawn);
        MinecraftForge.EVENT_BUS.addListener(ContentDimensions::onLogout);
        MinecraftForge.EVENT_BUS.addListener(GateEvents::onLogout);
        MinecraftForge.EVENT_BUS.addListener(GateEvents::onTravel);
        MinecraftForge.EVENT_BUS.addListener(GateEvents::onKill);
        MinecraftForge.EVENT_BUS.addListener(GateEvents::onCraft);
        MinecraftForge.EVENT_BUS.addListener(GateEvents::onRightClick);
        MinecraftForge.EVENT_BUS.addListener(GateEvents::onAdvancement);
        MinecraftForge.EVENT_BUS.addListener(CardEvents::onDimension);
        MinecraftForge.EVENT_BUS.addListener(CardEvents::onRespawn);
        MinecraftForge.EVENT_BUS.addListener(CardEvents::onDeath);
        MinecraftForge.EVENT_BUS.addListener(CardEvents::onAdvancement);
        MinecraftForge.EVENT_BUS.addListener(CardEvents::onCraft);
        MinecraftForge.EVENT_BUS.addListener(CardEvents::onPickup);
        MinecraftForge.EVENT_BUS.addListener(CardEvents::onLogout);
        MinecraftForge.EVENT_BUS.addListener(CardScan::onTick);
        MinecraftForge.EVENT_BUS.addListener(VanillaPortalLink::onTravel);
        MinecraftForge.EVENT_BUS.addListener(VanillaPortalLink::onDimensionChange);
        MinecraftForge.EVENT_BUS.addListener(VanillaPortalLink::onJoin);
        MinecraftForge.EVENT_BUS.addListener(ContentProspect::onBreakSpeed);
        MinecraftForge.EVENT_BUS.addListener(ContentProspect::onBreak);
        MinecraftForge.EVENT_BUS.addListener(PortalEvents::onLogout);
        MinecraftForge.EVENT_BUS.addListener(PortalEvents::onBroken);
        MinecraftForge.EVENT_BUS.addListener(PortalEvents::onLit);
        MinecraftForge.EVENT_BUS.addListener(ContentThreat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(ContentThreat::onLogout);
        MinecraftForge.EVENT_BUS.addListener(ContentThreat::onLevelUnload);
        MinecraftForge.EVENT_BUS.addListener(ContentSeams::onLevelTick);
        MinecraftForge.EVENT_BUS.addListener(ContentPregenDimensions::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onServerStopping);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(CityBorder::onServerTick);

        MinecraftForge.EVENT_BUS.addListener(ContentPregenHold::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentPregenHold::onLogout);
        MinecraftForge.EVENT_BUS.addListener(ContentPregenDimensions::onDimensionChange);
        MinecraftForge.EVENT_BUS.addListener(ContentPregenHold::onTravel);
        MinecraftForge.EVENT_BUS.addListener(ContentPregenHold::onTeleport);
        MinecraftForge.EVENT_BUS.addListener(ContentReplacements::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentReplacements::onLevelTick);
        MinecraftForge.EVENT_BUS.addListener(ContentHardness::onLevelTick);
        MinecraftForge.EVENT_BUS.addListener(ContentReplacements::onLevelUnload);
        MinecraftForge.EVENT_BUS.addListener(ContentEntityTicks::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(ContentEntitySpawns::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onEngagement);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onDeathWatch);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onStruck);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onKnockBack);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onEffect);
        MinecraftForge.EVENT_BUS.addListener(ContentMobExperience::onHurt);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentMobExperience::onDeath);
        MinecraftForge.EVENT_BUS.addListener(ContentEntityTicks::onLevelUnload);
        MinecraftForge.EVENT_BUS.addListener(ContentIntroPlay::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentIntroPlay::onLogout);
        modBus.addListener(ContentEntityTypes::attributes);
        modBus.addListener(ContentEntityTypes::extraAttributes);
        modBus.addListener(ContentEntityTypes::placements);
        RDPLNetwork.register();
        if (ContentExposures.enabled()) { MinecraftForge.EVENT_BUS.addListener(ContentExposures::onPlayerTick); }
        if (ContentCaveRegions.ambient()) { MinecraftForge.EVENT_BUS.addListener(ContentCaveAmbience::onPlayerTick); }
        MinecraftForge.EVENT_BUS.addListener(ContentSpawning::onPlacementCheck);
        MinecraftForge.EVENT_BUS.addListener(ContentSpawning::onPositionCheck);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, ContentEntities::onPositionCheck);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onDespawnCheck);
        MinecraftForge.EVENT_BUS.addListener(ContentOreControl::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(ContentRetrogen::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentRetrogen::onLevelUnload);
        MinecraftForge.EVENT_BUS.addListener(ContentRetrogen::onLevelTick);
        MinecraftForge.EVENT_BUS.addGenericListener(LevelChunk.class, ContentChunkTokens::onAttach);
        modBus.addListener(ContentChunkTokens::register);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onCreateSpawn);
        MinecraftForge.EVENT_BUS.addListener(ContentCaveRegions::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentBiomes::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentCaveRegions::onPotentialSpawns);
        MinecraftForge.EVENT_BUS.addListener(ContentPopulateControl::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentOreControl::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentGeneratorControl::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onRespawn);
        MinecraftForge.EVENT_BUS.addListener(ContentWelcome::onLogin);
        MinecraftForge.EVENT_BUS.addListener(Toasts::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentWelcome::onLogout);
        MinecraftForge.EVENT_BUS.addListener(ContentWelcome::onDimensionChange);
        modBus.addListener(this::onConfig);
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onAddPackFinders);
        LootFunctions.REGISTER.register(modBus);
        ContentTreeTrunk.REGISTER.register(modBus);
        BlockDrops.REGISTER.register(modBus);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, LootInjections::onLootTableLoad);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, PlayerLoot::onDrops);
        MinecraftForge.EVENT_BUS.addListener(RegistryRemaps::onMissingMappings);
        MinecraftForge.EVENT_BUS.addListener(this::onTagsUpdated);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(this::beforeServerStart);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopped);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener((RegisterClientReloadListenersEvent held) -> held.registerReloadListener(FaceCacheReset.INSTANCE));
            MinecraftForge.EVENT_BUS.addListener(ClientCommands::register);
            MinecraftForge.EVENT_BUS.addListener(ContentWorldScreen::onScreenInit);
            MinecraftForge.EVENT_BUS.addListener(CardOverlay::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(CardOverlay::onHud);
            MinecraftForge.EVENT_BUS.addListener(CardOverlay::onScreen);
            MinecraftForge.EVENT_BUS.addListener(CenterCard::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(CenterCard::onLevelUnload);
            MinecraftForge.EVENT_BUS.addListener(CenterCard::onHud);
            MinecraftForge.EVENT_BUS.addListener(PackOptionsButton::onInit);
            MinecraftForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPre);
            MinecraftForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPost);
            MinecraftForge.EVENT_BUS.addListener(ChatHistoryKeeper::onOpening);
            MinecraftForge.EVENT_BUS.addListener(SeamSkyRenderer::onRenderStage);
            MinecraftForge.EVENT_BUS.addListener(HoldView::onFog);
            MinecraftForge.EVENT_BUS.addListener(HoldView::onHud);
            MinecraftForge.EVENT_BUS.addListener(ProspectTooltip::onTooltip);
            MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> { if (event.phase == TickEvent.Phase.END) { HoldView.tick(); } });
            MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> {
                HoldView.reset();
                CenterCard.reset();
                Toasts.show(0);
            });
            ContentClient.register(modBus);
            if (Config.worldgen.worldgenDebug()) {
                MinecraftForge.EVENT_BUS.addListener(ContentHardnessCheck::onLevelLoad);
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
        event.enqueueWork(ContentPotions::applyBrewing);
        Set<String> missing = PackRequirements.required();
        if (missing.isEmpty()) {
            RegistryRemaps.reload();
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
        RegistryRemaps.reload();
        ContentOverrides.reload();
        ContentReplacements.reload();
        ContentWorldTemplates.load();
        ContentEntityTicks.reload();
        ContentTeams.load();
        ContentScoring.load();
        ContentRaids.load();
        CardRules.load();
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
