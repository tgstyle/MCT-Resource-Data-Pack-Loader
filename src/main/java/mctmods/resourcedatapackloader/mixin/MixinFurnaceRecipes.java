package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.recipe.FurnaceBlocking;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceRecipes.class)
public abstract class MixinFurnaceRecipes {
    @Inject(method = "addSmeltingRecipe", at = @At("HEAD"), cancellable = true)
    private void rdpl$blockSmelting(ItemStack input, ItemStack stack, float experience, CallbackInfo ci) {
        if (FurnaceBlocking.rejects(stack)) { ci.cancel(); }
    }
}
