package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.List;
import javax.annotation.Nonnull;

public record MessageAnvilCosts(int cap, List<Integer> paid, List<Integer> lapis, boolean opening) implements CustomPacketPayload {
    public static final Type<MessageAnvilCosts> TYPE = new Type<>(ResourceDataPackLoader.id("anvil_costs"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageAnvilCosts> CODEC = StreamCodec.composite(ByteBufCodecs.INT, MessageAnvilCosts::cap, ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(3)), MessageAnvilCosts::paid, ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(3)), MessageAnvilCosts::lapis, ByteBufCodecs.BOOL, MessageAnvilCosts::opening, MessageAnvilCosts::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
