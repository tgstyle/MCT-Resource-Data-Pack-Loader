package mctmods.resourcedatapackloader.common.items.derived;

import java.util.EnumMap;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public final class CopiedArmorMaterial implements ArmorMaterial {
    private final EnumMap<ArmorItem.Type, Integer> durability = new EnumMap<>(ArmorItem.Type.class);
    private final EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final Supplier<Ingredient> repairIngredient;
    private final String name;
    private final float toughness;
    private final float knockbackResistance;

    public CopiedArmorMaterial(ArmorMaterial source) {
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            durability.put(type, source.getDurabilityForType(type));
            defense.put(type, source.getDefenseForType(type));
        }
        enchantmentValue = source.getEnchantmentValue();
        sound = source.getEquipSound();
        repairIngredient = source::getRepairIngredient;
        name = source.getName();
        toughness = source.getToughness();
        knockbackResistance = source.getKnockbackResistance();
    }

    @Override
    public int getDurabilityForType(@Nonnull ArmorItem.Type type) { return durability.get(type); }

    @Override
    public int getDefenseForType(@Nonnull ArmorItem.Type type) { return defense.get(type); }

    @Override
    public int getEnchantmentValue() { return enchantmentValue; }

    @Override
    @Nonnull public SoundEvent getEquipSound() { return sound; }

    @Override
    @Nonnull public Ingredient getRepairIngredient() { return repairIngredient.get(); }

    @Override
    @Nonnull public String getName() { return name; }

    @Override
    public float getToughness() { return toughness; }

    @Override
    public float getKnockbackResistance() { return knockbackResistance; }
}
