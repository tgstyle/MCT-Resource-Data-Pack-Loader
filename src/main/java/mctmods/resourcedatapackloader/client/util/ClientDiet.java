package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.core.network.EatenMessage;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ResourceDataPackLoader.MOD_ID) @SideOnly(Side.CLIENT) public final class ClientDiet {
    private static Set<String> eaten = Collections.emptySet();
    private static boolean known;

    private ClientDiet() {}

    public static void receive(List<String> kinds) {
        known = true;
        eaten = new HashSet<>(kinds);
    }

    public static boolean waiting() { return !known; }

    public static boolean ate(String kind) { return eaten.contains(kind); }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { ClientResets.onDisconnect(ClientDiet::reset); }

    private static void reset() {
        known = false;
        eaten = Collections.emptySet();
    }

    public static class Handler extends AbstractClientMessageHandler<EatenMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, EatenMessage message, MessageContext ctx) { receive(message.eaten); }
    }
}
