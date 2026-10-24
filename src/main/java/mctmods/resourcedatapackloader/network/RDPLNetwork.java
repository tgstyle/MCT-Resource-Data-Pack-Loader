package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.client.BossBackgrounds;
import mctmods.resourcedatapackloader.client.CardOverlay;
import mctmods.resourcedatapackloader.client.ContentSeasonTint;
import mctmods.resourcedatapackloader.client.HeartsHud;
import mctmods.resourcedatapackloader.client.HoldView;
import mctmods.resourcedatapackloader.client.TalkScreen;
import mctmods.resourcedatapackloader.client.WorldIntroScreen;
import mctmods.resourcedatapackloader.content.AnvilCosts;
import mctmods.resourcedatapackloader.content.ContentHearts;
import mctmods.resourcedatapackloader.content.def.BossBackgroundDef;
import mctmods.resourcedatapackloader.content.dialog.DialogTalk;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.content.worldgen.ContentEnvironments;
import mctmods.resourcedatapackloader.content.worldgen.ContentRuleSwitch;
import mctmods.resourcedatapackloader.content.item.ContentWornContainers;
import mctmods.resourcedatapackloader.content.menu.ContentPortwayMenu;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Toasts;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public final class RDPLNetwork {
    private static final String VERSION = "1";

    private RDPLNetwork() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION).optional();
        registrar.playToClient(MessageCard.TYPE, MessageCard.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { CardOverlay.show(message); } });
        registrar.playToClient(MessageHold.TYPE, MessageHold.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { HoldView.set(message.held(), message.warning(), message.fog(), message.backdrop(), message.font()); } });
        registrar.playToClient(MessageNote.TYPE, MessageNote.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { HoldView.note(message.said(), message.backdrop(), message.font()); } });
        registrar.playToClient(MessageBossBackground.TYPE, MessageBossBackground.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { BossBackgrounds.set(message); } });
        registrar.playToClient(MessageToasts.TYPE, MessageToasts.CODEC, (message, context) -> Toasts.show(message.kinds()));
        registrar.playToClient(MessageAnvilCosts.TYPE, MessageAnvilCosts.CODEC, (message, context) -> AnvilCosts.show(message.cap(), ints(message.paid()), ints(message.lapis()), message.opening()));
        registrar.playToClient(MessageHeartColors.TYPE, MessageHeartColors.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { HeartsHud.show(message.colors()); } });
        registrar.playToClient(MessageIntroPlay.TYPE, MessageIntroPlay.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { WorldIntroScreen.open(message.landBeingMade()); } });
        registrar.playToClient(MessageIntroLandMade.TYPE, MessageIntroLandMade.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { WorldIntroScreen.landMade(); } });
        registrar.playToClient(MessageEnvironments.TYPE, MessageEnvironments.CODEC, (message, context) -> {
            ContentEnvironments.receive(message.active(), message.whole());
            ContentRuleSwitch.settle(context.player().level());
            if (FMLEnvironment.dist == Dist.CLIENT) { ContentSeasonTint.changed(); }
        });
        registrar.playToServer(MessageIntroDone.TYPE, MessageIntroDone.CODEC, (message, context) -> { if (context.player() instanceof ServerPlayer player) { ContentIntroPlay.finished(player); } });
        registrar.playToServer(MessageOpenWorn.TYPE, MessageOpenWorn.CODEC, (message, context) -> { if (context.player() instanceof ServerPlayer player) { ContentWornContainers.open(player); } });
        registrar.playToClient(MessageTalk.TYPE, MessageTalk.CODEC, (message, context) -> { if (FMLEnvironment.dist == Dist.CLIENT) { TalkScreen.receive(message); } });
        registrar.playToServer(MessageTalkPick.TYPE, MessageTalkPick.CODEC, (message, context) -> { if (context.player() instanceof ServerPlayer player) { DialogTalk.pick(player, message.index()); } });
        registrar.playToServer(MessagePortwayFilter.TYPE, MessagePortwayFilter.CODEC, (message, context) -> { if (context.player().containerMenu instanceof ContentPortwayMenu menu) { menu.filter(message.text()); } });
    }

    public static void openWorn() { PacketDistributor.sendToServer(new MessageOpenWorn()); }

    public static void portwayFilter(String text) { PacketDistributor.sendToServer(new MessagePortwayFilter(text)); }

    private static boolean send(ServerPlayer player, CustomPacketPayload payload) {
        if (!player.connection.hasChannel(payload.type())) { return false; }
        PacketDistributor.sendToPlayer(player, payload);
        return true;
    }

    public static void sendHold(ServerPlayer player, boolean held, String warning, boolean fog) { send(player, new MessageHold(held, warning, fog, Says.panel(), Says.font())); }

    public static boolean sendNote(ServerPlayer player, String said) { return send(player, new MessageNote(said, Says.panel(), Says.font())); }

    public static void playIntro(ServerPlayer player, boolean landBeingMade) { send(player, new MessageIntroPlay(landBeingMade)); }

    public static void introLandMade(ServerPlayer player) { send(player, new MessageIntroLandMade()); }

    public static void sendBossBackground(ServerPlayer player, UUID bar, @Nullable BossBackgroundDef background) {
        if (background == null) { send(player, new MessageBossBackground(bar, "", 0, 0, 0, 0)); }
        else { send(player, new MessageBossBackground(bar, background.texture(), background.width(), background.height(), background.x(), background.y())); }
    }

    public static void sendToasts(ServerPlayer player, int kinds) { send(player, new MessageToasts(kinds)); }

    public static void sendAnvilCosts(ServerPlayer player) { send(player, new MessageAnvilCosts(AnvilCosts.servedCap(), boxed(AnvilCosts.servedPaid()), boxed(AnvilCosts.servedLapis()), AnvilCosts.servedOpening())); }

    public static void sendHeartColors(ServerPlayer player) { send(player, new MessageHeartColors(boxed(ContentHearts.colors()))); }

    private static List<Integer> boxed(int[] values) { return Arrays.stream(values).boxed().toList(); }

    private static int[] ints(List<Integer> values) { return values.stream().mapToInt(Integer::intValue).toArray(); }

    public static void introDone() { PacketDistributor.sendToServer(new MessageIntroDone()); }

    public static boolean reaches(ServerPlayer player) { return player.connection.hasChannel(MessageCard.TYPE); }

    public static void sendCard(ServerPlayer player, MessageCard card) { send(player, card); }

    public static void sendTalk(ServerPlayer player, MessageTalk talk) { send(player, talk); }

    public static void sendTalkPick(int index) { PacketDistributor.sendToServer(new MessageTalkPick(index)); }

    public static void sendEnvironments(ServerPlayer player, MessageEnvironments environments) { send(player, environments); }
}
