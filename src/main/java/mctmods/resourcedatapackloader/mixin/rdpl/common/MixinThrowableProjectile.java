package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ThrowableProjectile.class) public abstract class MixinThrowableProjectile {
    @ModifyConstant(method = "getGravity", constant = @Constant(floatValue = 0.03F))
    private float rdpl$worldGravity(float vanilla) { return ContentPhysics.scaledFall((Entity) (Object) this, vanilla); }
}
