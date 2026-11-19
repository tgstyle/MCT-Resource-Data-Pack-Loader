package mctmods.resourcedatapackloader.common.recipes;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapelessRecipe;

final class TransferShapeless extends ShapelessRecipe {
    private final List<RecipeTransfer.Transfer> transfers;

    TransferShapeless(ShapelessRecipe inner, HolderLookup.Provider registries, List<RecipeTransfer.Transfer> transfers) {
        super(inner.getGroup(), inner.category(), inner.getResultItem(registries), inner.getIngredients());
        this.transfers = transfers;
    }

    @Override @Nonnull public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider registries) {
        ItemStack result = super.assemble(input, registries);
        for (RecipeTransfer.Transfer transfer : transfers) { transfer.carry(input, result); }
        return result;
    }
}
