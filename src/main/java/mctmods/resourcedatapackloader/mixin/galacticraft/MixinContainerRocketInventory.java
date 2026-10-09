package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.content.entity.RocketSlot;

import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType;
import micdoodle8.mods.galacticraft.core.inventory.ContainerRocketInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ContainerRocketInventory.class, remap = false) public abstract class MixinContainerRocketInventory extends Container {
    @Shadow @Final private IInventory spaceshipInv;

    @Inject(method = "<init>", at = @At("RETURN")) private void rdpl$packCargoSlots(IInventory par1IInventory, IInventory par2IInventory, EnumRocketType rocketType, EntityPlayer player, CallbackInfo ci) { RocketSlot.filter(inventorySlots, par2IInventory); }

    @Inject(method = "transferStackInSlot", at = @At("HEAD"), cancellable = true, remap = true) private void rdpl$packCargoQuickMove(EntityPlayer entityPlayer, int par2, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack moved = RocketSlot.moving(inventorySlots, spaceshipInv, par2);
        if (moved == null) { return; }
        if (!moved.isEmpty()) {
            mergeItemStack(moved, 0, RocketSlot.cargo(inventorySlots), false);
            RocketSlot.settle(inventorySlots.get(par2), moved);
        }
        cir.setReturnValue(ItemStack.EMPTY);
    }
}
