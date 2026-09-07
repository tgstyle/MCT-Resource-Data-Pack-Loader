package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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

    @Inject(method = "getRiddenInput", at = @At("RETURN"), cancellable = true)
    private void rdpl$riddenInput(Player player, Vec3 travelVector, CallbackInfoReturnable<Vec3> cir) {
        if (!ContentEntities.steerable((LivingEntity) (Object) this)) { return; }
        float forward = player.zza;
        if (forward <= 0.0F) { forward *= 0.25F; }
        cir.setReturnValue(new Vec3(player.xxa * 0.5F, 0.0D, forward));
    }

    @Inject(method = "getRiddenSpeed", at = @At("RETURN"), cancellable = true)
    private void rdpl$riddenSpeed(Player player, CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (ContentEntities.steerable(self)) { cir.setReturnValue((float) self.getAttributeValue(Attributes.MOVEMENT_SPEED)); }
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void rdpl$angrySize(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        float factor = ContentEntities.angryFactor((LivingEntity) (Object) this);
        if (factor != 1.0F) { cir.setReturnValue(cir.getReturnValue().scale(factor)); }
    }
}
