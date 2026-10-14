package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.def.DimensionTraitsDef;
import mctmods.resourcedatapackloader.content.def.TimeWindowDef;
import mctmods.resourcedatapackloader.content.worldgen.ContentDimensions;
import mctmods.resourcedatapackloader.content.worldgen.ContentTimeWindows;
import mctmods.resourcedatapackloader.content.worldgen.ContentWeather;
import mctmods.resourcedatapackloader.content.worldgen.ContentWeatherCycle;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class) public abstract class MixinServerLevelWeather {
    @Shadow @Final private ServerLevelData serverLevelData;
    @Unique private final ContentWeatherCycle rdpl$weatherCycle = new ContentWeatherCycle();

    @Redirect(method = "advanceWeatherCycle()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasSkyLight()Z")) private boolean rdpl$ownWeather(DimensionType type) {
        ServerLevel level = (ServerLevel) (Object) this;
        DimensionTraitsDef traits = ContentWeather.traits(level);
        DimensionTraitsDef.Cycle cycle = traits.cycle();
        TimeWindowDef window = ContentTimeWindows.active(level, ContentDimensions.def(level));
        if (!traits.precipitation()) {
            ContentWeatherCycle.clear(level);
            return false;
        }
        if (window != null && window.holdsDry()) {
            ContentWeatherCycle.dry(level);
            return false;
        }
        if (window != null && window.gives(TimeWindowDef.ALWAYS_RAIN) ? window.alwaysRain() : traits.alwaysRain()) { return ContentWeatherCycle.pour(level, serverLevelData, type.hasSkyLight()); }
        if (cycle != null) {
            rdpl$weatherCycle.tick(level, cycle);
            return false;
        }
        return type.hasSkyLight();
    }

    @Redirect(method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isThundering()Z")) private boolean rdpl$lightningWhereAllowed(ServerLevel level) {
        DimensionTraitsDef traits = ContentWeather.traits(level);
        return traits.precipitation() && traits.lightning() && level.isThundering();
    }

    @Redirect(method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")) private boolean rdpl$iceWhereItPrecipitates(Biome biome, LevelReader level, BlockPos pos) { return ContentWeather.traits(level).precipitation() && biome.shouldFreeze(level, pos); }

    @Redirect(method = "tickChunk(Lnet/minecraft/world/level/chunk/LevelChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;handlePrecipitation(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/biome/Biome$Precipitation;)V")) private void rdpl$rainFillBelowCeiling(Block block, BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (ContentWeather.above(level, pos.getY())) { return; }
        Biome.Precipitation falls = ContentWeather.fallsAs(level, level.getBiome(pos.above()).value(), precipitation);
        if (falls == Biome.Precipitation.SNOW && !ContentWeather.traits(level).snow()) { return; }
        block.handlePrecipitation(state, level, pos, falls);
    }
}
