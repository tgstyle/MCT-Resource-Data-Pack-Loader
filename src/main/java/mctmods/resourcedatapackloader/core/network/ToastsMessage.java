package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record ToastsMessage(int kinds, List<String> hidden) implements CustomPacketPayload {
    public static final Type<ToastsMessage> TYPE = new Type<>(ResourceDataPackLoader.id("toasts"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToastsMessage> CODEC = StreamCodec.composite(ByteBufCodecs.BYTE.map(Byte::intValue, Integer::byteValue), ToastsMessage::kinds, ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(StringListCodec.MOST)), ToastsMessage::hidden, ToastsMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
