package mctmods.resourcedatapackloader.core.network.handler;

import mctmods.resourcedatapackloader.client.util.CubeProviderClient;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class AbstractClientMessageHandler<T extends IMessage> extends AbstractMessageHandler<T> {
    public final void handleServerMessage(EntityPlayer player, T message, MessageContext ctx) {}

    @Nullable protected static CubeProviderClient cubeCache(World world) { return world.getChunkProvider() instanceof CubeProviderClient ? (CubeProviderClient) world.getChunkProvider() : null; }
}
