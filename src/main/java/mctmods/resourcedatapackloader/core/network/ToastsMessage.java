package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.common.util.messages.Toasts;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ToastsMessage implements IMessage {
    private int kinds;

    public ToastsMessage() {}

    public ToastsMessage(int kinds) { this.kinds = kinds; }

    @Override public void fromBytes(ByteBuf buf) { kinds = buf.readByte(); }

    @Override public void toBytes(ByteBuf buf) { buf.writeByte(kinds); }

    public static class Handler extends AbstractClientMessageHandler<ToastsMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, ToastsMessage message, MessageContext ctx) { Toasts.show(message.kinds); }
    }
}
