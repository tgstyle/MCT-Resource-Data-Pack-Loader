package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.menu.ContentPortwayMenu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import javax.annotation.Nonnull;

public record MessagePortwayFilter(String text) implements CustomPacketPayload {
    public static final Type<MessagePortwayFilter> TYPE = new Type<>(ResourceDataPackLoader.id("portwayfilter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessagePortwayFilter> CODEC = StreamCodec.composite(ByteBufCodecs.stringUtf8(ContentPortwayMenu.FILTER_MAX), MessagePortwayFilter::text, MessagePortwayFilter::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
