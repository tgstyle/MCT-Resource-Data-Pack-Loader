package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Stacks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class RecipeSelector {
    private final Set<String> names = new LinkedHashSet<>();
    private final Set<String> prefixes = new LinkedHashSet<>();
    private final List<ItemStack> outputs = new ArrayList<>();

    void clear() {
        names.clear();
        prefixes.clear();
        outputs.clear();
    }

    boolean isEmpty() { return names.isEmpty() && prefixes.isEmpty() && outputs.isEmpty(); }

    void read(ResourceLocation key, JsonObject json) {
        for (JsonElement element : array(json, "names")) {
            String name = element.getAsString();
            if (name.endsWith("*")) { prefixes.add(name.substring(0, name.length() - 1)); }
            else { names.add(name); }
        }
        for (JsonElement element : array(json, "namespaces")) { prefixes.add(element.getAsString() + ":"); }
        for (JsonElement element : array(json, "outputs")) {
            ItemStack stack = ContentStacks.parse(key, element.getAsString(), 1);
            if (!stack.isEmpty()) { outputs.add(stack); }
        }
    }

    boolean matches(ResourceLocation key, ItemStack output) {
        String name = key.toString();
        if (names.contains(name)) { return true; }
        for (String prefix : prefixes) {
            if (name.startsWith(prefix)) { return true; }
        }
        if (outputs.isEmpty() || output.isEmpty()) { return false; }
        for (ItemStack wanted : outputs) {
            if (Stacks.matches(wanted, output)) { return true; }
        }
        return false;
    }

    private static JsonArray array(JsonObject json, String member) { return json.has(member) ? Json.getJsonArray(json, member) : new JsonArray(); }
}
