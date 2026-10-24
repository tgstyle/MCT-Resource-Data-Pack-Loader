package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public record MessageTalk(int npc, boolean end, String title, List<String> lines, List<String> replies, ItemStack icon, String image, int background, boolean panel, String font, int titleColor, int textColor, int replyColor, int hoverColor, int hoverBackground, boolean history) implements CustomPacketPayload {
    public static final Type<MessageTalk> TYPE = new Type<>(ResourceDataPackLoader.id("talk"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTalk> CODEC = StreamCodec.of(MessageTalk::write, MessageTalk::read);
    private static final int MOST = 16;

    public static MessageTalk ending() { return new MessageTalk(0, true, "", List.of(), List.of(), ItemStack.EMPTY, "", 0, true, "", -1, -1, -1, -1, -1, true); }

    private static void write(RegistryFriendlyByteBuf buf, MessageTalk message) {
        buf.writeInt(message.npc);
        buf.writeBoolean(message.end);
        buf.writeUtf(message.title);
        strings(buf, message.lines);
        strings(buf, message.replies);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, message.icon);
        buf.writeUtf(message.image);
        buf.writeInt(message.background);
        buf.writeBoolean(message.panel);
        buf.writeUtf(message.font);
        buf.writeInt(message.titleColor);
        buf.writeInt(message.textColor);
        buf.writeInt(message.replyColor);
        buf.writeInt(message.hoverColor);
        buf.writeInt(message.hoverBackground);
        buf.writeBoolean(message.history);
    }

    private static MessageTalk read(RegistryFriendlyByteBuf buf) { return new MessageTalk(buf.readInt(), buf.readBoolean(), buf.readUtf(), strings(buf), strings(buf), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), buf.readUtf(), buf.readInt(), buf.readBoolean(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean()); }

    private static void strings(RegistryFriendlyByteBuf buf, List<String> strings) {
        int count = Math.min(strings.size(), MOST);
        buf.writeByte(count);
        for (int i = 0; i < count; i++) { buf.writeUtf(strings.get(i)); }
    }

    private static List<String> strings(RegistryFriendlyByteBuf buf) {
        int count = buf.readByte();
        List<String> read = new ArrayList<>();
        for (int i = 0; i < count; i++) { read.add(buf.readUtf()); }
        return read;
    }

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
