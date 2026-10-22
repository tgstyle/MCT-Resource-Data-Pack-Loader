package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.worldgen.ContentPregen;
import mctmods.resourcedatapackloader.client.CardOverlay;
import mctmods.resourcedatapackloader.client.HoldView;
import mctmods.resourcedatapackloader.client.IntroLandMadeHandler;
import mctmods.resourcedatapackloader.client.IntroPlayHandler;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public final class RDPLNetwork {
    public static final String CHANNEL = "rdpl";
    private static SimpleNetworkWrapper channel;
    private static byte packetId;

    private RDPLNetwork() {}

    public static void register() {
        if (channel != null) { return; }
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
        registerClient(() -> IntroPlayHandler.class, MessageIntroPlay.class);
        registerClient(() -> IntroLandMadeHandler.class, MessageIntroLandMade.class);
        registerMessage(MessageIntroDone.Handler.class, MessageIntroDone.class);
        registerMessage(MessagePouchKey.Handler.class, MessagePouchKey.class);
        registerClient(() -> CardOverlay.Handler.class, MessageCard.class);
        registerMessage(MessageHardnessSalt.Handler.class, MessageHardnessSalt.class);
        registerClient(() -> HoldView.Handler.class, MessageHold.class);
        registerClient(() -> HoldView.NoteHandler.class, MessageNote.class);
        registerMessage(MessageCubes.Handler.class, MessageCubes.class);
        registerMessage(MessageColumn.Handler.class, MessageColumn.class);
        registerMessage(MessageUnloadColumn.Handler.class, MessageUnloadColumn.class);
        registerMessage(MessageUnloadCube.Handler.class, MessageUnloadCube.class);
        registerMessage(MessageCubeBlockChange.Handler.class, MessageCubeBlockChange.class);
        registerMessage(MessageRubicWorldData.Handler.class, MessageRubicWorldData.class);
        registerMessage(MessageHeightMapUpdate.Handler.class, MessageHeightMapUpdate.class);
        registerMessage(MessageToasts.Handler.class, MessageToasts.class);
        registerMessage(MessageEnvironments.Handler.class, MessageEnvironments.class);
    }

    private static <REQ extends IMessage> void registerClient(Supplier<Class<? extends IMessageHandler<REQ, IMessage>>> client, Class<REQ> messageClass) {
        Class<? extends IMessageHandler<REQ, IMessage>> handlerClass = FMLCommonHandler.instance().getSide().isClient() ? client.get() : idle();
        channel.registerMessage(handlerClass, messageClass, packetId++, Side.CLIENT);
    }

    @SuppressWarnings("unchecked") private static <REQ extends IMessage> Class<? extends IMessageHandler<REQ, IMessage>> idle() {
        Class<?> idle = IdleHandler.class;
        return (Class<? extends IMessageHandler<REQ, IMessage>>) idle;
    }

    private static boolean unreachable(EntityPlayerMP player) { return channel == null || vanilla(player); }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, Class<REQ> messageClass) {
        Side side = AbstractClientMessageHandler.class.isAssignableFrom(handlerClass) ? Side.CLIENT : Side.SERVER;
        channel.registerMessage(handlerClass, messageClass, packetId++, side);
    }

    public static boolean vanilla(EntityPlayerMP player) { return vanilla(player.connection); }

    public static boolean vanilla(@Nullable NetHandlerPlayServer connection) {
        if (connection == null) { return true; }
        Boolean marked = connection.netManager.channel().attr(NetworkRegistry.FML_MARKER).get();
        return marked == null || !marked;
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        if (unreachable(player)) { return; }
        channel.sendTo(message, player);
    }

    public static void playIntro(EntityPlayerMP player) { sendTo(new MessageIntroPlay(ContentPregen.busy()), player); }

    public static void introLandMade(EntityPlayerMP player) { sendTo(new MessageIntroLandMade(), player); }

    public static boolean sendNote(EntityPlayerMP player, String said) {
        if (unreachable(player)) { return false; }
        channel.sendTo(new MessageNote(said, Says.panel(), Says.font()), player);
        return true;
    }

    public static void sendHold(EntityPlayerMP player, boolean held, String warning, boolean fog) { sendTo(new MessageHold(held, warning, fog, Says.panel(), Says.font()), player); }

    public static void sendHardnessSalt(EntityPlayerMP player, long salt) { sendTo(new MessageHardnessSalt(salt), player); }

    public static void sendToasts(EntityPlayerMP player, int kinds) { sendTo(new MessageToasts(kinds), player); }

    public static void introDone() {
        if (channel == null) { return; }
        channel.sendToServer(new MessageIntroDone());
    }

    public static void openWornPouch() { channel.sendToServer(new MessagePouchKey()); }
}
