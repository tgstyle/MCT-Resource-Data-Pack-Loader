package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.common.intro.ContentIntroPlay;
import mctmods.resourcedatapackloader.core.network.handler.AbstractServerMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class IntroDoneMessage implements IMessage {
    @Override public void fromBytes(ByteBuf buf) {}

    @Override public void toBytes(ByteBuf buf) {}

    public static class Handler extends AbstractServerMessageHandler<IntroDoneMessage> {
        @Override public void handleServerMessage(EntityPlayer player, IntroDoneMessage message, MessageContext ctx) { ContentIntroPlay.finished((EntityPlayerMP) player); }
    }
}
