package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Boat.class) public abstract class MixinBoat {
    @ModifyConstant(method = "floatBoat", constant = @Constant(doubleValue = -0.03999999910593033D))
    private double rdpl$worldGravity(double vanilla) { return ContentPhysics.scaledFall((Entity) (Object) this, vanilla); }
}
