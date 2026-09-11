package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public record MessageOpenWorn(int after) implements CustomPacketPayload {
    public static final Type<MessageOpenWorn> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "openworn"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageOpenWorn> CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, MessageOpenWorn::after, MessageOpenWorn::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
