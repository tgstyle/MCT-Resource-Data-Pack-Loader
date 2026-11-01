package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;
import java.util.ArrayList;
import java.util.List;

public record GatesMessage(List<String> ids) {
    public static final int MOST = 256;

    public static void write(GatesMessage message, FriendlyByteBuf buf) {
        int count = Math.min(message.ids.size(), MOST);
        buf.writeVarInt(count);
        for (int i = 0; i < count; i++) { buf.writeUtf(message.ids.get(i)); }
    }

    public static GatesMessage read(FriendlyByteBuf buf) {
        int count = Math.min(buf.readVarInt(), MOST);
        List<String> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) { ids.add(buf.readUtf()); }
        return new GatesMessage(ids);
    }
}
