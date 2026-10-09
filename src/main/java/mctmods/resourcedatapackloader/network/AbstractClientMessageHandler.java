package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.client.CubeProviderClient;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import javax.annotation.Nullable;

public abstract class AbstractClientMessageHandler<T extends IMessage> extends AbstractMessageHandler<T> {
    public final void handleServerMessage(EntityPlayer player, T message, MessageContext ctx) {}

    @Nullable protected static CubeProviderClient cubeCache(World world) { return world.getChunkProvider() instanceof CubeProviderClient ? (CubeProviderClient) world.getChunkProvider() : null; }
}
