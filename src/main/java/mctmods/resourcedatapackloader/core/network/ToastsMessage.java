package mctmods.resourcedatapackloader.core.network;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;

public record ToastsMessage(int kinds, List<String> hidden) {
    public static void write(ToastsMessage message, FriendlyByteBuf buf) {
        buf.writeByte(message.kinds);
        StringListCodec.write(message.hidden, buf);
    }

    public static ToastsMessage read(FriendlyByteBuf buf) { return new ToastsMessage(buf.readByte(), StringListCodec.read(buf)); }
}
