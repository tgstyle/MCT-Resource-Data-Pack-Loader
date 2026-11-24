package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public final class ContentBasin extends ContentShapeBase {
    public ContentBasin(ShapeDef shape) { super(shape); }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        int radius = radius(random, 1, ShapeDef.MOST_REACH * 2);
        int depth = rise(random);
        boolean round = shape.isRound();
        int span = radius * radius;
        boolean placed = false;
        for (int offX = -radius; offX <= radius; offX++) {
            for (int offZ = -radius; offZ <= radius; offZ++) {
                int flat = offX * offX + offZ * offZ;
                if (round && flat > span) { continue; }
                int reach = round ? depth - (int) Math.round(Math.sqrt(flat) * depth / (double) radius) : depth;
                for (int offY = 0; offY < reach; offY++) { placed |= placer.place(random, origin.getX() + offX, origin.getY() - offY, origin.getZ() + offZ); }
            }
        }
        return placed;
    }
}
