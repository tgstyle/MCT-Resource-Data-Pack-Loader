package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.common.sleeping.ContentSleep;
import mctmods.resourcedatapackloader.core.network.handler.AbstractServerMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetSpawnMessage implements IMessage {
    @Override public void fromBytes(ByteBuf buf) {}

    @Override public void toBytes(ByteBuf buf) {}

    public static class Handler extends AbstractServerMessageHandler<SetSpawnMessage> {
        @Override public void handleServerMessage(EntityPlayerMP player, SetSpawnMessage message, MessageContext ctx) { ContentSleep.setSpawnHere(player); }
    }
}
