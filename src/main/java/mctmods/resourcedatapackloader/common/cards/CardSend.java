package mctmods.resourcedatapackloader.common.cards;

import mctmods.resourcedatapackloader.common.intro.ContentIntroPlay;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.types.ContentTypes;
import mctmods.resourcedatapackloader.common.util.entity.Standing;
import mctmods.resourcedatapackloader.common.util.messages.Says;
import mctmods.resourcedatapackloader.core.network.CardMessage;
import mctmods.resourcedatapackloader.core.network.RDPLNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public final class CardSend {
    private static final int FADE_IN = 10;
    private static final int FADE_OUT = 20;
    private static final String TEXT = "{text}";
    private static final Map<UUID, List<CardLook>> HELD = new HashMap<>();

    private CardSend() {}

    static void dress(CardLook look, CardRule rule, EntityPlayerMP subject) {
        List<String> said = look.lines;
        if (rule.title != null) { look.title = fill(rule.title, subject, said); }
        else { look.title = fill(look.title, subject, said); }
        if (rule.lines != null) {
            List<String> lines = new ArrayList<>();
            for (String line : rule.lines) {
                if (TEXT.equals(line.trim())) { lines.addAll(said); }
                else { lines.add(fill(line, subject, said)); }
            }
            look.lines = lines;
        }
        if (rule.icon != null) { look.icon = rule.icon.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(new ResourceLocation(rule.key), rule.icon, 1); }
        if (rule.color != null && !rule.color.isEmpty()) { look.background = ContentTypes.color(rule.color, "card rule " + rule.key) & 0xFFFFFF; }
        if (rule.image != null) { look.image = rule.image; }
        if (rule.background != null) { look.panel = rule.background; }
        if (rule.font != null) { look.font = rule.font; }
        if (rule.style != null) { look.style = rule.style; }
        if (rule.ticks > 0) { look.ticks = rule.ticks; }
    }

    public static String fill(String line, EntityPlayerMP subject, List<String> said) {
        if (line.indexOf('{') < 0) { return line; }
        return line.replace(TEXT, String.join(" ", said))
                .replace("{player}", subject.getName())
                .replace("{dim}", CardPlace.dimensionName(subject.dimension))
                .replace("{biome}", CardPlace.biomeName(subject))
                .replace("{day}", Long.toString(CardPlace.day(subject.world)));
    }

    static List<EntityPlayerMP> audience(CardRule rule, EntityPlayerMP subject) {
        MinecraftServer server = subject.getServer();
        if (CardRule.PLAYER.equals(rule.audience) || server == null) { return Collections.singletonList(subject); }
        List<EntityPlayerMP> shown = new ArrayList<>();
        String team = Standing.teamOf(subject);
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            if (CardRule.DIMENSION.equals(rule.audience) && player.dimension != subject.dimension) { continue; }
            if (CardRule.TEAM.equals(rule.audience) && (team.isEmpty() ? player != subject : !team.equals(Standing.teamOf(player)))) { continue; }
            shown.add(player);
        }
        return shown;
    }

    static void send(CardLook look, EntityPlayerMP player) {
        if (ContentIntroPlay.reading(player.getUniqueID())) {
            HELD.computeIfAbsent(player.getUniqueID(), k -> new ArrayList<>()).add(look);
            return;
        }
        switch (look.style) {
            case CardLook.CHAT:
                chat(look, player);
                return;
            case CardLook.BAR:
                player.sendStatusMessage(Says.marked(String.join(" ", all(look)), look.chat), true);
                return;
            case CardLook.CENTER:
                if (RDPLNetwork.vanilla(player)) { titles(look, player); }
                else { RDPLNetwork.sendTo(message(look, true), player); }
                return;
            default:
                if (!Says.card() || RDPLNetwork.vanilla(player)) { chat(look, player); }
                else { RDPLNetwork.sendTo(message(look, false), player); }
        }
    }

    private static CardMessage message(CardLook look, boolean center) { return new CardMessage(look.title, look.lines, look.icon, look.image, look.background, look.text, look.ticks, center, look.panel, look.font); }

    private static List<String> all(CardLook look) {
        List<String> all = new ArrayList<>();
        if (!look.title.isEmpty()) { all.add(look.title); }
        all.addAll(look.lines);
        return all;
    }

    private static void chat(CardLook look, EntityPlayerMP player) { for (String line : all(look)) { Says.chat(player, look.chat, line); } }

    private static void titles(CardLook look, EntityPlayerMP player) {
        String subtitle = look.lines.isEmpty() ? "" : look.lines.get(0);
        Says.title(player, FADE_IN, Math.max(1, look.ticks - FADE_IN - FADE_OUT), FADE_OUT, look.title, subtitle, look.chat);
    }

    static void release(EntityPlayerMP player) {
        List<CardLook> held = HELD.remove(player.getUniqueID());
        if (held == null) { return; }
        for (CardLook look : held) { send(look, player); }
    }

    static void forget(UUID player) { HELD.remove(player); }
}
