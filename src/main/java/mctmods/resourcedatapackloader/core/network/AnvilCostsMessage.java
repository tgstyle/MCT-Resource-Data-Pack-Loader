package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.List;
import javax.annotation.Nonnull;

public record AnvilCostsMessage(int cap, List<Integer> paid, List<Integer> lapis, boolean opening) implements CustomPacketPayload {
    public static final Type<AnvilCostsMessage> TYPE = new Type<>(ResourceDataPackLoader.id("anvil_costs"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AnvilCostsMessage> CODEC = StreamCodec.composite(ByteBufCodecs.INT, AnvilCostsMessage::cap, ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(3)), AnvilCostsMessage::paid, ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(3)), AnvilCostsMessage::lapis, ByteBufCodecs.BOOL, AnvilCostsMessage::opening, AnvilCostsMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
