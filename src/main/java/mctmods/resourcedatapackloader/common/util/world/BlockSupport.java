package mctmods.resourcedatapackloader.common.util.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class BlockSupport {
    private BlockSupport() {}

    public static boolean dropUnless(Block block, World world, BlockPos pos, IBlockState state, boolean supported) {
        if (supported) { return true; }
        block.dropBlockAsItem(world, pos, state, 0);
        world.setBlockToAir(pos);
        return false;
    }
}
