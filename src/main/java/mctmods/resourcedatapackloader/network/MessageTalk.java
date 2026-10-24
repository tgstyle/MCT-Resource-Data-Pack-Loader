package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public record MessageTalk(int npc, boolean end, String title, List<String> lines, List<String> replies, ItemStack icon, String image, int background, boolean panel, String font) {
    private static final int MOST = 16;

    public static MessageTalk ending() { return new MessageTalk(0, true, "", List.of(), List.of(), ItemStack.EMPTY, "", 0, true, ""); }

    public static void write(MessageTalk message, FriendlyByteBuf buf) {
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
    }

    public static MessageTalk read(FriendlyByteBuf buf) { return new MessageTalk(buf.readInt(), buf.readBoolean(), buf.readUtf(), strings(buf), strings(buf), buf.readItem(), buf.readUtf(), buf.readInt(), buf.readBoolean(), buf.readUtf()); }

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
