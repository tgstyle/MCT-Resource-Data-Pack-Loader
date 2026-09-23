package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentAnvils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.inventory.ContainerRepair$2") public abstract class MixinContainerRepairOutput {
    @Unique private ItemStack rdpl$leftKept = ItemStack.EMPTY;

    @Inject(method = "onTake", at = @At("HEAD")) private void rdpl$noteWhatStays(EntityPlayer thePlayer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) { rdpl$leftKept = ContentAnvils.leftAfterWork(thePlayer, stack); }

    @ModifyArg(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/IInventory;setInventorySlotContents(ILnet/minecraft/item/ItemStack;)V", ordinal = 0), index = 1) private ItemStack rdpl$keepWhatStays(ItemStack emptied) { return rdpl$leftKept; }

    @Inject(method = "onTake", at = @At("RETURN")) private void rdpl$offerTheNext(EntityPlayer thePlayer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (!rdpl$leftKept.isEmpty() && thePlayer.openContainer instanceof ContainerRepair) { ((ContainerRepair) thePlayer.openContainer).updateRepairOutput(); }
    }
}
