package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentBiomeControl;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.List;

@Mixin(BiomeProvider.class) public abstract class MixinBiomeProvider {
    @Inject(method = "getBiomesForGeneration", at = @At("RETURN")) private void rdpl$substituteForGeneration(Biome[] biomes, int x, int z, int width, int height, CallbackInfoReturnable<Biome[]> cir) {
        ContentBiomeControl.substitute((BiomeProvider) (Object) this, cir.getReturnValue(), width * height);
    }

    @Inject(method = "getBiomes([Lnet/minecraft/world/biome/Biome;IIIIZ)[Lnet/minecraft/world/biome/Biome;", at = @At("RETURN"))
    private void rdpl$substitute(Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag, CallbackInfoReturnable<Biome[]> cir) {
        ContentBiomeControl.substitute((BiomeProvider) (Object) this, cir.getReturnValue(), width * length);
    }

    @Redirect(method = "areBiomesViable", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
    private boolean rdpl$viableAfterSubstitution(List<Biome> allowed, Object held) { return ContentBiomeControl.viable((BiomeProvider) (Object) this, (Biome) held, allowed); }
}
