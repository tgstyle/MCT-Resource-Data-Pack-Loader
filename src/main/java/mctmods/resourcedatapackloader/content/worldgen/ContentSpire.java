package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public final class ContentSpire extends ContentShapeBase {
    public ContentSpire(ContentPlacer placer, ShapeDef shape) { super(placer, shape); }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int radius = radius(random);
        int height = rise(random);
        int step = step();
        boolean round = shape.isRound();
        boolean placed = false;
        for (int level = 0; level < height; level++) {
            int reach = reach(radius, level / (double) height);
            int y = origin.getY() + step * level;
            placed |= placer.disc(world, random, origin, reach, round, y, y);
        }
        return placed;
    }

    private int reach(int radius, double climbed) {
        if (ShapeDef.BELL.equals(shape.taper)) { return (int) Math.round(radius * Math.sqrt(Math.max(0.0D, 1.0D - climbed * climbed))); }
        if (ShapeDef.NEEDLE.equals(shape.taper)) { return (int) Math.round(radius * (1.0D - climbed) * (1.0D - climbed)); }
        return (int) Math.round(radius * (1.0D - climbed));
    }
}
