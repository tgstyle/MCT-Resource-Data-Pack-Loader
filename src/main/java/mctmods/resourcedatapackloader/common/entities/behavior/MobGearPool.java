package mctmods.resourcedatapackloader.common.entities.behavior;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.equipment.Equippable;

public final class MobGearPool {
    private static final Map<String, List<Item>> POOLS = new HashMap<>();
    private static final Map<EquipmentSlot, List<Item>> ALL = new EnumMap<>(EquipmentSlot.class);

    private MobGearPool() {}

    public static void clear() {
        POOLS.clear();
        ALL.clear();
    }

    public static List<Item> pool(EquipmentSlot slot, List<String> exclude) {
        return POOLS.computeIfAbsent(slot.getName() + "|" + exclude, _ -> filtered(all(slot), exclude));
    }

    private static List<Item> all(EquipmentSlot slot) {
        if (ALL.isEmpty()) { build(); }
        return ALL.get(slot);
    }

    private static void build() {
        for (EquipmentSlot slot : EquipmentSlot.values()) { ALL.put(slot, new ArrayList<>()); }
        for (Item item : BuiltInRegistries.ITEM) {
            Equippable equippable = item.components().get(DataComponents.EQUIPPABLE);
            if (equippable != null && equippable.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR && modifies(item, Attributes.ARMOR, equippable.slot())) { ALL.get(equippable.slot()).add(item); }
            else if (item instanceof ShieldItem) { ALL.get(EquipmentSlot.OFFHAND).add(item); }
            else if (modifies(item, Attributes.ATTACK_DAMAGE, EquipmentSlot.MAINHAND)) { ALL.get(EquipmentSlot.MAINHAND).add(item); }
        }
    }

    private static boolean modifies(Item item, Holder<Attribute> attribute, EquipmentSlot slot) {
        return item.getDefaultInstance().getAttributeModifiers().modifiers().stream().anyMatch(entry -> entry.attribute().equals(attribute) && entry.slot().test(slot));
    }

    private static List<Item> filtered(List<Item> items, List<String> exclude) {
        List<Item> kept = new ArrayList<>();
        for (Item item : items) {
            Identifier name = BuiltInRegistries.ITEM.getKey(item);
            if (!exclude.contains(name.toString()) && !exclude.contains(name.getNamespace())) { kept.add(item); }
        }
        return kept;
    }
}
