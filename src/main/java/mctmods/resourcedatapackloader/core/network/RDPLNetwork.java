package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.client.gui.TalkScreen;
import mctmods.resourcedatapackloader.client.gui.hud.BossBackgrounds;
import mctmods.resourcedatapackloader.client.gui.hud.CardOverlay;
import mctmods.resourcedatapackloader.client.gui.hud.HeartsHud;
import mctmods.resourcedatapackloader.client.gui.hud.HoldView;
import mctmods.resourcedatapackloader.client.gui.intro.WorldIntroScreen;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSeasonTint;
import mctmods.resourcedatapackloader.client.renderer.sky.ContentSkyGates;
import mctmods.resourcedatapackloader.common.anvils.AnvilCosts;
import mctmods.resourcedatapackloader.common.dialogs.DialogTalk;
import mctmods.resourcedatapackloader.common.dimension.ContentEnvironments;
import mctmods.resourcedatapackloader.common.gui.ContentPortwayMenu;
import mctmods.resourcedatapackloader.common.hearts.ContentHearts;
import mctmods.resourcedatapackloader.common.intro.ContentIntroPlay;
import mctmods.resourcedatapackloader.common.items.ContentWornContainers;
import mctmods.resourcedatapackloader.common.pack.definitions.entities.BossBackgroundDef;
import mctmods.resourcedatapackloader.common.rules.ContentRuleSwitch;
import mctmods.resourcedatapackloader.common.util.messages.Says;
import mctmods.resourcedatapackloader.common.util.messages.Toasts;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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
        registrar.playToClient(CardMessage.TYPE, CardMessage.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { CardOverlay.show(message); } });
        registrar.playToClient(HoldMessage.TYPE, HoldMessage.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { HoldView.set(message.held(), message.warning(), message.fog(), message.backdrop(), message.font()); } });
        registrar.playToClient(NoteMessage.TYPE, NoteMessage.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { HoldView.note(message.said(), message.backdrop(), message.font()); } });
        registrar.playToClient(BossBackgroundMessage.TYPE, BossBackgroundMessage.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { BossBackgrounds.set(message); } });
        registrar.playToClient(ToastsMessage.TYPE, ToastsMessage.CODEC, (message, context) -> Toasts.show(message.kinds()));
        registrar.playToClient(AnvilCostsMessage.TYPE, AnvilCostsMessage.CODEC, (message, context) -> AnvilCosts.show(message.cap(), ints(message.paid()), ints(message.lapis()), message.opening()));
        registrar.playToClient(HeartColorsMessage.TYPE, HeartColorsMessage.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { HeartsHud.show(message.colors()); } });
        registrar.playToClient(GatesMessage.TYPE, GatesMessage.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { ContentSkyGates.receive(message.open()); } });
        registrar.playToClient(IntroPlayMessage.TYPE, IntroPlayMessage.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { WorldIntroScreen.open(message.landBeingMade()); } });
        registrar.playToClient(IntroLandMadeMessage.TYPE, IntroLandMadeMessage.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { WorldIntroScreen.landMade(); } });
        registrar.playToClient(EnvironmentsMessage.TYPE, EnvironmentsMessage.CODEC, (message, context) -> {
            ContentEnvironments.receive(message.active(), message.whole());
            ContentRuleSwitch.settle(context.player().level());
            if (FMLEnvironment.dist == Dist.CLIENT) { ContentSeasonTint.changed(); }
        });
        registrar.playToServer(IntroDoneMessage.TYPE, IntroDoneMessage.CODEC, (message, context) -> { if (context.player() instanceof ServerPlayer player) { ContentIntroPlay.finished(player); } });
        registrar.playToServer(OpenWornMessage.TYPE, OpenWornMessage.CODEC, (message, context) -> { if (context.player() instanceof ServerPlayer player) { ContentWornContainers.open(player); } });
        registrar.playToClient(TalkMessage.TYPE, TalkMessage.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { TalkScreen.receive(message); } });
        registrar.playToServer(TalkPickMessage.TYPE, TalkPickMessage.CODEC, (message, context) -> { if (context.player() instanceof ServerPlayer player) { DialogTalk.pick(player, message.index()); } });
        registrar.playToServer(PortwayFilterMessage.TYPE, PortwayFilterMessage.CODEC, (message, context) -> { if (context.player().containerMenu instanceof ContentPortwayMenu menu) { menu.filter(message.text()); } });
    }

    public static void openWorn() { PacketDistributor.sendToServer(new OpenWornMessage()); }

    public static void portwayFilter(String text) { PacketDistributor.sendToServer(new PortwayFilterMessage(text)); }

    private static boolean send(ServerPlayer player, CustomPacketPayload payload) {
        if (!player.connection.hasChannel(payload.type())) { return false; }
        PacketDistributor.sendToPlayer(player, payload);
        return true;
    }

    public static void sendHold(ServerPlayer player, boolean held, String warning, boolean fog) { send(player, new HoldMessage(held, warning, fog, Says.panel(), Says.font())); }

    public static boolean sendNote(ServerPlayer player, String said) { return send(player, new NoteMessage(said, Says.panel(), Says.font())); }

    public static void playIntro(ServerPlayer player, boolean landBeingMade) { send(player, new IntroPlayMessage(landBeingMade)); }

    public static void introLandMade(ServerPlayer player) { send(player, new IntroLandMadeMessage()); }

    public static void sendBossBackground(ServerPlayer player, UUID bar, @Nullable BossBackgroundDef background) {
        if (background == null) { send(player, new BossBackgroundMessage(bar, "", 0, 0, 0, 0)); }
        else { send(player, new BossBackgroundMessage(bar, background.texture(), background.width(), background.height(), background.x(), background.y())); }
    }

    public static void sendToasts(ServerPlayer player, int kinds) { send(player, new ToastsMessage(kinds)); }

    public static void sendAnvilCosts(ServerPlayer player) { send(player, new AnvilCostsMessage(AnvilCosts.servedCap(), boxed(AnvilCosts.servedPaid()), boxed(AnvilCosts.servedLapis()), AnvilCosts.servedOpening())); }

    public static void sendHeartColors(ServerPlayer player) { send(player, new HeartColorsMessage(boxed(ContentHearts.colors()))); }

    public static void sendGates(ServerPlayer player, Set<String> open) { send(player, new GatesMessage(open.stream().limit(GatesMessage.MOST).toList())); }

    private static List<Integer> boxed(int[] values) { return Arrays.stream(values).boxed().toList(); }

    private static int[] ints(List<Integer> values) { return values.stream().mapToInt(Integer::intValue).toArray(); }

    public static void introDone() { PacketDistributor.sendToServer(new IntroDoneMessage()); }

    public static boolean reaches(ServerPlayer player) { return player.connection.hasChannel(CardMessage.TYPE); }

    public static void sendCard(ServerPlayer player, CardMessage card) { send(player, card); }

    public static void sendTalk(ServerPlayer player, TalkMessage talk) { send(player, talk); }

    public static void sendTalkPick(int index) { PacketDistributor.sendToServer(new TalkPickMessage(index)); }

    public static void sendEnvironments(ServerPlayer player, EnvironmentsMessage environments) { send(player, environments); }
}
