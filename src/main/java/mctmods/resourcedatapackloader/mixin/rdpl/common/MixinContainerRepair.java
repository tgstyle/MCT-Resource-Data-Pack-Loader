package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.AnvilCosts;
import mctmods.resourcedatapackloader.content.AnvilRules;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerRepair.class) public abstract class MixinContainerRepair extends Container {
    @Shadow @Final private EntityPlayer player;
    @Shadow @Final private IInventory outputSlot;
    @Shadow public int maximumCost;

    @Inject(method = "updateRepairOutput", at = @At("HEAD")) private void rdpl$noteWorker(CallbackInfo ci) { AnvilRules.begin(player); }

    @Inject(method = "updateRepairOutput", at = @At("RETURN")) private void rdpl$refuseByRule(CallbackInfo ci) {
        Integer refused = AnvilRules.end();
        if (refused == null) { return; }
        outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
        maximumCost = refused;
    }

    @ModifyConstant(method = "updateRepairOutput", constant = @Constant(intValue = 40)) private int rdpl$tooExpensive(int vanilla) { return AnvilCosts.cap(player.world.isRemote); }

    @ModifyConstant(method = "updateRepairOutput", constant = @Constant(intValue = 39)) private int rdpl$renamingStaysBelow(int vanilla) { return AnvilCosts.cap(player.world.isRemote) - 1; }

    @Inject(method = "transferStackInSlot", at = @At("HEAD"), cancellable = true) private void rdpl$onlyWorkPaidFor(EntityPlayer playerIn, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (index == 2 && !getSlot(2).canTakeStack(playerIn)) { cir.setReturnValue(ItemStack.EMPTY); }
    }
}
