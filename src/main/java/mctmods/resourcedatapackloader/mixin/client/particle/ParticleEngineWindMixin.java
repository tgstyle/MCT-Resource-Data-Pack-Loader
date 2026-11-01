package mctmods.resourcedatapackloader.mixin.client.particle;

import mctmods.resourcedatapackloader.client.util.WindGust;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class) public abstract class ParticleEngineWindMixin {
    @Inject(method = "createParticle", at = @At("RETURN")) private void rdpl$windParticle(ParticleOptions options, double x, double y, double z, double xa, double ya, double za, CallbackInfoReturnable<Particle> cir) { WindGust.caught(cir.getReturnValue()); }
}
