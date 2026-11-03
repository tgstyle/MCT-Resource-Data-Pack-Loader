package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.pack.loading.ContentOwners;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public final class CompressedRecipes {
    private static final int GRID = 9;

    private CompressedRecipes() {}

    public static void register(IForgeRegistry<IRecipe> registry) {
        int added = 0;
        for (CompressedSet set : ContentCompressables.sets()) {
            if (!set.def.recipes) { continue; }
            for (int layer = 1; layer <= set.layers; layer++) {
                ItemStack lower = set.stack(layer - 1);
                ItemStack upper = set.stack(layer);
                if (lower.isEmpty() || upper.isEmpty()) {
                    ContentLog.LOGGER.error("The compressable {} has no registered item for layer {} or the layer below it, so its recipes stop there", set.def.key, layer);
                    break;
                }
                String namespace = set.def.key.getNamespace();
                ResourceLocation packId = new ResourceLocation(namespace, set.name + "_compress_" + layer);
                ResourceLocation unpackId = new ResourceLocation(namespace, set.name + "_decompress_" + layer);
                if (registry.containsKey(packId) || registry.containsKey(unpackId)) {
                    ContentLog.LOGGER.error("A recipe named {} or {} already exists, so the compressable {} gets no recipes for layer {}", packId, unpackId, set.def.key, layer);
                    continue;
                }
                NonNullList<Ingredient> nine = NonNullList.withSize(GRID, Ingredient.fromStacks(lower.copy()));
                NonNullList<Ingredient> one = NonNullList.withSize(1, Ingredient.fromStacks(upper.copy()));
                ItemStack nineBack = lower.copy();
                nineBack.setCount(GRID);
                IRecipe pack = new ShapedRecipes("", 3, 3, nine, upper.copy());
                IRecipe unpack = new ShapelessRecipes("", nineBack, one);
                ContentOwners.as(namespace, () -> {
                    registry.register(pack.setRegistryName(packId));
                    registry.register(unpack.setRegistryName(unpackId));
                });
                added += 2;
            }
        }
        if (added > 0) { Summary.info("recipes.compressed", "Registered " + added + " compressed block recipe(s)"); }
    }
}
