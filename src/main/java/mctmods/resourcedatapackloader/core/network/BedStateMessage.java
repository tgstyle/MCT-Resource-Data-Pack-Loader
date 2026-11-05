package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record BedStateMessage(byte state) implements CustomPacketPayload {
    public static final byte HIDDEN = 0;
    public static final byte NONE = 1;
    public static final byte CURRENT = 2;
    public static final byte OTHER = 3;
    public static final byte UPDATED = 4;
    public static final Type<BedStateMessage> TYPE = new Type<>(ResourceDataPackLoader.id("bedstate"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BedStateMessage> CODEC = StreamCodec.composite(ByteBufCodecs.BYTE, BedStateMessage::state, BedStateMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
