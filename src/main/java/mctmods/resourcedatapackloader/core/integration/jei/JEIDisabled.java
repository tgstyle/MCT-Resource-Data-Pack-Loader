package mctmods.resourcedatapackloader.core.integration.jei;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.util.ContentDisabled;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused") @JeiPlugin public final class JEIDisabled implements IModPlugin {
    private static final ResourceLocation UID = ResourceDataPackLoader.id("disabled");

    @Override @Nonnull public ResourceLocation getPluginUid() { return UID; }

    @Override public void onRuntimeAvailable(IJeiRuntime runtime) {
        IIngredientManager ingredients = runtime.getIngredientManager();
        List<ItemStack> hidden = ingredients.getAllIngredients(VanillaTypes.ITEM_STACK).stream().filter(stack -> ContentDisabled.disabled(stack) || ContentDisabled.hidden(stack)).toList();
        if (!hidden.isEmpty()) { ingredients.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hidden); }
        hideCategories(runtime.getRecipeManager());
    }

    private static void hideCategories(IRecipeManager recipes) {
        int count = 0;
        for (String uid : ContentDisabled.hiddenCategories()) {
            ResourceLocation id = ResourceLocation.tryParse(uid);
            Optional<RecipeType<?>> type = id == null ? Optional.empty() : recipes.getRecipeType(id);
            if (type.isEmpty()) {
                ContentLog.LOGGER.error("Disabled file hides JEI category '{}', which no loaded plugin registers, so it is shown as it was", uid);
                continue;
            }
            recipes.hideRecipeCategory(type.get());
            count++;
        }
        if (count > 0) { Summary.info("hidden_categories", "Hid " + count + " JEI recipe category/categories"); }
    }
}
