package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentDeepCaves;
import mctmods.resourcedatapackloader.content.worldgen.ContentPopulateControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
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

    @Inject(method = "doFill", at = @At("HEAD"), cancellable = true)
    private void rdpl$voidFill(Blender blender, StructureManager structureManager, RandomState random, ChunkAccess chunk, int minCellY, int cellCountY, CallbackInfoReturnable<ChunkAccess> cir) {
        if (ContentVoidWorld.voidEmpties(structureManager)) { cir.setReturnValue(chunk); }
    }

    @Redirect(method = "applyCarvers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/carver/ConfiguredWorldCarver;isStartChunk(Lnet/minecraft/util/RandomSource;)Z"))
    private boolean rdpl$carves(ConfiguredWorldCarver<?> carver, RandomSource random) {
        return ContentPopulateControl.carves(ChunkGenerator.class.cast(this), carver) && carver.isStartChunk(random);
    }

    @Inject(method = "spawnOriginalMobs", at = @At("HEAD"), cancellable = true)
    private void rdpl$animals(WorldGenRegion region, CallbackInfo ci) {
        if (ContentPopulateControl.refusesAnimals(ChunkGenerator.class.cast(this))) { ci.cancel(); }
    }
}
