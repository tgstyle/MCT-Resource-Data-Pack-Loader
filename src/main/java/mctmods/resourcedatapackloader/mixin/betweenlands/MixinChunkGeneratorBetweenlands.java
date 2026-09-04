package mctmods.resourcedatapackloader.mixin.betweenlands;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeWeights;

@Mixin(value = ChunkGeneratorBetweenlands.class, remap = false) public abstract class MixinChunkGeneratorBetweenlands {
    @Shadow @Final private World worldObj;

    @Inject(method = "generateChunk", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$emptyChunk(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        Chunk chunk = ContentVoidWorld.emptyChunk(worldObj, chunkX, chunkZ);
        if (chunk != null) { cir.setReturnValue(chunk); }
    }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$skipPopulate(int x, int z, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(worldObj)) { ci.cancel(); }
    }

    @Inject(method = "replaceBiomeBlocks", at = @At("RETURN"), remap = false) private void rdpl$packStone(int chunkX, int chunkZ, ChunkPrimer primer, Biome[] biomesIn, BiomeWeights biomeWeights, CallbackInfo ci) { ContentBiomes.replaceStone(primer, biomesIn); }
}
