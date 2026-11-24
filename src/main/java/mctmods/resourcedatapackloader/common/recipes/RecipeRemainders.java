package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public final class RecipeRemainders {
    private static final List<String> REMAINDER_KEYS = Collections.unmodifiableList(Arrays.asList("ingredient", "returns"));
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Map<ResourceLocation, List<Remainder>> BY_RECIPE = new HashMap<>();

    public static final String KEY = "remainder";
    private static final String KIND = "Recipe remainder";

    private RecipeRemainders() {}

    public static void apply(IForgeRegistry<IRecipe> registry) {
        ENTRIES.clear();
        BY_RECIPE.clear();
        if (!Config.recipes.swaps) { return; }
        Json.eachObject(PackManager.RECIPE_SWAPS, "recipe remainder file", RecipeRemainders::read);
        if (ENTRIES.isEmpty()) { return; }
        RecipeSelector.byRecipe(registry, ENTRIES, entry -> entry.scope, (list, entry) -> list.addAll(entry.remainders), BY_RECIPE);
        if (!BY_RECIPE.isEmpty()) { Summary.info("recipes.remainders", "Gave " + BY_RECIPE.size() + " crafting recipe(s) their own remainders"); }
    }

    public static void leaveReturns(IRecipe recipe, InventoryCrafting inv, NonNullList<ItemStack> left) {
        List<Remainder> remainders = BY_RECIPE.get(recipe.getRegistryName());
        if (remainders == null) { return; }
        for (int slot = 0; slot < left.size(); slot++) {
            ItemStack used = inv.getStackInSlot(slot);
            if (used.isEmpty()) { continue; }
            for (Remainder remainder : remainders) {
                if (remainder.ingredient.matches(used)) {
                    left.set(slot, remainder.returns.copy());
                    break;
                }
            }
        }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!json.has(KEY)) { return; }
        if (!json.has("swaps") && !json.has("transfer")) { Json.unknownKeys(json, "the top level", SwapEntry.FILE_KEYS); }
        if (RecipeSelector.lacksRecipes(KIND, key, json)) { return; }
        List<Remainder> remainders = new ArrayList<>();
        JsonArray array = Json.getJsonArray(json, KEY);
        for (JsonElement element : array) {
            Remainder remainder = element.isJsonObject() ? remainder(key, element.getAsJsonObject()) : null;
            if (remainder == null) { ContentLog.LOGGER.error("A remainder in {} needs an ingredient and a returns item, skipping it", key); }
            else { remainders.add(remainder); }
        }
        if (remainders.isEmpty()) { return; }
        Json.unknownKeys(json.getAsJsonObject("recipes"), "recipes", RecipeSelector.KEYS);
        RecipeSelector scope = RecipeSelector.scopeOf(KIND, key, json.getAsJsonObject("recipes"));
        if (scope != null) { ENTRIES.add(new Entry(scope, remainders)); }
    }

    @Nullable private static Remainder remainder(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, "remainder", REMAINDER_KEYS);
        String ingredient = Json.getString(json, "ingredient", "");
        ItemStack returns = ContentStacks.parse(key, Json.getString(json, "returns", ""), 1);
        if (ingredient.isEmpty() || returns.isEmpty()) { return null; }
        ItemSelection selection = ItemSelection.fromText(KIND, key, Collections.singletonList(ingredient));
        return selection.any() ? new Remainder(selection, returns) : null;
    }

    private static final class Entry {
        final RecipeSelector scope;
        final List<Remainder> remainders;

        Entry(RecipeSelector scope, List<Remainder> remainders) {
            this.scope = scope;
            this.remainders = remainders;
        }
    }

    private static final class Remainder {
        final ItemSelection ingredient;
        final ItemStack returns;

        Remainder(ItemSelection ingredient, ItemStack returns) {
            this.ingredient = ingredient;
            this.returns = returns;
        }
    }
}
