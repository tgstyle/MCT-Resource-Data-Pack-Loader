package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class RecipeSwaps {
    private static final Set<IRecipe> TOUCHED = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Set<IRecipe> LEFT = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Set<Class<?>> LEFT_TYPES = new HashSet<>();
    private static int ingredients;
    private static int results;

    private RecipeSwaps() {}

    public static void apply() {
        TOUCHED.clear();
        LEFT.clear();
        LEFT_TYPES.clear();
        ingredients = 0;
        results = 0;
        List<SwapEntry> entries = SwapEntry.all();
        if (entries.isEmpty()) { return; }
        Map<NonNullList<Ingredient>, List<ResourceLocation>> holders = new IdentityHashMap<>();
        for (Map.Entry<ResourceLocation, IRecipe> each : ForgeRegistries.RECIPES.getEntries()) {
            NonNullList<Ingredient> list = each.getValue().getIngredients();
            if (!list.isEmpty()) { holders.computeIfAbsent(list, k -> new ArrayList<>()).add(each.getKey()); }
        }
        for (SwapEntry entry : entries) {
            for (Map.Entry<ResourceLocation, IRecipe> each : ForgeRegistries.RECIPES.getEntries()) {
                if (entry.inScope(each.getKey())) { swap(entry, each.getKey(), each.getValue(), holders); }
            }
        }
    }

    static void summarize(int furnaceIngredients, int furnaceResults, int furnaceRecipes) {
        Summary.info("recipes.swapped", "Replaced " + (ingredients + furnaceIngredients) + " ingredient(s) and " + (results + furnaceResults) + " result(s) in " + TOUCHED.size() + " crafting and " + furnaceRecipes + " furnace recipe(s)");
        if (!LEFT.isEmpty()) { Summary.info("recipes.swapped.left", "Left " + LEFT.size() + " recipe(s) of " + LEFT_TYPES.size() + " type(s) alone, they name a replaced item where RDPL cannot rewrite it"); }
    }

    private static void swap(SwapEntry entry, ResourceLocation key, IRecipe recipe, Map<NonNullList<Ingredient>, List<ResourceLocation>> holders) {
        NonNullList<Ingredient> list = recipe.getIngredients();
        boolean named = names(entry, list);
        boolean made = entry.output() && entry.matches(recipe.getRecipeOutput());
        if (!named && !made) { return; }
        boolean stuck = named && !slots(entry, recipe, list, holders);
        if (made && !result(entry, recipe)) { stuck = true; }
        if (!stuck || !LEFT.add(recipe)) { return; }
        if (LEFT_TYPES.add(recipe.getClass())) { ContentLog.LOGGER.info("Left {} alone, a recipe of type {} names a replaced item where RDPL cannot rewrite it", key, recipe.getClass().getName()); }
    }

    private static boolean names(SwapEntry entry, NonNullList<Ingredient> list) {
        for (Ingredient ingredient : list) {
            for (ItemStack member : ingredient.getMatchingStacks()) {
                if (entry.matches(member)) { return true; }
            }
        }
        return false;
    }

    private static boolean slots(SwapEntry entry, IRecipe recipe, NonNullList<Ingredient> list, Map<NonNullList<Ingredient>, List<ResourceLocation>> holders) {
        List<ResourceLocation> held = holders.get(list);
        if (held == null) { return false; }
        for (ResourceLocation other : held) {
            if (!entry.inScope(other)) { return false; }
        }
        Map<Ingredient, Ingredient> fresh = new IdentityHashMap<>();
        for (Ingredient each : list) {
            if (!fresh.containsKey(each)) { fresh.put(each, replacement(entry, each)); }
        }
        Ingredient[] next = new Ingredient[list.size()];
        int changed = 0;
        for (int i = 0; i < next.length; i++) {
            Ingredient swapped = fresh.get(list.get(i));
            next[i] = swapped == null ? list.get(i) : swapped;
            if (swapped != null) { changed++; }
        }
        try {
            for (int i = 0; i < next.length; i++) {
                if (next[i] != list.get(i)) { list.set(i, next[i]); }
            }
        }
        catch (UnsupportedOperationException ex) { return false; }
        NonNullList<Ingredient> again = recipe.getIngredients();
        if (again.size() != next.length) { return false; }
        for (int i = 0; i < next.length; i++) {
            if (again.get(i) != next[i]) { return false; }
        }
        for (ResourceLocation other : held) {
            IRecipe sharer = ForgeRegistries.RECIPES.getValue(other);
            if (sharer == null) { continue; }
            simple(sharer, again);
            TOUCHED.add(sharer);
        }
        ingredients += changed;
        return true;
    }

    @Nullable private static Ingredient replacement(SwapEntry entry, Ingredient ingredient) {
        ItemStack[] members = ingredient.getMatchingStacks();
        List<ItemStack> kept = new ArrayList<>();
        boolean hit = false;
        for (ItemStack member : members) {
            if (entry.matches(member)) { hit = true; }
            else { include(kept, member.copy()); }
        }
        if (!hit) { return null; }
        include(kept, entry.result(1));
        return Ingredient.fromStacks(kept.toArray(new ItemStack[0]));
    }

    private static void include(List<ItemStack> stacks, ItemStack stack) {
        for (ItemStack held : stacks) {
            if (ItemStack.areItemsEqual(held, stack)) { return; }
        }
        stacks.add(stack);
    }

    private static void simple(IRecipe recipe, NonNullList<Ingredient> list) {
        boolean simple = true;
        for (Ingredient ingredient : list) { simple &= ingredient.isSimple(); }
        if (recipe instanceof ShapelessRecipes) { ((ShapelessRecipes) recipe).isSimple = simple; }
        else if (recipe instanceof ShapelessOreRecipe) { ((ShapelessOreRecipe) recipe).isSimple = simple; }
    }

    private static boolean result(SwapEntry entry, IRecipe recipe) {
        ItemStack made = entry.result(recipe.getRecipeOutput().getCount());
        if (recipe instanceof ShapedRecipes) { ((ShapedRecipes) recipe).recipeOutput = made; }
        else if (recipe instanceof ShapelessRecipes) { ((ShapelessRecipes) recipe).recipeOutput = made; }
        else if (recipe instanceof ShapedOreRecipe) { ((ShapedOreRecipe) recipe).output = made; }
        else if (recipe instanceof ShapelessOreRecipe) { ((ShapelessOreRecipe) recipe).output = made; }
        else { return false; }
        results++;
        TOUCHED.add(recipe);
        return true;
    }
}
