package mctmods.resourcedatapackloader.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHold implements IMessage {
    public boolean held;

    public MessageHold() {}

    public MessageHold(boolean held) { this.held = held; }

    @Override public void fromBytes(ByteBuf buf) { held = buf.readBoolean(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeBoolean(held); }

    public static class Idle implements IMessageHandler<MessageHold, IMessage> {
        @Override public IMessage onMessage(MessageHold message, MessageContext ctx) { return null; }
    }
}
