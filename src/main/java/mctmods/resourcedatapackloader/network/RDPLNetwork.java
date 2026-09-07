package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.client.CardOverlay;
import mctmods.resourcedatapackloader.client.HoldView;
import mctmods.resourcedatapackloader.client.WorldIntroScreen;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class RDPLNetwork {
    private static final String VERSION = "1";

    private RDPLNetwork() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION).optional();
        registrar.playToClient(MessageCard.TYPE, MessageCard.CODEC, (message, context) -> {
            if (FMLEnvironment.dist == Dist.CLIENT) { CardOverlay.show(message); }
        });
        registrar.playToClient(MessageHold.TYPE, MessageHold.CODEC, (message, context) -> {
            if (FMLEnvironment.dist == Dist.CLIENT) { HoldView.set(message.held(), message.warning()); }
        });
        registrar.playToClient(MessageIntroPlay.TYPE, MessageIntroPlay.CODEC, (message, context) -> {
            if (FMLEnvironment.dist == Dist.CLIENT) { WorldIntroScreen.open(message.landBeingMade()); }
        });
        registrar.playToServer(MessageIntroDone.TYPE, MessageIntroDone.CODEC, (message, context) -> {
            if (context.player() instanceof ServerPlayer player) { ContentIntroPlay.finished(player); }
        });
    }

    public static void sendHold(ServerPlayer player, boolean held, String warning) {
        if (reaches(player)) { PacketDistributor.sendToPlayer(player, new MessageHold(held, warning)); }
    }

    public static void playIntro(ServerPlayer player, boolean landBeingMade) {
        if (reaches(player)) { PacketDistributor.sendToPlayer(player, new MessageIntroPlay(landBeingMade)); }
    }

    public static void introDone() { PacketDistributor.sendToServer(new MessageIntroDone()); }

    public static boolean reaches(ServerPlayer player) { return player.connection.hasChannel(MessageCard.TYPE); }

    public static void sendCard(ServerPlayer player, MessageCard card) {
        if (reaches(player)) { PacketDistributor.sendToPlayer(player, card); }
    }
}
