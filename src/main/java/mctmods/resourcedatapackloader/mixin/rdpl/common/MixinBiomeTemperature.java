package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class) public abstract class MixinBiomeTemperature {
    @Shadow @Final private Biome.ClimateSettings climateSettings;
    @Unique private static final TemplateMemo<float[]> rdpl$curve = new TemplateMemo<>();

    @Shadow public abstract float getBaseTemperature();

    @Inject(method = "getHeightAdjustedTemperature(Lnet/minecraft/core/BlockPos;I)F", at = @At("HEAD"), cancellable = true) private void rdpl$packCurve(BlockPos pos, int seaLevel, CallbackInfoReturnable<Float> cir) {
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
        cir.setReturnValue(base + (LineCompat.temperatureNoise(pos) + Math.min(pos.getY(), curve[1]) - curve[0]) * curve[2]);
    }
}
