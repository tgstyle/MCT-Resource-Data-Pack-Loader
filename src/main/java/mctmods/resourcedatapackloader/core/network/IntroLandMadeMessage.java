package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record IntroLandMadeMessage() implements CustomPacketPayload {
    public static final Type<IntroLandMadeMessage> TYPE = new Type<>(ResourceDataPackLoader.id("introlandmade"));
    public static final StreamCodec<RegistryFriendlyByteBuf, IntroLandMadeMessage> CODEC = StreamCodec.unit(new IntroLandMadeMessage());

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
