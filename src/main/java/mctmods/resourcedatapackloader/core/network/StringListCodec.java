package mctmods.resourcedatapackloader.core.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;

public final class StringListCodec {
    public static final int MOST = 256;

    private StringListCodec() {}

    public static void write(List<String> values, FriendlyByteBuf buf) {
        int count = Math.min(values.size(), MOST);
        buf.writeVarInt(count);
        for (int i = 0; i < count; i++) { buf.writeUtf(values.get(i)); }
    }

    public static List<String> read(FriendlyByteBuf buf) {
        int count = Math.min(buf.readVarInt(), MOST);
        List<String> values = new ArrayList<>(count);
        for (int i = 0; i < count; i++) { values.add(buf.readUtf()); }
        return values;
    }
}
