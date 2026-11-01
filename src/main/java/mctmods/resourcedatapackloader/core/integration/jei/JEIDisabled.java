package mctmods.resourcedatapackloader.core.integration.jei;

import mctmods.resourcedatapackloader.common.util.ContentDisabled;

import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;

@SuppressWarnings("unused") @JEIPlugin public final class JEIDisabled implements IModPlugin {
    @Override public void register(@Nonnull IModRegistry registry) {
        if (!ContentDisabled.any()) { return; }
        IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        ContentDisabled.eachStack(blacklist::addIngredientToBlacklist);
    }
}
