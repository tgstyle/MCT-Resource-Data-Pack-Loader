package mctmods.resourcedatapackloader.mixin.world.level.biome;

import mctmods.resourcedatapackloader.common.worldgen.biome.ContentBiomes;
import mctmods.resourcedatapackloader.common.worldgen.carver.ContentCaveRegions;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class) public abstract class MultiNoiseBiomeSourceMixin {
    @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("HEAD"), cancellable = true) private void rdpl$caveRegion(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        BiomeSource source = (BiomeSource) (Object) this;
        Holder<Biome> region = ContentCaveRegions.biomeAt(source, x, y, z);
        if (region != null) {
            cir.setReturnValue(region);
            return;
        }
        Holder<Biome> band = ContentBiomes.bandAt(source, x, y, z, sampler);
        if (band != null) { cir.setReturnValue(band); }
    }
}
