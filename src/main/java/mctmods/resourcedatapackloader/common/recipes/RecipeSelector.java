package mctmods.resourcedatapackloader.common.recipes;

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
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

final class RecipeSelector {
    static final List<String> KEYS = Collections.unmodifiableList(Arrays.asList("names", "namespaces", "outputs", "regex", "inputs", "types"));
    private final Set<String> names = new LinkedHashSet<>();
    private final Set<String> prefixes = new LinkedHashSet<>();
    private final List<ItemStack> outputs = new ArrayList<>();
    private final Set<String> outputOres = new LinkedHashSet<>();
    private final List<Pattern> patterns = new ArrayList<>();
    private final List<String> inputNames = new ArrayList<>();
    private final List<String> inputOres = new ArrayList<>();
    private ItemSelection inputs = new ItemSelection("Recipe selector input", "matches");
    private boolean resolved;

    void clear() {
        names.clear();
        prefixes.clear();
        outputs.clear();
        outputOres.clear();
        patterns.clear();
        inputNames.clear();
        inputOres.clear();
        resolved = false;
    }

    boolean isEmpty() { return names.isEmpty() && prefixes.isEmpty() && outputs.isEmpty() && outputOres.isEmpty() && patterns.isEmpty() && inputNames.isEmpty() && inputOres.isEmpty(); }

    void read(ResourceLocation key, JsonObject json) {
        for (JsonElement element : array(json, "names")) {
            String name = element.getAsString();
            if (name.endsWith("*")) { prefixes.add(name.substring(0, name.length() - 1)); }
            else { names.add(name); }
        }
        for (JsonElement element : array(json, "namespaces")) { prefixes.add(element.getAsString() + ":"); }
        for (JsonElement element : array(json, "outputs")) { readOutput(key, element.getAsString()); }
        for (String regex : Json.strings(json, "regex")) { readPattern(key, regex); }
        for (String input : Json.strings(json, "inputs")) {
            if (input.startsWith("#")) { inputOres.add(input.substring(1)); }
            else { inputNames.add(input); }
        }
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
        if (inputNames.isEmpty() && inputOres.isEmpty()) { return false; }
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
        inputs = new ItemSelection("Recipe selector input", "matches");
        JsonObject limits = new JsonObject();
        limits.add("names", strings(inputNames));
        limits.add("oreDict", strings(inputOres));
        inputs.read(limits, new ResourceLocation("rdpl", "recipe_selector"));
        inputs.resolve();
        resolved = true;
    }

    private void readOutput(ResourceLocation key, String value) {
        if (value.startsWith("#")) {
            outputOres.add(value.substring(1));
            return;
        }
        ItemStack stack = ContentStacks.parse(key, value, 1);
        if (!stack.isEmpty()) { outputs.add(stack); }
    }

    private void readPattern(ResourceLocation key, String regex) {
        try { patterns.add(Pattern.compile(regex)); }
        catch (PatternSyntaxException ex) { ContentLog.LOGGER.error("The regex '{}' in {} is not valid, skipping it: {}", regex, key, ex.getDescription()); }
    }

    private static JsonArray strings(List<String> values) {
        JsonArray array = new JsonArray();
        for (String value : values) { array.add(value); }
        return array;
    }

    private static JsonArray array(JsonObject json, String member) { return json.has(member) ? Json.getJsonArray(json, member) : new JsonArray(); }
}
