package mctmods.resourcedatapackloader.common.util.loot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.List;

public final class Stacks {
    private Stacks() {}

    public static List<ItemStack> held(Player player) {
        List<ItemStack> all = new ArrayList<>();
        Container inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty()) { all.add(stack); }
        }
        return all;
    }

    public static void dropAt(Level level, double x, double y, double z, ItemStack stack, int delay) {
        ItemEntity item = new ItemEntity(level, x, y, z, stack);
        item.setPickUpDelay(delay);
        level.addFreshEntity(item);
    }
}
