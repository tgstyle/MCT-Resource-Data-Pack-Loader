package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomes;
import mctmods.resourcedatapackloader.content.worldgen.ContentCaveRegions;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class) public abstract class MixinMultiNoiseBiomeSource {
    @Inject(method = "createResolver(Lnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/world/level/biome/BiomeResolver;", at = @At("RETURN"), cancellable = true)
    private void rdpl$caveRegion(Climate.Sampler sampler, CallbackInfoReturnable<BiomeResolver> cir) { cir.setReturnValue(rdpl$wrap(cir.getReturnValue(), sampler)); }

    @Inject(method = "createResolverForChunk(Lnet/minecraft/world/level/biome/Climate$Sampler;IIIIII)Lnet/minecraft/world/level/biome/BiomeResolver;", at = @At("RETURN"), cancellable = true)
    private void rdpl$caveRegionForChunk(Climate.Sampler sampler, int minQuartX, int minQuartY, int minQuartZ, int quartSizeX, int quartSizeY, int quartSizeZ, CallbackInfoReturnable<BiomeResolver> cir) { cir.setReturnValue(rdpl$wrap(cir.getReturnValue(), sampler)); }

    @Unique private BiomeResolver rdpl$wrap(BiomeResolver resolver, Climate.Sampler sampler) {
        BiomeSource source = BiomeSource.class.cast(this);
        return (quartX, quartY, quartZ) -> {
            Holder<Biome> region = ContentCaveRegions.biomeAt(source, quartX, quartY, quartZ);
            if (region != null) { return region; }
            Holder<Biome> band = ContentBiomes.bandAt(source, quartX, quartY, quartZ, sampler);
            return band != null ? band : resolver.getNoiseBiome(quartX, quartY, quartZ);
        };
    }
}
