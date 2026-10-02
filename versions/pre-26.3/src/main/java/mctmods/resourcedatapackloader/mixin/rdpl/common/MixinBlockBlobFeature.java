package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentCityClaim;

import net.minecraft.world.level.levelgen.feature.BlockBlobFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockBlobConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBlobFeature.class) public abstract class MixinBlockBlobFeature {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void rdpl$spareRoads(FeaturePlaceContext<BlockBlobConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        if (ContentCityClaim.boulderOnRoad(context.level(), context.origin())) { cir.setReturnValue(false); }
    }
}
