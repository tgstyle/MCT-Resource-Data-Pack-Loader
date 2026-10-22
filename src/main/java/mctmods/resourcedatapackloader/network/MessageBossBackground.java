package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.UUID;
import javax.annotation.Nonnull;

public record MessageBossBackground(UUID bar, String texture, int width, int height, int x, int y) implements CustomPacketPayload {
    public static final Type<MessageBossBackground> TYPE = new Type<>(ResourceDataPackLoader.id("boss_background"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageBossBackground> CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC, MessageBossBackground::bar, ByteBufCodecs.STRING_UTF8, MessageBossBackground::texture, ByteBufCodecs.INT, MessageBossBackground::width, ByteBufCodecs.INT, MessageBossBackground::height, ByteBufCodecs.INT, MessageBossBackground::x, ByteBufCodecs.INT, MessageBossBackground::y, MessageBossBackground::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
