package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.LegacyTerrainShape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Interval;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.synth.NoiseStack;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import javax.annotation.Nonnull;

public record ContentLegacyOffsetFunction(DensityFunction offset, double scale, double shift, Identifier noise, double depthX, double depthZ, double baseSize) implements DensityFunction {
    private static final double CELL_WIDTH = 4.0D;
    public static final MapCodec<ContentLegacyOffsetFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.CODEC.fieldOf("offset").forGetter(ContentLegacyOffsetFunction::offset),
            Codec.DOUBLE.fieldOf("scale").forGetter(ContentLegacyOffsetFunction::scale),
            Codec.DOUBLE.fieldOf("shift").forGetter(ContentLegacyOffsetFunction::shift),
            Identifier.CODEC.fieldOf("noise").forGetter(ContentLegacyOffsetFunction::noise),
            Codec.DOUBLE.fieldOf("depth_scale_x").forGetter(ContentLegacyOffsetFunction::depthX),
            Codec.DOUBLE.fieldOf("depth_scale_z").forGetter(ContentLegacyOffsetFunction::depthZ),
            Codec.DOUBLE.fieldOf("base_size").forGetter(ContentLegacyOffsetFunction::baseSize)).apply(instance, ContentLegacyOffsetFunction::new));

    @Override @Nonnull public DensitySampler compileSampler(@Nonnull CompileContext context) {
        RandomSource random = context.createRandom(noise);
        NoiseStack.Builder depth = NoiseStack.builder();
        double amplitude = 1.0D;
        for (int octave = 0; octave < LegacyTerrainShape.DEPTH_OCTAVES; octave++) {
            depth.add(new PerlinNoise(random), 1.0D / amplitude, (float) amplitude);
            amplitude *= 2.0D;
        }
        return new Sampler(this, offset.compileSampler(context), depth.build());
    }

    private float value(float inner, int x, int z, NoiseStack depth) {
        double raw = depth.get(x * depthX / CELL_WIDTH, z * depthZ / CELL_WIDTH);
        return (float) (scale * inner + shift + LegacyTerrainShape.depthOffset(raw, baseSize));
    }

    @Override @Nonnull public DensityFunction rewriteChildren(DfRewriteRule rule) {
        DensityFunction rewritten = rule.rewrite(offset);
        return rewritten == offset ? this : new ContentLegacyOffsetFunction(rewritten, scale, shift, noise, depthX, depthZ, baseSize);
    }

    @Override @Nonnull public Interval range() {
        Interval scaled = Interval.add(Interval.mul(offset.range(), Interval.ofExact((float) scale)), Interval.ofExact((float) shift));
        return Interval.add(scaled, Interval.of((float) LegacyTerrainShape.lowestDepth(baseSize), (float) LegacyTerrainShape.highestDepth(baseSize)));
    }

    @Override public @Axes int domainAxes() { return offset.domainAxes() | AXIS_X | AXIS_Z; }

    @Override @Nonnull public MapCodec<ContentLegacyOffsetFunction> codec() { return CODEC; }

    private record Sampler(ContentLegacyOffsetFunction function, DensitySampler offset, NoiseStack depth) implements DensitySampler {
        @Override public float sampleValue(@Nonnull SamplerContext context, int blockX, int blockY, int blockZ) { return function.value(offset.sampleValue(context, blockX, blockY, blockZ), blockX, blockZ, depth); }

        @Override public void sampleVolume(@Nonnull SamplerContext context, @Nonnull DensityBuffer outputBuffer, @Nonnull DensityVolume volume) {
            offset.sampleVolume(context, outputBuffer, volume);
            int index = 0;
            for (int z = 0; z < volume.sizeZ(); z++) {
                int blockZ = volume.blockZ(z);
                for (int x = 0; x < volume.sizeX(); x++) {
                    int blockX = volume.blockX(x);
                    for (int y = 0; y < volume.sizeY(); y++) {
                        outputBuffer.set(index, function.value(outputBuffer.get(index), blockX, blockZ, depth));
                        index++;
                    }
                }
            }
        }
    }
}
