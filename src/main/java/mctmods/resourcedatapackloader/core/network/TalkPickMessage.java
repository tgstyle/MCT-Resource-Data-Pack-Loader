package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;

public record TalkPickMessage(int index) {
    public static void write(TalkPickMessage message, FriendlyByteBuf buf) { buf.writeInt(message.index); }

    public static TalkPickMessage read(FriendlyByteBuf buf) { return new TalkPickMessage(buf.readInt()); }
}
