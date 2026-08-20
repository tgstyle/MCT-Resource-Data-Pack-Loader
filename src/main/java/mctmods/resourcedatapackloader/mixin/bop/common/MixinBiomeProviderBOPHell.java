package mctmods.resourcedatapackloader.mixin.bop.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;

import biomesoplenty.common.world.BiomeProviderBOPHell;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeProviderBOPHell.class) public abstract class MixinBiomeProviderBOPHell {
    @Inject(method = "getBiomesForGeneration", at = @At("RETURN"), remap = false) private void rdpl$substituteForGeneration(Biome[] biomes, int x, int z, int width, int height, CallbackInfoReturnable<Biome[]> cir) {
        ContentBiomeControl.substitute((BiomeProvider) (Object) this, cir.getReturnValue(), width * height);
    }
}
