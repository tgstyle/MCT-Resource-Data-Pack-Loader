package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record OpenWornMessage() implements CustomPacketPayload {
    public static final Type<OpenWornMessage> TYPE = new Type<>(ResourceDataPackLoader.id("openworn"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenWornMessage> CODEC = StreamCodec.unit(new OpenWornMessage());

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
