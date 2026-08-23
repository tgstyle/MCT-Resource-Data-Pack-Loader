package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityTNTPrimed.class) public abstract class MixinEntityTNTPrimed extends Entity {
    public MixinEntityTNTPrimed(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.03999999910593033D))
    private double rdpl$worldGravity(double base) { return ContentPhysics.gravity(world, base); }
}
