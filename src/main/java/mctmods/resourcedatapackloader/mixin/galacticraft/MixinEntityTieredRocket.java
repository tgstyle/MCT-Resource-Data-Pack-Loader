package mctmods.resourcedatapackloader.mixin.galacticraft;

import mctmods.resourcedatapackloader.content.entity.RocketCargo;
import mctmods.resourcedatapackloader.content.gate.ContentGates;

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityTieredRocket.class, remap = false) public class MixinEntityTieredRocket {
    @Inject(method = "igniteCheckingCooldown()V", at = @At("HEAD"), cancellable = true) private void rdpl$packRequiredPayload(CallbackInfo ci) {
        if (RocketCargo.grounded((Entity) (Object) this)) { ci.cancel(); }
    }

    @Redirect(method = "onReachAtmosphere()V", at = @At(value = "INVOKE", target = "Lmicdoodle8/mods/galacticraft/core/util/WorldUtil;getProviderForDimensionServer(I)Lnet/minecraft/world/WorldProvider;")) private WorldProvider rdpl$gatedLaunch(int id) {
        for (Entity passenger : ((Entity) (Object) this).getPassengers()) {
            if (passenger instanceof EntityPlayerMP && ContentGates.barsDimension((EntityPlayerMP) passenger, id)) { return null; }
        }
        return WorldUtil.getProviderForDimensionServer(id);
    }
}
