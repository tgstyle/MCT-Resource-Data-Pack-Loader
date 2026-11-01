package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.client.gui.ContentWorldScreen;
import mctmods.resourcedatapackloader.client.gui.PackOptionsButton;
import mctmods.resourcedatapackloader.client.gui.hud.BossBackgrounds;
import mctmods.resourcedatapackloader.client.gui.hud.CardOverlay;
import mctmods.resourcedatapackloader.client.gui.hud.CenterCard;
import mctmods.resourcedatapackloader.client.gui.hud.HeartsHud;
import mctmods.resourcedatapackloader.client.gui.hud.HoldView;
import mctmods.resourcedatapackloader.client.gui.hud.PackTooltips;
import mctmods.resourcedatapackloader.client.gui.hud.ProspectTooltip;
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

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientSetup {
    public static void pouchKeys() {
        MinecraftForge.EVENT_BUS.addListener(PouchKey::tick);
        MinecraftForge.EVENT_BUS.addListener(PouchKey::screen);
    }

    public static void clientHooks(IEventBus modBus) {
        modBus.addListener((RegisterClientReloadListenersEvent held) -> held.registerReloadListener(FaceCacheReset.INSTANCE));
        MinecraftForge.EVENT_BUS.addListener(ClientCommands::register);
        MinecraftForge.EVENT_BUS.addListener(ContentWorldScreen::onScreenInit);
        MinecraftForge.EVENT_BUS.addListener(ToastFilter::onToastAdd);
        MinecraftForge.EVENT_BUS.addListener(BossBackgrounds::onBar);
        MinecraftForge.EVENT_BUS.addListener(CardOverlay::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(CardOverlay::onHud);
        MinecraftForge.EVENT_BUS.addListener(CardOverlay::onScreen);
        MinecraftForge.EVENT_BUS.addListener(HeartsHud::onHealth);
        MinecraftForge.EVENT_BUS.addListener(CenterCard::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(CenterCard::onLevelUnload);
        MinecraftForge.EVENT_BUS.addListener(CenterCard::onHud);
        MinecraftForge.EVENT_BUS.addListener(PackOptionsButton::onInit);
        MinecraftForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPre);
        MinecraftForge.EVENT_BUS.addListener(PackOptionsButton::onRenderPost);
        MinecraftForge.EVENT_BUS.addListener(ChatHistoryKeeper::onOpening);
        MinecraftForge.EVENT_BUS.addListener(SeamSkyRenderer::onRenderStage);
        MinecraftForge.EVENT_BUS.addListener(ContentFogSampler::onFog);
        MinecraftForge.EVENT_BUS.addListener(ContentFogSampler::onTick);
        MinecraftForge.EVENT_BUS.addListener(ContentSeasonTint::onTick);
        MinecraftForge.EVENT_BUS.addListener(ContentHeatShimmer::onTick);
        MinecraftForge.EVENT_BUS.addListener(ContentHeatShimmer::onStage);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, true, HoldView::onFog);
        MinecraftForge.EVENT_BUS.addListener(HoldView::onHud);
        MinecraftForge.EVENT_BUS.addListener(ProspectTooltip::onTooltip);
        MinecraftForge.EVENT_BUS.addListener(PackTooltips::onTooltip);
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> { if (event.phase == TickEvent.Phase.END) { HoldView.tick(); } });
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> {
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
            MinecraftForge.EVENT_BUS.addListener(ContentHardnessCheck::onLevelLoad);
            ContentHardnessCheck.watching();
        }
    }
}
