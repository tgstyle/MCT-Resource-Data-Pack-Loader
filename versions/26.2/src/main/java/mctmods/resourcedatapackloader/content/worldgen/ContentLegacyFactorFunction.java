package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

import javax.annotation.Nonnull;

public record ContentLegacyFactorFunction(DensityFunction factor, double stretchY, double scaleWeight, double scaleOffset) implements DensityFunction {
    private static final double NEAREST = 0.001D;
    public static final MapCodec<ContentLegacyFactorFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.CODEC.fieldOf("factor").forGetter(ContentLegacyFactorFunction::factor),
            Codec.DOUBLE.fieldOf("stretch_y").forGetter(ContentLegacyFactorFunction::stretchY),
            Codec.DOUBLE.fieldOf("scale_weight").forGetter(ContentLegacyFactorFunction::scaleWeight),
            Codec.DOUBLE.fieldOf("scale_offset").forGetter(ContentLegacyFactorFunction::scaleOffset)).apply(instance, ContentLegacyFactorFunction::new));
    private static final KeyDispatchDataCodec<ContentLegacyFactorFunction> DISPATCH = KeyDispatchDataCodec.of(CODEC);

    private double divisor(double value) {
        double divisor = 0.9D * scaleOffset + 0.1D + scaleWeight * (0.75D / value - 0.1D);
        return Math.abs(divisor) >= NEAREST ? divisor : Math.copySign(NEAREST, divisor);
    }

    private double shaped(double value) { return stretchY / 16.0D / divisor(value); }

    @Override public double compute(@Nonnull FunctionContext context) { return shaped(factor.compute(context)); }

    @Override public void fillArray(@Nonnull double[] values, @Nonnull ContextProvider provider) {
        factor.fillArray(values, provider);
        for (int index = 0; index < values.length; index++) { values[index] = shaped(values[index]); }
    }

    @Override @Nonnull public DensityFunction mapChildren(Visitor visitor) { return new ContentLegacyFactorFunction(visitor.apply(factor), stretchY, scaleWeight, scaleOffset); }

    private boolean bounded() {
        double low = factor.minValue();
        return low > 0.0D && Math.signum(divisor(low)) == Math.signum(divisor(factor.maxValue()));
    }

    private double widest() { return Math.abs(stretchY / 16.0D / NEAREST); }

    @Override public double minValue() { return bounded() ? Math.min(shaped(factor.minValue()), shaped(factor.maxValue())) : -widest(); }

    @Override public double maxValue() { return bounded() ? Math.max(shaped(factor.minValue()), shaped(factor.maxValue())) : widest(); }

    @Override @Nonnull public KeyDispatchDataCodec<? extends DensityFunction> codec() { return DISPATCH; }
}
