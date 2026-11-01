package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.List;
import javax.annotation.Nonnull;

public record GatesMessage(List<String> open) implements CustomPacketPayload {
    public static final int MOST = 256;
    public static final Type<GatesMessage> TYPE = new Type<>(ResourceDataPackLoader.id("open_gates"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GatesMessage> CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(MOST)), GatesMessage::open, GatesMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
