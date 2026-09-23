package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.compat.ContentCurios;
import mctmods.resourcedatapackloader.content.menu.ContentContainerMenu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ContentWornContainers {
    private ContentWornContainers() {}

    public static void open(ServerPlayer player) {
        List<ItemStack> worn = ContentCurios.worn(player);
        if (worn.isEmpty()) { return; }
        int from = player.containerMenu instanceof ContentContainerMenu menu ? menu.worn() : -1;
        int at = Math.floorMod(from + 1, worn.size());
        ItemStack held = worn.get(at);
        if (held.getItem() instanceof ContentContainerItem container) { container.open(player, held, () -> wornAt(player, at), at); }
    }

    private static ItemStack wornAt(ServerPlayer player, int at) {
        List<ItemStack> worn = ContentCurios.worn(player);
        return at < worn.size() ? worn.get(at) : ItemStack.EMPTY;
    }
}
