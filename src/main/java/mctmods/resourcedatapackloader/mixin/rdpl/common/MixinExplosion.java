package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.entity.ContentEntities;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import javax.annotation.Nullable;

@Mixin(ServerLevel.class) public abstract class MixinExplosion {
    @ModifyVariable(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/util/random/WeightedList;Lnet/minecraft/core/Holder;)V", at = @At("HEAD"), argsOnly = true)
    private Holder<SoundEvent> rdpl$ownBlastSound(Holder<SoundEvent> explosionSound, @Local(argsOnly = true) @Nullable Entity source) {
        SoundEvent own = ContentEntities.explodeSound(source);
        return own != null ? BuiltInRegistries.SOUND_EVENT.wrapAsHolder(own) : explosionSound;
    }
}
