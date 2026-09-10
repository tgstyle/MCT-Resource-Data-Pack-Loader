package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.client.CardOverlay;
import mctmods.resourcedatapackloader.client.HoldView;
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
        channel.registerMessage(MessagePouchKey.Handler.class, MessagePouchKey.class, packetId++, Side.SERVER);
        if (FMLCommonHandler.instance().getSide().isClient()) { channel.registerMessage(CardOverlay.Handler.class, MessageCard.class, packetId++, Side.CLIENT); }
        else { channel.registerMessage(MessageCard.Idle.class, MessageCard.class, packetId++, Side.CLIENT); }
        channel.registerMessage(MessageHardnessSalt.Handler.class, MessageHardnessSalt.class, packetId++, Side.CLIENT);
        if (FMLCommonHandler.instance().getSide().isClient()) { channel.registerMessage(HoldView.Handler.class, MessageHold.class, packetId++, Side.CLIENT); }
        else { channel.registerMessage(MessageHold.Idle.class, MessageHold.class, packetId++, Side.CLIENT); }
        if (FMLCommonHandler.instance().getSide().isClient()) { channel.registerMessage(HoldView.NoteHandler.class, MessageNote.class, packetId++, Side.CLIENT); }
        else { channel.registerMessage(MessageNote.Idle.class, MessageNote.class, packetId++, Side.CLIENT); }
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

    public static boolean vanilla(EntityPlayerMP player) { return vanilla(player.connection); }

    public static boolean vanilla(@javax.annotation.Nullable net.minecraft.network.NetHandlerPlayServer connection) {
        if (connection == null) { return true; }
        Boolean marked = connection.netManager.channel().attr(NetworkRegistry.FML_MARKER).get();
        return marked == null || !marked;
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        if (channel == null || vanilla(player)) { return; }
        channel.sendTo(message, player);
    }

    public static void playIntro(EntityPlayerMP player) {
        if (channel == null || vanilla(player)) { return; }
        channel.sendTo(new MessageIntroPlay(ContentPregen.busy()), player);
    }

    public static boolean sendNote(EntityPlayerMP player, String said) {
        if (channel == null || vanilla(player)) { return false; }
        channel.sendTo(new MessageNote(said), player);
        return true;
    }

    public static void sendHold(EntityPlayerMP player, boolean held, String warning, boolean fog) {
        if (channel == null || vanilla(player)) { return; }
        channel.sendTo(new MessageHold(held, warning, fog), player);
    }

    public static void sendHardnessSalt(EntityPlayerMP player, long salt) {
        if (channel == null || vanilla(player)) { return; }
        channel.sendTo(new MessageHardnessSalt(salt), player);
    }

    public static void introDone() {
        if (channel == null) { return; }
        channel.sendToServer(new MessageIntroDone());
    }

    public static void openWornPouch() { channel.sendToServer(new MessagePouchKey()); }
}
