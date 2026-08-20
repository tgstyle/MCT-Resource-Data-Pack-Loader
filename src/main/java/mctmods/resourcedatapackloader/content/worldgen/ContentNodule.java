package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public final class ContentNodule implements IContentShape {
    private final ContentPlacer placer;
    private final ShapeDef shape;

    public ContentNodule(ContentPlacer placer, ShapeDef shape) {
        this.placer = placer;
        this.shape = shape;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int radius = Math.min(ShapeDef.MOST_REACH, Math.max(1, shape.radius.pick(random)));
        int span = radius * radius;
        int core = shape.slim ? (radius - 1) * (radius - 1) : -1;
        boolean placed = false;
        for (int offX = -radius; offX <= radius; offX++) {
            for (int offY = -radius; offY <= radius; offY++) {
                for (int offZ = -radius; offZ <= radius; offZ++) {
                    int reach = offX * offX + offY * offY + offZ * offZ;
                    if (reach > span + random.nextInt(radius) || reach < core) { continue; }
                    placed |= placer.place(world, random, origin.getX() + offX, origin.getY() + offY, origin.getZ() + offZ);
                }
            }
        }
        return placed;
    }
}
