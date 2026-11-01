package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;

public record ToastsMessage(int kinds) {
    public static void write(ToastsMessage message, FriendlyByteBuf buf) { buf.writeByte(message.kinds); }

    public static ToastsMessage read(FriendlyByteBuf buf) { return new ToastsMessage(buf.readByte()); }
}
