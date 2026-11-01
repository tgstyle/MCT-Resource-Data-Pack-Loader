package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.client.gui.TalkScreen;
import mctmods.resourcedatapackloader.client.gui.hud.BossBackgrounds;
import mctmods.resourcedatapackloader.client.gui.hud.CardOverlay;
import mctmods.resourcedatapackloader.client.gui.hud.HeartsHud;
import mctmods.resourcedatapackloader.client.gui.hud.HoldView;
import mctmods.resourcedatapackloader.client.gui.intro.IntroLandMadeHandler;
import mctmods.resourcedatapackloader.client.gui.intro.IntroPlayHandler;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyGates;
import mctmods.resourcedatapackloader.common.anvils.AnvilCosts;
import mctmods.resourcedatapackloader.common.hearts.ContentHearts;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.BossBackgroundDef;
import mctmods.resourcedatapackloader.common.pregen.ContentPregen;
import mctmods.resourcedatapackloader.common.util.messages.Says;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.core.network.handler.IdleHandler;
import mctmods.resourcedatapackloader.core.network.world.ColumnMessage;
import mctmods.resourcedatapackloader.core.network.world.CubeBlockChangeMessage;
import mctmods.resourcedatapackloader.core.network.world.CubesMessage;
import mctmods.resourcedatapackloader.core.network.world.HeightMapUpdateMessage;
import mctmods.resourcedatapackloader.core.network.world.MultipartDescMessage;
import mctmods.resourcedatapackloader.core.network.world.RubicWorldDataMessage;
import mctmods.resourcedatapackloader.core.network.world.UnloadColumnMessage;
import mctmods.resourcedatapackloader.core.network.world.UnloadCubeMessage;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
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
        registerClient(() -> IntroPlayHandler.class, IntroPlayMessage.class);
        registerClient(() -> IntroLandMadeHandler.class, IntroLandMadeMessage.class);
        registerMessage(IntroDoneMessage.Handler.class, IntroDoneMessage.class);
        registerMessage(PouchKeyMessage.Handler.class, PouchKeyMessage.class);
        registerClient(() -> CardOverlay.Handler.class, CardMessage.class);
        registerMessage(HardnessSaltMessage.Handler.class, HardnessSaltMessage.class);
        registerClient(() -> HoldView.Handler.class, HoldMessage.class);
        registerClient(() -> HoldView.NoteHandler.class, NoteMessage.class);
        registerMessage(CubesMessage.Handler.class, CubesMessage.class);
        registerMessage(ColumnMessage.Handler.class, ColumnMessage.class);
        registerMessage(UnloadColumnMessage.Handler.class, UnloadColumnMessage.class);
        registerMessage(UnloadCubeMessage.Handler.class, UnloadCubeMessage.class);
        registerMessage(CubeBlockChangeMessage.Handler.class, CubeBlockChangeMessage.class);
        registerMessage(RubicWorldDataMessage.Handler.class, RubicWorldDataMessage.class);
        registerMessage(HeightMapUpdateMessage.Handler.class, HeightMapUpdateMessage.class);
        registerMessage(ToastsMessage.Handler.class, ToastsMessage.class);
        registerMessage(EnvironmentsMessage.Handler.class, EnvironmentsMessage.class);
        registerClient(() -> BossBackgrounds.Handler.class, BossBackgroundMessage.class);
        registerMessage(MultipartDescMessage.Handler.class, MultipartDescMessage.class);
        registerMessage(AnvilCostsMessage.Handler.class, AnvilCostsMessage.class);
        registerMessage(PortwayFilterMessage.Handler.class, PortwayFilterMessage.class);
        registerClient(() -> TalkScreen.Handler.class, TalkMessage.class);
        registerMessage(TalkPickMessage.Handler.class, TalkPickMessage.class);
        registerClient(() -> HeartsHud.Handler.class, HeartColorsMessage.class);
        registerClient(() -> ContentSkyGates.Handler.class, GatesMessage.class);
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

    public static void playIntro(EntityPlayerMP player) { sendTo(new IntroPlayMessage(ContentPregen.busy()), player); }

    public static void introLandMade(EntityPlayerMP player) { sendTo(new IntroLandMadeMessage(), player); }

    public static boolean sendNote(EntityPlayerMP player, String said) {
        if (unreachable(player)) { return false; }
        channel.sendTo(new NoteMessage(said, Says.panel(), Says.font()), player);
        return true;
    }

    public static void sendHold(EntityPlayerMP player, boolean held, String warning, boolean fog) { sendTo(new HoldMessage(held, warning, fog, Says.panel(), Says.font()), player); }

    public static void sendHardnessSalt(EntityPlayerMP player, long salt) { sendTo(new HardnessSaltMessage(salt), player); }

    public static void sendBossBackground(EntityPlayerMP player, UUID bar, @Nullable BossBackgroundDef background) {
        if (background == null) { sendTo(new BossBackgroundMessage(bar, "", 0, 0, 0, 0), player); }
        else { sendTo(new BossBackgroundMessage(bar, background.texture, background.width, background.height, background.x, background.y), player); }
    }

    public static void sendToasts(EntityPlayerMP player, int kinds) { sendTo(new ToastsMessage(kinds), player); }

    public static void sendAnvilCosts(EntityPlayerMP player) { sendTo(new AnvilCostsMessage(AnvilCosts.servedCap(), AnvilCosts.servedPaid(), AnvilCosts.servedLapis(), AnvilCosts.servedOpening()), player); }

    public static void sendHeartColors(EntityPlayerMP player) { sendTo(new HeartColorsMessage(ContentHearts.colors()), player); }

    public static void sendGates(EntityPlayerMP player, Collection<String> gates) { sendTo(new GatesMessage(gates), player); }

    public static void introDone() {
        if (channel == null) { return; }
        channel.sendToServer(new IntroDoneMessage());
    }

    public static void openWornPouch() { channel.sendToServer(new PouchKeyMessage()); }

    public static void portwayFilter(String text) { channel.sendToServer(new PortwayFilterMessage(text)); }

    public static void sendTalkPick(int index) {
        if (channel == null) { return; }
        channel.sendToServer(new TalkPickMessage(index));
    }
}
