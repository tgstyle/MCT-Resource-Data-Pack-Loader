package mctmods.resourcedatapackloader.common.recipes;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;

final class TransferShaped extends ShapedRecipe {
    private final List<RecipeTransfer.Transfer> transfers;

    TransferShaped(ShapedRecipe inner, HolderLookup.Provider registries, List<RecipeTransfer.Transfer> transfers) {
        super(inner.getGroup(), inner.category(), inner.pattern, inner.getResultItem(registries), inner.showNotification());
        this.transfers = transfers;
    }

    @Override @Nonnull public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider registries) {
        ItemStack result = super.assemble(input, registries);
        for (RecipeTransfer.Transfer transfer : transfers) { transfer.carry(input, result); }
        return result;
    }
}
