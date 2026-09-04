package mctmods.resourcedatapackloader.mixin.rtg;

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
import rtg.api.world.biome.IRealisticBiome;
import rtg.world.gen.ChunkGeneratorRTG;

@Mixin(value = ChunkGeneratorRTG.class, remap = false) public abstract class MixinChunkGeneratorRTG {
    @Shadow @Final private World world;

    @Inject(method = "generateChunk", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$emptyChunk(int cx, int cz, CallbackInfoReturnable<Chunk> cir) {
        Chunk chunk = ContentVoidWorld.emptyChunk(world, cx, cz);
        if (chunk != null) { cir.setReturnValue(chunk); }
    }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true, remap = false) private void rdpl$skipPopulate(int chunkX, int chunkZ, CallbackInfo ci) {
        if (ContentVoidWorld.appliesTo(world)) { ci.cancel(); }
    }

    @Inject(method = "replaceBiomeBlocks", at = @At("RETURN"), remap = false) private void rdpl$packStone(int cx, int cz, ChunkPrimer primer, IRealisticBiome[] biomes, Biome[] base, float[] noise, CallbackInfo ci) {
        Biome[] ordered = new Biome[256];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) { ordered[z * 16 + x] = base[x * 16 + z]; }
        }
        ContentBiomes.replaceStone(primer, ordered);
    }
}
