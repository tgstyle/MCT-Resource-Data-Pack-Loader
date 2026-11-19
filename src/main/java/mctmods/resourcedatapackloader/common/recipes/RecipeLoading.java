package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.blocks.compress.CompressedRecipes;
import mctmods.resourcedatapackloader.common.pack.io.RDPLResourcePack;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ContentDisabled;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

public final class RecipeLoading {
    private static final String ITEM = "item";
    private static final String CONDITIONS = "conditions";
    private static final String NEOFORGE_CONDITIONS = "neoforge:conditions";
    private static final FileToIdConverter FILES = FileToIdConverter.json("recipes");
    private static final Map<String, Boolean> REGISTERED = new HashMap<>();
    private static int removedByFile;
    private static int removedByName;
    private static int removedByOutput;
    private static int removedByType;
    private static int removedByTags;
    private static int removedDisabled;
    private static int skipped;
    @Nullable private static IRecipeFilter attached;
    private static final Set<String> SCRIPTED = Set.of("crafttweaker", "kubejs", "groovyscript");
    private static final Set<ResourceLocation> LOADED = new HashSet<>();
    private static boolean rebuilt;

    private RecipeLoading() {}

    public static void begin(Map<ResourceLocation, JsonElement> recipes, ResourceManager manager, Predicate<JsonElement> conditions, BiFunction<ResourceLocation, JsonElement, Recipe<?>> parse) {
        rebuilt = false;
        RecipeRemovals.reload();
        RecipeSwaps.reload();
        RecipeLocks.reload();
        RecipeRemainders.reload();
        RecipeRewrites.reload();
        FurnaceRecipes.reload();
        FurnaceRecipes.begin();
        RecipeBlocking.reload();
        FurnaceBlocking.reload();
        REGISTERED.clear();
        removedByFile = 0;
        removedByName = 0;
        removedByOutput = 0;
        removedByType = 0;
        removedByTags = 0;
        removedDisabled = 0;
        skipped = 0;
        keepOriginals(recipes, manager, conditions, parse);
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
            if (!skipMissing) { continue; }
            String missing = missingItem(entry.getValue(), id.getNamespace());
            if (missing == null) { continue; }
            ContentLog.LOGGER.debug("Skipping recipe {}, it uses '{}' which is not registered", id, missing);
            iterator.remove();
            skipped++;
        }
        LOADED.clear();
        LOADED.addAll(recipes.keySet());
        CompressedRecipes.register(recipes);
        RecipeRewrites.apply(recipes, parse, true);
        RecipeSwaps.apply(recipes, parse);
        RecipeRewrites.apply(recipes, parse, false);
    }

    @SuppressWarnings("resource") private static void keepOriginals(Map<ResourceLocation, JsonElement> recipes, ResourceManager manager, Predicate<JsonElement> conditions, BiFunction<ResourceLocation, JsonElement, Recipe<?>> parse) {
        for (Map.Entry<ResourceLocation, List<Resource>> stack : manager.listResourceStacks("recipes", path -> path.getPath().endsWith(PackManager.JSON_SUFFIX)).entrySet()) {
            List<Resource> layers = stack.getValue();
            if (layers.size() < 2 || !(layers.get(layers.size() - 1).source() instanceof RDPLResourcePack)) { continue; }
            ResourceLocation id = FILES.fileToId(stack.getKey());
            JsonElement held = recipes.get(id);
            if (id.getPath().startsWith("_") || held != null && RecipeRemovals.isRemoval(held)) { continue; }
            JsonElement original = original(layers);
            if (original != null && failed(id, held, conditions, parse)) { recipes.put(id, original); }
        }
    }

    private static boolean failed(ResourceLocation id, @Nullable JsonElement held, Predicate<JsonElement> conditions, BiFunction<ResourceLocation, JsonElement, Recipe<?>> parse) {
        if (held == null) {
            ContentLog.LOGGER.error("Parsing error in recipe {} while reading it, leaving the original in place: it is not valid JSON", id);
            return true;
        }
        if (!conditions.test(held)) {
            ContentLog.LOGGER.debug("Recipe {} was skipped by its own conditions, leaving the original in place", id);
            return true;
        }
        try { parse.apply(id, held); }
        catch (IllegalArgumentException | JsonParseException ex) {
            ContentLog.LOGGER.error("Parsing error in recipe {} while building it, leaving the original in place: {}", id, ex.getMessage());
            return true;
        }
        return false;
    }

    @SuppressWarnings("resource") @Nullable private static JsonElement original(List<Resource> layers) {
        for (int i = layers.size() - 2; i >= 0; i--) {
            Resource layer = layers.get(i);
            if (layer.source() instanceof RDPLResourcePack) { continue; }
            try (Reader reader = layer.openAsReader()) { return JsonParser.parseReader(reader); }
            catch (IOException | JsonParseException ex) { return null; }
        }
        return null;
    }

    public static boolean doomed(ResourceLocation id, Recipe<?> recipe, ItemStack result) {
        if (spared(id)) { return false; }
        if (RecipeRemovals.removesType(recipe)) {
            removedByType++;
            return true;
        }
        ItemStack judged = judged(id, result);
        if (recipe instanceof AbstractCookingRecipe) { return (FurnaceRecipes.resolvedAtLoad() && FurnaceRecipes.removes(recipe.getIngredients(), result)) || FurnaceBlocking.blocks(judged); }
        if (recipe.getType() != RecipeType.CRAFTING) { return false; }
        if (RecipeRemovals.removesName(id)) {
            removedByName++;
            return true;
        }
        if (RecipeRemovals.removesOutput(judged)) {
            removedByOutput++;
            return true;
        }
        return RecipeBlocking.blocks(id, judged);
    }

    private static ItemStack judged(ResourceLocation id, ItemStack result) {
        ResourceLocation was = RecipeSwaps.originalResult(id);
        Item item = was == null ? null : ContentStacks.item(was);
        return item == null ? result : new ItemStack(item, result.getCount());
    }

    public static void remaindered(ResourceLocation id, Recipe<?> recipe, ItemStack result) { RecipeRemainders.mark(recipe, judged(id, result)); }

    public static boolean late(ResourceLocation id, Recipe<?> recipe, ItemStack result) { return usesDisabled(recipe, result) || removedLate(id, recipe, result) || !FurnaceRecipes.resolvedAtLoad() && recipe instanceof AbstractCookingRecipe && FurnaceRecipes.removesLate(id, recipe.getIngredients(), result, spared(id)); }

    private static boolean usesDisabled(Recipe<?> recipe, ItemStack result) {
        if (!RecipeDisabled.uses(recipe, result)) { return false; }
        removedDisabled++;
        return true;
    }

    private static boolean removedLate(ResourceLocation id, Recipe<?> recipe, ItemStack result) {
        if (spared(id) || !RecipeRemovals.removesLate(recipe, judged(id, result))) { return false; }
        removedByTags++;
        return true;
    }

    public static void attach(IRecipeFilter filter) {
        attached = filter;
        rebuilt = true;
    }

    private static boolean spared(ResourceLocation id) { return !LOADED.contains(id) || SCRIPTED.contains(id.getNamespace().toLowerCase(Locale.ROOT)); }

    public static void afterReload(IRecipeFilter filter) {
        if (rebuilt) { return; }
        ContentLog.LOGGER.info("The recipe pass did not run inside RecipeManager.apply (another mod took the method over), running it after the reload instead");
        RecipeSwaps.scriptedNote();
        RecipeRewrites.scriptedNote();
        filter.rdpl$filterSkipped();
    }

    public static void giveTransfers() { if (attached instanceof RecipeManager manager) { RecipeTransfer.apply(manager); } }

    public static void onTagsBound() {
        IRecipeFilter filter = attached;
        boolean deferred = RecipeRemovals.deferred() || RecipeRemainders.deferred();
        if (filter == null || FurnaceRecipes.resolvedAtLoad() && ContentDisabled.none() && !deferred) { return; }
        filter.rdpl$filterLate();
        if (removedByTags > 0) { Summary.info("recipes.removed.tags", "Removed " + removedByTags + " more recipe(s) by an item tag or an input, once the tags were bound"); }
        if (removedDisabled > 0) { Summary.info("disabled.recipes", "Removed " + removedDisabled + " recipe(s) that use disabled blocks and items"); }
        RecipeRemainders.report();
        if (FurnaceRecipes.resolvedAtLoad()) { return; }
        reportFurnaces();
    }

    private static void reportFurnaces() {
        FurnaceBlocking.report();
        FurnaceRecipes.report();
    }

    public static void finish() {
        REGISTERED.clear();
        if (skipped > 0) { Summary.info("recipes.skipped", "Skipped " + skipped + " recipe(s) that use items which are not registered, usually content a mod's config has disabled"); }
        int removed = removedByFile + removedByName + removedByOutput + removedByType;
        if (removed > 0) { Summary.info("recipes.removed", "Removed " + removed + " recipe(s): " + removedByFile + " by a remove file, " + removedByName + " by name or regex, " + removedByOutput + " by output, " + removedByType + " by type"); }
        RecipeRemainders.report();
        RecipeBlocking.report();
        if (!FurnaceRecipes.resolvedAtLoad()) { return; }
        reportFurnaces();
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
