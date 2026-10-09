package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record MessageToasts(int kinds) implements CustomPacketPayload {
    public static final Type<MessageToasts> TYPE = new Type<>(ResourceDataPackLoader.id("toasts"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageToasts> CODEC = StreamCodec.composite(ByteBufCodecs.BYTE.map(Byte::intValue, Integer::byteValue), MessageToasts::kinds, MessageToasts::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
