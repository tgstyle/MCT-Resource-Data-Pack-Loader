package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;

public record BedStateMessage(byte state) {
    public static final byte HIDDEN = 0;
    public static final byte NONE = 1;
    public static final byte CURRENT = 2;
    public static final byte OTHER = 3;
    public static final byte UPDATED = 4;

    public static void write(BedStateMessage message, FriendlyByteBuf buf) { buf.writeByte(message.state); }

    public static BedStateMessage read(FriendlyByteBuf buf) { return new BedStateMessage(buf.readByte()); }
}
