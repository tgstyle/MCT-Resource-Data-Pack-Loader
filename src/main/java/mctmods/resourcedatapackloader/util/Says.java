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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public final class Says {
    public static final int CARD_TICKS = 160;
    private static final int CARD_BACKGROUND = 0x1E2630;
    private static final int PLAIN = 0xFFFF55;
    private static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "saysicon");

    private Says() {}

    public static void tell(ServerPlayer player, String said, ChatFormatting color) {
        if (said.isEmpty()) { return; }
        if (!card() || !RDPLNetwork.reaches(player)) {
            line(player, color, said);
            return;
        }
        RDPLNetwork.sendCard(player, new MessageCard("", List.of(said), icon(), image(), background(), rgb(color), CARD_TICKS, false, panel(), font()));
    }

    public static void tell(ServerPlayer player, String rule, String said, ChatFormatting color) {
        if (said.isEmpty()) { return; }
        if (CardRules.unset(rule)) { tell(player, said, color); }
        else { CardFire.builtin(rule, player, CardLook.says(said, color, CardLook.CORNER)); }
    }

    public static void line(ServerPlayer player, ChatFormatting color, String said) {
        if (said.isEmpty()) { return; }
        player.sendSystemMessage(marked(said, color));
    }

    public static void line(ServerPlayer player, String rule, ChatFormatting color, String said) {
        if (said.isEmpty()) { return; }
        if (CardRules.unset(rule)) { line(player, color, said); }
        else { CardFire.builtin(rule, player, CardLook.says(said, color, CardLook.CHAT)); }
    }

    public static void bar(ServerPlayer player, String rule, ChatFormatting color, MutableComponent said) {
        if (CardRules.unset(rule)) { player.displayClientMessage(said.withStyle(color), true); }
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

    public static void tellAll(MinecraftServer server, String said, ChatFormatting color) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { tell(player, said, color); }
    }

    public static void tellAll(MinecraftServer server, String rule, String said, ChatFormatting color) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { tell(player, rule, said, color); }
    }

    public static boolean card() { return ContentControl.flag(ContentControl.CHUNKS, "saysCard", Config.chunks.saysCard()); }

    public static ItemStack icon() {
        String iconName = ContentControl.text(ContentControl.CHUNKS, "saysIcon", Config.chunks.saysIcon()).trim();
        return iconName.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(KEY, iconName, 1);
    }

    public static int background() {
        String colorName = ContentControl.text(ContentControl.CHUNKS, "saysColor", Config.chunks.saysColor()).trim();
        return colorName.isEmpty() ? CARD_BACKGROUND : ContentParser.color(colorName, "saysColor") & 0xFFFFFF;
    }

    public static String image() { return ContentControl.text(ContentControl.CHUNKS, "saysImage", Config.chunks.saysImage()).trim(); }

    public static boolean panel() { return ContentControl.flag(ContentControl.CHUNKS, "saysBackground", Config.chunks.saysBackground()); }

    public static String font() { return ContentControl.text(ContentControl.CHUNKS, "saysFont", Config.chunks.saysFont()).trim(); }

    public static int rgb(ChatFormatting color) {
        return switch (color) {
            case GREEN -> 0x55FF55;
            case RED -> 0xFF5555;
            case GOLD -> 0xFFAA00;
            case AQUA -> 0x55FFFF;
            case GRAY -> 0xAAAAAA;
            case WHITE -> 0xFFFFFF;
            case LIGHT_PURPLE -> 0xFF55FF;
            case BLUE -> 0x5555FF;
            default -> PLAIN;
        };
    }
}
