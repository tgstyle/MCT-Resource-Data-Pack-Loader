package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;

public record NoteMessage(String said, boolean backdrop, String font) {
    public static void write(NoteMessage message, FriendlyByteBuf buf) {
        buf.writeUtf(message.said);
        buf.writeBoolean(message.backdrop);
        buf.writeUtf(message.font);
    }

    public static NoteMessage read(FriendlyByteBuf buf) { return new NoteMessage(buf.readUtf(), buf.readBoolean(), buf.readUtf()); }
}
