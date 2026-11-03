package mctmods.resourcedatapackloader.core.integration.jei;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.client.util.ClientItemLocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IIngredientFilter;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused") @JeiPlugin public final class JEIItemLocks implements IModPlugin {
    private static final ResourceLocation UID = ResourceDataPackLoader.id("item_locks");
    private final Set<ItemStack> held = Collections.newSetFromMap(new IdentityHashMap<>());
    @Nullable private IJeiRuntime runtime;

    @Override @Nonnull public ResourceLocation getPluginUid() { return UID; }

    @Override public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        held.clear();
        runtime = jeiRuntime;
        ClientItemLocks.listing(this::apply);
        apply();
    }

    @Override public void onRuntimeUnavailable() {
        runtime = null;
        held.clear();
    }

    private void apply() {
        if (runtime == null) { return; }
        IIngredientManager ingredients = runtime.getIngredientManager();
        IIngredientVisibility visibility = runtime.getJeiHelpers().getIngredientVisibility();
        List<ItemStack> hide = new ArrayList<>();
        for (ItemStack stack : ingredients.getAllIngredients(VanillaTypes.ITEM_STACK)) {
            if (ClientItemLocks.hides(stack) && !held.contains(stack) && visibility.isIngredientVisible(VanillaTypes.ITEM_STACK, stack)) { hide.add(stack); }
        }
        List<ItemStack> show = new ArrayList<>();
        for (ItemStack stack : held) {
            if (!ClientItemLocks.hides(stack)) { show.add(stack); }
        }
        if (hide.isEmpty() && show.isEmpty()) { return; }
        show.forEach(held::remove);
        held.addAll(hide);
        if (!hide.isEmpty()) { ingredients.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hide); }
        if (!show.isEmpty()) { ingredients.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, show); refreshList(runtime.getIngredientFilter()); }
        clearCategoryCache(runtime.getRecipeManager());
    }

    private static void refreshList(IIngredientFilter filter) {
        String text = filter.getFilterText();
        filter.setFilterText(text + " ");
        filter.setFilterText(text);
    }

    private static void clearCategoryCache(IRecipeManager recipes) {
        recipes.createRecipeCategoryLookup().get().findFirst().ifPresent(category -> {
            recipes.hideRecipeCategory(category.getRecipeType());
            recipes.unhideRecipeCategory(category.getRecipeType());
        });
    }
}
