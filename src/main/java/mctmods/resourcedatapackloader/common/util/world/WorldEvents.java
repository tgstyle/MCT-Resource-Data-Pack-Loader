package mctmods.resourcedatapackloader.common.util.world;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class WorldEvents {
    private static final int BREAK_EFFECT = 2001;

    private WorldEvents() {}

    public static void breakEffect(World world, @Nullable EntityPlayer except, BlockPos pos, IBlockState state) {
        world.playEvent(except, BREAK_EFFECT, pos, Block.getStateId(state));
    }
}
