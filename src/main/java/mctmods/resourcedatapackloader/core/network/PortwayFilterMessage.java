package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.gui.ContentPortwayMenu;

import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PortwayFilterMessage(String text) implements CustomPacketPayload {
    public static final Type<PortwayFilterMessage> TYPE = new Type<>(ResourceDataPackLoader.id("portwayfilter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PortwayFilterMessage> CODEC = StreamCodec.composite(ByteBufCodecs.stringUtf8(ContentPortwayMenu.FILTER_MAX), PortwayFilterMessage::text, PortwayFilterMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
