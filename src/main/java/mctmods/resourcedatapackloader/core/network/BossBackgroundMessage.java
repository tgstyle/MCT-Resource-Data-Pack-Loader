package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.UUID;
import javax.annotation.Nonnull;

public record BossBackgroundMessage(UUID bar, String texture, int width, int height, int x, int y) implements CustomPacketPayload {
    public static final Type<BossBackgroundMessage> TYPE = new Type<>(ResourceDataPackLoader.id("boss_background"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BossBackgroundMessage> CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC, BossBackgroundMessage::bar, ByteBufCodecs.STRING_UTF8, BossBackgroundMessage::texture, ByteBufCodecs.INT, BossBackgroundMessage::width, ByteBufCodecs.INT, BossBackgroundMessage::height, ByteBufCodecs.INT, BossBackgroundMessage::x, ByteBufCodecs.INT, BossBackgroundMessage::y, BossBackgroundMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
