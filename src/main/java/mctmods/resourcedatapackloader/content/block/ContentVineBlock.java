package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.content.def.GrowthDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

public final class ContentVineBlock extends VineBlock {
    private final GrowthDef growth;

    public ContentVineBlock(GrowthDef growth, Properties properties) {
        super(properties);
        this.growth = growth;
    }

    @Override public boolean isRandomlyTicking(@Nonnull BlockState state) { return growth.maxHeight() > 1 || growth.spread() > 0; }

    @Override public void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        if (random.nextInt(Math.max(1, growth.stages())) != 0) { return; }
        if (growth.spread() > 0 && spread(level, pos, random)) { return; }
        if (growth.maxHeight() <= 1) { return; }
        BlockPos below = pos.below();
        if (!level.isEmptyBlock(below)) { return; }
        int hanging = 1;
        while (level.getBlockState(pos.above(hanging)).is(this)) { hanging++; }
        if (hanging >= growth.maxHeight()) { return; }
        level.setBlock(below, state, Block.UPDATE_CLIENTS);
    }

    private boolean spread(ServerLevel level, BlockPos pos, RandomSource random) {
        if (crowded(level, pos)) { return false; }
        BlockPos beside = pos.relative(Direction.Plane.HORIZONTAL.getRandomDirection(random));
        if (!level.isEmptyBlock(beside)) { return false; }
        BlockState carried = defaultBlockState();
        boolean anchored = false;
        for (Direction wall : Direction.Plane.HORIZONTAL) {
            if (!isAcceptableNeighbour(level, beside.relative(wall), wall)) { continue; }
            carried = carried.setValue(getPropertyForFace(wall), Boolean.TRUE);
            anchored = true;
        }
        if (!anchored) { return false; }
        level.setBlock(beside, carried, Block.UPDATE_CLIENTS);
        return true;
    }

    private boolean crowded(ServerLevel level, BlockPos pos) {
        int found = 0;
        for (BlockPos near : BlockPos.betweenClosed(pos.offset(-2, -1, -2), pos.offset(2, 1, 2))) {
            if (level.getBlockState(near).is(this) && ++found >= growth.spread()) { return true; }
        }
        return false;
    }
}
