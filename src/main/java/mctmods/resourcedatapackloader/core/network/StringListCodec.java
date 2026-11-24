package mctmods.resourcedatapackloader.core.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class StringListCodec {
    public static final int MOST = 4096;
    public static final int MOST_LINES = 16;

    private StringListCodec() {}

    public static <M> StreamCodec<RegistryFriendlyByteBuf, M> of(Function<M, List<String>> values, Function<List<String>, M> message) { return StreamCodec.composite(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(MOST)), values, message); }

    public static List<String> capped(Collection<String> values) { return values.stream().limit(MOST).toList(); }

    public static List<String> readStrings(FriendlyByteBuf buf, int count) {
        List<String> read = new ArrayList<>();
        for (int i = 0; i < count; i++) { read.add(buf.readUtf()); }
        return read;
    }

    public static void writeStrings(FriendlyByteBuf buf, List<String> values, int count) {
        for (int i = 0; i < count; i++) { buf.writeUtf(values.get(i)); }
    }

    public static List<String> readLines(FriendlyByteBuf buf) { return readStrings(buf, buf.readByte()); }

    public static void writeLines(FriendlyByteBuf buf, List<String> values) {
        int count = Math.min(values.size(), MOST_LINES);
        buf.writeByte(count);
        writeStrings(buf, values, count);
    }
}
