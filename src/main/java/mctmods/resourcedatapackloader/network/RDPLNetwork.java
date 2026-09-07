package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.client.CardOverlay;
import mctmods.resourcedatapackloader.client.HoldView;
import mctmods.resourcedatapackloader.client.WorldIntroScreen;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import javax.annotation.Nullable;

public final class RDPLNetwork {
    private static final ResourceLocation NAME = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "main");
    private static final String VERSION = "1";
    @Nullable private static SimpleChannel channel;

    private RDPLNetwork() {}

    public static void register() {
        if (channel != null) { return; }
        channel = NetworkRegistry.newSimpleChannel(NAME, () -> VERSION, NetworkRegistry.acceptMissingOr(VERSION), NetworkRegistry.acceptMissingOr(VERSION));
        channel.messageBuilder(MessageCard.class, 0, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageCard::write).decoder(MessageCard::read).consumerMainThread((message, context) -> {
            if (FMLEnvironment.dist == Dist.CLIENT) { CardOverlay.show(message); }
        }).add();
        channel.messageBuilder(MessageHold.class, 1, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageHold::write).decoder(MessageHold::read).consumerMainThread((message, context) -> {
            if (FMLEnvironment.dist == Dist.CLIENT) { HoldView.set(message.held(), message.warning()); }
        }).add();
        channel.messageBuilder(MessageIntroPlay.class, 2, NetworkDirection.PLAY_TO_CLIENT).encoder(MessageIntroPlay::write).decoder(MessageIntroPlay::read).consumerMainThread((message, context) -> {
            if (FMLEnvironment.dist == Dist.CLIENT) { WorldIntroScreen.open(message.landBeingMade()); }
        }).add();
        channel.messageBuilder(MessageIntroDone.class, 3, NetworkDirection.PLAY_TO_SERVER).encoder((message, buf) -> {}).decoder(buf -> new MessageIntroDone()).consumerMainThread((message, context) -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) { ContentIntroPlay.finished(player); }
        }).add();
    }

    public static void sendHold(ServerPlayer player, boolean held, String warning) {
        if (channel != null && reaches(player)) { channel.send(PacketDistributor.PLAYER.with(() -> player), new MessageHold(held, warning)); }
    }

    public static void playIntro(ServerPlayer player, boolean landBeingMade) {
        if (channel != null && reaches(player)) { channel.send(PacketDistributor.PLAYER.with(() -> player), new MessageIntroPlay(landBeingMade)); }
    }

    public static void introDone() {
        if (channel != null) { channel.sendToServer(new MessageIntroDone()); }
    }

    public static boolean reaches(ServerPlayer player) {
        return channel != null && !NetworkHooks.isVanillaConnection(player.connection.connection) && channel.isRemotePresent(player.connection.connection);
    }

    public static void sendCard(ServerPlayer player, MessageCard card) {
        if (channel != null && reaches(player)) { channel.send(PacketDistributor.PLAYER.with(() -> player), card); }
    }
}
