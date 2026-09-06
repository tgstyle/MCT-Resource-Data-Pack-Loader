package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public record MessageCard(String title, List<String> lines, ItemStack icon, String image, int background, int text, int ticks) {
    private static final int MOST_LINES = 16;

    public static void write(MessageCard message, FriendlyByteBuf buf) {
        buf.writeUtf(message.title);
        int count = Math.min(message.lines.size(), MOST_LINES);
        buf.writeByte(count);
        for (int i = 0; i < count; i++) { buf.writeUtf(message.lines.get(i)); }
        buf.writeItem(message.icon);
        buf.writeUtf(message.image);
        buf.writeInt(message.background);
        buf.writeInt(message.text);
        buf.writeInt(message.ticks);
    }

    public static MessageCard read(FriendlyByteBuf buf) {
        String title = buf.readUtf();
        int count = buf.readByte();
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) { lines.add(buf.readUtf()); }
        return new MessageCard(title, lines, buf.readItem(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt());
    }
}
