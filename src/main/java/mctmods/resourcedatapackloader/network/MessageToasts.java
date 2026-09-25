package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nonnull;

public record MessageToasts(int kinds) implements CustomPacketPayload {
    public static final Type<MessageToasts> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "toasts"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageToasts> CODEC = StreamCodec.of((buf, message) -> buf.writeByte(message.kinds), buf -> new MessageToasts(buf.readByte()));

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
