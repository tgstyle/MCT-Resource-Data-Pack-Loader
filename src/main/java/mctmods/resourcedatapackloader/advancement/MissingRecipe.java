package mctmods.resourcedatapackloader.advancement;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public final class MissingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private final ResourceLocation missing;

    public MissingRecipe(ResourceLocation missing) { this.missing = missing; }

    @Override public String toString() { return "MissingRecipe[" + missing + "]"; }

    @Override public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) { return false; }

    @Override @Nonnull public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) { return ItemStack.EMPTY; }

    @Override public boolean canFit(int width, int height) { return false; }

    @Override @Nonnull public ItemStack getRecipeOutput() { return ItemStack.EMPTY; }

    @Override public boolean isDynamic() { return true; }
}
