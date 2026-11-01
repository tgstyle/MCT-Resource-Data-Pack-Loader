package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.client.gui.ContentWorldScreen;
import mctmods.resourcedatapackloader.client.gui.PackOptionsButton;
import mctmods.resourcedatapackloader.client.gui.hud.BossBackgrounds;
import mctmods.resourcedatapackloader.client.gui.hud.CardOverlay;
import mctmods.resourcedatapackloader.client.gui.hud.CenterCard;
import mctmods.resourcedatapackloader.client.gui.hud.HeartsHud;
import mctmods.resourcedatapackloader.client.gui.hud.HoldView;
import mctmods.resourcedatapackloader.client.gui.hud.PackTooltips;
import mctmods.resourcedatapackloader.client.gui.hud.ProspectTooltip;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionAmbience;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentFogSampler;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentHeatShimmer;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSeasonTint;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyGates;
import mctmods.resourcedatapackloader.client.renderer.sky.SeamSkyRenderer;
import mctmods.resourcedatapackloader.client.util.ChatHistoryKeeper;
import mctmods.resourcedatapackloader.client.util.ClientCommands;
import mctmods.resourcedatapackloader.client.util.ContentClient;
import mctmods.resourcedatapackloader.client.util.ContentHardnessCheck;
import mctmods.resourcedatapackloader.client.util.FaceCacheReset;
import mctmods.resourcedatapackloader.client.util.PouchKey;
import mctmods.resourcedatapackloader.client.util.ToastFilter;
import mctmods.resourcedatapackloader.common.anvils.AnvilCosts;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.util.messages.Toasts;
import mctmods.resourcedatapackloader.core.config.Config;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientSetup {
    public static void pouchKey() {
        NeoForge.EVENT_BUS.addListener(PouchKey::tick);
        NeoForge.EVENT_BUS.addListener(PouchKey::screen);
    }

    public static void register(IEventBus modBus) {
        modBus.addListener((RegisterClientReloadListenersEvent held) -> held.registerReloadListener(FaceCacheReset.INSTANCE));
        NeoForge.EVENT_BUS.addListener(ClientCommands::register);
        NeoForge.EVENT_BUS.addListener(ToastFilter::onToastAdd);
        NeoForge.EVENT_BUS.addListener(ContentDimensionAmbience::onSelectMusic);
        NeoForge.EVENT_BUS.addListener(ContentWorldScreen::onScreenInit);
        NeoForge.EVENT_BUS.addListener(BossBackgrounds::onBar);
        NeoForge.EVENT_BUS.addListener(CardOverlay::onClientTick);
        modBus.addListener((RegisterGuiLayersEvent held) -> held.registerAboveAll(ResourceDataPackLoader.id("cards"), CardOverlay::onLayer));
        modBus.addListener((RegisterGuiLayersEvent held) -> held.registerAboveAll(ResourceDataPackLoader.id("center_cards"), CenterCard::onLayer));
        NeoForge.EVENT_BUS.addListener(HeartsHud::onLayer);
        NeoForge.EVENT_BUS.addListener(CenterCard::onClientTick);
        NeoForge.EVENT_BUS.addListener(CenterCard::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(CardOverlay::onScreen);
        NeoForge.EVENT_BUS.addListener(PackOptionsButton::onInit);
        NeoForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPre);
        NeoForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPost);
        NeoForge.EVENT_BUS.addListener(ChatHistoryKeeper::onOpening);
        NeoForge.EVENT_BUS.addListener(SeamSkyRenderer::onRenderStage);
        NeoForge.EVENT_BUS.addListener(ContentFogSampler::onFog);
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> ContentFogSampler.tick());
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> ContentHeatShimmer.tick());
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> ContentSeasonTint.tick());
        NeoForge.EVENT_BUS.addListener(ContentHeatShimmer::onStage);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, true, HoldView::onFog);
        NeoForge.EVENT_BUS.addListener(HoldView::onHud);
        NeoForge.EVENT_BUS.addListener(ProspectTooltip::onTooltip);
        NeoForge.EVENT_BUS.addListener(PackTooltips::onTooltip);
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> HoldView.tick());
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event -> {
            HoldView.reset();
            CenterCard.reset();
            BossBackgrounds.reset();
            Toasts.leave();
            ContentEnvironments.clear();
            ContentSeasonTint.forget();
            AnvilCosts.leave();
            ContentSkyGates.clear();
        });
        ContentClient.register(modBus);
        if (Config.worldgen.worldgenDebug()) {
            NeoForge.EVENT_BUS.addListener(ContentHardnessCheck::onLevelLoad);
            ContentHardnessCheck.watching();
        }
    }
}
