package mctmods.resourcedatapackloader.core.network.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class AbstractServerMessageHandler<T extends IMessage> extends AbstractMessageHandler<T> {
    public final void handleClientMessage(World world, EntityPlayer player, T message, MessageContext ctx) {}

    public abstract void handleServerMessage(EntityPlayerMP player, T message, MessageContext ctx);

    @Override public final void handleServerMessage(EntityPlayer player, T message, MessageContext ctx) { handleServerMessage((EntityPlayerMP) player, message, ctx); }
}
