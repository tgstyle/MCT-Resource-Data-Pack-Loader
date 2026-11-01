package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;

public record IntroPlayMessage(boolean landBeingMade) {
    public static void write(IntroPlayMessage message, FriendlyByteBuf buf) { buf.writeBoolean(message.landBeingMade); }

    public static IntroPlayMessage read(FriendlyByteBuf buf) { return new IntroPlayMessage(buf.readBoolean()); }
}
