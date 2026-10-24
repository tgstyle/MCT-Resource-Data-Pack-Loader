package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.client.BlastHush;
import mctmods.resourcedatapackloader.client.BossBackgrounds;
import mctmods.resourcedatapackloader.client.CardOverlay;
import mctmods.resourcedatapackloader.client.ContentRuleClient;
import mctmods.resourcedatapackloader.client.ContentSeasonTint;
import mctmods.resourcedatapackloader.client.HoldView;
import mctmods.resourcedatapackloader.client.WorldIntroScreen;
import mctmods.resourcedatapackloader.content.def.BossBackgroundDef;
import mctmods.resourcedatapackloader.content.AnvilCosts;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.item.ContentWornContainers;
import mctmods.resourcedatapackloader.content.menu.ContentPortwayMenu;
import mctmods.resourcedatapackloader.content.worldgen.ContentEnvironments;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Toasts;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.UUID;
import javax.annotation.Nullable;

public final class RDPLNetwork {
    private static final ResourceLocation NAME = ResourceDataPackLoader.id("main");
    private static final String VERSION = "2";
    @Nullable private static SimpleChannel channel;

    private RDPLNetwork() {}

    public static void register() {
        if (channel != null) { return; }
        channel = NetworkRegistry.newSimpleChannel(NAME, () -> VERSION, NetworkRegistry.acceptMissingOr(VERSION), NetworkRegistry.acceptMissingOr(VERSION));
        channel.messageBuilder(MessageCard.class, 0, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageCard::write).decoder(MessageCard::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { CardOverlay.show(message); } }).add();
        channel.messageBuilder(MessageHold.class, 1, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageHold::write).decoder(MessageHold::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { HoldView.set(message.held(), message.warning(), message.fog(), message.backdrop(), message.font()); } }).add();
        channel.messageBuilder(MessageNote.class, 5, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageNote::write).decoder(MessageNote::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { HoldView.note(message.said(), message.backdrop(), message.font()); } }).add();
        channel.messageBuilder(MessageHush.class, 6, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageHush::write).decoder(MessageHush::read).consumerNetworkThread((message, context) -> {
            if (FMLEnvironment.dist == Dist.CLIENT) { BlastHush.mark(message); }
            return true;
        }).add();
        channel.messageBuilder(MessageBossBackground.class, 11, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageBossBackground::write).decoder(MessageBossBackground::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { BossBackgrounds.set(message); } }).add();
        channel.messageBuilder(MessageToasts.class, 7, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageToasts::write).decoder(MessageToasts::read).consumerMainThread((message, context) -> Toasts.show(message.kinds())).add();
        channel.messageBuilder(MessageAnvilCosts.class, 10, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageAnvilCosts::write).decoder(MessageAnvilCosts::read).consumerMainThread((message, context) -> AnvilCosts.show(message.cap(), message.paid(), message.lapis(), message.opening())).add();
        channel.messageBuilder(MessageIntroPlay.class, 2, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageIntroPlay::write).decoder(MessageIntroPlay::read).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { WorldIntroScreen.open(message.landBeingMade()); } }).add();
        channel.messageBuilder(MessageIntroLandMade.class, 8, NetworkDirection.PLAY_TO_CLIENT).encoder((message, buf) -> {}).decoder(buf -> new MessageIntroLandMade()).consumerMainThread((message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { WorldIntroScreen.landMade(); } }).add();
        channel.messageBuilder(MessageEnvironments.class, 9, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageEnvironments::write).decoder(MessageEnvironments::read).consumerMainThread((message, context) -> {
            ContentEnvironments.receive(message.active(), message.whole());
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ContentRuleClient.settle();
                ContentSeasonTint.changed();
            }
        }).add();
        channel.messageBuilder(MessageIntroDone.class, 3, NetworkDirection.PLAY_TO_SERVER).encoder((message, buf) -> {}).decoder(buf -> new MessageIntroDone()).consumerMainThread((message, context) -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) { ContentIntroPlay.finished(player); }
        }).add();
        channel.messageBuilder(MessageOpenWorn.class, 4, NetworkDirection.PLAY_TO_SERVER).encoder((message, buf) -> {}).decoder(buf -> new MessageOpenWorn()).consumerMainThread((message, context) -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) { ContentWornContainers.open(player); }
        }).add();
        channel.messageBuilder(MessagePortwayFilter.class, 12, NetworkDirection.PLAY_TO_SERVER).encoder(MessagePortwayFilter::write).decoder(MessagePortwayFilter::read).consumerMainThread((message, context) -> {
            ServerPlayer player = context.get().getSender();
            if (player != null && player.containerMenu instanceof ContentPortwayMenu menu) { menu.filter(message.text()); }
        }).add();
    }

    public static void openWorn() { if (channel != null) { channel.sendToServer(new MessageOpenWorn()); } }

    public static void portwayFilter(String text) { if (channel != null) { channel.sendToServer(new MessagePortwayFilter(text)); } }

    private static <MSG> boolean toPlayer(ServerPlayer player, MSG message) {
        if (channel == null || !reaches(player)) { return false; }
        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
        return true;
    }

    public static void sendHold(ServerPlayer player, boolean held, String warning, boolean fog) { toPlayer(player, new MessageHold(held, warning, fog, Says.panel(), Says.font())); }

    public static boolean sendNote(ServerPlayer player, String said) { return toPlayer(player, new MessageNote(said, Says.panel(), Says.font())); }

    public static boolean hush(ServerPlayer player, double x, double y, double z) { return toPlayer(player, new MessageHush(x, y, z)); }

    public static void playIntro(ServerPlayer player, boolean landBeingMade) { toPlayer(player, new MessageIntroPlay(landBeingMade)); }

    public static void introLandMade(ServerPlayer player) { toPlayer(player, new MessageIntroLandMade()); }

    public static void sendBossBackground(ServerPlayer player, UUID bar, @Nullable BossBackgroundDef background) {
        if (background == null) { toPlayer(player, new MessageBossBackground(bar, "", 0, 0, 0, 0)); }
        else { toPlayer(player, new MessageBossBackground(bar, background.texture(), background.width(), background.height(), background.x(), background.y())); }
    }

    public static void sendToasts(ServerPlayer player, int kinds) { toPlayer(player, new MessageToasts(kinds)); }

    public static void sendAnvilCosts(ServerPlayer player) { toPlayer(player, new MessageAnvilCosts(AnvilCosts.servedCap(), AnvilCosts.servedPaid(), AnvilCosts.servedLapis(), AnvilCosts.servedOpening())); }

    public static void introDone() { if (channel != null) { channel.sendToServer(new MessageIntroDone()); } }

    public static boolean reaches(ServerPlayer player) { return channel != null && !NetworkHooks.isVanillaConnection(player.connection.connection) && channel.isRemotePresent(player.connection.connection); }

    public static void sendCard(ServerPlayer player, MessageCard card) { toPlayer(player, card); }

    public static void sendEnvironments(ServerPlayer player, MessageEnvironments environments) { toPlayer(player, environments); }
}
