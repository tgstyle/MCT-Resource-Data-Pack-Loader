package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record IntroDoneMessage() implements CustomPacketPayload {
    public static final Type<IntroDoneMessage> TYPE = new Type<>(ResourceDataPackLoader.id("introdone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, IntroDoneMessage> CODEC = StreamCodec.unit(new IntroDoneMessage());

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
