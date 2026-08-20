package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(EntityLivingBase.class) public abstract class MixinEntityLivingBase extends Entity {
    @Shadow protected abstract SoundEvent getHurtSound(DamageSource damageSourceIn);
    @Shadow protected abstract int getExperiencePoints(EntityPlayer player);
    @Shadow protected abstract float getWaterSlowDown();
    @Shadow protected abstract SoundEvent getDeathSound();

    @Inject(method = "jump", at = @At("RETURN")) private void rdpl$jumpHigher(CallbackInfo ci) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        float multiplier = ContentEntities.jumpMultiplier(self);
        if (multiplier == 1.0F) { return; }
        self.motionY *= multiplier;
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true) private void rdpl$immunity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!ContentEntities.immune((EntityLivingBase) (Object) this, source.getDamageType())) { return; }
        cir.setReturnValue(Boolean.FALSE);
    }

    @Redirect(method = "playHurtSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getHurtSound(Lnet/minecraft/util/DamageSource;)Lnet/minecraft/util/SoundEvent;"))
    private SoundEvent rdpl$hurtSound(EntityLivingBase self, DamageSource damageSourceIn) {
        SoundEvent wanted = ContentEntities.soundEvent(self, 1);
        return wanted != null ? wanted : getHurtSound(damageSourceIn);
    }

    @Redirect(method = { "attackEntityFrom", "handleStatusUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getDeathSound()Lnet/minecraft/util/SoundEvent;"))
    private SoundEvent rdpl$deathSound(EntityLivingBase self) {
        SoundEvent wanted = ContentEntities.soundEvent(self, 2);
        return wanted != null ? wanted : getDeathSound();
    }

    @ModifyVariable(method = "fall", at = @At("HEAD"), ordinal = 1, argsOnly = true) private float rdpl$fallDamage(float multiplier) { return ContentEntities.fallDamage((EntityLivingBase) (Object) this, multiplier); }

    @Redirect(method = "onEntityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;canBreatheUnderwater()Z"))
    private boolean rdpl$breathe(EntityLivingBase self) { return ContentEntities.breathesUnderwater(self) || self.canBreatheUnderwater(); }

    @Redirect(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getExperiencePoints(Lnet/minecraft/entity/player/EntityPlayer;)I"))
    private int rdpl$experience(EntityLivingBase self, EntityPlayer player) { return ContentEntities.experience(self, getExperiencePoints(player)); }

    @Inject(method = "getSoundVolume", at = @At("RETURN"), cancellable = true) private void rdpl$volume(CallbackInfoReturnable<Float> cir) { cir.setReturnValue(ContentEntities.sound((EntityLivingBase) (Object) this, cir.getReturnValueF(), false)); }

    @Inject(method = "getSoundPitch", at = @At("RETURN"), cancellable = true) private void rdpl$pitch(CallbackInfoReturnable<Float> cir) { cir.setReturnValue(ContentEntities.sound((EntityLivingBase) (Object) this, cir.getReturnValueF(), true)); }

    @Inject(method = "handleJumpWater", at = @At("HEAD"), cancellable = true) private void rdpl$sink(CallbackInfo ci) {
        if (!ContentEntities.sinks((EntityLivingBase) (Object) this)) { return; }
        ci.cancel();
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getWaterSlowDown()F"))
    private float rdpl$waterSpeed(EntityLivingBase self) { return ContentEntities.waterSlowdown(self, getWaterSlowDown()); }

    @Inject(method = "getCreatureAttribute", at = @At("RETURN"), cancellable = true) private void rdpl$creatureAttribute(CallbackInfoReturnable<EnumCreatureAttribute> cir) {
        EnumCreatureAttribute wanted = ContentEntities.creatureAttribute((EntityLivingBase) (Object) this);
        if (wanted == null) { return; }
        cir.setReturnValue(wanted);
    }

    public MixinEntityLivingBase(World worldIn) { super(worldIn); }

    @ModifyArg(
            method = "travel",
            index = 1,
            at = @At(
                    target = "Lnet/minecraft/util/math/BlockPos$PooledMutableBlockPos;setPos(DDD)"
                            + "Lnet/minecraft/util/math/BlockPos$PooledMutableBlockPos;",
                    value = "INVOKE", ordinal = 1)
    )
    private double moveEntityWithHeading_getReplacedY(double y) { return this.posY; }

    @ModifyConstant(method = "attemptTeleport", constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO)) private int rdpl$getMinHeight(int orig) {
        return ((IRubicWorld) world).rdpl$getMinHeight();
    }

    @Redirect(method = "attemptTeleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;getY()I"))
    private int isBlockLoadedTeleportCheck(BlockPos blockPos) { return world.isBlockLoaded(blockPos.down()) ? blockPos.getY() : (Integer.MIN_VALUE + 1); }
}
