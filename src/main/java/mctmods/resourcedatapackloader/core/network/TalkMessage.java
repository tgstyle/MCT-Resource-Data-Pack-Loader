package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public record TalkMessage(int npc, boolean end, String title, List<String> lines, List<String> replies, ItemStack icon, String image, int background, boolean panel, String font, int titleColor, int textColor, int replyColor, int hoverColor, int hoverBackground, boolean history) {
    private static final int MOST = 16;

    public static TalkMessage ending() { return new TalkMessage(0, true, "", List.of(), List.of(), ItemStack.EMPTY, "", 0, true, "", -1, -1, -1, -1, -1, true); }

    public static void write(TalkMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.npc);
        buf.writeBoolean(message.end);
        buf.writeUtf(message.title);
        strings(buf, message.lines);
        strings(buf, message.replies);
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

    public static TalkMessage read(FriendlyByteBuf buf) { return new TalkMessage(buf.readInt(), buf.readBoolean(), buf.readUtf(), strings(buf), strings(buf), buf.readItem(), buf.readUtf(), buf.readInt(), buf.readBoolean(), buf.readUtf(),
            buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean()); }

    private static void strings(FriendlyByteBuf buf, List<String> strings) {
        int count = Math.min(strings.size(), MOST);
        buf.writeByte(count);
        for (int i = 0; i < count; i++) { buf.writeUtf(strings.get(i)); }
    }

    private static List<String> strings(FriendlyByteBuf buf) {
        int count = buf.readByte();
        List<String> read = new ArrayList<>();
        for (int i = 0; i < count; i++) { read.add(buf.readUtf()); }
        return read;
    }
}
