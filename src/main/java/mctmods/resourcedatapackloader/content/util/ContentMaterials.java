package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.MaterialDef;

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
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public final class ContentMaterials {
    private static final Map<ResourceLocation, Tier> TIERS = new HashMap<>();
    private static final Map<ResourceLocation, ArmorMaterial> ARMORS = new HashMap<>();
    private static final Map<ArmorItem.Type, Integer> BASE_DURABILITY = new EnumMap<>(Map.of(ArmorItem.Type.BOOTS, 13, ArmorItem.Type.LEGGINGS, 15, ArmorItem.Type.CHESTPLATE, 16, ArmorItem.Type.HELMET, 11));

    private ContentMaterials() {}

    public static Tier tier(MaterialDef def) {
        return TIERS.computeIfAbsent(def.key(), key -> new ForgeTier(def.harvestLevel(), def.durability(), def.efficiency(), def.damage(), def.enchantability(), needs(def.harvestLevel()), () -> repair(def)));
    }

    public static ArmorMaterial armor(MaterialDef def) { return ARMORS.computeIfAbsent(def.key(), key -> new Armor(def)); }

    private static TagKey<Block> needs(int level) {
        return switch (level) {
            case 0 -> Tags.Blocks.NEEDS_WOOD_TOOL;
            case 1 -> BlockTags.NEEDS_STONE_TOOL;
            case 2 -> BlockTags.NEEDS_IRON_TOOL;
            case 3 -> BlockTags.NEEDS_DIAMOND_TOOL;
            default -> Tags.Blocks.NEEDS_NETHERITE_TOOL;
        };
    }

    private static Ingredient repair(MaterialDef def) {
        Item item = ContentStacks.find(def.key(), def.repairItem());
        return item == null ? Ingredient.EMPTY : Ingredient.of(item);
    }

    private static SoundEvent sound(String name) {
        ResourceLocation key = ResourceLocation.tryParse(name);
        SoundEvent sound = key == null ? null : ForgeRegistries.SOUND_EVENTS.getValue(key);
        return sound == null ? SoundEvents.ARMOR_EQUIP_IRON : sound;
    }

    private record Armor(MaterialDef def) implements ArmorMaterial {
        @Override public int getDurabilityForType(@Nonnull ArmorItem.Type type) { return BASE_DURABILITY.get(type) * Math.max(1, def.durability() / 10); }

        @Override public int getDefenseForType(@Nonnull ArmorItem.Type type) { return def.reduction()[type.getSlot().getIndex()]; }

        @Override public int getEnchantmentValue() { return def.enchantability(); }

        @Override @Nonnull public SoundEvent getEquipSound() { return sound(def.equipSound()); }

        @Override @Nonnull public Ingredient getRepairIngredient() { return repair(def); }

        @Override @Nonnull public String getName() { return def.armorTexture(); }

        @Override public float getToughness() { return def.toughness(); }

        @Override public float getKnockbackResistance() { return 0.0F; }
    }
}
