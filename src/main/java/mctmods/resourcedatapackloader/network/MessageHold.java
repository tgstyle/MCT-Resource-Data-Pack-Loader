package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;

public record MessageHold(boolean held, String warning) {
    public static void write(MessageHold message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.held);
        buf.writeUtf(message.warning);
    }

    public static MessageHold read(FriendlyByteBuf buf) { return new MessageHold(buf.readBoolean(), buf.readUtf()); }
}
