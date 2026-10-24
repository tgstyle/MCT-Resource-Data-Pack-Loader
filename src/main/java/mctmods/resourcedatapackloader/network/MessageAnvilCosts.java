package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.AnvilCosts;

import net.minecraft.network.FriendlyByteBuf;

public record MessageAnvilCosts(int cap, int[] paid, int[] lapis, boolean opening) {
    public static void write(MessageAnvilCosts message, FriendlyByteBuf buf) {
        buf.writeInt(message.cap);
        buf.writeVarIntArray(message.paid);
        buf.writeVarIntArray(message.lapis);
        buf.writeBoolean(message.opening);
    }

    public static MessageAnvilCosts read(FriendlyByteBuf buf) { return new MessageAnvilCosts(buf.readInt(), buf.readVarIntArray(AnvilCosts.SLOTS), buf.readVarIntArray(AnvilCosts.SLOTS), buf.readBoolean()); }
}
