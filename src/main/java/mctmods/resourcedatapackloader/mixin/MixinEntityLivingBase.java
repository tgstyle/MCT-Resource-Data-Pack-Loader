package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {
    @Inject(method = "jump", at = @At("RETURN"))
    private void rdpl$jumpHigher(CallbackInfo ci) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        float multiplier = ContentEntities.jumpMultiplier(self);
        if (multiplier == 1.0F) { return; }

        self.motionY *= multiplier;
    }

    @ModifyVariable(method = "fall", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private float rdpl$fallDamage(float multiplier) { return ContentEntities.fallDamage((EntityLivingBase) (Object) this, multiplier); }

    @Inject(method = "canBreatheUnderwater", at = @At("HEAD"), cancellable = true)
    private void rdpl$breathe(CallbackInfoReturnable<Boolean> cir) {
        if (!ContentEntities.breathesUnderwater((EntityLivingBase) (Object) this)) { return; }

        cir.setReturnValue(Boolean.TRUE);
    }

    @Inject(method = "getSoundVolume", at = @At("RETURN"), cancellable = true)
    private void rdpl$volume(CallbackInfoReturnable<Float> cir) { cir.setReturnValue(ContentEntities.sound((EntityLivingBase) (Object) this, cir.getReturnValueF(), false)); }

    @Inject(method = "getSoundPitch", at = @At("RETURN"), cancellable = true)
    private void rdpl$pitch(CallbackInfoReturnable<Float> cir) { cir.setReturnValue(ContentEntities.sound((EntityLivingBase) (Object) this, cir.getReturnValueF(), true)); }

    @Inject(method = "getWaterSlowDown", at = @At("RETURN"), cancellable = true)
    private void rdpl$waterSpeed(CallbackInfoReturnable<Float> cir) { cir.setReturnValue(ContentEntities.waterSlowdown((EntityLivingBase) (Object) this, cir.getReturnValueF())); }

    @Inject(method = "getCreatureAttribute", at = @At("RETURN"), cancellable = true)
    private void rdpl$creatureAttribute(CallbackInfoReturnable<EnumCreatureAttribute> cir) {
        EnumCreatureAttribute wanted = ContentEntities.creatureAttribute((EntityLivingBase) (Object) this);
        if (wanted == null) { return; }

        cir.setReturnValue(wanted);
    }

    @Inject(method = "getExperiencePoints", at = @At("RETURN"), cancellable = true)
    private void rdpl$experience(EntityPlayer player, CallbackInfoReturnable<Integer> cir) { cir.setReturnValue(ContentEntities.experience((EntityLivingBase) (Object) this, cir.getReturnValueI())); }
}
