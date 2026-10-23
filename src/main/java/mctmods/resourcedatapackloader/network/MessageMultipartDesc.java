package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.util.compat.CbmpRubicParts;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageMultipartDesc implements IMessage {
    private BlockPos pos;
    private byte[] data;

    public MessageMultipartDesc() {}

    public MessageMultipartDesc(BlockPos pos, byte[] data) {
        this.pos = pos;
        this.data = data;
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.data = new byte[buf.readInt()];
        buf.readBytes(this.data);
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(data.length);
        buf.writeBytes(data);
    }

    public static class Handler extends AbstractClientMessageHandler<MessageMultipartDesc> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MessageMultipartDesc message, MessageContext ctx) { CbmpRubicParts.Client.apply(world, message.pos, message.data); }
    }
}
