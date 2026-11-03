package mctmods.resourcedatapackloader.core.network;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class StringListCodec {
    public static final int MOST = 256;

    private StringListCodec() {}

    public static <M> StreamCodec<RegistryFriendlyByteBuf, M> of(Function<M, List<String>> values, Function<List<String>, M> message) { return StreamCodec.composite(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(MOST)), values, message); }

    public static List<String> capped(Collection<String> values) { return values.stream().limit(MOST).toList(); }
}
