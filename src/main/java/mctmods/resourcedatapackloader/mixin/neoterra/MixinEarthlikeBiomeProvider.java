package mctmods.resourcedatapackloader.mixin.neoterra;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.platuro.neoterra.worldgen.EarthlikeBiomeProvider", remap = false)
public abstract class MixinEarthlikeBiomeProvider {
    @Inject(method = "getBiomesForGeneration", at = @At("RETURN"), remap = false)
    private void rdpl$substituteForGeneration(Biome[] biomes, int x, int z, int width, int height, CallbackInfoReturnable<Biome[]> cir) {
        ContentBiomeControl.substitute((BiomeProvider) (Object) this, cir.getReturnValue(), width * height);
    }

    @Inject(method = "getBiomes([Lnet/minecraft/world/biome/Biome;IIIIZ)[Lnet/minecraft/world/biome/Biome;", at = @At("RETURN"), remap = false)
    private void rdpl$substitute(Biome[] listToReuse, int x, int z, int width, int depth, boolean cacheFlag, CallbackInfoReturnable<Biome[]> cir) {
        ContentBiomeControl.substitute((BiomeProvider) (Object) this, cir.getReturnValue(), width * depth);
    }
}
