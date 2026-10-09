package mctmods.resourcedatapackloader.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class IdleHandler<T extends IMessage> extends AbstractClientMessageHandler<T> {
    @Override public void handleClientMessage(World world, EntityPlayer player, T message, MessageContext ctx) {}
}
