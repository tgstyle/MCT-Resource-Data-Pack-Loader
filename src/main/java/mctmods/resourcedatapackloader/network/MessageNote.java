package mctmods.resourcedatapackloader.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageNote implements IMessage {
    public String said = "";
    public boolean backdrop = true;
    public String font = "";

    public MessageNote() {}

    public MessageNote(String said, boolean backdrop, String font) {
        this.said = said == null ? "" : said;
        this.backdrop = backdrop;
        this.font = font;
    }

    @Override public void fromBytes(ByteBuf buf) {
        said = ByteBufUtils.readUTF8String(buf);
        backdrop = buf.readBoolean();
        font = ByteBufUtils.readUTF8String(buf);
    }

    @Override public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, said);
        buf.writeBoolean(backdrop);
        ByteBufUtils.writeUTF8String(buf, font);
    }

    public static class Idle implements IMessageHandler<MessageNote, IMessage> {
        @Override public IMessage onMessage(MessageNote message, MessageContext ctx) { return null; }
    }
}
