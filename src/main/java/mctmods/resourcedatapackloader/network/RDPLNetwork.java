package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.client.CardOverlay;

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
    }

    public static boolean reaches(ServerPlayer player) { return player.connection.hasChannel(MessageCard.TYPE); }

    public static void sendCard(ServerPlayer player, MessageCard card) {
        if (reaches(player)) { PacketDistributor.sendToPlayer(player, card); }
    }
}
