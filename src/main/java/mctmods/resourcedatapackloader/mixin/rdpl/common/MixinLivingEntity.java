package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

@Mixin(LivingEntity.class) public abstract class MixinLivingEntity {
    @Shadow protected abstract SoundEvent getHurtSound(DamageSource source);
    @Shadow protected abstract SoundEvent getDeathSound();

    @Unique private EntityVariantDef rdpl$def() { return ContentEntities.def(LivingEntity.class.cast(this)); }

    @Redirect(method = "playHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/sounds/SoundEvent;"))
    private SoundEvent rdpl$hurtSound(LivingEntity self, DamageSource source) {
        SoundEvent sound = ContentEntities.sound(self, ContentEntities.HURT);
        return sound != null ? sound : getHurtSound(source);
    }

    @Redirect(method = { "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", "handleEntityEvent(B)V" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDeathSound()Lnet/minecraft/sounds/SoundEvent;"))
    private SoundEvent rdpl$deathSound(LivingEntity self) {
        SoundEvent sound = ContentEntities.sound(self, ContentEntities.DEATH);
        return sound != null ? sound : getDeathSound();
    }

    @Inject(method = "getSoundVolume()F", at = @At("RETURN"), cancellable = true)
    private void rdpl$soundVolume(CallbackInfoReturnable<Float> cir) {
        EntityVariantDef def = rdpl$def();
        if (def != null && def.sounds().volume() != 1.0F) { cir.setReturnValue(cir.getReturnValueF() * def.sounds().volume()); }
    }

    @Inject(method = "getVoicePitch()F", at = @At("RETURN"), cancellable = true)
    private void rdpl$voicePitch(CallbackInfoReturnable<Float> cir) {
        EntityVariantDef def = rdpl$def();
        if (def != null && def.sounds().pitch() != 1.0F) { cir.setReturnValue(cir.getReturnValueF() * def.sounds().pitch()); }
    }

    @Inject(method = "jumpFromGround()V", at = @At("RETURN"))
    private void rdpl$jumpHigher(CallbackInfo ci) {
        EntityVariantDef def = rdpl$def();
        if (def == null || def.physics().jumpMultiplier() == 1.0F) { return; }
        LivingEntity self = LivingEntity.class.cast(this);
        Vec3 motion = self.getDeltaMovement();
        self.setDeltaMovement(motion.x, motion.y * def.physics().jumpMultiplier(), motion.z);
    }

    @Inject(method = "getWaterSlowDown()F", at = @At("RETURN"), cancellable = true)
    private void rdpl$waterSlowDown(CallbackInfoReturnable<Float> cir) {
        EntityVariantDef def = rdpl$def();
        if (def != null) { cir.setReturnValue(def.physics().waterSlowdown()); }
    }

    @Inject(method = "getRiddenInput(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", at = @At("RETURN"), cancellable = true)
    private void rdpl$riddenInput(Player controller, Vec3 selfInput, CallbackInfoReturnable<Vec3> cir) {
        if (!ContentEntities.steerable(LivingEntity.class.cast(this))) { return; }
        float forward = controller.zza;
        if (forward <= 0.0F) { forward *= 0.25F; }
        cir.setReturnValue(new Vec3(controller.xxa * 0.5F, 0.0D, forward));
    }

    @Inject(method = "getRiddenSpeed(Lnet/minecraft/world/entity/player/Player;)F", at = @At("RETURN"), cancellable = true)
    private void rdpl$riddenSpeed(Player controller, CallbackInfoReturnable<Float> cir) {
        LivingEntity self = LivingEntity.class.cast(this);
        if (ContentEntities.steerable(self)) { cir.setReturnValue((float) self.getAttributeValue(Attributes.MOVEMENT_SPEED)); }
    }

    @Inject(method = "onClimbable()Z", at = @At("RETURN"), cancellable = true)
    private void rdpl$climbs(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = LivingEntity.class.cast(this);
        Boolean wanted = ContentEntities.climbs(self);
        if (wanted != null) { cir.setReturnValue(wanted && (cir.getReturnValueZ() || self.horizontalCollision)); }
    }

    @Redirect(method = "aiStep()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;jumpInFluid(Lnet/neoforged/neoforge/fluids/FluidType;)V", remap = false))
    private void rdpl$sink(LivingEntity self, FluidType type) {
        if (type == NeoForgeMod.WATER_TYPE.value() && ContentEntities.sinks(self)) { return; }
        self.jumpInFluid(type);
    }
}
