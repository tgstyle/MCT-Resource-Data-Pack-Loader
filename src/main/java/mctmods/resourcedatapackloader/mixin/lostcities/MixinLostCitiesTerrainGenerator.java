package mctmods.resourcedatapackloader.mixin.lostcities;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;

import mcjty.lostcities.dimensions.world.terraingen.LostCitiesTerrainGenerator;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LostCitiesTerrainGenerator.class, remap = false)
public abstract class MixinLostCitiesTerrainGenerator {
    @Inject(method = "replaceBlocksForBiome", at = @At("RETURN"), remap = false)
    private void rdpl$packStone(int chunkX, int chunkZ, ChunkPrimer primer, Biome[] biomes, CallbackInfo ci) { ContentBiomes.replaceStone(primer, biomes); }
}
