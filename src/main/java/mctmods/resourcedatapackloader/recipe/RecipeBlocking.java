package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Names;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;

public final class RecipeBlocking {
    public static final String MATCH_RECIPE = "recipe";
    public static final String MATCH_OUTPUT = "output";
    public static final String MATCH_BOTH = "both";
    private static final Blocked BLOCKED = new Blocked();
    private static final Set<String> WARNED = new LinkedHashSet<>();

    private RecipeBlocking() {}

    public static boolean disabled() {
        if (ContentControl.off(ContentControl.RECIPES)) { return true; }

        return !ContentControl.flag(ContentControl.RECIPES, "blockRecipes", Config.recipes.blockRecipes) && ContentControl.list(ContentControl.RECIPES, "blockedRecipeMods", Config.recipes.blockedRecipeMods).length == 0;
    }

    public static void apply(IForgeRegistry<IRecipe> registry) {
        if (disabled()) { return; }
        if (!(registry instanceof IForgeRegistryModifiable)) {
            ContentLog.LOGGER.error("The recipe registry cannot be modified, no recipes were blocked");
            return;
        }

        BLOCKED.clear();
        Set<String> whitelist = Names.lower(ContentControl.list(ContentControl.RECIPES, "recipeWhitelist", Config.recipes.recipeWhitelist));
        Set<String> blocked = Names.lower(ContentControl.list(ContentControl.RECIPES, "blockedRecipeMods", Config.recipes.blockedRecipeMods));
        String match = ContentControl.text(ContentControl.RECIPES, "recipeMatch", Config.recipes.recipeMatch).toLowerCase(Locale.ROOT);

        List<ResourceLocation> doomed = new ArrayList<>();
        for (ResourceLocation key : registry.getKeys()) {
            Set<String> owners = owners(key, registry.getValue(key), match);
            String reason = reason(owners, whitelist, blocked);
            if (reason == null) { continue; }

            doomed.add(key);
            BLOCKED.count(reason);
        }

        IForgeRegistryModifiable<IRecipe> modifiable = (IForgeRegistryModifiable<IRecipe>) registry;
        for (ResourceLocation key : doomed) { modifiable.remove(key); }

        if (doomed.isEmpty()) { return; }

        Summary.info("recipes.blocked", "Blocked " + doomed.size() + " crafting recipe(s)");
        if (ContentControl.flag(ContentControl.RECIPES, "logBlockedRecipes", Config.recipes.logBlockedRecipes)) { BLOCKED.report("crafting recipe(s)"); }
    }

    @Nullable private static String reason(Set<String> owners, Set<String> whitelist, Set<String> blocked) {
        for (String owner : owners) {
            if (blocked.contains(owner)) { return owner; }
        }
        if (!ContentControl.flag(ContentControl.RECIPES, "blockRecipes", Config.recipes.blockRecipes)) { return null; }

        for (String owner : owners) {
            if (whitelist.contains(owner)) { return null; }
        }
        for (String owner : owners) { return owner; }

        return null;
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

        if (!MATCH_RECIPE.equals(match) && WARNED.add(match)) {
            ContentLog.LOGGER.error("recipeMatch is '{}', which is not one of {}, {} or {}. Reading the mod id from the recipe name instead", match, MATCH_RECIPE, MATCH_OUTPUT, MATCH_BOTH);
        }
        owners.add(namespace);
        return owners;
    }

    @Nullable private static String output(@Nullable IRecipe recipe) {
        if (recipe == null) { return null; }

        ItemStack result = recipe.getRecipeOutput();
        if (result.isEmpty()) { return null; }

        ResourceLocation name = result.getItem().getRegistryName();
        return name == null ? null : name.getNamespace().toLowerCase(Locale.ROOT);
    }

}
