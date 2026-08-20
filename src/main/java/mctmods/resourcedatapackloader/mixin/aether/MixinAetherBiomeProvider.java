package mctmods.resourcedatapackloader.mixin.aether;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;

import com.gildedgames.the_aether.world.AetherBiomeProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AetherBiomeProvider.class, remap = false) public abstract class MixinAetherBiomeProvider {
    @SuppressWarnings("ConstantConditions") @Inject(method = "getBiomesForGeneration", at = @At("RETURN"), remap = false) private void rdpl$substituteForGeneration(Biome[] biomes, int x, int z, int width, int height, CallbackInfoReturnable<Biome[]> cir) {
        ContentBiomeControl.substitute((BiomeProvider) (Object) this, cir.getReturnValue(), width * height);
    }

    @SuppressWarnings("ConstantConditions") @Inject(method = "getBiomes([Lnet/minecraft/world/biome/Biome;IIIIZ)[Lnet/minecraft/world/biome/Biome;", at = @At("RETURN"), remap = false)
    private void rdpl$substitute(Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag, CallbackInfoReturnable<Biome[]> cir) {
        ContentBiomeControl.substitute((BiomeProvider) (Object) this, cir.getReturnValue(), width * length);
    }
}
