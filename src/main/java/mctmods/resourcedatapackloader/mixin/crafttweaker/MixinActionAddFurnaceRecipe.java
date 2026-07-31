package mctmods.resourcedatapackloader.mixin.crafttweaker;

import mctmods.resourcedatapackloader.recipe.FurnaceBlocking;

import crafttweaker.mc1120.actions.ActionAddFurnaceRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ActionAddFurnaceRecipe.class, remap = false)
public abstract class MixinActionAddFurnaceRecipe {
    @Inject(method = "apply()V", at = @At("HEAD"), remap = false)
    private void rdpl$trust(CallbackInfo ci) { FurnaceBlocking.beginTrusted("CraftTweaker"); }

    @Inject(method = "apply()V", at = @At("RETURN"), remap = false)
    private void rdpl$release(CallbackInfo ci) { FurnaceBlocking.endTrusted(); }
}
