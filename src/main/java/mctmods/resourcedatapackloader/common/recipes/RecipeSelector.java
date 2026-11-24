package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.common.util.loot.Stacks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

final class RecipeSelector {
    static final List<String> KEYS = Collections.unmodifiableList(Arrays.asList("names", "namespaces", "outputs", "regex", "inputs", "types"));
    private final Set<String> names = new LinkedHashSet<>();
    private final Set<String> prefixes = new LinkedHashSet<>();
    private final List<ItemStack> outputs = new ArrayList<>();
    private final Set<String> outputOres = new LinkedHashSet<>();
    private final List<Pattern> patterns = new ArrayList<>();
    private final List<String> inputTexts = new ArrayList<>();
    private ItemSelection inputs = new ItemSelection("Recipe selector input", "matches");
    private boolean resolved;

    static boolean lacksRecipes(String kind, ResourceLocation key, JsonObject json) {
        if (json.has("recipes") && json.get("recipes").isJsonObject()) { return false; }
        ContentLog.LOGGER.error("{} file {} needs a recipes object saying which recipes it covers, ignoring it", kind, key);
        return true;
    }

    @Nullable static RecipeSelector scopeOf(String kind, ResourceLocation key, JsonObject json) {
        RecipeSelector scope = new RecipeSelector();
        scope.read(key, json);
        if (!scope.isEmpty()) { return scope; }
        ContentLog.LOGGER.error("{} file {} selects no recipes, ignoring it", kind, key);
        return null;
    }

    static <E, T> void byRecipe(IForgeRegistry<IRecipe> registry, List<E> entries, Function<E, RecipeSelector> scope, BiConsumer<List<T>, E> collect, Map<ResourceLocation, List<T>> into) {
        for (ResourceLocation key : registry.getKeys()) {
            IRecipe recipe = registry.getValue(key);
            if (recipe == null) { continue; }
            for (E entry : entries) {
                if (scope.apply(entry).matches(key, recipe)) { collect.accept(into.computeIfAbsent(key, k -> new ArrayList<>()), entry); }
            }
        }
    }

    void clear() {
        names.clear();
        prefixes.clear();
        outputs.clear();
        outputOres.clear();
        patterns.clear();
        inputTexts.clear();
        resolved = false;
    }

    boolean isEmpty() { return names.isEmpty() && prefixes.isEmpty() && outputs.isEmpty() && outputOres.isEmpty() && patterns.isEmpty() && inputTexts.isEmpty(); }

    void read(ResourceLocation key, JsonObject json) {
        for (JsonElement element : array(json, "names")) {
            String name = element.getAsString();
            if (name.endsWith("*")) { prefixes.add(name.substring(0, name.length() - 1)); }
            else { names.add(name); }
        }
        for (JsonElement element : array(json, "namespaces")) { prefixes.add(element.getAsString() + ":"); }
        for (JsonElement element : array(json, "outputs")) { readOutput(key, element.getAsString()); }
        for (String regex : Json.strings(json, "regex")) { readPattern(key, regex); }
        inputTexts.addAll(Json.strings(json, "inputs"));
        if (!Json.strings(json, "types").isEmpty()) { ContentLog.LOGGER.error("{} lists types, but 1.12.2 has no recipe types, so they are ignored", key); }
        resolved = false;
    }

    boolean matches(ResourceLocation key, ItemStack output) {
        String name = key.toString();
        if (names.contains(name)) { return true; }
        for (String prefix : prefixes) {
            if (name.startsWith(prefix)) { return true; }
        }
        for (Pattern pattern : patterns) {
            if (pattern.matcher(name).find()) { return true; }
        }
        if (output.isEmpty()) { return false; }
        for (ItemStack wanted : outputs) {
            if (Stacks.matches(wanted, output)) { return true; }
        }
        if (outputOres.isEmpty()) { return false; }
        for (int id : OreDictionary.getOreIDs(output)) {
            if (outputOres.contains(OreDictionary.getOreName(id))) { return true; }
        }
        return false;
    }

    boolean matches(ResourceLocation key, IRecipe recipe) { return matches(key, recipe == null ? ItemStack.EMPTY : recipe.getRecipeOutput()) || recipe != null && usesInput(recipe); }

    private boolean usesInput(IRecipe recipe) {
        if (inputTexts.isEmpty()) { return false; }
        resolveInputs();
        for (Ingredient ingredient : recipe.getIngredients()) {
            for (ItemStack member : ingredient.getMatchingStacks()) {
                if (inputs.matches(member)) { return true; }
            }
        }
        return false;
    }

    private void resolveInputs() {
        if (resolved) { return; }
        inputs = ItemSelection.fromText("Recipe selector input", ResourceDataPackLoader.rdpl("recipe_selector"), inputTexts);
        resolved = true;
    }

    private void readOutput(ResourceLocation key, String value) {
        String ore = ItemSelection.oreName(value);
        if (ore != null) {
            outputOres.add(ore);
            return;
        }
        ItemStack stack = ContentStacks.parse(key, value, 1);
        if (!stack.isEmpty()) { outputs.add(stack); }
    }

    private void readPattern(ResourceLocation key, String regex) {
        try { patterns.add(Pattern.compile(regex)); }
        catch (PatternSyntaxException ex) { ContentLog.LOGGER.error("The regex '{}' in {} is not valid, skipping it: {}", regex, key, ex.getDescription()); }
    }

    private static JsonArray array(JsonObject json, String member) { return json.has(member) ? Json.getJsonArray(json, member) : new JsonArray(); }
}
