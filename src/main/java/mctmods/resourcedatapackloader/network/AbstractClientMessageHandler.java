package mctmods.resourcedatapackloader.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class AbstractClientMessageHandler<T extends IMessage> extends AbstractMessageHandler<T> {
    public final void handleServerMessage(EntityPlayer player, T message, MessageContext ctx) {}
}
