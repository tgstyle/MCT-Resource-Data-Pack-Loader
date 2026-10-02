package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentWeather;
import mctmods.resourcedatapackloader.content.worldgen.ContentWeatherCycle;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class) public abstract class MixinServerLevelWeather {
    @Unique private final ContentWeatherCycle rdpl$weatherCycle = new ContentWeatherCycle();

    @WrapOperation(method = "advanceWeatherCycle()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;canHaveWeather()Z"))
    private boolean rdpl$ownWeather(ServerLevel level, Operation<Boolean> original) {
        DimensionTraitsDef traits = ContentWeather.traits(level);
        DimensionTraitsDef.Cycle cycle = traits.cycle();
        if (!traits.precipitation()) {
            ContentWeatherCycle.clear(level);
            return false;
        }
        if (cycle != null) {
            rdpl$weatherCycle.tick(level, cycle);
            return false;
        }
        return original.call(level);
    }

    @ModifyExpressionValue(method = "advanceWeatherCycle()V", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z"))
    private boolean rdpl$followServerWeather(boolean advance) { return advance && ContentDimensions.def(ServerLevel.class.cast(this)) == null; }

    @Inject(method = "tickThunder(Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At("HEAD"), cancellable = true)
    private void rdpl$noLightning(LevelChunk chunk, CallbackInfo ci) {
        DimensionTraitsDef traits = ContentWeather.traits(ServerLevel.class.cast(this));
        if (!traits.precipitation() || !traits.lightning()) { ci.cancel(); }
    }

    @Inject(method = "tickPrecipitation(Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"), cancellable = true)
    private void rdpl$noPrecipitation(BlockPos pos, CallbackInfo ci) {
        if (!ContentWeather.traits(ServerLevel.class.cast(this)).precipitation()) { ci.cancel(); }
    }

    @Redirect(method = "tickPrecipitation(Lnet/minecraft/core/BlockPos;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;handlePrecipitation(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/biome/Biome$Precipitation;)V"))
    private void rdpl$rainFillBelowCeiling(Block block, BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (ContentWeather.above(level, pos.getY())) { return; }
        Biome.Precipitation falls = ContentWeather.fallsAs(level, level.getBiome(pos.above()).value(), precipitation);
        if (falls == Biome.Precipitation.SNOW && !ContentWeather.traits(level).snow()) { return; }
        block.handlePrecipitation(state, level, pos, falls);
    }
}
