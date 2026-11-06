package mctmods.resourcedatapackloader.common.entities.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class MobGearPool {
    private static final Map<String, List<Item>> POOLS = new HashMap<>();
    private static final Map<EntityEquipmentSlot, List<Item>> ALL = new HashMap<>();

    private MobGearPool() {}

    public static void clear() {
        POOLS.clear();
        ALL.clear();
    }

    public static List<Item> pool(EntityEquipmentSlot slot, List<String> exclude) {
        return POOLS.computeIfAbsent(slot.getName() + "|" + exclude, key -> filtered(all(slot), exclude));
    }

    private static List<Item> all(EntityEquipmentSlot slot) {
        if (ALL.isEmpty()) { build(); }
        return ALL.get(slot);
    }

    private static void build() {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) { ALL.put(slot, new ArrayList<>()); }
        for (Item item : ForgeRegistries.ITEMS) {
            if (item instanceof ItemArmor) { ALL.get(((ItemArmor) item).armorType).add(item); }
            else if (item instanceof ItemShield) { ALL.get(EntityEquipmentSlot.OFFHAND).add(item); }
            else if (new ItemStack(item).getAttributeModifiers(EntityEquipmentSlot.MAINHAND).containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) { ALL.get(EntityEquipmentSlot.MAINHAND).add(item); }
        }
    }

    private static List<Item> filtered(List<Item> items, List<String> exclude) {
        List<Item> kept = new ArrayList<>();
        for (Item item : items) {
            ResourceLocation name = item.getRegistryName();
            if (name != null && !exclude.contains(name.toString()) && !exclude.contains(name.getNamespace())) { kept.add(item); }
        }
        return kept;
    }
}
