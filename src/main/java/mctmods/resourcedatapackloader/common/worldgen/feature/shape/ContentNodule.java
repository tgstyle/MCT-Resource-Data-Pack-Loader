package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.ShapeDef;
import mctmods.resourcedatapackloader.common.worldgen.feature.IContentShape;
import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public final class ContentNodule implements IContentShape {
    private final ContentPlacer placer;
    private final ShapeDef shape;

    public ContentNodule(ContentPlacer placer, ShapeDef shape) {
        this.placer = placer;
        this.shape = shape;
    }

    @Override public boolean places(World world, IBlockState wood, List<IBlockState> canopies) { return placer.offers(wood); }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int radius = MathHelper.clamp(shape.radius.pick(random), 1, ShapeDef.MOST_REACH);
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
