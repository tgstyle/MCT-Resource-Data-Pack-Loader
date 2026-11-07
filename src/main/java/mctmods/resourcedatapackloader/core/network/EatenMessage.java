package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record EatenMessage(List<String> eaten) implements CustomPacketPayload {
    public static final Type<EatenMessage> TYPE = new Type<>(ResourceDataPackLoader.id("eaten"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EatenMessage> CODEC = StringListCodec.of(EatenMessage::eaten, EatenMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
