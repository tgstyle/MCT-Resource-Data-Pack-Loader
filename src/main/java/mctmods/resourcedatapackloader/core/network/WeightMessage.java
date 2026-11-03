package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record WeightMessage(int total, int limit, int tier, boolean exempt) implements CustomPacketPayload {
    public static final Type<WeightMessage> TYPE = new Type<>(ResourceDataPackLoader.id("weight"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WeightMessage> CODEC = StreamCodec.composite(ByteBufCodecs.INT, WeightMessage::total, ByteBufCodecs.INT, WeightMessage::limit, ByteBufCodecs.INT, WeightMessage::tier, ByteBufCodecs.BOOL, WeightMessage::exempt, WeightMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
