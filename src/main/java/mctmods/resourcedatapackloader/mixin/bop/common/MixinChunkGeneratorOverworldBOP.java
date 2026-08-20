package mctmods.resourcedatapackloader.mixin.bop.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import biomesoplenty.common.world.ChunkGeneratorOverworldBOP;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkGeneratorOverworldBOP.class, remap = false) public abstract class MixinChunkGeneratorOverworldBOP {
    @Shadow private World world;

    @Inject(method = "generateChunk", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$emptyChunk(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        if (!ContentVoidWorld.appliesTo(world)) { return; }
        Chunk chunk = new Chunk(world, new ChunkPrimer(), chunkX, chunkZ);
        chunk.generateSkylightMap();
        cir.setReturnValue(chunk);
    }

    @Inject(method = "replaceBlocksForBiome", at = @At("RETURN"), remap = false) private void rdpl$packStone(int chunkX, int chunkZ, ChunkPrimer primer, Biome[] biomes, CallbackInfo ci) { ContentBiomes.replaceStone(primer, biomes); }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$skipPopulate(int chunkX, int chunkZ, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(world)) { ci.cancel(); }
    }
}
