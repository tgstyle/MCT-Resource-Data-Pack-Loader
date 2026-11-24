package mctmods.resourcedatapackloader.core.network.handler;

import mctmods.resourcedatapackloader.client.util.CubeProviderClient;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class AbstractClientMessageHandler<T extends IMessage> extends AbstractMessageHandler<T> {
    public final void handleServerMessage(EntityPlayer player, T message, MessageContext ctx) {}

    @Nullable protected static CubeProviderClient cubeCache(World world) { return world.getChunkProvider() instanceof CubeProviderClient ? (CubeProviderClient) world.getChunkProvider() : null; }

    @Nullable protected static CubeProviderClient cubeCache(World world, String ignored) {
        CubeProviderClient cache = cubeCache(world);
        if (cache == null) { ContentLog.LOGGER.warn("Ignored {} for a world the client no longer sees as rubic", ignored); }
        return cache;
    }
}
