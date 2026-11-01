package mctmods.resourcedatapackloader.core.network.handler;

import mctmods.resourcedatapackloader.client.util.AbstractMessageHandlerClient;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.SideUtils;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class AbstractMessageHandler<T extends IMessage> implements IMessageHandler<T, IMessage> {
    public abstract void handleClientMessage(World world, EntityPlayer player, T message, MessageContext ctx);

    public abstract void handleServerMessage(EntityPlayer player, T message, MessageContext ctx);

    @Override @Nullable public final IMessage onMessage(T message, MessageContext ctx) {
        try {
            IThreadListener taskQueue = SideUtils.getForSide(
                    () -> AbstractMessageHandlerClient::taskQueue,
                    () -> () -> FMLCommonHandler.instance().getMinecraftServerInstance()
            );
            if (!taskQueue.isCallingFromMinecraftThread()) {
                taskQueue.addScheduledTask(() -> onMessage(message, ctx));
                return null;
            }
            World mainWorld = SideUtils.getForSide(
                    () -> AbstractMessageHandlerClient::world,
                    () -> () -> FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0)
            );
            if (mainWorld == null) {
                ContentLog.LOGGER.warn("Received packet when world doesn't exist!");
                return null;
            }
            EntityPlayer player = SideUtils.getForSide(ctx,
                    () -> AbstractMessageHandlerClient::player,
                    () -> c -> c.getServerHandler().player
            );
            if (ctx.side.isClient()) { handleClientMessage(mainWorld, player, message, ctx); }
            else { handleServerMessage(player, message, ctx); }
            return null;
        } catch (Error error) {
            ContentLog.LOGGER.catching(error);
            FMLCommonHandler.instance().exitJava(-1, false);
            throw error;
        } catch (RuntimeException ex) {
            ContentLog.LOGGER.error("A {} packet could not be handled and was dropped", message.getClass().getSimpleName(), ex);
            return null;
        }
    }
}
