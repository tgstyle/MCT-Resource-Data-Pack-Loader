package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

public final class ContentVines implements IContentShape {
    private final AmountDef count;
    private final AmountDef drop;
    private final int scatterX;
    private final int scatterY;
    private final int scatterZ;

    public ContentVines(AmountDef count, ShapeDef shape) {
        this.count = count;
        this.drop = shape.stack();
        this.scatterX = shape.scatterX();
        this.scatterY = shape.scatterY();
        this.scatterZ = shape.scatterZ();
    }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) {
        WorldGenLevel level = placer.level();
        boolean placed = false;
        int attempts = count.pick(random);
        for (int attempt = 0; attempt < attempts; attempt++) {
            BlockPos start = origin.offset(ContentPlacer.scatter(random, scatterX), ContentPlacer.scatter(random, scatterY), ContentPlacer.scatter(random, scatterZ));
            if (placer.unreadable(start) || !level.isEmptyBlock(start)) { continue; }
            Direction wall = wallBeside(placer, start);
            if (wall == null) { continue; }
            BlockState state = placer.palette().choose(random);
            if (!(state.getBlock() instanceof VineBlock)) { continue; }
            BlockState attached = state.setValue(VineBlock.getPropertyForFace(wall), Boolean.TRUE);
            int length = drop.pick(random);
            BlockPos at = start;
            for (int step = 0; step < length; step++) {
                if (!level.isEmptyBlock(at) || !attachable(placer, at.relative(wall), wall.getOpposite())) { break; }
                if (!placer.placeExactly(attached, at.getX(), at.getY(), at.getZ())) { break; }
                placed = true;
                at = at.below();
            }
        }
        return placed;
    }

    @Nullable private static Direction wallBeside(ContentPlacer placer, BlockPos pos) {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            if (attachable(placer, pos.relative(facing), facing.getOpposite())) { return facing; }
        }
        return null;
    }

    private static boolean attachable(ContentPlacer placer, BlockPos pos, Direction face) {
        if (placer.unreadable(pos)) { return false; }
        return VineBlock.isAcceptableNeighbour(placer.level(), pos, face);
    }
}
