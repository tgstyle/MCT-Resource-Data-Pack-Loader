package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.common.items.ContentContainerItem;
import mctmods.resourcedatapackloader.core.integration.baubles.BaublesPouch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class Worn {
    private Worn() {}

    public static List<ItemStack> baubles(EntityPlayer player) { return ContentContainerItem.baubled() ? BaublesPouch.wornStacks(player) : Collections.emptyList(); }

    public static List<ItemStack> worn(EntityPlayer player) {
        List<ItemStack> found = new ArrayList<>();
        for (ItemStack stack : player.inventory.armorInventory) {
            if (!stack.isEmpty()) { found.add(stack); }
        }
        found.addAll(baubles(player));
        return found;
    }
}
