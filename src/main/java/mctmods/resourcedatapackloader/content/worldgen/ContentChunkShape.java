package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.List;
import java.util.Random;

public abstract class ContentChunkShape implements IContentShape {
    protected static final int OFFSET = 8;
    protected final ContentPlacer placer;
    protected final int minHeight;
    protected final int maxHeight;

    protected ContentChunkShape(ContentPlacer placer, int minHeight, int maxHeight) {
        this.placer = placer;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    @Override public boolean places(World world, IBlockState wood, List<IBlockState> canopies) { return placer.offers(wood); }

    @Override public boolean generate(World world, Random random, BlockPos origin) { return false; }

    protected final int lowest(World world) { return Math.max(ContentPlacer.floorY(world), minHeight); }

    protected final int highest(World world) { return Math.min(ContentPlacer.ceilingY(world) - 1, maxHeight); }
}
