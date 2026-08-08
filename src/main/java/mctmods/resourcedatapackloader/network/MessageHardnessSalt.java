package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.ContentHardness;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageHardnessSalt implements IMessage {
    private long salt;

    public MessageHardnessSalt() {}

    public MessageHardnessSalt(long salt) { this.salt = salt; }

    @Override public void fromBytes(ByteBuf buf) { salt = buf.readLong(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeLong(salt); }

    public static class Handler implements IMessageHandler<MessageHardnessSalt, IMessage> {

        @Override public IMessage onMessage(MessageHardnessSalt message, MessageContext ctx) {
            if (ctx.side != Side.CLIENT) { return null; }

            ContentHardness.salt(message.salt);
            return null;
        }
    }
}
