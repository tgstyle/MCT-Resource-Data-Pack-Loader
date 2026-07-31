package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RecipeRemovals {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Set<String> NAMES = new LinkedHashSet<>();
    private static final Set<String> PREFIXES = new LinkedHashSet<>();
    private static final List<ItemStack> OUTPUTS = new ArrayList<>();

    private RecipeRemovals() {}

    public static void apply(IForgeRegistry<IRecipe> registry) {
        if (!Config.recipes.removals) { return; }
        if (!(registry instanceof IForgeRegistryModifiable)) {
            ContentLog.LOGGER.error("The recipe registry cannot be modified, no recipes were removed");
            return;
        }

        NAMES.clear();
        PREFIXES.clear();
        OUTPUTS.clear();

        PackManager.get().forEach(PackManager.RECIPE_REMOVALS, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try { read(key, contents); }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in recipe removal file {}, ignoring it", key, ex); }
        });

        if (NAMES.isEmpty() && PREFIXES.isEmpty() && OUTPUTS.isEmpty()) { return; }

        List<ResourceLocation> doomed = new ArrayList<>();
        for (ResourceLocation key : registry.getKeys()) {
            if (matches(key, registry.getValue(key))) { doomed.add(key); }
        }

        IForgeRegistryModifiable<IRecipe> modifiable = (IForgeRegistryModifiable<IRecipe>) registry;
        for (ResourceLocation key : doomed) { modifiable.remove(key); }

        if (!doomed.isEmpty()) { Summary.info("recipes.removed", "Removed " + doomed.size() + " crafting recipe(s)"); }
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = JsonUtils.gsonDeserialize(GSON, contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Recipe removal file {} is empty, ignoring it", key);
            return;
        }

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

    private static JsonArray array(JsonObject json, String member) {
        return json.has(member) ? JsonUtils.getJsonArray(json, member) : new JsonArray();
    }

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
            if (wanted.getItem() != output.getItem()) { continue; }
            if (wanted.getMetadata() == OreDictionary.WILDCARD_VALUE || wanted.getMetadata() == output.getMetadata()) { return true; }
        }
        return false;
    }
}
