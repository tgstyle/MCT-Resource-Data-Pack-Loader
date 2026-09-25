package mctmods.resourcedatapackloader.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHold implements IMessage {
    public boolean held;
    public String warning = "";
    public boolean fog;
    public boolean backdrop = true;
    public String font = "";

    public MessageHold() {}

    public MessageHold(boolean held, String warning, boolean fog, boolean backdrop, String font) {
        this.held = held;
        this.warning = warning == null ? "" : warning;
        this.fog = fog;
        this.backdrop = backdrop;
        this.font = font;
    }

    @Override public void fromBytes(ByteBuf buf) {
        held = buf.readBoolean();
        warning = ByteBufUtils.readUTF8String(buf);
        fog = buf.readBoolean();
        backdrop = buf.readBoolean();
        font = ByteBufUtils.readUTF8String(buf);
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeBoolean(held);
        ByteBufUtils.writeUTF8String(buf, warning);
        buf.writeBoolean(fog);
        buf.writeBoolean(backdrop);
        ByteBufUtils.writeUTF8String(buf, font);
    }

    public static class Idle implements IMessageHandler<MessageHold, IMessage> {
        @Override public IMessage onMessage(MessageHold message, MessageContext ctx) { return null; }
    }
}
