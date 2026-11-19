package mctmods.resourcedatapackloader.mixin.world.level.block;

import mctmods.resourcedatapackloader.common.recipes.RecipeRemainders;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrafterBlock.class) public abstract class CrafterBlockMixin {
    @ModifyExpressionValue(method = "dispenseFrom(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/CraftingRecipe;getRemainingItems(Lnet/minecraft/world/item/crafting/RecipeInput;)Lnet/minecraft/core/NonNullList;")) private NonNullList<ItemStack> rdpl$leaveReturns(NonNullList<ItemStack> left, @Local CraftingInput input, @Local RecipeHolder<?> holder) {
        RecipeRemainders.leaveReturns(holder.id(), input, left);
        return left;
    }
}
