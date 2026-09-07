package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MessageIntroDone() implements CustomPacketPayload {
    public static final Type<MessageIntroDone> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "introdone"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageIntroDone> CODEC = StreamCodec.unit(new MessageIntroDone());

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
