package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.client.IntroPlayHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class RDPLNetwork {
    public static final String CHANNEL = "rdpl";
    private static SimpleNetworkWrapper channel;

    private RDPLNetwork() {}

    public static void register() {
        if (channel != null) { return; }
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
        if (FMLCommonHandler.instance().getSide().isClient()) { channel.registerMessage(IntroPlayHandler.class, MessageIntroPlay.class, 0, Side.CLIENT); }
        else { channel.registerMessage(MessageIntroPlay.Idle.class, MessageIntroPlay.class, 0, Side.CLIENT); }

        channel.registerMessage(MessageIntroDone.Handler.class, MessageIntroDone.class, 1, Side.SERVER);
        channel.registerMessage(MessageHardnessSalt.Handler.class, MessageHardnessSalt.class, 2, Side.CLIENT);
    }

    public static boolean vanilla(EntityPlayerMP player) {
        if (player.connection == null) { return true; }

        Boolean marked = player.connection.netManager.channel().attr(NetworkRegistry.FML_MARKER).get();
        return marked == null || !marked;
    }

    public static void playIntro(EntityPlayerMP player) {
        if (channel == null || vanilla(player)) { return; }
        channel.sendTo(new MessageIntroPlay(), player);
    }

    public static void sendHardnessSalt(EntityPlayerMP player, long salt) {
        if (channel == null || vanilla(player)) { return; }

        channel.sendTo(new MessageHardnessSalt(salt), player);
    }

    public static void introDone() {
        if (channel == null) { return; }
        channel.sendToServer(new MessageIntroDone());
    }
}
