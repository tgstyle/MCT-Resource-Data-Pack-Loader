package mctmods.resourcedatapackloader.mixin.world.inventory;

import mctmods.resourcedatapackloader.common.weights.WeightEnforce;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class) public abstract class SlotMixin {
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true) private void rdpl$weighTake(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (WeightEnforce.refusesTake((Slot) (Object) this, player)) { cir.setReturnValue(false); }
    }
}
