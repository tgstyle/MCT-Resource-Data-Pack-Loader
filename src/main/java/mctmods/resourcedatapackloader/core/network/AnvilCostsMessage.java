package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.common.anvils.AnvilCosts;

import net.minecraft.network.FriendlyByteBuf;

public record AnvilCostsMessage(int cap, int[] paid, int[] lapis, boolean opening) {
    public static void write(AnvilCostsMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.cap);
        buf.writeVarIntArray(message.paid);
        buf.writeVarIntArray(message.lapis);
        buf.writeBoolean(message.opening);
    }

    public static AnvilCostsMessage read(FriendlyByteBuf buf) { return new AnvilCostsMessage(buf.readInt(), buf.readVarIntArray(AnvilCosts.SLOTS), buf.readVarIntArray(AnvilCosts.SLOTS), buf.readBoolean()); }
}
