package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public record MessageCard(String title, List<String> lines, ItemStack icon, String image, int background, int text, int ticks) implements CustomPacketPayload {
    public static final Type<MessageCard> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "card"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageCard> CODEC = StreamCodec.of(MessageCard::write, MessageCard::read);
    private static final int MOST_LINES = 16;

    private static void write(RegistryFriendlyByteBuf buf, MessageCard message) {
        buf.writeUtf(message.title);
        int count = Math.min(message.lines.size(), MOST_LINES);
        buf.writeByte(count);
        for (int i = 0; i < count; i++) { buf.writeUtf(message.lines.get(i)); }
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, message.icon);
        buf.writeUtf(message.image);
        buf.writeInt(message.background);
        buf.writeInt(message.text);
        buf.writeInt(message.ticks);
    }

    private static MessageCard read(RegistryFriendlyByteBuf buf) {
        String title = buf.readUtf();
        int count = buf.readByte();
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) { lines.add(buf.readUtf()); }
        return new MessageCard(title, lines, ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
