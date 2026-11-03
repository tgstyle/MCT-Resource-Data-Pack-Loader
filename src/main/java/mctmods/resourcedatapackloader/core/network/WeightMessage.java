package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;

public record WeightMessage(int total, int limit, int tier, boolean exempt) {
    public static void write(WeightMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.total);
        buf.writeInt(message.limit);
        buf.writeInt(message.tier);
        buf.writeBoolean(message.exempt);
    }

    public static WeightMessage read(FriendlyByteBuf buf) { return new WeightMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean()); }
}
