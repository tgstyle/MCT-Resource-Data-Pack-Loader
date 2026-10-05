package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nonnull;

public record MessageIntroLandMade() implements CustomPacketPayload {
    public static final Type<MessageIntroLandMade> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "introlandmade"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageIntroLandMade> CODEC = StreamCodec.unit(new MessageIntroLandMade());

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
