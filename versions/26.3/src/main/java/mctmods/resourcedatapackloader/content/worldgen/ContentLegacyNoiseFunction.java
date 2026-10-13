package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.generator.NoiseFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.BinaryFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.ClampFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.LerpFunction;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NoiseStack;

import javax.annotation.Nonnull;

public record ContentLegacyNoiseFunction(DensityFunction noise, double coordinateScale, double heightScale, double mainX, double mainY, double mainZ, double lower, double upper) implements DensityFunction {
    private static final int CELL_WIDTH = 4;
    private static final int CELL_HEIGHT = 8;
    private static final int LIMIT_FIRST_OCTAVE = -15;
    private static final int MAIN_FIRST_OCTAVE = -7;
    private static final double LIMIT_FACTOR = 0.99998474F;
    private static final double MAIN_FACTOR = 12.75D;
    private static final double VANILLA_LIMIT = 512.0D;
    private static final float CHOICE_CENTER = 0.5F;
    public static final MapCodec<ContentLegacyNoiseFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.CODEC.fieldOf("noise").forGetter(ContentLegacyNoiseFunction::noise),
            Codec.DOUBLE.fieldOf("coordinate_scale").forGetter(ContentLegacyNoiseFunction::coordinateScale),
            Codec.DOUBLE.fieldOf("height_scale").forGetter(ContentLegacyNoiseFunction::heightScale),
            Codec.DOUBLE.fieldOf("main_scale_x").forGetter(ContentLegacyNoiseFunction::mainX),
            Codec.DOUBLE.fieldOf("main_scale_y").forGetter(ContentLegacyNoiseFunction::mainY),
            Codec.DOUBLE.fieldOf("main_scale_z").forGetter(ContentLegacyNoiseFunction::mainZ),
            Codec.DOUBLE.fieldOf("lower_limit_scale").forGetter(ContentLegacyNoiseFunction::lower),
            Codec.DOUBLE.fieldOf("upper_limit_scale").forGetter(ContentLegacyNoiseFunction::upper)).apply(instance, ContentLegacyNoiseFunction::new));

    @Override @Nonnull public DensitySampler compileSampler(@Nonnull CompileContext context) {
        if (!(noise instanceof BlendedNoise blended)) { return noise.compileSampler(context); }
        RandomSource random = context.createRandom(BlendedNoise.NOISE_SEED);
        double xzMultiplier = coordinateScale / CELL_WIDTH;
        double yMultiplier = heightScale / CELL_HEIGHT;
        double limitSmear = yMultiplier * blended.smearScaleMultiplier();
        NoiseStack min = BlendedNoise.createFbm(random, LIMIT_FIRST_OCTAVE, limitSmear, LIMIT_FACTOR * VANILLA_LIMIT / lower);
        NoiseStack max = BlendedNoise.createFbm(random, LIMIT_FIRST_OCTAVE, limitSmear, LIMIT_FACTOR * VANILLA_LIMIT / upper);
        NoiseStack main = BlendedNoise.createFbm(random, MAIN_FIRST_OCTAVE, limitSmear / mainY, MAIN_FACTOR);
        DensitySampler mainSampler = mainX == mainZ ? new NoiseFunction.Sampler(main, xzMultiplier / mainX, yMultiplier / mainY) : new Uneven(main, xzMultiplier / mainX, yMultiplier / mainY, xzMultiplier / mainZ);
        DensitySampler choice = new ClampFunction.Sampler(new BinaryFunction.ConstAddSampler(mainSampler, CHOICE_CENTER), 0.0F, 1.0F);
        return new LerpFunction.Sampler(choice, new NoiseFunction.Sampler(min, xzMultiplier, yMultiplier), new NoiseFunction.Sampler(max, xzMultiplier, yMultiplier));
    }

    @Override @Nonnull public DensityFunction rewriteChildren(@Nonnull DfRewriteRule rule) { return this; }

    @Override @Nonnull public Interval range() {
        if (!(noise instanceof BlendedNoise blended)) { return noise.range(); }
        double smear = Math.abs(heightScale / CELL_HEIGHT * blended.smearScaleMultiplier());
        return Interval.ofSymmetric((float) ((smear + 2.0D) * LIMIT_FACTOR * VANILLA_LIMIT / Math.min(lower, upper)));
    }

    @Override public @Axes int domainAxes() { return ALL_AXES; }

    @Override @Nonnull public MapCodec<ContentLegacyNoiseFunction> codec() { return CODEC; }

    private record Uneven(Noise noise, double xScale, double yScale, double zScale) implements DensitySampler {
        @Override public float sampleValue(@Nonnull SamplerContext context, int blockX, int blockY, int blockZ) { return noise.get(blockX * xScale, blockY * yScale, blockZ * zScale); }

        @Override public void sampleVolume(@Nonnull SamplerContext context, @Nonnull DensityBuffer outputBuffer, @Nonnull DensityVolume volume) {
            int index = 0;
            for (int z = 0; z < volume.sizeZ(); z++) {
                double sampleZ = volume.blockZ(z) * zScale;
                for (int x = 0; x < volume.sizeX(); x++) {
                    double sampleX = volume.blockX(x) * xScale;
                    for (int y = 0; y < volume.sizeY(); y++) {
                        outputBuffer.set(index, noise.get(sampleX, volume.blockY(y) * yScale, sampleZ));
                        index++;
                    }
                }
            }
        }
    }
}
