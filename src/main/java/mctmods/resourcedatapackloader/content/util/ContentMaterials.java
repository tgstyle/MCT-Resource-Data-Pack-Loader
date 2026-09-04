package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.MaterialDef;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class ContentMaterials {
    private static final Map<ResourceLocation, Tier> TIERS = new HashMap<>();
    private static final Map<ResourceLocation, Holder<ArmorMaterial>> ARMORS = new HashMap<>();

    private ContentMaterials() {}

    public static Tier tier(MaterialDef def) {
        return TIERS.computeIfAbsent(def.key(), key -> new SimpleTier(incorrect(def.harvestLevel()), def.durability(), def.efficiency(), def.damage(), def.enchantability(), () -> repair(def)));
    }

    public static void registerArmor(BiConsumer<ResourceLocation, ArmorMaterial> out) {
        for (MaterialDef def : ContentRegistry.materialDefs()) {
            if (BuiltInRegistries.ARMOR_MATERIAL.containsKey(def.key())) { continue; }
            out.accept(def.key(), armorMaterial(def));
        }
    }

    public static Holder<ArmorMaterial> armor(MaterialDef def) {
        return ARMORS.computeIfAbsent(def.key(), key -> BuiltInRegistries.ARMOR_MATERIAL.getHolder(key).map(held -> (Holder<ArmorMaterial>) held).orElseGet(() -> Holder.direct(armorMaterial(def))));
    }

    private static ArmorMaterial armorMaterial(MaterialDef def) {
        Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.BOOTS, def.reduction()[0]);
        defense.put(ArmorItem.Type.LEGGINGS, def.reduction()[1]);
        defense.put(ArmorItem.Type.CHESTPLATE, def.reduction()[2]);
        defense.put(ArmorItem.Type.HELMET, def.reduction()[3]);
        defense.put(ArmorItem.Type.BODY, def.reduction()[2]);
        ResourceLocation texture = ResourceLocation.tryParse(def.armorTexture());
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(texture == null ? def.key() : texture));
        return new ArmorMaterial(defense, def.enchantability(), sound(def.equipSound()), () -> repair(def), layers, def.toughness(), 0.0F);
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

    private static Ingredient repair(MaterialDef def) {
        Item item = ContentStacks.find(def.key(), def.repairItem());
        return item == null ? Ingredient.EMPTY : Ingredient.of(item);
    }

    private static Holder<SoundEvent> sound(String name) {
        ResourceLocation key = ResourceLocation.tryParse(name);
        Holder<SoundEvent> sound = Registered.holder(BuiltInRegistries.SOUND_EVENT, key);
        return sound == null ? SoundEvents.ARMOR_EQUIP_IRON : sound;
    }
}
