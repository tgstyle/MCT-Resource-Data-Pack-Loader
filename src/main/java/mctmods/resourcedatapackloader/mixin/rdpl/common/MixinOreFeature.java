package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.worldgen.ContentCityClaim;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Function;

@Mixin(OreFeature.class) public abstract class MixinOreFeature {
    @Inject(method = "doPlace", at = @At("HEAD"))
    private void rdpl$findRailways(WorldGenLevel level, RandomSource random, OreConfiguration config, double minX, double maxX, double minZ, double maxZ, double minY, double maxY, int x, int y, int z, int width, int height, CallbackInfoReturnable<Boolean> cir) { ContentCityClaim.veinStarts(level, config, x, z, x + width, z + width); }

    @WrapOperation(method = "doPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/OreFeature;canPlaceOre(Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/function/Function;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/feature/configurations/OreConfiguration;Lnet/minecraft/world/level/levelgen/feature/configurations/OreConfiguration$TargetBlockState;Lnet/minecraft/core/BlockPos$MutableBlockPos;)Z"))
    private boolean rdpl$notUnderARailway(BlockState state, Function<BlockPos, BlockState> adjacent, RandomSource random, OreConfiguration config, OreConfiguration.TargetBlockState target, BlockPos.MutableBlockPos at, Operation<Boolean> original) { return original.call(state, adjacent, random, config, target, at) && ContentCityClaim.oreMayFill(target.state, at); }

    @Inject(method = "doPlace", at = @At("RETURN"))
    private void rdpl$sayWhatWasSpared(WorldGenLevel level, RandomSource random, OreConfiguration config, double minX, double maxX, double minZ, double maxZ, double minY, double maxY, int x, int y, int z, int width, int height, CallbackInfoReturnable<Boolean> cir) { ContentCityClaim.veinEnds(x + width / 2, y + height / 2, z + width / 2); }
}
