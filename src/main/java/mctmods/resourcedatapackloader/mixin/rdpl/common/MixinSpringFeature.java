package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentCityClaim;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpringFeature.class) public abstract class MixinSpringFeature {
    @Inject(method = "place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z", at = @At("HEAD"), cancellable = true)
    private void rdpl$notIntoACut(FeaturePlaceContext<SpringConfiguration> p_160404_, CallbackInfoReturnable<Boolean> cir) {
        if (ContentCityClaim.springIntoCut(p_160404_.level(), p_160404_.origin())) { cir.setReturnValue(false); }
    }
}
