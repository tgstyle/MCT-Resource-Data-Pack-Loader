package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public final class ContentSpire extends ContentShapeBase {
    public ContentSpire(ShapeDef shape) { super(shape); }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        return discColumn(placer, random, origin, (radius, level, height) -> reach(radius, level / (double) height), false);
    }

    private int reach(int radius, double climbed) {
        if (ShapeDef.BELL.equals(shape.taper())) { return (int) Math.round(radius * Math.sqrt(Math.max(0.0D, 1.0D - climbed * climbed))); }
        if (ShapeDef.NEEDLE.equals(shape.taper())) { return (int) Math.round(radius * (1.0D - climbed) * (1.0D - climbed)); }
        return (int) Math.round(radius * (1.0D - climbed));
    }
}
