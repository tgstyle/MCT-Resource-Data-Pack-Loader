package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

import javax.annotation.Nonnull;

public record ContentSkyLandFunction(DensityFunction mask, DensityFunction cave, String dimension, int floor, int ceiling, double islands, double thickness) implements DensityFunction {
    private static final int LEAST_TAPER = 4;
    private static final int MOST_TAPER = 24;
    private static final double MOST_ISLANDS = 0.999D;
    public static final MapCodec<ContentSkyLandFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.CODEC.fieldOf("mask").forGetter(ContentSkyLandFunction::mask),
            DensityFunction.CODEC.fieldOf("cave").forGetter(ContentSkyLandFunction::cave),
            Codec.STRING.fieldOf("dimension").forGetter(ContentSkyLandFunction::dimension),
            Codec.INT.fieldOf("floor").forGetter(ContentSkyLandFunction::floor),
            Codec.INT.fieldOf("ceiling").forGetter(ContentSkyLandFunction::ceiling),
            Codec.DOUBLE.fieldOf("islands").forGetter(ContentSkyLandFunction::islands),
            Codec.DOUBLE.fieldOf("thickness").forGetter(ContentSkyLandFunction::thickness)).apply(instance, ContentSkyLandFunction::new));
    private static final KeyDispatchDataCodec<ContentSkyLandFunction> DISPATCH = KeyDispatchDataCodec.of(CODEC);

    @Override public double compute(FunctionContext context) {
        int y = context.blockY();
        int taper = Mth.clamp((ceiling - floor + 1) / 4, LEAST_TAPER, MOST_TAPER);
        int distance = Math.min(y - floor, ceiling - y);
        if (distance <= 0) { return -1.0D; }
        double fade = distance >= taper ? 1.0D : distance / (double) taper;
        ContentSkyLand.Shape shape = ContentSkyLand.shapeAt(dimension, context.blockX(), y, context.blockZ());
        double reach = Math.min(shape == null || Double.isNaN(shape.islands()) ? islands : shape.islands(), MOST_ISLANDS);
        double strength = (mask.compute(context) - reach) / (1.0D - reach) * fade;
        if (strength <= 0.0D) { return -1.0D; }
        double solid = shape == null || Double.isNaN(shape.thickness()) ? thickness : shape.thickness();
        return Mth.clamp(Math.min(strength, 1.0D) * solid - cave.compute(context), -1.0D, 1.0D);
    }

    @Override public void fillArray(@Nonnull double[] values, ContextProvider provider) { provider.fillAllDirectly(values, this); }

    @Override @Nonnull public DensityFunction mapChildren(Visitor visitor) { return new ContentSkyLandFunction(visitor.apply(mask), visitor.apply(cave), dimension, floor, ceiling, islands, thickness); }

    @Override public double minValue() { return -1.0D; }

    @Override public double maxValue() { return 1.0D; }

    @Override @Nonnull public KeyDispatchDataCodec<? extends DensityFunction> codec() { return DISPATCH; }
}
