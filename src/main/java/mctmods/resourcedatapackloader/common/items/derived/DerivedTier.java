package mctmods.resourcedatapackloader.common.items.derived;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

final class DerivedTier implements Tier {
    int uses;
    float speed;
    float attackDamageBonus;
    TagKey<Block> incorrectBlocksForDrops;
    int enchantmentValue;
    Supplier<Ingredient> repairIngredient;
    final String name;

    DerivedTier(Tier from) {
        name = from.toString();
        uses = from.getUses();
        speed = from.getSpeed();
        attackDamageBonus = from.getAttackDamageBonus();
        incorrectBlocksForDrops = from.getIncorrectBlocksForDrops();
        enchantmentValue = from.getEnchantmentValue();
        repairIngredient = from::getRepairIngredient;
    }

    @Override public String toString() { return name; }

    @Override public int getUses() { return uses; }

    @Override public float getSpeed() { return speed; }

    @Override public float getAttackDamageBonus() { return attackDamageBonus; }

    @Override @Nonnull public TagKey<Block> getIncorrectBlocksForDrops() { return incorrectBlocksForDrops; }

    @Override public int getEnchantmentValue() { return enchantmentValue; }

    @Override @Nonnull public Ingredient getRepairIngredient() { return repairIngredient.get(); }
}
