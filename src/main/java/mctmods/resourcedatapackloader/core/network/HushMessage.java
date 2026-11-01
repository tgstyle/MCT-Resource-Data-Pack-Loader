package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;

public record HushMessage(double x, double y, double z) {
    public static void write(HushMessage message, FriendlyByteBuf buf) {
        buf.writeDouble(message.x);
        buf.writeDouble(message.y);
        buf.writeDouble(message.z);
    }

    public static HushMessage read(FriendlyByteBuf buf) { return new HushMessage(buf.readDouble(), buf.readDouble(), buf.readDouble()); }
}
