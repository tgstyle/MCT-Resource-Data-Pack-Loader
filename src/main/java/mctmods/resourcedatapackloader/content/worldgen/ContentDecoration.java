package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;
import java.util.Set;

public final class ContentDecoration implements IContentShape {
    private final ContentPlacer placer;
    private final AmountDef size;
    private final AmountDef stack;
    private final Set<Block> surface;
    private final boolean seeSky;
    private final boolean checkStay;
    private final int scatterX;
    private final int scatterY;
    private final int scatterZ;

    public ContentDecoration(ContentPlacer placer, AmountDef size, ShapeDef shape, Set<Block> surface) {
        this.placer = placer;
        this.size = size;
        this.stack = shape.stack;
        this.surface = surface;
        this.seeSky = shape.seeSky;
        this.checkStay = shape.checkStay;
        this.scatterX = shape.scatterX;
        this.scatterY = shape.scatterY;
        this.scatterZ = shape.scatterZ;
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        boolean placed = false;
        int placements = size.pick(random);
        for (int attempt = 0; attempt < placements; attempt++) {
            int x = origin.getX() + scatter(random, scatterX);
            int z = origin.getZ() + scatter(random, scatterZ);
            int y = origin.getY() + scatter(random, scatterY);
            BlockPos pos = new BlockPos(x, y, z);
            if (!world.isAreaLoaded(pos, 3)) { continue; }
            if (seeSky && !world.canSeeSky(pos)) { continue; }
            if (!surface.isEmpty() && !surface.contains(world.getBlockState(pos.down()).getBlock())) { continue; }
            if (placer.occupied(world, x, y, z)) { continue; }
            IBlockState chosen = placer.choose(random);
            int height = stack.pick(random);
            for (int level = 0; level < height; level++) {
                if (checkStay && !chosen.getBlock().canPlaceBlockAt(world, pos)) { break; }
                placed |= placer.placeExactly(world, chosen, x, y + level, z);
                if (level + 1 >= height) { break; }
                if (placer.occupied(world, x, y + level + 1, z)) { break; }
                pos = pos.up();
            }
        }
        return placed;
    }

    private static int scatter(Random random, int bound) {
        if (bound <= 0) { return 0; }
        return random.nextInt(bound) - random.nextInt(bound);
    }
}
