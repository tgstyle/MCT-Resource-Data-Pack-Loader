package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.recipe.FurnaceRecipes;
import mctmods.resourcedatapackloader.recipe.RecipeLoading;
import mctmods.resourcedatapackloader.recipe.interfaces.IRecipeFilter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.context.ContextMap;
import net.minecraft.util.profiling.ProfilerFiller;
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
import net.neoforged.neoforge.common.conditions.ICondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Mixin(RecipeManager.class) public abstract class MixinRecipeManager implements IRecipeFilter {
    @Unique private static final int RDPL_COOKING_TIME = 200;
    @Shadow @Final private HolderLookup.Provider registries;
    @Shadow private RecipeMap recipes;

    @WrapOperation(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleJsonResourceReloadListener;scanDirectoryWithModifier(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/FileToIdConverter;Lcom/mojang/serialization/DynamicOps;Lcom/mojang/serialization/Codec;Ljava/util/Map;Ljava/util/function/Consumer;)V"))
    private void rdpl$beforeRecipes(ResourceManager manager, FileToIdConverter lister, DynamicOps<JsonElement> ops, Codec<Recipe<?>> codec, Map<Identifier, Recipe<?>> result, Consumer<Map<Identifier, JsonElement>> jsonConsumer, Operation<Void> original) {
        Consumer<Map<Identifier, JsonElement>> loading = jsons -> {
            RecipeLoading.begin(jsons, manager, json -> ICondition.conditionsMatched(ops, json), json -> Recipe.CONDITIONAL_CODEC.parse(ops, json).getOrThrow(JsonParseException::new));
            jsonConsumer.accept(jsons);
        };
        original.call(manager, lister, ops, codec, result, loading);
    }

    @Inject(method = "apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("RETURN"))
    private void rdpl$afterRecipes(RecipeMap recipes, ResourceManager manager, ProfilerFiller profiler, CallbackInfo ci) { RecipeLoading.attach(this); }

    @Inject(method = "finalizeRecipeLoading(Lnet/minecraft/world/flag/FeatureFlagSet;)V", at = @At("HEAD"))
    private void rdpl$beforeFinalize(FeatureFlagSet enabledFlags, CallbackInfo ci) { RecipeLoading.componentsBound(this); }

    @Override public void rdpl$filter(boolean late) {
        ContextMap context = LineCompat.displayContext(registries);
        List<RecipeHolder<?>> kept = new ArrayList<>();
        Set<Identifier> named = new HashSet<>();
        for (RecipeHolder<?> holder : recipes.values()) {
            Identifier id = holder.id().identifier();
            ItemStack result = RecipeLoading.result(holder.value(), context, registries);
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
        recipes = RecipeMap.create(kept);
        if (!late) { RecipeLoading.finish(); }
    }
}
