package mctmods.resourcedatapackloader.common.util.ticks;

import java.util.function.BiPredicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class RandomTicks {
    private RandomTicks() {}

    public static void run(World world, BlockPos pos, Block block, int count, @Nullable BiPredicate<BlockPos, IBlockState> skip) {
        for (int index = 0; index < count; index++) {
            IBlockState now = world.getBlockState(pos);
            if (now.getBlock() != block) { return; }
            if (skip == null || !skip.test(pos, now)) { block.randomTick(world, pos, now, world.rand); }
        }
    }
}
