package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public final class RecipeSwaps {
    private static final String ITEM_TAGS = Registries.tagsDirPath(Registries.ITEM);
    private static final String TYPE = "type";
    private static final Map<ResourceLocation, ResourceLocation> ORIGINALS = new HashMap<>();
    private static final Set<ResourceLocation> TOUCHED = new HashSet<>();
    private static final Set<ResourceLocation> LEFT = new HashSet<>();
    private static final Set<String> LEFT_TYPES = new HashSet<>();
    private static final Map<String, Map<ResourceLocation, Set<ResourceLocation>>> INPUTS = new HashMap<>();
    @Nullable private static volatile Map<ResourceLocation, Set<ResourceLocation>> built;
    private static Map<ResourceLocation, Set<ResourceLocation>> active = Map.of();
    private static int ingredients;
    private static int results;

    private RecipeSwaps() {}

    public static void reload() {
        ORIGINALS.clear();
        TOUCHED.clear();
        LEFT.clear();
        LEFT_TYPES.clear();
        INPUTS.clear();
        ingredients = 0;
        results = 0;
    }

    public static <T> void tags(String directory, Map<ResourceLocation, Collection<T>> tags) {
        if (!ITEM_TAGS.equals(directory)) { return; }
        if (SwapEntry.all().isEmpty()) {
            built = null;
            return;
        }
        Map<ResourceLocation, Set<ResourceLocation>> members = new HashMap<>();
        for (Map.Entry<ResourceLocation, Collection<T>> tag : tags.entrySet()) {
            Set<ResourceLocation> ids = new LinkedHashSet<>();
            for (T value : tag.getValue()) {
                ResourceLocation id = ItemSelection.id(value);
                if (id != null) { ids.add(id); }
            }
            members.put(tag.getKey(), ids);
        }
        built = members;
        ContentLog.LOGGER.debug("Stored {} item tag(s) for recipe swaps", members.size());
    }

    @Nullable public static ResourceLocation originalResult(ResourceLocation id) { return ORIGINALS.get(id); }

    public static void scriptedNote() {
        if (SwapEntry.all().isEmpty()) { return; }
        ContentLog.LOGGER.info("Recipe swaps ran on the recipe files only, recipes made by a script are not swapped");
    }

    public static void apply(Map<ResourceLocation, JsonElement> recipes, BiFunction<ResourceLocation, JsonElement, Recipe<?>> parse) {
        List<SwapEntry> entries = SwapEntry.all();
        if (entries.isEmpty()) { return; }
        Map<ResourceLocation, Set<ResourceLocation>> stored = built;
        active = stored == null ? Map.of() : stored;
        List<Swap> swaps = resolved(entries, stored != null);
        for (Map.Entry<ResourceLocation, JsonElement> each : recipes.entrySet()) {
            if (each.getValue().isJsonObject()) { index(each.getValue().getAsJsonObject(), each.getKey(), active); }
        }
        for (Map.Entry<ResourceLocation, JsonElement> each : recipes.entrySet()) {
            ResourceLocation id = each.getKey();
            if (id.getPath().startsWith("_") || !each.getValue().isJsonObject()) { continue; }
            JsonObject current = each.getValue().getAsJsonObject();
            for (Swap swap : swaps) {
                if (!swap.entry().inScope(id)) { continue; }
                JsonObject next = swap(swap, id, current, parse);
                if (next != null) { current = next; }
            }
            each.setValue(current);
        }
        summarize();
    }

    private static List<Swap> resolved(List<SwapEntry> entries, boolean tagged) {
        List<Swap> swaps = new ArrayList<>();
        for (SwapEntry entry : entries) {
            Set<ResourceLocation> matched = new LinkedHashSet<>(entry.items());
            if (!entry.tags().isEmpty() && !tagged) {
                ContentLog.LOGGER.error("A recipe swap to {} names item tags, but the item tags were not built for this reload, skipping it", entry.to());
                continue;
            }
            for (ResourceLocation tag : entry.tags()) {
                Set<ResourceLocation> held = active.get(tag);
                if (held == null) { ContentLog.LOGGER.debug("Recipe swap tag {} is no item tag", tag); }
                else { matched.addAll(held); }
            }
            if (matched.contains(entry.to())) {
                ContentLog.LOGGER.error("The to item {} of a recipe swap is matched by its own from tags, skipping it", entry.to());
                continue;
            }
            if (!matched.isEmpty()) { swaps.add(new Swap(entry, new RecipeSwapJson(matched, entry.to(), entry.output(), active))); }
        }
        return swaps;
    }

    @Nullable private static JsonObject swap(Swap swap, ResourceLocation id, JsonObject current, BiFunction<ResourceLocation, JsonElement, Recipe<?>> parse) {
        SwapEntry entry = swap.entry();
        RecipeSwapJson walker = swap.walker();
        JsonObject copy = walker.walk(current);
        if (copy == null) {
            if (walker.left() && accepted(id, current, parse, entry)) { markLeft(id, current); }
            return null;
        }
        Recipe<?> made = parsed(id, copy, parse);
        if (made == null) {
            if (accepted(id, current, parse, entry)) {
                ContentLog.LOGGER.error("Left {} alone, its swapped form does not parse", id);
                markLeft(id, current);
            }
            return null;
        }
        if (!inScope(made, entry)) {
            ContentLog.LOGGER.debug("Recipe {} is not a crafting or cooking recipe this swap touches, leaving it", id);
            return null;
        }
        String type = type(current);
        boolean smelting = made instanceof AbstractCookingRecipe && walker.ingredients() > 0;
        if (smelting && collides(type, id, entry.to())) {
            ContentLog.LOGGER.info("Did not swap the ingredient of {}: {} already smelts in another {} recipe", id, entry.to(), type);
            return null;
        }
        if (smelting) {
            unindex(type, id);
            index(copy, id, active);
        }
        ingredients += walker.ingredients();
        results += walker.results();
        TOUCHED.add(id);
        ResourceLocation replaced = walker.replaced();
        if (replaced != null) { ORIGINALS.putIfAbsent(id, replaced); }
        if (walker.left()) { markLeft(id, current); }
        return copy;
    }

    private static boolean accepted(ResourceLocation id, JsonObject json, BiFunction<ResourceLocation, JsonElement, Recipe<?>> parse, SwapEntry entry) {
        Recipe<?> kept = parsed(id, json, parse);
        return kept != null && inScope(kept, entry);
    }

    private static boolean inScope(Recipe<?> recipe, SwapEntry entry) {
        boolean cooking = recipe instanceof AbstractCookingRecipe;
        return recipe.getType() == RecipeType.CRAFTING || cooking && !entry.scoped();
    }

    @Nullable private static Recipe<?> parsed(ResourceLocation id, JsonObject json, BiFunction<ResourceLocation, JsonElement, Recipe<?>> parse) {
        try { return parse.apply(id, json); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.debug("Recipe {} did not parse during the swap pass: {}", id, ex.getMessage());
            return null;
        }
    }

    private static void markLeft(ResourceLocation id, JsonObject json) {
        if (!LEFT.add(id)) { return; }
        String type = type(json);
        if (LEFT_TYPES.add(type)) { ContentLog.LOGGER.info("Left {} alone, a recipe of type {} names a replaced item where RDPL cannot rewrite it", id, type); }
    }

    private static String type(JsonObject json) { return json.has(TYPE) && json.get(TYPE).isJsonPrimitive() ? json.get(TYPE).getAsString() : ""; }

    private static void index(JsonObject json, ResourceLocation id, Map<ResourceLocation, Set<ResourceLocation>> tags) {
        Map<ResourceLocation, Set<ResourceLocation>> byItem = null;
        for (ResourceLocation item : RecipeSwapJson.inputs(json, tags)) {
            if (byItem == null) { byItem = INPUTS.computeIfAbsent(type(json), k -> new HashMap<>()); }
            byItem.computeIfAbsent(item, k -> new HashSet<>()).add(id);
        }
    }

    private static void unindex(String type, ResourceLocation id) {
        Map<ResourceLocation, Set<ResourceLocation>> byItem = INPUTS.get(type);
        if (byItem == null) { return; }
        for (Set<ResourceLocation> holders : byItem.values()) { holders.remove(id); }
    }

    private static boolean collides(String type, ResourceLocation id, ResourceLocation to) {
        Map<ResourceLocation, Set<ResourceLocation>> byItem = INPUTS.get(type);
        Set<ResourceLocation> holders = byItem == null ? null : byItem.get(to);
        if (holders == null) { return false; }
        for (ResourceLocation holder : holders) {
            if (!holder.equals(id)) { return true; }
        }
        return false;
    }

    private static void summarize() {
        Summary.info("recipes.swapped", "Replaced " + ingredients + " ingredient(s) and " + results + " result(s) in " + TOUCHED.size() + " recipe(s)");
        if (!LEFT.isEmpty()) { Summary.info("recipes.swapped.left", "Left " + LEFT.size() + " recipe(s) alone, they name a replaced item in a form RDPL cannot rewrite"); }
    }

    private record Swap(SwapEntry entry, RecipeSwapJson walker) {}
}
