package mctmods.resourcedatapackloader.mixin.world.level.levelgen.placement;

import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPopulateControl;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.FeaturePlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FeaturePlacer.class) public abstract class FeaturePlacerMixin {
    @Shadow @Final private WorldGenLevel level;
    @Shadow @Final private ChunkGenerator generator;

    @Inject(method = "placeWithBiomeCheck(Lnet/minecraft/world/level/levelgen/placement/PlacedFeature;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true) private void rdpl$populate(PlacedFeature placedFeature, RandomSource random, BlockPos origin, CallbackInfoReturnable<Boolean> cir) { if (ContentPopulateControl.refusesPlacement(level, generator, placedFeature)) { cir.setReturnValue(false); } }
}
