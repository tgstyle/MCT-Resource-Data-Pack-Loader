package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record TalkPickMessage(int index) implements CustomPacketPayload {
    public static final Type<TalkPickMessage> TYPE = new Type<>(ResourceDataPackLoader.id("talkpick"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TalkPickMessage> CODEC = StreamCodec.of((buf, message) -> buf.writeInt(message.index), buf -> new TalkPickMessage(buf.readInt()));

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
