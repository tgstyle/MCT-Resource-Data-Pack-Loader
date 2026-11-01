package mctmods.resourcedatapackloader.mixin.entity.item;

import mctmods.resourcedatapackloader.common.environment.ContentPhysics;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityTNTPrimed.class) public abstract class MixinEntityTNTPrimed extends Entity {
    public MixinEntityTNTPrimed(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.03999999910593033D)) private double rdpl$worldGravity(double base) { return ContentPhysics.gravity(world, base); }
}
