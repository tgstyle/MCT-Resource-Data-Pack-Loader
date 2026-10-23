package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.LinkedHashSet;
import java.util.Set;

final class RecipeSelector {
    private static final String NAMES = "names";
    private static final String NAMESPACES = "namespaces";
    private static final String OUTPUTS = "outputs";
    private final Set<String> named = new LinkedHashSet<>();
    private final Set<String> prefixes = new LinkedHashSet<>();
    private final Set<Item> outputItems = new LinkedHashSet<>();

    void clear() {
        named.clear();
        prefixes.clear();
        outputItems.clear();
    }

    boolean isEmpty() { return named.isEmpty() && prefixes.isEmpty() && outputItems.isEmpty(); }

    void read(Identifier key, JsonObject json) {
        for (JsonElement element : array(json, NAMES)) {
            String name = element.getAsString();
            if (name.endsWith("*")) { prefixes.add(name.substring(0, name.length() - 1)); }
            else { named.add(name); }
        }
        for (JsonElement element : array(json, NAMESPACES)) { prefixes.add(element.getAsString() + ":"); }
        for (JsonElement element : array(json, OUTPUTS)) {
            Item item = ContentStacks.find(key, element.getAsString());
            if (item != null) { outputItems.add(item); }
        }
    }

    boolean matchesName(Identifier id) {
        String name = id.toString();
        if (named.contains(name)) { return true; }
        for (String prefix : prefixes) {
            if (name.startsWith(prefix)) { return true; }
        }
        return false;
    }

    boolean matchesOutput(ItemStack result) { return !result.isEmpty() && outputItems.contains(result.getItem()); }

    private static JsonArray array(JsonObject json, String member) { return json.has(member) ? Json.getJsonArray(json, member) : new JsonArray(); }
}
