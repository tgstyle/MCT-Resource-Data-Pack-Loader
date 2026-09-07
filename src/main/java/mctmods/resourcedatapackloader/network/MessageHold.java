package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MessageHold(boolean held, String warning) implements CustomPacketPayload {
    public static final Type<MessageHold> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "hold"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageHold> CODEC = StreamCodec.of((buf, message) -> {
        buf.writeBoolean(message.held);
        buf.writeUtf(message.warning);
    }, buf -> new MessageHold(buf.readBoolean(), buf.readUtf()));

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
