package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;

public record MessageHold(boolean held) {
    public static void write(MessageHold message, FriendlyByteBuf buf) { buf.writeBoolean(message.held); }

    public static MessageHold read(FriendlyByteBuf buf) { return new MessageHold(buf.readBoolean()); }
}
