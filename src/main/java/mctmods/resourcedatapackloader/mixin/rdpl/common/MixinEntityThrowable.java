package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityThrowable.class) public abstract class MixinEntityThrowable extends Entity {
    public MixinEntityThrowable(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "getGravityVelocity", constant = @Constant(floatValue = 0.03F))
    private float rdpl$worldGravity(float base) { return (float) ContentPhysics.gravity(world, base); }
}
