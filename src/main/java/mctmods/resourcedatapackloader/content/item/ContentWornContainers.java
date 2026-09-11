package mctmods.resourcedatapackloader.content.item;

import mctmods.resourcedatapackloader.content.compat.ContentCurios;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ContentWornContainers {
    private ContentWornContainers() {}

    public static void open(ServerPlayer player, int after) {
        List<ItemStack> worn = ContentCurios.worn(player);
        if (worn.isEmpty()) { return; }
        int at = Math.floorMod(after + 1, worn.size());
        ItemStack held = worn.get(at);
        if (held.getItem() instanceof ContentContainerItem container) { container.open(player, held); }
    }
}
