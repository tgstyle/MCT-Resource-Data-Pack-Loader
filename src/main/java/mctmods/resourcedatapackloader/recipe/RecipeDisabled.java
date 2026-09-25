package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.advancement.RecipeTolerance;
import mctmods.resourcedatapackloader.content.util.ContentDisabled;
import mctmods.resourcedatapackloader.content.util.ContentOreDict;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.List;

public final class RecipeDisabled {
    private RecipeDisabled() {}

    public static void apply() {
        ContentDisabled.load();
        if (!ContentDisabled.any()) { return; }
        List<ResourceLocation> crafting = RecipeRemovals.removeWhere(ForgeRegistries.RECIPES, (key, recipe) -> recipe != null && uses(recipe));
        RecipeTolerance.removedOnPurpose(crafting);
        int unlinked = ContentOreDict.strip(ContentDisabled::disabled, ContentDisabled.oreNames());
        Summary.info("disabled.recipes", "Removed " + crafting.size() + " crafting and " + furnace() + " furnace recipe(s) and " + unlinked + " ore dictionary entry/entries for disabled blocks and items");
    }

    public static int furnace() {
        if (!ContentDisabled.any()) { return 0; }
        return FurnaceRecipes.removeWhere((input, output) -> ContentDisabled.disabled(input) || ContentDisabled.disabled(output));
    }

    private static boolean uses(IRecipe recipe) {
        if (ContentDisabled.disabled(recipe.getRecipeOutput())) { return true; }
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (onlyDisabled(ingredient.getMatchingStacks())) { return true; }
        }
        return false;
    }

    private static boolean onlyDisabled(ItemStack[] stacks) {
        if (stacks.length == 0) { return false; }
        for (ItemStack stack : stacks) {
            if (!ContentDisabled.disabled(stack)) { return false; }
        }
        return true;
    }
}
