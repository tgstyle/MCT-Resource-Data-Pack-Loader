package mctmods.resourcedatapackloader.common.recipes;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

final class TransferShaped extends ShapedRecipe {
    private final List<Transfer> transfers;

    TransferShaped(ShapedRecipe inner, List<Transfer> transfers) {
        super(inner.getId(), inner.getGroup(), inner.category(), inner.getWidth(), inner.getHeight(), inner.getIngredients(), inner.getResultItem(RegistryAccess.EMPTY), inner.showNotification());
        this.transfers = transfers;
    }

    @Override @Nonnull public ItemStack assemble(@Nonnull CraftingContainer container, @Nonnull RegistryAccess access) {
        ItemStack result = super.assemble(container, access);
        Transfer.carryAll(transfers, container, result);
        return result;
    }
}
