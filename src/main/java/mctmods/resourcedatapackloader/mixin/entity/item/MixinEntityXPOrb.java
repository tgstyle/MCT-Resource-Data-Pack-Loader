package mctmods.resourcedatapackloader.mixin.entity.item;

import mctmods.resourcedatapackloader.common.environment.ContentPhysics;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityXPOrb.class) public abstract class MixinEntityXPOrb extends Entity {
    public MixinEntityXPOrb(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.029999999329447746D)) private double rdpl$worldGravity(double base) { return ContentPhysics.gravity(world, base); }
}
