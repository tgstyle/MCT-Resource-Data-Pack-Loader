package mctmods.resourcedatapackloader.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageIntroLandMade implements IMessage {
    @Override public void fromBytes(ByteBuf buf) {}

    @Override public void toBytes(ByteBuf buf) {}

    public static class Idle implements IMessageHandler<MessageIntroLandMade, IMessage> {
        @Override public IMessage onMessage(MessageIntroLandMade message, MessageContext ctx) { return null; }
    }
}
