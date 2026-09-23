package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;

public record MessageHush(double x, double y, double z) {
    public static void write(MessageHush message, FriendlyByteBuf buf) {
        buf.writeDouble(message.x);
        buf.writeDouble(message.y);
        buf.writeDouble(message.z);
    }

    public static MessageHush read(FriendlyByteBuf buf) { return new MessageHush(buf.readDouble(), buf.readDouble(), buf.readDouble()); }
}
