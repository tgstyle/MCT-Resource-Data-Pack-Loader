package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.util.ContentDisabled;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IShapelessRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class RecipeDisabled {
    private RecipeDisabled() {}

    public static boolean uses(Recipe<?> recipe, ItemStack result) {
        if (!ContentDisabled.any()) { return false; }
        if (ContentDisabled.disabled(result)) { return true; }
        for (Ingredient ingredient : ingredients(recipe)) {
            if (onlyDisabled(ingredient)) { return true; }
        }
        return false;
    }

    private static List<Ingredient> ingredients(Recipe<?> recipe) {
        if (recipe instanceof ShapedRecipe shaped) { return shaped.getIngredients().stream().flatMap(Optional::stream).toList(); }
        if (recipe instanceof IShapelessRecipe shapeless) { return shapeless.rdpl$getIngredients(); }
        if (recipe instanceof SingleItemRecipe single) { return List.of(single.input()); }
        if (recipe instanceof SmithingRecipe smithing) { return Stream.of(smithing.templateIngredient(), Optional.of(smithing.baseIngredient()), smithing.additionIngredient()).flatMap(Optional::stream).toList(); }
        return recipe.placementInfo().ingredients();
    }

    private static boolean onlyDisabled(Ingredient ingredient) {
        if (ingredient.isEmpty()) { return false; }
        ICustomIngredient custom = ingredient.getCustomIngredient();
        List<ItemStack> stacks = (custom == null ? ingredient.getValues().stream() : custom.items()).map(ItemStack::new).toList();
        if (stacks.isEmpty()) { return false; }
        for (ItemStack stack : stacks) {
            if (!ContentDisabled.disabled(stack) && !ContentDisabled.emptiedTag(stack)) { return false; }
        }
        return true;
    }
}
