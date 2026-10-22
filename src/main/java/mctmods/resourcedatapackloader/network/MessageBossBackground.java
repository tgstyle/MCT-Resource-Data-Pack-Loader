package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;
import java.util.UUID;

public record MessageBossBackground(UUID bar, String texture, int width, int height, int x, int y) {
    public static void write(MessageBossBackground message, FriendlyByteBuf buf) {
        buf.writeUUID(message.bar);
        buf.writeUtf(message.texture);
        buf.writeInt(message.width);
        buf.writeInt(message.height);
        buf.writeInt(message.x);
        buf.writeInt(message.y);
    }

    public static MessageBossBackground read(FriendlyByteBuf buf) { return new MessageBossBackground(buf.readUUID(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()); }
}
