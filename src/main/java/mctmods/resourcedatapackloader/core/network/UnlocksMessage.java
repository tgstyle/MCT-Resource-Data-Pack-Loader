package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.List;
import javax.annotation.Nonnull;

public record UnlocksMessage(List<String> open) implements CustomPacketPayload {
    public static final Type<UnlocksMessage> TYPE = new Type<>(ResourceDataPackLoader.id("unlocks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UnlocksMessage> CODEC = StringListCodec.of(UnlocksMessage::open, UnlocksMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
