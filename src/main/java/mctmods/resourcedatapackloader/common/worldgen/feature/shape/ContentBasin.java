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

public final class ContentBasin implements IContentShape {
    private final ContentPlacer placer;
    private final ShapeDef shape;

    public ContentBasin(ContentPlacer placer, ShapeDef shape) {
        this.placer = placer;
        this.shape = shape;
    }

    @Override public boolean places(World world, IBlockState wood, List<IBlockState> canopies) { return placer.offers(wood); }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        int radius = MathHelper.clamp(shape.radius.pick(random), 1, ShapeDef.MOST_REACH * 2);
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
