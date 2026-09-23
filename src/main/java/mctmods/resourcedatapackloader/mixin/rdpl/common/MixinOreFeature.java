package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentCityClaim;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Function;

@Mixin(OreFeature.class) public abstract class MixinOreFeature {
    @Inject(method = "doPlace", at = @At("HEAD"))
    private void rdpl$findRailways(WorldGenLevel level, RandomSource random, OreConfiguration config, double minX, double maxX, double minZ, double maxZ, double minY, double maxY, int x, int y, int z, int width, int height, CallbackInfoReturnable<Boolean> cir) { ContentCityClaim.veinStarts(level, config, x, z, x + width, z + width); }

    @Redirect(method = "doPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/OreFeature;canPlaceOre(Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/function/Function;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/feature/configurations/OreConfiguration;Lnet/minecraft/world/level/levelgen/feature/configurations/OreConfiguration$TargetBlockState;Lnet/minecraft/core/BlockPos$MutableBlockPos;)Z"))
    private boolean rdpl$notUnderARailway(BlockState state, Function<BlockPos, BlockState> adjacentStateAccessor, RandomSource random, OreConfiguration config, OreConfiguration.TargetBlockState targetState, BlockPos.MutableBlockPos mutablePos) { return OreFeature.canPlaceOre(state, adjacentStateAccessor, random, config, targetState, mutablePos) && ContentCityClaim.oreMayFill(targetState.state, mutablePos); }

    @Inject(method = "doPlace", at = @At("RETURN"))
    private void rdpl$sayWhatWasSpared(WorldGenLevel level, RandomSource random, OreConfiguration config, double minX, double maxX, double minZ, double maxZ, double minY, double maxY, int x, int y, int z, int width, int height, CallbackInfoReturnable<Boolean> cir) { ContentCityClaim.veinEnds(x + width / 2, y + height / 2, z + width / 2); }
}
