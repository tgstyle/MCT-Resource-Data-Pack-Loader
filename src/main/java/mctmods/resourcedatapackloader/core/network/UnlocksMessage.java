package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;
import java.util.List;

public record UnlocksMessage(List<String> open) {
    public static void write(UnlocksMessage message, FriendlyByteBuf buf) { StringListCodec.write(message.open, buf); }

    public static UnlocksMessage read(FriendlyByteBuf buf) { return new UnlocksMessage(StringListCodec.read(buf)); }
}
