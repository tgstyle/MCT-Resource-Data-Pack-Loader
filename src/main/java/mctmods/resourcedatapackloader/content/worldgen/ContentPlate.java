package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public final class ContentPlate extends ContentShapeBase {
    public ContentPlate(ShapeDef shape) { super(shape); }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        int radius = radius(random);
        int thickness = Math.max(0, shape.height().pick(random));
        int centerY = origin.getY() + 1;
        boolean round = shape.isRound();
        return placer.disc(random, origin, radius, round, centerY - thickness, shape.slim() ? centerY + thickness - 1 : centerY + thickness);
    }
}
