package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record NoteMessage(String said, boolean backdrop, String font) implements CustomPacketPayload {
    public static final Type<NoteMessage> TYPE = new Type<>(ResourceDataPackLoader.id("note"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NoteMessage> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, NoteMessage::said, ByteBufCodecs.BOOL, NoteMessage::backdrop, ByteBufCodecs.STRING_UTF8, NoteMessage::font, NoteMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
