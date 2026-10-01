package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.content.entity.RocketCargo;

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityTieredRocket.class, remap = false) public class MixinEntityTieredRocket {
    @Inject(method = "igniteCheckingCooldown()V", at = @At("HEAD"), cancellable = true)
    private void rdpl$packRequiredPayload(CallbackInfo ci) {
        if (RocketCargo.grounded((Entity) (Object) this)) { ci.cancel(); }
    }
}
