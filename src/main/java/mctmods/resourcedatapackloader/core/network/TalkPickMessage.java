package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.common.dialogs.DialogTalk;
import mctmods.resourcedatapackloader.core.network.handler.AbstractServerMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TalkPickMessage implements IMessage {
    public int index;

    public TalkPickMessage() {}

    public TalkPickMessage(int index) { this.index = index; }

    @Override public void fromBytes(ByteBuf buf) { index = buf.readInt(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeInt(index); }

    public static class Handler extends AbstractServerMessageHandler<TalkPickMessage> {
        @Override public void handleServerMessage(EntityPlayer player, TalkPickMessage message, MessageContext ctx) { DialogTalk.pick((EntityPlayerMP) player, message.index); }
    }
}
