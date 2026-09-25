package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.util.ContentDisabled;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import javax.annotation.Nonnull;

@SuppressWarnings("unused") @JeiPlugin public final class JEIDisabled implements IModPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, "disabled");

    @Override @Nonnull public ResourceLocation getPluginUid() { return UID; }

    @Override public void onRuntimeAvailable(IJeiRuntime runtime) {
        IIngredientManager ingredients = runtime.getIngredientManager();
        List<ItemStack> hidden = ingredients.getAllIngredients(VanillaTypes.ITEM_STACK).stream().filter(ContentDisabled::disabled).toList();
        if (!hidden.isEmpty()) { ingredients.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hidden); }
    }
}
