package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageIntroDone implements IMessage {
    @Override public void fromBytes(ByteBuf buf) {}

    @Override public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<MessageIntroDone, IMessage> {
        @Override public IMessage onMessage(MessageIntroDone message, MessageContext ctx) {
            if (ctx.side != Side.SERVER) { return null; }
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.server.addScheduledTask(() -> ContentIntroPlay.finished(player));
            return null;
        }
    }
}
