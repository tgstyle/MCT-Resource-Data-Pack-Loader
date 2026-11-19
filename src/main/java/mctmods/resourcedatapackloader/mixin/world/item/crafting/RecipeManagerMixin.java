package mctmods.resourcedatapackloader.mixin.world.item.crafting;

import mctmods.resourcedatapackloader.common.recipes.FurnaceRecipes;
import mctmods.resourcedatapackloader.common.recipes.IRecipeFilter;
import mctmods.resourcedatapackloader.common.recipes.RecipeLoading;
import mctmods.resourcedatapackloader.common.recipes.RecipeRemainders;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RecipeManager.class) public abstract class RecipeManagerMixin implements IRecipeFilter {
    @Unique private static final RegistryAccess rdpl$REGISTRIES = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    @Unique private static final int rdpl$COOKING_TIME = 200;
    @Shadow private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;
    @Shadow private Map<ResourceLocation, Recipe<?>> byName;
    @Shadow(remap = false) @Final private ICondition.IContext context;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD")) private void rdpl$beforeRecipes(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) { RecipeLoading.begin(object, resourceManager, json -> !json.isJsonObject() || CraftingHelper.processConditions(json.getAsJsonObject(), "conditions", context), this::rdpl$parse); }

    @SuppressWarnings("ConstantValue") @Unique private Recipe<?> rdpl$parse(ResourceLocation id, JsonElement json) {
        Recipe<?> recipe = RecipeManager.fromJson(id, GsonHelper.convertToJsonObject(json, "top element"), context);
        if (recipe == null) { throw new JsonParseException("its serializer produced no recipe"); }
        return recipe;
    }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("RETURN")) private void rdpl$afterRecipes(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        rdpl$rebuild(false);
        RecipeLoading.attach(this);
    }

    @Override public void rdpl$filterLate() { rdpl$rebuild(true); }

    @Override public void rdpl$filterSkipped() {
        rdpl$rebuild(false);
        RecipeLoading.attach(this);
    }

    @Inject(method = "getRemainingItemsFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Lnet/minecraft/core/NonNullList;", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD) private void rdpl$leaveReturns(RecipeType<?> recipeType, Container inventory, Level level, CallbackInfoReturnable<NonNullList<ItemStack>> cir, Optional<? extends Recipe<?>> optional) {
        if (level.isClientSide || recipeType != RecipeType.CRAFTING || !(inventory instanceof CraftingContainer crafting) || RecipeRemainders.noneMarked()) { return; }
        optional.ifPresent(recipe -> RecipeRemainders.leaveReturns(recipe.getId(), crafting, cir.getReturnValue()));
    }

    @Unique private void rdpl$rebuild(boolean late) {
        RecipeRemainders.begin();
        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> kept = new LinkedHashMap<>();
        Map<ResourceLocation, Recipe<?>> named = new LinkedHashMap<>();
        for (Recipe<?> recipe : byName.values()) {
            ItemStack result = rdpl$result(recipe);
            if (late ? RecipeLoading.late(recipe.getId(), recipe, result) : RecipeLoading.doomed(recipe.getId(), recipe, result)) { continue; }
            RecipeLoading.remaindered(recipe.getId(), recipe, result);
            rdpl$keep(kept, named, recipe);
        }
        if (late) {
            for (FurnaceRecipes.Addition addition : FurnaceRecipes.settle()) {
                if (!named.containsKey(addition.id())) { rdpl$keep(kept, named, new SmeltingRecipe(addition.id(), "", CookingBookCategory.MISC, Ingredient.of(addition.input()), addition.output(), addition.output().getCount() * Math.min(addition.experience(), 1.0F), rdpl$COOKING_TIME)); }
            }
        }
        ImmutableMap.Builder<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> byType = ImmutableMap.builder();
        for (Map.Entry<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> entry : kept.entrySet()) { byType.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue())); }
        recipes = byType.build();
        byName = ImmutableMap.copyOf(named);
        if (!late) { RecipeLoading.finish(); }
    }

    @Unique private static ItemStack rdpl$result(Recipe<?> recipe) {
        try { return recipe.getResultItem(rdpl$REGISTRIES); }
        catch (RuntimeException needsWorldRegistries) { return ItemStack.EMPTY; }
    }

    @Unique private static void rdpl$keep(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> kept, Map<ResourceLocation, Recipe<?>> named, Recipe<?> recipe) {
        kept.computeIfAbsent(recipe.getType(), k -> new LinkedHashMap<>()).put(recipe.getId(), recipe);
        named.put(recipe.getId(), recipe);
    }
}
