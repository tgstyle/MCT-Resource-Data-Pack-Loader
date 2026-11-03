package mctmods.resourcedatapackloader.core.integration.jei;

import mctmods.resourcedatapackloader.client.util.ClientItemLocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused") @JEIPlugin public final class JEIItemLocks implements IModPlugin {
    private final Set<ItemStack> held = Collections.newSetFromMap(new IdentityHashMap<>());
    private IIngredientRegistry ingredients;
    private IIngredientBlacklist blacklist;
    private IJeiRuntime runtime;

    @Override public void register(@Nonnull IModRegistry registry) {
        ingredients = registry.getIngredientRegistry();
        blacklist = registry.getJeiHelpers().getIngredientBlacklist();
    }

    @Override public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        held.clear();
        runtime = jeiRuntime;
        ClientItemLocks.listing(this::apply);
        apply();
    }

    private void apply() {
        List<ItemStack> hide = new ArrayList<>();
        for (ItemStack stack : ingredients.getAllIngredients(VanillaTypes.ITEM)) {
            if (ClientItemLocks.hides(stack) && !held.contains(stack) && (stack.getItem() instanceof ItemEnchantedBook || !blacklist.isIngredientBlacklisted(stack))) { hide.add(stack); }
        }
        List<ItemStack> show = new ArrayList<>();
        for (ItemStack stack : held) {
            if (!ClientItemLocks.hides(stack)) { show.add(stack); }
        }
        if (hide.isEmpty() && show.isEmpty()) { return; }
        show.forEach(held::remove);
        held.addAll(hide);
        if (!hide.isEmpty()) { ingredients.removeIngredientsAtRuntime(VanillaTypes.ITEM, hide); }
        if (!show.isEmpty()) { ingredients.addIngredientsAtRuntime(VanillaTypes.ITEM, show); }
        clearCategoryCache(runtime.getRecipeRegistry());
    }

    private static void clearCategoryCache(IRecipeRegistry recipes) {
        recipes.getRecipeCategories().stream().findFirst().ifPresent(category -> {
            recipes.hideRecipeCategory(category.getUid());
            recipes.unhideRecipeCategory(category.getUid());
        });
    }
}
