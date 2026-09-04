package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RecipeRemovals {
    public static final String REMOVE = "remove";
    private static final String NAMES = "names";
    private static final String NAMESPACES = "namespaces";
    private static final String OUTPUTS = "outputs";
    private static final Gson GSON = new GsonBuilder().create();
    private static final Set<String> NAMED = new LinkedHashSet<>();
    private static final Set<String> PREFIXES = new LinkedHashSet<>();
    private static final Set<Item> OUTPUT_ITEMS = new LinkedHashSet<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private RecipeRemovals() {}

    public static void reload() {
        if (!GENERATION.stale()) { return; }
        NAMED.clear();
        PREFIXES.clear();
        OUTPUT_ITEMS.clear();
        if (!Config.recipes.removals()) { return; }
        Json.eachFile(PackManager.RECIPE_REMOVALS, "recipe removal file", RecipeRemovals::read);
    }

    private static void read(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Recipe removal file {} is empty, ignoring it", key);
            return;
        }
        for (JsonElement element : array(json, NAMES)) {
            String name = element.getAsString();
            if (name.endsWith("*")) { PREFIXES.add(name.substring(0, name.length() - 1)); }
            else { NAMED.add(name); }
        }
        for (JsonElement element : array(json, NAMESPACES)) { PREFIXES.add(element.getAsString() + ":"); }
        for (JsonElement element : array(json, OUTPUTS)) {
            Item item = ContentStacks.find(key, element.getAsString());
            if (item != null) { OUTPUT_ITEMS.add(item); }
        }
    }

    private static JsonArray array(JsonObject json, String member) { return json.has(member) ? GsonHelper.getAsJsonArray(json, member) : new JsonArray(); }

    public static boolean isRemoval(JsonElement json) {
        if (!json.isJsonObject()) { return false; }
        try { return GsonHelper.getAsBoolean(json.getAsJsonObject(), REMOVE, false); }
        catch (RuntimeException notBoolean) { return false; }
    }

    public static boolean removesName(ResourceLocation id) {
        String name = id.toString();
        if (NAMED.contains(name)) { return true; }
        for (String prefix : PREFIXES) {
            if (name.startsWith(prefix)) { return true; }
        }
        return false;
    }

    public static boolean removesOutput(ItemStack result) { return !result.isEmpty() && OUTPUT_ITEMS.contains(result.getItem()); }
}
