package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentCityClaim;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Function;

@Mixin(OreFeature.class) public abstract class MixinOreFeature {
    @Inject(method = "doPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;DDDDDDIIIII)Z", at = @At("HEAD"))
    private void rdpl$findRailways(WorldGenLevel level, RandomSource random, double x0, double x1, double z0, double z1, double y0, double y1, int xStart, int yStart, int zStart, int sizeXZ, int sizeY, CallbackInfoReturnable<Boolean> cir) { ContentCityClaim.veinStarts(level, ((OreFeature) (Object) this).targetStates().stream().map(BlockReplacement::state).toList(), xStart, zStart, xStart + sizeXZ, zStart + sizeXZ); }

    @WrapOperation(method = "doPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;DDDDDDIIIII)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/OreFeature;canPlaceOre(Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/function/Function;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/feature/BlockReplacement;Lnet/minecraft/core/BlockPos$MutableBlockPos;)Z"))
    private boolean rdpl$notUnderARailway(OreFeature feature, BlockState orePosState, Function<BlockPos, BlockState> blockGetter, RandomSource random, BlockReplacement targetState, BlockPos.MutableBlockPos orePos, Operation<Boolean> original) { return original.call(feature, orePosState, blockGetter, random, targetState, orePos) && ContentCityClaim.oreMayFill(targetState.state(), orePos); }

    @Inject(method = "doPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;DDDDDDIIIII)Z", at = @At("RETURN"))
    private void rdpl$sayWhatWasSpared(WorldGenLevel level, RandomSource random, double x0, double x1, double z0, double z1, double y0, double y1, int xStart, int yStart, int zStart, int sizeXZ, int sizeY, CallbackInfoReturnable<Boolean> cir) { ContentCityClaim.veinEnds(xStart + sizeXZ / 2, yStart + sizeY / 2, zStart + sizeXZ / 2); }
}
