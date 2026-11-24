package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public record TalkMessage(int npc, boolean end, String title, List<String> lines, List<String> replies, ItemStack icon, String image, int background, boolean panel, String font, int titleColor, int textColor, int replyColor, int hoverColor, int hoverBackground, boolean history) {
    public static TalkMessage ending() { return new TalkMessage(0, true, "", List.of(), List.of(), ItemStack.EMPTY, "", 0, true, "", -1, -1, -1, -1, -1, true); }

    public static void write(TalkMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.npc);
        buf.writeBoolean(message.end);
        buf.writeUtf(message.title);
        StringListCodec.writeLines(buf, message.lines);
        StringListCodec.writeLines(buf, message.replies);
        buf.writeItem(message.icon);
        buf.writeUtf(message.image);
        buf.writeInt(message.background);
        buf.writeBoolean(message.panel);
        buf.writeUtf(message.font);
        buf.writeInt(message.titleColor);
        buf.writeInt(message.textColor);
        buf.writeInt(message.replyColor);
        buf.writeInt(message.hoverColor);
        buf.writeInt(message.hoverBackground);
        buf.writeBoolean(message.history);
    }

    public static TalkMessage read(FriendlyByteBuf buf) { return new TalkMessage(buf.readInt(), buf.readBoolean(), buf.readUtf(), StringListCodec.readLines(buf), StringListCodec.readLines(buf), buf.readItem(), buf.readUtf(), buf.readInt(), buf.readBoolean(), buf.readUtf(),
            buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean()); }
}
