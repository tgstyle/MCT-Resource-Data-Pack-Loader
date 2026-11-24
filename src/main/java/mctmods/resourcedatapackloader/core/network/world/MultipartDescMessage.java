package mctmods.resourcedatapackloader.core.network.world;

import mctmods.resourcedatapackloader.core.integration.forgemultipartcbe.CbmpRubicParts;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;
import mctmods.resourcedatapackloader.core.util.PacketUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MultipartDescMessage implements IMessage {
    private BlockPos pos;
    private byte[] data;

    public MultipartDescMessage() {}

    public MultipartDescMessage(BlockPos pos, byte[] data) {
        this.pos = pos;
        this.data = data;
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.pos = PacketUtils.readBlockPos(buf);
        this.data = PacketUtils.readBytes(buf);
    }

    @Override public void toBytes(ByteBuf buf) {
        PacketUtils.write(buf, pos);
        PacketUtils.writeBytes(buf, data);
    }

    public static class Handler extends AbstractClientMessageHandler<MultipartDescMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, MultipartDescMessage message, MessageContext ctx) { CbmpRubicParts.Client.apply(world, message.pos, message.data); }
    }
}
