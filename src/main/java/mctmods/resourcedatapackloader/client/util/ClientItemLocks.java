package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.common.itemlocks.ContentItemLocks;
import mctmods.resourcedatapackloader.common.util.ContentDisabled;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TagsUpdatedEvent;

public final class ClientItemLocks {
    private static Set<String> open = Collections.emptySet();
    private static Set<Item> hidden = Collections.emptySet();
    private static boolean known;
    @Nullable private static Runnable listing;

    private ClientItemLocks() {}

    public static void receive(List<String> ids) {
        known = true;
        open = new HashSet<>(ids);
        refresh();
    }

    public static boolean hides(ItemStack stack) { return known && hidden.contains(stack.getItem()) && !ContentDisabled.disabled(stack); }

    public static void listing(Runnable apply) { listing = apply; }

    public static void onTags(TagsUpdatedEvent event) {
        if (known && event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) { refresh(); }
    }

    public static void clear() {
        if (!known) { return; }
        known = false;
        open = Collections.emptySet();
        refresh();
    }

    private static void refresh() {
        hidden = closed();
        refreshScreen();
        if (listing != null) { listing.run(); }
    }

    private static Set<Item> closed() {
        Set<Item> items = Collections.newSetFromMap(new IdentityHashMap<>());
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (!known || connection == null) { return items; }
        connection.registryAccess().registryOrThrow(Registries.ITEM).getTags().forEach(entry -> {
            String group = ContentItemLocks.group(entry.getFirst().location());
            if (group == null || open.contains(group)) { return; }
            for (Holder<Item> holder : entry.getSecond()) { items.add(holder.value()); }
        });
        return items;
    }

    private static void refreshScreen() {
        if (!(Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen creative)) { return; }
        CreativeModeTab tab = CreativeModeInventoryScreen.selectedTab;
        if (tab.getType() == CreativeModeTab.Type.CATEGORY || tab.getType() == CreativeModeTab.Type.SEARCH) { creative.refreshCurrentTabContents(tab.getDisplayItems()); }
    }
}
