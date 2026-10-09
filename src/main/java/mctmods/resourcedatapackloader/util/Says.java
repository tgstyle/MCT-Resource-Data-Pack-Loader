package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.card.CardFire;
import mctmods.resourcedatapackloader.content.card.CardLook;
import mctmods.resourcedatapackloader.content.card.CardRules;
import mctmods.resourcedatapackloader.network.MessageCard;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Map;

public final class Says {
    public static final int CARD_TICKS = 160;
    private static final int CARD_BACKGROUND = 0x1E2630;
    private static final int PLAIN = 0xFFFF55;
    private static final Identifier KEY = ResourceDataPackLoader.id("saysicon");

    private Says() {}

    public static void tell(ServerPlayer player, ChatFormatting color, String said) {
        if (said.isEmpty()) { return; }
        if (!card() || !RDPLNetwork.reaches(player)) {
            line(player, color, said);
            return;
        }
        RDPLNetwork.sendCard(player, new MessageCard("", List.of(said), icon(), image(), background(), rgb(color), CARD_TICKS, false, panel(), font()));
    }

    public static void tell(ServerPlayer player, String rule, ChatFormatting color, String said) {
        if (said.isEmpty()) { return; }
        if (CardRules.unset(rule)) { tell(player, color, said); }
        else { CardFire.builtin(rule, player, CardLook.says(said, color, CardLook.CORNER)); }
    }

    public static void line(ServerPlayer player, ChatFormatting color, String said) {
        if (said.isEmpty()) { return; }
        player.sendSystemMessage(marked(said, color));
    }

    public static void options(ServerPlayer player, ChatFormatting color, String said, Map<String, String> commands) {
        MutableComponent line = marked(said, color);
        for (Map.Entry<String, String> option : commands.entrySet()) { line.append(Component.literal(" [" + option.getKey() + "]").withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA).withClickEvent(new ClickEvent.RunCommand(option.getValue())))); }
        player.sendSystemMessage(line);
    }

    public static void line(ServerPlayer player, String rule, ChatFormatting color, String said) {
        if (said.isEmpty()) { return; }
        if (CardRules.unset(rule)) { line(player, color, said); }
        else { CardFire.builtin(rule, player, CardLook.says(said, color, CardLook.CHAT)); }
    }

    public static void bar(ServerPlayer player, String rule, ChatFormatting color, MutableComponent said) {
        if (CardRules.unset(rule)) { player.sendOverlayMessage(said.withStyle(color)); }
        else { CardFire.builtin(rule, player, CardLook.says(said.getString(), color, CardLook.BAR)); }
    }

    public static void title(ServerPlayer player, int fadeIn, int stay, int fadeOut, String title, String subtitle, ChatFormatting style) {
        player.connection.send(new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
        if (!subtitle.isEmpty()) { player.connection.send(new ClientboundSetSubtitleTextPacket(marked(subtitle, style))); }
        player.connection.send(new ClientboundSetTitleTextPacket(title.isEmpty() ? Component.empty() : marked(title, style)));
    }

    public static MutableComponent marked(String said, ChatFormatting color) {
        MutableComponent all = Component.empty().withStyle(color);
        for (Marks.Run run : Marks.runs(said)) {
            Style style = Style.EMPTY.withBold(run.has(Marks.BOLD) ? Boolean.TRUE : null).withItalic(run.has(Marks.ITALIC) ? Boolean.TRUE : null).withStrikethrough(run.has(Marks.STRIKE) ? Boolean.TRUE : null).withUnderlined(run.has(Marks.LINK) ? Boolean.TRUE : null);
            all.append(Component.literal(run.text).withStyle(run.has(Marks.CODE) ? style.withColor(ChatFormatting.AQUA) : style));
        }
        return all;
    }

    public static void tellAll(MinecraftServer server, ChatFormatting color, String said) { for (ServerPlayer player : server.getPlayerList().getPlayers()) { tell(player, color, said); } }

    public static void tellAll(MinecraftServer server, String rule, ChatFormatting color, String said) { for (ServerPlayer player : server.getPlayerList().getPlayers()) { tell(player, rule, color, said); } }

    public static boolean card() { return ContentControl.flag(ContentControl.CHUNKS, "saysCard", Config.chunks.saysCard()); }

    public static ItemStack icon() {
        String iconName = text("saysIcon", Config.chunks.saysIcon());
        return iconName.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(KEY, iconName, 1);
    }

    public static int background() {
        String colorName = text("saysColor", Config.chunks.saysColor());
        return colorName.isEmpty() ? CARD_BACKGROUND : ContentParser.color(colorName, "saysColor") & 0xFFFFFF;
    }

    public static String image() { return text("saysImage", Config.chunks.saysImage()); }

    public static boolean panel() { return ContentControl.flag(ContentControl.CHUNKS, "saysBackground", Config.chunks.saysBackground()); }

    public static String font() { return text("saysFont", Config.chunks.saysFont()); }

    private static String text(String key, String fallback) { return ContentControl.text(ContentControl.CHUNKS, key, fallback).trim(); }

    public static int rgb(ChatFormatting color) {
        TextColor value = TextColor.fromLegacyFormat(color);
        return value == null ? PLAIN : value.getValue();
    }
}
