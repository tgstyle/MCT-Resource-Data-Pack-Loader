package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.LegacyTerrainShape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import javax.annotation.Nonnull;

public record ContentLegacyOffsetFunction(DensityFunction offset, double scale, double shift, NoiseHolder noise, double depthX, double depthZ, double baseSize) implements DensityFunction {
    private static final double CELL_WIDTH = 4.0D;
    private static final MapCodec<ContentLegacyOffsetFunction> DATA = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("offset").forGetter(ContentLegacyOffsetFunction::offset),
            Codec.DOUBLE.fieldOf("scale").forGetter(ContentLegacyOffsetFunction::scale),
            Codec.DOUBLE.fieldOf("shift").forGetter(ContentLegacyOffsetFunction::shift),
            NoiseHolder.CODEC.fieldOf("noise").forGetter(ContentLegacyOffsetFunction::noise),
            Codec.DOUBLE.fieldOf("depth_scale_x").forGetter(ContentLegacyOffsetFunction::depthX),
            Codec.DOUBLE.fieldOf("depth_scale_z").forGetter(ContentLegacyOffsetFunction::depthZ),
            Codec.DOUBLE.fieldOf("base_size").forGetter(ContentLegacyOffsetFunction::baseSize)).apply(instance, ContentLegacyOffsetFunction::new));
    public static final KeyDispatchDataCodec<ContentLegacyOffsetFunction> CODEC = KeyDispatchDataCodec.of(DATA);

    @Override public double compute(@Nonnull FunctionContext context) { return scale * offset.compute(context) + shift + depth(context.blockX(), context.blockZ()); }

    private double depth(int x, int z) {
        NormalNoise seeded = noise.noise();
        if (seeded == null) { return 0.0D; }
        double raw = seeded.first.getValue(x * depthX / CELL_WIDTH, 0.0D, z * depthZ / CELL_WIDTH) * LegacyTerrainShape.DEPTH_SUM;
        return LegacyTerrainShape.depthOffset(raw, baseSize);
    }

    @Override public void fillArray(@Nonnull double[] values, ContextProvider provider) { provider.fillAllDirectly(values, this); }

    @Override @Nonnull public DensityFunction mapAll(Visitor visitor) { return visitor.apply(new ContentLegacyOffsetFunction(offset.mapAll(visitor), scale, shift, visitor.visitNoise(noise), depthX, depthZ, baseSize)); }

    @Override public double minValue() { return Math.min(scale * offset.minValue(), scale * offset.maxValue()) + shift + LegacyTerrainShape.lowestDepth(baseSize); }

    @Override public double maxValue() { return Math.max(scale * offset.minValue(), scale * offset.maxValue()) + shift + LegacyTerrainShape.highestDepth(baseSize); }

    @Override @Nonnull public KeyDispatchDataCodec<? extends DensityFunction> codec() { return CODEC; }
}
