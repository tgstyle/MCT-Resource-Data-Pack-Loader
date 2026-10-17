package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentCraters;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlatLevelSource.class) public abstract class MixinFlatLevelSource {
    @Inject(method = "buildSurface", at = @At("HEAD")) private void rdpl$surface(WorldGenRegion level, StructureManager structureManager, RandomState randomState, ChunkAccess protoChunk, CallbackInfo ci) { ContentCraters.carveFlat(level, protoChunk); }
}
