package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;

public record MessageNote(String said, boolean backdrop, String font) {
    public static void write(MessageNote message, FriendlyByteBuf buf) {
        buf.writeUtf(message.said);
        buf.writeBoolean(message.backdrop);
        buf.writeUtf(message.font);
    }

    public static MessageNote read(FriendlyByteBuf buf) { return new MessageNote(buf.readUtf(), buf.readBoolean(), buf.readUtf()); }
}
