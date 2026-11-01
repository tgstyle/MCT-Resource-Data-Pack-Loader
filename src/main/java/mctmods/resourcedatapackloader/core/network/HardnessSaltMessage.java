package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.common.hardness.ContentHardness;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HardnessSaltMessage implements IMessage {
    private long salt;

    public HardnessSaltMessage() {}

    public HardnessSaltMessage(long salt) { this.salt = salt; }

    @Override public void fromBytes(ByteBuf buf) { salt = buf.readLong(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeLong(salt); }

    public static class Handler extends AbstractClientMessageHandler<HardnessSaltMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, HardnessSaltMessage message, MessageContext ctx) { ContentHardness.salt(message.salt); }
    }
}
