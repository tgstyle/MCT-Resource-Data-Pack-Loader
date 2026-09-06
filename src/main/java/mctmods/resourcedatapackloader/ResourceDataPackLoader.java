package mctmods.resourcedatapackloader;

import mctmods.resourcedatapackloader.client.CardOverlay;
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
import mctmods.resourcedatapackloader.content.worldgen.ContentOreControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentRetrogen;
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
import mctmods.resourcedatapackloader.registry.RegistryRemaps;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
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
        ContentWorldgen.load();
        modBus.addListener(EventPriority.LOWEST, ContentEvents::onRegister);
        modBus.addListener(ContentEvents::onBuildTab);
        NeoForge.EVENT_BUS.addListener(RegisterBrewingRecipesEvent.class, event -> ContentPotions.applyBrewing(event.getBuilder()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ContentFuels::onFuelBurnTime);
        NeoForge.EVENT_BUS.addListener(ContentVillagers::applyTrades);
        if (ContentPaths.enabled()) { NeoForge.EVENT_BUS.addListener(ContentPaths::onRightClick); }
        NeoForge.EVENT_BUS.addListener(ContentHardness::onBreakSpeed);
        NeoForge.EVENT_BUS.addListener(ContentHardness::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentEvents::onBreak);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onJoin);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onFall);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onExperience);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onBreathe);
        NeoForge.EVENT_BUS.addListener(ContentEntities::onHurt);
        NeoForge.EVENT_BUS.addListener(EntityTickEvent.Post.class, event -> { if (event.getEntity() instanceof LivingEntity living) { ContentEntities.tick(living); } });
        modBus.addListener(ContentEntities::attributes);
        modBus.addListener(ContentEntities::extraAttributes);
        modBus.addListener(ContentEntities::placements);
        NeoForge.EVENT_BUS.addListener(ContentEvents::onDetonate);
        modBus.addListener(RDPLNetwork::register);
        if (ContentExposures.enabled()) { NeoForge.EVENT_BUS.addListener(ContentExposures::onPlayerTick); }
        NeoForge.EVENT_BUS.addListener(ContentSpawning::onPositionCheck);
        NeoForge.EVENT_BUS.addListener(ContentOreControl::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ContentRetrogen::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(ContentRetrogen::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(ContentRetrogen::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onCreateSpawn);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onLogin);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onRespawn);
        NeoForge.EVENT_BUS.addListener(ContentWorldShape::onDimensionChange);
        NeoForge.EVENT_BUS.addListener(ContentWelcome::onLogin);
        NeoForge.EVENT_BUS.addListener(ContentWelcome::onDimensionChange);
        modBus.addListener(this::onConfig);
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onAddPackFinders);
        LootFunctions.REGISTER.register(modBus);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, LootInjections::onLootTableLoad);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, PlayerLoot::onDrops);
        NeoForge.EVENT_BUS.addListener(this::onTagsUpdated);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::beforeServerStart);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.addListener(ClientCommands::register);
            NeoForge.EVENT_BUS.addListener(ContentWorldScreen::onScreenInit);
            NeoForge.EVENT_BUS.addListener(CardOverlay::onClientTick);
            NeoForge.EVENT_BUS.addListener(CardOverlay::onHud);
            NeoForge.EVENT_BUS.addListener(CardOverlay::onScreen);
            NeoForge.EVENT_BUS.addListener(PackOptionsButton::onInit);
            NeoForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPre);
            NeoForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPost);
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
        LOGGER.info("Config worldgen: worldgenDebug={}", Config.worldgen.worldgenDebug());
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        PackFinder.ensureScanned();
        Set<String> missing = PackRequirements.required();
        if (missing.isEmpty()) {
            RegistryRemaps.applyAliases();
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
        ContentWorldTemplates.load();
        ContentSpawning.applyCaps();
    }

    private void onServerStopped(ServerStoppedEvent event) {
        if (FMLEnvironment.dist == Dist.CLIENT) { return; }
        PackManager.get().close();
    }
}
