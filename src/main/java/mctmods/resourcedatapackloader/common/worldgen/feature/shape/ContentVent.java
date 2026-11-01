package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public final class ContentVent extends ContentShapeBase {
    public ContentVent(ShapeDef shape) { super(shape); }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        int radius = radius(random);
        int height = rise(random);
        int step = step();
        boolean round = shape.isRound();
        boolean placed = false;
        for (int level = 0; level < height; level++) {
            int y = origin.getY() + step * level;
            if (!placer.disc(random, origin, radius, round, y, y)) { break; }
            placed = true;
        }
        return placed;
    }
}
