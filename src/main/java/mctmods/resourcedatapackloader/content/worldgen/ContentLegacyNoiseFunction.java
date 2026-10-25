package mctmods.resourcedatapackloader.content.worldgen;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation") public record ContentLegacyNoiseFunction(DensityFunction noise, double coordinateScale, double heightScale, double mainX, double mainY, double mainZ, double lower, double upper) implements DensityFunction {
    private static final int CELL_WIDTH = 4;
    private static final int CELL_HEIGHT = 8;
    private static final int MAIN_OCTAVES = 8;
    private static final int LIMIT_OCTAVES = 16;
    private static final double VANILLA_LIMIT = 512.0D;
    private static final MapCodec<ContentLegacyNoiseFunction> DATA = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("noise").forGetter(ContentLegacyNoiseFunction::noise),
            Codec.DOUBLE.fieldOf("coordinate_scale").forGetter(ContentLegacyNoiseFunction::coordinateScale),
            Codec.DOUBLE.fieldOf("height_scale").forGetter(ContentLegacyNoiseFunction::heightScale),
            Codec.DOUBLE.fieldOf("main_scale_x").forGetter(ContentLegacyNoiseFunction::mainX),
            Codec.DOUBLE.fieldOf("main_scale_y").forGetter(ContentLegacyNoiseFunction::mainY),
            Codec.DOUBLE.fieldOf("main_scale_z").forGetter(ContentLegacyNoiseFunction::mainZ),
            Codec.DOUBLE.fieldOf("lower_limit_scale").forGetter(ContentLegacyNoiseFunction::lower),
            Codec.DOUBLE.fieldOf("upper_limit_scale").forGetter(ContentLegacyNoiseFunction::upper)).apply(instance, ContentLegacyNoiseFunction::new));
    public static final KeyDispatchDataCodec<ContentLegacyNoiseFunction> CODEC = KeyDispatchDataCodec.of(DATA);

    @Override public double compute(@Nonnull FunctionContext context) {
        if (!(noise instanceof BlendedNoise blended)) { return noise.compute(context); }
        double xzMultiplier = coordinateScale / CELL_WIDTH;
        double yMultiplier = heightScale / CELL_HEIGHT;
        double limitX = context.blockX() * xzMultiplier;
        double limitY = context.blockY() * yMultiplier;
        double limitZ = context.blockZ() * xzMultiplier;
        double sampleX = limitX / mainX;
        double sampleY = limitY / mainY;
        double sampleZ = limitZ / mainZ;
        double limitSmear = yMultiplier * blended.smearScaleMultiplier;
        double mainSmear = limitSmear / mainY;
        double mainValue = 0.0D;
        double pow = 1.0D;
        for (int octave = 0; octave < MAIN_OCTAVES; octave++) {
            ImprovedNoise level = blended.mainNoise.getOctaveNoise(octave);
            if (level != null) { mainValue += level.noise(PerlinNoise.wrap(sampleX * pow), PerlinNoise.wrap(sampleY * pow), PerlinNoise.wrap(sampleZ * pow), mainSmear * pow, sampleY * pow) / pow; }
            pow /= 2.0D;
        }
        double factor = (mainValue / 10.0D + 1.0D) / 2.0D;
        boolean isMax = factor >= 1.0D;
        boolean isMin = factor <= 0.0D;
        double blendMin = 0.0D;
        double blendMax = 0.0D;
        pow = 1.0D;
        for (int octave = 0; octave < LIMIT_OCTAVES; octave++) {
            double wx = PerlinNoise.wrap(limitX * pow);
            double wy = PerlinNoise.wrap(limitY * pow);
            double wz = PerlinNoise.wrap(limitZ * pow);
            double smear = limitSmear * pow;
            ImprovedNoise min = isMax ? null : blended.minLimitNoise.getOctaveNoise(octave);
            if (min != null) { blendMin += min.noise(wx, wy, wz, smear, limitY * pow) / pow; }
            ImprovedNoise max = isMin ? null : blended.maxLimitNoise.getOctaveNoise(octave);
            if (max != null) { blendMax += max.noise(wx, wy, wz, smear, limitY * pow) / pow; }
            pow /= 2.0D;
        }
        return Mth.clampedLerp(blendMin / lower, blendMax / upper, factor) / 128.0D;
    }

    @Override public void fillArray(@Nonnull double[] values, ContextProvider provider) { provider.fillAllDirectly(values, this); }

    @Override @Nonnull public DensityFunction mapAll(Visitor visitor) { return visitor.apply(new ContentLegacyNoiseFunction(noise.mapAll(visitor), coordinateScale, heightScale, mainX, mainY, mainZ, lower, upper)); }

    @Override public double minValue() { return -maxValue(); }

    @Override public double maxValue() {
        double bound = noise instanceof BlendedNoise blended ? blended.minLimitNoise.maxBrokenValue(heightScale / CELL_HEIGHT) : noise.maxValue();
        return bound * VANILLA_LIMIT / Math.min(lower, upper);
    }

    @Override @Nonnull public KeyDispatchDataCodec<? extends DensityFunction> codec() { return CODEC; }
}
