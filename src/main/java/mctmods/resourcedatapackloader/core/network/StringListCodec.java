package mctmods.resourcedatapackloader.core.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;

public final class StringListCodec {
    public static final int MOST = 4096;
    public static final int MOST_LINES = 16;

    private StringListCodec() {}

    public static void write(List<String> values, FriendlyByteBuf buf) {
        int count = Math.min(values.size(), MOST);
        buf.writeVarInt(count);
        writeStrings(buf, values, count);
    }

    public static List<String> read(FriendlyByteBuf buf) { return readStrings(buf, Math.min(buf.readVarInt(), MOST)); }

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
