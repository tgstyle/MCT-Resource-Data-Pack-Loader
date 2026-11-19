package mctmods.resourcedatapackloader.mixin.item.crafting;

import mctmods.resourcedatapackloader.common.recipes.RecipeRemainders;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingManager.class) public abstract class MixinCraftingManager {
    @WrapOperation(method = "getRemainingItems(Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/world/World;)Lnet/minecraft/util/NonNullList;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/crafting/IRecipe;getRemainingItems(Lnet/minecraft/inventory/InventoryCrafting;)Lnet/minecraft/util/NonNullList;")) private static NonNullList<ItemStack> rdpl$leaveReturns(IRecipe recipe, InventoryCrafting inv, Operation<NonNullList<ItemStack>> original) {
        NonNullList<ItemStack> left = original.call(recipe, inv);
        RecipeRemainders.leaveReturns(recipe, inv, left);
        return left;
    }
}
