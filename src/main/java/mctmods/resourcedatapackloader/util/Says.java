package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.network.MessageCard;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import java.util.Collections;

public final class Says {
    private static final int CARD_TICKS = 160;
    private static final int CARD_BACKGROUND = 0x1E2630;
    private static final ResourceLocation KEY = new ResourceLocation("resourcedatapackloader", "saysicon");

    private Says() {}

    public static void tell(EntityPlayerMP player, String said, TextFormatting color) {
        if (said.isEmpty()) { return; }
        if (!card()) {
            player.sendMessage(new TextComponentString(said).setStyle(new Style().setColor(color)));
            return;
        }
        String iconName = ContentControl.text(ContentControl.CHUNKS, "saysIcon", Config.chunks.saysIcon).trim();
        ItemStack icon = iconName.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(KEY, iconName, 1);
        String colorName = ContentControl.text(ContentControl.CHUNKS, "saysColor", Config.chunks.saysColor).trim();
        int background = colorName.isEmpty() ? CARD_BACKGROUND : ContentTypes.color(colorName, "saysColor") & 0xFFFFFF;
        String image = ContentControl.text(ContentControl.CHUNKS, "saysImage", Config.chunks.saysImage).trim();
        RDPLNetwork.sendTo(new MessageCard("", Collections.singletonList(said), icon, image, background, rgb(color), CARD_TICKS), player);
    }

    public static void line(ICommandSender sender, TextFormatting color, String said) {
        if (said.isEmpty()) { return; }
        sender.sendMessage(new TextComponentString(said).setStyle(new Style().setColor(color)));
    }

    public static void tellAll(String said, TextFormatting color) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null || said.isEmpty()) { return; }
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) { tell(player, said, color); }
    }

    public static boolean card() { return ContentControl.flag(ContentControl.CHUNKS, "saysCard", Config.chunks.saysCard); }

    private static int rgb(TextFormatting color) {
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
