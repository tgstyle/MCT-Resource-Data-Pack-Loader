package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public final class RecipeRemainders {
    private static final String REMAINDER = "remainder";
    private static final String RECIPES = "recipes";
    private static final List<String> REMAINDER_KEYS = List.of("ingredient", "returns");
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final Map<ResourceLocation, List<Remainder>> BY_RECIPE = new HashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private RecipeRemainders() {}

    public static void reload() {
        if (!GENERATION.stale()) { return; }
        ENTRIES.clear();
        if (!Config.recipes.swaps()) { return; }
        Json.eachObject(PackManager.RECIPE_SWAPS, "recipe remainder file", RecipeRemainders::read);
    }

    public static void begin() { BY_RECIPE.clear(); }

    public static boolean deferred() {
        for (Entry entry : ENTRIES) {
            if (entry.scope.deferred()) { return true; }
        }
        return false;
    }

    public static boolean noneMarked() { return BY_RECIPE.isEmpty(); }

    public static void report() { if (!BY_RECIPE.isEmpty()) { Summary.info("recipes.remainders", "Gave " + BY_RECIPE.size() + " crafting recipe(s) their own remainders"); } }

    public static void mark(Recipe<?> recipe, ItemStack result) {
        if (ENTRIES.isEmpty() || recipe.getType() != RecipeType.CRAFTING) { return; }
        List<Remainder> wanted = new ArrayList<>();
        for (Entry entry : ENTRIES) {
            if (entry.scope.matchesName(recipe.getId()) || entry.scope.matchesOutput(result) || entry.scope.matchesType(recipe) || entry.scope.matchesInput(recipe)) { wanted.addAll(entry.remainders); }
        }
        if (!wanted.isEmpty()) { BY_RECIPE.put(recipe.getId(), wanted); }
    }

    public static void leaveReturns(ResourceLocation id, CraftingContainer container, NonNullList<ItemStack> left) {
        List<Remainder> remainders = BY_RECIPE.get(id);
        if (remainders == null) { return; }
        for (int slot = 0; slot < left.size(); slot++) {
            ItemStack used = container.getItem(slot);
            if (used.isEmpty()) { continue; }
            for (Remainder remainder : remainders) {
                if (remainder.matches(used)) {
                    left.set(slot, remainder.returns().copy());
                    break;
                }
            }
        }
    }

    private static void read(ResourceLocation key, JsonObject json) {
        if (!json.has(REMAINDER)) { return; }
        if (!json.has("swaps") && !json.has("transfer")) { Json.unknownKeys(json, "the top level", SwapEntry.FILE_KEYS); }
        if (!json.has(RECIPES) || !json.get(RECIPES).isJsonObject()) {
            ContentLog.LOGGER.error("Recipe remainder file {} needs a recipes object saying which recipes it covers, ignoring it", key);
            return;
        }
        List<Remainder> remainders = new ArrayList<>();
        for (JsonElement element : Json.getJsonArray(json, REMAINDER)) {
            Remainder remainder = element.isJsonObject() ? remainder(key, element.getAsJsonObject()) : null;
            if (remainder == null) { ContentLog.LOGGER.error("A remainder in {} needs an ingredient and a returns item, skipping it", key); }
            else { remainders.add(remainder); }
        }
        if (remainders.isEmpty()) { return; }
        RecipeSelector scope = new RecipeSelector();
        Json.unknownKeys(json.getAsJsonObject(RECIPES), RECIPES, RecipeSelector.KEYS);
        scope.read(key, json.getAsJsonObject(RECIPES));
        if (scope.isEmpty()) {
            ContentLog.LOGGER.error("Recipe remainder file {} selects no recipes, ignoring it", key);
            return;
        }
        ENTRIES.add(new Entry(scope, remainders));
    }

    @Nullable private static Remainder remainder(ResourceLocation key, JsonObject json) {
        Json.unknownKeys(json, REMAINDER, REMAINDER_KEYS);
        String ingredient = Json.getString(json, "ingredient", "");
        ItemStack returns = ContentStacks.parse(key, Json.getString(json, "returns", ""), 1);
        if (ingredient.isEmpty() || returns.isEmpty()) { return null; }
        if (!ingredient.startsWith("#")) {
            Item item = ContentStacks.find(key, ingredient);
            return item == null ? null : new Remainder(item, null, returns);
        }
        ResourceLocation tag = Registered.tagId(ingredient);
        return tag == null ? null : new Remainder(null, TagKey.create(Registries.ITEM, tag), returns);
    }

    private record Entry(RecipeSelector scope, List<Remainder> remainders) {}

    private record Remainder(@Nullable Item item, @Nullable TagKey<Item> tag, ItemStack returns) {
        boolean matches(ItemStack used) { return item != null ? used.is(item) : tag != null && used.is(tag); }
    }
}
