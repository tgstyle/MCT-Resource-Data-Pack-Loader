package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.card.CardFire;
import mctmods.resourcedatapackloader.content.card.CardLook;
import mctmods.resourcedatapackloader.content.card.CardRules;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.network.MessageCard;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import java.util.Collections;

public final class Says {
    public static final int CARD_TICKS = 160;
    private static final int CARD_BACKGROUND = 0x1E2630;
    private static final ResourceLocation KEY = new ResourceLocation("resourcedatapackloader", "saysicon");

    private Says() {}

    public static void tell(EntityPlayerMP player, String said, TextFormatting color) {
        if (said.isEmpty()) { return; }
        if (!card() || RDPLNetwork.vanilla(player)) {
            player.sendMessage(marked(said, color));
            return;
        }
        RDPLNetwork.sendTo(new MessageCard("", Collections.singletonList(said), icon(), image(), background(), rgb(color), CARD_TICKS, false, panel(), font()), player);
    }

    public static void tell(EntityPlayerMP player, String rule, String said, TextFormatting color) {
        if (said.isEmpty()) { return; }
        if (CardRules.unset(rule)) { tell(player, said, color); }
        else { CardFire.builtin(rule, player, CardLook.says(said, color, CardLook.CORNER)); }
    }

    public static void line(ICommandSender sender, TextFormatting color, String said) {
        if (said.isEmpty()) { return; }
        sender.sendMessage(new TextComponentString(said).setStyle(new Style().setColor(color)));
    }

    public static void chat(EntityPlayer player, TextFormatting color, String said) {
        if (said.isEmpty()) { return; }
        player.sendMessage(marked(said, color));
    }

    public static void line(EntityPlayer player, String rule, TextFormatting color, String said) {
        if (said.isEmpty()) { return; }
        if (CardRules.unset(rule) || !(player instanceof EntityPlayerMP)) { chat(player, color, said); }
        else { CardFire.builtin(rule, (EntityPlayerMP) player, CardLook.says(said, color, CardLook.CHAT)); }
    }

    public static void bar(EntityPlayer player, String rule, TextFormatting color, String said) {
        if (said.isEmpty()) { return; }
        if (CardRules.unset(rule) || !(player instanceof EntityPlayerMP)) { player.sendStatusMessage(marked(said, color), true); }
        else { CardFire.builtin(rule, (EntityPlayerMP) player, CardLook.says(said, color, CardLook.BAR)); }
    }

    public static void title(EntityPlayerMP player, int fadeIn, int stay, int fadeOut, String title, String subtitle, TextFormatting style) {
        player.connection.sendPacket(new SPacketTitle(fadeIn, stay, fadeOut));
        if (!subtitle.isEmpty()) { player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.SUBTITLE, marked(subtitle, style))); }
        player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.TITLE, title.isEmpty() ? new TextComponentString("") : marked(title, style)));
    }

    public static void tellAll(String said, TextFormatting color) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || said.isEmpty()) { return; }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { tell(player, said, color); }
    }

    public static void tellAll(String rule, String said, TextFormatting color) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || said.isEmpty()) { return; }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { tell(player, rule, said, color); }
    }

    public static ITextComponent marked(String said, TextFormatting color) {
        ITextComponent all = new TextComponentString("").setStyle(new Style().setColor(color));
        for (Marks.Run run : Marks.runs(said)) {
            Style style = new Style();
            if (run.has(Marks.BOLD)) { style.setBold(true); }
            if (run.has(Marks.ITALIC)) { style.setItalic(true); }
            if (run.has(Marks.STRIKE)) { style.setStrikethrough(true); }
            if (run.has(Marks.LINK)) { style.setUnderlined(true); }
            if (run.has(Marks.CODE)) { style.setColor(TextFormatting.AQUA); }
            all.appendSibling(new TextComponentString(run.text).setStyle(style));
        }
        return all;
    }

    public static boolean card() { return ContentControl.flag(ContentControl.CHUNKS, "saysCard", Config.chunks.saysCard); }

    public static ItemStack icon() {
        String iconName = ContentControl.text(ContentControl.CHUNKS, "saysIcon", Config.chunks.saysIcon).trim();
        return iconName.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(KEY, iconName, 1);
    }

    public static int background() {
        String colorName = ContentControl.text(ContentControl.CHUNKS, "saysColor", Config.chunks.saysColor).trim();
        return colorName.isEmpty() ? CARD_BACKGROUND : ContentTypes.color(colorName, "saysColor") & 0xFFFFFF;
    }

    public static String image() { return ContentControl.text(ContentControl.CHUNKS, "saysImage", Config.chunks.saysImage).trim(); }

    public static boolean panel() { return ContentControl.flag(ContentControl.CHUNKS, "saysBackground", Config.chunks.saysBackground); }

    public static String font() { return ContentControl.text(ContentControl.CHUNKS, "saysFont", Config.chunks.saysFont).trim(); }

    public static int rgb(TextFormatting color) {
        switch (color) {
            case GREEN: return 0x55FF55;
            case RED: return 0xFF5555;
            case GOLD: return 0xFFAA00;
            case AQUA: return 0x55FFFF;
            case GRAY: return 0xAAAAAA;
            case WHITE: return 0xFFFFFF;
            case LIGHT_PURPLE: return 0xFF55FF;
            case BLUE: return 0x5555FF;
            default: return 0xFFFF55;
        }
    }
}
