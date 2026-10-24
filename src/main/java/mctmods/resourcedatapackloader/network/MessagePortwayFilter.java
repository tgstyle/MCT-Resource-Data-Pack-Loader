package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.gui.ContainerPortway;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePortwayFilter implements IMessage {
    private String text = "";

    public MessagePortwayFilter() {}

    public MessagePortwayFilter(String text) { this.text = text; }

    @Override public void fromBytes(ByteBuf buf) { text = ByteBufUtils.readUTF8String(buf); }

    @Override public void toBytes(ByteBuf buf) { ByteBufUtils.writeUTF8String(buf, text); }

    public static class Handler extends AbstractServerMessageHandler<MessagePortwayFilter> {
        @Override public void handleServerMessage(EntityPlayer player, MessagePortwayFilter message, MessageContext ctx) {
            if (player.openContainer instanceof ContainerPortway) { ((ContainerPortway) player.openContainer).filter(message.text); }
        }
    }
}
