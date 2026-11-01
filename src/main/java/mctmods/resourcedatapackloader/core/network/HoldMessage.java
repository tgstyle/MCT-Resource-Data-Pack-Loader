package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record HoldMessage(boolean held, String warning, boolean fog, boolean backdrop, String font) implements CustomPacketPayload {
    public static final Type<HoldMessage> TYPE = new Type<>(ResourceDataPackLoader.id("hold"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HoldMessage> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, HoldMessage::held, ByteBufCodecs.STRING_UTF8, HoldMessage::warning, ByteBufCodecs.BOOL, HoldMessage::fog, ByteBufCodecs.BOOL, HoldMessage::backdrop, ByteBufCodecs.STRING_UTF8, HoldMessage::font, HoldMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
