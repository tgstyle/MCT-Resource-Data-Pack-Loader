package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.client.IntroPlayHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class RDPLNetwork {
    public static final String CHANNEL = "rdpl";
    private static SimpleNetworkWrapper channel;
    private static byte packetId;

    private RDPLNetwork() {}

    public static void register() {
        if (channel != null) { return; }
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
        if (FMLCommonHandler.instance().getSide().isClient()) { channel.registerMessage(IntroPlayHandler.class, MessageIntroPlay.class, packetId++, Side.CLIENT); }
        else { channel.registerMessage(MessageIntroPlay.Idle.class, MessageIntroPlay.class, packetId++, Side.CLIENT); }
        channel.registerMessage(MessageIntroDone.Handler.class, MessageIntroDone.class, packetId++, Side.SERVER);
        channel.registerMessage(MessageHardnessSalt.Handler.class, MessageHardnessSalt.class, packetId++, Side.CLIENT);
        registerMessage(MessageCubes.Handler.class, MessageCubes.class);
        registerMessage(MessageColumn.Handler.class, MessageColumn.class);
        registerMessage(MessageUnloadColumn.Handler.class, MessageUnloadColumn.class);
        registerMessage(MessageUnloadCube.Handler.class, MessageUnloadCube.class);
        registerMessage(MessageCubeBlockChange.Handler.class, MessageCubeBlockChange.class);
        registerMessage(MessageRubicWorldData.Handler.class, MessageRubicWorldData.class);
        registerMessage(MessageHeightMapUpdate.Handler.class, MessageHeightMapUpdate.class);
        registerMessage(MessageCubeSkyLightUpdates.Handler.class, MessageCubeSkyLightUpdates.class);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, Class<REQ> messageClass) {
        Side side = AbstractClientMessageHandler.class.isAssignableFrom(handlerClass) ? Side.CLIENT : Side.SERVER;
        channel.registerMessage(handlerClass, messageClass, packetId++, side);
    }

    public static boolean vanilla(EntityPlayerMP player) {
        if (player.connection == null) { return true; }
        Boolean marked = player.connection.netManager.channel().attr(NetworkRegistry.FML_MARKER).get();
        return marked == null || !marked;
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) { channel.sendTo(message, player); }

    public static void playIntro(EntityPlayerMP player) {
        if (channel == null || vanilla(player)) { return; }
        channel.sendTo(new MessageIntroPlay(ContentPregen.busy()), player);
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
