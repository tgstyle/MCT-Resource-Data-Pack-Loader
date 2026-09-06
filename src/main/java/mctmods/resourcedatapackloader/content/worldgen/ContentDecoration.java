package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ContentDecoration implements IContentShape {
    private final AmountDef size;
    private final AmountDef stack;
    private final boolean seeSky;
    private final boolean checkStay;
    private final int scatterX;
    private final int scatterY;
    private final int scatterZ;

    public ContentDecoration(AmountDef size, ShapeDef shape) {
        this.size = size;
        this.stack = shape.stack();
        this.seeSky = shape.seeSky();
        this.checkStay = shape.checkStay();
        this.scatterX = shape.scatterX();
        this.scatterY = shape.scatterY();
        this.scatterZ = shape.scatterZ();
    }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        WorldGenLevel level = placer.level();
        boolean placed = false;
        int placements = size.pick(random);
        for (int attempt = 0; attempt < placements; attempt++) {
            int x = origin.getX() + ContentPlacer.scatter(random, scatterX);
            int z = origin.getZ() + ContentPlacer.scatter(random, scatterZ);
            int y = origin.getY() + ContentPlacer.scatter(random, scatterY);
            BlockPos pos = new BlockPos(x, y, z);
            if (placer.unreadable(pos)) { continue; }
            if (seeSky && y < level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z)) { continue; }
            if (!placer.palette().surface().isEmpty() && !placer.palette().surface().contains(level.getBlockState(pos.below()).getBlock())) { continue; }
            if (placer.occupied(x, y, z)) { continue; }
            BlockState chosen = placer.palette().choose(random);
            int height = stack.pick(random);
            for (int stacked = 0; stacked < height; stacked++) {
                if (checkStay && !chosen.canSurvive(level, pos)) { break; }
                placed |= placer.placeExactly(chosen, x, y + stacked, z);
                if (stacked + 1 >= height) { break; }
                if (placer.occupied(x, y + stacked + 1, z)) { break; }
                pos = pos.above();
            }
        }
        return placed;
    }
}
