package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;
import java.util.UUID;

public record BossBackgroundMessage(UUID bar, String texture, int width, int height, int x, int y) {
    public static void write(BossBackgroundMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.bar);
        buf.writeUtf(message.texture);
        buf.writeInt(message.width);
        buf.writeInt(message.height);
        buf.writeInt(message.x);
        buf.writeInt(message.y);
    }

    public static BossBackgroundMessage read(FriendlyByteBuf buf) { return new BossBackgroundMessage(buf.readUUID(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()); }
}
