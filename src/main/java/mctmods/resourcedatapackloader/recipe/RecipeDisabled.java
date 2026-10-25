package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.util.ContentDisabled;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import java.util.List;

public final class RecipeDisabled {
    private RecipeDisabled() {}

    public static boolean uses(Recipe<?> recipe, ItemStack result) {
        if (ContentDisabled.none()) { return false; }
        if (ContentDisabled.disabled(result)) { return true; }
        for (Ingredient ingredient : ingredients(recipe)) {
            if (onlyDisabled(ingredient)) { return true; }
        }
        return false;
    }

    private static List<Ingredient> ingredients(Recipe<?> recipe) {
        if (recipe instanceof SmithingTransformRecipe transform) { return List.of(transform.template, transform.base, transform.addition); }
        if (recipe instanceof SmithingTrimRecipe trim) { return List.of(trim.template, trim.base, trim.addition); }
        return recipe.getIngredients();
    }

    private static boolean onlyDisabled(Ingredient ingredient) {
        if (ingredient.isEmpty()) { return false; }
        ItemStack[] stacks = ingredient.getItems();
        if (stacks.length == 0) { return false; }
        for (ItemStack stack : stacks) {
            if (!ContentDisabled.disabled(stack) && !ContentDisabled.emptiedTag(stack)) { return false; }
        }
        return true;
    }
}
