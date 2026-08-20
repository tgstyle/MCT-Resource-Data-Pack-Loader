package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public final class ContentVent implements IContentShape {
    private final ContentPlacer placer;
    private final ShapeDef shape;

    public ContentVent(ContentPlacer placer, ShapeDef shape) {
        this.placer = placer;
        this.shape = shape;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int radius = Math.min(ShapeDef.MOST_REACH, Math.max(0, shape.radius.pick(random)));
        int height = Math.max(1, shape.height.pick(random));
        int step = shape.hanging ? -1 : 1;
        boolean round = shape.isRound();
        int span = radius * radius;
        boolean placed = false;
        for (int level = 0; level < height; level++) {
            int y = origin.getY() + step * level;
            boolean any = false;
            for (int offX = -radius; offX <= radius; offX++) {
                for (int offZ = -radius; offZ <= radius; offZ++) {
                    if (round && offX * offX + offZ * offZ > span) { continue; }
                    any |= placer.place(world, random, origin.getX() + offX, y, origin.getZ() + offZ);
                }
            }
            if (!any) { break; }
            placed = true;
        }
        return placed;
    }
}
