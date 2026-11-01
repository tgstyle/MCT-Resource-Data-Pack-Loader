package mctmods.resourcedatapackloader.mixin.world.level.levelgen.feature;

import mctmods.resourcedatapackloader.common.worldgen.structure.city.ContentCityClaim;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.IcebergFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IcebergFeature.class) public abstract class IcebergFeatureMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true) private void rdpl$spareCity(FeaturePlaceContext<BlockStateConfiguration> context, CallbackInfoReturnable<Boolean> cir) { if (ContentCityClaim.bergInCity(context.level(), context.chunkGenerator(), context.origin())) { cir.setReturnValue(false); } }
}
