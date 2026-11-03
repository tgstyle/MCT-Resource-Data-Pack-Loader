package mctmods.resourcedatapackloader.mixin.inventory;

import mctmods.resourcedatapackloader.common.weights.WeightEnforce;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class) public abstract class MixinSlot {
    @Inject(method = "canTakeStack", at = @At("HEAD"), cancellable = true) private void rdpl$weighTake(EntityPlayer playerIn, CallbackInfoReturnable<Boolean> cir) {
        if (WeightEnforce.refusesTake((Slot) (Object) this, playerIn)) { cir.setReturnValue(false); }
    }
}
