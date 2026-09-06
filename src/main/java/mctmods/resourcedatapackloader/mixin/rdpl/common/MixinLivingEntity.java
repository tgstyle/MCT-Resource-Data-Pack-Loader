package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class) public abstract class MixinLivingEntity {
    @Unique private EntityVariantDef rdpl$def() { return ContentEntities.def((LivingEntity) (Object) this); }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void rdpl$hurtSound(DamageSource damageSource, CallbackInfoReturnable<SoundEvent> cir) {
        SoundEvent sound = ContentEntities.sound((LivingEntity) (Object) this, ContentEntities.HURT);
        if (sound != null) { cir.setReturnValue(sound); }
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    private void rdpl$deathSound(CallbackInfoReturnable<SoundEvent> cir) {
        SoundEvent sound = ContentEntities.sound((LivingEntity) (Object) this, ContentEntities.DEATH);
        if (sound != null) { cir.setReturnValue(sound); }
    }

    @Inject(method = "getSoundVolume", at = @At("RETURN"), cancellable = true)
    private void rdpl$soundVolume(CallbackInfoReturnable<Float> cir) {
        EntityVariantDef def = rdpl$def();
        if (def != null && def.sounds().volume() != 1.0F) { cir.setReturnValue(cir.getReturnValueF() * def.sounds().volume()); }
    }

    @Inject(method = "getVoicePitch", at = @At("RETURN"), cancellable = true)
    private void rdpl$voicePitch(CallbackInfoReturnable<Float> cir) {
        EntityVariantDef def = rdpl$def();
        if (def != null && def.sounds().pitch() != 1.0F) { cir.setReturnValue(cir.getReturnValueF() * def.sounds().pitch()); }
    }

    @Inject(method = "getJumpPower()F", at = @At("RETURN"), cancellable = true)
    private void rdpl$jumpPower(CallbackInfoReturnable<Float> cir) {
        EntityVariantDef def = rdpl$def();
        if (def != null && def.physics().jumpMultiplier() != 1.0F) { cir.setReturnValue(cir.getReturnValueF() * def.physics().jumpMultiplier()); }
    }

    @Inject(method = "getWaterSlowDown", at = @At("RETURN"), cancellable = true)
    private void rdpl$waterSlowDown(CallbackInfoReturnable<Float> cir) {
        EntityVariantDef def = rdpl$def();
        if (def != null) { cir.setReturnValue(def.physics().waterSlowdown()); }
    }

    @Inject(method = "getMobType", at = @At("HEAD"), cancellable = true)
    private void rdpl$mobType(CallbackInfoReturnable<MobType> cir) {
        MobType type = ContentEntities.mobType((LivingEntity) (Object) this);
        if (type != null) { cir.setReturnValue(type); }
    }
}
