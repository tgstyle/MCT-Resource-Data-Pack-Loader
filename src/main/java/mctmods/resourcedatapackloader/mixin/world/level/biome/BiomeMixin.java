package mctmods.resourcedatapackloader.mixin.world.level.biome;

import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.util.worldgen.TemplateMemo;
import mctmods.resourcedatapackloader.core.util.LineCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class) public abstract class BiomeMixin {
    @Shadow @Final private Biome.ClimateSettings climateSettings;
    @Unique private static final TemplateMemo<float[]> rdpl$curve = new TemplateMemo<>();

    @Shadow public abstract float getBaseTemperature();

    @Inject(method = "shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true) private void rdpl$snowBelowCeiling(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) { if (ContentWeather.above(level, pos.getY()) || !ContentWeather.traits(level).snow()) { cir.setReturnValue(false); } }

    @Inject(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at = @At("HEAD"), cancellable = true) private void rdpl$freezeWhereAllowed(LevelReader level, BlockPos pos, boolean checkNeighbors, CallbackInfoReturnable<Boolean> cir) { if (!ContentWeather.traits(level).freeze()) { cir.setReturnValue(false); } }

    @Inject(method = "getHeightAdjustedTemperature(Lnet/minecraft/core/BlockPos;I)F", at = @At("HEAD"), cancellable = true) private void rdpl$packCurve(BlockPos pos, int seaLevel, CallbackInfoReturnable<Float> cir) {
        float[] curve = rdpl$curve.get(() -> {
            int center = ContentControl.number(ContentControl.TERRAIN, "biomeTemperatureCenterY", Integer.MIN_VALUE);
            int top = ContentControl.number(ContentControl.TERRAIN, "biomeTemperatureScaleMaxY", Integer.MIN_VALUE);
            float factor = ContentControl.decimal(ContentControl.TERRAIN, "biomeTemperatureHeightFactor", Float.NaN);
            if (center == Integer.MIN_VALUE && top == Integer.MIN_VALUE && Float.isNaN(factor)) { return null; }
            return new float[] { center == Integer.MIN_VALUE ? Float.NaN : center, top == Integer.MIN_VALUE ? Integer.MAX_VALUE : top, Float.isNaN(factor) ? -0.05F / 40.0F : factor };
        });
        if (curve == null) { return; }
        float base = climateSettings.temperatureModifier().modifyTemperature(pos, getBaseTemperature());
        float center = Float.isNaN(curve[0]) ? seaLevel + 17 : curve[0];
        if (pos.getY() <= center) {
            cir.setReturnValue(base);
            return;
        }
        cir.setReturnValue(base + (LineCompat.temperatureNoise(pos) + Math.min(pos.getY(), curve[1]) - center) * curve[2]);
    }
}
