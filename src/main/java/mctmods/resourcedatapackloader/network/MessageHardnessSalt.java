package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.ContentHardness;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHardnessSalt implements IMessage {
    private long salt;

    public MessageHardnessSalt() {}

    public MessageHardnessSalt(long salt) { this.salt = salt; }

    @Override public void fromBytes(ByteBuf buf) { salt = buf.readLong(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeLong(salt); }

    public static class Handler extends AbstractClientMessageHandler<MessageHardnessSalt> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageHardnessSalt message, MessageContext ctx) { ContentHardness.salt(message.salt); }
    }
}
