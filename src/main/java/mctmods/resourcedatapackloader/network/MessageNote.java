package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public record MessageNote(String said) implements CustomPacketPayload {
    public static final Type<MessageNote> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "note"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageNote> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, MessageNote::said, MessageNote::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
