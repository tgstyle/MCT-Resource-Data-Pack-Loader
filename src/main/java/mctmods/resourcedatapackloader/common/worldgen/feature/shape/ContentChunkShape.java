package mctmods.resourcedatapackloader.common.worldgen.feature.shape;

import mctmods.resourcedatapackloader.common.worldgen.placement.ContentPlacer;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ContentChunkShape extends ContentPlacedShape {
    protected final int minHeight;
    protected final int maxHeight;

    protected ContentChunkShape(ContentPlacer placer, int minHeight, int maxHeight) {
        super(placer);
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) { return false; }

    protected final int lowest(World world) { return lowest(world, minHeight); }

    protected final int highest(World world) { return highest(world, maxHeight); }

    protected final int lowest(World world, int least) { return Math.max(ContentPlacer.floorY(world), least); }

    protected final int highest(World world, int most) { return Math.min(ContentPlacer.ceilingY(world) - 1, most); }
}
