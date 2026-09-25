package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;
import mctmods.resourcedatapackloader.network.MessageCard;
import mctmods.resourcedatapackloader.network.RDPLNetwork;
import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

final class CardSend {
    private static final int FADE_IN = 10;
    private static final int FADE_OUT = 20;
    private static final String TEXT = "{text}";
    private static final Map<UUID, List<CardLook>> HELD = new HashMap<>();

    private CardSend() {}

    static void dress(CardLook look, CardRule rule, ServerPlayer subject) {
        List<String> said = look.lines;
        look.title = fill(rule.title != null ? rule.title : look.title, subject, said);
        if (rule.lines != null) {
            List<String> lines = new ArrayList<>();
            for (String line : rule.lines) {
                if (TEXT.equals(line.trim())) { lines.addAll(said); }
                else { lines.add(fill(line, subject, said)); }
            }
            look.lines = lines;
        }
        if (rule.icon != null) { look.icon = rule.icon.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(ResourceLocation.tryParse(rule.key), rule.icon, 1); }
        if (rule.color != null && !rule.color.isEmpty()) { look.background = ContentParser.color(rule.color, "card rule " + rule.key) & 0xFFFFFF; }
        if (rule.image != null) { look.image = rule.image; }
        if (rule.background != null) { look.panel = rule.background; }
        if (rule.font != null) { look.font = rule.font; }
        if (rule.style != null) { look.style = rule.style; }
        if (rule.ticks > 0) { look.ticks = rule.ticks; }
    }

    private static String fill(String line, ServerPlayer subject, List<String> said) {
        if (line.indexOf('{') < 0) { return line; }
        return line.replace(TEXT, String.join(" ", said))
                .replace("{player}", subject.getName().getString())
                .replace("{dim}", CardPlace.dimensionName(subject))
                .replace("{biome}", CardPlace.biomeName(subject))
                .replace("{day}", Long.toString(CardPlace.day(subject.level())));
    }

    static List<ServerPlayer> audience(CardRule rule, ServerPlayer subject) {
        if (CardRule.PLAYER.equals(rule.audience)) { return List.of(subject); }
        List<ServerPlayer> shown = new ArrayList<>();
        String team = CardWhen.teamOf(subject);
        for (ServerPlayer player : subject.server.getPlayerList().getPlayers()) {
            if (CardRule.DIMENSION.equals(rule.audience) && player.level() != subject.level()) { continue; }
            if (CardRule.TEAM.equals(rule.audience) && (team.isEmpty() ? player != subject : !team.equals(CardWhen.teamOf(player)))) { continue; }
            shown.add(player);
        }
        return shown;
    }

    static void send(CardLook look, ServerPlayer player) {
        if (ContentIntroPlay.reading(player.getUUID())) {
            HELD.computeIfAbsent(player.getUUID(), k -> new ArrayList<>()).add(look);
            return;
        }
        switch (look.style) {
            case CardLook.CHAT -> chat(look, player);
            case CardLook.BAR -> player.displayClientMessage(Says.marked(String.join(" ", all(look)), look.chat), true);
            case CardLook.CENTER -> {
                if (RDPLNetwork.reaches(player)) { RDPLNetwork.sendCard(player, message(look, true)); }
                else { titles(look, player); }
            }
            default -> {
                if (Says.card() && RDPLNetwork.reaches(player)) { RDPLNetwork.sendCard(player, message(look, false)); }
                else { chat(look, player); }
            }
        }
    }

    private static MessageCard message(CardLook look, boolean center) { return new MessageCard(look.title, look.lines, look.icon, look.image, look.background, look.text, look.ticks, center, look.panel, look.font); }

    private static List<String> all(CardLook look) {
        List<String> all = new ArrayList<>();
        if (!look.title.isEmpty()) { all.add(look.title); }
        all.addAll(look.lines);
        return all;
    }

    private static void chat(CardLook look, ServerPlayer player) {
        for (String line : all(look)) { Says.line(player, look.chat, line); }
    }

    private static void titles(CardLook look, ServerPlayer player) {
        String subtitle = look.lines.isEmpty() ? "" : look.lines.get(0);
        Says.title(player, FADE_IN, Math.max(1, look.ticks - FADE_IN - FADE_OUT), FADE_OUT, look.title, subtitle, look.chat);
    }

    static void release(ServerPlayer player) {
        List<CardLook> held = HELD.remove(player.getUUID());
        if (held == null) { return; }
        for (CardLook look : held) { send(look, player); }
    }

    static void forget(UUID player) { HELD.remove(player); }
}
