package mctmods.resourcedatapackloader.mixin.entity.projectile;

import mctmods.resourcedatapackloader.common.environment.ContentPhysics;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityArrow.class) public abstract class MixinEntityArrow extends Entity {
    public MixinEntityArrow(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.05000000074505806D), require = 0, expect = 0) private double rdpl$worldGravity(double base) { return ContentPhysics.arrowGravity(world, base); }
}
