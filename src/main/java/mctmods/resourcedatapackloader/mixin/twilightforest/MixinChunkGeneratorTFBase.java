package mctmods.resourcedatapackloader.mixin.twilightforest;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.world.ChunkGeneratorTFBase;

@Mixin(value = ChunkGeneratorTFBase.class, remap = false) public abstract class MixinChunkGeneratorTFBase {
    @Inject(method = "replaceBiomeBlocks", at = @At("RETURN"), remap = false) private void rdpl$packStone(int x, int z, ChunkPrimer primer, Biome[] biomesIn, CallbackInfo ci) { ContentBiomes.replaceStone(primer, biomesIn); }
}
