package mctmods.resourcedatapackloader.mixin.rdpl.client;

import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class) public abstract class MixinClientLevel {
    @Unique private DimensionDef rdpl$def() { return ContentDimensions.def((ClientLevel) (Object) this); }

    @Inject(method = "addEnvironmentAttributeLayers(Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;)Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;", at = @At("RETURN"))
    private void rdpl$packSky(EnvironmentAttributeSystem.Builder environmentAttributes, CallbackInfoReturnable<EnvironmentAttributeSystem.Builder> cir) {
        environmentAttributes.addTimeBasedLayer(EnvironmentAttributes.CLOUD_COLOR, (cloud, _) -> {
            DimensionDef def = rdpl$def();
            return def != null && def.cloudColor() >= 0 ? ARGB.color(ARGB.alpha(cloud), def.cloudColor()) : cloud;
        });
        environmentAttributes.addTimeBasedLayer(EnvironmentAttributes.STAR_BRIGHTNESS, (stars, _) -> {
            DimensionDef def = rdpl$def();
            return def != null && def.starBrightness() >= 0.0F ? def.starBrightness() * (1.0F - ((ClientLevel) (Object) this).getRainLevel(1.0F)) : stars;
        });
    }
}
