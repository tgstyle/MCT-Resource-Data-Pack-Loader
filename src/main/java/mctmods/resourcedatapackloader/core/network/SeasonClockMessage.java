package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;

public record SeasonClockMessage(long ticks) {
    public static void write(SeasonClockMessage message, FriendlyByteBuf buf) { buf.writeLong(message.ticks); }

    public static SeasonClockMessage read(FriendlyByteBuf buf) { return new SeasonClockMessage(buf.readLong()); }
}
