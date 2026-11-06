package mctmods.resourcedatapackloader.common.entities.behavior;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.registries.ForgeRegistries;

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
        for (Item item : ForgeRegistries.ITEMS) {
            if (item instanceof ArmorItem armor) { ALL.get(armor.getEquipmentSlot()).add(item); }
            else if (item instanceof ShieldItem) { ALL.get(EquipmentSlot.OFFHAND).add(item); }
            else if (item.getDefaultInstance().getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE)) { ALL.get(EquipmentSlot.MAINHAND).add(item); }
        }
    }

    private static List<Item> filtered(List<Item> items, List<String> exclude) {
        List<Item> kept = new ArrayList<>();
        for (Item item : items) {
            ResourceLocation name = ForgeRegistries.ITEMS.getKey(item);
            if (name != null && !exclude.contains(name.toString()) && !exclude.contains(name.getNamespace())) { kept.add(item); }
        }
        return kept;
    }
}
