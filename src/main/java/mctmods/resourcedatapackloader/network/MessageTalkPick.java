package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;

public record MessageTalkPick(int index) {
    public static void write(MessageTalkPick message, FriendlyByteBuf buf) { buf.writeInt(message.index); }

    public static MessageTalkPick read(FriendlyByteBuf buf) { return new MessageTalkPick(buf.readInt()); }
}
