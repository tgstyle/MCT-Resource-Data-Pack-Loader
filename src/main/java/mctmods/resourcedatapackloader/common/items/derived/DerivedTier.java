package mctmods.resourcedatapackloader.common.items.derived;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

final class DerivedTier implements Tier {
    int level;
    int uses;
    float speed;
    float attackDamageBonus;
    int enchantmentValue;
    TagKey<Block> tag;
    Supplier<Ingredient> repairIngredient;
    final String name;

    @SuppressWarnings("deprecation") DerivedTier(Tier from, TagKey<Block> tag) {
        name = from.toString();
        level = from.getLevel();
        uses = from.getUses();
        speed = from.getSpeed();
        attackDamageBonus = from.getAttackDamageBonus();
        enchantmentValue = from.getEnchantmentValue();
        this.tag = tag;
        repairIngredient = from::getRepairIngredient;
    }

    @Override public String toString() { return name; }

    @Override public int getUses() { return uses; }

    @Override public float getSpeed() { return speed; }

    @Override public float getAttackDamageBonus() { return attackDamageBonus; }

    @Override public int getLevel() { return level; }

    @Override public int getEnchantmentValue() { return enchantmentValue; }

    @Override @Nonnull public Ingredient getRepairIngredient() { return repairIngredient.get(); }

    @Override public TagKey<Block> getTag() { return tag; }
}
