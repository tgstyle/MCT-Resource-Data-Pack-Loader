package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.compat.BaublesPouch;
import mctmods.resourcedatapackloader.content.gui.PackGuiHandler;
import mctmods.resourcedatapackloader.content.item.ContentItemContainer;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessagePouchKey implements IMessage {
    @Override public void fromBytes(ByteBuf buf) {}

    @Override public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<MessagePouchKey, IMessage> {
        @Override public IMessage onMessage(MessagePouchKey message, MessageContext ctx) {
            if (ctx.side != Side.SERVER) { return null; }
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.server.addScheduledTask(() -> open(player));
            return null;
        }

        private static void open(EntityPlayerMP player) {
            if (!ContentItemContainer.baubled()) { return; }
            int from = player.openContainer instanceof mctmods.resourcedatapackloader.content.gui.ContainerPouch
                    ? ((mctmods.resourcedatapackloader.content.gui.ContainerPouch) player.openContainer).worn()
                    : -1;
            int slot = BaublesPouch.nextWorn(player, from);
            if (slot < 0) { return; }
            player.openGui(ResourceDataPackLoader.INSTANCE, PackGuiHandler.POUCH_BAUBLE, player.world, slot, 0, 0);
        }
    }
}
