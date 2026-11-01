package mctmods.resourcedatapackloader.common.items;

import mctmods.resourcedatapackloader.common.gui.ContentContainerMenu;
import mctmods.resourcedatapackloader.core.integration.curios.ContentCurios;

import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ContentWornContainers {
    private ContentWornContainers() {}

    private static List<ItemStack> containers(ServerPlayer player) { return ContentCurios.worn(player, stack -> stack.getItem() instanceof ContentContainerItem); }

    public static void open(ServerPlayer player) {
        List<ItemStack> worn = containers(player);
        if (worn.isEmpty()) { return; }
        int from = player.containerMenu instanceof ContentContainerMenu menu ? menu.worn() : -1;
        int at = Math.floorMod(from + 1, worn.size());
        ItemStack held = worn.get(at);
        if (held.getItem() instanceof ContentContainerItem container) { container.open(player, held, () -> wornAt(player, at), at); }
    }

    private static ItemStack wornAt(ServerPlayer player, int at) {
        List<ItemStack> worn = containers(player);
        return at < worn.size() ? worn.get(at) : ItemStack.EMPTY;
    }
}
