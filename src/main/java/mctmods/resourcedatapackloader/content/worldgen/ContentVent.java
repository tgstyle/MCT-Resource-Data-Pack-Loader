package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;

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
