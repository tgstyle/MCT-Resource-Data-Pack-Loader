package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.content.entity.RocketCargo;

import micdoodle8.mods.galacticraft.api.entity.ICargoEntity.EnumCargoLoadingState;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityAutoRocket.class, remap = false) public class MixinEntityAutoRocket {
    @Inject(method = "addCargo(Lnet/minecraft/item/ItemStack;Z)Lmicdoodle8/mods/galacticraft/api/entity/ICargoEntity$EnumCargoLoadingState;", at = @At("HEAD"), cancellable = true)
    private void rdpl$packCargoFilter(ItemStack stack, boolean doAdd, CallbackInfoReturnable<EnumCargoLoadingState> cir) {
        if (RocketCargo.refuses(this, stack)) { cir.setReturnValue(EnumCargoLoadingState.FULL); }
    }

    @Inject(method = "igniteWithResult()Z", at = @At("HEAD"), cancellable = true)
    private void rdpl$packRequiredPayload(CallbackInfoReturnable<Boolean> cir) {
        if (RocketCargo.grounded((Entity) (Object) this)) { cir.setReturnValue(false); }
    }
}
