package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record BedStateMessage(int state) implements CustomPacketPayload {
    public static final Type<BedStateMessage> TYPE = new Type<>(ResourceDataPackLoader.id("bedstate"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BedStateMessage> CODEC = StreamCodec.composite(ByteBufCodecs.BYTE.map(Byte::intValue, Integer::byteValue), BedStateMessage::state, BedStateMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
