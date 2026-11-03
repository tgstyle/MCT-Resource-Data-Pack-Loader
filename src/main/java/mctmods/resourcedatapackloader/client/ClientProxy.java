package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.client.gui.CreateWorldPack;
import mctmods.resourcedatapackloader.client.gui.PackOptionsButton;
import mctmods.resourcedatapackloader.client.gui.hud.BossBackgrounds;
import mctmods.resourcedatapackloader.client.gui.hud.CardOverlay;
import mctmods.resourcedatapackloader.client.gui.hud.CenterCard;
import mctmods.resourcedatapackloader.client.gui.hud.HeartsHud;
import mctmods.resourcedatapackloader.client.gui.hud.HoldView;
import mctmods.resourcedatapackloader.client.gui.hud.PackTooltips;
import mctmods.resourcedatapackloader.client.gui.hud.WeightHud;
import mctmods.resourcedatapackloader.client.gui.hud.ProspectTooltip;
import mctmods.resourcedatapackloader.client.gui.packlist.PackListEntries;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentDimensionAmbience;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentFogSampler;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentHeatShimmer;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyRenderers;
import mctmods.resourcedatapackloader.client.util.ClientCommands;
import mctmods.resourcedatapackloader.client.util.ClientItemLocks;
import mctmods.resourcedatapackloader.client.util.ClientWeights;
import mctmods.resourcedatapackloader.client.util.ContentHardnessCheck;
import mctmods.resourcedatapackloader.client.util.PackListClient;
import mctmods.resourcedatapackloader.client.util.PouchKey;
import mctmods.resourcedatapackloader.common.items.ContentContainerItem;
import mctmods.resourcedatapackloader.core.config.Config;

import net.minecraftforge.common.MinecraftForge;

public class ClientProxy {
    public static void init() {
        ClientCommands.register();
        ContentSkyRenderers.listen();
        if (Config.worldgen.worldgenDebug) {
            MinecraftForge.EVENT_BUS.register(ContentHardnessCheck.class);
            ContentHardnessCheck.watching();
        }
        MinecraftForge.EVENT_BUS.register(new PackOptionsButton.Handler());
        MinecraftForge.EVENT_BUS.register(BossBackgrounds.class);
        MinecraftForge.EVENT_BUS.register(CreateWorldPack.class);
        MinecraftForge.EVENT_BUS.register(CardOverlay.class);
        MinecraftForge.EVENT_BUS.register(HeartsHud.class);
        MinecraftForge.EVENT_BUS.register(CenterCard.class);
        MinecraftForge.EVENT_BUS.register(HoldView.class);
        MinecraftForge.EVENT_BUS.register(ContentFogSampler.class);
        MinecraftForge.EVENT_BUS.register(ContentHeatShimmer.class);
        MinecraftForge.EVENT_BUS.register(ContentDimensionAmbience.class);
        MinecraftForge.EVENT_BUS.register(ProspectTooltip.class);
        MinecraftForge.EVENT_BUS.register(PackTooltips.class);
        MinecraftForge.EVENT_BUS.register(new PackListEntries.Handler());
        MinecraftForge.EVENT_BUS.register(PackListClient.class);
        MinecraftForge.EVENT_BUS.register(ClientItemLocks.class);
        MinecraftForge.EVENT_BUS.register(ClientWeights.class);
        MinecraftForge.EVENT_BUS.register(WeightHud.class);
        if (ContentContainerItem.baubled()) { PouchKey.register(); }
    }

    public static void reload() { ContentSkyRenderers.reload(); }
}
