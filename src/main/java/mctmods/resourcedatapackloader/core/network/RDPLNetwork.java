package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.client.gui.TalkScreen;
import mctmods.resourcedatapackloader.client.gui.hud.BossBackgrounds;
import mctmods.resourcedatapackloader.client.gui.hud.CardOverlay;
import mctmods.resourcedatapackloader.client.gui.hud.HeartsHud;
import mctmods.resourcedatapackloader.client.gui.hud.HoldView;
import mctmods.resourcedatapackloader.client.gui.intro.WorldIntroScreen;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSeasonTint;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyGates;
import mctmods.resourcedatapackloader.client.util.BlastHush;
import mctmods.resourcedatapackloader.client.util.ContentRuleClient;
import mctmods.resourcedatapackloader.common.anvils.AnvilCosts;
import mctmods.resourcedatapackloader.common.dialogs.DialogTalk;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.gui.ContentPortwayMenu;
import mctmods.resourcedatapackloader.common.hearts.ContentHearts;
import mctmods.resourcedatapackloader.common.intro.ContentIntroPlay;
import mctmods.resourcedatapackloader.common.items.ContentWornContainers;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.BossBackgroundDef;
import mctmods.resourcedatapackloader.common.util.messages.Says;
import mctmods.resourcedatapackloader.common.util.messages.Toasts;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class RDPLNetwork {
    private static final ResourceLocation NAME = ResourceDataPackLoader.id("main");
    private static final String VERSION = "2";
    @Nullable private static SimpleChannel channel;

    private RDPLNetwork() {}

    public static void register() {
        if (channel != null) { return; }
        channel = NetworkRegistry.newSimpleChannel(NAME, () -> VERSION, NetworkRegistry.acceptMissingOr(VERSION), NetworkRegistry.acceptMissingOr(VERSION));
        channel.messageBuilder(CardMessage.class, 0, NetworkDirection.PLAY_TO_CLIENT).encoder(CardMessage::write).decoder(CardMessage::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { CardOverlay.show(message); } }).add();
        channel.messageBuilder(HoldMessage.class, 1, NetworkDirection.PLAY_TO_CLIENT).encoder(HoldMessage::write).decoder(HoldMessage::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { HoldView.set(message.held(), message.warning(), message.fog(), message.backdrop(), message.font()); } }).add();
        channel.messageBuilder(NoteMessage.class, 5, NetworkDirection.PLAY_TO_CLIENT).encoder(NoteMessage::write).decoder(NoteMessage::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { HoldView.note(message.said(), message.backdrop(), message.font()); } }).add();
        channel.messageBuilder(HushMessage.class, 6, NetworkDirection.PLAY_TO_CLIENT).encoder(HushMessage::write).decoder(HushMessage::read).consumerNetworkThread((message, context) -> {
            if (FMLEnvironment.dist == Dist.CLIENT) { BlastHush.mark(message); }
            return true;
        }).add();
        channel.messageBuilder(BossBackgroundMessage.class, 11, NetworkDirection.PLAY_TO_CLIENT).encoder(BossBackgroundMessage::write).decoder(BossBackgroundMessage::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { BossBackgrounds.set(message); } }).add();
        channel.messageBuilder(ToastsMessage.class, 7, NetworkDirection.PLAY_TO_CLIENT).encoder(ToastsMessage::write).decoder(ToastsMessage::read).consumerMainThread((message, context) -> Toasts.show(message.kinds())).add();
        channel.messageBuilder(AnvilCostsMessage.class, 10, NetworkDirection.PLAY_TO_CLIENT).encoder(AnvilCostsMessage::write).decoder(AnvilCostsMessage::read).consumerMainThread((message, context) -> AnvilCosts.show(message.cap(), message.paid(), message.lapis(), message.opening())).add();
        channel.messageBuilder(IntroPlayMessage.class, 2, NetworkDirection.PLAY_TO_CLIENT).encoder(IntroPlayMessage::write).decoder(IntroPlayMessage::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { WorldIntroScreen.open(message.landBeingMade()); } }).add();
        channel.messageBuilder(IntroLandMadeMessage.class, 8, NetworkDirection.PLAY_TO_CLIENT).encoder((message, buf) -> {}).decoder(buf -> new IntroLandMadeMessage()).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { WorldIntroScreen.landMade(); } }).add();
        channel.messageBuilder(EnvironmentsMessage.class, 9, NetworkDirection.PLAY_TO_CLIENT).encoder(EnvironmentsMessage::write).decoder(EnvironmentsMessage::read).consumerMainThread((message, context) -> {
            ContentEnvironments.receive(message.active(), message.whole());
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ContentRuleClient.settle();
                ContentSeasonTint.changed();
            }
        }).add();
        channel.messageBuilder(IntroDoneMessage.class, 3, NetworkDirection.PLAY_TO_SERVER).encoder((message, buf) -> {}).decoder(buf -> new IntroDoneMessage()).consumerMainThread((message, context) -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) { ContentIntroPlay.finished(player); }
        }).add();
        channel.messageBuilder(OpenWornMessage.class, 4, NetworkDirection.PLAY_TO_SERVER).encoder((message, buf) -> {}).decoder(buf -> new OpenWornMessage()).consumerMainThread((message, context) -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) { ContentWornContainers.open(player); }
        }).add();
        channel.messageBuilder(PortwayFilterMessage.class, 12, NetworkDirection.PLAY_TO_SERVER).encoder(PortwayFilterMessage::write).decoder(PortwayFilterMessage::read).consumerMainThread((message, context) -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.containerMenu instanceof ContentPortwayMenu menu) { menu.filter(message.text()); }
        }).add();
        channel.messageBuilder(TalkMessage.class, 13, NetworkDirection.PLAY_TO_CLIENT).encoder(TalkMessage::write).decoder(TalkMessage::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { TalkScreen.receive(message); } }).add();
        channel.messageBuilder(TalkPickMessage.class, 14, NetworkDirection.PLAY_TO_SERVER).encoder(TalkPickMessage::write).decoder(TalkPickMessage::read).consumerMainThread((message, context) -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) { DialogTalk.pick(player, message.index()); }
        }).add();
        channel.messageBuilder(HeartColorsMessage.class, 15, NetworkDirection.PLAY_TO_CLIENT).encoder(HeartColorsMessage::write).decoder(HeartColorsMessage::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { HeartsHud.show(message.colors()); } }).add();
        channel.messageBuilder(GatesMessage.class, 16, NetworkDirection.PLAY_TO_CLIENT).encoder(GatesMessage::write).decoder(GatesMessage::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { ContentSkyGates.receive(message.ids()); } }).add();
    }

    public static void openWorn() { if (channel != null) { channel.sendToServer(new OpenWornMessage()); } }

    public static void portwayFilter(String text) { if (channel != null) { channel.sendToServer(new PortwayFilterMessage(text)); } }

    private static <MSG> boolean toPlayer(ServerPlayer player, MSG message) {
        if (channel == null || !reaches(player)) { return false; }
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
        return true;
    }

    public static void sendHold(ServerPlayer player, boolean held, String warning, boolean fog) { toPlayer(player, new HoldMessage(held, warning, fog, Says.panel(), Says.font())); }

    public static boolean sendNote(ServerPlayer player, String said) { return toPlayer(player, new NoteMessage(said, Says.panel(), Says.font())); }

    public static boolean hush(ServerPlayer player, double x, double y, double z) { return toPlayer(player, new HushMessage(x, y, z)); }

    public static void playIntro(ServerPlayer player, boolean landBeingMade) { toPlayer(player, new IntroPlayMessage(landBeingMade)); }

    public static void introLandMade(ServerPlayer player) { toPlayer(player, new IntroLandMadeMessage()); }

    public static void sendBossBackground(ServerPlayer player, UUID bar, @Nullable BossBackgroundDef background) {
        if (background == null) { toPlayer(player, new BossBackgroundMessage(bar, "", 0, 0, 0, 0)); }
        else { toPlayer(player, new BossBackgroundMessage(bar, background.texture(), background.width(), background.height(), background.x(), background.y())); }
    }

    public static void sendToasts(ServerPlayer player, int kinds) { toPlayer(player, new ToastsMessage(kinds)); }

    public static void sendAnvilCosts(ServerPlayer player) { toPlayer(player, new AnvilCostsMessage(AnvilCosts.servedCap(), AnvilCosts.servedPaid(), AnvilCosts.servedLapis(), AnvilCosts.servedOpening())); }

    public static void introDone() { if (channel != null) { channel.sendToServer(new IntroDoneMessage()); } }

    public static boolean reaches(ServerPlayer player) { return channel != null && !NetworkHooks.isVanillaConnection(player.connection.connection) && channel.isRemotePresent(player.connection.connection); }

    public static void sendCard(ServerPlayer player, CardMessage card) { toPlayer(player, card); }

    public static void sendTalk(ServerPlayer player, TalkMessage talk) { toPlayer(player, talk); }

    public static void sendTalkPick(int index) { if (channel != null) { channel.sendToServer(new TalkPickMessage(index)); } }

    public static void sendHeartColors(ServerPlayer player) { toPlayer(player, new HeartColorsMessage(ContentHearts.colors())); }

    public static void sendGates(ServerPlayer player, List<String> ids) { toPlayer(player, new GatesMessage(ids)); }

    public static void sendEnvironments(ServerPlayer player, EnvironmentsMessage environments) { toPlayer(player, environments); }
}
