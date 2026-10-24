package mctmods.resourcedatapackloader.content.dialog;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.card.CardFire;
import mctmods.resourcedatapackloader.content.card.CardRule;
import mctmods.resourcedatapackloader.content.card.CardRules;
import mctmods.resourcedatapackloader.content.card.CardSend;
import mctmods.resourcedatapackloader.content.def.DialogDef;
import mctmods.resourcedatapackloader.network.MessageTalk;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DialogTalk {
    public static final int CLOSE = -1;
    private static final double REACH = 8.0D;
    private static final String NPC = "{npc}";
    private static final Map<UUID, Session> SESSIONS = new HashMap<>();

    private DialogTalk() {}

    private static final class Session {
        final DialogDef def;
        final int npc;
        final String title;
        String node;
        List<DialogDef.Reply> shown = List.of();

        Session(DialogDef def, int npc, String title) {
            this.def = def;
            this.npc = npc;
            this.title = title;
            this.node = def.start();
        }
    }

    public static void open(ServerPlayer player, Entity npc, String variantName, DialogDef def) {
        Session session = new Session(def, npc.getId(), variantName.isEmpty() ? NPC : variantName);
        SESSIONS.put(player.getUUID(), session);
        ContentLog.LOGGER.info("Dialog {} opened for {} at node {}", def.key(), player.getName().getString(), session.node);
        show(player, session, npc);
    }

    public static boolean idle(ServerPlayer player) {
        if (SESSIONS.containsKey(player.getUUID())) { return false; }
        ContentLog.LOGGER.info("Dialog refused for {}: no dialog is open", player.getName().getString());
        return true;
    }

    public static void pick(ServerPlayer player, int number) {
        if (idle(player)) { return; }
        Session session = SESSIONS.get(player.getUUID());
        String who = player.getName().getString();
        if (number == CLOSE) {
            SESSIONS.remove(player.getUUID());
            ContentLog.LOGGER.info("Dialog {} {} picked {} -> closed", session.def.key(), who, number);
            return;
        }
        Entity npc = player.level().getEntity(session.npc);
        if (npc == null || !npc.isAlive() || npc.distanceTo(player) > REACH) {
            SESSIONS.remove(player.getUUID());
            RDPLNetwork.sendTalk(player, MessageTalk.ending());
            ContentLog.LOGGER.info("Dialog {} refused {}: the speaker is gone or farther than {} blocks", session.def.key(), who, (int) REACH);
            return;
        }
        if (number < 1 || number > session.shown.size()) {
            ContentLog.LOGGER.info("Dialog {} ignored pick {} from {}: there are {} replies", session.def.key(), number, who, session.shown.size());
            return;
        }
        DialogDef.Reply reply = session.shown.get(number - 1);
        ContentLog.LOGGER.info("Dialog {} {} picked {} -> {}", session.def.key(), who, number, reply.target().isEmpty() ? (reply.close() ? "closed" : session.node) : reply.target());
        if (!reply.runs().isEmpty()) { Functions.runAs(player, reply.runs(), "Dialog " + session.def.key()); }
        if (!reply.card().isEmpty()) { card(player, session, reply.card()); }
        if (!reply.target().isEmpty()) { session.node = reply.target(); }
        if (reply.close()) {
            SESSIONS.remove(player.getUUID());
            RDPLNetwork.sendTalk(player, MessageTalk.ending());
        }
        else { show(player, session, npc); }
    }

    public static void forget(UUID player) { SESSIONS.remove(player); }

    private static void card(ServerPlayer player, Session session, String named) {
        CardRule rule = CardRules.find(named);
        if (rule == null) { ContentLog.LOGGER.error("Dialog {} shows the card {}, which no pack provides, so no card is shown", session.def.key(), named); }
        else { CardFire.show(rule, player); }
    }

    private static void show(ServerPlayer player, Session session, Entity npc) {
        DialogDef def = session.def;
        DialogDef.Node node = def.nodes().get(session.node);
        String name = npc.getName().getString();
        List<DialogDef.Reply> shown = new ArrayList<>();
        for (DialogDef.Reply reply : node.replies()) {
            if (reply.when() == null || !reply.when().fails(player)) { shown.add(reply); }
        }
        session.shown = shown;
        String title = line(node.title() != null ? node.title() : def.title() != null ? def.title() : session.title, player, name);
        List<String> lines = new ArrayList<>();
        for (String line : node.lines()) { lines.add(line(line, player, name)); }
        List<String> replies = new ArrayList<>();
        for (DialogDef.Reply reply : shown) { replies.add(line(reply.text(), player, name)); }
        if (shown.isEmpty()) { SESSIONS.remove(player.getUUID()); }
        if (RDPLNetwork.reaches(player)) { RDPLNetwork.sendTalk(player, new MessageTalk(npc.getId(), false, title, lines, replies, icon(def), image(def), background(def), panel(def), font(def))); }
        else { chat(player, title, lines, replies); }
    }

    private static String line(String text, ServerPlayer player, String name) { return CardSend.fill(text.replace(NPC, name), player, List.of()); }

    private static void chat(ServerPlayer player, String title, List<String> lines, List<String> replies) {
        Says.line(player, ChatFormatting.YELLOW, title);
        for (String line : lines) { Says.line(player, ChatFormatting.YELLOW, line); }
        for (int i = 0; i < replies.size(); i++) {
            MutableComponent row = Component.literal("[" + (i + 1) + "] ").withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rdplserver talk " + (i + 1))));
            row.append(Says.marked(replies.get(i), ChatFormatting.AQUA));
            player.sendSystemMessage(row);
        }
    }

    private static ItemStack icon(DialogDef def) {
        if (def.icon() == null) { return Says.icon(); }
        return def.icon().isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(ResourceLocation.tryParse(def.key()), def.icon(), 1);
    }

    private static int background(DialogDef def) { return def.color() == null || def.color().isEmpty() ? Says.background() : ContentParser.color(def.color(), "dialog " + def.key()) & 0xFFFFFF; }

    private static String image(DialogDef def) { return def.image() == null ? Says.image() : def.image(); }

    private static boolean panel(DialogDef def) { return def.background() == null ? Says.panel() : def.background(); }

    private static String font(DialogDef def) { return def.font() == null ? Says.font() : def.font(); }
}
