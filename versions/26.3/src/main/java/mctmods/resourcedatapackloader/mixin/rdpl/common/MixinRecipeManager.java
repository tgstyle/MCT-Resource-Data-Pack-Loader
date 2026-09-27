package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.recipe.BrewingRecipes;
import mctmods.resourcedatapackloader.recipe.FurnaceRecipes;
import mctmods.resourcedatapackloader.recipe.RecipeLoading;
import mctmods.resourcedatapackloader.recipe.interfaces.IRecipeFilter;
import mctmods.resourcedatapackloader.recipe.interfaces.IRecipeRegistries;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(RecipeManager.class) public abstract class MixinRecipeManager implements IRecipeFilter, IRecipeRegistries {
    @Unique private static final int RDPL_COOKING_TIME = 200;
    @Shadow @Final @Mutable private RecipeMap recipes;
    @Shadow @Final @Mutable private Collection<RecipeHolder<?>> learnableRecipes;
    @Unique private RegistryAccess rdpl$registries;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void rdpl$afterRecipes(HolderLookup.Provider registries, CallbackInfo ci) { RecipeLoading.attach(this); }

    @Inject(method = "finalizeRecipeLoading(Lnet/minecraft/world/flag/FeatureFlagSet;)V", at = @At("HEAD"))
    private void rdpl$beforeFinalize(FeatureFlagSet enabledFlags, CallbackInfo ci) { RecipeLoading.componentsBound(this); }

    @Override public void rdpl$registries(RegistryAccess registries) { rdpl$registries = registries; }

    @Override public void rdpl$filter(boolean late) {
        ContextMap context = LineCompat.displayContext(rdpl$registries);
        List<RecipeHolder<?>> kept = new ArrayList<>();
        Set<Identifier> named = new HashSet<>();
        List<RecipeHolder<?>> candidates = new ArrayList<>(recipes.values());
        if (!late) { candidates.addAll(BrewingRecipes.build(recipes.values())); }
        for (RecipeHolder<?> holder : candidates) {
            Identifier id = holder.id().identifier();
            ItemStack result = RecipeLoading.result(holder.value(), context, rdpl$registries);
            if (late ? RecipeLoading.late(id, holder.value(), result) : RecipeLoading.doomed(id, holder.value(), result)) { continue; }
            kept.add(holder);
            named.add(id);
        }
        if (late) {
            for (FurnaceRecipes.Addition addition : FurnaceRecipes.settle()) {
                if (!named.add(addition.id())) { continue; }
                SmeltingRecipe smelting = new SmeltingRecipe(new Recipe.CommonInfo(true), new AbstractCookingRecipe.CookingBookInfo(CookingBookCategory.MISC, ""), Ingredient.of(addition.input()), addition.output(), addition.output().count() *Math.min(addition.experience(), 1.0F), RDPL_COOKING_TIME);
                kept.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, addition.id()), smelting));
            }
        }
        recipes = RecipeMap.createClient(kept);
        learnableRecipes = kept.stream().filter(holder -> !holder.value().isSpecial()).toList();
        if (!late) { RecipeLoading.finish(); }
    }
}
