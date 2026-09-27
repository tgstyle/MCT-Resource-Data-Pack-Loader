package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.interfaces.IMarkedNoise;
import mctmods.resourcedatapackloader.content.worldgen.CityDeckBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentDeepCaves;
import mctmods.resourcedatapackloader.content.worldgen.ContentPopulateControl;
import mctmods.resourcedatapackloader.content.worldgen.ContentVoidWorld;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
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

    @Inject(method = "createNoiseChunk", at = @At("RETURN"))
    private void rdpl$markFill(ChunkAccess chunk, StructureManager structureManager, Blender blender, RandomState randomState, NoiseSettings noiseSettings, CallbackInfoReturnable<NoiseChunk> cir, @Local Beardifier beardifier) { ((IMarkedNoise) cir.getReturnValue()).rdpl$mark(ContentVoidWorld.voidEmpties(structureManager), beardifier instanceof CityDeckBeard city ? city : null); }

    @Inject(method = "doFill", at = @At("HEAD"), cancellable = true)
    private void rdpl$voidFill(NoiseChunk noiseChunk, ChunkAccess chunk, CallbackInfo ci) {
        if (((IMarkedNoise) noiseChunk).rdpl$voided()) { ci.cancel(); }
    }

    @WrapOperation(method = "doFill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Aquifer;computeSubstance(IIID)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState rdpl$dryCityCut(Aquifer aquifer, int x, int y, int z, double density, Operation<BlockState> original, @Local(argsOnly = true) NoiseChunk noiseChunk) {
        BlockState held = original.call(aquifer, x, y, z, density);
        if (held == null || held.getFluidState().isEmpty() || !(aquifer instanceof Aquifer.NoiseBasedAquifer)) { return held; }
        CityDeckBeard city = ((IMarkedNoise) noiseChunk).rdpl$city();
        return city != null && city.dug(x, y, z, density) ? Blocks.AIR.defaultBlockState() : held;
    }

    @Redirect(method = "generateCarvers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/carver/WorldCarver;isStartChunk(Lnet/minecraft/util/RandomSource;)Z"))
    private boolean rdpl$carves(WorldCarver carver, RandomSource random) { return ContentPopulateControl.carves(ChunkGenerator.class.cast(this), carver) && carver.isStartChunk(random); }

    @Inject(method = "spawnOriginalMobs", at = @At("HEAD"), cancellable = true)
    private void rdpl$animals(WorldGenRegion worldGenRegion, CallbackInfo ci) {
        if (ContentPopulateControl.refusesAnimals(ChunkGenerator.class.cast(this))) { ci.cancel(); }
    }
}
