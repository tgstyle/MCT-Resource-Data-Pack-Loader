package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentCityClaim;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BlockBlobFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBlobFeature.class) public abstract class MixinBlockBlobFeature {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void rdpl$spareRoads(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin, CallbackInfoReturnable<Boolean> cir) {
        if (ContentCityClaim.boulderOnRoad(level, origin)) { cir.setReturnValue(false); }
    }
}
