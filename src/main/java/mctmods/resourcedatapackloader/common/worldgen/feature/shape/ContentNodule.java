package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public final class ContentNodule extends ContentShapeBase {
    public ContentNodule(ShapeDef shape) { super(shape); }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        int radius = radius(random, 1, ShapeDef.MOST_REACH);
        int span = radius * radius;
        int core = shape.slim() ? (radius - 1) * (radius - 1) : -1;
        boolean placed = false;
        for (int offX = -radius; offX <= radius; offX++) {
            for (int offY = -radius; offY <= radius; offY++) {
                for (int offZ = -radius; offZ <= radius; offZ++) {
                    int reach = offX * offX + offY * offY + offZ * offZ;
                    if (reach > span + random.nextInt(radius) || reach < core) { continue; }
                    placed |= placer.place(random, origin.getX() + offX, origin.getY() + offY, origin.getZ() + offZ);
                }
            }
        }
        return placed;
    }
}
