package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SeasonClockMessage(long ticks) implements CustomPacketPayload {
    public static final Type<SeasonClockMessage> TYPE = new Type<>(ResourceDataPackLoader.id("season_clock"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SeasonClockMessage> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_LONG, SeasonClockMessage::ticks, SeasonClockMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
