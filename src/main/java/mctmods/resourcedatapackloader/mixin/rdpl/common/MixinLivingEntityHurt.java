package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class) public abstract class MixinLivingEntityHurt {
    @ModifyConstant(method = "hurt", constant = @Constant(floatValue = 10.0F, ordinal = 0))
    private float rdpl$halfWay(float was) { return ContentEntities.hurtResistance((LivingEntity) (Object) this, 20) / 2.0F; }
}
