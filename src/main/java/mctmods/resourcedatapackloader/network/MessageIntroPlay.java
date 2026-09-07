package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MessageIntroPlay(boolean landBeingMade) implements CustomPacketPayload {
    public static final Type<MessageIntroPlay> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "introplay"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageIntroPlay> CODEC = StreamCodec.of((buf, message) -> buf.writeBoolean(message.landBeingMade), buf -> new MessageIntroPlay(buf.readBoolean()));

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
