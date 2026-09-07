package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class) public abstract class MixinEntity {
    @Inject(method = "getGravity", at = @At("RETURN"), cancellable = true)
    private void rdpl$worldGravity(CallbackInfoReturnable<Double> cir) {
        double base = cir.getReturnValueD();
        if (base == 0.0D) { return; }
        double factor = ContentPhysics.gravity(((Entity) (Object) this).level());
        if (factor != 1.0D) { cir.setReturnValue(base * factor); }
    }
}
