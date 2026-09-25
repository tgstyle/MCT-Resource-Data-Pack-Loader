package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.util.Toasts;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageToasts implements IMessage {
    private int kinds;

    public MessageToasts() {}

    public MessageToasts(int kinds) { this.kinds = kinds; }

    @Override public void fromBytes(ByteBuf buf) { kinds = buf.readByte(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeByte(kinds); }

    public static class Handler implements IMessageHandler<MessageToasts, IMessage> {
        @Override public IMessage onMessage(MessageToasts message, MessageContext ctx) {
            Toasts.show(message.kinds);
            return null;
        }
    }
}
