package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.extra.ContentIntroPlay;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageIntroDone implements IMessage {
    @Override public void fromBytes(ByteBuf buf) {}

    @Override public void toBytes(ByteBuf buf) {}

    public static class Handler extends AbstractServerMessageHandler<MessageIntroDone> {
        @Override public void handleServerMessage(EntityPlayer player, MessageIntroDone message, MessageContext ctx) { ContentIntroPlay.finished((EntityPlayerMP) player); }
    }
}
