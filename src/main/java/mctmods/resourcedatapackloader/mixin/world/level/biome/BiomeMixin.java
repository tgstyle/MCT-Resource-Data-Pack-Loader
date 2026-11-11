package mctmods.resourcedatapackloader.mixin.world.level.biome;

import mctmods.resourcedatapackloader.common.blocks.shield.ShieldIndexes;
import mctmods.resourcedatapackloader.common.environment.ContentSeasonSnow;
import mctmods.resourcedatapackloader.common.environment.ContentWeather;
import mctmods.resourcedatapackloader.common.pack.loading.ContentControl;
import mctmods.resourcedatapackloader.common.util.worldgen.TemplateMemo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class) public abstract class BiomeMixin {
    @Inject(method = "shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true) private void rdpl$snowBelowCeiling(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) { if (ContentWeather.shut(level, pos) || ContentSeasonSnow.snowStopped(level, pos)) { cir.setReturnValue(false); } }

    @Inject(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at = @At("HEAD"), cancellable = true) private void rdpl$freezeWhereAllowed(LevelReader level, BlockPos water, boolean mustBeAtEdge, CallbackInfoReturnable<Boolean> cir) { if (ShieldIndexes.weatherShut(level, water.getX(), water.getZ()) || ContentSeasonSnow.freezeStopped(level, water)) { cir.setReturnValue(false); } }

    @WrapOperation(method = "shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z")) private boolean rdpl$snowWhereAny(Biome biome, BlockPos pos, Operation<Boolean> original, @Local(argsOnly = true) LevelReader level) { return ContentSeasonSnow.warmForSnow(level, pos) && original.call(biome, pos); }

    @WrapOperation(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z")) private boolean rdpl$freezeWhereAny(Biome biome, BlockPos pos, Operation<Boolean> original, @Local(argsOnly = true) LevelReader level) { return ContentSeasonSnow.warmForFreeze(level, pos) && original.call(biome, pos); }

    @Shadow @Final private static PerlinSimplexNoise TEMPERATURE_NOISE;
    @Shadow @Final private Biome.ClimateSettings climateSettings;
    @Unique private static final TemplateMemo<float[]> rdpl$curve = new TemplateMemo<>();

    @Shadow public abstract float getBaseTemperature();

    @Inject(method = "getHeightAdjustedTemperature", at = @At("HEAD"), cancellable = true) private void rdpl$packCurve(BlockPos pos, CallbackInfoReturnable<Float> cir) {
        float[] curve = rdpl$curve.get(() -> {
            int center = ContentControl.number(ContentControl.TERRAIN, "biomeTemperatureCenterY", Integer.MIN_VALUE);
            int top = ContentControl.number(ContentControl.TERRAIN, "biomeTemperatureScaleMaxY", Integer.MIN_VALUE);
            float factor = ContentControl.decimal(ContentControl.TERRAIN, "biomeTemperatureHeightFactor", Float.NaN);
            if (center == Integer.MIN_VALUE && top == Integer.MIN_VALUE && Float.isNaN(factor)) { return null; }
            return new float[] { center == Integer.MIN_VALUE ? 80.0F : center, top == Integer.MIN_VALUE ? Integer.MAX_VALUE : top, Float.isNaN(factor) ? -0.05F / 40.0F : factor };
        });
        if (curve == null) { return; }
        float base = climateSettings.temperatureModifier().modifyTemperature(pos, getBaseTemperature());
        if (pos.getY() <= curve[0]) {
            cir.setReturnValue(base);
            return;
        }
        float noise = (float) (TEMPERATURE_NOISE.getValue(pos.getX() / 8.0F, pos.getZ() / 8.0F, false) * 8.0D);
        cir.setReturnValue(base + (noise + Math.min(pos.getY(), curve[1]) - curve[0]) * curve[2]);
    }
}
