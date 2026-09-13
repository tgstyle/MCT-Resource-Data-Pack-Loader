package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentDeepCaves;
import mctmods.resourcedatapackloader.content.worldgen.ContentPopulateControl;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseBasedChunkGenerator.class) public abstract class MixinNoiseBasedChunkGenerator {
    @Inject(method = "createFluidPicker", at = @At("HEAD"), cancellable = true)
    private static void rdpl$deepLava(NoiseGeneratorSettings settings, CallbackInfoReturnable<Aquifer.FluidPicker> cir) {
        Aquifer.FluidPicker picker = ContentDeepCaves.fluidPicker(settings);
        if (picker != null) { cir.setReturnValue(picker); }
    }

    @Redirect(method = "applyCarvers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/carver/ConfiguredWorldCarver;isStartChunk(Lnet/minecraft/util/RandomSource;)Z"))
    private boolean rdpl$carves(ConfiguredWorldCarver<?> carver, RandomSource random) {
        return ContentPopulateControl.carves(ChunkGenerator.class.cast(this), carver) && carver.isStartChunk(random);
    }

    @Inject(method = "spawnOriginalMobs", at = @At("HEAD"), cancellable = true)
    private void rdpl$animals(WorldGenRegion level, CallbackInfo ci) {
        if (ContentPopulateControl.refusesAnimals(ChunkGenerator.class.cast(this))) { ci.cancel(); }
    }
}
