package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.interfaces.IRegionAquifer;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveRegions;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseBasedChunkGenerator.class) public abstract class MixinRegionWaterGenerator {
    @Inject(method = "createNoiseChunk", at = @At("RETURN"))
    private void rdpl$regionWater(ChunkAccess chunk, StructureManager structureManager, Blender blender, RandomState random, CallbackInfoReturnable<NoiseChunk> cir) {
        ChunkGenerator generator = ChunkGenerator.class.cast(this);
        if (cir.getReturnValue().aquifer() instanceof IRegionAquifer aquifer && ContentCaveRegions.pinsWater(generator.getBiomeSource())) { aquifer.rdpl$pinRegions(generator.getBiomeSource(), generator.getSeaLevel()); }
    }
}
