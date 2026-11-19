package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class RecipeRemainders {
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Map<Identifier, List<Remainder>> BY_RECIPE = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private RecipeRemainders() {}

    public static void reload() {
        if (!GENERATION.stale()) { return; }
        ENTRIES.clear();
        if (!Config.recipes.swaps()) { return; }
        Json.eachObject(PackManager.RECIPE_SWAPS, "recipe remainder file", RecipeRemainders::read);
    }

    public static void begin() { BY_RECIPE.clear(); }

    public static boolean noneMarked() { return BY_RECIPE.isEmpty(); }

    public static void mark(RecipeHolder<?> holder, ItemStack result) {
        if (ENTRIES.isEmpty() || !(holder.value() instanceof CraftingRecipe recipe)) { return; }
        Identifier id = holder.id().identifier();
        List<Remainder> wanted = new ArrayList<>();
        for (Entry entry : ENTRIES) {
            if (entry.scope.matches(id, recipe, result)) { wanted.addAll(entry.remainders); }
        }
        if (!wanted.isEmpty()) { BY_RECIPE.put(id, wanted); }
    }

    public static void leaveReturns(Identifier id, CraftingInput input, NonNullList<ItemStack> left) {
        List<Remainder> remainders = BY_RECIPE.get(id);
        if (remainders == null) { return; }
        for (int slot = 0; slot < left.size(); slot++) {
            ItemStack used = input.getItem(slot);
            if (used.isEmpty()) { continue; }
            for (Remainder remainder : remainders) {
                if (remainder.ingredient().matches(used)) {
                    left.set(slot, remainder.returns().create());
                    break;
                }
            }
        }
    }

    public static void report() {
        if (!BY_RECIPE.isEmpty()) { Summary.info("recipes.remainders", "Gave " + BY_RECIPE.size() + " crafting recipe(s) their own remainders"); }
    }

    private static void read(Identifier key, JsonObject json) {
        if (!json.has("remainder")) { return; }
        if (!json.has("recipes") || !json.get("recipes").isJsonObject()) {
            ContentLog.LOGGER.error("Recipe remainder file {} needs a recipes object saying which recipes it covers, ignoring it", key);
            return;
        }
        List<Remainder> remainders = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(json, "remainder")) {
            Remainder remainder = element.isJsonObject() ? remainder(key, element.getAsJsonObject()) : null;
            if (remainder == null) { ContentLog.LOGGER.error("A remainder in {} needs an ingredient and a returns item, skipping it", key); }
            else { remainders.add(remainder); }
        }
        if (remainders.isEmpty()) { return; }
        RecipeSelector scope = new RecipeSelector();
        scope.read(key, json.getAsJsonObject("recipes"));
        if (scope.isEmpty()) {
            ContentLog.LOGGER.error("Recipe remainder file {} selects no recipes, ignoring it", key);
            return;
        }
        ENTRIES.add(new Entry(scope, remainders));
    }

    @Nullable private static Remainder remainder(Identifier key, JsonObject json) {
        String ingredient = Json.getString(json, "ingredient", "");
        ItemStackTemplate returns = ContentStacks.template(key, Json.getString(json, "returns", ""), 1);
        if (ingredient.isEmpty() || returns == null) { return null; }
        ItemSelection selection = selection("Recipe remainder", key, ingredient);
        return selection.unnamed() ? null : new Remainder(selection, returns);
    }

    static ItemSelection selection(String kind, Identifier key, String text) {
        ItemSelection selection = new ItemSelection(kind, "matches");
        selection.readNames(List.of(text), key);
        selection.unknown();
        return selection;
    }

    private record Entry(RecipeSelector scope, List<Remainder> remainders) {}

    private record Remainder(ItemSelection ingredient, ItemStackTemplate returns) {}
}
