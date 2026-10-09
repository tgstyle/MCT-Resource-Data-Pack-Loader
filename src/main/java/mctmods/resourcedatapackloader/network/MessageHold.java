package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record MessageHold(boolean held, String warning, boolean fog, boolean backdrop, String font) implements CustomPacketPayload {
    public static final Type<MessageHold> TYPE = new Type<>(ResourceDataPackLoader.id("hold"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageHold> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, MessageHold::held, ByteBufCodecs.STRING_UTF8, MessageHold::warning, ByteBufCodecs.BOOL, MessageHold::fog, ByteBufCodecs.BOOL, MessageHold::backdrop, ByteBufCodecs.STRING_UTF8, MessageHold::font, MessageHold::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
