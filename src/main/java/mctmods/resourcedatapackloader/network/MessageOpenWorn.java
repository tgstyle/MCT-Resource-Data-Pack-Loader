package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public record MessageOpenWorn() implements CustomPacketPayload {
    public static final Type<MessageOpenWorn> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "openworn"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageOpenWorn> CODEC = StreamCodec.unit(new MessageOpenWorn());

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
