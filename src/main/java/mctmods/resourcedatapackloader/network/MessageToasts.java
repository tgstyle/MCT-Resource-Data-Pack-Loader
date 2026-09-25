package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;

public record MessageToasts(int kinds) {
    public static void write(MessageToasts message, FriendlyByteBuf buf) { buf.writeByte(message.kinds); }

    public static MessageToasts read(FriendlyByteBuf buf) { return new MessageToasts(buf.readByte()); }
}
