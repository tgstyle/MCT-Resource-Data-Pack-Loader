package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;

import com.google.gson.JsonElement;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class RecipeRemovals {
    private static final String REMOVE = "remove";
    private static final RecipeSelector SELECTOR = new RecipeSelector();
    private static final PackGeneration GENERATION = new PackGeneration();

    private RecipeRemovals() {}

    public static void reload() {
        if (!GENERATION.stale()) { return; }
        SELECTOR.clear();
        if (!Config.recipes.removals()) { return; }
        Json.eachObject(PackManager.RECIPE_REMOVALS, "recipe removal file", SELECTOR::read);
    }

    public static boolean isRemoval(JsonElement json) {
        if (!json.isJsonObject()) { return false; }
        try { return Json.getBoolean(json.getAsJsonObject(), REMOVE, false); }
        catch (RuntimeException notBoolean) { return false; }
    }

    public static boolean removesName(Identifier id) { return SELECTOR.matchesName(id); }

    public static boolean removesOutput(ItemStack result) { return SELECTOR.matchesOutput(result); }
}
