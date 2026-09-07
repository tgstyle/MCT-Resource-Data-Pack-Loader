package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.world.entity.projectile.ThrowableProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrowableProjectile.class) public abstract class MixinThrowableProjectile {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrowableProjectile;getGravity()F"))
    private float rdpl$worldGravity(ThrowableProjectile self) { return ContentPhysics.scaledFall(self, ((IThrowableProjectile) self).rdpl$gravity()); }
}
