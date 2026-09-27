package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.recipe.interfaces.IRecipeRegistries;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class) public abstract class MixinReloadableServerResources {
    @Shadow @Final private RecipeManager recipes;
    @Shadow @Final private RegistryAccess registryAccess;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void rdpl$recipeRegistries(CallbackInfo ci) { ((IRecipeRegistries) recipes).rdpl$registries(registryAccess); }
}
