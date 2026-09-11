package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentCityClaim;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("deprecation") @Mixin(LakeFeature.class) public abstract class MixinLakeFeature {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void rdpl$spareCity(FeaturePlaceContext<LakeFeature.Configuration> context, CallbackInfoReturnable<Boolean> cir) {
        if (ContentCityClaim.floods(context.level(), context.chunkGenerator(), context.origin())) { cir.setReturnValue(false); }
    }
}
