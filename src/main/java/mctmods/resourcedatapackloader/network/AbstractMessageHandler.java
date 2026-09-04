package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
import mctmods.resourcedatapackloader.mixin.rdpl.client.INetHandlerPlayClient;
import mctmods.resourcedatapackloader.util.SideUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import javax.annotation.Nullable;

public abstract class AbstractMessageHandler<T extends IMessage> implements IMessageHandler<T, IMessage> {
    public abstract void handleClientMessage(World world, EntityPlayer player, T message, MessageContext ctx);

    public abstract void handleServerMessage(EntityPlayer player, T message, MessageContext ctx);

    @Override @Nullable public final IMessage onMessage(T message, MessageContext ctx) {
        try {
            @SuppressWarnings("Convert2MethodRef") IThreadListener taskQueue = SideUtils.<IThreadListener>getForSide(
                    () -> () -> Minecraft.getMinecraft(),
                    () -> () -> FMLCommonHandler.instance().getMinecraftServerInstance()
            );
            if (!taskQueue.isCallingFromMinecraftThread()) {
                taskQueue.addScheduledTask(() -> onMessage(message, ctx));
                return null;
            }
            World mainWorld = SideUtils.getForSide(
                    () -> ClientAccessProxy::getWorld,
                    () -> () -> FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0)
            );
            if (mainWorld == null) {
                Rubic.LOGGER.warn("Received packet when world doesn't exist!");
                return null;
            }
            EntityPlayer player = SideUtils.getForSide(ctx,
                    () -> ClientAccessProxy::getPlayer,
                    () -> c -> c.getServerHandler().player
            );
            if (ctx.side.isClient()) { handleClientMessage(mainWorld, player, message, ctx); }
            else { handleServerMessage(player, message, ctx); }
            return null;
        } catch (Error error) {
            Rubic.LOGGER.catching(error);
            FMLCommonHandler.instance().exitJava(-1, false);
            throw error;
        } catch (RuntimeException ex) {
            Rubic.LOGGER.error("A {} packet could not be handled and was dropped", message.getClass().getSimpleName(), ex);
            return null;
        }
    }

    private static class ClientAccessProxy {
        static EntityPlayer getPlayer(MessageContext c) { return c.side.isClient() ? Minecraft.getMinecraft().player : c.getServerHandler().player; }

        @Nullable static World getWorld() {
            return Minecraft.getMinecraft().getConnection() == null ? null :
                    ((INetHandlerPlayClient) Minecraft.getMinecraft().getConnection()).getWorld();
        }
    }
}
