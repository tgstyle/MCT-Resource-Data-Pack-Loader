package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public final class ContentPlate implements IContentShape {
    private final ShapeDef shape;

    public ContentPlate(ShapeDef shape) { this.shape = shape; }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        int radius = Math.min(ShapeDef.MOST_REACH, Math.max(0, shape.radius().pick(random)));
        int thickness = Math.max(0, shape.height().pick(random));
        int centerY = origin.getY() + 1;
        boolean round = shape.isRound();
        int square = radius * radius;
        boolean placed = false;
        for (int offX = -radius; offX <= radius; offX++) {
            for (int offZ = -radius; offZ <= radius; offZ++) {
                if (round && offX * offX + offZ * offZ > square) { continue; }
                int top = shape.slim() ? centerY + thickness - 1 : centerY + thickness;
                for (int y = centerY - thickness; y <= top; y++) { placed |= placer.place(random, origin.getX() + offX, y, origin.getZ() + offZ); }
            }
        }
        return placed;
    }
}
