package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record MessageTalkPick(int index) implements CustomPacketPayload {
    public static final Type<MessageTalkPick> TYPE = new Type<>(ResourceDataPackLoader.id("talkpick"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTalkPick> CODEC = StreamCodec.of((buf, message) -> buf.writeInt(message.index), buf -> new MessageTalkPick(buf.readInt()));

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
