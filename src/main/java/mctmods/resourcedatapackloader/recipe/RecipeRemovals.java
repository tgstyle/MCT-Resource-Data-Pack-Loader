package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.util.Stacks;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import javax.annotation.Nullable;

public final class RecipeRemovals {
    private static final Set<String> NAMES = new LinkedHashSet<>();
    private static final Set<String> PREFIXES = new LinkedHashSet<>();
    private static final List<ItemStack> OUTPUTS = new ArrayList<>();

    private RecipeRemovals() {}

    public static void apply(IForgeRegistry<IRecipe> registry) {
        if (!Config.recipes.removals) { return; }
        NAMES.clear();
        PREFIXES.clear();
        OUTPUTS.clear();
        Json.eachObject(PackManager.RECIPE_REMOVALS, "recipe removal file", RecipeRemovals::read);
        if (NAMES.isEmpty() && PREFIXES.isEmpty() && OUTPUTS.isEmpty()) { return; }
        int removed = removeWhere(registry, (key, recipe) -> !RecipeSpared.spares(key) && matches(key, recipe)).size();
        if (removed > 0) { Summary.info("recipes.removed", "Removed " + removed + " crafting recipe(s)"); }
    }

    public static List<ResourceLocation> removeWhere(IForgeRegistry<IRecipe> registry, BiPredicate<ResourceLocation, IRecipe> doomed) {
        IForgeRegistryModifiable<IRecipe> modifiable = modifiable(registry);
        if (modifiable == null) {
            ContentLog.LOGGER.error("The recipe registry cannot be modified, no recipes were removed");
            return new ArrayList<>();
        }
        List<ResourceLocation> keys = new ArrayList<>();
        for (ResourceLocation key : registry.getKeys()) {
            if (doomed.test(key, registry.getValue(key))) { keys.add(key); }
        }
        for (ResourceLocation key : keys) { modifiable.remove(key); }
        return keys;
    }

    @Nullable static IForgeRegistryModifiable<IRecipe> modifiable(IForgeRegistry<IRecipe> registry) { return registry instanceof IForgeRegistryModifiable ? (IForgeRegistryModifiable<IRecipe>) registry : null; }

    private static void read(ResourceLocation key, JsonObject json) {
        for (JsonElement element : array(json, "names")) {
            String name = element.getAsString();
            if (name.endsWith("*")) { PREFIXES.add(name.substring(0, name.length() - 1)); }
            else { NAMES.add(name); }
        }
        for (JsonElement element : array(json, "namespaces")) { PREFIXES.add(element.getAsString() + ":"); }
        for (JsonElement element : array(json, "outputs")) {
            ItemStack stack = ContentStacks.parse(key, element.getAsString(), 1);
            if (!stack.isEmpty()) { OUTPUTS.add(stack); }
        }
    }

    private static JsonArray array(JsonObject json, String member) { return json.has(member) ? Json.getJsonArray(json, member) : new JsonArray(); }

    private static boolean matches(ResourceLocation key, IRecipe recipe) {
        String name = key.toString();
        if (NAMES.contains(name)) { return true; }
        for (String prefix : PREFIXES) {
            if (name.startsWith(prefix)) { return true; }
        }
        if (recipe == null || OUTPUTS.isEmpty()) { return false; }
        ItemStack output = recipe.getRecipeOutput();
        if (output.isEmpty()) { return false; }
        for (ItemStack wanted : OUTPUTS) {
            if (Stacks.matches(wanted, output)) { return true; }
        }
        return false;
    }
}
