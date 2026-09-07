package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.LlamaSpit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LlamaSpit.class) public abstract class MixinLlamaSpit {
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = -0.05999999865889549D))
    private double rdpl$worldGravity(double vanilla) { return ContentPhysics.scaledFall((Entity) (Object) this, vanilla); }
}
