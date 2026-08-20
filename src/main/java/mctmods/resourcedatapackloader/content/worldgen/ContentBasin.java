package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public final class ContentBasin implements IContentShape {
    private final ContentPlacer placer;
    private final ShapeDef shape;

    public ContentBasin(ContentPlacer placer, ShapeDef shape) {
        this.placer = placer;
        this.shape = shape;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int radius = Math.min(ShapeDef.MOST_REACH * 2, Math.max(1, shape.radius.pick(random)));
        int depth = Math.max(1, shape.height.pick(random));
        boolean round = shape.isRound();
        int span = radius * radius;
        boolean placed = false;
        for (int offX = -radius; offX <= radius; offX++) {
            for (int offZ = -radius; offZ <= radius; offZ++) {
                int flat = offX * offX + offZ * offZ;
                if (round && flat > span) { continue; }
                int reach = round ? depth - (int) Math.round(Math.sqrt(flat) * depth / (double) radius) : depth;
                for (int offY = 0; offY < reach; offY++) { placed |= placer.place(world, random, origin.getX() + offX, origin.getY() - offY, origin.getZ() + offZ); }
            }
        }
        return placed;
    }
}
