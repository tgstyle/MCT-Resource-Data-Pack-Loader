package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.network.MessageCard;
import mctmods.resourcedatapackloader.network.RDPLNetwork;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public final class Says {
    private static final int CARD_TICKS = 160;
    private static final int CARD_BACKGROUND = 0x1E2630;
    private static final int PLAIN = 0xFFFF55;
    private static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "saysicon");

    private Says() {}

    public static void tell(ServerPlayer player, String said, ChatFormatting color) {
        if (said.isEmpty()) { return; }
        if (!card() || !RDPLNetwork.reaches(player)) {
            player.sendSystemMessage(Component.literal(said).withStyle(color));
            return;
        }
        String iconName = ContentControl.text(ContentControl.CHUNKS, "saysIcon", Config.chunks.saysIcon()).trim();
        ItemStack icon = iconName.isEmpty() ? ItemStack.EMPTY : ContentStacks.parse(KEY, iconName, 1);
        String colorName = ContentControl.text(ContentControl.CHUNKS, "saysColor", Config.chunks.saysColor()).trim();
        int background = colorName.isEmpty() ? CARD_BACKGROUND : ContentParser.color(colorName, "saysColor") & 0xFFFFFF;
        String image = ContentControl.text(ContentControl.CHUNKS, "saysImage", Config.chunks.saysImage()).trim();
        RDPLNetwork.sendCard(player, new MessageCard("", List.of(said), icon, image, background, rgb(color), CARD_TICKS));
    }

    public static void tellAll(MinecraftServer server, String said, ChatFormatting color) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) { tell(player, said, color); }
    }

    public static boolean card() { return ContentControl.flag(ContentControl.CHUNKS, "saysCard", Config.chunks.saysCard()); }

    private static int rgb(ChatFormatting color) {
        Integer value = color.getColor();
        return value == null ? PLAIN : value;
    }
}
