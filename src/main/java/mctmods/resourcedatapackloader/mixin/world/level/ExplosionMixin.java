package mctmods.resourcedatapackloader.mixin.world.level;

import mctmods.resourcedatapackloader.common.entities.ContentEntities;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Explosion.class) public abstract class ExplosionMixin {
    @Shadow @Nullable public abstract Entity getDirectSourceEntity();
    @Shadow @Final private Holder<SoundEvent> explosionSound;

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"), index = 6) private float rdpl$ownBlastPitch(float pitch) { return ContentEntities.packExplodeSound(explosionSound.value()) ? 1.0F : pitch; }

    @Inject(method = "getExplosionSound", at = @At("HEAD"), cancellable = true) private void rdpl$ownBlastSound(CallbackInfoReturnable<Holder<SoundEvent>> cir) {
        SoundEvent own = ContentEntities.explodeSound(getDirectSourceEntity());
        if (own != null) { cir.setReturnValue(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(own)); }
    }
}
