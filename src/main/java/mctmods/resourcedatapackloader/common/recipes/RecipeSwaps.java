package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public final class RecipeSwaps {
    private static final String ITEM_TAGS = Registries.tagsDirPath(Registries.ITEM);
    private static final String TYPE = "type";
    private static final Map<Identifier, Identifier> ORIGINALS = new ConcurrentHashMap<>();
    private static final Map<String, Map<Identifier, Set<Identifier>>> INPUTS = new HashMap<>();
    private static final Set<Identifier> TOUCHED = new HashSet<>();
    private static final Set<Identifier> LEFT = new HashSet<>();
    private static final Set<String> LEFT_TYPES = new HashSet<>();
    @Nullable private static volatile Map<Identifier, List<Identifier>> tagStore;
    private static int ingredients;
    private static int results;
    private static boolean noted;
    private static boolean indexed;

    private RecipeSwaps() {}

    public static void reload() {
        ORIGINALS.clear();
        INPUTS.clear();
        TOUCHED.clear();
        LEFT.clear();
        LEFT_TYPES.clear();
        ingredients = 0;
        results = 0;
        indexed = false;
        SwapEntry.all();
    }

    public static <T> void tags(String directory, Map<Identifier, List<T>> built) {
        if (!ITEM_TAGS.equals(directory)) { return; }
        if (!Config.recipes.swaps()) {
            tagStore = null;
            return;
        }
        Map<Identifier, List<Identifier>> store = new HashMap<>();
        for (Map.Entry<Identifier, List<T>> entry : built.entrySet()) {
            List<Identifier> ids = new ArrayList<>();
            for (T value : entry.getValue()) {
                Identifier id = ItemSelection.id(value);
                if (id != null) { ids.add(id); }
            }
            store.put(entry.getKey(), List.copyOf(ids));
        }
        tagStore = store;
    }

    @Nullable public static Identifier originalResult(Identifier id) { return ORIGINALS.get(id); }

    public static void apply(Map<Identifier, JsonElement> recipes, Function<JsonElement, Optional<Recipe<?>>> parse) {
        List<SwapEntry> entries = SwapEntry.all();
        if (entries.isEmpty()) { return; }
        Map<Identifier, List<Identifier>> tags = tagStore;
        for (SwapEntry entry : entries) {
            if (tags == null && entry.needsTags()) {
                ContentLog.LOGGER.error("A recipe swap names item tags but the built item tags were not seen for this reload, skipping it");
                continue;
            }
            Map<Identifier, List<Identifier>> known = tags == null ? Map.of() : tags;
            Set<Identifier> from = entry.members(known);
            if (from.contains(entry.to())) {
                ContentLog.LOGGER.error("The to item {} of a recipe swap is matched by its own from, skipping it", entry.to());
                continue;
            }
            if (from.isEmpty()) { continue; }
            if (!indexed) { index(recipes, known); }
            for (Map.Entry<Identifier, JsonElement> each : recipes.entrySet()) { swap(entry, from, each, parse, known); }
        }
    }

    public static void report() {
        if (SwapEntry.all().isEmpty()) { return; }
        Summary.info("recipes.swapped", "Replaced " + ingredients + " ingredient(s) and " + results + " result(s) in " + TOUCHED.size() + " recipe(s)");
        if (!LEFT.isEmpty()) { Summary.info("recipes.swapped.left", "Left " + LEFT.size() + " recipe(s) alone, they name a replaced item in a form RDPL cannot rewrite"); }
    }

    public static void scripted() {
        if (noted || SwapEntry.all().isEmpty()) { return; }
        noted = true;
        ContentLog.LOGGER.info("The recipe swap ran on the recipe JSON only, recipes made by a script are not swapped");
    }

    private static void index(Map<Identifier, JsonElement> recipes, Map<Identifier, List<Identifier>> tags) {
        indexed = true;
        for (Map.Entry<Identifier, JsonElement> each : recipes.entrySet()) {
            if (!each.getValue().isJsonObject()) { continue; }
            index(each.getValue().getAsJsonObject(), each.getKey(), tags);
        }
    }

    private static void index(JsonObject json, Identifier id, Map<Identifier, List<Identifier>> tags) {
        for (Identifier item : RecipeSwapJson.inputs(json, tags)) { input(type(json), item, id); }
    }

    private static String type(JsonObject json) { return json.has(TYPE) && json.get(TYPE).isJsonPrimitive() ? json.get(TYPE).getAsString() : ""; }

    private static void swap(SwapEntry entry, Set<Identifier> from, Map.Entry<Identifier, JsonElement> each, Function<JsonElement, Optional<Recipe<?>>> parse, Map<Identifier, List<Identifier>> tags) {
        Identifier id = each.getKey();
        if (id.getPath().startsWith("_") || !each.getValue().isJsonObject() || !entry.inScope(id)) { return; }
        JsonObject copy = each.getValue().deepCopy().getAsJsonObject();
        RecipeSwapJson.Outcome found = RecipeSwapJson.rewrite(copy, from, entry.to(), entry.output(), tags);
        if (!found.changed() && !found.typed) { return; }
        Optional<Recipe<?>> held = parsed(id, copy, parse);
        if (held.isEmpty()) {
            if (found.changed() && accepted(id, each.getValue().getAsJsonObject(), parse, entry)) {
                ContentLog.LOGGER.error("Left {} alone, its swapped form does not parse", id);
                left(id, type(copy));
            }
            return;
        }
        Recipe<?> parsed = held.get();
        boolean cooking = parsed instanceof AbstractCookingRecipe;
        if (!inScope(parsed, entry)) {
            ContentLog.LOGGER.debug("Recipe {} is of type {}, which a recipe swap does not touch", id, type(copy));
            return;
        }
        if (found.typed) { left(id, type(copy)); }
        if (!found.changed()) { return; }
        if (cooking && found.slots > 0 && collides(type(copy), entry.to(), id)) {
            ContentLog.LOGGER.info("Did not swap the ingredient of {}: {} already smelts in another {} recipe", id, entry.to(), type(copy));
            return;
        }
        each.setValue(copy);
        TOUCHED.add(id);
        ingredients += found.slots;
        if (found.made != null) {
            results++;
            ORIGINALS.putIfAbsent(id, found.made);
        }
        if (cooking && found.slots > 0) {
            unindex(type(copy), id);
            index(copy, id, tags);
        }
    }

    private static boolean accepted(Identifier id, JsonObject json, Function<JsonElement, Optional<Recipe<?>>> parse, SwapEntry entry) {
        Optional<Recipe<?>> kept = parsed(id, json, parse);
        return kept.isPresent() && inScope(kept.get(), entry);
    }

    private static boolean inScope(Recipe<?> recipe, SwapEntry entry) {
        boolean cooking = recipe instanceof AbstractCookingRecipe;
        return recipe.getType() == RecipeType.CRAFTING || cooking && !entry.scoped();
    }

    private static void unindex(String type, Identifier id) {
        Map<Identifier, Set<Identifier>> inputs = INPUTS.get(type);
        if (inputs == null) { return; }
        for (Set<Identifier> holders : inputs.values()) { holders.remove(id); }
    }

    private static void input(String type, Identifier item, Identifier id) { INPUTS.computeIfAbsent(type, _ -> new HashMap<>()).computeIfAbsent(item, _ -> new HashSet<>()).add(id); }

    private static Optional<Recipe<?>> parsed(Identifier id, JsonObject copy, Function<JsonElement, Optional<Recipe<?>>> parse) {
        try {
            Optional<Recipe<?>> recipe = parse.apply(copy);
            if (recipe.isEmpty()) { ContentLog.LOGGER.debug("Recipe {} gave no recipe during the swap pass, conditions dropped it", id); }
            return recipe;
        }
        catch (IllegalArgumentException | JsonParseException ex) {
            ContentLog.LOGGER.debug("Recipe {} did not parse during the swap pass: {}", id, ex.getMessage());
            return Optional.empty();
        }
    }

    private static boolean collides(String type, Identifier to, Identifier id) {
        Map<Identifier, Set<Identifier>> inputs = INPUTS.get(type);
        Set<Identifier> held = inputs == null ? null : inputs.get(to);
        if (held == null) { return false; }
        for (Identifier other : held) {
            if (!other.equals(id)) { return true; }
        }
        return false;
    }

    private static void left(Identifier id, String type) {
        if (!LEFT.add(id) || !LEFT_TYPES.add(type)) { return; }
        ContentLog.LOGGER.info("Left {} alone, a recipe of type {} names a replaced item in a form RDPL cannot rewrite", id, type);
    }
}
