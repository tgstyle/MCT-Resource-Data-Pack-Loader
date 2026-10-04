package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentDimensionAmbience;

import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnvironmentAttributeSystem.Builder.class) public abstract class MixinEnvironmentAttributeSystemBuilder {
    @Inject(method = "addDefaultLayers", at = @At("RETURN"))
    private void rdpl$dimensionAmbience(Level level, CallbackInfoReturnable<EnvironmentAttributeSystem.Builder> cir) { ContentDimensionAmbience.layers((EnvironmentAttributeSystem.Builder) (Object) this, level); }
}
