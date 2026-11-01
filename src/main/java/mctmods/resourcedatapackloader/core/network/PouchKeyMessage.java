package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.gui.ContainerPouch;
import mctmods.resourcedatapackloader.common.gui.PackGuiHandler;
import mctmods.resourcedatapackloader.common.items.ContentContainerItem;
import mctmods.resourcedatapackloader.core.integration.baubles.BaublesPouch;
import mctmods.resourcedatapackloader.core.network.handler.AbstractServerMessageHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PouchKeyMessage implements IMessage {
    @Override public void fromBytes(ByteBuf buf) {}

    @Override public void toBytes(ByteBuf buf) {}

    public static class Handler extends AbstractServerMessageHandler<PouchKeyMessage> {
        @Override public void handleServerMessage(EntityPlayer player, PouchKeyMessage message, MessageContext ctx) {
            if (!ContentContainerItem.baubled()) { return; }
            int from = player.openContainer instanceof ContainerPouch ? ((ContainerPouch) player.openContainer).worn() : -1;
            int slot = BaublesPouch.nextWorn(player, from);
            if (slot < 0) { return; }
            player.openGui(ResourceDataPackLoader.INSTANCE, PackGuiHandler.POUCH_BAUBLE, player.world, slot, 0, 0);
        }
    }
}
