package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerRepair.class) public abstract class MixinContainerRepair {
    @Inject(method = "transferStackInSlot", at = @At("HEAD"), cancellable = true) private void rdpl$onlyWorkPaidFor(EntityPlayer player, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (index == 2 && !((Container) (Object) this).getSlot(2).canTakeStack(player)) { cir.setReturnValue(ItemStack.EMPTY); }
    }
}
