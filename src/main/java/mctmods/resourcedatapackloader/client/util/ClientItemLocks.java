package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.itemlocks.ContentItemLocks;
import mctmods.resourcedatapackloader.common.util.ContentDisabled;
import mctmods.resourcedatapackloader.core.network.UnlocksMessage;
import mctmods.resourcedatapackloader.core.network.handler.AbstractClientMessageHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ClientItemLocks {
    private static Set<String> open = Collections.emptySet();
    private static boolean known;
    @Nullable private static Runnable listing;

    private ClientItemLocks() {}

    public static void receive(List<String> ids) {
        known = true;
        open = new HashSet<>(ids);
        refresh();
    }

    public static boolean active() { return known && ContentItemLocks.any(); }

    public static boolean hides(ItemStack stack) { return known && !ContentDisabled.disabled(stack) && ContentItemLocks.closed(stack, open); }

    public static void listing(Runnable apply) { listing = apply; }

    @SubscribeEvent public static void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) { Minecraft.getMinecraft().addScheduledTask(ClientItemLocks::reset); }

    private static void reset() {
        if (!known) { return; }
        known = false;
        open = Collections.emptySet();
        refresh();
    }

    private static void refresh() {
        refreshScreen();
        if (listing != null) { listing.run(); }
    }

    private static void refreshScreen() {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (!(screen instanceof GuiContainerCreative)) { return; }
        GuiContainerCreative creative = (GuiContainerCreative) screen;
        CreativeTabs tab = CreativeTabs.CREATIVE_TAB_ARRAY[creative.getSelectedTabIndex()];
        if (tab == CreativeTabs.INVENTORY) { return; }
        if (tab.hasSearchBar()) { creative.updateCreativeSearch(); }
        else { creative.setCurrentCreativeTab(tab); }
    }

    public static class Handler extends AbstractClientMessageHandler<UnlocksMessage> {
        @Override public void handleClientMessage(World world, EntityPlayer player, UnlocksMessage message, MessageContext ctx) { receive(message.open); }
    }
}
