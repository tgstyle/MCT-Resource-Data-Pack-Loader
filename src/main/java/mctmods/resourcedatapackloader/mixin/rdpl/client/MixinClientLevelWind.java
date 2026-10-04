package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.util.WindGust;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientLevel.class) public abstract class MixinClientLevelWind {
    @Redirect(method = "doAnimateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 1))
    private void rdpl$windParticle(ClientLevel level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
        DimensionDef def = ContentDimensions.def(level);
        WindGust.carry(def == null ? null : def.traits().rain(), level.getGameTime());
        level.addParticle(particle, x, y, z, xd, yd, zd);
        WindGust.carry(null, 0.0D);
    }
}
