package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.advancement.RecipeTolerance;

import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeUnlockedTrigger.class) public abstract class MixinRecipeUnlockedTrigger {
    @Redirect(method = "deserializeInstance(Lcom/google/gson/JsonObject;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/advancements/critereon/RecipeUnlockedTrigger$Instance;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/crafting/CraftingManager;getRecipe(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/item/crafting/IRecipe;"))
    private IRecipe rdpl$tolerateMissing(ResourceLocation name) {
        if (RecipeTolerance.disabled()) { return CraftingManager.getRecipe(name); }
        return RecipeTolerance.resolve(name);
    }
}
