package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;

public record HoldMessage(boolean held, String warning, boolean fog, boolean backdrop, String font) {
    public static void write(HoldMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.held);
        buf.writeUtf(message.warning);
        buf.writeBoolean(message.fog);
        buf.writeBoolean(message.backdrop);
        buf.writeUtf(message.font);
    }

    public static HoldMessage read(FriendlyByteBuf buf) { return new HoldMessage(buf.readBoolean(), buf.readUtf(), buf.readBoolean(), buf.readBoolean(), buf.readUtf()); }
}
