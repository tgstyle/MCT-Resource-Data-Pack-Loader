package mctmods.resourcedatapackloader.recipe;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;

public final class RecipeBlocking {
    public static final String MATCH_RECIPE = "recipe";
    public static final String MATCH_OUTPUT = "output";
    public static final String MATCH_BOTH = "both";
    private static final Blocked BLOCKED = new Blocked();
    private static final Set<String> WARNED = new LinkedHashSet<>();
    private static Set<String> whitelist = Collections.emptySet();
    private static Set<String> blocked = Collections.emptySet();
    private static String match = MATCH_RECIPE;
    private static boolean blockAll;
    private static boolean off;

    private RecipeBlocking() {}

    public static void reload() {
        BLOCKED.clear();
        off = ContentControl.off(ContentControl.RECIPES);
        whitelist = Settings.lower(ContentControl.list(ContentControl.RECIPES, "recipeWhitelist", Config.recipes.recipeWhitelist()));
        blocked = Settings.lower(ContentControl.list(ContentControl.RECIPES, "blockedRecipeMods", Config.recipes.blockedRecipeMods()));
        match = ContentControl.text(ContentControl.RECIPES, "recipeMatch", Config.recipes.recipeMatch()).toLowerCase(Locale.ROOT);
        blockAll = ContentControl.flag(ContentControl.RECIPES, "blockRecipes", Config.recipes.blockRecipes());
    }

    public static boolean disabled() { return off || (!blockAll && blocked.isEmpty()); }

    public static boolean blocks(ResourceLocation id, ItemStack result) {
        if (disabled()) { return false; }
        String reason = reason(owners(id, result));
        if (reason == null) { return false; }
        BLOCKED.count(reason);
        return true;
    }

    public static void report() {
        int total = BLOCKED.total();
        if (total == 0) { return; }
        Summary.info("recipes.blocked", "Blocked " + total + " crafting recipe(s)");
        if (ContentControl.flag(ContentControl.RECIPES, "logBlockedRecipes", Config.recipes.logBlockedRecipes())) { BLOCKED.report("crafting recipe(s)"); }
    }

    @Nullable private static String reason(Set<String> owners) {
        for (String owner : owners) {
            if (blocked.contains(owner)) { return owner; }
        }
        if (!blockAll) { return null; }
        for (String owner : owners) {
            if (whitelist.contains(owner)) { return null; }
        }
        for (String owner : owners) { return owner; }
        return null;
    }

    private static Set<String> owners(ResourceLocation id, ItemStack result) {
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
        if (!MATCH_RECIPE.equals(match) && WARNED.add(match)) {
            ContentLog.LOGGER.error("recipeMatch is '{}', which is not one of {}, {} or {}. Reading the mod id from the recipe name instead", match, MATCH_RECIPE, MATCH_OUTPUT, MATCH_BOTH);
        }
        owners.add(namespace);
        return owners;
    }

    @Nullable static String owner(ItemStack result) {
        if (result.isEmpty()) { return null; }
        return ContentStacks.namespaceOf(result.getItem()).toLowerCase(Locale.ROOT);
    }
}
