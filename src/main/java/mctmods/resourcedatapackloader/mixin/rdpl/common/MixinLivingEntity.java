package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.def.EntityVariantDef;
import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.worldgen.ContentPhysics;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

@Mixin(LivingEntity.class) public abstract class MixinLivingEntity {
    @Shadow protected abstract SoundEvent getHurtSound(DamageSource damageSource);
    @Shadow protected abstract SoundEvent getDeathSound();

    @Unique private EntityVariantDef rdpl$def() { return ContentEntities.def((LivingEntity) (Object) this); }

    @Redirect(method = "playHurtSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/sounds/SoundEvent;")) private SoundEvent rdpl$hurtSound(LivingEntity self, DamageSource damageSource) { return ContentEntities.sound(self, ContentEntities.HURT, () -> getHurtSound(damageSource)); }

    @Redirect(method = { "hurt", "handleEntityEvent" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDeathSound()Lnet/minecraft/sounds/SoundEvent;")) private SoundEvent rdpl$deathSound(LivingEntity self) { return ContentEntities.sound(self, ContentEntities.DEATH, this::getDeathSound); }

    @Redirect(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false)) private void rdpl$namedDeathLog(Logger logger, String format, Object entity, Object message) { if (rdpl$def() == null) { logger.info(format, entity, message); } }

    @Inject(method = "getSoundVolume", at = @At("RETURN"), cancellable = true) private void rdpl$soundVolume(CallbackInfoReturnable<Float> cir) {
        EntityVariantDef def = rdpl$def();
        if (def != null && def.sounds().volume() != 1.0F) { cir.setReturnValue(cir.getReturnValueF() * def.sounds().volume()); }
    }

    @Inject(method = "getVoicePitch", at = @At("RETURN"), cancellable = true) private void rdpl$voicePitch(CallbackInfoReturnable<Float> cir) {
        EntityVariantDef def = rdpl$def();
        if (def != null && def.sounds().pitch() != 1.0F) { cir.setReturnValue(cir.getReturnValueF() * def.sounds().pitch()); }
    }

    @Inject(method = "jumpFromGround", at = @At("RETURN")) private void rdpl$jumpHigher(CallbackInfo ci) {
        EntityVariantDef def = rdpl$def();
        if (def == null || def.physics().jumpMultiplier() == 1.0F) { return; }
        LivingEntity self = (LivingEntity) (Object) this;
        Vec3 motion = self.getDeltaMovement();
        self.setDeltaMovement(motion.x, motion.y * def.physics().jumpMultiplier(), motion.z);
    }

    @Inject(method = "getWaterSlowDown", at = @At("RETURN"), cancellable = true) private void rdpl$waterSlowDown(CallbackInfoReturnable<Float> cir) {
        EntityVariantDef def = rdpl$def();
        if (def != null) { cir.setReturnValue(def.physics().waterSlowdown()); }
    }

    @Inject(method = "getMobType", at = @At("HEAD"), cancellable = true) private void rdpl$mobType(CallbackInfoReturnable<MobType> cir) {
        MobType type = ContentEntities.mobType((LivingEntity) (Object) this);
        if (type != null) { cir.setReturnValue(type); }
    }

    @Inject(method = "getRiddenInput", at = @At("RETURN"), cancellable = true) private void rdpl$riddenInput(Player player, Vec3 travelVector, CallbackInfoReturnable<Vec3> cir) {
        if (!ContentEntities.steerable((LivingEntity) (Object) this)) { return; }
        float forward = player.zza;
        if (forward <= 0.0F) { forward *= 0.25F; }
        cir.setReturnValue(new Vec3(player.xxa * 0.5F, 0.0D, forward));
    }

    @Inject(method = "getRiddenSpeed", at = @At("RETURN"), cancellable = true) private void rdpl$riddenSpeed(Player player, CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (ContentEntities.steerable(self)) { cir.setReturnValue((float) self.getAttributeValue(Attributes.MOVEMENT_SPEED)); }
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true) private void rdpl$angrySize(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        float factor = ContentEntities.angryFactor((LivingEntity) (Object) this);
        if (factor != 1.0F) { cir.setReturnValue(cir.getReturnValue().scale(factor)); }
    }

    @Inject(method = "onClimbable", at = @At("RETURN"), cancellable = true) private void rdpl$climbs(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        Boolean wanted = ContentEntities.climbs(self);
        if (wanted != null) { cir.setReturnValue(wanted && (cir.getReturnValueZ() || self.horizontalCollision)); }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;getValue()D", ordinal = 0)) private double rdpl$openAirGravity(AttributeInstance gravity) { return ContentPhysics.openAirGravity((LivingEntity) (Object) this, gravity); }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;jumpInFluid(Lnet/minecraftforge/fluids/FluidType;)V", remap = false)) private void rdpl$sink(LivingEntity self, FluidType type) {
        if (type == ForgeMod.WATER_TYPE.get() && ContentEntities.sinks(self)) { return; }
        self.jumpInFluid(type);
    }
}
