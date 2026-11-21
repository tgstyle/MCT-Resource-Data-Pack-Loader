package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.stacks.ContentStacks;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;

final class RecipeSelector {
    private static final String NAMES = "names";
    private static final String NAMESPACES = "namespaces";
    private static final String OUTPUTS = "outputs";
    private static final String INPUTS = "inputs";
    private static final String REGEX = "regex";
    private static final String TYPES = "types";
    static final List<String> KEYS = List.of(NAMES, NAMESPACES, OUTPUTS, REGEX, INPUTS, TYPES);
    private final Set<String> named = new LinkedHashSet<>();
    private final Set<String> prefixes = new LinkedHashSet<>();
    private final Set<Pattern> patterns = new LinkedHashSet<>();
    private final Set<ResourceLocation> types = new LinkedHashSet<>();
    private final Set<Item> outputItems = new LinkedHashSet<>();
    private final Set<TagKey<Item>> outputTags = new LinkedHashSet<>();
    private final Set<Item> inputItems = new LinkedHashSet<>();
    private final Set<TagKey<Item>> inputTags = new LinkedHashSet<>();
    private int invalid;

    int invalid() { return invalid; }

    void clear() {
        invalid = 0;
        named.clear();
        prefixes.clear();
        patterns.clear();
        types.clear();
        outputItems.clear();
        outputTags.clear();
        inputItems.clear();
        inputTags.clear();
    }

    boolean isEmpty() { return named.isEmpty() && prefixes.isEmpty() && patterns.isEmpty() && types.isEmpty() && outputItems.isEmpty() && outputTags.isEmpty() && inputItems.isEmpty() && inputTags.isEmpty(); }

    boolean deferred() { return !outputTags.isEmpty() || !inputItems.isEmpty() || !inputTags.isEmpty(); }

    void read(ResourceLocation key, JsonObject json) {
        for (JsonElement element : array(json, NAMES)) {
            String name = element.getAsString();
            if (name.endsWith("*")) { prefixes.add(name.substring(0, name.length() - 1)); }
            else { named.add(name); }
        }
        for (JsonElement element : array(json, NAMESPACES)) { prefixes.add(element.getAsString() + ":"); }
        for (String regex : Json.strings(json, REGEX)) { readPattern(key, regex); }
        for (String type : Json.strings(json, TYPES)) {
            ResourceLocation id = ResourceLocation.tryParse(type);
            if (id == null) { ContentLog.LOGGER.error("The recipe type '{}' in {} is not a valid name, skipping it", type, key); }
            else { types.add(id); }
        }
        readItems(key, Json.strings(json, OUTPUTS), outputItems, outputTags);
        readItems(key, Json.strings(json, INPUTS), inputItems, inputTags);
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

    boolean matchesType(Recipe<?> recipe) { return !types.isEmpty() && types.contains(ForgeRegistries.RECIPE_TYPES.getKey(recipe.getType())); }

    boolean matchesInput(Recipe<?> recipe) {
        if (inputItems.isEmpty() && inputTags.isEmpty()) { return false; }
        for (Ingredient ingredient : recipe.getIngredients()) {
            for (ItemStack member : ingredient.getItems()) {
                if (accepts(member)) { return true; }
            }
        }
        return false;
    }

    private boolean accepts(ItemStack member) {
        if (inputItems.contains(member.getItem())) { return true; }
        for (TagKey<Item> tag : inputTags) {
            if (member.is(tag)) { return true; }
        }
        return false;
    }

    private static void readItems(ResourceLocation key, Iterable<String> values, Set<Item> items, Set<TagKey<Item>> tags) {
        for (String value : values) {
            if (!value.startsWith("#")) {
                Item item = ContentStacks.find(key, value);
                if (item != null) { items.add(item); }
                continue;
            }
            ResourceLocation tag = Registered.tagId(value);
            if (tag == null) { ContentLog.LOGGER.error("Tag '{}' in {} is not a valid name, skipping it", value, key); }
            else { tags.add(TagKey.create(Registries.ITEM, tag)); }
        }
    }

    private void readPattern(ResourceLocation key, String regex) {
        try { patterns.add(Pattern.compile(regex)); }
        catch (PatternSyntaxException ex) {
            invalid++;
            ContentLog.LOGGER.error("The regex '{}' in {} is not valid, skipping it: {}", regex, key, ex.getDescription());
        }
    }

    private static JsonArray array(JsonObject json, String member) { return json.has(member) ? Json.getJsonArray(json, member) : new JsonArray(); }
}
