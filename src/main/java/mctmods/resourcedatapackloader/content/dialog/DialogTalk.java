package mctmods.resourcedatapackloader.content.dialog;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.card.CardFire;
import mctmods.resourcedatapackloader.content.card.CardRule;
import mctmods.resourcedatapackloader.content.card.CardRules;
import mctmods.resourcedatapackloader.content.card.CardSend;
import mctmods.resourcedatapackloader.content.def.DialogDef;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.network.MessageTalk;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Functions;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import java.util.ArrayList;
import java.util.Collections;
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
        List<DialogDef.Reply> shown = Collections.emptyList();

        Session(DialogDef def, int npc, String title) {
            this.def = def;
            this.npc = npc;
            this.title = title;
            this.node = def.start;
        }
    }

    public static void open(EntityPlayerMP player, Entity npc, String variantName, DialogDef def) {
        Session session = new Session(def, npc.getEntityId(), variantName.isEmpty() ? NPC : variantName);
        SESSIONS.put(player.getUniqueID(), session);
        ContentLog.LOGGER.info("Dialog {} opened for {} at node {}", def.key, player.getName(), session.node);
        show(player, session, npc);
    }

    public static boolean idle(EntityPlayerMP player) {
        if (SESSIONS.containsKey(player.getUniqueID())) { return false; }
        ContentLog.LOGGER.info("Dialog refused for {}: no dialog is open", player.getName());
        return true;
    }

    public static void pick(EntityPlayerMP player, int number) {
        if (idle(player)) { return; }
        Session session = SESSIONS.get(player.getUniqueID());
        if (number == CLOSE) {
            SESSIONS.remove(player.getUniqueID());
            ContentLog.LOGGER.info("Dialog {} {} picked {} -> closed", session.def.key, player.getName(), number);
            return;
        }
        Entity npc = player.world.getEntityByID(session.npc);
        if (npc == null || npc.isDead || npc.getDistance(player) > REACH) {
            SESSIONS.remove(player.getUniqueID());
            RDPLNetwork.sendTo(MessageTalk.ending(), player);
            ContentLog.LOGGER.info("Dialog {} refused {}: the speaker is gone or farther than {} blocks", session.def.key, player.getName(), (int) REACH);
            return;
        }
        if (number < 1 || number > session.shown.size()) {
            ContentLog.LOGGER.info("Dialog {} ignored pick {} from {}: there are {} replies", session.def.key, number, player.getName(), session.shown.size());
            return;
        }
        DialogDef.Reply reply = session.shown.get(number - 1);
        ContentLog.LOGGER.info("Dialog {} {} picked {} -> {}", session.def.key, player.getName(), number, reply.target.isEmpty() ? (reply.close ? "closed" : session.node) : reply.target);
        if (!reply.runs.isEmpty()) { Functions.runAs(player, reply.runs, "Dialog " + session.def.key); }
        if (!reply.card.isEmpty()) { card(player, session, reply.card); }
        if (!reply.target.isEmpty()) { session.node = reply.target; }
        if (reply.close) {
            SESSIONS.remove(player.getUniqueID());
            RDPLNetwork.sendTo(MessageTalk.ending(), player);
        }
        else { show(player, session, npc); }
    }

    public static void forget(UUID player) { SESSIONS.remove(player); }

    private static void card(EntityPlayerMP player, Session session, String named) {
        CardRule rule = CardRules.find(named);
        if (rule == null) { ContentLog.LOGGER.error("Dialog {} shows the card {}, which no pack provides, so no card is shown", session.def.key, named); }
        else { CardFire.show(rule, player); }
    }

    private static void show(EntityPlayerMP player, Session session, Entity npc) {
        DialogDef def = session.def;
        DialogDef.Node node = def.nodes.get(session.node);
        String name = npc.getName();
        List<DialogDef.Reply> shown = new ArrayList<>();
        for (DialogDef.Reply reply : node.replies) {
            if (reply.when == null || !reply.when.fails(player)) { shown.add(reply); }
        }
        session.shown = shown;
        String title = line(node.title != null ? node.title : def.title != null ? def.title : session.title, player, name);
        List<String> lines = new ArrayList<>();
        for (String line : node.lines) { lines.add(line(line, player, name)); }
        List<String> replies = new ArrayList<>();
        for (DialogDef.Reply reply : shown) { replies.add(line(reply.text, player, name)); }
        if (shown.isEmpty()) { SESSIONS.remove(player.getUniqueID()); }
        if (RDPLNetwork.vanilla(player)) { chat(player, title, lines, replies); }
        else { RDPLNetwork.sendTo(new MessageTalk(npc.getEntityId(), title, lines, replies, icon(def), image(def), background(def), panel(def), font(def)), player); }
    }

    private static String line(String text, EntityPlayerMP player, String name) { return CardSend.fill(text.replace(NPC, name), player, Collections.emptyList()); }

    private static void chat(EntityPlayerMP player, String title, List<String> lines, List<String> replies) {
        Says.chat(player, TextFormatting.YELLOW, title);
        for (String line : lines) { Says.chat(player, TextFormatting.YELLOW, line); }
        for (int i = 0; i < replies.size(); i++) {
            ITextComponent row = new TextComponentString("[" + (i + 1) + "] ").setStyle(new Style().setColor(TextFormatting.AQUA).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rdplserver talk " + (i + 1))));
            row.appendSibling(Says.marked(replies.get(i), TextFormatting.AQUA));
            player.sendMessage(row);
        }
    }

    private static ItemStack icon(DialogDef def) {
        if (def.icon == null) { return Says.icon(); }
        return def.icon.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(new ResourceLocation(def.key), def.icon, 1);
    }

    private static int background(DialogDef def) { return def.color == null || def.color.isEmpty() ? Says.background() : ContentTypes.color(def.color, "dialog " + def.key) & 0xFFFFFF; }

    private static String image(DialogDef def) { return def.image == null ? Says.image() : def.image; }

    private static boolean panel(DialogDef def) { return def.background == null ? Says.panel() : def.background; }

    private static String font(DialogDef def) { return def.font == null ? Says.font() : def.font; }
}
