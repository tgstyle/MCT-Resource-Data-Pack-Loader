package mctmods.resourcedatapackloader.mixin.world.inventory;

import mctmods.resourcedatapackloader.common.recipes.RecipeRemainders;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResultSlot.class) public abstract class ResultSlotMixin {
    @Inject(method = "getRemainingItems(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Lnet/minecraft/core/NonNullList;", at = @At("RETURN")) private void rdpl$leaveReturns(CraftingInput input, Level level, CallbackInfoReturnable<NonNullList<ItemStack>> cir) {
        if (!(level instanceof ServerLevel server) || RecipeRemainders.noneMarked()) { return; }
        server.recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, server).ifPresent(holder -> RecipeRemainders.leaveReturns(holder.id().identifier(), input, cir.getReturnValue()));
    }
}
