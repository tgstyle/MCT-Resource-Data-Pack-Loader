package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.List;
import javax.annotation.Nonnull;

public record MessageGates(List<String> open) implements CustomPacketPayload {
    public static final Type<MessageGates> TYPE = new Type<>(ResourceDataPackLoader.id("gates"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageGates> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(256)), MessageGates::open, MessageGates::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
