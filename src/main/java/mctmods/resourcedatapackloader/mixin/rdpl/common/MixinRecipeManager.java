package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.recipe.FurnaceRecipes;
import mctmods.resourcedatapackloader.recipe.RecipeLoading;
import mctmods.resourcedatapackloader.recipe.interfaces.IRecipeFilter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(RecipeManager.class) public abstract class MixinRecipeManager implements IRecipeFilter {
    @Unique private static final int RDPL_COOKING_TIME = 200;
    @Shadow @Final private HolderLookup.Provider registries;
    @Shadow private Multimap<RecipeType<?>, RecipeHolder<?>> byType;
    @Shadow private Map<ResourceLocation, RecipeHolder<?>> byName;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void rdpl$beforeRecipes(Map<ResourceLocation, JsonElement> loaded, ResourceManager manager, ProfilerFiller profiler, CallbackInfo ci) { RecipeLoading.begin(loaded); }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("RETURN"))
    private void rdpl$afterRecipes(Map<ResourceLocation, JsonElement> loaded, ResourceManager manager, ProfilerFiller profiler, CallbackInfo ci) {
        rdpl$rebuild(false);
        RecipeLoading.attach(this);
    }

    @Override public void rdpl$filterLate() { rdpl$rebuild(true); }

    @Unique private void rdpl$rebuild(boolean late) {
        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> kept = ImmutableMultimap.builder();
        Map<ResourceLocation, RecipeHolder<?>> named = new LinkedHashMap<>();
        for (RecipeHolder<?> holder : byName.values()) {
            ItemStack result = holder.value().getResultItem(registries);
            if (late ? RecipeLoading.late(holder.value(), result) : RecipeLoading.doomed(holder.id(), holder.value(), result)) { continue; }
            kept.put(holder.value().getType(), holder);
            named.put(holder.id(), holder);
        }
        int added = 0;
        if (!late) {
            for (FurnaceRecipes.Addition addition : FurnaceRecipes.additions()) {
                if (named.containsKey(addition.id())) { continue; }
                RecipeHolder<SmeltingRecipe> holder = new RecipeHolder<>(addition.id(), new SmeltingRecipe("", CookingBookCategory.MISC, Ingredient.of(addition.input()), addition.output(), addition.experience(), RDPL_COOKING_TIME));
                kept.put(RecipeType.SMELTING, holder);
                named.put(addition.id(), holder);
                added++;
            }
        }
        byType = kept.build();
        byName = ImmutableMap.copyOf(named);
        if (!late) { RecipeLoading.finish(added); }
    }
}
