package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.common.gui.ContainerPortway;
import mctmods.resourcedatapackloader.core.network.handler.AbstractServerMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PortwayFilterMessage implements IMessage {
    private String text = "";

    public PortwayFilterMessage() {}

    public PortwayFilterMessage(String text) { this.text = text; }

    @Override public void fromBytes(ByteBuf buf) { text = ByteBufUtils.readUTF8String(buf); }

    @Override public void toBytes(ByteBuf buf) { ByteBufUtils.writeUTF8String(buf, text); }

    public static class Handler extends AbstractServerMessageHandler<PortwayFilterMessage> {
        @Override public void handleServerMessage(EntityPlayer player, PortwayFilterMessage message, MessageContext ctx) {
            if (player.openContainer instanceof ContainerPortway) { ((ContainerPortway) player.openContainer).filter(message.text); }
        }
    }
}
