package mctmods.resourcedatapackloader.mixin.client.multiplayer;

import mctmods.resourcedatapackloader.client.util.WindGust;
import mctmods.resourcedatapackloader.common.dimension.ContentDimensions;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DimensionDef;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class) public abstract class ClientLevelMixin {
    @Unique private DimensionDef rdpl$def() { return ContentDimensions.def((ClientLevel) (Object) this); }

    @Inject(method = "addEnvironmentAttributeLayers(Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;)Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;", at = @At("RETURN")) private void rdpl$packSky(EnvironmentAttributeSystem.Builder environmentAttributes, CallbackInfoReturnable<EnvironmentAttributeSystem.Builder> cir) {
        environmentAttributes.addTimeBasedLayer(EnvironmentAttributes.CLOUD_COLOR, (cloud, _) -> {
            DimensionDef def = rdpl$def();
            return def != null && def.cloudColor() >= 0 ? ARGB.color(ARGB.alpha(cloud), def.cloudColor()) : cloud;
        });
        environmentAttributes.addTimeBasedLayer(EnvironmentAttributes.STAR_BRIGHTNESS, (stars, _) -> {
            DimensionDef def = rdpl$def();
            return def != null && def.starBrightness() >= 0.0F ? def.starBrightness() * (1.0F - ((ClientLevel) (Object) this).getRainLevel(1.0F)) : stars;
        });
    }

    @Redirect(method = "doAnimateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V", ordinal = 1)) private void rdpl$windParticle(ClientLevel level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
        DimensionDef def = ContentDimensions.def(level);
        WindGust.carry(def == null ? null : def.traits().rain(), level.getGameTime());
        level.addParticle(particle, x, y, z, xd, yd, zd);
        WindGust.carry(null, 0.0D);
    }
}
