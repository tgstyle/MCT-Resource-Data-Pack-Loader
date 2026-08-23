package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityItem.class) public abstract class MixinEntityItem extends Entity {
    public MixinEntityItem(World worldIn) { super(worldIn); }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.03999999910593033D), require = 0, expect = 0)
    private double rdpl$worldGravity(double base) { return ContentPhysics.gravity(world, base); }
}
