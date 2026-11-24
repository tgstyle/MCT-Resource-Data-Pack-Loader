package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import javax.annotation.Nonnull;

public record TalkMessage(int npc, boolean end, String title, List<String> lines, List<String> replies, ItemStack icon, String image, int background, boolean panel, String font, int titleColor, int textColor, int replyColor, int hoverColor, int hoverBackground, boolean history) implements CustomPacketPayload {
    public static final Type<TalkMessage> TYPE = new Type<>(ResourceDataPackLoader.id("talk"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TalkMessage> CODEC = StreamCodec.of(TalkMessage::write, TalkMessage::read);

    public static TalkMessage ending() { return new TalkMessage(0, true, "", List.of(), List.of(), ItemStack.EMPTY, "", 0, true, "", -1, -1, -1, -1, -1, true); }

    private static void write(RegistryFriendlyByteBuf buf, TalkMessage message) {
        buf.writeInt(message.npc);
        buf.writeBoolean(message.end);
        buf.writeUtf(message.title);
        StringListCodec.writeLines(buf, message.lines);
        StringListCodec.writeLines(buf, message.replies);
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

    private static TalkMessage read(RegistryFriendlyByteBuf buf) { return new TalkMessage(buf.readInt(), buf.readBoolean(), buf.readUtf(), StringListCodec.readLines(buf), StringListCodec.readLines(buf), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), buf.readUtf(), buf.readInt(), buf.readBoolean(), buf.readUtf(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean()); }

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
