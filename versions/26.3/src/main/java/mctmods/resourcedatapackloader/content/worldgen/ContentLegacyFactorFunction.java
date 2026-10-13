package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

import javax.annotation.Nonnull;

public record ContentLegacyFactorFunction(DensityFunction factor, double stretchY, double scaleWeight, double scaleOffset) implements DensityFunction {
    private static final double NEAREST = 0.001D;
    public static final MapCodec<ContentLegacyFactorFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.CODEC.fieldOf("factor").forGetter(ContentLegacyFactorFunction::factor),
            Codec.DOUBLE.fieldOf("stretch_y").forGetter(ContentLegacyFactorFunction::stretchY),
            Codec.DOUBLE.fieldOf("scale_weight").forGetter(ContentLegacyFactorFunction::scaleWeight),
            Codec.DOUBLE.fieldOf("scale_offset").forGetter(ContentLegacyFactorFunction::scaleOffset)).apply(instance, ContentLegacyFactorFunction::new));

    private double divisor(double value) {
        double divisor = 0.9D * scaleOffset + 0.1D + scaleWeight * (0.75D / value - 0.1D);
        return Math.abs(divisor) >= NEAREST ? divisor : Math.copySign(NEAREST, divisor);
    }

    private float shaped(float value) { return (float) (stretchY / 16.0D / divisor(value)); }

    @Override @Nonnull public DensitySampler compileSampler(@Nonnull CompileContext context) { return new Sampler(this, factor.compileSampler(context)); }

    @Override @Nonnull public DensityFunction rewriteChildren(DfRewriteRule rule) {
        DensityFunction rewritten = rule.rewrite(factor);
        return rewritten == factor ? this : new ContentLegacyFactorFunction(rewritten, stretchY, scaleWeight, scaleOffset);
    }

    @Override @Nonnull public Interval range() {
        Interval inner = factor.range();
        if (inner.min() > 0.0F && Math.signum(divisor(inner.min())) == Math.signum(divisor(inner.max()))) { return Interval.encapsulating(shaped(inner.min()), shaped(inner.max())); }
        return Interval.ofSymmetric((float) Math.abs(stretchY / 16.0D / NEAREST));
    }

    @Override public @Axes int domainAxes() { return factor.domainAxes(); }

    @Override @Nonnull public MapCodec<ContentLegacyFactorFunction> codec() { return CODEC; }

    private record Sampler(ContentLegacyFactorFunction function, DensitySampler factor) implements DensitySampler {
        @Override public float sampleValue(@Nonnull SamplerContext context, int blockX, int blockY, int blockZ) { return function.shaped(factor.sampleValue(context, blockX, blockY, blockZ)); }

        @Override public void sampleVolume(@Nonnull SamplerContext context, @Nonnull DensityBuffer outputBuffer, @Nonnull DensityVolume volume) {
            factor.sampleVolume(context, outputBuffer, volume);
            for (int index = 0; index < volume.size(); index++) { outputBuffer.set(index, function.shaped(outputBuffer.get(index))); }
        }
    }
}
