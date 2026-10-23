package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.recipe.RecipeLocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Container.class) public abstract class MixinContainerCrafting {
    @Shadow public int windowId;

    @Inject(method = "slotChangedCraftingGrid", at = @At("RETURN"), require = 1) private void rdpl$lockRecipes(World p_192389_1_, EntityPlayer p_192389_2_, InventoryCrafting p_192389_3_, InventoryCraftResult p_192389_4_, CallbackInfo ci) {
        if (!(p_192389_2_ instanceof EntityPlayerMP)) { return; }
        ItemStack output = p_192389_4_.getStackInSlot(0);
        if (output.isEmpty()) { return; }
        IRecipe recipe = p_192389_4_.getRecipeUsed();
        ResourceLocation id = recipe == null ? null : recipe.getRegistryName();
        if (id == null || !RecipeLocks.barred(p_192389_2_, id, output)) { return; }
        p_192389_4_.setInventorySlotContents(0, ItemStack.EMPTY);
        ((EntityPlayerMP) p_192389_2_).connection.sendPacket(new SPacketSetSlot(windowId, 0, ItemStack.EMPTY));
    }
}
