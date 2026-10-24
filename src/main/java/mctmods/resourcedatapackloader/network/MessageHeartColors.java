package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;

public record MessageHeartColors(int[] colors) {
    public static final int MOST = 32;

    public static void write(MessageHeartColors message, FriendlyByteBuf buf) {
        int count = Math.min(message.colors.length, MOST);
        buf.writeByte(count);
        for (int i = 0; i < count; i++) { buf.writeInt(message.colors[i]); }
    }

    public static MessageHeartColors read(FriendlyByteBuf buf) {
        int[] colors = new int[Math.min(buf.readByte(), MOST)];
        for (int i = 0; i < colors.length; i++) { colors[i] = buf.readInt(); }
        return new MessageHeartColors(colors);
    }
}
