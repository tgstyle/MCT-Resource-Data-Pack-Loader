package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.dialog.DialogTalk;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTalkPick implements IMessage {
    public int index;

    public MessageTalkPick() {}

    public MessageTalkPick(int index) { this.index = index; }

    @Override public void fromBytes(ByteBuf buf) { index = buf.readInt(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeInt(index); }

    public static class Handler extends AbstractServerMessageHandler<MessageTalkPick> {
        @Override public void handleServerMessage(EntityPlayer player, MessageTalkPick message, MessageContext ctx) { DialogTalk.pick((EntityPlayerMP) player, message.index); }
    }
}
