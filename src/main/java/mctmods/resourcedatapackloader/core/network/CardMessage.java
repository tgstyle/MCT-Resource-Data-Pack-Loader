package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public record CardMessage(String title, List<String> lines, ItemStack icon, String image, int background, int text, int ticks, boolean center, boolean panel, String font) implements CustomPacketPayload {
    public static final Type<CardMessage> TYPE = new Type<>(ResourceDataPackLoader.id("card"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CardMessage> CODEC = StreamCodec.of(CardMessage::write, CardMessage::read);
    private static final int MOST_LINES = 16;

    private static void write(RegistryFriendlyByteBuf buf, CardMessage message) {
        buf.writeUtf(message.title);
        int count = Math.min(message.lines.size(), MOST_LINES);
        buf.writeByte(count);
        for (int i = 0; i < count; i++) { buf.writeUtf(message.lines.get(i)); }
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, message.icon);
        buf.writeUtf(message.image);
        buf.writeInt(message.background);
        buf.writeInt(message.text);
        buf.writeInt(message.ticks);
        buf.writeBoolean(message.center);
        buf.writeBoolean(message.panel);
        buf.writeUtf(message.font);
    }

    private static CardMessage read(RegistryFriendlyByteBuf buf) {
        String title = buf.readUtf();
        int count = buf.readByte();
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) { lines.add(buf.readUtf()); }
        return new CardMessage(title, lines, ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean(), buf.readUtf());
    }

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
