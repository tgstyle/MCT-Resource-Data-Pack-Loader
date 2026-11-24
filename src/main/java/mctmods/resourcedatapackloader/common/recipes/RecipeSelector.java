package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.common.util.ItemSelection;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

final class RecipeSelector {
    private static final String NAMES = "names";
    private static final String NAMESPACES = "namespaces";
    private static final String OUTPUTS = "outputs";
    private static final String REGEX = "regex";
    private static final String TYPES = "types";
    private static final String INPUTS = "inputs";
    static final List<String> KEYS = List.of(NAMES, NAMESPACES, OUTPUTS, REGEX, INPUTS, TYPES);
    private final Set<String> named = new LinkedHashSet<>();
    private final Set<String> prefixes = new LinkedHashSet<>();
    private final Set<Item> outputItems = new LinkedHashSet<>();
    private final Set<TagKey<Item>> outputTags = new LinkedHashSet<>();
    private final List<Pattern> patterns = new ArrayList<>();
    private final Set<ResourceLocation> types = new LinkedHashSet<>();
    private final List<String> inputNames = new ArrayList<>();
    private final ItemSelection inputs = new ItemSelection("Recipe selector input", "matches");
    private int invalid;

    int invalid() { return invalid; }

    static boolean lacksRecipes(String kind, ResourceLocation key, JsonObject json) {
        if (json.has("recipes") && json.get("recipes").isJsonObject()) { return false; }
        ContentLog.LOGGER.error("{} file {} needs a recipes object saying which recipes it covers, ignoring it", kind, key);
        return true;
    }

    @Nullable static RecipeSelector scopeOf(String kind, ResourceLocation key, JsonObject json) { return scopeOf(kind, key, json, true); }

    @Nullable static RecipeSelector scopeOf(String kind, ResourceLocation key, JsonObject json, boolean withInputs) {
        RecipeSelector scope = new RecipeSelector();
        if (withInputs) { scope.read(key, json); }
        else { scope.readWithoutInputs(key, json); }
        if (!scope.isEmpty()) { return scope; }
        ContentLog.LOGGER.error("{} file {} selects no recipes, ignoring it", kind, key);
        return null;
    }

    void clear() {
        invalid = 0;
        named.clear();
        prefixes.clear();
        outputItems.clear();
        outputTags.clear();
        patterns.clear();
        types.clear();
        inputNames.clear();
        inputs.clear();
    }

    boolean isEmpty() { return named.isEmpty() && prefixes.isEmpty() && outputItems.isEmpty() && outputTags.isEmpty() && patterns.isEmpty() && types.isEmpty() && inputNames.isEmpty(); }

    boolean tagged() { return !outputTags.isEmpty() || !inputNames.isEmpty(); }

    void read(ResourceLocation key, JsonObject json) { read(key, json, true); }

    void readWithoutInputs(ResourceLocation key, JsonObject json) { read(key, json, false); }

    private void read(ResourceLocation key, JsonObject json, boolean withInputs) {
        for (JsonElement element : array(json, NAMES)) {
            String name = element.getAsString();
            if (name.endsWith("*")) { prefixes.add(name.substring(0, name.length() - 1)); }
            else { named.add(name); }
        }
        for (JsonElement element : array(json, NAMESPACES)) { prefixes.add(element.getAsString() + ":"); }
        for (JsonElement element : array(json, OUTPUTS)) { readOutput(key, element.getAsString()); }
        for (String regex : Json.strings(json, REGEX)) { readPattern(key, regex); }
        if (!withInputs) { return; }
        for (String type : Json.strings(json, TYPES)) { readType(key, type); }
        List<String> wanted = Json.strings(json, INPUTS);
        inputNames.addAll(wanted);
        inputs.readNames(wanted, key);
        inputs.unknown();
    }

    boolean matchesName(ResourceLocation id) {
        String name = id.toString();
        if (named.contains(name)) { return true; }
        for (String prefix : prefixes) {
            if (name.startsWith(prefix)) { return true; }
        }
        for (Pattern pattern : patterns) {
            if (pattern.matcher(name).find()) { return true; }
        }
        return false;
    }

    boolean matchesOutput(ItemStack result) {
        if (result.isEmpty()) { return false; }
        if (outputItems.contains(result.getItem())) { return true; }
        for (TagKey<Item> tag : outputTags) {
            if (result.is(tag)) { return true; }
        }
        return false;
    }

    boolean matchesType(RecipeType<?> type) { return !types.isEmpty() && types.contains(BuiltInRegistries.RECIPE_TYPE.getKey(type)); }

    boolean matches(ResourceLocation id, Recipe<?> recipe, ItemStack result) { return matchesName(id) || matchesOutput(result) || matchesType(recipe.getType()) || matchesInput(recipe); }

    boolean matchesInput(Recipe<?> recipe) {
        if (inputNames.isEmpty()) { return false; }
        for (Ingredient ingredient : recipe.getIngredients()) {
            for (ItemStack member : ingredient.getItems()) {
                if (inputs.matches(member)) { return true; }
            }
        }
        return false;
    }

    private void readOutput(ResourceLocation key, String value) {
        if (value.startsWith("#")) {
            ResourceLocation tag = Registered.tagId(value);
            if (tag == null) { ContentLog.LOGGER.error("Tag '{}' in {} is not a valid name, skipping it", value, key); }
            else { outputTags.add(TagKey.create(Registries.ITEM, tag)); }
            return;
        }
        Item item = ContentStacks.find(key, value);
        if (item != null) { outputItems.add(item); }
    }

    private void readPattern(ResourceLocation key, String regex) {
        try { patterns.add(Pattern.compile(regex)); }
        catch (PatternSyntaxException ex) {
            invalid++;
            ContentLog.LOGGER.error("The regex '{}' in {} is not valid, skipping it: {}", regex, key, ex.getDescription());
        }
    }

    private void readType(ResourceLocation key, String value) {
        ResourceLocation id = ResourceLocation.tryParse(value);
        if (id == null || !BuiltInRegistries.RECIPE_TYPE.containsKey(id)) { ContentLog.LOGGER.error("The recipe type '{}' in {} is not registered, so it matches nothing", value, key); }
        if (id != null) { types.add(id); }
    }

    private static JsonArray array(JsonObject json, String member) { return json.has(member) ? Json.getJsonArray(json, member) : new JsonArray(); }
}
