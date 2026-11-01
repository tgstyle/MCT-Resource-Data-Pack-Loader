package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;
import java.util.ArrayList;
import java.util.List;

public record MessageGates(List<String> ids) {
    public static final int MOST = 256;

    public static void write(MessageGates message, FriendlyByteBuf buf) {
        int count = Math.min(message.ids.size(), MOST);
        buf.writeVarInt(count);
        for (int i = 0; i < count; i++) { buf.writeUtf(message.ids.get(i)); }
    }

    public static MessageGates read(FriendlyByteBuf buf) {
        int count = Math.min(buf.readVarInt(), MOST);
        List<String> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) { ids.add(buf.readUtf()); }
        return new MessageGates(ids);
    }
}
