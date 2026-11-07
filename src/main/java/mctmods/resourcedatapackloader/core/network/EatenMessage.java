package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;
import java.util.List;

public record EatenMessage(List<String> eaten) {
    public static void write(EatenMessage message, FriendlyByteBuf buf) { StringListCodec.write(message.eaten, buf); }

    public static EatenMessage read(FriendlyByteBuf buf) { return new EatenMessage(StringListCodec.read(buf)); }
}
