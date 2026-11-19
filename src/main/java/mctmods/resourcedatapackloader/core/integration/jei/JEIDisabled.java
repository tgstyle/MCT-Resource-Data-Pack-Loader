package mctmods.resourcedatapackloader.core.integration.jei;

import mctmods.resourcedatapackloader.common.util.ContentDisabled;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;

import javax.annotation.Nonnull;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;

@SuppressWarnings("unused") @JEIPlugin public final class JEIDisabled implements IModPlugin {
    @Override public void register(@Nonnull IModRegistry registry) {
        IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        if (ContentDisabled.any()) { ContentDisabled.eachStack(blacklist::addIngredientToBlacklist); }
        if (ContentDisabled.anyHidden()) { ContentDisabled.eachHidden(blacklist::addIngredientToBlacklist); }
    }

    @Override public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        IRecipeRegistry recipes = jeiRuntime.getRecipeRegistry();
        int hidden = 0;
        for (String uid : ContentDisabled.hiddenCategories()) {
            if (recipes.getRecipeCategory(uid) == null) {
                ContentLog.LOGGER.error("Disabled file hides JEI category '{}', which no loaded plugin registers, so it is shown as it was", uid);
                continue;
            }
            recipes.hideRecipeCategory(uid);
            hidden++;
        }
        if (hidden > 0) { Summary.info("hidden_categories", "Hid " + hidden + " JEI recipe category/categories"); }
    }
}
