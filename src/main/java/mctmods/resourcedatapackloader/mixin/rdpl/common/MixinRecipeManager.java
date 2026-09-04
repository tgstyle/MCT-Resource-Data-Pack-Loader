package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.recipe.FurnaceRecipes;
import mctmods.resourcedatapackloader.recipe.RecipeLoading;
import mctmods.resourcedatapackloader.recipe.interfaces.IRecipeFilter;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(RecipeManager.class) public abstract class MixinRecipeManager implements IRecipeFilter {
    @Unique private static final RegistryAccess RDPL_REGISTRIES = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    @Unique private static final int RDPL_COOKING_TIME = 200;
    @Shadow private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;
    @Shadow private Map<ResourceLocation, Recipe<?>> byName;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void rdpl$beforeRecipes(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) { RecipeLoading.begin(object); }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("RETURN"))
    private void rdpl$afterRecipes(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        rdpl$rebuild(false);
        RecipeLoading.attach(this);
    }

    @Override public void rdpl$filterLate() { rdpl$rebuild(true); }

    @Unique private void rdpl$rebuild(boolean late) {
        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> kept = new LinkedHashMap<>();
        Map<ResourceLocation, Recipe<?>> named = new LinkedHashMap<>();
        for (Recipe<?> recipe : byName.values()) {
            ItemStack result = rdpl$result(recipe);
            if (late ? RecipeLoading.late(recipe, result) : RecipeLoading.doomed(recipe.getId(), recipe, result)) { continue; }
            rdpl$keep(kept, named, recipe);
        }
        int added = 0;
        if (!late) {
            for (FurnaceRecipes.Addition addition : FurnaceRecipes.additions()) {
                if (named.containsKey(addition.id())) { continue; }
                rdpl$keep(kept, named, new SmeltingRecipe(addition.id(), "", CookingBookCategory.MISC, Ingredient.of(addition.input()), addition.output(), addition.experience(), RDPL_COOKING_TIME));
                added++;
            }
        }
        ImmutableMap.Builder<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> byType = ImmutableMap.builder();
        for (Map.Entry<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> entry : kept.entrySet()) { byType.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue())); }
        recipes = byType.build();
        byName = ImmutableMap.copyOf(named);
        if (!late) { RecipeLoading.finish(added); }
    }

    @Unique private static ItemStack rdpl$result(Recipe<?> recipe) {
        try { return recipe.getResultItem(RDPL_REGISTRIES); }
        catch (RuntimeException needsWorldRegistries) { return ItemStack.EMPTY; }
    }

    @Unique private static void rdpl$keep(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> kept, Map<ResourceLocation, Recipe<?>> named, Recipe<?> recipe) {
        kept.computeIfAbsent(recipe.getType(), k -> new LinkedHashMap<>()).put(recipe.getId(), recipe);
        named.put(recipe.getId(), recipe);
    }
}
