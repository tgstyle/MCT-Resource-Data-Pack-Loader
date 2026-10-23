package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import javax.annotation.Nullable;

public final class RecipeRemovals {
    private static final RecipeSelector SELECTOR = new RecipeSelector();

    private RecipeRemovals() {}

    public static void apply(IForgeRegistry<IRecipe> registry) {
        if (!Config.recipes.removals) { return; }
        SELECTOR.clear();
        Json.eachObject(PackManager.RECIPE_REMOVALS, "recipe removal file", SELECTOR::read);
        if (SELECTOR.isEmpty()) { return; }
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

    private static boolean matches(ResourceLocation key, IRecipe recipe) { return SELECTOR.matches(key, recipe == null ? ItemStack.EMPTY : recipe.getRecipeOutput()); }
}
