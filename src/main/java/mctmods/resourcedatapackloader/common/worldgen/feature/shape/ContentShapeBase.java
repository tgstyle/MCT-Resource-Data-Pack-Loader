package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.pack.definitions.items.AmountDef;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

abstract class ContentShapeBase extends ContentPlacedShape {
    final ShapeDef shape;

    ContentShapeBase(ContentPlacer placer, ShapeDef shape) {
        super(placer);
        this.shape = shape;
    }

    final int radius(Random random) { return radius(random, 0, ShapeDef.MOST_REACH); }

    final int radius(Random random, int least, int most) { return radius(random, shape.radius, least, most); }

    final int radius(Random random, AmountDef amount, int least, int most) { return MathHelper.clamp(amount.pick(random), least, most); }

    final int rise(Random random) { return rise(random, 1); }

    final int rise(Random random, int least) { return Math.max(least, shape.height.pick(random)); }

    final int step() { return shape.hanging ? -1 : 1; }

    final boolean discColumn(World world, Random random, BlockPos origin, Reach reachAt, boolean stopOnMiss) {
        int radius = radius(random);
        int height = rise(random);
        int step = step();
        boolean round = shape.isRound();
        boolean placed = false;
        for (int level = 0; level < height; level++) {
            int y = origin.getY() + step * level;
            boolean hit = placer.disc(world, random, origin, reachAt.at(radius, level, height), round, y, y);
            if (!hit && stopOnMiss) { break; }
            placed |= hit;
        }
        return placed;
    }

    interface Reach { int at(int radius, int level, int height); }
}
