package mctmods.resourcedatapackloader.common.recipes;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;

final class TransferShaped extends ShapedRecipe {
    private final List<RecipeTransfer.Transfer> transfers;

    TransferShaped(ShapedRecipe inner, List<RecipeTransfer.Transfer> transfers) {
        super(new Recipe.CommonInfo(inner.showNotification()), new CraftingRecipe.CraftingBookInfo(inner.category(), inner.group()), inner.pattern, inner.result);
        this.transfers = transfers;
    }

    @Override @Nonnull public ItemStack assemble(@Nonnull CraftingInput input) {
        ItemStack result = super.assemble(input);
        for (RecipeTransfer.Transfer transfer : transfers) { transfer.carry(input, result); }
        return result;
    }
}
