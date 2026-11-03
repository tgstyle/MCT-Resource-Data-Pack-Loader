package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.Item;
import javax.annotation.Nonnull;

public record WeightFloorsMessage(Map<Item, Integer> floors) implements CustomPacketPayload {
    public static final Type<WeightFloorsMessage> TYPE = new Type<>(ResourceDataPackLoader.id("weight_floors"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WeightFloorsMessage> CODEC = StreamCodec.composite(ByteBufCodecs.map(IdentityHashMap::new, ByteBufCodecs.registry(Registries.ITEM), ByteBufCodecs.VAR_INT), WeightFloorsMessage::floors, WeightFloorsMessage::new);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
