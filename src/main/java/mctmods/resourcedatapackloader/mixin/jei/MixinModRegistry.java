package mctmods.resourcedatapackloader.mixin.jei;

import mctmods.resourcedatapackloader.util.compat.JEIPluginOrder;

import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.startup.ModRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModRegistry.class, remap = false)
public abstract class MixinModRegistry {

    @Inject(method = "addRecipeRegistryPlugin", at = @At("HEAD"))
    private void rdpl$markProvider(IRecipeRegistryPlugin recipeRegistryPlugin, CallbackInfo ci) { JEIPluginOrder.markProvider(); }
}
