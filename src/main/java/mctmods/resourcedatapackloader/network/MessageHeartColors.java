package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.List;
import javax.annotation.Nonnull;

public record MessageHeartColors(List<Integer> colors) implements CustomPacketPayload {
    public static final int MOST = 32;
    public static final Type<MessageHeartColors> TYPE = new Type<>(ResourceDataPackLoader.id("heart_colors"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageHeartColors> CODEC = StreamCodec.composite(ByteBufCodecs.INT.apply(ByteBufCodecs.list(MOST)), MessageHeartColors::colors, MessageHeartColors::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
