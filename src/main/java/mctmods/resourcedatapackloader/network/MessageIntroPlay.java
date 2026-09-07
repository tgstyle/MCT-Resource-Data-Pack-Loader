package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;

public record MessageIntroPlay(boolean landBeingMade) {
    public static void write(MessageIntroPlay message, FriendlyByteBuf buf) { buf.writeBoolean(message.landBeingMade); }

    public static MessageIntroPlay read(FriendlyByteBuf buf) { return new MessageIntroPlay(buf.readBoolean()); }
}
