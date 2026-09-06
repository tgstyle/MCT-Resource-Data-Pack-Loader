package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public final class ContentVent implements IContentShape {
    private final ShapeDef shape;

    public ContentVent(ShapeDef shape) { this.shape = shape; }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        int radius = Math.min(ShapeDef.MOST_REACH, Math.max(0, shape.radius().pick(random)));
        int height = Math.max(1, shape.height().pick(random));
        int step = shape.hanging() ? -1 : 1;
        boolean round = shape.isRound();
        int span = radius * radius;
        boolean placed = false;
        for (int level = 0; level < height; level++) {
            int y = origin.getY() + step * level;
            boolean any = false;
            for (int offX = -radius; offX <= radius; offX++) {
                for (int offZ = -radius; offZ <= radius; offZ++) {
                    if (round && offX * offX + offZ * offZ > span) { continue; }
                    any |= placer.place(random, origin.getX() + offX, y, origin.getZ() + offZ);
                }
            }
            if (!any) { break; }
            placed = true;
        }
        return placed;
    }
}
