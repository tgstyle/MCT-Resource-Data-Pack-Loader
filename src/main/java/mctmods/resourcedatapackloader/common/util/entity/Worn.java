package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.core.integration.curios.ContentCurios;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class Worn {
    private Worn() {}

    public static List<ItemStack> worn(Player player) {
        List<ItemStack> found = new ArrayList<>();
        for (ItemStack stack : player.getInventory().armor) {
            if (!stack.isEmpty()) { found.add(stack); }
        }
        found.addAll(ContentCurios.worn(player, stack -> !stack.isEmpty()));
        return found;
    }
}
