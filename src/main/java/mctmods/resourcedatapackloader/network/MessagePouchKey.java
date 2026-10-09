package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.compat.BaublesPouch;
import mctmods.resourcedatapackloader.content.gui.ContainerPouch;
import mctmods.resourcedatapackloader.content.gui.PackGuiHandler;
import mctmods.resourcedatapackloader.content.item.ContentItemContainer;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePouchKey implements IMessage {
    @Override public void fromBytes(ByteBuf buf) {}

    @Override public void toBytes(ByteBuf buf) {}

    public static class Handler extends AbstractServerMessageHandler<MessagePouchKey> {
        @Override public void handleServerMessage(EntityPlayer player, MessagePouchKey message, MessageContext ctx) {
            if (!ContentItemContainer.baubled()) { return; }
            int from = player.openContainer instanceof ContainerPouch ? ((ContainerPouch) player.openContainer).worn() : -1;
            int slot = BaublesPouch.nextWorn(player, from);
            if (slot < 0) { return; }
            player.openGui(ResourceDataPackLoader.INSTANCE, PackGuiHandler.POUCH_BAUBLE, player.world, slot, 0, 0);
        }
    }
}
