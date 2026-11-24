package mctmods.resourcedatapackloader.common.util.entity;

import mctmods.resourcedatapackloader.core.integration.curios.ContentCurios;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class Worn {
    private Worn() {}

    public static List<ItemStack> worn(Player player) { return collect(player, EquipmentSlot::isArmor, stack -> !stack.isEmpty()); }

    public static List<ItemStack> humanoid(Player player) { return collect(player, slot -> slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR, _ -> true); }

    private static List<ItemStack> collect(Player player, Predicate<EquipmentSlot> slots, Predicate<ItemStack> curios) {
        List<ItemStack> found = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (slots.test(slot) && !stack.isEmpty()) { found.add(stack); }
        }
        found.addAll(ContentCurios.worn(player, curios));
        return found;
    }
}
