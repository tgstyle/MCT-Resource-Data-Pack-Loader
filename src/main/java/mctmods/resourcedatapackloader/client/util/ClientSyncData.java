package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.syncdata.SyncMirror;
import mctmods.resourcedatapackloader.core.network.SyncDataMessage;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ClientSyncData {
    private ClientSyncData() {}

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { Minecraft.getMinecraft().addScheduledTask(SyncMirror::reset); }

    public static class Handler extends AbstractClientMessageHandler<SyncDataMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, SyncDataMessage message, MessageContext ctx) { SyncMirror.apply(message.full, message.entries); }
    }
}
