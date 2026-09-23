package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.PieceLaid;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureStart.class) public abstract class MixinStructureStart {
    @Inject(method = "placeInChunk", at = @At("HEAD"))
    private void rdpl$layingPieces(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, CallbackInfo ci) {
        if (level instanceof PieceLaid laid) { laid.rdpl$laying(true); }
    }

    @Inject(method = "placeInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/Structure;afterPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/levelgen/structure/pieces/PiecesContainer;)V"))
    private void rdpl$piecesLaid(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, CallbackInfo ci) {
        if (level instanceof PieceLaid laid) { laid.rdpl$laying(false); }
    }

    @Inject(method = "placeInChunk", at = @At("RETURN"))
    private void rdpl$placed(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, CallbackInfo ci) {
        if (level instanceof PieceLaid laid) { laid.rdpl$laying(false); }
    }
}
