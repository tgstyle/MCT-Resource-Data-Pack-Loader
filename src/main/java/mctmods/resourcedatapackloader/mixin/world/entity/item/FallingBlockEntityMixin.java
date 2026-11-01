package mctmods.resourcedatapackloader.mixin.world.entity.item;

import mctmods.resourcedatapackloader.common.environment.ContentPhysics;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FallingBlockEntity.class) public abstract class FallingBlockEntityMixin {
    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = -0.04D)) private double rdpl$worldGravity(double vanilla) { return ContentPhysics.scaledFall((Entity) (Object) this, vanilla); }
}
