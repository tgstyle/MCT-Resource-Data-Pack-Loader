package mctmods.resourcedatapackloader.common.recipes;

import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.util.loot.Stacks;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public final class RecipeBlocking {
    private static final String MATCH_RECIPE = "recipe";
    private static final String MATCH_OUTPUT = "output";
    private static final String MATCH_BOTH = "both";
    private static final ModBlocking BLOCKING = new ModBlocking("recipeWhitelist", () -> Config.recipes.recipeWhitelist, "blockedRecipeMods", () -> Config.recipes.blockedRecipeMods, "blockRecipes", () -> Config.recipes.blockRecipes, "recipes.blocked", "crafting recipe(s)");
    private static final Set<String> WARNED = new LinkedHashSet<>();

    private RecipeBlocking() {}

    public static void apply(IForgeRegistry<IRecipe> registry) {
        BLOCKING.reset();
        if (BLOCKING.disabled()) { return; }
        IForgeRegistryModifiable<IRecipe> modifiable = RecipeRemovals.modifiable(registry);
        if (modifiable == null) {
            ContentLog.LOGGER.error("The recipe registry cannot be modified, no recipes were blocked");
            return;
        }
        String match = ContentControl.text(ContentControl.RECIPES, "recipeMatch", Config.recipes.recipeMatch).toLowerCase(Locale.ROOT);
        List<ResourceLocation> doomed = new ArrayList<>();
        for (ResourceLocation key : registry.getKeys()) {
            if (RecipeSpared.spares(key)) { continue; }
            if (BLOCKING.blocks(owners(key, registry.getValue(key), match))) { doomed.add(key); }
        }
        for (ResourceLocation key : doomed) { modifiable.remove(key); }
        BLOCKING.report("");
    }

    private static Set<String> owners(ResourceLocation key, @Nullable IRecipe recipe, String match) {
        Set<String> owners = new LinkedHashSet<>();
        String namespace = key.getNamespace().toLowerCase(Locale.ROOT);
        String output = output(recipe);
        if (MATCH_OUTPUT.equals(match)) {
            if (output != null) { owners.add(output); }
            return owners;
        }
        if (MATCH_BOTH.equals(match)) {
            owners.add(namespace);
            if (output != null) { owners.add(output); }
            return owners;
        }
        if (!MATCH_RECIPE.equals(match)) { ContentLog.LOGGER.errorOnce(WARNED, match, "recipeMatch is '{}', which is not one of {}, {} or {}. Reading the mod id from the recipe name instead", match, MATCH_RECIPE, MATCH_OUTPUT, MATCH_BOTH); }
        owners.add(namespace);
        return owners;
    }

    @Nullable private static String output(@Nullable IRecipe recipe) { return recipe == null ? null : Stacks.namespace(recipe.getRecipeOutput()); }
}
