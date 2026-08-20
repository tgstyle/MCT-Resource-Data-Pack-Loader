package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public final class ContentSpire implements IContentShape {
    private final ContentPlacer placer;
    private final ShapeDef shape;

    public ContentSpire(ContentPlacer placer, ShapeDef shape) {
        this.placer = placer;
        this.shape = shape;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int radius = Math.min(ShapeDef.MOST_REACH, Math.max(0, shape.radius.pick(random)));
        int height = Math.max(1, shape.height.pick(random));
        int step = shape.hanging ? -1 : 1;
        boolean round = shape.isRound();
        boolean placed = false;
        for (int level = 0; level < height; level++) {
            int reach = reach(radius, level / (double) height);
            int span = reach * reach;
            int y = origin.getY() + step * level;
            for (int offX = -reach; offX <= reach; offX++) {
                for (int offZ = -reach; offZ <= reach; offZ++) {
                    if (round && offX * offX + offZ * offZ > span) { continue; }
                    placed |= placer.place(world, random, origin.getX() + offX, y, origin.getZ() + offZ);
                }
            }
        }
        return placed;
    }

    private int reach(int radius, double climbed) {
        if (ShapeDef.BELL.equals(shape.taper)) { return (int) Math.round(radius * Math.sqrt(Math.max(0.0D, 1.0D - climbed * climbed))); }
        if (ShapeDef.NEEDLE.equals(shape.taper)) { return (int) Math.round(radius * (1.0D - climbed) * (1.0D - climbed)); }
        return (int) Math.round(radius * (1.0D - climbed));
    }
}
