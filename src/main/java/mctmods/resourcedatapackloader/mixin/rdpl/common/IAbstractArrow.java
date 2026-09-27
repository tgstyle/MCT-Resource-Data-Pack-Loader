package mctmods.resourcedatapackloader.mixin.rdpl.common;

import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractArrow.class) public interface IAbstractArrow {
    @Accessor("baseDamage") double rdpl$baseDamage();
}
