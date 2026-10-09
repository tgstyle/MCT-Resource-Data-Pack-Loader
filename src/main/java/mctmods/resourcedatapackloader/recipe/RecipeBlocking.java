package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;

public final class RecipeBlocking {
    private static final String MATCH_RECIPE = "recipe";
    private static final String MATCH_OUTPUT = "output";
    private static final String MATCH_BOTH = "both";
    private static final ModBlocking BLOCKING = new ModBlocking("recipeWhitelist", "blockedRecipeMods", "blockRecipes", "recipes.blocked", "crafting recipe(s)");
    private static final Set<String> WARNED = new LinkedHashSet<>();
    private static String match = MATCH_RECIPE;

    private RecipeBlocking() {}

    public static void reload() {
        BLOCKING.reload(Config.recipes.recipeWhitelist(), Config.recipes.blockedRecipeMods(), Config.recipes.blockRecipes());
        match = ContentControl.text(ContentControl.RECIPES, "recipeMatch", Config.recipes.recipeMatch()).toLowerCase(Locale.ROOT);
    }

    public static boolean blocks(Identifier id, ItemStack result) { return BLOCKING.blocks(() -> owners(id, result)); }

    public static void report() { BLOCKING.report(); }

    private static Set<String> owners(Identifier id, ItemStack result) {
        Set<String> owners = new LinkedHashSet<>();
        String namespace = id.getNamespace().toLowerCase(Locale.ROOT);
        String output = owner(result);
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

    @Nullable static String owner(ItemStack result) {
        if (result.isEmpty()) { return null; }
        return ContentStacks.namespaceOf(result.getItem()).toLowerCase(Locale.ROOT);
    }
}
