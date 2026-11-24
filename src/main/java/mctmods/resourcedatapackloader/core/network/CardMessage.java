package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import java.util.List;

public record CardMessage(String title, List<String> lines, ItemStack icon, String image, int background, int text, int ticks, boolean center, boolean panel, String font) {
    public static void write(CardMessage message, FriendlyByteBuf buf) {
        buf.writeUtf(message.title);
        StringListCodec.writeLines(buf, message.lines);
        buf.writeItem(message.icon);
        buf.writeUtf(message.image);
        buf.writeInt(message.background);
        buf.writeInt(message.text);
        buf.writeInt(message.ticks);
        buf.writeBoolean(message.center);
        buf.writeBoolean(message.panel);
        buf.writeUtf(message.font);
    }

    public static CardMessage read(FriendlyByteBuf buf) {
        String title = buf.readUtf();
        List<String> lines = StringListCodec.readLines(buf);
        return new CardMessage(title, lines, buf.readItem(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean(), buf.readUtf());
    }
}
