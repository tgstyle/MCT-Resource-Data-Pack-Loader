package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.TemplateMemo;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Biome.class) public abstract class MixinBiomeTemperatureConfig {
    @Shadow @Final protected static NoiseGeneratorPerlin TEMPERATURE_NOISE;

    @Shadow public abstract float getDefaultTemperature();
    @Unique private static final TemplateMemo<float[]> rdpl$curve = new TemplateMemo<>();

    /**
     * @author tgstyle
     * @reason Scale biome temperature by height above the pack-configured center instead of vanilla's fixed sea-level curve.
     */

    @SuppressWarnings("OverwriteModifiers") @Overwrite public float getTemperature(BlockPos pos) {
        float[] curve = rdpl$curve.get(() -> new float[] {
                ContentControl.number(ContentControl.TERRAIN, "biomeTemperatureCenterY", 64),
                ContentControl.number(ContentControl.TERRAIN, "biomeTemperatureScaleMaxY", 256),
                ContentControl.decimal(ContentControl.TERRAIN, "biomeTemperatureHeightFactor", -0.05F / 30.0F) });
        if (pos.getY() > curve[0]) {
            float noise = (float) (TEMPERATURE_NOISE.getValue((float) pos.getX() / 8.0F, (float) pos.getZ() / 8.0F) * 4.0D);
            int y = Math.min(pos.getY(), (int) curve[1]);
            return this.getDefaultTemperature() + (noise + y - curve[0]) * curve[2];
        }
        else { return this.getDefaultTemperature(); }
    }
}
