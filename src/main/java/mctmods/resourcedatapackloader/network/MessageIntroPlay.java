package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record MessageIntroPlay(boolean landBeingMade) implements CustomPacketPayload {
    public static final Type<MessageIntroPlay> TYPE = new Type<>(ResourceDataPackLoader.id("introplay"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageIntroPlay> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, MessageIntroPlay::landBeingMade, MessageIntroPlay::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
