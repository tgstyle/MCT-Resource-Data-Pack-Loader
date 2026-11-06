package mctmods.resourcedatapackloader.common.entities.behavior;

import mctmods.resourcedatapackloader.common.stacks.ContentStacks;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;

public final class MobGearPool {
    private static final Map<String, List<Item>> POOLS = new HashMap<>();
    private static final Map<EquipmentSlot, List<Item>> ALL = new EnumMap<>(EquipmentSlot.class);

    private MobGearPool() {}

    public static void clear() {
        POOLS.clear();
        ALL.clear();
    }

    public static List<Item> pool(EquipmentSlot slot, List<String> exclude) {
        return POOLS.computeIfAbsent(slot.getName() + "|" + exclude, key -> filtered(all(slot), exclude));
    }

    private static List<Item> all(EquipmentSlot slot) {
        if (ALL.isEmpty()) { build(); }
        return ALL.get(slot);
    }

    private static void build() {
        for (EquipmentSlot slot : EquipmentSlot.values()) { ALL.put(slot, new ArrayList<>()); }
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof ArmorItem armor) { ALL.get(armor.getEquipmentSlot()).add(item); }
            else if (item instanceof ShieldItem) { ALL.get(EquipmentSlot.OFFHAND).add(item); }
            else if (item.getDefaultInstance().getAttributeModifiers().modifiers().stream().anyMatch(entry -> entry.attribute().equals(Attributes.ATTACK_DAMAGE) && entry.slot().test(EquipmentSlot.MAINHAND))) { ALL.get(EquipmentSlot.MAINHAND).add(item); }
        }
    }

    private static List<Item> filtered(List<Item> items, List<String> exclude) {
        List<Item> kept = new ArrayList<>();
        for (Item item : items) {
            if (!exclude.contains(ContentStacks.id(item)) && !exclude.contains(ContentStacks.namespaceOf(item))) { kept.add(item); }
        }
        return kept;
    }
}
