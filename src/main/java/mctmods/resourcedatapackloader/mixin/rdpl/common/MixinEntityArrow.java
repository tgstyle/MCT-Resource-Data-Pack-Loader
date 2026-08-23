package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityArrow.class) public abstract class MixinEntityArrow extends Entity {
    public MixinEntityArrow(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.05000000074505806D), require = 0, expect = 0)
    private double rdpl$worldGravity(double base) { return ContentPhysics.gravity(world, base); }
}
