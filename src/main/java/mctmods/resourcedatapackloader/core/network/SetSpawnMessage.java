package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record SetSpawnMessage() implements CustomPacketPayload {
    public static final Type<SetSpawnMessage> TYPE = new Type<>(ResourceDataPackLoader.id("setspawn"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetSpawnMessage> CODEC = StreamCodec.unit(new SetSpawnMessage());

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
