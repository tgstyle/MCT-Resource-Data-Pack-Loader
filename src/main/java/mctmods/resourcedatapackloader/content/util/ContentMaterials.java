package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.MaterialDef;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.block.Block;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentMaterials {
    private static final Map<Identifier, ToolMaterial> TOOLS = new HashMap<>();
    private static final Map<Identifier, ArmorMaterial> ARMORS = new HashMap<>();
    private static final Map<String, ArmorMaterial> VANILLA_ARMORS = Map.of("minecraft:leather", ArmorMaterials.LEATHER, "minecraft:chainmail", ArmorMaterials.CHAINMAIL, "minecraft:iron", ArmorMaterials.IRON, "minecraft:gold", ArmorMaterials.GOLD,
            "minecraft:diamond", ArmorMaterials.DIAMOND, "minecraft:turtle", ArmorMaterials.TURTLE_SCUTE, "minecraft:netherite", ArmorMaterials.NETHERITE, "minecraft:armadillo", ArmorMaterials.ARMADILLO_SCUTE);

    private ContentMaterials() {}

    public static ToolMaterial tool(MaterialDef def) {
        return TOOLS.computeIfAbsent(def.key(), key -> new ToolMaterial(incorrect(def.harvestLevel()), def.durability(), def.efficiency(), def.damage(), def.enchantability(), TagKey.create(Registries.ITEM, key)));
    }

    public static ArmorMaterial armor(MaterialDef def) { return ARMORS.computeIfAbsent(def.key(), _ -> armorMaterial(def)); }

    public static boolean customArmor(MaterialDef def) { return !VANILLA_ARMORS.containsKey(def.key().toString()); }

    public static void repairWith(ResourceKey<Item> id, @Nullable MaterialDef def) {
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.add(id, (components, _, _) -> components.set(DataComponents.REPAIRABLE, def == null ? null : repair(def)));
    }

    private static ArmorMaterial armorMaterial(MaterialDef def) {
        int durability = def.durability() / 10;
        ArmorMaterial vanilla = VANILLA_ARMORS.get(def.key().toString());
        if (vanilla != null) { return new ArmorMaterial(durability, vanilla.defense(), vanilla.enchantmentValue(), vanilla.equipSound(), vanilla.toughness(), vanilla.knockbackResistance(), vanilla.repairIngredient(), vanilla.assetId()); }
        Map<ArmorType, Integer> defense = new EnumMap<>(ArmorType.class);
        defense.put(ArmorType.BOOTS, def.reduction()[0]);
        defense.put(ArmorType.LEGGINGS, def.reduction()[1]);
        defense.put(ArmorType.CHESTPLATE, def.reduction()[2]);
        defense.put(ArmorType.HELMET, def.reduction()[3]);
        defense.put(ArmorType.BODY, def.reduction()[2]);
        Identifier texture = Identifier.tryParse(def.armorTexture());
        ResourceKey<EquipmentAsset> asset = ResourceKey.create(EquipmentAssets.ROOT_ID, texture == null ? def.key() : texture);
        return new ArmorMaterial(durability, defense, def.enchantability(), sound(def.equipSound()), def.toughness(), 0.0F, TagKey.create(Registries.ITEM, def.key()), asset);
    }

    private static TagKey<Block> incorrect(int level) {
        return switch (level) {
            case 0 -> BlockTags.INCORRECT_FOR_WOODEN_TOOL;
            case 1 -> BlockTags.INCORRECT_FOR_STONE_TOOL;
            case 2 -> BlockTags.INCORRECT_FOR_IRON_TOOL;
            case 3 -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
            default -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        };
    }

    @Nullable private static Repairable repair(MaterialDef def) {
        Item item = ContentStacks.find(def.key(), def.repairItem());
        return item == null ? null : new Repairable(HolderSet.direct(BuiltInRegistries.ITEM.wrapAsHolder(item)));
    }

    private static Holder<SoundEvent> sound(String name) {
        Identifier key = Identifier.tryParse(name);
        Holder<SoundEvent> sound = Registered.holder(BuiltInRegistries.SOUND_EVENT, key);
        return sound == null ? SoundEvents.ARMOR_EQUIP_IRON : sound;
    }
}
