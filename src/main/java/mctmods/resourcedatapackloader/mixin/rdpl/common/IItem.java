package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import javax.annotation.Nullable;

@Mixin(Item.class) public interface IItem {
    @Accessor("maxStackSize") int rdpl$getMaxStackSize();

    @Accessor("maxStackSize") @Mutable void rdpl$setMaxStackSize(int size);

    @Accessor("maxDamage") int rdpl$getMaxDamage();

    @Accessor("maxDamage") @Mutable void rdpl$setMaxDamage(int damage);

    @Accessor("craftingRemainingItem") @Nullable Item rdpl$getCraftingRemainingItem();

    @Accessor("craftingRemainingItem") @Mutable void rdpl$setCraftingRemainingItem(@Nullable Item item);

    @Accessor("foodProperties") @Nullable FoodProperties rdpl$getFoodProperties();

    @Accessor("foodProperties") @Mutable void rdpl$setFoodProperties(@Nullable FoodProperties food);
}
