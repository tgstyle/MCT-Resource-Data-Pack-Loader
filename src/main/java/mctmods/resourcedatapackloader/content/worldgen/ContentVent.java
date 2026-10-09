package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public final class ContentVent extends ContentShapeBase {
    public ContentVent(ContentPlacer placer, ShapeDef shape) { super(placer, shape); }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int radius = radius(random);
        int height = rise(random);
        int step = step();
        boolean round = shape.isRound();
        boolean placed = false;
        for (int level = 0; level < height; level++) {
            int y = origin.getY() + step * level;
            if (!placer.disc(world, random, origin, radius, round, y, y)) { break; }
            placed = true;
        }
        return placed;
    }
}
