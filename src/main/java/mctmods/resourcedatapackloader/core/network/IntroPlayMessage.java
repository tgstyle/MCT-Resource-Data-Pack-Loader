package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record IntroPlayMessage(boolean landBeingMade) implements CustomPacketPayload {
    public static final Type<IntroPlayMessage> TYPE = new Type<>(ResourceDataPackLoader.id("introplay"));
    public static final StreamCodec<RegistryFriendlyByteBuf, IntroPlayMessage> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, IntroPlayMessage::landBeingMade, IntroPlayMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
