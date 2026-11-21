package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public final class RecipeRemovals {
    private static final String REMOVE = "remove";
    private static final RecipeSelector SELECTOR = new RecipeSelector();
    private static final PackGeneration GENERATION = new PackGeneration();

    private RecipeRemovals() {}

    public static void reload() {
        if (!GENERATION.stale()) { return; }
        SELECTOR.clear();
        if (!Config.recipes.removals()) { return; }
        Json.eachObject(PackManager.RECIPE_REMOVALS, "recipe removal file", (key, json) -> {
            Json.unknownKeys(json, "the top level", RecipeSelector.KEYS);
            SELECTOR.read(key, json);
        });
    }

    public static boolean isRemoval(JsonElement json) {
        if (!json.isJsonObject()) { return false; }
        try { return Json.getBoolean(json.getAsJsonObject(), REMOVE, false); }
        catch (RuntimeException notBoolean) { return false; }
    }

    public static boolean removesName(ResourceLocation id) { return SELECTOR.matchesName(id); }

    public static boolean removesOutput(ItemStack result) { return SELECTOR.matchesOutput(result); }

    public static boolean removesType(RecipeType<?> type) { return SELECTOR.matchesType(type); }

    public static boolean tagged() { return SELECTOR.tagged(); }

    public static boolean removesLate(ResourceLocation id, Recipe<?> recipe, ItemStack result) { return SELECTOR.matches(id, recipe, result); }
}
