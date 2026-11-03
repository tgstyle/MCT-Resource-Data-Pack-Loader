package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;
import java.util.List;

public record GatesMessage(List<String> ids) {
    public static void write(GatesMessage message, FriendlyByteBuf buf) { StringListCodec.write(message.ids, buf); }

    public static GatesMessage read(FriendlyByteBuf buf) { return new GatesMessage(StringListCodec.read(buf)); }
}
