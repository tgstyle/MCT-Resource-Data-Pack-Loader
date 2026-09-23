package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.pack.RDPLResourcePack;
import mctmods.resourcedatapackloader.recipe.interfaces.IRecipeFilter;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import java.util.HashMap;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class RecipeLoading {
    private static final String ITEM = "item";
    private static final String CONDITIONS = "conditions";
    private static final String NEOFORGE_CONDITIONS = "neoforge:conditions";
    private static final FileToIdConverter FILES = FileToIdConverter.json("recipes");
    private static final Map<String, Boolean> REGISTERED = new HashMap<>();
    private static int removedByFile;
    private static int removedByName;
    private static int removedByOutput;
    private static int skipped;
    @Nullable private static IRecipeFilter attached;
    private static final Set<String> SCRIPTED = Set.of("crafttweaker", "kubejs", "groovyscript");
    private static final Set<ResourceLocation> LOADED = new HashSet<>();
    private static boolean rebuilt;

    private RecipeLoading() {}

    public static void begin(Map<ResourceLocation, JsonElement> recipes, ResourceManager manager, Predicate<JsonElement> conditions, BiConsumer<ResourceLocation, JsonElement> parse) {
        rebuilt = false;
        RecipeRemovals.reload();
        FurnaceRecipes.reload();
        FurnaceRecipes.begin();
        RecipeBlocking.reload();
        FurnaceBlocking.reload();
        REGISTERED.clear();
        removedByFile = 0;
        removedByName = 0;
        removedByOutput = 0;
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
    }

    @SuppressWarnings("resource") private static void keepOriginals(Map<ResourceLocation, JsonElement> recipes, ResourceManager manager, Predicate<JsonElement> conditions, BiConsumer<ResourceLocation, JsonElement> parse) {
        for (Map.Entry<ResourceLocation, List<Resource>> stack : manager.listResourceStacks("recipes", path -> path.getPath().endsWith(".json")).entrySet()) {
            List<Resource> layers = stack.getValue();
            if (layers.size() < 2 || !(layers.get(layers.size() - 1).source() instanceof RDPLResourcePack)) { continue; }
            ResourceLocation id = FILES.fileToId(stack.getKey());
            JsonElement held = recipes.get(id);
            if (id.getPath().startsWith("_") || held != null && RecipeRemovals.isRemoval(held)) { continue; }
            JsonElement original = original(layers);
            if (original != null && failed(id, held, conditions, parse)) { recipes.put(id, original); }
        }
    }

    private static boolean failed(ResourceLocation id, @Nullable JsonElement held, Predicate<JsonElement> conditions, BiConsumer<ResourceLocation, JsonElement> parse) {
        if (held == null) {
            ContentLog.LOGGER.error("Parsing error in recipe {} while reading it, leaving the original in place: it is not valid JSON", id);
            return true;
        }
        if (!conditions.test(held)) {
            ContentLog.LOGGER.debug("Recipe {} was skipped by its own conditions, leaving the original in place", id);
            return true;
        }
        try { parse.accept(id, held); }
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
        if (recipe instanceof AbstractCookingRecipe) { return (FurnaceRecipes.resolvedAtLoad() && FurnaceRecipes.removes(recipe.getIngredients(), result)) || FurnaceBlocking.blocks(result); }
        if (recipe.getType() != RecipeType.CRAFTING) { return false; }
        if (RecipeRemovals.removesName(id)) {
            removedByName++;
            return true;
        }
        if (RecipeRemovals.removesOutput(result)) {
            removedByOutput++;
            return true;
        }
        return RecipeBlocking.blocks(id, result);
    }

    public static boolean late(ResourceLocation id, Recipe<?> recipe, ItemStack result) { return recipe instanceof AbstractCookingRecipe && FurnaceRecipes.removesLate(id, recipe.getIngredients(), result, spared(id)); }

    public static void attach(IRecipeFilter filter) {
        attached = filter;
        rebuilt = true;
    }

    private static boolean spared(ResourceLocation id) { return !LOADED.contains(id) || SCRIPTED.contains(id.getNamespace().toLowerCase(Locale.ROOT)); }

    public static void afterReload(IRecipeFilter filter) {
        if (rebuilt) { return; }
        ContentLog.LOGGER.info("The recipe pass did not run inside RecipeManager.apply (another mod took the method over), running it after the reload instead");
        filter.rdpl$filterSkipped();
    }

    public static void onTagsBound() {
        IRecipeFilter filter = attached;
        if (filter == null || FurnaceRecipes.resolvedAtLoad()) { return; }
        filter.rdpl$filterLate();
        FurnaceBlocking.report();
        FurnaceRecipes.report();
    }

    public static void finish() {
        REGISTERED.clear();
        if (skipped > 0) { Summary.info("recipes.skipped", "Skipped " + skipped + " recipe(s) that use items which are not registered, usually content a mod's config has disabled"); }
        int removed = removedByFile + removedByName + removedByOutput;
        if (removed > 0) { Summary.info("recipes.removed", "Removed " + removed + " recipe(s): " + removedByFile + " by a remove file, " + removedByName + " by name, " + removedByOutput + " by output"); }
        RecipeBlocking.report();
        if (!FurnaceRecipes.resolvedAtLoad()) { return; }
        FurnaceBlocking.report();
        FurnaceRecipes.report();
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
