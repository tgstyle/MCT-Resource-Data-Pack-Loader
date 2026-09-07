package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.client.CardOverlay;
import mctmods.resourcedatapackloader.client.HoldView;
import mctmods.resourcedatapackloader.client.SeamSkyRenderer;
import mctmods.resourcedatapackloader.client.ChatHistoryKeeper;
import mctmods.resourcedatapackloader.client.PackOptionsButton;
import mctmods.resourcedatapackloader.command.ClientCommands;
import mctmods.resourcedatapackloader.command.ServerCommands;
import mctmods.resourcedatapackloader.content.ContentClient;
import mctmods.resourcedatapackloader.content.ContentEvents;
import mctmods.resourcedatapackloader.content.ContentExposures;
import mctmods.resourcedatapackloader.content.ContentHardness;
import mctmods.resourcedatapackloader.content.ContentHardnessCheck;
import mctmods.resourcedatapackloader.content.ContentOverrides;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentWelcome;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.extra.ContentFuels;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.content.extra.ContentVillagers;
import mctmods.resourcedatapackloader.content.worldgen.ContentPaths;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldScreen;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldShape;
import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveRegions;
import mctmods.resourcedatapackloader.content.worldgen.ContentChunkTokens;
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
import mctmods.resourcedatapackloader.content.worldgen.ContentGameRules;
import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;
import mctmods.resourcedatapackloader.content.entity.ContentEntityTicks;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.extra.ContentWorldIntro;
import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.content.worldgen.ContentReplacements;
import mctmods.resourcedatapackloader.content.worldgen.ContentSeams;
import mctmods.resourcedatapackloader.content.worldgen.ContentSpawning;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureMaps;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldTemplates;
import mctmods.resourcedatapackloader.loot.LootFunctions;
import mctmods.resourcedatapackloader.loot.LootInjections;
import mctmods.resourcedatapackloader.loot.PlayerLoot;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.pack.PackFinder;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.pack.PackRequirements;
import mctmods.resourcedatapackloader.recipe.RecipeLoading;
import mctmods.resourcedatapackloader.recipe.interfaces.IRecipeFilter;
import mctmods.resourcedatapackloader.registry.RegistryRemaps;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import net.minecraft.util.Unit;
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
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ContentLog.LOGGER.setDebug(Config.worldgen.worldgenDebug());
        IEventBus modBus = context.getModEventBus();
        PackFinder.ensureScanned();
        ContentRegistry.load();
        ContentEntities.load();
        ContentWorldTemplates.load();
        ContentBiomes.load();
        ContentCaveRegions.load();
        ContentStructureMaps.load();
        ContentDimensions.load();
        ContentGameRules.load();
        ContentGates.load();
        ContentPortalFrames.load();
        ContentPortals.prepare();
        ContentWorldIntro.load();
        ContentWorldgen.load();
        modBus.addListener(EventPriority.LOWEST, ContentEvents::onRegister);
        modBus.addListener(ContentEvents::onBuildTab);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentFuels::onFuelBurnTime);
        MinecraftForge.EVENT_BUS.addListener(ContentVillagers::applyTrades);
        if (ContentPaths.enabled()) { MinecraftForge.EVENT_BUS.addListener(ContentPaths::onRightClick); }
        MinecraftForge.EVENT_BUS.addListener(ContentHardness::onBreakSpeed);
        MinecraftForge.EVENT_BUS.addListener(ContentHardness::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentEvents::onBreak);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onJoin);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onInteract);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onFall);
        MinecraftForge.EVENT_BUS.addListener(ContentEntities::onExperience);
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
        MinecraftForge.EVENT_BUS.addListener(VanillaPortalLink::onTravel);
        MinecraftForge.EVENT_BUS.addListener(VanillaPortalLink::onDimensionChange);
        MinecraftForge.EVENT_BUS.addListener(VanillaPortalLink::onJoin);
        MinecraftForge.EVENT_BUS.addListener(PortalEvents::onLogout);
        MinecraftForge.EVENT_BUS.addListener(PortalEvents::onBroken);
        MinecraftForge.EVENT_BUS.addListener(PortalEvents::onLit);
        MinecraftForge.EVENT_BUS.addListener(ContentThreat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(ContentThreat::onLogout);
        MinecraftForge.EVENT_BUS.addListener(ContentThreat::onLevelUnload);
        MinecraftForge.EVENT_BUS.addListener(ContentSeams::onLevelTick);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onServerStopping);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onLogout);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onDimensionChange);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onTravel);
        MinecraftForge.EVENT_BUS.addListener(ContentPregen::onTeleport);
        MinecraftForge.EVENT_BUS.addListener(ContentReplacements::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentReplacements::onLevelTick);
        MinecraftForge.EVENT_BUS.addListener(ContentReplacements::onLevelUnload);
        MinecraftForge.EVENT_BUS.addListener(ContentEntityTicks::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(ContentEntityTicks::onLevelUnload);
        MinecraftForge.EVENT_BUS.addListener(ContentIntroPlay::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentIntroPlay::onLogout);
        modBus.addListener(ContentEntities::attributes);
        modBus.addListener(ContentEntities::extraAttributes);
        modBus.addListener(ContentEntities::placements);
        MinecraftForge.EVENT_BUS.addListener(ContentEvents::onDetonate);
        RDPLNetwork.register();
        if (ContentExposures.enabled()) { MinecraftForge.EVENT_BUS.addListener(ContentExposures::onPlayerTick); }
        MinecraftForge.EVENT_BUS.addListener(ContentSpawning::onPositionCheck);
        MinecraftForge.EVENT_BUS.addListener(ContentOreControl::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(ContentRetrogen::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentRetrogen::onLevelUnload);
        MinecraftForge.EVENT_BUS.addListener(ContentRetrogen::onLevelTick);
        MinecraftForge.EVENT_BUS.addGenericListener(LevelChunk.class, ContentChunkTokens::onAttach);
        modBus.addListener(ContentChunkTokens::register);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onCreateSpawn);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onRespawn);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldShape::onDimensionChange);
        MinecraftForge.EVENT_BUS.addListener(ContentWelcome::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ContentWelcome::onDimensionChange);
        modBus.addListener(this::onConfig);
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onAddPackFinders);
        LootFunctions.REGISTER.register(modBus);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, LootInjections::onLootTableLoad);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, PlayerLoot::onDrops);
        MinecraftForge.EVENT_BUS.addListener(RegistryRemaps::onMissingMappings);
        MinecraftForge.EVENT_BUS.addListener(this::onTagsUpdated);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(this::beforeServerStart);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopped);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.addListener(ClientCommands::register);
            MinecraftForge.EVENT_BUS.addListener(ContentWorldScreen::onScreenInit);
            MinecraftForge.EVENT_BUS.addListener(CardOverlay::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(CardOverlay::onHud);
            MinecraftForge.EVENT_BUS.addListener(CardOverlay::onScreen);
            MinecraftForge.EVENT_BUS.addListener(PackOptionsButton::onInit);
            MinecraftForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPre);
            MinecraftForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPost);
            MinecraftForge.EVENT_BUS.addListener(ChatHistoryKeeper::onOpening);
            MinecraftForge.EVENT_BUS.addListener(SeamSkyRenderer::onRenderStage);
            MinecraftForge.EVENT_BUS.addListener(HoldView::onFog);
            MinecraftForge.EVENT_BUS.addListener(HoldView::onHud);
            MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> { if (event.phase == TickEvent.Phase.END) { HoldView.tick(); } });
            MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> HoldView.reset());
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
        LOGGER.info("Config worldgen: worldgenDebug={}", Config.worldgen.worldgenDebug());
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        PackFinder.ensureScanned();
        event.enqueueWork(ContentPotions::applyBrewing);
        Set<String> missing = PackRequirements.required();
        if (missing.isEmpty()) {
            RegistryRemaps.reload();
            event.enqueueWork(() -> {
                ContentHardness.setup();
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
        ContentWorldTemplates.load();
        ContentSpawning.applyCaps();
    }

    private void onServerStopped(ServerStoppedEvent event) {
        if (FMLEnvironment.dist == Dist.CLIENT) { return; }
        PackManager.get().close();
    }
}
