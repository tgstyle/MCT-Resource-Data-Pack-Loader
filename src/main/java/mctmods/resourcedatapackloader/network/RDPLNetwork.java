package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.client.CardOverlay;

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
    }

    public static boolean reaches(ServerPlayer player) {
        return channel != null && player.connection != null && !NetworkHooks.isVanillaConnection(player.connection.connection) && channel.isRemotePresent(player.connection.connection);
    }

    public static void sendCard(ServerPlayer player, MessageCard card) {
        if (channel != null && reaches(player)) { channel.send(PacketDistributor.PLAYER.with(() -> player), card); }
    }
}
