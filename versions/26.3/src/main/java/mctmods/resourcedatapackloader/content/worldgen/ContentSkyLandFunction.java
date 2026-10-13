package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;

import javax.annotation.Nonnull;

public record ContentSkyLandFunction(DensityFunction mask, DensityFunction cave, String dimension, int floor, int ceiling, double islands, double thickness) implements DensityFunction {
    private static final int LEAST_TAPER = 4;
    private static final int MOST_TAPER = 24;
    private static final double MOST_ISLANDS = 0.999D;
    private static final Interval RANGE = Interval.of(-1.0F, 1.0F);
    public static final MapCodec<ContentSkyLandFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.CODEC.fieldOf("mask").forGetter(ContentSkyLandFunction::mask),
            DensityFunction.CODEC.fieldOf("cave").forGetter(ContentSkyLandFunction::cave),
            Codec.STRING.fieldOf("dimension").forGetter(ContentSkyLandFunction::dimension),
            Codec.INT.fieldOf("floor").forGetter(ContentSkyLandFunction::floor),
            Codec.INT.fieldOf("ceiling").forGetter(ContentSkyLandFunction::ceiling),
            Codec.DOUBLE.fieldOf("islands").forGetter(ContentSkyLandFunction::islands),
            Codec.DOUBLE.fieldOf("thickness").forGetter(ContentSkyLandFunction::thickness)).apply(instance, ContentSkyLandFunction::new));

    @Override @Nonnull public DensitySampler compileSampler(@Nonnull CompileContext context) { return new Sampler(this, mask.compileSampler(context), cave.compileSampler(context)); }

    @Override @Nonnull public DensityFunction rewriteChildren(DfRewriteRule rule) {
        DensityFunction rewrittenMask = rule.rewrite(mask);
        DensityFunction rewrittenCave = rule.rewrite(cave);
        return rewrittenMask == mask && rewrittenCave == cave ? this : new ContentSkyLandFunction(rewrittenMask, rewrittenCave, dimension, floor, ceiling, islands, thickness);
    }

    @Override @Nonnull public Interval range() { return RANGE; }

    @Override public @Axes int domainAxes() { return ALL_AXES; }

    @Override @Nonnull public MapCodec<ContentSkyLandFunction> codec() { return CODEC; }

    private int distance(int y) { return Math.min(y - floor, ceiling - y); }

    private float density(int x, int y, int z, float maskValue, float caveValue) {
        int taper = Mth.clamp((ceiling - floor + 1) / 4, LEAST_TAPER, MOST_TAPER);
        int distance = distance(y);
        double fade = distance >= taper ? 1.0D : distance / (double) taper;
        ContentSkyLand.Shape shape = ContentSkyLand.shapeAt(dimension, x, y, z);
        double reach = Math.min(shape == null || Double.isNaN(shape.islands()) ? islands : shape.islands(), MOST_ISLANDS);
        double strength = (maskValue - reach) / (1.0D - reach) * fade;
        if (strength <= 0.0D) { return -1.0F; }
        double solid = shape == null || Double.isNaN(shape.thickness()) ? thickness : shape.thickness();
        return (float) Mth.clamp(Math.min(strength, 1.0D) * solid - caveValue, -1.0D, 1.0D);
    }

    private record Sampler(ContentSkyLandFunction function, DensitySampler mask, DensitySampler cave) implements DensitySampler {
        @Override public float sampleValue(@Nonnull SamplerContext context, int blockX, int blockY, int blockZ) {
            if (function.distance(blockY) <= 0) { return -1.0F; }
            return function.density(blockX, blockY, blockZ, mask.sampleValue(context, blockX, blockY, blockZ), cave.sampleValue(context, blockX, blockY, blockZ));
        }

        @Override public void sampleVolume(@Nonnull SamplerContext context, @Nonnull DensityBuffer outputBuffer, @Nonnull DensityVolume volume) {
            try (ScopedDensityBuffer masks = context.acquireBuffer(volume); ScopedDensityBuffer caves = context.acquireBuffer(volume)) {
                mask.sampleVolume(context, masks, volume);
                cave.sampleVolume(context, caves, volume);
                int index = 0;
                for (int z = 0; z < volume.sizeZ(); z++) {
                    int blockZ = volume.blockZ(z);
                    for (int x = 0; x < volume.sizeX(); x++) {
                        int blockX = volume.blockX(x);
                        for (int y = 0; y < volume.sizeY(); y++) {
                            int blockY = volume.blockY(y);
                            outputBuffer.set(index, function.distance(blockY) <= 0 ? -1.0F : function.density(blockX, blockY, blockZ, masks.get(index), caves.get(index)));
                            index++;
                        }
                    }
                }
            }
        }
    }
}
