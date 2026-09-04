package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.recipe.interfaces.IRecipeFilter;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;

public final class RecipeLoading {
    private static final String ITEM = "item";
    private static final String CONDITIONS = "conditions";
    private static final String NEOFORGE_CONDITIONS = "neoforge:conditions";
    private static final Map<String, Boolean> REGISTERED = new HashMap<>();
    private static int removedByFile;
    private static int removedByName;
    private static int removedByOutput;
    private static int skipped;
    private static int furnaceRemoved;
    private static int furnaceAdded;
    @Nullable private static IRecipeFilter attached;

    private RecipeLoading() {}

    public static void begin(Map<ResourceLocation, JsonElement> recipes) {
        RecipeRemovals.reload();
        FurnaceRecipes.reload();
        RecipeBlocking.reload();
        FurnaceBlocking.reload();
        REGISTERED.clear();
        removedByFile = 0;
        removedByName = 0;
        removedByOutput = 0;
        skipped = 0;
        furnaceRemoved = 0;
        furnaceAdded = 0;
        boolean skipMissing = Config.recipes.skipMissingItems();
        Iterator<Map.Entry<ResourceLocation, JsonElement>> iterator = recipes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, JsonElement> entry = iterator.next();
            ResourceLocation id = entry.getKey();
            if (id.getPath().startsWith("_")) { continue; }
            if (RecipeRemovals.isRemoval(entry.getValue())) {
                iterator.remove();
                removedByFile++;
                continue;
            }
            if (RecipeRemovals.removesName(id)) {
                iterator.remove();
                removedByName++;
                continue;
            }
            if (!skipMissing) { continue; }
            String missing = missingItem(entry.getValue(), id.getNamespace());
            if (missing == null) { continue; }
            ContentLog.LOGGER.debug("Skipping recipe {}, it uses '{}' which is not registered", id, missing);
            iterator.remove();
            skipped++;
        }
    }

    public static boolean doomed(ResourceLocation id, Recipe<?> recipe, ItemStack result) {
        if (RecipeRemovals.removesOutput(result)) {
            removedByOutput++;
            return true;
        }
        if (recipe instanceof AbstractCookingRecipe cooking) {
            if (FurnaceRecipes.removes(cooking.getIngredients(), result, false)) {
                furnaceRemoved++;
                return true;
            }
            return FurnaceBlocking.blocks(result);
        }
        return RecipeBlocking.blocks(id, result);
    }

    public static boolean late(Recipe<?> recipe, ItemStack result) {
        if (!(recipe instanceof AbstractCookingRecipe cooking) || !FurnaceRecipes.removes(cooking.getIngredients(), result, true)) { return false; }
        furnaceRemoved++;
        return true;
    }

    public static void attach(IRecipeFilter filter) { attached = filter; }

    public static void onTagsBound() {
        IRecipeFilter filter = attached;
        if (filter == null || FurnaceRecipes.resolvedAtLoad()) { return; }
        filter.rdpl$filterLate();
        FurnaceRecipes.report(furnaceRemoved, furnaceAdded);
    }

    public static void finish(int added) {
        furnaceAdded = added;
        REGISTERED.clear();
        if (skipped > 0) { Summary.info("recipes.skipped", "Skipped " + skipped + " recipe(s) that use items which are not registered, usually content a mod's config has disabled"); }
        int removed = removedByFile + removedByName + removedByOutput;
        if (removed > 0) { Summary.info("recipes.removed", "Removed " + removed + " recipe(s): " + removedByFile + " by a remove file, " + removedByName + " by name, " + removedByOutput + " by output"); }
        RecipeBlocking.report();
        FurnaceBlocking.report();
        if (FurnaceRecipes.resolvedAtLoad()) { FurnaceRecipes.report(furnaceRemoved, furnaceAdded); }
    }

    @Nullable private static String missingItem(JsonElement element, String namespace) {
        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                String missing = missingItem(child, namespace);
                if (missing != null) { return missing; }
            }
            return null;
        }
        if (!element.isJsonObject()) { return null; }
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            if (CONDITIONS.equals(entry.getKey()) || NEOFORGE_CONDITIONS.equals(entry.getKey())) { continue; }
            JsonElement value = entry.getValue();
            if (ITEM.equals(entry.getKey()) && value.isJsonPrimitive()) {
                String name = qualify(value.getAsString(), namespace);
                if (name != null && !registered(name)) { return name; }
                continue;
            }
            String missing = missingItem(value, namespace);
            if (missing != null) { return missing; }
        }
        return null;
    }

    @Nullable private static String qualify(String name, String namespace) {
        if (name.isEmpty() || name.charAt(0) == '#') { return null; }
        return name.indexOf(':') < 0 ? namespace + ":" + name : name;
    }

    private static boolean registered(String name) {
        Boolean known = REGISTERED.get(name);
        if (known != null) { return known; }
        ResourceLocation location = ResourceLocation.tryParse(name);
        boolean present = location == null || ContentStacks.registered(location);
        REGISTERED.put(name, present);
        return present;
    }
}
