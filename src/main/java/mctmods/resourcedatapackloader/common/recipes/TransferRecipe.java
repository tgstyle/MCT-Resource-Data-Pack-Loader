package mctmods.resourcedatapackloader.common.recipes;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

final class TransferRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private final IRecipe inner;
    private final List<RecipeTransfer.Transfer> transfers;

    TransferRecipe(IRecipe inner, List<RecipeTransfer.Transfer> transfers) {
        this.inner = inner;
        this.transfers = transfers;
    }

    @Override public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) { return inner.matches(inv, world); }

    @Override @Nonnull public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
        ItemStack result = inner.getCraftingResult(inv);
        for (RecipeTransfer.Transfer transfer : transfers) { transfer.carry(inv, result); }
        return result;
    }

    @Override public boolean canFit(int width, int height) { return inner.canFit(width, height); }

    @Override @Nonnull public ItemStack getRecipeOutput() { return inner.getRecipeOutput(); }

    @Override @Nonnull public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) { return inner.getRemainingItems(inv); }

    @Override @Nonnull public NonNullList<Ingredient> getIngredients() { return inner.getIngredients(); }

    @Override public boolean isDynamic() { return inner.isDynamic(); }

    @Override @Nonnull public String getGroup() { return inner.getGroup(); }
}
